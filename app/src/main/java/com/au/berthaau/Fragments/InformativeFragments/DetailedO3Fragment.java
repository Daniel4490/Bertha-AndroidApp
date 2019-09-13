package com.au.berthaau.Fragments.InformativeFragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.au.berthaau.R;

import java.io.Serializable;


public class DetailedO3Fragment extends Fragment implements Serializable {


    public DetailedO3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detailed_o3, container, false);
    }

}
