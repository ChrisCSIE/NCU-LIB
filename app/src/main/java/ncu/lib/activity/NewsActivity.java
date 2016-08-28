package ncu.lib.activity;

import java.util.ArrayList;
import java.util.HashMap;

import ncu.lib.R;
import ncu.lib.library.VolleyProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

public class NewsActivity extends Activity {
	
	private ArrayList<HashMap<String, String>> mListHashMap = new ArrayList<HashMap<String, String>>();
    private ListView mListView;
    private SimpleAdapter mAdapter;
    private ArrayList<String> mContent = new ArrayList<String>();
    //private String mTarget;
    private ProgressBar progressBar;
    
    RequestQueue mQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		progressBar = (ProgressBar) findViewById(R.id.news_loading);
        progressBar.setVisibility(View.VISIBLE);
		
		mQueue = VolleyProvider.getQueue(this);

        mListView = (ListView) findViewById(R.id.list_news_title);
        mAdapter = new SimpleAdapter(this,
                mListHashMap,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "subtitle"},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> map
                        = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

                WebView webView = new WebView(view.getContext());
                webView.loadData(mContent.get(position), "text/html; charset=UTF-8", null);

                alert.setView(webView);
                alert.show();
            }
        });

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                GlobalStaticVariable.BASEURL + "news",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                    	progressBar.setVisibility(View.GONE);
                    	
                        for(int i = 0; i < jsonArray.length(); ++i) {
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);

                                HashMap<String, String> item = new HashMap<String, String>();
                                item.put("title", json.getString("title"));
                                item.put("subtitle", json.getString("time"));

                                mContent.add(i, json.getString("content"));

                                mListHashMap.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("test", volleyError.getMessage());
                    }
                }
        );

        mQueue.add(jsonArrayRequest);
	}
}
