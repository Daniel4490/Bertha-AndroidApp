package com.au.berthaau.ChartMarkerViews;

import android.content.Context;
import android.widget.TextView;

import com.au.berthaau.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {

    private MPPointF offset;

    private TextView testTextView;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        testTextView = findViewById(R.id.marker_view_text);
    }

    @Override
    public MPPointF getOffset() {

        if (offset == null){
            offset = new MPPointF(-(getWidth() / 2), -getHeight() + 30);
        }

        return offset;
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        testTextView.setText(e.getY() + " ");

        super.refreshContent(e, highlight);
    }
}
