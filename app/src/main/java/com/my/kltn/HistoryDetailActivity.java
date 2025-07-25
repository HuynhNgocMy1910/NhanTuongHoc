package com.my.kltn;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        // Nhận dữ liệu từ Intent
        String result = getIntent().getStringExtra("result");
        String date = getIntent().getStringExtra("date");
        int imageRes = getIntent().getIntExtra("imageRes", 0);

        // Hiển thị dữ liệu
        ImageView imageView = findViewById(R.id.detailImage);
        TextView resultText = findViewById(R.id.detailResult);
        TextView dateText = findViewById(R.id.detailDate);

        imageView.setImageResource(imageRes);
        resultText.setText(result);
        dateText.setText(date);

        // Xử lý nút quay lại
        Button btnBack = findViewById(R.id.btnDetailBack);
        btnBack.setOnClickListener(v -> finish());
    }
}