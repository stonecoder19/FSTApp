package com.uwimonacs.fstmobile.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CalendarView;

import com.uwimonacs.fstmobile.MyApplication;
import com.uwimonacs.fstmobile.R;
import com.uwimonacs.fstmobile.adapters.SASTimetableAdapter;
import com.uwimonacs.fstmobile.models.ComponentDate;
import com.uwimonacs.fstmobile.models.Course;
import com.uwimonacs.fstmobile.models.SASConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class SASTimetableActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private SASConfig sasConfig;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView webView;
    private AccountManager manager;
    private Account account;
    private List<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sastimetable);
        sasConfig = MyApplication.getSasConfig();
        manager = AccountManager.get(this);
        account = manager.getAccountsByType("UWI")[0];
        webView = MyApplication.getWebView();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.timetable_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        updateCourses();
        final CalendarView calendarView = (CalendarView) findViewById(R.id.sas_timetable);
        calendarView.setDate(System.currentTimeMillis());

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sas_timetable_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final Calendar calendar = Calendar.getInstance();
        final List<ComponentDate> dates = new ArrayList<>();
        final List<Course> localCourses = new ArrayList<>();

        for (int i = 0; i < courses.size(); i++){
            if (compareDates(courses.get(i).getStart(), courses.get(i).getEnd(), calendar)) {
                final List<ComponentDate> courseDates = courses.get(i).getDates();
                for (int j = 0; j < courseDates.size(); j++) {
                    if (courseDates.get(j).getDay() == calendar.get(Calendar.DAY_OF_WEEK)) {
                        localCourses.add(courses.get(i));
                        dates.add(courseDates.get(j));
                        break;
                    }
                }
            }
        }
        final SASTimetableAdapter adapter = new SASTimetableAdapter(localCourses, dates, this);
        sasConfig.setTimetableActivity(this);
        sasConfig.setSwipe1(swipeRefreshLayout);
        recyclerView.setAdapter(adapter);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                final Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.YEAR, year);
                newDate.set(Calendar.MONTH, month);
                newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                final List<Course> localCourses = new ArrayList<>();
                for (int i = 0; i < courses.size(); i++) {
                    final Calendar startDate = courses.get(i).getStart(),
                    endDate = courses.get(i).getEnd();
                    if (compareDates(startDate, endDate, newDate)) {
                        localCourses.add(courses.get(i));
                    }
                }
                adapter.updateCourses(localCourses, newDate);
            }
        });
    }

    public void updateCourses(){
        this.courses = sasConfig.student.getTimeTable().getCourses();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    public boolean compareDates(Calendar min, Calendar max, Calendar calendar) {
        boolean between = false;

        if (calendar.get(Calendar.YEAR) >= min.get(Calendar.YEAR) && calendar.get(Calendar.YEAR) <= max.get(Calendar.YEAR)) {
            if (min.get(Calendar.YEAR) == max.get(Calendar.YEAR)){
                if (calendar.get(Calendar.MONTH) >= min.get(Calendar.MONTH) && calendar.get(Calendar.MONTH) <= max.get(Calendar.MONTH)) {
                    //Compare days
                    if (calendar.get(Calendar.MONTH) == min.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) >= min.get(Calendar.DAY_OF_MONTH)) {
                        between = true;
                    }
                    else if (calendar.get(Calendar.MONTH) == max.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) <= max.get(Calendar.DAY_OF_MONTH)) {
                        between = true;
                    }
                    else if (calendar.get(Calendar.MONTH) > min.get(Calendar.MONTH) && calendar.get(Calendar.MONTH) < max.get(Calendar.MONTH)) {
                        between = true;
                    }
                }
            }
            else {
                max.set(Calendar.MONTH, (11 + min.get(Calendar.MONTH)));
                if (calendar.get(Calendar.MONTH) >= min.get(Calendar.MONTH) && calendar.get(Calendar.MONTH) <= max.get(Calendar.MONTH)) {
                    //Compare days
                    if (calendar.get(Calendar.MONTH) == min.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) >= min.get(Calendar.DAY_OF_MONTH)) {
                        between = true;
                    }
                    else if (calendar.get(Calendar.MONTH) == max.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) <= max.get(Calendar.DAY_OF_MONTH)) {
                        between = true;
                    }
                    else if (calendar.get(Calendar.MONTH) > min.get(Calendar.MONTH) && calendar.get(Calendar.MONTH) < max.get(Calendar.MONTH)) {
                        between = true;
                    }
                }
            }
        }
        return between;
    }

    @Override
    public void onRefresh() {
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.sasConfig.passiveLogin('<body>'+document.getElementsByTagName('body')[0].innerHTML+'</body>', 'timetable');");
                super.onPageFinished(view, url);
            }
        });
        String idNumber = sasConfig.student.getIdNumber();
        String password = manager.getPassword(account);
        String formData = "sid="+idNumber+"&PIN="+password;
        webView.postUrl(getResources().getString(R.string.login_post), formData.getBytes());
    }
}
