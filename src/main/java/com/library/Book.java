package com.library;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String publisher;
    private double price;
    private int totalBooks;
    private int booksIssued;
    
    public Book(String bookId, String title, String author, String publisher, double price, int totalBooks, int booksIssued) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.totalBooks = totalBooks;
        this.booksIssued = booksIssued;
    }
    
    public String getBookId() {
        return bookId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getTotalBooks() {
        return totalBooks;
    }
    
    public int getBooksIssued() {
        return booksIssued;
    }
    
    public int getAvailableBooks() {
        return totalBooks - booksIssued;
    }
    
    @Override
    public String toString() {
        return bookId + " - " + title + " by " + author;
    }
} 