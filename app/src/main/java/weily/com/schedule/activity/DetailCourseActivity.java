package weily.com.schedule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import weily.com.schedule.R;

public class DetailCourseActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> courseDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);
        ListView courseDetailView = (ListView) findViewById(R.id.course_detail);
        Button buttonBack = (Button) findViewById(R.id.back_button2);
        buttonBack.setOnClickListener(this);
        courseDetailList = new ArrayList<>();
        loadCourseDetailData();
        courseDetailView.setEnabled(false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseDetailList);
        courseDetailView.setAdapter(adapter);
    }

    private void loadCourseDetailData() {
        Intent tempIntent = getIntent();
        String name = tempIntent.getStringExtra("name");//课程名
        String courseIndex = tempIntent.getStringExtra("courseIndex");
        String startCourseIndex = courseIndex.charAt(0) + "";//起始节数
        String endCourseIndex;//结束节数
        if (Integer.parseInt(startCourseIndex) < 9) {
            endCourseIndex = courseIndex.substring(1, courseIndex.length());
        } else {
            endCourseIndex = courseIndex.substring(courseIndex.length() - 2, courseIndex.length());
        }
        String weekIndex = tempIntent.getStringExtra("weekIndex");
        String weekStart = tempIntent.getStringExtra("weekStart");//结束周
        String weekEnd = tempIntent.getStringExtra("weekEnd");//结束周
        String teacher = tempIntent.getStringExtra("teacher");//老师
        String location = tempIntent.getStringExtra("location");//位置
        String zhou = tempIntent.getStringExtra("zhou");
        courseDetailList.add("课程名: " + name);
        if ((Integer.parseInt(startCourseIndex) >= 5) && (Integer.parseInt(startCourseIndex) < 9)) {
            courseDetailList.add("节数: " + "下午" + startCourseIndex + "—" + endCourseIndex + "节");
        } else if ((Integer.parseInt(startCourseIndex) < 5)) {
            courseDetailList.add("节数: " + "上午" + startCourseIndex + "—" + endCourseIndex + "节");
        } else {
            courseDetailList.add("节数: " + "晚上" + startCourseIndex + "—" + endCourseIndex + "节");
        }
        courseDetailList.add("上课地点: " + location);
        courseDetailList.add("老师: " + teacher);
        courseDetailList.add("课程持续: " + weekStart + "—" + weekEnd + "周");
        courseDetailList.add("时间: 星期" + weekIndex);
        switch (zhou.substring(1,zhou.length())){
            case "单周":
               courseDetailList.add("单双周: 单周");
                break;
            case "双周":
                courseDetailList.add("单双周: 双周");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_button2) {
            finish();
        }
    }
}
