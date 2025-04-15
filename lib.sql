CREATE TABLE BOOK_1109 (
    book_id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    publisher VARCHAR(255),
    price DECIMAL(10,2),
    total_books INT DEFAULT 1,
    books_issued INT DEFAULT 0
);

CREATE TABLE BOOKCOPY_1109 (
    book_id VARCHAR2(10),
    serial_no INT,
    status VARCHAR2(10) DEFAULT 'Available',
    PRIMARY KEY (book_id, serial_no),
    FOREIGN KEY (book_id) REFERENCES BOOK_1109(book_id),
    CONSTRAINT chk_status CHECK (status IN ('Available', 'Issued'))
);

CREATE TABLE MEMBER_1109 (
    member_id VARCHAR2(10) PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    email VARCHAR2(255) UNIQUE NOT NULL,
    address VARCHAR2(255) NOT NULL,
    member_type VARCHAR2(10) NOT NULL,
    books_currently_issued INT DEFAULT 0,
    CONSTRAINT chk_member_type CHECK (member_type IN ('Student', 'Faculty'))
);

CREATE TABLE STUDENT_1109 (
    member_id VARCHAR(10) PRIMARY KEY,
    max_books_allowed INT DEFAULT 5,
    FOREIGN KEY (member_id) REFERENCES MEMBER_1109(member_id) ON DELETE CASCADE
);

CREATE TABLE FACULTY_1109 (
    member_id VARCHAR(10) PRIMARY KEY,
    max_books_allowed INT DEFAULT 10,
    FOREIGN KEY (member_id) REFERENCES MEMBER_1109(member_id) ON DELETE CASCADE
);

CREATE TABLE TRANSACTION_1109 (
    transaction_id INT PRIMARY KEY,  
    member_id VARCHAR2(10),
    book_id VARCHAR2(10),
    serial_no INT,
    issue_date DATE NOT NULL,
    to_be_returned_by DATE,  -- Virtual column or calculated field
    return_date DATE DEFAULT NULL,
    transaction_type VARCHAR2(10) DEFAULT 'Issued',
    FOREIGN KEY (member_id) REFERENCES MEMBER_1109(member_id),
    FOREIGN KEY (book_id, serial_no) REFERENCES BOOKCOPY_1109(book_id, serial_no),
    CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('Issued', 'Returned'))
);

CREATE SEQUENCE transaction_seq
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER trg_transaction_id
BEFORE INSERT ON TRANSACTION_1109
FOR EACH ROW
DECLARE
    -- Declare a variable to store the sequence value
    v_transaction_id INT;
BEGIN
    -- Get the next value from the sequence and assign it to the variable
    SELECT transaction_seq.NEXTVAL INTO v_transaction_id FROM dual;

    -- Assign the sequence value to the transaction_id field
    :NEW.transaction_id := v_transaction_id;

    -- Calculate the 'to_be_returned_by' date, which is 7 days after the 'issue_date'
    :NEW.to_be_returned_by := :NEW.issue_date + 7;
END;
/

CREATE OR REPLACE PROCEDURE IssueBook(
    p_member_id IN VARCHAR2,
    p_book_id IN VARCHAR2
)
IS
    v_serial_no INT;
    v_available_count INT;
    v_max_books INT;
    v_currently_issued INT;
    v_member_type VARCHAR2(10);

BEGIN
    -- Get available book count from BOOK_1109
    SELECT (total_books - books_issued) INTO v_available_count 
    FROM BOOK_1109 
    WHERE book_id = p_book_id;

    -- If no available copies, raise an error
    IF v_available_count <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'No available copies of this book.');
    ELSE
        -- Get the first available book copy (without searching for count)
        SELECT serial_no INTO v_serial_no 
        FROM (SELECT serial_no
              FROM BOOKCOPY_1109 
              WHERE book_id = p_book_id AND status = 'Available'
              ORDER BY serial_no ASC)
        WHERE ROWNUM = 1;  -- Use ROWNUM to limit the result to 1 row

        -- Get member type and books issued count
        SELECT member_type, books_currently_issued INTO v_member_type, v_currently_issued
        FROM MEMBER_1109 
        WHERE member_id = p_member_id;

        -- Get max books allowed based on member type
        IF v_member_type = 'Student' THEN
            SELECT max_books_allowed INTO v_max_books 
            FROM STUDENT_1109 
            WHERE member_id = p_member_id;
        ELSIF v_member_type = 'Faculty' THEN
            SELECT max_books_allowed INTO v_max_books 
            FROM FACULTY_1109 
            WHERE member_id = p_member_id;
        ELSE
            RAISE_APPLICATION_ERROR(-20002, 'Invalid member type!');
        END IF;

        -- Check if issue limit is exceeded
        IF v_currently_issued >= v_max_books THEN
            RAISE_APPLICATION_ERROR(-20003, 'Issue limit exceeded for this member.');
        ELSE
            -- Insert transaction record
            INSERT INTO TRANSACTION_1109 (member_id, book_id, serial_no, issue_date)
            VALUES (p_member_id, p_book_id, v_serial_no, SYSDATE);

            -- Update book copy status
            UPDATE BOOKCOPY_1109 
            SET status = 'Issued' 
            WHERE book_id = p_book_id AND serial_no = v_serial_no;

            -- Update issued books count in BOOK_1109
            UPDATE BOOK_1109 
            SET books_issued = books_issued + 1 
            WHERE book_id = p_book_id;

            -- Update books currently issued by member
            UPDATE MEMBER_1109 
            SET books_currently_issued = books_currently_issued + 1 
            WHERE member_id = p_member_id;
        END IF;
    END IF;
END IssueBook;
/

CREATE OR REPLACE PROCEDURE ReturnBook(
    p_member_id IN VARCHAR2,
    p_book_id IN VARCHAR2,
    p_serial_no IN INT
)
IS
    v_transaction_id INT;
BEGIN
    -- Find the issued book transaction
    BEGIN
        SELECT transaction_id 
        INTO v_transaction_id
        FROM TRANSACTION_1109 
        WHERE member_id = p_member_id 
          AND book_id = p_book_id 
          AND serial_no = p_serial_no 
          AND transaction_type = 'Issued'
        AND ROWNUM = 1;  -- Ensure only one row is fetched
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20001, 'Invalid return: No matching issued book found.');
    END;
    -- Update transaction status to 'Returned' and set return date
    UPDATE TRANSACTION_1109 
    SET return_date = SYSDATE, transaction_type = 'Returned'
    WHERE transaction_id = v_transaction_id
    AND transaction_type = 'Issued';

    -- Check if the update was successful
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Transaction already returned or does not exist.');
    END IF;
    -- Update book copy status
    UPDATE BOOKCOPY_1109 
    SET status = 'Available' 
    WHERE book_id = p_book_id 
      AND serial_no = p_serial_no;
    -- Update issued books count in BOOK_1109
    UPDATE BOOK_1109 
    SET books_issued = books_issued - 1 
    WHERE book_id = p_book_id;
    -- Update books currently issued by member
    UPDATE MEMBER_1109 
    SET books_currently_issued = books_currently_issued - 1 
    WHERE member_id = p_member_id;
    COMMIT;  -- Ensure changes are committed after the procedure completes
END ReturnBook;
/

CREATE OR REPLACE PROCEDURE AddBook(
    p_book_id IN VARCHAR2,
    p_title IN VARCHAR2,
    p_author IN VARCHAR2,
    p_publisher IN VARCHAR2,
    p_price IN NUMBER
)
IS
    v_exists INT;
    v_new_serial_no INT;
BEGIN
    -- Check if the book already exists
    SELECT COUNT(*) INTO v_exists FROM BOOK_1109 WHERE book_id = p_book_id;

    -- If the book doesn't exist, insert into BOOK_1109
    IF v_exists = 0 THEN
        INSERT INTO BOOK_1109 (book_id, title, author, publisher, price, total_books)
        VALUES (p_book_id, p_title, p_author, p_publisher, p_price, 1);
        v_new_serial_no := 1;  -- First copy of this book
    ELSE
        -- Find the last serial number for this book and increment it
        SELECT NVL(MAX(serial_no), 0) + 1 INTO v_new_serial_no 
        FROM BOOKCOPY_1109 
        WHERE book_id = p_book_id;

        -- Update total_books count in BOOK_1109
        UPDATE BOOK_1109 
        SET total_books = total_books + 1 
        WHERE book_id = p_book_id;
    END IF;

    -- Insert new copy into BOOKCOPY_1109
    INSERT INTO BOOKCOPY_1109 (book_id, serial_no, status)
    VALUES (p_book_id, v_new_serial_no, 'Available');

END AddBook;
/

CREATE OR REPLACE PROCEDURE AddMember(
    p_member_id IN VARCHAR2,
    p_name IN VARCHAR2,
    p_email IN VARCHAR2,
    p_address IN VARCHAR2,
    p_member_type IN VARCHAR2
)
IS
    v_exists INT;
BEGIN
    -- Check if the member already exists
    SELECT COUNT(*) INTO v_exists FROM MEMBER_1109 WHERE member_id = p_member_id;

    IF v_exists > 0 THEN
        -- Raise custom error if member ID already exists
        RAISE_APPLICATION_ERROR(-20001, 'Member ID already exists!');
    ELSE
        -- Insert into MEMBER_1109
        INSERT INTO MEMBER_1109 (member_id, name, email, address, member_type)
        VALUES (p_member_id, p_name, p_email, p_address, p_member_type);

        -- Insert into STUDENT_1109 or FACULTY_1109 based on member type
        IF p_member_type = 'Student' THEN
            INSERT INTO STUDENT_1109 (member_id)
            VALUES (p_member_id);
        ELSIF p_member_type = 'Faculty' THEN
            INSERT INTO FACULTY_1109 (member_id)
            VALUES (p_member_id);
        ELSE
            -- Raise custom error if member type is invalid
            RAISE_APPLICATION_ERROR(-20002, 'Invalid member type!');
        END IF;
    END IF;
END AddMember;
/
-- bid,title,auth,pub,price
EXEC AddBook('B001', 'Book1', 'Auth1', 'Pub1', 100);
EXEC AddBook('B002', 'Book2', 'Auth2', 'Pub2', 500);
EXEC AddBook('B003', 'Book3', 'Auth3', 'Pub3', 200);
SELECT * FROM BOOK_1109;
SELECT * FROM BOOKCOPY_1109;

-- mid,name,email,address,type
EXEC AddMember('M001', 'Member1', 'member1@example.com', 'Place1', 'Student');
EXEC AddMember('M002', 'Member2', 'member2@example.com', 'Place2', 'Faculty');
EXEC AddMember('M003', 'Member3', 'member3@example.com', 'Place3', 'Student');
SELECT * FROM MEMBER_1109;

EXEC IssueBook('M003', 'B002');
EXEC IssueBook('M002', 'B002');
EXEC IssueBook('M001', 'B002');
EXEC IssueBook('M003', 'B003');
SELECT * FROM TRANSACTION_1109;
EXEC ReturnBook('M003', 'B002', 1);
EXEC ReturnBook('M001', 'B002', 1);