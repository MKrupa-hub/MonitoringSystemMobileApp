package com.example.monitoringsystem.fragments.DoctorFragments;

import android.content.Context;
import android.widget.TextView;

import com.example.monitoringsystem.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.Calendar;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView (Context context) {
        super(context, R.layout.custom_marker);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(getDate(Long.valueOf((long) e.getX())) + "\n" + getTime(Long.valueOf((long) e.getX()))); // set the entry-value as the display text
    }

    private String getDate(long timestampValue){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestampValue);
        return cal.get(Calendar.DATE)+ "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
    }

    private String getTime(long timestampValue){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestampValue);
        return cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
    }


}
