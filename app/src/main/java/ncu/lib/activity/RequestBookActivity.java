package ncu.lib.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ncu.lib.R;
import ncu.lib.activity.GlobalStaticVariable;
import ncu.lib.library.VolleyProvider;
import ncu.lib.util.RequestBookAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import android.app.Activity;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import ncu.lib.util.EntryItem;
import ncu.lib.util.Item;
import ncu.lib.util.Requestable;
import ncu.lib.util.SectionItem;

//import org.apache.commons.lang3.StringEscapeUtils;

public class RequestBookActivity extends Activity implements AdapterView.OnItemClickListener{

    private ArrayList<Item> mRequestList;
    private String mBookname;
    private String mToken;
    private String mUrl;

    private ListView mBookList;
    private RequestBookAdapter mRequestBookAdapter;
    private ProgressBar progressBar;
    
    private Button returnMenu, returnSearch;

    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_book);
        
        progressBar = (ProgressBar) findViewById(R.id.request_loading);
        progressBar.setVisibility(View.VISIBLE);
        
        returnMenu = (Button) findViewById(R.id.returnMenu);
        returnSearch = (Button) findViewById(R.id.returnSearch);
        returnMenu.setOnClickListener(returnButton);
        returnSearch.setOnClickListener(returnButton);
        
        final String mRequest = getIntent().getStringExtra("request");
        mToken = GlobalStaticVariable.global.getToken();
//        mToken = ((NcuLibraryApplication) getApplicationContext()).getToken();
        
        mQueue = VolleyProvider.getQueue(this);
        mRequestList = new ArrayList<Item>();
        mRequestBookAdapter = new RequestBookAdapter(this, mRequestList);
        mBookList = (ListView) findViewById(R.id.book_request_listview);
        mBookList.setAdapter(mRequestBookAdapter);
        mBookList.setOnItemClickListener(this);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GlobalStaticVariable.BASEURL + "requestlist/?request=" + mRequest + "&token=" + mToken,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            mBookname = jsonObject.getString("bookname");
                            mBookname = CodeConverter(mBookname);
                            mUrl = jsonObject.optString("url");

                            if(mBookname != "") {
                                String header = getResources().getString(R.string.request);
                                mRequestList.add(new SectionItem(header));
                                mRequestList.add(new SectionItem(mBookname));
                            }

                            JSONArray jsonArray = jsonObject.optJSONArray("book");

                            for(int i = 0; i < jsonArray.length(); ++i) {
                                if(jsonArray.isNull(i)) {
                                   break;
                                }

                                JSONObject book = (JSONObject) jsonArray.get(i);

                                RequestBook temp = new RequestBook(book.optString("barcode"),
                                        book.optString("date"), book.optString("request_no"),
                                        book.optInt("requestable"), book.optString("item_no"), mUrl, mRequest);

                                mRequestList.add(temp);
                            }

                            mRequestBookAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        mQueue.add(jsonObjectRequest);
    }
    
//    String CodeConverter(String s) {
//    	return StringEscapeUtils.unescapeHtml4(s);
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.request_book, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(this, mRequestList.get(i).toString(), Toast.LENGTH_SHORT).show();
    }

    
    private class RequestBook extends EntryItem implements Requestable {
        private String barcode;
        private String status;
        private String callNumber;
        private int requestable;
        private String itemNumber;
        private String url;
        private String request;
		
        public boolean isRequestable() {
            return (requestable == 1)? true: false;
        }


        @Override
        public String getTitle() {
            String callnumber_title = getResources().getString(R.string.callnumber);
            String barcode_title = getResources().getString(R.string.barcode);
            String status_title = getResources().getString(R.string.status);
            return barcode_title + ": " + barcode + "\n" + callnumber_title + ": " + callNumber + "\n" + status_title + ": " + status;
        }

        public RequestBook(String barcode, String status, String call_number, int requestable, String item_no, String url, String request) {
            super(null);
            this.barcode = barcode;
            this.status = status;
            this.requestable = requestable;
            this.callNumber = call_number;
            this.itemNumber = item_no;
            this.url = url;
            this.request = request;
        }

        @Override
        public String getItemNumber() {
            return itemNumber;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getRequest() {
            return request;
        }
    }
    
    View.OnClickListener returnButton = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			if (v==returnMenu) {
				intent.setClass(RequestBookActivity.this, MenuActivity.class);
				startActivity(intent);
			}
				RequestBookActivity.this.finish();
		}
	};
    
    String CodeConverter(String s) {
		return Html.fromHtml(s).toString();
    }
}
