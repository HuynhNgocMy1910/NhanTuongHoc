package com.my.kltn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<AnalyzedImage> imageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Khởi tạo danh sách AnalyzedImage
        imageList = new ArrayList<>();

        // Lấy danh sách URI ảnh từ Intent
        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("imageUris");

        if (uriStrings != null && !uriStrings.isEmpty()) {
            Log.d("ReviewActivity", "Nhận được " + uriStrings.size() + " ảnh.");
            for (int i = 0; i < uriStrings.size(); i++) {
                String uriStr = uriStrings.get(i);
                Log.d("ReviewActivity", "URI[" + i + "] = " + uriStr);

                AnalyzedImage image = new AnalyzedImage();
                image.setImageUri(uriStr);
                image.setMessage(""); // hoặc có thể để trống
                image.setTimestamp(getCurrentTimestamp());

                imageList.add(image);
            }
        } else {
            Toast.makeText(this, "Không có ảnh để hiển thị", Toast.LENGTH_SHORT).show();
        }


        // Truyền danh sách vào adapter
        imageAdapter = new ImageAdapter(imageList, this);
        recyclerView.setAdapter(imageAdapter);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}
