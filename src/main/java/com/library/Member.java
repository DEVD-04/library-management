package com.library;

public class Member {
    private String memberId;
    private String name;
    private String email;
    private String address;
    private String memberType;
    private int booksCurrentlyIssued;
    
    public Member(String memberId, String name, String email, String address, String memberType, int booksCurrentlyIssued) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.memberType = memberType;
        this.booksCurrentlyIssued = booksCurrentlyIssued;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getMemberType() {
        return memberType;
    }
    
    public int getBooksCurrentlyIssued() {
        return booksCurrentlyIssued;
    }
    
    @Override
    public String toString() {
        return memberId + " - " + name + " (" + memberType + ")";
    }
} 