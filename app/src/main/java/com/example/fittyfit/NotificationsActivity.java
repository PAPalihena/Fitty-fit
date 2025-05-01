package com.example.fittyfit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Setup navigation bar
        setupNavigationBar();
    }
} 