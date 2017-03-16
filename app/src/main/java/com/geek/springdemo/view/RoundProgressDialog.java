package com.geek.springdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.geek.springdemo.R;


/**
 * 圆形 进度条
 * @author chenmeng
 *
 */
public class RoundProgressDialog extends Dialog {

	private static RoundProgressDialog customProgressDialog = null;

	public RoundProgressDialog(Context context) {
		super(context);
	}
	public RoundProgressDialog(Context context, AttributeSet attrs, int theme)     {
		super(context, theme);
	}

	public static RoundProgressDialog createDialog(Context context) {
		customProgressDialog = new RoundProgressDialog(context,null,
				R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.style_progressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCanceledOnTouchOutside(false);

		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}

		ImageView imageView = (ImageView) customProgressDialog
				.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

	/**
	 * 
	 * setMessage
	 * 
	 * @param strMessage
	 * 
	 */
	public void setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.loading_msg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

	}
}
