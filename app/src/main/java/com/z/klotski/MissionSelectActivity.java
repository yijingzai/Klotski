package com.z.klotski;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MissionSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_select);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        SharedPreferences read = getSharedPreferences("map", MODE_PRIVATE);
        Mission.getFinish(read);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview);
        Mission_List mAdapter = new Mission_List(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
