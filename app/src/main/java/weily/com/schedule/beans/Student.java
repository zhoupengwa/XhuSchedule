package weily.com.schedule.beans;

/**
 * Created by peng on 2017/9/11.
 * the object student
 */

public class Student {
    private  String name;
    private String no;
    private String college;
    private String profession;
    private  String gradeClass;

    public Student(String name, String no, String college, String profession, String gradeClass) {
        this.name = name;//学生姓名
        this.no = no;//学号
        this.college = college;//所属学院
        this.profession = profession;//专业
        this.gradeClass = gradeClass;//班级
    }

    public String getName() {
        return name;
    }

    public String getNo() {
        return no;
    }

    public String getCollege() {
        return college;
    }

    public String getProfession() {
        return profession;
    }

    public String getGradeClass() {
        return gradeClass;
    }
}
