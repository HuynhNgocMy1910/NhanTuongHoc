package com.my.kltn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ResultActivity extends AppCompatActivity { // ✅ Bổ sung AppCompatActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result); // Layout của ảnh phân tích

        ImageView imageView = findViewById(R.id.imageViewResult);
        TextView textView = findViewById(R.id.textViewResult);
        TextView timestampView = findViewById(R.id.textViewTimestamp);

        // Nhận dữ liệu từ Intent
        String imageUri = getIntent().getStringExtra("imageUri");
        String message = getIntent().getStringExtra("message");
        String timestamp = getIntent().getStringExtra("timestamp");

        Glide.with(this).load(Uri.parse(imageUri)).into(imageView);
        textView.setText(message);
        timestampView.setText("Thời gian: " + timestamp);
    }
}
