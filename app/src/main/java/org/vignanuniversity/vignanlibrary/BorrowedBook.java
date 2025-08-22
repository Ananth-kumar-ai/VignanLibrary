package org.vignanuniversity.vignanlibrary;
public class BorrowedBook {
    public String title, department, author, accno, dateOfIssue, dateOfReturn;

    public BorrowedBook(String title, String department, String author, String accno) {
        this.title = title;
        this.department = department;
        this.author = author;
        this.accno = accno;
    }
}
