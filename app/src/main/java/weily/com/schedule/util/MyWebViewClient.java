package weily.com.schedule.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;


/**
 * Created by peng on 2017/9/30.
 * this is a son class
 */

public class MyWebViewClient extends WebViewClient {
    private Context mcontext;

    public MyWebViewClient(Context context) {
        this.mcontext = context;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = cookieManager.getCookie(url);
        // Log.i("————服务器设置的cookie", cookieStr+"");
        try {
            out = mcontext.openFileOutput("cookies.txt", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(cookieStr+"");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onPageFinished(view, url);
    }
}
