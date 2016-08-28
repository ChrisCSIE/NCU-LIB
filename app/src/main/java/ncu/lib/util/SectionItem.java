package ncu.lib.util;

/**
 * Created by chenli-han on 2014/8/14.
 */
public class SectionItem implements Item {

    private String title;

    public SectionItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
