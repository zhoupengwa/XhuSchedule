package weily.com.schedule.util;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import weily.com.schedule.beans.Course;
import weily.com.schedule.beans.Student;

/**
 * Created by peng on 2017/9/11.
 * this is a util to parse the json data  it will accept the datasoursce and
 * give a object as to the student info  and a list included the courses
 */

public class Utility {
    private List<Course> mCourseList = new ArrayList<>();
    private Student mStudent = new Student("", "", "", "", "");

    public boolean handleCourseData(String response, int currentWeek) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject studentObject = new JSONObject(response);
                //解析学生信息
                String sno = studentObject.getString("sno");
                String sname = studentObject.getString("sname");
                String college = studentObject.getString("college");
                String prefession = studentObject.getString("profession");
                String gradeClass = studentObject.getString("class");
                mStudent = new Student(sname, sno, college, prefession, gradeClass);//存入学生对象
                //解析课程信息
                JSONArray courseArray = new JSONArray(studentObject.getString("courses"));//JSON数组，通过长度又来获取JSON对象
                for (int i = 0; i < courseArray.length(); i++) {
                    JSONObject courseObject = courseArray.getJSONObject(i);
                    String cname = courseObject.getString("name");
                    String weelIndex = courseObject.getString("week_index");
                    //解析上课节数   [3,4]->  34      [9-11]->  91011
                    JSONArray courseIndexArray = new JSONArray(courseObject.getString("course_index"));//分号，即为数组
                    String courseIndex = "";
                    for (int j = 0; j < courseIndexArray.length(); j++) {
                        courseIndex = courseIndex + courseIndexArray.getString(j);
                    }
                    String weekStart = courseObject.getString("week_start");
                    String weekEnd = courseObject.getString("week_end");
                    String teacher = courseObject.getString("teacher");
                    String location = courseObject.getString("location");
                    String zhou = courseObject.getString("zhou");
                    String flag = "0";//初始假设不满足单双周条件
                    switch (zhou) {
                        case "":
                            flag = "1";
                            break;
                        case "单周":
                            if (!((currentWeek % 2) == 0)) {
                                flag = "1";
                            }
                            break;
                        case "双周":
                            if ((currentWeek % 2) == 0) {
                                flag = "1";
                            }
                            break;
                        default:
                    }
                    Course mCourse = new Course(cname, weelIndex, courseIndex, weekStart, weekEnd, teacher, location, flag+zhou);//存入课程对象
                    mCourseList.add(mCourse);
                }
                return true;//解析完成
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;//解析失败
    }

    //回传课程列表
    public List<Course> getmCourseList() {
        return mCourseList;
    }

    //回车学生对象
    public Student getmStudent() {
        return mStudent;
    }
}
