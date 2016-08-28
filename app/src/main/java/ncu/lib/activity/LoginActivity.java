package ncu.lib.activity;

import java.util.Timer;
import java.util.TimerTask;

import ncu.lib.R;
import ncu.lib.library.VolleyProvider;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class LoginActivity extends Activity {
	private boolean debugMode = false;
	private Button login, clean;
	private EditText account, pwd;
	private ImageView welcome;
	private CheckBox autoLogin;
	private Boolean isExit = false;
	private Boolean hasTask = false;
	private FrameLayout welcomeLayout;
	private LinearLayout loginLayout, componentLayout;
	private SharedPreferences mSettings;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		getActionBar().hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		/* initial layout component*/
		findComponent();
		
		/*----------------Resize Layout--------------*/
		
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);   
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        //Toast.makeText(this, String.valueOf(screenHeight)+":"+String.valueOf(screenWidth), Toast.LENGTH_SHORT).show();
        
		LayoutParams welcome_parms = welcome.getLayoutParams();
		LayoutParams component_parms = componentLayout.getLayoutParams();
		
        if (screenHeight < 1201) {
        	loginLayout.setBackgroundResource(R.drawable.white_background);
//        	loginLayout.setBackgroundColor(Color.TRANSPARENT);
    		welcome_parms.height = screenHeight/2 - screenHeight/10;
    		component_parms.height = screenHeight/2 - screenHeight/20;
        }
        else {
        	welcome_parms.height = screenHeight/3;
    		component_parms.height = screenHeight/3;
        }
		
		/*---------------------------------------------------------*/
		
		/* if touch screen, hide the soft keyboard */
		loginLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(account.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(pwd.getWindowToken(), 0);
				return false;
			}
		});
		
		/*initial shared preferences*/
		mSettings = getSharedPreferences("Preferences", 0);

		// getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
		
		/* if previous login checked "save account and password"*/
		if (isAutoLogin()) {
			account.setText(mSettings.getString("user", null)); // if saved, show account. else show ""
			pwd.setText(mSettings.getString("passwd", null)); // if saved, show passwd. else show ""
			autoLogin.setChecked(true); // if saved, auto check "save the account and password" checkBox
		}

		login.setOnClickListener(loginEvent);

		clean.setOnClickListener(cleanEvent);
	}

	private void findComponent() {
		welcome = (ImageView) findViewById(R.id.welcome);
		account = (EditText) findViewById(R.id.account);
		pwd = (EditText) findViewById(R.id.pwd);
		login = (Button) findViewById(R.id.login);
		clean = (Button) findViewById(R.id.clean);
		autoLogin = (CheckBox) findViewById(R.id.autoLogin);
		
		loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
		welcomeLayout = (FrameLayout) findViewById(R.id.welcomeLayout);
		componentLayout = (LinearLayout) findViewById(R.id.componentLayout);
	}

	private OnClickListener loginEvent = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RequestQueue queue = VolleyProvider
					.getQueue(getApplicationContext());
			SharedPreferences.Editor editor = mSettings.edit();

			String user = account.getText().toString();
			String passwd = pwd.getText().toString();

			if (autoLogin.isChecked()) {
				editor.remove("user");
				editor.remove("passwd");
				editor.commit();
				editor.putString("user", user);
				editor.putString("passwd", passwd);
				/* save sharedPreference of autoLoginCheckbox */
				editor.putBoolean("auto_login", true);
				editor.commit();
			} else {
				editor.remove("user");
				editor.remove("passwd");
				editor.remove("auto_login");
				editor.commit();
			}

			// final NcuLibraryApplication global =
			// (NcuLibraryApplication) getApplicationContext();
			// final NcuLibraryApplication global = new
			// NcuLibraryApplication();
			
			if (debugMode && user.isEmpty() && passwd.isEmpty()){
				user = "104522100";
				passwd = "104522100";
			}
			
			pd = ProgressDialog.show(LoginActivity.this, "", getResources()
					.getString(R.string.logining));

			/* "access https://www2.lib.ncu.edu.tw/~nfsnfs/mobile-new/api/" */
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
					Request.Method.GET, GlobalStaticVariable.BASEURL
							+ "login/?user=" + user + "&passwd=" + passwd,
					null, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject jsonObject) {
							try {
								/* if login successful, take response tokens */
								if (jsonObject.getBoolean("success") == true) {
									GlobalStaticVariable.global
											.setToken(jsonObject
													.getString("token"));
									GlobalStaticVariable.global.setLogin(true);
								/* if fail to login, show the reason */
								} else {
									String message = jsonObject
											.getString("message");
									Toast.makeText(getApplicationContext(),
											message, Toast.LENGTH_SHORT).show();
									pd.dismiss();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								Log.d("JSON", "JSON ERROR");
							}

						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.network_issue),
									Toast.LENGTH_SHORT).show();
							Log.e("BASEURL_ERROR", volleyError.toString());
							pd.dismiss();
							// Toast.makeText(
							// getApplicationContext(),
							// volleyError.getMessage(),
							// Toast.LENGTH_SHORT).show();
						}
					});
			/* i have no idea what can it do. by yuming. */
			queue.add(jsonObjectRequest);
			
			/* delay 1 second for login progress */
			new Thread() {
				public void run() {
					try {
						while (!GlobalStaticVariable.global.isLogin()) {
							sleep(1000);
						}
						// finish LoginActivity, start MenuActivity
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,
								MenuActivity.class);
						startActivity(intent);
						LoginActivity.this.finish();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						pd.dismiss();
					}
				}
			}.start();
		}
	};

	/* clean the editText fields */
	private OnClickListener cleanEvent = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("CLEAN", "Clean Button");
			account.setText("");
			pwd.setText("");
		}
	};
	
	/* is "save account and password?" checked? */
	private boolean isAutoLogin() {
		return mSettings.getBoolean("auto_login", false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Toast.makeText(getApplicationContext(),
				"setting", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* finish the application when press twice "back" *//* 按兩次退出才結束APP */
	Timer timerExit = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isExit = false;
			hasTask = true;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 判斷是否按下Back
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 是否要退出
			if (isExit == false) {
				isExit = true; // 記錄下一次要退出
				Toast.makeText(this, getResources().getText(R.string.press_back_again), Toast.LENGTH_SHORT)
						.show();

				// 如果超過兩秒則恢復預設值
				if (!hasTask) {
					timerExit.schedule(task, 2000);
				}
			} else {
				finish(); // 離開程式
				System.exit(0);
			}
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
