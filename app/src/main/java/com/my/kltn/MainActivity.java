package com.my.kltn;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity {

    private UserManager userManager;
    private Button btnSelectImage, btnTakePhoto;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Uri imageUri;
    private ImageView imagePreview;
    private ArrayList<Uri> imageUriList = new ArrayList<>();
    private static final int REQUEST_IMAGE_PICK = 100;
    TextView tvMessage;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Uri> cameraLauncherModern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (savedInstanceState != null) {
            ArrayList<String> uriStrings = savedInstanceState.getStringArrayList("imageUris");
            if (uriStrings != null) {
                for (String uri : uriStrings) {
                    imageUriList.add(Uri.parse(uri));
                }
            }
        }





        imagePreview = findViewById(R.id.imagePreview);
        userManager = new UserManager(this);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        tvMessage = findViewById(R.id.tvMessage);

        if (!userManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        btnSelectImage.setOnClickListener(v -> openGallery());

        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });

        TextView tvHome = findViewById(R.id.tvHome);
        tvHome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        TextView tvAccount = findViewById(R.id.tvAccount);
        tvAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        TextView tvReview = findViewById(R.id.tvReview);
        tvReview.setOnClickListener(v -> {
            if (imageUriList == null || imageUriList.isEmpty()) {
                Toast.makeText(this, "Chưa có ảnh nào để xem lại", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, ReviewActivity.class);
            ArrayList<String> uriStrings = new ArrayList<>();

            for (Uri uri : imageUriList) {
                uriStrings.add(uri.toString());
            }

            // ✅ CHỈ GỌI MỘT LẦN SAU KHI XONG VÒNG LẶP
            intent.putStringArrayListExtra("imageUris", uriStrings);
            startActivity(intent); // ✅ CHỈ START ACTIVITY MỘT LẦN
        });
        cameraLauncherModern = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        Toast.makeText(MainActivity.this, "Ảnh đã chụp", Toast.LENGTH_SHORT).show();
                        imagePreview.setImageURI(imageUri);
                        imageUriList.add(imageUri);
                        analyzeFaceFromUri(imageUri);
                    } else {
                        Toast.makeText(MainActivity.this, "Chụp ảnh thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // <-- dùng OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // <-- bắt buộc
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

        private void openCamera() {
        imageUri = createImageUri();
        if (imageUri != null) {
            cameraLauncherModern.launch(imageUri);
        } else {
            Toast.makeText(this, "Không thể tạo URI để lưu ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyApp");

        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (imageUri != null) {
                    imagePreview.setImageURI(imageUri);
                    imageUriList.add(imageUri);
                    analyzeFaceFromUri(imageUri);
                }
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
                imageUri = data.getData();

                try {
                    getContentResolver().takePersistableUriPermission(
                            imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                imagePreview.setImageURI(imageUri);
                imageUriList.add(imageUri);
                analyzeFaceFromUri(imageUri);
            }
        }
    }





    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri uri : imageUriList) {
            uriStrings.add(uri.toString());
        }
        outState.putStringArrayList("imageUris", uriStrings);
    }



    private void analyzeFaceFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "Không thể đọc dữ liệu ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                Toast.makeText(this, "Không thể chuyển ảnh thành bitmap", Toast.LENGTH_SHORT).show();
                return;
            }
            analyzeFace(bitmap); // Gọi phương thức phân tích khuôn mặt
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initFaceDetector() {
        // Khởi tạo tùy chọn cho FaceDetector
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build();
        // Khởi tạo FaceDetector
        FaceDetector detector = FaceDetection.getClient(options);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền Camera để sử dụng chức năng này",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void logout(View view) {
        userManager.setLogin(false);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private String analyzePersonality(List<Face> faces) {
        StringBuilder traits = new StringBuilder();
        for (Face face : faces) {
            // Phân tích dựa trên xác suất cười
            if (face.getSmilingProbability() != null && face.getSmilingProbability() > 0.5) {
                traits.append("Là người vui vẻ, thân thiện.\n");
            } else {
                traits.append("Có thể nghiêm túc hoặc đang không thoải mái.\n");
            }
            // Phân tích dựa trên xác suất mở mắt
            if (face.getLeftEyeOpenProbability() != null && face.getLeftEyeOpenProbability() > 0.5) {
                traits.append("Khuôn mặt thể hiện rõ sự Tự tin.\n");
            } else {
                traits.append("Có thể có sự thiếu tự tin.\n");
            }
            // Phân tích dựa trên các đặc trưng khuôn mặt
            analyzeFacialFeatures(face, traits);
        }
        return traits.toString();
    }

    private String formatPosition(PointF position) {
        return String.format("(%.2f, %.2f)", position.x, position.y);
    }

    private void analyzeFacialFeatures(Face face, StringBuilder traits) {
        List<FaceLandmark> landmarks = face.getAllLandmarks(); // ML Kit API mới
        if (landmarks != null) {
            for (FaceLandmark landmark : landmarks) {
                PointF position = landmark.getPosition(); // Lấy tọa độ 3D của điểm
                if (position != null) {
                    switch (landmark.getLandmarkType()) {
                        case FaceLandmark.LEFT_EYE:
                            traits.append("Mắt trái:")
                                    .append(formatPosition(position))
                                    .append("\n");
                            break;
                        case FaceLandmark.RIGHT_EYE:
                            traits.append("Mắt phải:")
                                    .append(formatPosition(position))
                                    .append("\n");
                            break;
                        case FaceLandmark.NOSE_BASE:
                            traits.append("Mũi:")
                                    .append(formatPosition(position))
                                    .append("\n");
                            break;
                        case FaceLandmark.MOUTH_LEFT:
                            traits.append("Khóe miệng trái:")
                                    .append(formatPosition(position))
                                    .append("\n");
                            break;
                        case FaceLandmark.MOUTH_RIGHT:
                            traits.append("Khóe miệng phải:")
                                    .append(formatPosition(position))
                                    .append("\n");
                            break;
                    }
                }
            }
        }
        // 1. Phân tích hình dạng khuôn mặt
        float width = face.getBoundingBox().width();
        float height = face.getBoundingBox().height();
        float ratio = width / height;

        if (ratio > 1.2) {
            traits.append("Khuôn mặt hình vuông (chữ điền) – Là người có sức sống mạnh mẽ, tính cách hoạt bát, làm việc có sự kiên trì đến cùng. Đây là người có tài vận là người có tướng hạnh phúc.\n");
        } else if (ratio < 0.8) {
            traits.append("Khuôn mặt dài (hình tam giác) – Là người có tính cách hướng nội, thiếu sức sống-sức khỏe. Cần phải vận động nhều để nâng cao tinh lực.\n");
        } else {
            traits.append("Khuôn mặt hình tròn – Luôn vui vẻ với đời nhưng dễ bị tình cảm chi phối trong công việc, do có tính cách ôn hòa được sự khen ngợi của bạn bè nên phù hợp với công việc mang lại niềm vui cho mọi người.\n");
        }

// 2. Trán
        PointF leftEye = getLandmarkPosition(face, FaceLandmark.LEFT_EYE);
        if (leftEye != null) {
            float foreheadHeight = leftEye.y - face.getBoundingBox().top;
            if (foreheadHeight > height * 0.35) {
                traits.append("Trán cao – Thông minh, tư duy chiến lược. ");
            } else {
                traits.append("Trán thấp – Thực tế, thiên về hành động. ");
            }
        }

// 3. Khoảng cách giữa hai mắt
        PointF leftEyeLeft = getLandmarkPosition(face, FaceLandmark.LEFT_EYE);
        PointF leftEyeTop = getLandmarkPosition(face, FaceLandmark.LEFT_EYE);
        PointF leftEyeBottom = getLandmarkPosition(face, FaceLandmark.LEFT_EYE);
        PointF rightEye = getLandmarkPosition(face, FaceLandmark.RIGHT_EYE);
        PointF rightEyeTop = getLandmarkPosition(face, FaceLandmark.RIGHT_EYE);
        PointF rightEyeBottom = getLandmarkPosition(face, FaceLandmark.RIGHT_EYE);
        if (leftEye == null) Log.d("DEBUG", "Không tìm thấy mắt trái.\n");
        PointF mouthLeft = null;
        PointF mouthRight = null;
        if (mouthLeft == null || mouthRight == null) Log.d("DEBUG", "Không tìm thấy miệng.\n");

        if (leftEye != null && rightEye != null) {
            float eyeDistance = Math.abs(rightEye.x - leftEye.x);
            if (eyeDistance > width * 0.5) {
                traits.append("Mắt xa nhau – Tầm nhìn xa, tư duy sáng tạo.\n");
            } else if (eyeDistance < width * 0.3) {
                traits.append("Mắt gần nhau – Tập trung, cẩn thận, kỹ tính.\n");
            } else {
                traits.append("Mắt cân đối – Tư duy hài hòa, sống ổn định.\n");
            }
        }

// 4. Miệng
        mouthLeft = getLandmarkPosition(face, FaceLandmark.MOUTH_LEFT);
        mouthRight = getLandmarkPosition(face, FaceLandmark.MOUTH_RIGHT);
        PointF mouthBottom = getLandmarkPosition(face, FaceLandmark.MOUTH_BOTTOM);
        PointF nose = getLandmarkPosition(face, FaceLandmark.NOSE_BASE);

// Ước lượng mouthTop vì ML Kit không có UPPER_LIP_TOP
        PointF mouthTop = null;
        if (nose != null) {
            mouthTop = new PointF(nose.x, nose.y + 20);  // Tùy chỉnh khoảng cách nếu cần
        }

        if (mouthLeft != null && mouthRight != null && mouthBottom != null && mouthTop != null) {
            float mouthWidth = Math.abs(mouthRight.x - mouthLeft.x);
            float mouthHeight = Math.abs(mouthBottom.y - mouthTop.y);
            float mouthAngle = calculateAngle(mouthLeft, mouthRight);
            float mouthAspectRatio = mouthHeight / mouthWidth;

            boolean isUpward = mouthAngle > 5;
            boolean isFlat = Math.abs(mouthAngle) <= 3;

            // Phân loại miệng
            if (mouthAspectRatio < 0.1 && isFlat) {
                traits.append("Miệng chữ Nhất – Môi mỏng, tính tình lạnh lùng, kín đáo.\n");
            } else if (mouthAspectRatio > 0.3 && isUpward) {
                traits.append("Miệng Phúc Chu – Môi dày, khóe miệng nhếch nhẹ, nhân hậu, nhiều phúc khí.\n");
            } else if (mouthAspectRatio >= 0.2 && isFlat && mouthWidth > width * 0.5) {
                traits.append("Miệng Vuông – Chính trực, cương nghị, giữ chữ tín.\n");
            } else if (isAngularShape(mouthTop, mouthBottom, mouthLeft, mouthRight)) {
                traits.append("Miệng Lăng Giác – Cá tính mạnh, cứng rắn, quyết đoán.\n");
            } else if (mouthWidth > width * 0.5 && isUpward && mouthAspectRatio < 0.2) {
                traits.append("Miệng Rồng – Mạnh mẽ, có tố chất lãnh đạo.\n");
            } else if (mouthAspectRatio > 0.25 && isUpward) {
                traits.append("Miệng Ngưỡng Nguyệt – Cười duyên, dễ mến, nhân hậu.\n");
            } else {
                traits.append("Miệng bình thường – Chưa có đặc điểm nổi bật.\n");
            }
        }



// 5. Mũi
        nose = getLandmarkPosition(face, FaceLandmark.NOSE_BASE);
        if (nose != null) {
            float noseHeight = nose.y - leftEye.y;
            if (noseHeight > height * 0.25) {
                traits.append("Mũi cao – Là người lương thiện, có tính cách ôn hòa, được mọi người xung quanh yêu mến. Vì thế, họ luôn chiếm ưu thế trong các mối quan hệ xung quanh, đường sự nghiệp hanh thông, cuộc sống ấm êm, hạnh phúc.\n");
            } else {
                traits.append("Mũi thấp – Những người này thường phải đối mặt với nhiều thử thách trong sự nghiệp. Họ có thể gặp khó khăn trong việc leo lên các vị trí cao hơn. Hơn nữa, người có dáng mũi thấp còn làm giảm sự tự tin và quyết đoán trong công việc.\n");
            }
        }

    }

    private boolean isAngularShape(PointF mouthTop, PointF mouthBottom, PointF mouthLeft, PointF mouthRight) {
        // Kiểm tra sự lệch bất thường giữa các khoảng cách cạnh môi
        float topToLeft = distance(mouthTop, mouthLeft);
        float topToRight = distance(mouthTop, mouthRight);
        float bottomToLeft = distance(mouthBottom, mouthLeft);
        float bottomToRight = distance(mouthBottom, mouthRight);

        float delta1 = Math.abs(topToLeft - bottomToLeft);
        float delta2 = Math.abs(topToRight - bottomToRight);

        // Nếu độ chênh lệch giữa các cạnh môi lớn → lăng giác
        return delta1 > 15 || delta2 > 15;
    }
    private float distance(PointF p1, PointF p2) {
        return (float) Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }

    //Tính góc nghiêng của miệng Nếu kết quả > 0 → miệng nhếch lên bên phải; < 0 → bên trái nhếch; gần 0 → miệng ngang.
    private float calculateAngle(PointF mouthLeft, PointF mouthRight) {
        float dx = mouthRight.x - mouthLeft.x;
        float dy = mouthRight.y - mouthLeft.y;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }


    private void analyzeFace(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();


        FaceDetector detector = FaceDetection.getClient(options);
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.size() == 0) {
                        tvMessage.setText("Không phát hiện khuôn mặt nào.\n");
                        return;
                    }

                    StringBuilder result = new StringBuilder();
                    for (Face face : faces) {
                        String personalityTraits = analyzePersonality(List.of(face)); // hoặc truyền face
                        result.append("Tính cách: ").append(personalityTraits).append("\n");
                    }
                    tvMessage.setText(result.toString());
                })
                .addOnFailureListener(e -> {
                    tvMessage.setText("Lỗi: " + e.getMessage());
                    Toast.makeText(this, "Phân tích thất bại", Toast.LENGTH_SHORT).show();
                });
    }
    private PointF getLandmarkPosition(Face face, int landmarkID) {
        FaceLandmark landmark = face.getLandmark(landmarkID);
        if (landmark != null) {
            return landmark.getPosition();
        }
        return null;
    }


}
