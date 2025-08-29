package org.vignanuniversity.vignanlibrary;

public class Book {
    private String title;
    private String publisher;
    private String edition;
    private String department;

    public Book(String title, String publisher, String edition, String department) {
        this.title = title;
        this.publisher = publisher;
        this.edition = edition;
        this.department = department;
    }

    public String getTitle() { return title; }
    public String getPublisher() { return publisher; }
    public String getEdition() { return edition; }
    public String getDepartment() { return department; }
}
