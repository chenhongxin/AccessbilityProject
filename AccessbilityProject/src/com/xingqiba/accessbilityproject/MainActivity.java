package com.xingqiba.accessbilityproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static boolean isRunning = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(getApplicationContext());
		textView.setText("ajhsf");
		setContentView(textView);

		isRunning = AutomaticInstallationService.isRunning();
		if (!isRunning) {
			// 跳到微信
			openAccessibilityServiceSettings();
		}
	}

	/** 打开辅助服务的设置 */
	private void openAccessibilityServiceSettings() {
		try {
			Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(isRunning && !AutomaticInstallationService.success){
			Intent wxIntent = getPackageManager().getLaunchIntentForPackage(
					"com.tencent.mm");
			if (wxIntent != null) {
				try {
					startActivity(wxIntent);
				} catch (Exception e) {
					
				}
			} else {
				Toast.makeText(MainActivity.this, "您还没有安装微信",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}