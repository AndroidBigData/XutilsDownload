package com.zhao.xutilsdownload;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    private Button download;
    private static NotificationManager mNotifiyManager;
    private static NotificationCompat.Builder mBuilder;
    private String url="http://openbox.mobilem.360.cn/index/d/sid/3150342";
    private BigDecimal b;
    private double f;
    private final String path = Environment.getExternalStorageDirectory()+ "/" + "zyloushi" +"/"+"/zyloushi_" + "appName" + ".apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
//    "sdcard/storge/sdcard0/Download"
    private void initView() {
        download= (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(url,path,getBaseContext());
            }
        });
    }
    private void download(String url, final String path, final Context context){
        mNotifiyManager= (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        mBuilder=new  NotificationCompat.Builder(context);
        RequestParams params=new RequestParams(url);
        params.setAutoRename(true);
        params.setSaveFilePath(path);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                mBuilder.setContentTitle("版本更新" )
                        .setContentText("正在下载...")
                        .setContentInfo("0%")
                        .setProgress(100,0,true)
                        .setSmallIcon(R.mipmap.ic_launcher);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                b = new BigDecimal((double) current/total);
                f=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                mBuilder.setProgress(100,(int)(f*100),false);
                mBuilder.setContentInfo((int)(f*100)+"%");
                mNotifiyManager.notify(0,mBuilder.build());
            }

            @Override
            public void onSuccess(File result) {
                mBuilder.setContentText("正在下载...")
                        .setProgress(0,0,false);
                mNotifiyManager.cancel(0);
                Toast.makeText(getBaseContext(),"下载成功",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                /**
                 * 下载完成后，安装App
                 */
                File App = new File(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(App),
                        "application/vnd.android.package-archive");
                context.startActivity(intent);
            }
        });
    }
}
