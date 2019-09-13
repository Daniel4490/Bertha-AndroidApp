package com.au.berthaau.Fragments.InformativeFragments;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.au.berthaau.DataTypesDetailedActivity;
import com.au.berthaau.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataTypesCardViewFragment extends Fragment {

    private Button no2DetailsButton;
    private View imageView;
    private View textView;




    public DataTypesCardViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_types_card_view, container, false);
//
//        no2DetailsButton = view.findViewById(R.id.CardViewNO2ButtonLearnMore);
//        imageView = view.findViewById(R.id.mainImage);
//        textView = view.findViewById(R.id.mainTitle);
//
//        no2DetailsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());
//                Pair<View, String> pair2 = Pair.create(textView, textView.getTransitionName());
//
//                ActivityOptions activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair1, pair2);
//                Intent in = new Intent(getActivity(), DataTypesDetailedActivity.class);
//                startActivity(in,activityOptionsCompat.toBundle());
//            }
//        });

        return view;

    }

}
