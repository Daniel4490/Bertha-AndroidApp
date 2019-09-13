package com.au.berthaau.Fragments.GraphFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.au.berthaau.ChartAxisValueFormatters.DateValueDayFormatter;
import com.au.berthaau.ChartAxisValueFormatters.DateValueHourFormatter;
import com.au.berthaau.ChartMarkerViews.CustomMarkerView;
import com.au.berthaau.GraphInfoActivity;
import com.au.berthaau.HttpHelpers.HttpGetAsyncTask;
import com.au.berthaau.MainActivity;
import com.au.berthaau.Models.KorrigeretData;
import com.au.berthaau.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.savvyapps.togglebuttonlayout.Toggle;
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class GraphFragment extends Fragment {

    private LineChart chart;
    private TextView xAxisTitle;
    private TextView yAxisTitle;
    private TextView graphTitle;
    private ToggleButtonLayout toggleButtonLayout;
    private String sensorId;
    private String selectedPeriodText;
    private ProgressBar progressBar;
    private CheckBox limitLinesCheckBox;


    private DataType selectedDataType;
    private Boolean limitLinesEnabled;

    private Context mainContext;

    private final String httpBaseUrl = "http://envs-atair-web.au.dk/berthaapi/api/KorrigeretData";



    public GraphFragment() {
        // Required empty public constructor
    }

    private enum DataType{
        NO2, O3, PM25, PM10
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = mainContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        sensorId = prefs.getString("sensorID", "sensorIdNotFound");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        // Init view elements
        chart = view.findViewById(R.id.chart);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        xAxisTitle = view.findViewById(R.id.x_axis_title);
        yAxisTitle = view.findViewById(R.id.y_axis_title);
        toggleButtonLayout = view.findViewById(R.id.toggle_button_layout);
        graphTitle = view.findViewById(R.id.graph_title);
        limitLinesCheckBox = view.findViewById(R.id.limit_lines_enabled);


        // Fragment must use AppCompatActivity from Mainactivity to handle toolbar in each individual fragment
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu_graph);

        // Event listeners

        limitLinesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                limitLinesEnabled = isChecked;
                refreshChartData();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity activity = (MainActivity) getActivity();

                Intent intent = new Intent(mainContext, GraphInfoActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
        });

//        if (activity != null) {
//            activity.setSupportActionBar(toolbar);
//            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }


        // ToggleButtonLayout library was intended to be used with Kotlin where a lambda expression would be used instead of the callback listener below.
        // Callback method syntax might seem a little odd because of this.
        toggleButtonLayout.setOnToggledListener(new Function3<ToggleButtonLayout, Toggle, Boolean, Unit>() {
            @Override
            public Unit invoke(ToggleButtonLayout toggleButtonLayout, Toggle toggle, Boolean aBoolean) {
                selectedPeriodText = toggle.getTitle().toString();
                refreshChartData();
                return null;
            }
        });

        // Default values
        IMarker marker = new CustomMarkerView(mainContext, R.layout.marker_view_layout);
        chart.setMarker(marker);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setNoDataText("Ingen data tilgængelig");

        initOnclickListeners(view);

        limitLinesEnabled = false;

        selectedDataType = DataType.NO2;
        selectedPeriodText = toggleButtonLayout.getToggles().get(0).getTitle().toString();
        toggleButtonLayout.setToggled(R.id.toggle_hour, true);


        refreshChartData();

        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        getActivity().getMenuInflater().inflate(R.menu.toolbar_menu_graph, menu);
//    }

    private void refreshChartData(){

        GetHttpCorrectedData task = new GetHttpCorrectedData();

        Log.d("CHART", sensorId);
        String requestUrl = httpBaseUrl + "/" + sensorId + "/" + selectedPeriodText;
        Log.d("CHART", requestUrl);
        task.execute(requestUrl);



    }

    private void initOnclickListeners(View view){

        view.findViewById(R.id.no2_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDataType = DataType.NO2;
                refreshChartData();
            }
        });

        view.findViewById(R.id.o3_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDataType = DataType.O3;
                refreshChartData();
            }
        });

        view.findViewById(R.id.pm25_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDataType = DataType.PM25;
                refreshChartData();
            }
        });

        view.findViewById(R.id.pm10_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDataType = DataType.PM10;
                refreshChartData();
            }
        });
    }

    private void addEntries(ArrayList<KorrigeretData> correctedData){

        Log.d("CHART", correctedData.size() + "");
        List<Entry> entries = new ArrayList<>();
        LineDataSet dataSet;

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelRotationAngle(0);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(true);


//        chart.getXAxis().setLabelCount(7, true);
//        chart.getXAxis().setAxisMinimum(0);
//        chart.getXAxis().setAxisMaximum(60);

        // Format x-axis values based on selected period so values appear more meaningful to the user
        switch (selectedPeriodText.toLowerCase()){
            case "time":
                xAxis.setValueFormatter(new DateValueHourFormatter());
                break;
            case "dag":
                xAxis.setValueFormatter(new DateValueHourFormatter());
                break;
            case "uge":
                xAxis.setValueFormatter(new DateValueDayFormatter());
                xAxis.setLabelRotationAngle(-45);
                chart.setScaleXEnabled(false);
                chart.setScaleYEnabled(false);
                break;
            default:
                xAxis.setValueFormatter(new DateValueHourFormatter());
                break;
        }

        switch (selectedDataType) {
            case NO2:
                setupLineLimits(600, 650, 720);
                graphTitle.setText("NO2 Data - seneste " + selectedPeriodText);
                chart.getDescription().setText("NO2 Data - seneste " + selectedPeriodText);
                for (KorrigeretData data : correctedData) {
                    // Data values below zero are considered invalid
                    if (data.getNO2() > 0){
                        Log.d("CHART", selectedPeriodText);
                        entries.add(new Entry(data.getTidspunkt().getTime(), (float) data.getNO2()));
                    }
                }
                break;
            case O3:
                setupLineLimits(35, 40, 45);
                graphTitle.setText("O3 Data - seneste " + selectedPeriodText);
                chart.getDescription().setText("O3 Data - seneste " + selectedPeriodText);
                for (KorrigeretData data : correctedData) {
                    // Data values below zero are considered invalid
                    if (data.getO3() > 0){
                        // turn data into Entry objects
                        entries.add(new Entry(data.getTidspunkt().getTime(), (float) data.getO3()));
                    }
                }
                break;
            case PM25:
                graphTitle.setText("PM 2.5 Data - seneste " + selectedPeriodText);
                chart.getDescription().setText("PM 2.5 Data - seneste " + selectedPeriodText);
                for (KorrigeretData data : correctedData) {
                    // Data values below zero are considered invalid
                    if (data.getPM25() > 0){
                        // turn data into Entry objects
                        entries.add(new Entry(data.getTidspunkt().getTime(), (float) data.getPM25()));
                    }
                }
                break;
            case PM10:
                graphTitle.setText("PM 10 Data - seneste " + selectedPeriodText);
                chart.getDescription().setText("PM 10 Data - seneste " + selectedPeriodText);
                for (KorrigeretData data : correctedData) {
                    // Data values below zero are considered invalid
                    if (data.getPM10() > 0){
                        // turn data into Entry objects
                        entries.add(new Entry(data.getTidspunkt().getTime(), (float) data.getPM10()));
                    }
                }
                break;
        }

        // From documentation: Entries must be added to a DataSet sorted by their x-position. Otherwise unexpected behaviour might occur. The reason is mainly performance.
        Collections.sort(entries, new EntryXComparator());


        // DataSet object represents a group of entries (e.g. class Entry) inside the chart that belong together.
        // It is designed to logically separate different groups of values in the chart.
        dataSet = new LineDataSet(entries, selectedDataType.toString()); // add entries to dataset
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(ColorTemplate.rgb("#0d47a1"));

        dataSet.setLineWidth(3);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(0);

        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setDrawVerticalHighlightIndicator(false);

        LineData lineData = new LineData(dataSet);
        //chart.animateX(1500);
        chart.setData(lineData);
        chart.invalidate(); // After calling invalidate() the chart is refreshed and the provided data is drawn.
    }

    private void setupLineLimits(int lowConcentration, int mediumConcentration, int highConcentration){

        // Line limits
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();

        if (!limitLinesEnabled){
            return;
        }


        LimitLine highConcentrationLine = new LimitLine(highConcentration, "Høj koncentration");
        highConcentrationLine.setLineColor(Color.RED);
        highConcentrationLine.setLineWidth(4);
        highConcentrationLine.setTextSize(12);

        LimitLine mediumConcentrationLine = new LimitLine(mediumConcentration, "Middel koncentration");
        mediumConcentrationLine.setLineColor(Color.YELLOW);
        mediumConcentrationLine.setLineWidth(4);
        mediumConcentrationLine.setTextSize(12);

        LimitLine lowConcentrationLine = new LimitLine(lowConcentration, "Lav koncentration");
        lowConcentrationLine.setLineColor(Color.GREEN);
        lowConcentrationLine.setLineWidth(4);
        lowConcentrationLine.setTextSize(12);


        leftAxis.addLimitLine(highConcentrationLine);
        leftAxis.addLimitLine(mediumConcentrationLine);
        leftAxis.addLimitLine(lowConcentrationLine);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mainContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class GetHttpCorrectedData extends HttpGetAsyncTask{

        private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:sss").create();
        private final ArrayList<KorrigeretData> korrigeretDataArrayList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

           korrigeretDataArrayList.clear();

           // Update view elements
           progressBar.setVisibility(View.GONE);

            // Every data type shown in graphs share the same y-axis unit
           yAxisTitle.setText("μg/m\u00B3");

            switch (selectedPeriodText) {
                case "Time":
                    xAxisTitle.setText("Tidspunkt");
                    break;
                case "Dag":
                    xAxisTitle.setText("Tidspunkt");
                    break;
                case "Uge":
                    xAxisTitle.setText("Tid (Dage)");
                    break;
                default:
                    xAxisTitle.setText("Tid");
                    break;
            }

           // Add data to Chart
           KorrigeretData[] korrigeretData = gson.fromJson(s, KorrigeretData[].class);

           Collections.addAll(korrigeretDataArrayList, korrigeretData);

           addEntries(korrigeretDataArrayList);

           this.cancel(true);
//            chart.getData().setHighlightEnabled(false);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            progressBar.setVisibility(View.GONE);
        }

    }

}
