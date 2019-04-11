package com.tc.bubblelayout;

import android.app.Application;

import com.tc.bubblelayout.fresco.FrescoUtil;

public class MyApplication extends Application {
    @Override
	public void onCreate() {
		super.onCreate();
		FrescoUtil.init(this);
	}
}