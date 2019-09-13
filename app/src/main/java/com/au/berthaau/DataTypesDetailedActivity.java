package com.au.berthaau;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.au.berthaau.Fragments.InformativeFragments.DetailedNo2Fragment;
import com.au.berthaau.Fragments.OverviewFragment;

public class DataTypesDetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_types_detailed);





        Intent intent = getIntent();

        findViewById(R.id.mainImage).setBackgroundResource(intent.getIntExtra("imageView", R.drawable.ic_directions_car_150dp));

        TextView textViewTitle = findViewById(R.id.mainTitle);
        textViewTitle.setText(intent.getIntExtra("textView", R.string.ValueNotFound));

//      TextView textViewDescription = findViewById(R.id.mainDescription);
//      textViewDescription.setText(intent.getIntExtra("description", R.string.ValueNotFound));

        Fragment fragment = (Fragment) intent.getSerializableExtra("fragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        findViewById(R.id.closeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });


        findViewById(R.id.mainTitle).setTransitionName("dataTypeTitle");
        findViewById(R.id.mainImage).setTransitionName("dataTypeImage");
    }
}
