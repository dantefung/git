package com.stay4it.im;

import com.iteacher.android.R;
import com.stay4it.im.net.RequestManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/** 
 * @author Stay  
 * @version create time：Apr 12, 2015 11:08:50 AM 
 */
public class BaseFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		RequestManager.getInstance().cancel(toString());
	}
	
//	@Override
//	public void startActivity(Intent intent) {
//		super.startActivity(intent);
//		getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//	}
//	
//	@Override
//	public void startActivityForResult(Intent intent, int requestCode) {
//		super.startActivityForResult(intent, requestCode);
//		getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//	}
}
