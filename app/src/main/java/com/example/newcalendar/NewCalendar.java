package com.example.newcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zwp
 * @description:
 * @date: 2020/11/2 14:41
 */
public class NewCalendar extends LinearLayout {
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private Calendar curDate = Calendar.getInstance();
    private String displayFormat;

    public NewCalendarListener listener;

    //构造函数
    public NewCalendar(Context context) {
        super(context);
    }

    public NewCalendar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public NewCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    private void initControl(Context context, AttributeSet attrs) {
        // 绑定控件
        bindControl(context);
        // 绑定事件
        bindControlEvent();


        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.NewCalendar);
        try {
            String format = ta.getString(R.styleable.NewCalendar_dateFormat);
            displayFormat = format;
            if (displayFormat == null) {
                displayFormat = "MMM yyyy";
            }
        } finally {
            ta.recycle();//回收
        }
        renderCalendar();
    }

    private void bindControlEvent() {
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                curDate.add(Calendar.MONTH, -1);
                renderCalendar();
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                curDate.add(Calendar.MONTH, +1);
                renderCalendar();
            }
        });
    }

    /**
     * 渲染方法
     */
    private void renderCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat(displayFormat);
        txtDate.setText(sdf.format(curDate.getTime()));

        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) curDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int preDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -preDays);
        int maxCellCount = 6 * 7;
        while (cells.size() < maxCellCount) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        grid.setAdapter(new CalendarAdapter(getContext(), cells));
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener == null) {
                    return false;
                } else {
                        listener.onItemLongPress((Date)parent.getItemAtPosition(position));
                        return true;
                }
            }
        });
    }

    private class CalendarAdapter extends ArrayAdapter<Date> {
        LayoutInflater inflater;

        public CalendarAdapter(@NonNull Context context, ArrayList<Date> days) {
            super(context, R.layout.calendar_text_day, days);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Date date = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar_text_day, parent, false);
            }
            int day = date.getDate();
            ((TextView) convertView).setText(String.valueOf(day));
            Date now = new Date();
            boolean isTheSameMonth = false;
            if (date.getMonth() == now.getMonth()) {
                isTheSameMonth = true;
            }
            if (isTheSameMonth) {
                ((TextView) convertView).setTextColor(Color.parseColor("#000000"));
            } else {
                //不是当月的日期
                ((TextView) convertView).setTextColor(Color.parseColor("#DCDCDC"));
            }

            if (now.getDate() == date.getDate() && now.getMonth() == date.getMonth()
                    && now.getYear() == date.getYear()) {
                ((TextView) convertView).setTextColor(Color.parseColor("#ff0000"));
                ((Calendar_day_textView) convertView).isToday = true;
            }
            return convertView;
        }

    }

    private void bindControl(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_view, this);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        txtDate = findViewById(R.id.txtDate);
        grid = findViewById(R.id.calendar_grid);
    }

    public interface NewCalendarListener {
        void onItemLongPress(Date day);

    }
}
