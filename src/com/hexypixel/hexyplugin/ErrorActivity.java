package com.hexypixel.hexyplugin;
import java.lang.Character.UnicodeBlock;

import com.hexypixel.hexyplugin.Q2AActivity.ButtonCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
public class ErrorActivity extends Activity {
	public static String description = "Error in buying.";
	public static String title = "Error";
	public static String buttonText = "Close";
	public static int widthPercentage;
	public static int heightPercentage;
	private static ErrorActivity self;
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
		LinearLayout l = new LinearLayout(this);
		l.setOrientation(LinearLayout.VERTICAL);
		TextView text = new TextView(this);
		text.setText(description);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((width * widthPercentage) / 100, (height * heightPercentage) / 100);
		param.leftMargin = param.rightMargin = param.bottomMargin = param.topMargin = height / 100;
		text.setLayoutParams(param);
//		text.setWidth((width * 6) / 10);
		l.addView(text);
		Button button = new Button(this);
		param = new LinearLayout.LayoutParams((width * widthPercentage) / 200, LinearLayout.LayoutParams.WRAP_CONTENT);
		param.leftMargin = param.rightMargin = param.bottomMargin = param.topMargin = height / 100;
		param.gravity = Gravity.CENTER_HORIZONTAL;
		button.setLayoutParams(param);
		button.setText(buttonText);
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				self.finish();
			}
		});
		l.addView(button);
		setContentView(l);
	}
	public static void showErrorDialog(Activity unityActivity, String title, String description, String buttonText, int widthPercentage, int heightPercentage){
		ErrorActivity.buttonText = buttonText;
		ErrorActivity.title = title;
		ErrorActivity.description = description;
		ErrorActivity.widthPercentage = widthPercentage;
		ErrorActivity.heightPercentage = heightPercentage;
		Intent intent = new Intent(unityActivity, ErrorActivity.class);
		unityActivity.startActivity(intent);
	}
	
	public static void showDialog(Activity unityActivity, String title, String description, String buttonText){
		AlertDialog.Builder builder = new AlertDialog.Builder(unityActivity);
		builder.setMessage(description);
		builder.setTitle(title);
		builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
}