package ncu.lib.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ncu.lib.R;
import ncu.lib.util.AsyncResponse;
import ncu.lib.util.NewBooksXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;



public class NewBooksListActivity extends Activity implements ncu.lib.util.AsyncResponse {
	
	private ListView mListView;
    private SimpleAdapter mAdapter;
    private List mList;
    
    private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_books_list);
		
		progressBar = (ProgressBar) findViewById(R.id.newbooks_loading);
        progressBar.setVisibility(View.VISIBLE);
        
        DownloadRSS asyncTask = new DownloadRSS();
        asyncTask.delegate = this;
        asyncTask.execute("http://www2.lib.ncu.edu.tw/newbook/nculibrssall.xml");
		
		// Set the adapter
        mListView = (ListView) findViewById(R.id.list_newbooks);
        
        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);
	}

	/*@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}*/
	
	public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

	@Override
	public void processFinish(List list) {
		// TODO Auto-generated method stub
    	progressBar.setVisibility(View.GONE);
		mList = list;
        mAdapter = new SimpleAdapter(this,
                list,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "subtitle"},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) mList.get(position);
                Intent intent = new Intent();
                intent.putExtra("bookID", (String) map.get("link"));
                //intent.setClassName("tw.edu.ncu.nculibrary", "tw.edu.ncu.nculibrary.BookDetailActivity");
                intent.setClass(NewBooksListActivity.this, BookDetailActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        mAdapter.notifyDataSetChanged();
	}
	
	private class DownloadRSS extends AsyncTask<String, Void, String> {

        List<HashMap<String, String>> mListHashMap = new ArrayList<HashMap<String, String>>();
        AsyncResponse delegate = null;

        @Override
        protected String doInBackground(String... urls) {
            try {
                loadXmlFromNetwork(urls[0]);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return "Error";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        private String loadXmlFromNetwork(String url) throws  XmlPullParserException, IOException{
            InputStream is = null;

            NewBooksXmlParser newBooksXmlParser = new NewBooksXmlParser();
            List<NewBooksXmlParser.Entry> entries = null;

            try {
                is = downloadFromUrl(url);
                entries = newBooksXmlParser.parse(is);
            } finally {
                if(is != null) {
                    is.close();
                }
            }

            for(NewBooksXmlParser.Entry entry : entries) {
                HashMap<String, String> item = new HashMap<String, String>();

                item.put("title", entry.title);
                item.put("subtitle", entry.description);
                item.put("link", entry.link);

                mListHashMap.add(item);
            }

            return null;
        }

        private InputStream downloadFromUrl(String target) throws XmlPullParserException, IOException{
            URL url = new URL(target);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            //int response = conn.getResponseCode();
            return conn.getInputStream();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("test", "onPostExecute");
            delegate.processFinish(mListHashMap);
        }
    }
}
