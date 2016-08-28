package ncu.lib.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import ncu.lib.R;
import ncu.lib.library.VolleyProvider;
import ncu.lib.util.SearchBookAdapter;
import ncu.lib.zxing.integration.android.IntentIntegrator;
import ncu.lib.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class SearchActivity extends Activity {
	private EditText keyword;
	private Button searchButton, ISBNsearchButton;
	private ArrayAdapter<String> mListAdapter;
    private ListView mListView;
    private ArrayList<String> mBookNameList;
    private ArrayList<String> mBookIDList;
    private String mQueryString;
    private RelativeLayout loadingPanel, searchLayout;
//    private SearchBookAdapter bookAdapter;
    IntentIntegrator integrator;
    IntentResult scanResult;

    RequestQueue mQueue;
    
    private Button nextBtn, prevBtn;
    private String mNext, mPrev;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		mListView = (ListView) findViewById(R.id.bookListView);
		keyword = (EditText)findViewById(R.id.keyword);
		keyword.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				Search();
				return false;
			}
		});
		searchButton = (Button)findViewById(R.id.search_button);
		searchButton.setOnClickListener(searchEvent);
		ISBNsearchButton = (Button)findViewById(R.id.ISBNsearch_button);
		ISBNsearchButton.setOnClickListener(ISBNSearchEvent);
		

        mBookIDList = new ArrayList<String>();
        mBookNameList = new ArrayList<String>();
        mListAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, mBookNameList);
//        bookAdapter = new SearchBookAdapter(this, mBookNameList);
        
        mQueue = VolleyProvider.getQueue(SearchActivity.this);

        mListView.setAdapter(mListAdapter);
//        mListView.setAdapter(bookAdapter);
        mListView.setOnItemClickListener(mBookClickListener);

        nextBtn = (Button) findViewById(R.id.next);
        prevBtn = (Button) findViewById(R.id.prev);

        nextBtn.setOnClickListener(mButtonOnClick);
        prevBtn.setOnClickListener(mButtonOnClick);

//        nextBtn.setClickable(false);
//        prevBtn.setClickable(false);
        
        loadingPanel = (RelativeLayout)findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.GONE);
        
        searchLayout = (RelativeLayout)findViewById(R.id.searchLayout);
        searchLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(searchButton.getWindowToken(), 0);
				return false;
			}
		});
	}
	
	private void Search() {
        mQueryString = keyword.getText().toString();
		if(mQueryString.equals("")) {
			Toast.makeText(SearchActivity.this, getResources().getString(
					R.string.search_hint), Toast.LENGTH_SHORT).show();
        }
		else {
            try {
				mQueryString = URLEncoder.encode(mQueryString, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
	                Request.Method.GET, GlobalStaticVariable.BASEURL + "search/?query=" + mQueryString,
	                null, mResponseListener, mErrorListener);

	        mQueue.add(jsonObjectRequest);
	        loadingPanel.setVisibility(View.VISIBLE);
//	        prevBtn.setClickable(false);
//            nextBtn.setClickable(false);
	        prevBtn.setVisibility(View.GONE);
        	nextBtn.setVisibility(View.GONE);
        	
        	mListView.setVisibility(View.GONE);
		}
	}
	
	private OnClickListener searchEvent = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchButton.getWindowToken(), 0);
			
			Search();
		}
	};
	
	private OnClickListener ISBNSearchEvent = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchButton.getWindowToken(), 0);
			
			integrator = new IntentIntegrator(SearchActivity.this);//指定當前的Activity
			integrator.initiateScan(IntentIntegrator.ONE_D_CODE_TYPES); //啟動掃描器
		}
	};
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		scanResult = integrator.parseActivityResult(requestCode, resultCode, intent);
		if(scanResult != null) {
			keyword.setText(scanResult.getContents());
			ISBNSearch();
		}
	}
	
	private void ISBNSearch() {
        mQueryString = keyword.getText().toString();
		if(mQueryString.equals("")) {
			Toast.makeText(SearchActivity.this, getResources().getString(
					R.string.search_hint), Toast.LENGTH_SHORT).show();
        }
		else {
            try {
				mQueryString = URLEncoder.encode(mQueryString, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                    Request.Method.GET, GlobalStaticVariable.BASEURL + "search/?query=" + mQueryString
//                    + "&url=i?SEARCH=" + mQueryString + "&SORT=D",
//                    null, mResponseListener, mErrorListener);
            
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                  Request.Method.GET, GlobalStaticVariable.BASEURL + "isbnsearch/?query=" + mQueryString,
                  null, ISBN_ResponseListener, mErrorListener);
            
//            Toast.makeText(SearchActivity.this, 
//            		GlobalStaticVariable.ISBN_BASEURL + "ISBNsearch?query=" + mQueryString, 
//            		Toast.LENGTH_LONG).show();

	        mQueue.add(jsonObjectRequest);
	        loadingPanel.setVisibility(View.VISIBLE);
	        prevBtn.setVisibility(View.GONE);
        	nextBtn.setVisibility(View.GONE);
        	
        	mListView.setVisibility(View.GONE);
		}
	}
	
	Response.Listener ISBN_ResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mBookIDList.clear();
                mBookNameList.clear();

                mNext = jsonObject.getString("next");
                mPrev = jsonObject.getString("prev");

                JSONArray bookJSONArray = jsonObject.getJSONArray("books");
                for (int i = 0; i < bookJSONArray.length(); ++i) {
                    JSONObject temp = bookJSONArray.getJSONObject(i);
                    mBookNameList.add(i, temp.getString("booktitle"));
                    mBookIDList.add(i, temp.getString("url"));

                    //mListAdapter.notifyDataSetChanged();
//                    loadingPanel.setVisibility(View.GONE);
                }
                if(mListAdapter != null)
                    mListAdapter.notifyDataSetChanged();
                
                if(mBookNameList.isEmpty())
                	Toast.makeText(SearchActivity.this, "No this book.", Toast.LENGTH_SHORT).show();
                else
                	ISBN_BookDatail(mBookIDList.get(0));
                
                loadingPanel.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                prevBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
	
	private void ISBN_BookDatail(String bookID) {
    	Intent intent = new Intent();
		intent.putExtra("bookID", bookID);
		intent.setClass(SearchActivity.this, BookDetailActivity.class);
		startActivity(intent);
	}
	
	
	View.OnClickListener mButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.next:
                    if(!mNext.isEmpty()) {
                    	loadingPanel.setVisibility(View.VISIBLE);
//                    	prevBtn.setClickable(false);
//                        nextBtn.setClickable(false);
                    	prevBtn.setVisibility(View.GONE);
                    	nextBtn.setVisibility(View.GONE);
                    	mListView.setVisibility(View.GONE);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.GET, GlobalStaticVariable.BASEURL + "search/?query=" + mQueryString + "&url=" + mNext, null, mResponseListener, mErrorListener);
                        mQueue.add(jsonObjectRequest);
                        mListView.setSelection(0);
                    } else {
                        Toast.makeText(SearchActivity.this, "No more!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.prev:
                    if(!mPrev.isEmpty()) {
                    	loadingPanel.setVisibility(View.VISIBLE);
//                    	prevBtn.setClickable(false);
//                        nextBtn.setClickable(false);
                    	prevBtn.setVisibility(View.GONE);
                    	nextBtn.setVisibility(View.GONE);
                    	mListView.setVisibility(View.GONE);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.GET, GlobalStaticVariable.BASEURL + "search/?query=" + mQueryString + "&url=" + mPrev, null, mResponseListener, mErrorListener);
                        mQueue.add(jsonObjectRequest);
                        mListView.setSelection(0);
                    } else {
                        Toast.makeText(SearchActivity.this, "No more!", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

	AdapterView.OnItemClickListener mBookClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//			Toast.makeText(SearchActivity.this, mBookNameList.get(i).toString(), Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
//			Toast.makeText(SearchActivity.this, mBookIDList.get(i), Toast.LENGTH_SHORT).show();
			intent.putExtra("bookID", mBookIDList.get(i));
			intent.putExtra("bookName", mBookNameList.get(i));
			intent.setClass(SearchActivity.this, BookDetailActivity.class);
//			intent.setClassName("tw.edu.ncu.nculibrary", "tw.edu.ncu.nculibrary.BookDetailListActivity");
//			startActivityForResult(intent, 1);
			startActivity(intent);
        }
    };

    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(SearchActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }
    };

    Response.Listener mResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mBookIDList.clear();
                mBookNameList.clear();

                mNext = jsonObject.getString("next");
                mPrev = jsonObject.getString("prev");

                JSONArray bookJSONArray = jsonObject.getJSONArray("books");
                for (int i = 0; i < bookJSONArray.length(); ++i) {
                    JSONObject temp = bookJSONArray.getJSONObject(i);
                    mBookNameList.add(i, temp.getString("booktitle"));
                    mBookIDList.add(i, temp.getString("url"));

                    mListAdapter.notifyDataSetChanged();
//                    bookAdapter.notifyDataSetChanged();
                    loadingPanel.setVisibility(View.GONE);
                }
                if(mListAdapter != null)
                    mListAdapter.notifyDataSetChanged();
                
                if(mBookNameList.isEmpty())
                	Toast.makeText(SearchActivity.this, "No this book.", Toast.LENGTH_SHORT).show();
                
//                if(bookAdapter != null)
//                    bookAdapter.notifyDataSetChanged();

                loadingPanel.setVisibility(View.GONE);
                mListView.setSelection(0);

//                Button prevBtn = (Button) findViewById(R.id.prev);
//                Button nextBtn = (Button) findViewById(R.id.next);

//                prevBtn.setClickable(true);
//                nextBtn.setClickable(true);
                prevBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mQueue != null) {
            mQueue.cancelAll(this);
        }
    }
    
    /* 旋轉螢幕不刷新
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 什麼都不用寫
        	if(mListAdapter != null)
                mListAdapter.notifyDataSetChanged();
        }
        else {
            // 什麼都不用寫
        	
        }
    } */
}
