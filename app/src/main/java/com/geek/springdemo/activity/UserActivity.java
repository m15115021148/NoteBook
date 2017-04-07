package com.geek.springdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebHostConfig;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpImageUtil;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.util.DialogUtil;
import com.geek.springdemo.util.FileNames;
import com.geek.springdemo.util.ImageUtil;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.SystemFunUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;

@ContentView(R.layout.activity_user)
public class UserActivity extends BaseActivity implements View.OnClickListener {
    private UserActivity mContext;
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.header)
    private ImageView mHeader;//头像
    private String[] values = {"拍照","相册"};
    private PopupWindow popDialog;//dialog
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
            };
    private SystemFunUtil imgUtil;//相册
    private HttpUtil http;
    private RoundProgressDialog progressDialog;
    private File saveFile;//上传文件夹

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HttpUtil.SUCCESS:
                    if (msg.arg1 == RequestCode.UPLOADHEADER) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();// 关闭进度条
                        }
                        ResultModel model = (ResultModel) ParserUtil.jsonToObject(msg.obj.toString(),ResultModel.class);
                        if (model.getResult().equals("1")){
                            ImageUtil.deleteFolder(saveFile);
                            MyApplication.userModel.setPhoto(model.getMsg());
                            HttpImageUtil.loadRoundImage(mHeader,MyApplication.userModel.getPhoto());
                            ToastUtil.showBottomShort(mContext,"上传成功");
                        }else{
                            ToastUtil.showBottomShort(mContext,model.getErrorMsg());
                        }
                    }
                    break;
                case HttpUtil.EMPTY:
                    break;
                case HttpUtil.FAILURE:
                    ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
                    break;
                case HttpUtil.LOADING:
                    if (msg.arg1 == RequestCode.UPLOADHEADER) {
                        progressDialog.setMessage(msg.arg2 + "%");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData(){
        verifyStoragePermissions(this);
        mBack.setOnClickListener(this);
        mTitle.setText(MyApplication.userModel.getName());
        mHeader.setOnClickListener(this);
        HttpImageUtil.loadRoundImage(mHeader,MyApplication.userModel.getPhoto());
        imgUtil = new SystemFunUtil(mContext);
        saveFile = imgUtil.createRootDirectory("upload");
        ImageUtil.deleteFolder(saveFile);
        if (http == null){
            http = new HttpUtil(handler);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mHeader){
            ImageUtil.deleteFolder(saveFile);
            popDialog = DialogUtil.customPopShowWayDialog(mContext, DialogUtil.DialogShowWay.FROM_DOWN_TO_UP, values,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView tx = (TextView) v;
                            if (values[0].equals(tx.getText().toString())){
                                imgUtil.openCamera(SystemFunUtil.SYSTEM_IMAGE, 101);
                            }
                            if (values[1].equals(tx.getText().toString())){
                                imgUtil.openCamera(SystemFunUtil.SYSTEM_IMAGE_PHONE, 102);
                            }
                            popDialog.dismiss();
                        }
                    });
        }
    }

    /**
     * 上传头像
     * @param userID
     * @param img
     */
    private void uploadHeader(String userID,String img){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            RequestParams params = http.getParams(WebUrlConfig.upLoadHeader());
            params.addBodyParameter("userID",userID);
            params.addBodyParameter("img",new File(img));
            http.uploadFile(RequestCode.UPLOADHEADER,params);
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101){//相机
                try{
                    File imgFile = imgUtil.getImgFile();
                    HttpImageUtil.loadRoundImage(mHeader,imgFile.getPath());
                    Log.e("result","path:"+imgFile.getPath());

                    FileNames names = new FileNames();
                    String path = ImageUtil.saveBitmap(saveFile.getPath(), ImageUtil.getSmallBitmap(imgFile.getPath()), names.getImageName());
                    //删除原有图片
                    File file = new File(imgFile.getPath());
                    file.delete();

                    uploadHeader(MyApplication.userModel.getUserID(),path);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(requestCode == 102){//相册
                try {
                    Uri uri = data.getData();
                    String str = imgUtil.getImageAbsolutePath(uri);
                    HttpImageUtil.loadRoundImage(mHeader,str);
                    Log.e("result","path:"+str);
                    FileNames names = new FileNames();
                    String path = ImageUtil.saveBitmap(saveFile.getPath(), ImageUtil.getSmallBitmap(str), names.getImageName());
                    uploadHeader(MyApplication.userModel.getUserID(),path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取手机拍照 相册 权限
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }
    }
}
