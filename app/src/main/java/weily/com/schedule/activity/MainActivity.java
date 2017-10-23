package weily.com.schedule.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import weily.com.schedule.R;
import weily.com.schedule.adapter.CourseAdapter;
import weily.com.schedule.adapter.CourseListAdapter;
import weily.com.schedule.beans.Course;
import weily.com.schedule.beans.Student;
import weily.com.schedule.util.HttpUtil;
import weily.com.schedule.util.PermissionCheckUtil;
import weily.com.schedule.util.SpaceItemDecoration;
import weily.com.schedule.util.UpdateCheckUtil;
import weily.com.schedule.util.Utility;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private Utility utility;
    private int currentWeek;
    private DrawerLayout mDrawerLayout;
    private ImageView bgImage;
    private ProgressDialog progressDialog;
    private NavigationView navView;
    private UpdateCheckUtil updateCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查是否第一次登录
        checkCourseInfo();
        bgImage = (ImageView) findViewById(R.id.bg_img);//背景图
        //设置材料主题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);//将supportActionBar换了
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_bar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);//设置滑动菜单引导图标
        }
        //显示当前是周几
        inintCurrentDayOfWeek();
        //读取系统保存的当前周数
        initCurrentWeek();
        //刷新界面，显示学生、课程信息
        initCourseIno();
        //获取背景图
        initBgImage();
        //检查版更新
        checkVersion();
    }


    private void checkVersion() {
        updateCheck = new UpdateCheckUtil(MainActivity.this);
        updateCheck.checkVsersion();
    }

    //检查是否第一次登录
    private void checkCourseInfo() {
        SharedPreferences pref1 = getSharedPreferences("course_data", MODE_PRIVATE);
        //含有课程表数据，就不用再登录
        if (!(pref1.getString("courses", "").startsWith("{"))) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //设读取系统保存周数
    private void initCurrentWeek() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        TextView currentWeekText = (TextView) findViewById(R.id.current_week_text);
        currentWeek = pref.getInt("current_week", 0);//找不到，则以0作为默认值
        currentWeekText.setText("第" + currentWeek + "周");
    }

    //刷新界面
    private void initCourseIno() {
        SharedPreferences pref = getSharedPreferences("course_data", MODE_PRIVATE);
        String data = pref.getString("courses", "");
        //String data = new DataSourceUtil().getDataSource();
        utility = new Utility();
        if (utility.handleCourseData(data, currentWeek)) {
            showStudentInfo();//处理学生对象
            showCourseInfo(); //处理课程信息
        }
    }

    //处理学生对象
    void showStudentInfo() {
        Student student = utility.getmStudent();
        View navHeaderView = navView.getHeaderView(0);
        TextView stuName = (TextView) navHeaderView.findViewById(R.id.stu_name);
        TextView stuClass = (TextView) navHeaderView.findViewById(R.id.stu_class);
        TextView stuCollege = (TextView) navHeaderView.findViewById(R.id.stu_college);
        stuName.setText("你好，" + student.getName());
        stuCollege.setText(student.getCollege());
        stuClass.setText(student.getProfession() + ":" + student.getGradeClass());
    }

    //处理课程信息
    void showCourseInfo() {
        CourseListAdapter courseListAdapter = new CourseListAdapter();
        List<Course> courseList = courseListAdapter.dealCourseList(utility.getmCourseList());  //得到适配后的课程列表
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
//      GridLayoutManager layoutManager = new GridLayoutManager(this, 7);//使用网格布局
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        CourseAdapter adapter = new CourseAdapter(courseList, recyclerView, currentWeek);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration(1);
        recyclerView.addItemDecoration(spaceItemDecoration);
        recyclerView.setAdapter(adapter);
    }

    //设置菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //菜单添加点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_week_option:
                setCurrentWeek();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            default:
        }
        return true;
    }

    //设置当前周数，对话框形式,并且将其保存到数据库
    private void setCurrentWeek() {
        List<Integer> weekList = new ArrayList<>();
        for (int i = 0; i <= 16; i++) {
            weekList.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weekList);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("选择当前周");
        dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //更改系统保存周数
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putInt("current_week", which);
                editor.apply();
                //设置当前周数
                initCurrentWeek();
                //刷新界面
                initCourseIno();
                Toast.makeText(MainActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_bg:
                //设置背景
                PermissionCheckUtil permissionCheckUtil = new PermissionCheckUtil(MainActivity.this);
                if ((permissionCheckUtil.checkPhonePermission()) == 0) {
                    openAblum();
                } else {
                    //申请运行时权限
                    permissionCheckUtil.requestNeedPermission(1);//请求码1代表设置背景申请的权限
                }
                break;
            case R.id.bing_bg:
                mDrawerLayout.closeDrawers();
                //获取必应每日一图
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("每日一图");
                    progressDialog.setMessage("正在加载...");
                }
                progressDialog.show();
                loadBingPic();
                break;
            case R.id.origin_bg:
                mDrawerLayout.closeDrawers();
                SharedPreferences.Editor editor = getSharedPreferences("bg_img", MODE_PRIVATE).edit();
                editor.putString("picture", "");
                editor.apply();
                Glide.with(MainActivity.this).load("").into(bgImage);
                break;
            case R.id.change_user:
                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.end:
                finish();
                break;
            case R.id.about:
                Intent intent3 = new Intent(MainActivity.this, AppInfoActivity.class);
                startActivity(intent3);
                break;
            default:
        }
        return true;
    }


    private void openAblum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openAblum();
                } else {
                    Toast.makeText(MainActivity.this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    updateCheck.doDownLOad();
                } else {
                    Toast.makeText(MainActivity.this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    //获取背景图
    private void initBgImage() {
        SharedPreferences pref2 = getSharedPreferences("bg_img", MODE_PRIVATE);
        String picture = pref2.getString("picture", null);
        if (picture != null) {
            Glide.with(MainActivity.this).load(picture).into(bgImage);
        }
    }

    //获取必应每日一图
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequestMethodGET(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "请检查网络!", Toast.LENGTH_SHORT).show();
                    }
                });
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor2 = getSharedPreferences("bg_img", MODE_PRIVATE).edit();
                editor2.putString("picture", bingPic);
                editor2.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(bgImage);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    //自定义背景图——>结果返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        handImageOnKitKat(data);
                    } else {
                        handImageBeforeKitKat(data);
                    }

                }
                break;
            default:
        }
    }

    //4.4以下处理图片
    private void handImageBeforeKitKat(Intent data) {
        Uri selectedImage = data.getData();//图片的相对路径
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);//查找选中的图片
        String path = getImagePath(selectedImage, null);
        File imageFile = new File(path);
        Glide.with(MainActivity.this).load(imageFile).into(bgImage);
        SharedPreferences.Editor editor2 = getSharedPreferences("bg_img", MODE_PRIVATE).edit();
        editor2.putString("picture", path);
        editor2.apply();
    }

    //4.4以上处理图片
    private void handImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(MainActivity.this, uri)) {
            //如果是document类型的uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是文件类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        File imageFile = new File(imagePath);
        Glide.with(MainActivity.this).load(imageFile).into(bgImage);
        SharedPreferences.Editor editor2 = getSharedPreferences("bg_img", MODE_PRIVATE).edit();
        editor2.putString("picture", imagePath);
        editor2.apply();
    }

    private String getImagePath(Uri uri, String selection) {
        String path = "";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //显示当前是周几
    private void inintCurrentDayOfWeek() {
        Date nowTime = new Date();
        String pattern = "%ta";
        String currentDayOfWeek = String.format(Locale.CHINA, pattern, nowTime);
        switch (currentDayOfWeek) {
            case "周一":
                TextView monText = (TextView) findViewById(R.id.mon);
                // monText.setBackgroundColor(parseColor("#96267397"));
                monText.setTextColor(Color.parseColor("#F1D33650"));
                break;
            case "周二":
                TextView tueText = (TextView) findViewById(R.id.tue);
                tueText.setTextColor(Color.parseColor("#F1D33650"));
                break;
            case "周三":
                TextView wedText = (TextView) findViewById(R.id.wed);
                wedText.setTextColor(Color.parseColor("#F1D33650"));
                break;
            case "周四":
                TextView thuText = (TextView) findViewById(R.id.thu);
                thuText.setTextColor(Color.parseColor("#F1D33650"));
                break;
            case "周五":
                TextView friText = (TextView) findViewById(R.id.fri);
                friText.setTextColor(Color.parseColor("#F1D33650"));
                break;
            case "周六":
                TextView satText = (TextView) findViewById(R.id.sat);
                satText.setTextColor(Color.parseColor("#F1D33650"));
                break;
            case "周日":
                TextView sunText = (TextView) findViewById(R.id.sun);
                sunText.setTextColor(Color.parseColor("#F1D33650"));
                break;
            default:
        }
    }
}
