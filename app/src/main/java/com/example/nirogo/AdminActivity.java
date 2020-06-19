package com.example.nirogo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    List<AdminShow> list = new ArrayList<>();
    AdminAdapter adapter;
    ProgressBar progressBar;
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }
}