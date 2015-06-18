package com.stay4it.im.widget;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.stay4it.im.entities.Tab;
import com.stay4it.im.utilities.TextUtil;
import com.stay4it.im.widget.TabIndicator.OnTabClickListener;

/** 
 * @author Stay  
 * @version create time：Apr 11, 2015 8:44:21 PM 
 */
public class TabIndicator extends LinearLayout implements OnClickListener {
	private int mTabSize;
	private int mTabIndex = -1;
	private OnTabClickListener listener;
	private LayoutParams mTabParams;
	private final static int ID_PREFIX = 100000;

	public TabIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeView();
	}

	

	public TabIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}

	public TabIndicator(Context context) {
		super(context);
		initializeView();
	}
	
	public void setOnTabClickListener(OnTabClickListener listener){
		this.listener = listener;
	}
	
	public void initializeData(ArrayList<Tab> tabs){
		if (!TextUtil.isValidate(tabs)) {
			throw new IllegalArgumentException("the tabs should not be 0");
		}
		mTabSize = tabs.size();
		TabView tab = null;
		for (int i = 0; i < mTabSize; i++) {
			tab = new TabView(getContext());
			tab.setId(ID_PREFIX+i);
			tab.setOnClickListener(this);
			tab.initializeData(tabs.get(i));
			addView(tab,mTabParams);
		}
	}
	
	public void onDataChanged(int index, int number){
		((TabView)(findViewById(ID_PREFIX+index))).notifyDataChanged(number);
	}
	
	private void initializeView() {
		setOrientation(LinearLayout.HORIZONTAL);
		mTabParams = new LayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
		mTabParams.weight = 1.0f;
	}



	@Override
	public void onClick(View v) {
		int index = v.getId() - ID_PREFIX;
		if (listener != null && mTabIndex != index) {
			listener.onTabClick(v.getId() - ID_PREFIX);
			v.setSelected(true);
			if (mTabIndex != -1) {
				View old = findViewById(ID_PREFIX+mTabIndex);
				old.setSelected(false);
			}
			mTabIndex = index;
		}
	}
	
	public interface OnTabClickListener{
		void onTabClick(int index);
	}

	public void setCurrentTab(int i) {
		if (i == mTabIndex) {
			return;
		}
		View view = findViewById(ID_PREFIX+i);
		onClick(view);
	}
	
}
