package com.my.leproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.letvcloud.sdk.gandalf.BlockCallback;
import com.letvcloud.sdk.gandalf.sdsupload;
import com.my.leproject.activity.MyVideoActivity;
import com.my.leproject.unit.StaticContent;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    Button button;
    Button button2;
    EditText mEdit;
    String tag = "my";
    TextView tv;
    ProgressBar pb;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text_reason);
        button = (Button) findViewById(R.id.button_my);
        button2 = (Button) findViewById(R.id.button_choose);
        pb = (ProgressBar)findViewById(R.id.my_progress);
        mEdit = (EditText)findViewById(R.id.edit_text);
        ip = getIp();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyVideoActivity.launch2(MainActivity.this, mEdit.getText().toString().trim());
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startupload();
            }
        });
    }


    public void startupload(){
        //选择文件
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 1);


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(this,"选择了文件",Toast.LENGTH_SHORT).show();
            Uri uri = data.getData();

            String url = GetPathFromUri4kitkat.getPath(this, uri);
            File f=new File(url);
            if(!f.exists()){
                Toast.makeText(this,"文件不存在:"+url, Toast.LENGTH_SHORT).show();
                return;
            }else{
                Toast.makeText(this,"开始上传:"+url,Toast.LENGTH_SHORT).show();

            }
            Log.v(tag, url);
            Uplodad(url);
        }
        Log.v(tag, "************");
    }

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    tv.setText(msg.getData().getString("tv"));
                    Log.v(tag, "process:" + msg.getData().getInt("pb") );
                    pb.setProgress(msg.getData().getInt("pb"));

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    protected void Uplodad(final String  file){
        //上传
        new Thread(new Runnable()
        {

            @Override
            public void run() {
                UplodadFile(file);
            }
        }).start();

    }

    private String getIp(){

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }
    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    protected void UplodadFile(String  file){
        //上传文件
        sdsupload sds = sdsupload.create(StaticContent.uuid, StaticContent.key);
        sds.tryUpload(file, ip, "0", new BlockCallback() {
            @Override
            public void handleInit(String token) {

            }

            @Override
            public void handleProgress(double progress, double rate) {

                Bundle bundle = new Bundle();
                Log.v(tag, "Math:"+(int)Math.floor(progress));
                bundle.putInt("pb", (int)Math.floor(progress));

                Message msg = new Message();
                msg.what=1;
                msg.setData(bundle);//mes利用Bundle传递数据
                mHandler.sendMessage(msg);
            }
            @Override
            public void handleComplete(int code, String message) {
                System.out.print(String.format("handleComplete: %d, %s", code, message));
                Log.v(tag, "handleComplete:"+code+"<>"+message);
                Bundle bundle = new Bundle();
                bundle.putString("tv", message);
                Message msg = new Message();
                msg.what=1;
                msg.setData(bundle);//mes利用Bundle传递数据
                mHandler.sendMessage(msg);
            }
        });
    }





    public static class GetPathFromUri4kitkat {

        /**
         * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
         */
        @SuppressLint("NewApi")
        public static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] { split[1] };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context
         *            The context.
         * @param uri
         *            The Uri to query.
         * @param selection
         *            (Optional) Filter used in the query.
         * @param selectionArgs
         *            (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = { column };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }
}
