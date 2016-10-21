package ncu.lib.activity;

import java.util.Timer;
import java.util.TimerTask;

import ncu.lib.R;
import ncu.lib.R.drawable;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class MenuActivity extends Activity {
	private static Boolean isExit = false;
	private static Boolean hasTask = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_menu);
//		Drawable title = getResources().getDrawable(R.drawable.title);
//		getActionBar().setBackgroundDrawable(title);
//		getActionBar().setTitle("?????????");
		getActionBar().hide();
		setContentView(new MainMenuView(MenuActivity.this));
	}


	/* ????????APP */
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
		// ??????Back
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// ?????
			if (isExit == false) {
				isExit = true; // ????????
				Toast.makeText(this, getResources().getText(R.string.press_back_again), Toast.LENGTH_SHORT).show();

				// ????????????
				if (!hasTask) {
					timerExit.schedule(task, 2000);
				}
			} else {
				finish(); // ????
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
