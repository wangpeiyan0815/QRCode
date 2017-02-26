package com.wpy.qrcode;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wpy.qrcode.utils.ImageUtil;

/**
 * 实现二维码
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 111;
    private static final int REQUEST_IMAGE = 112;
    private ImageView iamgeView;
    private EditText contentEd;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iamgeView = (ImageView) findViewById(R.id.iamgeView);
        contentEd = (EditText) findViewById(R.id.contentEd);
    }

    /**
     * 相机扫描
     *
     * @param view
     */
    public void ScanBtn(View view) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 打开图库
     *
     * @param view
     */
    public void pictureBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     *  无图二维码
     * @param view
     */
    public void NoLogBtn(View view){

        String textContent = contentEd.getText().toString();
        if (TextUtils.isEmpty(textContent)) {
            Toast.makeText(MainActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        contentEd.setText("");
        mBitmap = CodeUtils.createImage(textContent, 400, 400, null);
        iamgeView.setImageBitmap(mBitmap);
    }

    /**
     *  有图
     * @param view
     */
    public void LogBtn(View view){
        String textContent = contentEd.getText().toString();
        if (TextUtils.isEmpty(textContent)) {
            Toast.makeText(MainActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        contentEd.setText("");
        mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        iamgeView.setImageBitmap(mBitmap);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片

                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });

                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
