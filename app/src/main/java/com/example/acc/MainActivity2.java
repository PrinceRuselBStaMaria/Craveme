package com.example.acc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {
    private ImageView profileImage;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_PICK_IMAGE = 2;
    private ImageView capturedImage;
    private String currentPhotoPath;
    private Button openCameraButton;
    private Button filePickerButton;
    private TextView dateEdt;
    private EditText regpassword;
    private EditText conpassword;
    private Button regbutton;
    private EditText reguser;
    private EditText fname;
    private EditText email;
    private EditText otherGenderEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        profileImage = findViewById(R.id.profileImage);
        capturedImage = findViewById(R.id.capturedImage);

        try {
            initializeViews();
            setupDatePicker();

            openCameraButton.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    openCamera();
                }
            });

            regbutton.setOnClickListener(view -> {
                if (!isFinishing()) {
                    validateForm();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        try {
            dateEdt = findViewById(R.id.bday);
            regpassword = findViewById(R.id.regpassword);
            conpassword = findViewById(R.id.conpass);
            regbutton = findViewById(R.id.regbutton);
            reguser = findViewById(R.id.reguser);
            fname = findViewById(R.id.fname);
            email = findViewById(R.id.email);
            capturedImage = findViewById(R.id.capturedImage);
            openCameraButton = findViewById(R.id.openCameraButton);
            filePickerButton = findViewById(R.id.file);

            filePickerButton.setOnClickListener(v -> openFilePicker());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupDatePicker() {
        dateEdt.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity2.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1;
                        dateEdt.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }



    private Boolean validatePasswords() {
        String password = regpassword.getText().toString();
        String confirmPassword = conpassword.getText().toString();

        if (!password.equals(confirmPassword)) {
            return false;
        } else {
            return true;
        }
    }

    private void showPasswordMismatchDialog() {
        showAlert("Passwords do not match!");
    }

    private void showAlert(String message) {
        if (!isFinishing()) {
            try {
                new AlertDialog.Builder(MainActivity2.this)
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void validateForm() {
        if (!validatePasswords()) {
            showPasswordMismatchDialog();
            return;
        }

        if (!areFieldsFilled()) {
            showAlert("Please fill in all required fields");
            return;
        }

        saveUserCredentials();
        display();
    }

    private boolean validatePhoto() {
        return currentPhotoPath != null && !currentPhotoPath.isEmpty();
    }

    private boolean areFieldsFilled() {
        return !isEmpty(reguser) &&
                !isEmpty(regpassword) &&
                !isEmpty(conpassword) &&
                !isEmpty(fname) &&
                !isEmpty(email);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }


    private void display() {
        StringBuilder message = new StringBuilder();
        message.append("Username: ").append(reguser.getText().toString()).append("\n\n");
        message.append("Name: ").append(fname.getText().toString()).append("\n");
        message.append("Email: ").append(email.getText().toString()).append("\n");
        message.append("Birthday: ").append(dateEdt.getText().toString()).append("\n\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account Details")
                .setMessage(message.toString())
                .setPositiveButton("Proceed", (dialog, which) -> {
                    dialog.dismiss();
                    navigateToMenu();
                })
                .setNegativeButton("Edit", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void navigateToMenu() {
        // Update to pass name to MainActivity3
        Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
        intent.putExtra("fullName", fname.getText().toString());
        startActivity(intent);
        finish();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.acc.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                capturedImage.setImageBitmap(bitmap);
                capturedImage.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                try {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    capturedImage.setImageBitmap(bitmap);
                    capturedImage.setVisibility(View.VISIBLE);
                    currentPhotoPath = getRealPathFromURI(selectedImageUri);
                    handleImageSelection(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void saveUserCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save user details
        editor.putString("username", reguser.getText().toString());
        editor.putString("password", regpassword.getText().toString());
        editor.putString("firstName", fname.getText().toString());
        editor.putString("email", email.getText().toString());

        // Save image path with logging
        if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
            editor.putString("profileImage", currentPhotoPath);
            Log.d("MainActivity2", "Saving image path: " + currentPhotoPath);
        }

        editor.apply();
    }

    private void handleImageSelection(Uri selectedImageUri) {
        try {
            // Create directory in app's private storage
            File imageDir = new File(getFilesDir(), "profile_images");
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            // Create new file with unique name
            String timestamp = String.valueOf(System.currentTimeMillis());
            File imageFile = new File(imageDir, "profile_" + timestamp + ".jpg");

            // Copy image to app's private storage
            try (InputStream in = getContentResolver().openInputStream(selectedImageUri);
                 OutputStream out = new FileOutputStream(imageFile)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }

            // Save path to SharedPreferences
            currentPhotoPath = imageFile.getAbsolutePath();
            SharedPreferences.Editor editor = getSharedPreferences("UserCredentials", MODE_PRIVATE).edit();
            editor.putString("profileImage", currentPhotoPath);
            editor.apply();

            // Update UI with new image
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap != null) {
                capturedImage.setImageBitmap(bitmap);
                capturedImage.setVisibility(View.VISIBLE);
                Log.d("MainActivity2", "Image saved and displayed: " + currentPhotoPath);
            }

        } catch (IOException e) {
            Log.e("MainActivity2", "Error handling image: " + e.getMessage());
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
}
