package com.library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class LibraryApp extends JFrame {
    private LibraryService service;
    private JTabbedPane tabbedPane;
    private JTable booksTable;
    private JTable membersTable;
    private JTable transactionsTable;
    
    private DefaultTableModel booksModel;
    private DefaultTableModel membersModel;
    private DefaultTableModel transactionsModel;
    
    public LibraryApp() {
        super("Library Management System");
        service = new LibraryService();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        initComponents();
        refreshAllData();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Books Tab
        JPanel booksPanel = new JPanel(new BorderLayout());
        booksModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Book ID", "Title", "Author", "Publisher", "Price", "Total", "Issued", "Available"}
        );
        booksTable = new JTable(booksModel);
        JScrollPane booksScrollPane = new JScrollPane(booksTable);
        booksPanel.add(booksScrollPane, BorderLayout.CENTER);
        
        JPanel bookButtonsPanel = new JPanel();
        JButton addBookButton = new JButton("Add Book");
        JButton issueBookButton = new JButton("Issue Book");
        JButton returnBookButton = new JButton("Return Book");
        bookButtonsPanel.add(addBookButton);
        bookButtonsPanel.add(issueBookButton);
        bookButtonsPanel.add(returnBookButton);
        booksPanel.add(bookButtonsPanel, BorderLayout.SOUTH);
        
        // Members Tab
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Member ID", "Name", "Email", "Address", "Type", "Books Issued"}
        );
        membersTable = new JTable(membersModel);
        JScrollPane membersScrollPane = new JScrollPane(membersTable);
        membersPanel.add(membersScrollPane, BorderLayout.CENTER);
        
        JPanel memberButtonsPanel = new JPanel();
        JButton addMemberButton = new JButton("Add Member");
        memberButtonsPanel.add(addMemberButton);
        membersPanel.add(memberButtonsPanel, BorderLayout.SOUTH);
        
        // Transactions Tab
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Member ID", "Book ID", "Serial No", "Issue Date", "Due Date", "Return Date", "Status"}
        );
        transactionsTable = new JTable(transactionsModel);
        JScrollPane transactionsScrollPane = new JScrollPane(transactionsTable);
        transactionsPanel.add(transactionsScrollPane, BorderLayout.CENTER);
        
        JPanel refreshPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Data");
        refreshPanel.add(refreshButton);
        transactionsPanel.add(refreshPanel, BorderLayout.SOUTH);
        
        // Add tabs
        tabbedPane.addTab("Books", booksPanel);
        tabbedPane.addTab("Members", membersPanel);
        tabbedPane.addTab("Transactions", transactionsPanel);
        
        // Add to frame
        add(tabbedPane);
        
        // Action Listeners
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddBookDialog();
            }
        });
        
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddMemberDialog();
            }
        });
        
        issueBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showIssueBookDialog();
            }
        });
        
        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReturnBookDialog();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllData();
            }
        });
    }
    
    private void refreshAllData() {
        try {
            // Clear existing data
            booksModel.setRowCount(0);
            membersModel.setRowCount(0);
            transactionsModel.setRowCount(0);
            
            // Load books
            List<Book> books = service.getAllBooks();
            for (Book book : books) {
                booksModel.addRow(new Object[] {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPrice(),
                    book.getTotalBooks(),
                    book.getBooksIssued(),
                    book.getAvailableBooks()
                });
            }
            
            // Load members
            List<Member> members = service.getAllMembers();
            for (Member member : members) {
                membersModel.addRow(new Object[] {
                    member.getMemberId(),
                    member.getName(),
                    member.getEmail(),
                    member.getAddress(),
                    member.getMemberType(),
                    member.getBooksCurrentlyIssued()
                });
            }
            
            // Load transactions
            List<Transaction> transactions = service.getAllTransactions();
            for (Transaction transaction : transactions) {
                transactionsModel.addRow(new Object[] {
                    transaction.getTransactionId(),
                    transaction.getMemberId(),
                    transaction.getBookId(),
                    transaction.getSerialNo(),
                    transaction.getIssueDate(),
                    transaction.getToBeReturnedBy(),
                    transaction.getReturnDate(),
                    transaction.getTransactionType()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error refreshing data: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddBookDialog() {
        JTextField bookIdField = new JTextField(10);
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        JTextField priceField = new JTextField(10);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Publisher:"));
        panel.add(publisherField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book",
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String bookId = bookIdField.getText().trim();
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String publisher = publisherField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                
                service.addBook(bookId, title, author, publisher, price);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage(),
                                           "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showAddMemberDialog() {
        JTextField memberIdField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JComboBox<String> typeComboBox = new JComboBox<>(new String[] {"Student", "Faculty"});
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Member Type:"));
        panel.add(typeComboBox);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Member",
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String memberId = memberIdField.getText().trim();
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();
                String memberType = (String) typeComboBox.getSelectedItem();
                
                service.addMember(memberId, name, email, address, memberType);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Member added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding member: " + ex.getMessage(),
                                           "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showIssueBookDialog() {
        JComboBox<String> memberComboBox = new JComboBox<>();
        JComboBox<String> bookComboBox = new JComboBox<>();
        
        try {
            List<Member> members = service.getAllMembers();
            for (Member member : members) {
                memberComboBox.addItem(member.getMemberId() + " - " + member.getName());
            }
            
            List<Book> books = service.getAllBooks();
            for (Book book : books) {
                bookComboBox.addItem(book.getBookId() + " - " + book.getTitle());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(),
                                       "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Member:"));
        panel.add(memberComboBox);
        panel.add(new JLabel("Book:"));
        panel.add(bookComboBox);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Issue Book",
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String selectedMember = (String) memberComboBox.getSelectedItem();
                String selectedBook = (String) bookComboBox.getSelectedItem();
                
                String memberId = selectedMember.substring(0, selectedMember.indexOf(" - "));
                String bookId = selectedBook.substring(0, selectedBook.indexOf(" - "));
                
                service.issueBook(memberId, bookId);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Book issued successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error issuing book: " + ex.getMessage(),
                                           "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showReturnBookDialog() {
        JTextField memberIdField = new JTextField(10);
        JTextField bookIdField = new JTextField(10);
        JTextField serialNoField = new JTextField(5);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);
        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Serial No:"));
        panel.add(serialNoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Return Book",
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String memberId = memberIdField.getText().trim();
                String bookId = bookIdField.getText().trim();
                int serialNo = Integer.parseInt(serialNoField.getText().trim());
                
                service.returnBook(memberId, bookId, serialNo);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid serial number!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error returning book: " + ex.getMessage(),
                                           "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LibraryApp().setVisible(true);
            }
        });
    }
} 