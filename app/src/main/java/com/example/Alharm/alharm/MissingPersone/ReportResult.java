package com.example.Alharm.alharm.MissingPersone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.Alharm.alharm.R;

import java.util.ArrayList;

public class ReportResult extends AppCompatActivity {
    private ArrayList<String> user_info_list;
    private ArrayList<String> Percentage_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result);
    }
}
