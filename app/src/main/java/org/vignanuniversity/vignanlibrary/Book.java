package org.vignanuniversity.vignanlibrary;

public class Book {
    private String title;
    private String publisher;
    private String edition;
    private String department;
    private String author; // added

    public Book(String title, String publisher, String edition, String department, String author) {
        this.title = title;
        this.publisher = publisher;
        this.edition = edition;
        this.department = department;
        this.author = author;
    }

    public String getTitle() { return title; }
    public String getPublisher() { return publisher; }
    public String getEdition() { return edition; }
    public String getDepartment() { return department; }
    public String getAuthor() { return author; } // added
}
