package ncu.lib.util;

/**
 * Created by nculib on 2016/10/13.
 */

public class ListviewItem {
    private String section, content;

    public ListviewItem (String section, String content) {
        this.section = section;
        this.content = content;
    }

    public String getSection() {
        return section;
    }

    public String getContent() {
        return content;
    }
}
