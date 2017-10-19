package weily.com.schedule.adapter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weily.com.schedule.beans.Course;

/**
 * Created by peng on 2017/9/11.
 * this is a adapter to deal the courselist and it will give an complete
 * course list to adapt the week which has 5*7 cousers to show;
 */

public class CourseListAdapter {
    private List<Course> mcourseList = new ArrayList<>();
    private Course[][] courses = new Course[5][7];

    public List<Course> dealCourseList(List<Course> courseList) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                courses[i][j] = new Course("", "", "", "", "", "", "","");
            }
        }
        for (int i = 0; i < courseList.size(); i++) {
            int weekIndex = Integer.parseInt(courseList.get(i).getWeekIndex());//周几
            int relativeCourseIndex = Integer.parseInt(courseList.get(i).getCourseIndex().charAt(0) + "");//课程的起始节数  3-4节则为3
            String name = courseList.get(i).getName();//课程名字
            String location = courseList.get(i).getLocation();//位置
            String weekStart = courseList.get(i).getWeekStart();//开始周
            String weekEnd = courseList.get(i).getWeekEnd();//结束周
            String teacher = courseList.get(i).getTeacher();//老师
            String courseIndex = courseList.get(i).getCourseIndex();//节数
            String zhou=courseList.get(i).getZhou();//是否单周
            int m = relativeCourseIndex / 2;
            int n = weekIndex - 1;
            String tempName = courses[m][n].getName();
            if (!(tempName.equals(""))) {
                courses[m][n].setName(tempName + "|" + name);
                courses[m][n].setLocation(courses[m][n].getLocation() + "|" + location);
                courses[m][n].setWeekIndex(courses[m][n].getWeekIndex() +"|"+ weekIndex);
                courses[m][n].setWeekStart(courses[m][n].getWeekStart() + "|" + weekStart);
                courses[m][n].setWeekEnd(courses[m][n].getWeekEnd() + "|" + weekEnd);
                courses[m][n].setTeacher(courses[m][n].getTeacher() + "|" + teacher);
                courses[m][n].setCourseIndex(courses[m][n].getCourseIndex() + "|" + courseIndex);
                courses[m][n].setZhou(courses[m][n].getZhou()+"|"+zhou);
            } else {
                courses[m][n].setName(name);
                courses[m][n].setLocation(location);
                courses[m][n].setWeekIndex(weekIndex+"");
                courses[m][n].setWeekStart(weekStart);
                courses[m][n].setWeekEnd(weekEnd);
                courses[m][n].setTeacher(teacher);
                courses[m][n].setCourseIndex(courseIndex);
                courses[m][n].setZhou(zhou);
            }

        }
        for (int i = 0; i < 5; i++) {
            mcourseList.addAll(Arrays.asList(courses[i]).subList(0, 7));
        }
        return mcourseList;
    }
}
