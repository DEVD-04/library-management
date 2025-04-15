package com.library;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibraryService {
    
    public void addBook(String bookId, String title, String author, String publisher, double price) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{call AddBook(?, ?, ?, ?, ?)}")) {
            
            stmt.setString(1, bookId);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, publisher);
            stmt.setDouble(5, price);
            
            stmt.execute();
        }
    }
    
    public void addMember(String memberId, String name, String email, String address, String memberType) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{call AddMember(?, ?, ?, ?, ?)}")) {
            
            stmt.setString(1, memberId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, address);
            stmt.setString(5, memberType);
            
            stmt.execute();
        }
    }
    
    public void issueBook(String memberId, String bookId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{call IssueBook(?, ?)}")) {
            
            stmt.setString(1, memberId);
            stmt.setString(2, bookId);
            
            stmt.execute();
        }
    }
    
    public void returnBook(String memberId, String bookId, int serialNo) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{call ReturnBook(?, ?, ?)}")) {
            
            stmt.setString(1, memberId);
            stmt.setString(2, bookId);
            stmt.setInt(3, serialNo);
            
            stmt.execute();
        }
    }
    
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM BOOK_1109")) {
            
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("publisher"),
                    rs.getDouble("price"),
                    rs.getInt("total_books"),
                    rs.getInt("books_issued")
                );
                books.add(book);
            }
        }
        
        return books;
    }
    
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM MEMBER_1109")) {
            
            while (rs.next()) {
                Member member = new Member(
                    rs.getString("member_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("member_type"),
                    rs.getInt("books_currently_issued")
                );
                members.add(member);
            }
        }
        
        return members;
    }
    
    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM TRANSACTION_1109")) {
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getString("member_id"),
                    rs.getString("book_id"),
                    rs.getInt("serial_no"),
                    rs.getDate("issue_date"),
                    rs.getDate("to_be_returned_by"),
                    rs.getDate("return_date"),
                    rs.getString("transaction_type")
                );
                transactions.add(transaction);
            }
        }
        
        return transactions;
    }
} 