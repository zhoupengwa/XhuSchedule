package weily.com.schedule.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by peng on 2017/10/18.
 * this is util to get
 */

public class ImeiGetUtil {
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }
}
