package weily.com.schedule.util;
import android.os.AsyncTask;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by peng on 2017/10/11.
 * a down picture util
 */

public class DownPicUtil {
    public static void downPic(String url, DownFinishListener downFinishListener) {
        //获取存储卡的目录
        String filePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filePath + "/" + "webViewCache");
        if (!file.exists()) {
            file.mkdir();
        }
        loadPic(file.getPath(), url, downFinishListener);//目录路径，url，下载结果
    }

    private static void loadPic(final String filePath, final String url, final DownFinishListener downFinishListener) {
        new AsyncTask<Void, Void, String>() {//params:  第一个是执行AsynTask时需要传入的参数，第二个是显示进度，第三个是执行结果
            String fileName; // 下载文件名称
            InputStream in;
            OutputStream out;

            @Override
            protected String doInBackground(Void... params) {
//                String[] split = url.split("/");
//                fileName = split[split.length - 1];//文件名
                //创建目标文件
                File picFile = new File(filePath + "/" + "login.gif");//目录+/+文件名
                boolean result = false;
                if (picFile.exists()) {
                    result = picFile.delete();
                }
                if (result) {
                    try {
                        picFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    URL picUrl = new URL(url);
                    in = picUrl.openStream();
                    if (in == null) {
                        return null;
                    }
                    out = new FileOutputStream(picFile);
                    byte[] b = new byte[1024];
                    int end;
                    while ((end = in.read(b)) != -1) {
                        out.write(b, 0, end);
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return picFile.getPath();
            }

            @Override
            protected void onPostExecute(String s) {//这里的s是doInBackGround()传过来带来的
                super.onPostExecute(s);
                if (s != null) {
                    downFinishListener.getDownPath(s);
                }
            }
        }.execute();
    }

    /**
     * Created by peng on 2017/10/11.
     * this is 回调接口 after download finished
     */
    public interface DownFinishListener {//下载完成的回调接口

        void getDownPath(String s);
    }
}



