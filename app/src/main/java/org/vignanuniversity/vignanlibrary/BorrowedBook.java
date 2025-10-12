package org.vignanuniversity.vignanlibrary;

public class BorrowedBook {
    public String title, department, author, publisher, accno, dateOfIssue, dateOfReturn;

    public BorrowedBook(String title, String department, String author, String publisher, String accno) {
        this.title = title;
        this.department = department;
        this.author = author;
        this.publisher = publisher;
        this.accno = accno;
    }
}
