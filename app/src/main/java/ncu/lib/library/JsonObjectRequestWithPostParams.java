package ncu.lib.library;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;


/**
 * Created by chenli-han on 2014/10/9.
 */
public class JsonObjectRequestWithPostParams extends Request<JSONObject>{

    /** Charset for request */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** content-type request */
    private static final String PROTOCOL_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=";

    private final Listener<JSONObject> mListener;
    private final Map mRequestMap;


    public JsonObjectRequestWithPostParams(int method, String url, Map requestBody, Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mRequestMap = requestBody;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonString = new String(networkResponse.data,
                    HttpHeaderParser.parseCharset(networkResponse.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException uee) {
            return Response.error(new ParseError(uee));
        } catch (JSONException je) {
            Log.d("test", "Data Error: " + networkResponse.data);
            Log.d("test", "Header Error: " + networkResponse.headers);
            Log.d("test", "Status: " + networkResponse.statusCode);
            Log.d("test", "String: " + networkResponse.toString());
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject jsonObject) {
        mListener.onResponse(jsonObject);
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE + getParamsEncoding();
    }

    @Override
    public Map<String, String> getParams() {
        return mRequestMap;
    }

    @Override
    public byte[] getBody() throws AuthFailureError{
        Map<String, String> params = getParams();

        if(params != null && params.size() > 0) {
            StringBuilder encodedParams = new StringBuilder();
            String paramsEncoding = getParamsEncoding();

            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                    encodedParams.append("=");
                    encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                    encodedParams.append("&");
                }
                return encodedParams.toString().getBytes(paramsEncoding);
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
            }
        }

        return null;
    }

    @Override
    public byte[] getPostBody() throws AuthFailureError {
        return getBody();
    }

    @Override
    protected String getParamsEncoding() {
        return PROTOCOL_CHARSET;
    }
}
