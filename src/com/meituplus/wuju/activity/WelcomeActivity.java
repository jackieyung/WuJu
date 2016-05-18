package com.meituplus.wuju.activity;

import java.util.ArrayList;
import java.util.List;



import com.meituplus.wuju.R;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.util.Log;
import android.widget.LinearLayout;
import android.app.Activity;
import android.content.Intent;

public class WelcomeActivity extends Activity {

	protected static final String TAG = "WelcomeActivity";
	
	
  

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		Log.i(TAG,"welcomeActivity->onCreate");
		welcomeUI();
	}

	private void welcomeUI() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);//欢迎页显示秒数
					Message message = new Message();
					myHandler.sendMessage(message);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}).start();
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			welcomeFunction();
		}

		private void welcomeFunction() {//跳转到MainActivity
			Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
			Log.i(TAG,"welcomeActivity->start");
			startActivity(intent);
			Log.i(TAG,"welcomeActivity->stop");
			WelcomeActivity.this.finish();
		}
	};
}
