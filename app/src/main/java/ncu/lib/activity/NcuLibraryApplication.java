package ncu.lib.activity;

import android.app.Application;

/**
 * Created by chenli-han on 2014/8/28.
 */
public class NcuLibraryApplication extends Application {

    private String token;
    private Boolean isLogin = false;

    public String getToken() {
        return token;
    }

    public void setToken(String mToken) {
        this.token = mToken;
    }

    public Boolean isLogin() {
        return isLogin;
    }

    public void setLogin(Boolean isLogin) {
        this.isLogin = isLogin;
    }

}
