package com.example.project4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class EditTourActivity extends AppCompatActivity {
    private EditText tourNameEditText;
    private EditText tourDescriptionEditText;
    private Button saveTourButton;
    private Button addRecordingButton;
    private String videoUri;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tour);

        tourNameEditText = findViewById(R.id.tour_name_et);
        tourDescriptionEditText = findViewById(R.id.tour_description_et);
        saveTourButton = findViewById(R.id.save_tour_button);
        addRecordingButton = findViewById(R.id.add_recording_button);

        // Get data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        String description = intent.getStringExtra("DESCRIPTION");
        videoUri = intent.getStringExtra("VIDEO");
        position = intent.getIntExtra("POSITION", -1);


        tourNameEditText.setText(name);
        tourDescriptionEditText.setText(description);

        saveTourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTour();
            }
        });
    }

    private void saveTour() {
        String name = tourNameEditText.getText().toString().trim();
        String description = tourDescriptionEditText.getText().toString().trim();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("NAME", name);
        resultIntent.putExtra("DESCRIPTION", description);
        resultIntent.putExtra("VIDEO", videoUri);
        resultIntent.putExtra("POSITION", position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
