package org.vignanuniversity.vignanlibrary.Models;

public class EbooksListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_RESOURCE = 1;

    public int type;

    public String headerTitle;
    public EbookResource resource;

    public static EbooksListItem header(String title) {
        EbooksListItem item = new EbooksListItem();
        item.type = TYPE_HEADER;
        item.headerTitle = title;
        return item;
    }

    public static EbooksListItem resource(EbookResource r) {
        EbooksListItem item = new EbooksListItem();
        item.type = TYPE_RESOURCE;
        item.resource = r;
        return item;
    }
}
