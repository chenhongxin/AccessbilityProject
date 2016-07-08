package com.xingqiba.accessbilityproject;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class IAccessbilityService extends AccessibilityService {

	// 大多数的手机包名一样，联想部分机型的手机不一样
	private String[] packageNames = { "com.android.packageinstaller",
			"com.lenovo.security", "com.lenovo.safecenter" };

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		AccessibilityServiceInfo mAccessibilityServiceInfo = new AccessibilityServiceInfo();
		// 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
		mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		// 反馈给用户的类型，这里是语音提示
		mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
		// 过滤的包名  
        mAccessibilityServiceInfo.packageNames = packageNames;
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

	}

	@Override
	public void onInterrupt() {

	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
}
