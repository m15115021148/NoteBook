package com.geek.springdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.geek.springdemo.http.HttpImageUtil;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.rxjava.ProgressSubscriber;
import com.geek.springdemo.rxjava.ProgressUploadListener;
import com.geek.springdemo.rxjava.RetrofitUtil;
import com.geek.springdemo.rxjava.SubscriberOnNextListener;
import com.geek.springdemo.util.DialogUtil;
import com.geek.springdemo.util.FileNames;
import com.geek.springdemo.util.ImageUtil;
import com.geek.springdemo.util.SystemFunUtil;
import com.geek.springdemo.util.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

@ContentView(R.layout.activity_user)
public class UserActivity extends BaseActivity implements View.OnClickListener,ProgressUploadListener {
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
    private File saveFile;//上传文件夹
    @ViewInject(R.id.tel)
    private TextView mTel;//电话
    @ViewInject(R.id.userName)
    private TextView mUserName;//用户名
    private ProgressSubscriber ps;

    private SubscriberOnNextListener mListener = new SubscriberOnNextListener<ResultModel>() {

        @Override
        public void onNext(ResultModel model, int requestCode) {
            if (requestCode == RequestCode.UPLOADHEADER){
                if ("1".equals(model.getResult())){
                    ImageUtil.deleteFolder(saveFile);
                    MyApplication.userModel.setPhoto(model.getMsg());
                    HttpImageUtil.loadRoundImage(mHeader,MyApplication.userModel.getPhoto());
                    ToastUtil.showBottomShort(mContext,"上传成功");
                }else{
                    ToastUtil.showBottomShort(mContext,model.getErrorMsg());
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof SocketTimeoutException) {
                ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
            } else if (e instanceof ConnectException) {
                ToastUtil.showBottomLong(mContext,RequestCode.NOLOGIN);
            } else {
                ToastUtil.showBottomLong(mContext, "onError:"+ e.getMessage());
            }
        }
    };

    /**
     * 初始化数据
     */
    protected void initData(){
        mContext = this;
        verifyStoragePermissions(this);
        mBack.setOnClickListener(this);
        mTitle.setVisibility(View.GONE);
        mUserName.setText(MyApplication.userModel.getName());
        mHeader.setOnClickListener(this);
        HttpImageUtil.loadRoundImage(mHeader,MyApplication.userModel.getPhoto());
        mTel.setText(MyApplication.userModel.getTelphone());
        imgUtil = new SystemFunUtil(mContext);
        saveFile = imgUtil.createRootDirectory("upload");
        ImageUtil.deleteFolder(saveFile);

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

                    ps = new ProgressSubscriber<>(mListener, mContext, RequestCode.UPLOADHEADER, true);
                    RetrofitUtil.getInstance(this).uploadHeader(MyApplication.userModel.getUserID(),path,
                            ps
                    );

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

                    ps = new ProgressSubscriber<>(mListener, mContext, RequestCode.UPLOADHEADER, true);
                    RetrofitUtil.getInstance(this).uploadHeader(MyApplication.userModel.getUserID(),path,
                            ps
                            );

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

    /**
     * 进度显示
     */
    @Override
    public void onUploadProgress(String progress) {
        ps.setDialogMsg(progress);
    }
}
