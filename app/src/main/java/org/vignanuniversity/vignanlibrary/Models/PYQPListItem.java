package org.vignanuniversity.vignanlibrary.Models;

public class PYQPListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_DEPARTMENT = 1;

    public int type;

    // for header
    public String headerTitle;

    // for department
    public PYQDepartment department;

    public static PYQPListItem createHeader(String title) {
        PYQPListItem item = new PYQPListItem();
        item.type = TYPE_HEADER;
        item.headerTitle = title;
        return item;
    }

    public static PYQPListItem createDepartment(PYQDepartment dept) {
        PYQPListItem item = new PYQPListItem();
        item.type = TYPE_DEPARTMENT;
        item.department = dept;
        return item;
    }
}