package com.example.project4;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ToursListActivity extends AppCompatActivity implements OnTourClickListener {

    private static final String TOURS_FILE_NAME = "tours.txt";
    private static final int REQUEST_CODE_DETAILS = 1;
    private List<Tour> tours;
    private RecyclerView recyclerViewTours;
    private ToursRecyclerListAdapter tourAdapter;
    private Button backToDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_list_activity);

        recyclerViewTours = findViewById(R.id.recycler_view);
        recyclerViewTours.setLayoutManager(new LinearLayoutManager(this));

        tours = UserFileUtils.loadTours(this);
        tourAdapter = new ToursRecyclerListAdapter(this, tours, this);
        recyclerViewTours.setAdapter(tourAdapter);

        backToDashboard = findViewById(R.id.back_button);
        backToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToursListActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onTourClick(int position) {
        Intent intent = new Intent(ToursListActivity.this, TourDetailsActivity.class);
        Tour tour = tours.get(position);
        intent.putExtra("NAME", tour.getName());
        intent.putExtra("DESCRIPTION", tour.getDescription());
        intent.putExtra("VIDEO", tour.getVideoUri().toString());
        intent.putExtra("POSITION", position);
        startActivityForResult(intent, REQUEST_CODE_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAILS && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("POSITION", -1);
            if (position != -1) {
                String name = data.getStringExtra("NAME");
                String description = data.getStringExtra("DESCRIPTION");
                String videoString = data.getStringExtra("VIDEO");
                tours.set(position, new Tour(name, description, videoString));
                tourAdapter.notifyItemChanged(position);
                UserFileUtils.saveTours(this, tours); // Save the updated tours list
            }
        }
    }
}


