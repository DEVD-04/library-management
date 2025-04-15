# Library Management System

A simple Java Swing application to manage a library database using Oracle stored procedures.

## Features

- Manage books (add, issue, return)
- Manage library members (add)
- View transaction history
- Uses stored procedures for database operations

## Prerequisites

- Java 8 or higher
- Maven
- Oracle Database with the schema already set up from `lib.sql`

## Setup

1. Configure the database connection:
   - Open `src/main/java/com/library/DBConnection.java`
   - Update the following constants with your Oracle database information:
     ```java
     private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE"; // Update with your DB URL
     private static final String USER = "username"; // Replace with your Oracle username
     private static final String PASSWORD = "password"; // Replace with your Oracle password
     ```

2. Build the application:
   ```
   mvn clean package
   ```

3. Run the application:
   ```
   java -jar target/library-management-system-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Usage

### Adding a Book
1. Go to the "Books" tab
2. Click "Add Book"
3. Fill in the required information
4. Click "OK"

### Adding a Member
1. Go to the "Members" tab
2. Click "Add Member"
3. Fill in the required information
4. Click "OK"

### Issuing a Book
1. Go to the "Books" tab
2. Click "Issue Book"
3. Select a member and a book
4. Click "OK"

### Returning a Book
1. Go to the "Books" tab
2. Click "Return Book"
3. Enter the member ID, book ID, and serial number
4. Click "OK"

## Database Schema

This application works with the schema defined in `lib.sql`, which includes:
- Book management
- Member management (students and faculty)
- Book copy tracking
- Transaction history

The application directly uses the stored procedures defined in `lib.sql` for all database operations. 