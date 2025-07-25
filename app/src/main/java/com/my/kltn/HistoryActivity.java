package com.my.kltn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo dữ liệu mẫu (sau này sẽ thay bằng dữ liệu từ database)
        loadHistoryData();

        // Thiết lập adapter
        historyAdapter = new HistoryAdapter(this, historyItems);
        recyclerView.setAdapter(historyAdapter);

        // Xử lý sự kiện nút quay lại
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadHistoryData() {
        // TODO: Thay bằng dữ liệu thực từ database
        historyItems.add(new HistoryItem("Kết quả: Hướng ngoại", "12/05/2024", R.drawable.login));
        historyItems.add(new HistoryItem("Kết quả: Hướng nội", "11/05/2024", R.drawable.login));
    }
}