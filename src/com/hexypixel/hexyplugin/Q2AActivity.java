package com.hexypixel.hexyplugin;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
public class Q2AActivity extends Activity {
	public static String description = "Error in buying.";
	public static String title = "Error";
	public static String buttonText1 = "Cancel";
	public static String buttonText2 = "Ok";
	public static int widthPercentage;
	public static int heightPercentage;
	public static volatile int buttonPressed = 0;
	private static Q2AActivity self;
	private static Semaphore lock = new Semaphore(0);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		setTitle(title);
		final int margin = height / 100;
		LinearLayout l = new LinearLayout(this);
		l.setOrientation(LinearLayout.VERTICAL);
		TextView text = new TextView(this);
		text.setText(description);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((width * widthPercentage) / 100, (height * heightPercentage) / 100);
		param.leftMargin = param.rightMargin = param.bottomMargin = param.topMargin = margin;
		text.setLayoutParams(param);
		l.addView(text);
		
		LinearLayout lb = new LinearLayout(this);
		lb.setOrientation(LinearLayout.HORIZONTAL);

		Button button1 = new Button(this);
		param = new LinearLayout.LayoutParams(((width * widthPercentage) / 200) - margin, LinearLayout.LayoutParams.WRAP_CONTENT);
		param.leftMargin = param.rightMargin = param.bottomMargin = param.topMargin = margin;
		param.gravity = Gravity.LEFT;
		button1.setLayoutParams(param);
		button1.setText(buttonText1);
		button1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonPressed = 1;
				lock.release();
				self.finish();
			}
		});
		lb.addView(button1);

		Button button2 = new Button(this);
		param = new LinearLayout.LayoutParams(((width * widthPercentage) / 200) - margin, LinearLayout.LayoutParams.WRAP_CONTENT);
		param.leftMargin = param.rightMargin = param.bottomMargin = param.topMargin = margin;
		param.gravity = Gravity.RIGHT;
		button2.setLayoutParams(param);
		button2.setText(buttonText2);
		button2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonPressed = 2;
				lock.release();
				self.finish();
			}
		});
		lb.addView(button2);
		l.addView(lb);
		setContentView(l);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		lock.release();
		finish();
	}
	

	private static final String tag = "Hulixerian-test-01";
	
	public static int showDialog(Activity unityActivity, String title, String description, String buttonText1, String buttonText2, int widthPercentage, int heightPercentage){
		Q2AActivity.buttonText1 = buttonText1;
		Q2AActivity.buttonText2 = buttonText2;
		Q2AActivity.title = title;
		Q2AActivity.description = description;
		Q2AActivity.widthPercentage = widthPercentage;
		Q2AActivity.heightPercentage = heightPercentage;
		Intent intent = new Intent(unityActivity, Q2AActivity.class);
		unityActivity.startActivity(intent);
		Thread.yield();
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			Log.d(tag, "Button not pressed. Exception: " + e);
		}
		Log.d(tag, "Button pressed.");
		return buttonPressed;
	}
	
	public interface ButtonCallback
	{
		public void fun(boolean ok);
	}
	private static ButtonCallback butLis;
	
	public static void showDialog(Activity unityActivity, String title, String description, String buttonText1, String buttonText2, ButtonCallback c){
		AlertDialog.Builder builder = new AlertDialog.Builder(unityActivity);
		builder.setMessage(description);
		builder.setTitle(title);
		butLis = c;
		builder.setPositiveButton(buttonText1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				butLis.fun(true);
			}
		});
		builder.setNegativeButton(buttonText2, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				butLis.fun(false);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
}