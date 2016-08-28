package ncu.lib.util;

/**
 * Created by chenli-han on 2014/8/14.
 */
public class EntryItem implements Item {
    private String title;
    //private String content;

    public EntryItem(String title) {
        this.title = title;
        //this.content = content;
    }

    public String getTitle() {
        return title;
    }

    /*public String getContent() {
        return content;
    }*/

    @Override
    public boolean isSection() {
        return false;
    }

}
