package com.my.kltn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private TextView tvUserNameValue, tvEmailValue;
    private Button btnEdit, btnLogout;
    private ImageView avatarImageView;
    private UserManager userManager;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvUserNameValue = findViewById(R.id.tvUserNameValue);
        tvEmailValue = findViewById(R.id.tvEmailValue);

        btnLogout = findViewById(R.id.btnLogout);
        avatarImageView = findViewById(R.id.avatarImageView);

        userManager = new UserManager(this);

        // Hiển thị thông tin
        String username = userManager.getUsername();
        String email = userManager.getEmail();

        tvUserNameValue.setText(username.isEmpty() ? "Chưa có" : username);
        tvEmailValue.setText(email.isEmpty() ? "Chưa có" : email);





        // Chức năng đăng xuất
        btnLogout.setOnClickListener(v -> {
            userManager.clear(); // Xóa thông tin đăng nhập
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            finish(); // Thoát màn này
            // startActivity(new Intent(this, LoginActivity.class));
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Load lại khi quay về
        String username = userManager.getUsername();
        String email = userManager.getEmail();
        tvUserNameValue.setText(username.isEmpty() ? "Chưa có" : username);
        tvEmailValue.setText(email.isEmpty() ? "Chưa có" : email);
    }

}
