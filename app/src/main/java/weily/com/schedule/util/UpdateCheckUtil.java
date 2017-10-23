package weily.com.schedule.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class UpdateCheckUtil {
    private Context mContext;
    private String describe;
    private int newVersionCode = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("检测到新版本");
                dialog.setMessage(describe);
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //下载最新版程序
                        loadNewVersionProgress();
                    }
                });
                dialog.show();
            }
        }
    };


    public UpdateCheckUtil(Context context) {
        mContext = context;
    }

    public void checkVsersion() {
        SharedPreferences pref = mContext.getSharedPreferences("version", MODE_PRIVATE);
        final int versionCode = pref.getInt("version_code", 1);
        HttpUtil.sendOkHttpRequestMethodGET("http://www.xhban.com:8080/course/version_code.schedule", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseVersion = response.body().string();
                newVersionCode = Integer.parseInt(responseVersion.charAt(0) + "");
                if (newVersionCode > versionCode) {
                    describe = responseVersion.substring(1);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void loadNewVersionProgress() {
        PermissionCheckUtil permissionCheckUtil = new PermissionCheckUtil(mContext);
        if (permissionCheckUtil.checkPhonePermission() == 0) {
            doDownLOad();
        } else {
            permissionCheckUtil.requestNeedPermission(2);//请求码2代表下载时请求的权限
        }
    }

    public void doDownLOad() {
        final String url = "http://owiijjrb0.bkt.clouddn.com/weily.com.schedule-1.apk";
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/西瓜课表.apk");
        if (file.exists()) {
            file.delete();
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "西瓜课表.apk");
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
