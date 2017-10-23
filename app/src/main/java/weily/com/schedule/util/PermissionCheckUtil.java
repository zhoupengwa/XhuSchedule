package weily.com.schedule.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by peng on 2017/10/21.
 * this is a util to check the permission to write and read the external storage
 */

public class PermissionCheckUtil {
    private final int NO_WRITE_READ = 1;
    private final int NO_WRITE = 2;
    private final int NO_READ = 3;
    private Context mContext;

    public PermissionCheckUtil(Context context) {
        this.mContext = context;
    }

    //检查运行时权限
    public int checkPhonePermission() {
        if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED) {
            return NO_WRITE_READ;
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return NO_WRITE;
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return NO_READ;
        }
        return 0;
    }

    public void requestNeedPermission(int code) {
        switch (checkPhonePermission()) {
            case NO_READ:
                requestPhonePermission(NO_READ, code);
            case NO_WRITE:
                requestPhonePermission(NO_WRITE, code);
            case NO_WRITE_READ:
                requestPhonePermission(NO_WRITE_READ, code);
        }
    }

    public void requestPhonePermission(int requestPermissionCode, int code) {
        switch (requestPermissionCode) {
            case NO_WRITE_READ://两者都要申请
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, code);
                break;
            case NO_WRITE://申请写
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
                break;
            case NO_READ://申请读
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, code);
                break;
            default:
        }
    }


}
