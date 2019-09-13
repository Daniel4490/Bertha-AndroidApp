package com.au.berthaau.Fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.au.berthaau.DataTypesDetailedActivity;

import com.au.berthaau.Fragments.InformativeFragments.DetailedNo2Fragment;
import com.au.berthaau.Fragments.InformativeFragments.DetailedO3Fragment;
import com.au.berthaau.HttpHelpers.HttpGetAsyncTask;
import com.au.berthaau.Models.KorrigeretData;
import com.au.berthaau.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static android.content.Context.MODE_PRIVATE;


public class OverviewFragment extends Fragment {

    private TextView sensorReading;

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:sss").create();

    private SharedPreferences sharedPreferences;

    private Context mainContext;

    // NO2
    private Button no2DetailsButton;
    private ImageView imageViewNo2;
    private TextView textViewTitleNo2;

    // O3
    private Button o3DetailsButton;
    private ImageView imageViewO3;
    private TextView textViewTitleO3;

    // PM 2.5
    private Button pm25DetailsButton;
    private ImageView imageViewPm25;
    private TextView textViewTitlePm25;

    // PM 10
    private Button pm10DetailsButton;
    private ImageView imageViewPm10;
    private TextView textViewTitlePm10;


    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Store context in instance variable
        mainContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        setButtonsEnabled();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_overview, container, false);

        // Latest reading TextViews
        final TextView textViewNo2LatestValue = view.findViewById(R.id.CardViewNO2LatestReadingValue);
        final TextView textViewO3LatestValue = view.findViewById(R.id.CardViewO3LatestReadingValue);
        final TextView textViewPm25LatestValue = view.findViewById(R.id.CardViewPM25LatestReadingValue);
        final TextView textViewPm10LatestValue = view.findViewById(R.id.CardViewPM10LatestReadingValue);

        sharedPreferences = mainContext.getSharedPreferences("latestSensorReading", MODE_PRIVATE);



        SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("no2Value")){
                    textViewNo2LatestValue.setText(sharedPreferences.getFloat("no2Value", 0) + "");
                    textViewO3LatestValue.setText(sharedPreferences.getFloat("o3Value", 0) + "");
                    textViewPm25LatestValue.setText(sharedPreferences.getFloat("pm25Value", 0) + "");
                    textViewPm10LatestValue.setText(sharedPreferences.getFloat("pm10Value", 0) + "");
                }
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);


        // NO2
        no2DetailsButton = view.findViewById(R.id.CardViewNO2ButtonLearnMore);
        imageViewNo2 = view.findViewById(R.id.imageViewNO2);
        textViewTitleNo2 = view.findViewById(R.id.CardViewNO2Title);


        // O3
        o3DetailsButton = view.findViewById(R.id.CardViewO3ButtonLearnMore);
        imageViewO3 = view.findViewById(R.id.imageViewO3);
        textViewTitleO3 = view.findViewById(R.id.CardViewO3Title);

        // PM 2.5
        pm25DetailsButton = view.findViewById(R.id.CardViewPM25ButtonLearnMore);
        imageViewPm25 = view.findViewById(R.id.imageViewPM25);
        textViewTitlePm25 = view.findViewById(R.id.CardViewPM25Title);

        // PM 10
        pm10DetailsButton = view.findViewById(R.id.CardViewPM10ButtonLearnMore);
        imageViewPm10 = view.findViewById(R.id.imageViewPM10);
        textViewTitlePm10 = view.findViewById(R.id.CardViewPM10Title);


        initOnClickListeners();

        return view;
    }


    private void initOnClickListeners(){

        // NO2
        no2DetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pair<View, String> pair1 = Pair.create((View) imageViewNo2, imageViewNo2.getTransitionName());
                Pair<View, String> pair2 = Pair.create((View) textViewTitleNo2, textViewTitleNo2.getTransitionName());

                ActivityOptions activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair1, pair2);
                Intent in = new Intent(getActivity(), DataTypesDetailedActivity.class);

                in.putExtra("fragment",new DetailedNo2Fragment());

                in.putExtra("imageView", R.drawable.ic_directions_car_150dp);
                in.putExtra("textView", R.string.CardviewTitleNO2);
                //in.putExtra("description", R.string.DescriptionTextNo2);

                startActivity(in,activityOptionsCompat.toBundle());

                setButtonsDisabled();
            }
        });

        // O3
        o3DetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View, String> pair1 = Pair.create((View) imageViewO3, imageViewO3.getTransitionName());
                Pair<View, String> pair2 = Pair.create((View) textViewTitleO3, textViewTitleO3.getTransitionName());

                ActivityOptions activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair1, pair2);
                Intent in = new Intent(getActivity(), DataTypesDetailedActivity.class);

                in.putExtra("fragment",new DetailedO3Fragment());

                in.putExtra("imageView", R.drawable.ic_directions_car_150dp);
                in.putExtra("textView", R.string.CardViewTitleO3);
                //in.putExtra("description", R.string.DescriptionTextO3);

                startActivity(in,activityOptionsCompat.toBundle());

                setButtonsDisabled();
            }
        });

        // PM 2.5
        pm25DetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View, String> pair1 = Pair.create((View) imageViewPm25, imageViewPm25.getTransitionName());
                Pair<View, String> pair2 = Pair.create((View) textViewTitlePm25, textViewTitlePm25.getTransitionName());

                ActivityOptions activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair1, pair2);
                Intent in = new Intent(getActivity(), DataTypesDetailedActivity.class);

                in.putExtra("fragment",new DetailedO3Fragment());

                in.putExtra("imageView", R.drawable.ic_directions_car_150dp);
                in.putExtra("textView", R.string.CardViewPM25Title);
                in.putExtra("description", R.string.DescriptionTextO3);

                startActivity(in,activityOptionsCompat.toBundle());

                setButtonsDisabled();
            }
        });


        // PM 10
        pm10DetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View, String> pair1 = Pair.create((View) imageViewPm10, imageViewPm10.getTransitionName());
                Pair<View, String> pair2 = Pair.create((View) textViewTitlePm10, textViewTitlePm10.getTransitionName());

                ActivityOptions activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair1, pair2);
                Intent in = new Intent(getActivity(), DataTypesDetailedActivity.class);

                in.putExtra("fragment",new DetailedO3Fragment());

                in.putExtra("imageView", R.drawable.ic_directions_car_150dp);
                in.putExtra("textView", R.string.CardViewPM10Title);
                in.putExtra("description", R.string.DescriptionTextO3);

                startActivity(in,activityOptionsCompat.toBundle());

                setButtonsDisabled();
            }
        });

    }

    private void setButtonsDisabled(){
        no2DetailsButton.setEnabled(false);
        o3DetailsButton.setEnabled(false);
        pm25DetailsButton.setEnabled(false);
        pm10DetailsButton.setEnabled(false);
    }

    private void setButtonsEnabled(){
        no2DetailsButton.setEnabled(true);
        o3DetailsButton.setEnabled(true);
        pm25DetailsButton.setEnabled(true);
        pm10DetailsButton.setEnabled(true);
    }

}

