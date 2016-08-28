package ncu.lib.activity;

import java.util.ArrayList;
import java.util.HashMap;

import ncu.lib.R;
import ncu.lib.library.JsonObjectRequestWithPostParams;
import ncu.lib.library.VolleyProvider;
import ncu.lib.util.BookItem;
import ncu.lib.util.RequestedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class RequestedActivity extends Activity {
	
	private ArrayList<BookItem> mBookItemList;
    private String mToken;
    private RequestedAdapter mRequestedAdapter;
    RequestQueue mQueue;
    
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested);
        
//        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//        if (actionBarTitleId > 0) {
//            TextView title = (TextView) findViewById(actionBarTitleId);
//            if (title != null) {
//                title.setTextColor(R.drawable.actionbar_text);
//            }
//        }
//        Drawable actionbar_background = getResources().getDrawable(R.drawable.actionbar_background);
//        getActionBar().setBackgroundDrawable(actionbar_background);

        mBookItemList = new ArrayList<BookItem>();

        mRequestedAdapter = new RequestedAdapter(this, mBookItemList);
        ListView listView = (ListView) findViewById(R.id.requested_listview);
        listView.setAdapter(mRequestedAdapter);

        mQueue = VolleyProvider.getQueue(this);
        mToken = GlobalStaticVariable.global.getToken();
//        mToken = ((NcuLibraryApplication) getApplicationContext()).getToken();

        if(mToken != null && !mToken.isEmpty()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, GlobalStaticVariable.BASEURL + "user/reserve?token=" + mToken,
                    null, mResponseListener, mErrorListener);

            mQueue.add(jsonObjectRequest);
        }

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        progressBar = (ProgressBar) findViewById(R.id.requested_loading);
        progressBar.setVisibility(View.VISIBLE);
    }
    
    Response.Listener<JSONObject> mResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                TextView tv = (TextView) findViewById(R.id.no_requested);
                tv.setVisibility(View.GONE);
                parseJsonBookArray(jsonObject);
                
                progressBar.setVisibility(View.GONE);

                if(mRequestedAdapter.getCount() == 0) {
                    tv.setVisibility(View.VISIBLE);
                }

                mRequestedAdapter.notifyDataSetChanged();

                if(mRequestedAdapter.getCount() != 0) {
                    final Button button = (Button) findViewById(R.id.cancel_requested_button);
                    button.setVisibility(View.VISIBLE);
                    button.setClickable(true);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();

//                            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.requested_loading);
                            progressBar.setVisibility(View.VISIBLE);

                            for (BookItem book: mBookItemList) {
                                if (book.isChecked()) {
                                    params.put(book.getItemid(), "on");
                                }
                            }

                            JsonObjectRequestWithPostParams jsonObjectRequest = new JsonObjectRequestWithPostParams(
                                    Request.Method.POST,
                                    GlobalStaticVariable.BASEURL + "user/cancelreserve?token=" + mToken,
                                    params,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject jsonObject) {
                                            try {
                                                parseJsonBookArray(jsonObject);
                                                mRequestedAdapter.notifyDataSetChanged();
                                                progressBar.setVisibility(View.GONE);
                                                
                                                String message;
                                                if (jsonObject.getBoolean("success")==true)
                                                	message = (String)getResources().getText(R.string.cancel_request_success);
                                                else
                                                	message = (String)getResources().getText(R.string.cancel_request_fail);
                                                
                                                Toast.makeText(getApplicationContext(),
            											message, Toast.LENGTH_SHORT).show();

                                                if(mRequestedAdapter.getCount() == 0) {
                                                    button.setVisibility(View.GONE);
                                                    TextView tv = (TextView) findViewById(R.id.no_requested);
                                                    tv.setVisibility(View.VISIBLE);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {
//                                        	Toast.makeText(getApplicationContext(),
//        											volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                            mQueue.add(jsonObjectRequest);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void parseJsonBookArray(JSONObject jsonObject) throws JSONException {
        mBookItemList.clear();

        JSONArray bookArray = jsonObject.getJSONArray("booksArray");

        for (int i = 0; i < bookArray.length(); ++i) {
            JSONObject json = bookArray.getJSONObject(i);
            BookItem book = new BookItem(json.getString("bookname"),
                                             json.getString("status"),
                                             json.getString("item"),
                                             false);
            mBookItemList.add(book);
        }
    }

    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(RequestedActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_requested, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.requested_refresh) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, GlobalStaticVariable.BASEURL + "user/reserve?token=" + mToken,
                    null, mResponseListener, mErrorListener);
            mQueue.add(jsonObjectRequest);
        } else if (id == R.id.requested_select_all) {
            for (BookItem book: mBookItemList) {
                if (!book.isChecked()) book.setChecked(true);
            }

            mRequestedAdapter.notifyDataSetChanged();
        } else if (id == R.id.requested_unselect_all) {
            for (BookItem book: mBookItemList) {
                if (book.isChecked()) book.setChecked(false);
            }
            mRequestedAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
