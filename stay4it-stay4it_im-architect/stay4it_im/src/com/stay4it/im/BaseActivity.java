package com.stay4it.im;

import com.iteacher.android.R;
import com.stay4it.im.home.HomeActivity;
import com.stay4it.im.home.WelcomeActivity;
import com.stay4it.im.net.RequestManager;
import com.stay4it.im.utilities.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Stay
 * @version create time：Mar 15, 2015 8:24:15 PM
 */
public abstract class BaseActivity extends Activity {
	private boolean isStartActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(IMApplication.mAppState != -1){
			setContentView();
			initializeView();
			initializeData();
		}else {
			protectApp();
		}
	}

	private void protectApp() {
		Intent intent = new Intent(this,HomeActivity.class);
		intent.putExtra(Constants.KEY_PROTECT_APP, true);
		startActivity(intent);
		finish();
	}

	protected abstract void setContentView();

	protected abstract void initializeView();

	protected abstract void initializeData();

	@Override
	protected void onResume() {
		super.onResume();
		// TODO umeng
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		RequestManager.getInstance().cancel(toString());
	}

	// @Override
	// public void startActivity(Intent intent) {
	// super.startActivity(intent);
	// isStartActivity = true;
	// overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	// }
	//
	// @Override
	// public void startActivityForResult(Intent intent, int requestCode) {
	// super.startActivityForResult(intent, requestCode);
	// isStartActivity = true;
	// overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	// }
	//
	// @Override
	// public void finish() {
	// super.finish();
	// if (!isStartActivity) {
	// overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	// }
	// }
	

}
