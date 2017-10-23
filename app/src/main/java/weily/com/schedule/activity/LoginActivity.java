package weily.com.schedule.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import weily.com.schedule.R;
import weily.com.schedule.util.HttpUtil;
import weily.com.schedule.util.MyWebViewClient;
import weily.com.schedule.util.VersionInfo;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login;
    private EditText account;
    private EditText password;
    private EditText checkCode;
    private WebView checkCodeView;
    private String username = "";
    private String pw = "";
    private String loginCheckCode = "";
    private ProgressDialog dialog = null;
    private StringBuffer cookies;
    private TextView changeCheckCode, logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button) findViewById(R.id.login_btn);
        logOut = (TextView) findViewById(R.id.log_out);
        account = (EditText) findViewById(R.id.account_text);
        password = (EditText) findViewById(R.id.password_text);
        checkCode = (EditText) findViewById(R.id.check_code);
        checkCodeView = (WebView) findViewById(R.id.checkcode_view);
        changeCheckCode = (TextView) findViewById(R.id.change_check_code);
        //获取验证码
        //checkCodeView.getSettings().setJavaScriptEnabled(true);
        checkCodeView.setWebViewClient(new MyWebViewClient(LoginActivity.this));     //获取Cookie
        checkCodeView.loadUrl("http://course.xhban.com:8000/login");
        changeCheckCode.setOnClickListener(this);//更换验证码
        login.setOnClickListener(this);  //登录
        logOut.setOnClickListener(this);
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("正在登陆...");
            dialog.setCancelable(false);
        }
        dialog.show();
    }

    private void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //登录
            case R.id.login_btn:
                username = String.valueOf(account.getText());
                pw = String.valueOf(password.getText());
                loginCheckCode = String.valueOf(checkCode.getText());
                if (username.equals("") || pw.equals("") || loginCheckCode.equals("")) {
                    Snackbar.make(v, "不能含有空项!", Snackbar.LENGTH_SHORT).show();
                } else {
                    loadCookie();
                    showDialog();
                    requestLogin();
                    saveVersionCode();

                }
                break;
            case R.id.change_check_code:
                checkCodeView.loadUrl("http://course.xhban.com:8000/login");
                break;
            case R.id.log_out:
                finish();
                break;
            default:
        }
    }

    //保存版本信息
    private void saveVersionCode() {
        SharedPreferences.Editor editor = getSharedPreferences("version", MODE_PRIVATE).edit();
        editor.putString("version_name", VersionInfo.getVersionName(LoginActivity.this));
        editor.putInt("version_code", Integer.parseInt(VersionInfo.getVersionCode(LoginActivity.this)));
        editor.apply();
    }

    private void loadCookie() {
        cookies = new StringBuffer();
        BufferedReader reader = null;
        FileInputStream in = null;
        try {
            in = openFileInput("cookies.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                cookies.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void requestLogin() {
        HttpUtil.sendOkHttpRequestMethodPOST("http://www.xhban.com:8080/course/login.schedule", "", username, pw, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
        HttpUtil.sendOkHttpRequestMethodPOST("http://course.xhban.com:8000/courses", cookies + "", username, pw, loginCheckCode, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        Toast.makeText(LoginActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                        checkCodeView.loadUrl("http://course.xhban.com:8000/login");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (responseText.trim().charAt(0) + "") {
                            case "2":
                                closeDialog();
                                Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                                checkCodeView.loadUrl("http://course.xhban.com:8000/login");
                                break;
                            case "3":
                                closeDialog();
                                Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                                checkCodeView.loadUrl("http://course.xhban.com:8000/login");
                                break;
                            case "{":
                                SharedPreferences.Editor editor = getSharedPreferences("course_data", MODE_PRIVATE).edit();
                                editor.putString("courses", responseText);
                                editor.apply();
                                // Log.i("login_activity_data", responseText+"");
                                closeDialog();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            default:
                        }
                    }
                });

            }
        });
    }

}
