package com.agrobx.agrotic.tutorial;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ViewerPersonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_viewer);
        Intent intent = getIntent();
        String action = intent.getAction();
        PersonDataModel personDataModel = (PersonDataModel)
                intent.getSerializableExtra("PersonDataModel");
        TextView firstNameView = (TextView)
                findViewById(R.id.firstNameViewerId);
        TextView lastNameView = (TextView) findViewById(R.id.lastNameViewerId);
        firstNameView.setText(personDataModel.getFirstName());
        lastNameView.setText(personDataModel.getLastName());
    }
}