package weily.com.schedule.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import java.io.File;

public class DownFinishedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri uri;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/西瓜课表.apk");
        if (Build.VERSION.SDK_INT >= 24) {
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//临时授权
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "weily.com.schedule.fileprovider", file);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
