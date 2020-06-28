package com.example.customcalendarview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.example.customcalendarview.R;
import com.example.customcalendarview.adapter.BaseRecycerAdapter;
import com.example.customcalendarview.adapter.ViewHolder;
import com.example.customcalendarview.bean.CalendarBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomCalendarView extends FrameLayout {

    protected float FLIP_DISTANCE;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

    private RecyclerView listView;
    private ArrayList<CalendarBean> mDatas = new ArrayList<>();
    private BaseRecycerAdapter mAdapter;

    //当前显示月份
    private CalendarBean mCurrentMonthCalendar;

    public CustomCalendarView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CustomCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        FLIP_DISTANCE = ViewConfiguration.get(context).getScaledTouchSlop();
        View view = LayoutInflater.from(context).inflate(R.layout.view_calendar, this);
        listView = view.findViewById(R.id.list_date);

        initAdapter();

        initData(null);

        listView.setLayoutManager(new GridLayoutManager(context, 7));
        listView.setAdapter(mAdapter);


    }

    private float downX;
    private float downY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();

                float suduY = Math.abs(downY - upY);
                if (upX - downX > suduY && upX - downX > FLIP_DISTANCE) {
                    goLastMonth();
                }

                if (downX - upX > suduY && downX - upX > FLIP_DISTANCE) {
                    goNextMonth();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void initAdapter() {
        mAdapter = new BaseRecycerAdapter<CalendarBean>(getContext(), mDatas, R.layout.item_scheduling_day) {
            @Override
            public void onBindViewHolder(ViewHolder viewHolder, CalendarBean itemVO, int position) {
                TextView tv_day = viewHolder.findViewById(R.id.tv_day);
                TextView tv_type = viewHolder.findViewById(R.id.tv_type);
                TextView tv_state = viewHolder.findViewById(R.id.tv_state);
                tv_day.setText(itemVO.getDay());

                if (itemVO.getDayType() == 1) {
                    //上个月数据
                    tv_day.setTextColor(getResources().getColor(R.color.color_999999));
                    viewHolder.getRootView().setBackgroundColor(getResources().getColor(R.color.color_ffffff));
                    tv_type.setVisibility(INVISIBLE);
                    tv_state.setVisibility(INVISIBLE);
                } else {
                    viewHolder.getRootView().setBackgroundColor(getResources().getColor(R.color.color_F5f5f5));
                    tv_day.setTextColor(getResources().getColor(R.color.color_434343));
                    tv_type.setVisibility(VISIBLE);
                    tv_state.setVisibility(VISIBLE);
                }
            }
        };
    }


    /**
     * 初始化时间
     *
     * @param indexDate yyyy-MM-dd
     */
    private void initData(CalendarBean indexDate) {

        Calendar instance = Calendar.getInstance();

        if (indexDate != null) {
            //自定月份
            instance.setTime(indexDate.getDate());
        }

        //获取当前月最大的日期
        int maxDate = instance.getActualMaximum(Calendar.DATE);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        //获取当前月所有的天数
        for (int i = 0; i < maxDate; i++, instance.add(Calendar.DATE, 1)) {
            int day = instance.get(Calendar.DAY_OF_MONTH);
            CalendarBean calendarBean = new CalendarBean();
            calendarBean.setDay(String.valueOf(day));
            calendarBean.setDate(instance.getTime());
            mDatas.add(calendarBean);
        }

        //赋值当前月份
        mCurrentMonthCalendar = mDatas.get(0);

        ArrayList<CalendarBean> calendarBeans = processDay(mDatas);
        mDatas.clear();
        mDatas.addAll(calendarBeans);

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public String getMonth() {
        return simpleDateFormat.format(mCurrentMonthCalendar.getDate());
    }

    /**
     * 显示下一个月
     */
    public void goNextMonth() {
        String lastMonth = simpleDateFormat.format(mCurrentMonthCalendar.getDate());
        Calendar instance = Calendar.getInstance();
        instance.setTime(mCurrentMonthCalendar.getDate());
        instance.add(Calendar.MONTH, 1);
        CalendarBean tempBean = new CalendarBean();
        tempBean.setDate(instance.getTime());

        mDatas.clear();
        initData(tempBean);
        String newMonth = simpleDateFormat.format(tempBean.getDate());
        if (listener != null) {
            listener.onMonthChanger(lastMonth, newMonth);
        }
    }

    /**
     * 显示上一个月
     */
    public void goLastMonth() {
        String lastMonth = simpleDateFormat.format(mCurrentMonthCalendar.getDate());
        Calendar instance = Calendar.getInstance();
        instance.setTime(mCurrentMonthCalendar.getDate());
        instance.add(Calendar.MONTH, -1);
        CalendarBean tempBean = new CalendarBean();
        tempBean.setDate(instance.getTime());

        mDatas.clear();
        initData(tempBean);
        String newMonth = simpleDateFormat.format(tempBean.getDate());
        if (listener != null) {
            listener.onMonthChanger(lastMonth, newMonth);
        }
    }


    /**
     * 拼接上一个月数据和下一个月数据
     *
     * @param signDatas
     * @return
     */
    private ArrayList<CalendarBean> processDay(ArrayList<CalendarBean> signDatas) {

        ArrayList<CalendarBean> list = new ArrayList<>();

        //获取上一个月需要补全的天数
        ArrayList<CalendarBean> lastMonth = getLastMonth(signDatas.get(0));

        //获取下一个月需要补全的天数
        ArrayList<CalendarBean> nextMonth = getNextMonth(signDatas.get(0));

        if (lastMonth != null && lastMonth.size() > 0) {
            //如果有上一个月的天数，进行添加
            list.addAll(lastMonth);
        }

        if (signDatas != null) {
            //当前月天数
            list.addAll(signDatas);
        }

        if (nextMonth != null && nextMonth.size() > 0) {
            //如果有下一个月的天数，进行添加
            list.addAll(nextMonth);
        }
        return list;
    }

    /**
     * 获取上一个月需要补全的天数
     *
     * @return
     */
    private ArrayList<CalendarBean> getLastMonth(CalendarBean calendarBean) {
        //获取当前月第一天
        Calendar calendarFirstDay = Calendar.getInstance();
        calendarFirstDay.setTime(calendarBean.getDate());
        calendarFirstDay.set(Calendar.DAY_OF_MONTH, 1);
        //当月第一天是星期几
        int firstDayOfWeek = calendarFirstDay.get(Calendar.DAY_OF_WEEK);
        if (firstDayOfWeek != 1) {
            /**
             *   firstDayOfWeek =1时，就是星期日，当前月第一天已经处于星期日，不需要添加上一个月补充天数
             */
            //需要补全的天数，当needAdd为负数时候，说明是星期日，需要补6天
            int needAdd = firstDayOfWeek - 1;

            ArrayList<CalendarBean> lastMonth = new ArrayList<>();
            for (int i = 0; i < needAdd; i++) {
                CalendarBean dayBean = new CalendarBean();
                //0：当月，1：上月，2：下一个月
                dayBean.setDayType(1);
                dayBean.setWeekOfDay(getWeekString(i));
                //取出上一个月需要补全的天数
                //calendarFirstDay.set(Calendar.DAY_OF_MONTH, 0 - i);
                calendarFirstDay.add(Calendar.DATE, -1);
                //上一个月的天数
                int lastMonthDay = calendarFirstDay.get(Calendar.DAY_OF_MONTH);
                dayBean.setDay(String.valueOf(lastMonthDay));
                dayBean.setDate(dayBean.getDate());
                lastMonth.add(0, dayBean);
            }

            return lastMonth;

        }
        return new ArrayList<>();
    }

    /**
     * 获取下一个月需要补全的天数
     *
     * @return
     */
    private ArrayList<CalendarBean> getNextMonth(CalendarBean calendarBean) {

        //获取当前月最后一天
        Calendar calendarLastDay = Calendar.getInstance();
        calendarLastDay.setTime(calendarBean.getDate());
        calendarLastDay.add(Calendar.MONTH, 1);
        calendarLastDay.set(Calendar.DAY_OF_MONTH, 0);
        //获取当前月最后一天是星期几
        int nextDayOfWeek = calendarLastDay.get(Calendar.DAY_OF_WEEK);
        if (nextDayOfWeek != 7) {
            /**
             *   nextDayOfWeek =7时，就是星期六，当前月最后一天已经处于星期六，不需要添加上一个月补充天数
             */
            int needAdd = 7 - nextDayOfWeek;

            ArrayList<CalendarBean> nextMonth = new ArrayList<>();
            for (int i = 1; i <= needAdd; i++) {
                CalendarBean dayBean = new CalendarBean();
                //0：当月，1：上月，2：下一个月
                dayBean.setDayType(2);
                dayBean.setWeekOfDay(getWeekString(i));
                //取出上一个月需要补全的天数
                calendarLastDay.set(Calendar.DAY_OF_MONTH, i);
                //上一个月的天数
                int lastMonthDay = calendarLastDay.get(Calendar.DAY_OF_MONTH);
                dayBean.setDay(String.valueOf(lastMonthDay));
                dayBean.setDate(dayBean.getDate());
                nextMonth.add(dayBean);
            }
            return nextMonth;
        }
        return new ArrayList<>();
    }

    private String getWeekString(int i) {
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "超出范围";
        }
    }

    private OnMonthChangerListener listener;

    public void setOnMonthChangerListener(OnMonthChangerListener listener) {
        this.listener = listener;
    }

    public interface OnMonthChangerListener {
        void onMonthChanger(String lastMonth, String newMonth);
    }
}