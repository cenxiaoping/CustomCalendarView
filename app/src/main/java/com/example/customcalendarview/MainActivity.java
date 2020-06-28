package com.example.customcalendarview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.customcalendarview.widget.CustomCalendarView;

public class MainActivity extends AppCompatActivity {

    private CustomCalendarView calendarView;
    private TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        tv_date = findViewById(R.id.tv_date);
        ImageView iv_last_month = findViewById(R.id.iv_last_month);
        ImageView iv_next_month = findViewById(R.id.iv_next_month);

        String month = calendarView.getMonth();
        tv_date.setText(month);

        iv_last_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上一个月
                calendarView.goLastMonth();
            }
        });
        iv_next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下一个月
                calendarView.goNextMonth();
            }
        });

        calendarView.setOnMonthChangerListener(new CustomCalendarView.OnMonthChangerListener() {
            @Override
            public void onMonthChanger(String lastMonth, String newMonth) {
                tv_date.setText(newMonth);
            }
        });

    }
}