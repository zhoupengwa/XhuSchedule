package weily.com.schedule.adapter;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import weily.com.schedule.R;
import weily.com.schedule.activity.DetailCourseActivity;
import weily.com.schedule.beans.Course;

/**
 * Created by peng on 2017/9/11.
 * this is a adapter for recyclerview item
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private int mCurrentWeek;
    private RecyclerView mRecyclerView;
    private List<Course> mCourseList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View courseView;//用于设置点击事件，获取顶层的view
        TextView courseText;

        ViewHolder(View itemView) {
            super(itemView);
            courseText = (TextView) itemView.findViewById(R.id.course_text);
            courseView = itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        view.getLayoutParams().width = mRecyclerView.getMeasuredWidth() / 7;
        final ViewHolder holder = new ViewHolder(view);
        //设置点击事件
        holder.courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int position = holder.getAdapterPosition();
                final Course course1 = mCourseList.get(position);
                if (course1.getName().contains("|")) {
                    //多节课的处理，解析成对话框列表，跳转到详细课活动
                    List<String> courseTempList = new ArrayList<>();
                    final String[] courseName = new String[10];//课程名
                    final String[] courseWeekStart = new String[10];//开始周
                    final String[] courseWeekEnd = new String[10];//结束周
                    final String[] courseLocation = new String[10];//位置
                    final String[] courseTeacher = new String[10];//老师
                    final String[] courseCourseIndex = new String[10];//节数
                    final String[] courseWeekIndex = new String[10];//节数
                    final String[] courseZhou = new String[10];//是否单周
                    StringTokenizer st = new StringTokenizer(course1.getName(), "|");
                    StringTokenizer st2 = new StringTokenizer(course1.getWeekStart(), "|");
                    StringTokenizer st3 = new StringTokenizer(course1.getWeekEnd(), "|");
                    StringTokenizer st4 = new StringTokenizer(course1.getLocation(), "|");
                    StringTokenizer st5 = new StringTokenizer(course1.getTeacher(), "|");
                    StringTokenizer st6 = new StringTokenizer(course1.getCourseIndex(), "|");
                    StringTokenizer st7 = new StringTokenizer(course1.getWeekIndex(), "|");
                    StringTokenizer st8 = new StringTokenizer(course1.getZhou(), "|");
                    int i = 1;
                    while (st.hasMoreElements()) {
                        courseName[i] = st.nextToken();
                        courseWeekStart[i] = st2.nextToken();
                        courseWeekEnd[i] = st3.nextToken();
                        courseLocation[i] = st4.nextToken();
                        courseTeacher[i] = st5.nextToken();
                        courseCourseIndex[i] = st6.nextToken();
                        courseWeekIndex[i] = st7.nextToken();
                        courseZhou[i] = st8.nextToken();
                        //courseZhou[i].startWith("1");表示周满足当前周条件
                        if ((courseZhou[i].startsWith("1"))&&(Integer.parseInt(courseWeekStart[i]) <= mCurrentWeek) && (Integer.parseInt(courseWeekEnd[i]) >= mCurrentWeek)) {
                            courseTempList.add(courseName[i] + "（今日有课）");
                        } else {
                            courseTempList.add(courseName[i]);
                        }
                        i++;
                    }
                    View tempView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_course_name, null, false);
                    ListView listView = (ListView) tempView.findViewById(R.id.course_name_list);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_list_item_1, courseTempList);
                    listView.setAdapter(adapter);
                    final AlertDialog dialog = new AlertDialog.Builder(v.getContext()).setView(tempView).show();
                    dialog.setView(tempView);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(v.getContext(), DetailCourseActivity.class);
                            intent.putExtra("name", courseName[position + 1]);
                            intent.putExtra("courseIndex", courseCourseIndex[position + 1]);
                            intent.putExtra("weekIndex", courseWeekIndex[position + 1]);
                            intent.putExtra("weekStart", courseWeekStart[position + 1]);
                            intent.putExtra("weekEnd", courseWeekEnd[position + 1]);
                            intent.putExtra("teacher", courseTeacher[position + 1]);
                            intent.putExtra("location", courseLocation[position + 1]);
                            intent.putExtra("zhou", courseZhou[position + 1]);
                            v.getContext().startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    //单节课直接跳转到详细课活动
                } else {
                    Intent intent = new Intent(v.getContext(), DetailCourseActivity.class);
                    intent.putExtra("name", course1.getName());
                    intent.putExtra("courseIndex", course1.getCourseIndex());
                    intent.putExtra("weekIndex", course1.getWeekIndex());
                    intent.putExtra("weekStart", course1.getWeekStart());
                    intent.putExtra("weekEnd", course1.getWeekEnd());
                    intent.putExtra("teacher", course1.getTeacher());
                    intent.putExtra("location", course1.getLocation());
                    intent.putExtra("zhou", course1.getZhou());
                    v.getContext().startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.setIsRecyclable(false);
        //初始颜色为灰色
        Course course = mCourseList.get(position);
        String name = course.getName();
        String weekStart = course.getWeekStart();//起始周
        String weekEnd = course.getWeekEnd();//结束周
        String location = course.getLocation();//位置
        String zhou = course.getZhou();//单双周
        if (course.getName().equals("")) {
            //不存在课的地方隐藏，且不可点击
            holder.courseText.setBackgroundColor(Color.parseColor("#00000000"));
            holder.courseText.setEnabled(false);
        } else if (name.contains("|")) {
            int i = 1;
            String[] courseName = new String[10];
            String[] courseWeekStart = new String[10];
            String[] courseWeekEnd = new String[10];
            String[] courseLocation = new String[10];
            String[] courseZhou = new String[10];
            StringTokenizer st = new StringTokenizer(name, "|");
            StringTokenizer st2 = new StringTokenizer(weekStart, "|");
            StringTokenizer st3 = new StringTokenizer(weekEnd, "|");
            StringTokenizer st4 = new StringTokenizer(location, "|");
            StringTokenizer st5 = new StringTokenizer(zhou, "|");
            while (st.hasMoreElements()) {
                courseName[i] = st.nextToken();
                courseWeekStart[i] = st2.nextToken();
                courseWeekEnd[i] = st3.nextToken();
                courseLocation[i] = st4.nextToken();
                courseZhou[i] = st5.nextToken();//提取到了单双周
                if (courseZhou[i].startsWith("1") && (Integer.parseInt(courseWeekStart[i]) <= mCurrentWeek) && (Integer.parseInt(courseWeekEnd[i]) >= mCurrentWeek)) {
                    holder.courseText.setText(courseName[i] + "\n@" + courseLocation[i] + "\n......");
                    holder.courseText.setBackgroundColor(Color.parseColor("#94C650C8"));
                    holder.courseText.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                } else {
                    holder.courseText.setText(courseName[i] + "\n@" + courseLocation[i] + "\n......");
                }
                i++;
            }//单节课的处理
        } else if (zhou.startsWith("1") && (Integer.parseInt(weekStart) <= mCurrentWeek) && (Integer.parseInt(weekEnd) >= mCurrentWeek)) {
            holder.courseText.setText(name + "\n@" + location);
            holder.courseText.setBackgroundColor(Color.parseColor("#96267397"));
            holder.courseText.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.courseText.setText(name + "\n@" + location);
        }
    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }


    public CourseAdapter(List<Course> courseList, RecyclerView recyclerView, int currentWeek) {
        mCurrentWeek = currentWeek;
        mCourseList = courseList;
        mRecyclerView = recyclerView;
    }


}
