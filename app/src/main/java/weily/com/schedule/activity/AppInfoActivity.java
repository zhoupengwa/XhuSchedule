package weily.com.schedule.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import weily.com.schedule.R;
import weily.com.schedule.util.UpdateCheckUtil;

public class AppInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);
        TextView versionName = (TextView) findViewById(R.id.version_info);
        SharedPreferences pref = getSharedPreferences("version", MODE_PRIVATE);
        String name = pref.getString("version_name", "1.0.0");
        versionName.setText("v" + name);
        final TextView updateCheck = (TextView) findViewById(R.id.check_update);
        updateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCheck.setText("检测中...");
                updateCheck.setEnabled(false);
                updateCheck.setTextColor(Color.parseColor("#BBBBBB"));
              new UpdateCheckUtil(AppInfoActivity.this).checkVsersion();
            }
        });
    }
}
