package ncu.lib.library;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by chenli-han on 2014/8/28.
 */
public class VolleyProvider {
    private static RequestQueue mQueue = null;

    private VolleyProvider() {};

    public static synchronized RequestQueue getQueue(Context context) {
        if(mQueue == null) {
            mQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return mQueue;
    }
}
