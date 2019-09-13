package com.au.berthaau;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

public class GraphInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_info);

        Toolbar toolbar = findViewById(R.id.toolbar_graph_info);


        // Navigation to previous activity
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Animation is used even if user presses Android back button (which triggers onPause in lifecycle)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
