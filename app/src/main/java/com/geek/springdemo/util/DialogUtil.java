package com.geek.springdemo.util;


import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.geek.springdemo.R;


/**
 * com.sitemap.stm.utils
 * 
 * @author chenmeng
 * @Description 对话框工具类， 统一全局对话框
 * @date create at 2016年9月12日 上午10:04:12
 */
public class DialogUtil {

	/**
	 * 自定义系统提示框
	 * 
	 * @param context
	 *            本类
	 * @param isCancle
	 *            是否 有取消or其他 按钮
	 * @param title
	 *            提示框标题
	 * @param message
	 *            提示框内容
	 * @param btOkName
	 *            按钮名称
	 * @param btCancleName
	 *            按钮名称
	 * @param listen
	 *            事件
	 * @return
	 */
	public static void customSystemDialog(Context context, boolean isCancle,
			String title, String message, String btOkName, String btCancleName,
			DialogInterface.OnClickListener listen) {
		Builder normalDia = new Builder(context);
		normalDia.setTitle(title);
		normalDia.setMessage(message);
		normalDia.setPositiveButton(btOkName, listen);
		if (isCancle) {
			normalDia.setNegativeButton(btCancleName,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}
		normalDia.create().show();
	}

	/**
	 * 自定义输入对话框
	 * 
	 * @param context
	 *            本类
	 * @param btOkName
	 *            确定名称
	 * @param btCancleName
	 *            取消名称
	 * @param pListener
	 *            确定事件
	 * @param nListener
	 *            取消事件
	 * @return 返回输入对话框的view
	 */
	public static View customInputDialog(Context context, String btOkName,
			String btCancleName, DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener) {
		Builder buidler = new Builder(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.custom_input_dialog, null);
		view.findViewById(R.id.dialog_et_txt).setVisibility(View.VISIBLE);
		buidler.setView(view);
		buidler.setPositiveButton(btOkName, pListener);
		buidler.setNegativeButton(btCancleName, nListener);
		buidler.create().show();
		return view;
	}

	/**
	 * 自定义提示框
	 * 
	 * @param context
	 *            本类
	 * @param btOkName
	 *            确定名称
	 * @param btCancleName
	 *            取消名称
	 * @param pListener
	 *            确定 事件
	 * @param nListener
	 *            取消事件
	 * @return 返回提示框的view
	 */
	public static View customPromptDialog(Context context, String btOkName,
			String btCancleName, DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener) {
		Builder buidler = new Builder(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.custom_input_dialog, null);
		view.findViewById(R.id.dialog_tv_txt).setVisibility(View.VISIBLE);
		buidler.setView(view);
		buidler.setPositiveButton(btOkName, pListener);
		buidler.setNegativeButton(btCancleName, nListener);
		buidler.create().show();
		return view;
	}

	/**
	 * 自定义弹出框 popupwindow
	 * 
	 * @param context
	 *            本类
	 * @param styleType
	 *            弹出的方式(四种)
	 * @param values
	 *            内容 数组
	 * @param listener
	 *            点击事件
	 * @return 返回一个popupwindow
	 */
	@SuppressWarnings("deprecation")
	public static PopupWindow customPopShowWayDialog(Context context,
			DialogShowWay styleType, String[] values, OnClickListener listener) {
		final PopupWindow pop = new PopupWindow(context);
		final View view = LayoutInflater.from(context).inflate(
				R.layout.custom_pop_down_up, null);
		LinearLayout container = (LinearLayout) view
				.findViewById(R.id.pop_container);
		final LinearLayout popLayout = (LinearLayout) view
				.findViewById(R.id.pop_layout);
		for (String str : values) {
			TextView txt = new TextView(context);
			txt.setText(str);
			txt.setTextSize(16);
			txt.setTextColor(context.getResources().getColor(
					R.color.pop_txt_color));
			txt.setGravity(Gravity.CENTER);
			LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 15, 0, 15);
			popLayout.addView(txt, params);
			View bg = new View(context);
			bg.setBackgroundColor(context.getResources().getColor(
					R.color.pop_txt_bg));
			LayoutParams params1 = new LayoutParams(
					LayoutParams.MATCH_PARENT, 2);
			params1.setMargins(30, 0, 30, 0);
			popLayout.addView(bg, params1);
			txt.setOnClickListener(listener);
		}
		TextView cancle = (TextView) view.findViewById(R.id.pop_cancle);
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		pop.setContentView(view);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		pop.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xB0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		pop.setBackgroundDrawable(dw);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		if (styleType == DialogShowWay.FROM_DOWN_TO_UP) {
			// 从下往上
			container.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
			pop.setAnimationStyle(R.style.PupDownToUp);
			// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
			view.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int height = popLayout.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							pop.dismiss();
						}
					}
					return true;
				}
			});
			pop.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_VERTICAL,
					0, 0);
		} else if (styleType == DialogShowWay.FROM_UP_TO_DOWN) {
			// 从上往下
			container.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL);
			pop.setAnimationStyle(R.style.PupUpToDown);
			// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
			view.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int height = popLayout.getBottom();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y > height) {
							pop.dismiss();
						}
					}
					return true;
				}
			});
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			pop.showAsDropDown(view, 0,
					manager.getDefaultDisplay().getHeight() / 26);
		} else if(styleType == DialogShowWay.FROM_LEFT_T0_RIGHT){
			//从左至右
			container.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
			pop.setAnimationStyle(R.style.PupLeftToRight);
			// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
			view.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int height = popLayout.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							pop.dismiss();
						}
					}
					return true;
				}
			});
			pop.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_VERTICAL,
					0, 0);
		}else if(styleType == DialogShowWay.FROM_RIGHT_TO_LEFT){
			//从右至左
			container.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
			pop.setAnimationStyle(R.style.PupRightToLeft);
			// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
			view.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int height = popLayout.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							pop.dismiss();
						}
					}
					return true;
				}
			});
			pop.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_VERTICAL,
					0, 0);
		}
		return pop;
	}

	/**
	 * popupwindow出现的方式
	 * 
	 */
	public enum DialogShowWay {
		/**从下往上*/ 
		FROM_DOWN_TO_UP, 
		/**从上往下*/ 
		FROM_UP_TO_DOWN,
		/**从左至右(居底)*/ 
		FROM_LEFT_T0_RIGHT,
		/**从右至左(居底)*/ 
		FROM_RIGHT_TO_LEFT
	}

}
