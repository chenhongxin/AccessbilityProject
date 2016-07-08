package com.xingqiba.accessbilityproject;

import java.util.Iterator;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

public class AutomaticInstallationService extends AccessibilityService {

	private static AutomaticInstallationService service;

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		service = this;
		Log.i("json", "onServiceConnected");
		MainActivity.isRunning = true;
	}

	public static boolean success;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		try {
			if (success) {
				return;
			}
			String pkn = String.valueOf(event.getPackageName());
			if (!"com.tencent.mm".equals(pkn)) {
				return;
			}
			String className = event.getClassName() + "";
			Log.i("json", "" + className);
			AccessibilityNodeInfo note = event.getSource();
			switch (className) {
			case "com.tencent.mm.ui.LauncherUI": {
				int count = note.getChildCount();
				for (int i = 0; i < count; i++) {
					AccessibilityNodeInfo child = note.getChild(i);
					search(child);
				}
			}
			case "com.tencent.mm.plugin.profile.ui.ContactInfoUI":
			case "com.tencent.mm.plugin.search.ui.FTSMainUI": {
				int count = note.getChildCount();
				for (int i = 0; i < count; i++) {
					AccessibilityNodeInfo child = note.getChild(i);
					inputText(child, event);
				}
			}
				break;
			case "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI": {
				int count = note.getChildCount();
				for (int i = 0; i < count; i++) {
					AccessibilityNodeInfo child = note.getChild(i);
					inputText(child, event);
				}
			}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void search(AccessibilityNodeInfo child) {
		if (child.getChildCount() != 0) {
			for (int i = 0; i < child.getChildCount(); i++) {
				AccessibilityNodeInfo noteInfo = child.getChild(i);
				if (noteInfo != null) {
					if ("android.widget.TextView".equals(noteInfo
							.getClassName())) {
						if (noteInfo.getParent() != null) {
							if ("android.view.View".equals(noteInfo.getParent()
									.getClassName())) {
								AccessibilityHelper.performClick(noteInfo);
							}
						}
					}
				}
				search(noteInfo);
			}
		}
	}

	boolean input = false;

	private void inputText(AccessibilityNodeInfo child, AccessibilityEvent event) {
		if (child.getChildCount() != 0) {
			for (int i = 0; i < child.getChildCount(); i++) {
				AccessibilityNodeInfo noteInfo = child.getChild(i);
				if ("android.widget.EditText".equals(noteInfo.getClassName())) {
					Bundle arguments = new Bundle();
					arguments
							.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
									AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
					arguments
							.putBoolean(
									AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
									true);
					noteInfo.performAction(
							AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
							arguments);
					ClipData clip = ClipData.newPlainText("label",
							"15221089157");
					ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					clipboardManager.setPrimaryClip(clip);
					noteInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
					input = true;
				}
				if (input = true) {
					findNodesByText(event, "查找手机/QQ号:15221089157");
				}
				findNodesByText(event, "添加到通讯录");
				findNodesByText(event, "发送");
				inputText(noteInfo, event);
			}
		}
	}

	@Override
	public void onInterrupt() {
		Log.i("json", "onInterrupt");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("json", "onUnbind");
		return super.onUnbind(intent);
	}

	/**
	 * 根据文字寻找节点
	 * 
	 * @param event
	 * @param text
	 *            文字
	 */
	private void findNodesByText(AccessibilityEvent event, String text) {
		List<AccessibilityNodeInfo> nodes = event.getSource()
				.findAccessibilityNodeInfosByText(text);
		if (nodes != null && !nodes.isEmpty()) {
			for (AccessibilityNodeInfo info : nodes) {
				if (info.getParent() != null && info.getParent().isClickable()) {
					info.getParent().performAction(
							AccessibilityNodeInfo.ACTION_CLICK);
				}
				if (info.isClickable()) {
					info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				}
				if("发送".equals(text)){
					success = true;
				}
			}
		}

	}

	/**
	 * 判断当前服务是否正在运行
	 * */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean isRunning() {
		if (service == null) {
			return false;
		}
		AccessibilityManager accessibilityManager = (AccessibilityManager) service
				.getSystemService(Context.ACCESSIBILITY_SERVICE);
		AccessibilityServiceInfo info = service.getServiceInfo();
		if (info == null) {
			return false;
		}
		List<AccessibilityServiceInfo> list = accessibilityManager
				.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
		Iterator<AccessibilityServiceInfo> iterator = list.iterator();

		boolean isConnect = false;
		while (iterator.hasNext()) {
			AccessibilityServiceInfo i = iterator.next();
			if (i.getId().equals(info.getId())) {
				isConnect = true;
				break;
			}
		}
		if (!isConnect) {
			return false;
		}
		return true;
	}

}
