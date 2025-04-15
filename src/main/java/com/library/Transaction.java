package com.library;

import java.util.Date;

public class Transaction {
    private int transactionId;
    private String memberId;
    private String bookId;
    private int serialNo;
    private Date issueDate;
    private Date toBeReturnedBy;
    private Date returnDate;
    private String transactionType;
    
    public Transaction(int transactionId, String memberId, String bookId, int serialNo, 
                      Date issueDate, Date toBeReturnedBy, Date returnDate, String transactionType) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.serialNo = serialNo;
        this.issueDate = issueDate;
        this.toBeReturnedBy = toBeReturnedBy;
        this.returnDate = returnDate;
        this.transactionType = transactionType;
    }
    
    public int getTransactionId() {
        return transactionId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public String getBookId() {
        return bookId;
    }
    
    public int getSerialNo() {
        return serialNo;
    }
    
    public Date getIssueDate() {
        return issueDate;
    }
    
    public Date getToBeReturnedBy() {
        return toBeReturnedBy;
    }
    
    public Date getReturnDate() {
        return returnDate;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public boolean isReturned() {
        return "Returned".equals(transactionType);
    }
    
    @Override
    public String toString() {
        return transactionId + " - Book: " + bookId + ", Member: " + memberId + ", Status: " + transactionType;
    }
} 