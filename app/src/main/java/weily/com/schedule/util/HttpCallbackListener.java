package weily.com.schedule.util;

/**
 * Created by peng on 2017/9/15.
 * a interface to receive the data from server when had do internet request
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
