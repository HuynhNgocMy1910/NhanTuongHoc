package com.my.kltn;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditAccountActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail;
    private Button btnSave;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);

        userManager = new UserManager(this);

        // Gán dữ liệu hiện tại
        edtUsername.setText(userManager.getUsername());
        edtEmail.setText(userManager.getEmail());

        btnSave.setOnClickListener(v -> {
            String newUsername = edtUsername.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(newEmail)) {
                Toast.makeText(this, "Vui lòng không để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            userManager.setUsername(newUsername);
            userManager.setEmail(newEmail);

            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish(); // quay lại màn trước
        });
    }
}
