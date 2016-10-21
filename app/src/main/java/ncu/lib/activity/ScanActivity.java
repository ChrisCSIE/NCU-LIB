package ncu.lib.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ncu.lib.R;
import ncu.lib.library.VolleyProvider;

public class ScanActivity extends Activity {
    private String mQueryString;

    private RequestQueue mQueue;

    private IntentIntegrator integrator;
    private IntentResult scanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        getActionBar().setTitle("");
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mQueue = VolleyProvider.getQueue(ScanActivity.this);

        integrator = new IntentIntegrator(ScanActivity.this);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_CANCELED)
            finish();
        else {
            scanResult = integrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {
                ISBNSearch(scanResult.getContents());
            }
            else {
                Toast.makeText(ScanActivity.this, "No ISBN", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void ISBNSearch(String scanCode) {
        mQueryString = scanCode;
        if(mQueryString.equals("")) {
            Toast.makeText(ScanActivity.this, "No ISBN", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            try {
                mQueryString = URLEncoder.encode(mQueryString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, GlobalStaticVariable.BASEURL + "isbnsearch/?query=" + mQueryString,
                    null, ISBN_ResponseListener, mErrorListener);

            mQueue.add(jsonObjectRequest);
        }
    }

    Response.Listener ISBN_ResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                JSONArray bookJSONArray = jsonObject.getJSONArray("books");

                if(bookJSONArray.length() == 0) {
                    Toast.makeText(ScanActivity.this, "No this book.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    JSONObject temp = bookJSONArray.getJSONObject(0);
                    ISBN_BookDatail(temp.getString("url"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void ISBN_BookDatail(String bookID) {
        Intent intent = new Intent();
        intent.putExtra("bookID", bookID);
        intent.setClass(ScanActivity.this, BookDetailActivity.class);
        startActivity(intent);
        finish();
    }

    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ScanActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mQueue != null) {
            mQueue.cancelAll(this);
        }
    }
}
