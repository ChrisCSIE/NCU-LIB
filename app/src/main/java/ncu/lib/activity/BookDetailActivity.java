package ncu.lib.activity;

import java.util.ArrayList;

import ncu.lib.R;
import ncu.lib.util.BookDetailAdapter;
import ncu.lib.library.VolleyProvider;
import ncu.lib.util.EntryItem;
import ncu.lib.util.Item;
import ncu.lib.util.SectionItem;

//import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class BookDetailActivity extends Activity {
	RequestQueue mQueue;

    private String mBookname;
    private String mISBN;
    private String mImprint;
    private String mVersion;
    private String mRequest;
    private String mLink;

    private ListView mDetailListView;
    private Button button;
    private ArrayList<Item> mDetailList;
    private BookDetailAdapter mAdapter;
    
    private ArrayList<String> mDetail;
    
    private RelativeLayout loadingPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);
		
		loadingPanel = (RelativeLayout)findViewById(R.id.loadingPanel2);
        loadingPanel.setVisibility(View.VISIBLE);
		
		String bookID = getIntent().getStringExtra("bookID");
//        String bookName = getIntent().getStringExtra("bookName");

        mDetailListView = (ListView) findViewById(R.id.book_detail_listview);
        mDetailList = new ArrayList<Item>();
        mAdapter = new BookDetailAdapter(BookDetailActivity.this, mDetailList);
        mDetailListView.setAdapter(mAdapter);
        
        button = (Button) findViewById(R.id.request_button);
        button.setClickable(false);

        mQueue = VolleyProvider.getQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
            GlobalStaticVariable.BASEURL + "getbook/?url=" + bookID, null,
           new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	loadingPanel.setVisibility(View.GONE);
            	
                mDetailList.clear();
                
                try {
                    mDetail = new ArrayList<String>();

                    mBookname = jsonObject.getString("title");
                    mBookname = CodeConverter(mBookname);
                    mRequest = jsonObject.optString("request");
                    mISBN = jsonObject.optString("isbn");
                    mImprint = jsonObject.optString("imprint");
                    mVersion = jsonObject.optString("version");
                    mLink = jsonObject.optString("links");

                    JSONArray jsonArray = jsonObject.optJSONArray("detail");

                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.optJSONObject(i);
                            String tmp = TextUtils.join("/",
                                    new String[] {
                                            object.optString("callnumber"),
                                            object.optString("location"),
                                            object.optString("status"),
                                            object.optString("barcode")
                                    }
                            );
                            mDetail.add(tmp);
                        }
                    }

                    Button button = (Button) findViewById(R.id.request_button);

                    if (!mBookname.equalsIgnoreCase("")) {
                        mDetailList.add(new SectionItem(getString(R.string.bookname)));
                        mDetailList.add(new EntryItem(mBookname));
                    }

                    if (!mISBN.equalsIgnoreCase("")) {
                        mDetailList.add(new SectionItem(getString(R.string.isbn)));
                        mDetailList.add(new EntryItem(mISBN));
                    }

                    if (!mImprint.equalsIgnoreCase("")) {
                        mDetailList.add(new SectionItem(getString(R.string.imprint)));
                        mDetailList.add(new EntryItem(mImprint));
                    }

                    if (!mVersion.equalsIgnoreCase("")) {
                        mDetailList.add(new SectionItem(getString(R.string.version)));
                        mDetailList.add(new EntryItem(mVersion));
                    }

                    if (mDetail.size() != 0) {
                        mDetailList.add(new SectionItem(getString(R.string.book_detail)));
                        for (String tmp : mDetail) {
                            mDetailList.add(new EntryItem(tmp));
                        }
                    }

                    if (!mLink.equalsIgnoreCase("")) {
                        button.setText(getString(R.string.link));
                        button.setVisibility(View.VISIBLE);
                        button.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                getResources().getDrawable(R.drawable.ic_action_forward), null);
                        button.setClickable(true);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLink));
                                startActivity(intent);
                            }
                        });
                    }

                    if (!mRequest.equalsIgnoreCase("")) {
                        button.setText(getString(R.string.request_button));
                        button.setClickable(true);
                        button.setVisibility(View.VISIBLE);
                        Log.d("test", "mRequest: " + mRequest);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent .setClass(BookDetailActivity.this, RequestBookActivity.class);
                                intent.putExtra("request", mRequest);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*try {
                    mBookname = jsonObject.getString("title");
                    mRequest = jsonObject.optString("request");
                    mISBN = jsonObject.optString("isbn");
                    mImprint = jsonObject.optString("imprint");
                    mVerison = jsonObject.optString("version");
                    mLink = jsonObject.optString("links");

                    if(mBookname != "") {
                        mDetailList.add(new SectionItem(getString(R.string.bookname)));
                        mDetailList.add(new EntryItem(mBookname));
                    }

                    if(mISBN != "") {
                        mDetailList.add(new SectionItem(getString(R.string.isbn)));
                        mDetailList.add(new EntryItem(mISBN));
                    }

                    if(mImprint != "") {
                        mDetailList.add(new SectionItem(getString(R.string.imprint)));
                        mDetailList.add(new EntryItem(mImprint));
                    }

                    if(mVersion != "") {
                        Log.d("test", mVerison);
                        mDetailList.add(new SectionItem(getString(R.string.version)));
                        mDetailList.add(new EntryItem(mVerison));
                    }

                    if(mLink != "") {
                        button.setText(getString(R.string.link));
                        button.setVisibility(View.VISIBLE);
                        button.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                getResources().getDrawable(R.drawable.ic_action_forward), null);
                        button.setClickable(true);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLink));

                                startActivity(intent);
                            }
                        });

                    }

                    if(mRequest != "") {
                        button.setText(getString(R.string.request_button));
                        button.setClickable(true);
                        button.setVisibility(View.VISIBLE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(BookDetailActivity.this, RequestBookActivity.class);
//                                intent.setClassName(GlobalStaticVariable.PKGNAME,
//                                        GlobalStaticVariable.PKGNAME + ".RequestBookActivity");
                                intent.putExtra("request", mRequest);
                                startActivity(intent);
                            }
                        });

                    }

                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("test", "onErrorResponse");
            }
        });

        mQueue.add(jsonObjectRequest);
	}
	String CodeConverter(String s) {
//		Log.d("escapeHtml3", StringEscapeUtils.escapeHtml3(s));
//		Log.d("escapeHtml4", StringEscapeUtils.escapeHtml4(s));
//		Log.d("unescapeHtml3", StringEscapeUtils.unescapeHtml3(s));
//		Log.d("unescapeHtml4", StringEscapeUtils.unescapeHtml4(s));
		Log.d("fromHtml", Html.fromHtml(s).toString());
		return Html.fromHtml(s).toString();
    }
}
