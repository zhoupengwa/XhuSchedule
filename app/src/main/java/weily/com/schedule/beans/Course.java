package weily.com.schedule.beans;

/**
 * Created by peng on 2017/9/11.
 * the object course
 */

public class Course {
    private String name;
    private String weekIndex;//此课程要上的周数
    private String courseIndex;//该课程要上的节数
    private String weekStart;//起始周
    private String weekEnd;//结束周
    private String teacher;//；老师
    private String location;//位置
    private String zhou;//是否单周

    public Course(String name, String weekIndex, String courseIndex, String weekStart,
                  String weekEnd, String teacher, String location, String zhou) {
        this.name = name;
        this.weekIndex = weekIndex;
        this.courseIndex = courseIndex;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.teacher = teacher;
        this.location = location;
        this.zhou = zhou;
    }

    public String getZhou() {
        return zhou;
    }

    public void setZhou(String zhou) {
        this.zhou = zhou;
    }

    public String getName() {
        return name;
    }

    public String getWeekIndex() {
        return weekIndex;
    }

    public String getCourseIndex() {
        return courseIndex;
    }

    public String getWeekStart() {
        return weekStart;
    }

    public String getWeekEnd() {
        return weekEnd;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeekIndex(String weekIndex) {
        this.weekIndex = weekIndex;
    }

    public void setCourseIndex(String courseIndex) {
        this.courseIndex = courseIndex;
    }

    public void setWeekStart(String weekStart) {
        this.weekStart = weekStart;
    }

    public void setWeekEnd(String weekEnd) {
        this.weekEnd = weekEnd;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
