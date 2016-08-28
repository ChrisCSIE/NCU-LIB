package ncu.lib.util;

/**
 * Created by chenli-han on 2014/8/28.
 */
public interface Requestable {
    public boolean isRequestable();
    public String getItemNumber();
    public String getUrl();
    public String getRequest();
}
