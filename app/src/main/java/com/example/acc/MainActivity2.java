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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private ImageView capturedImage;
    private String currentPhotoPath;
    private Button openCameraButton;
    private TextView dateEdt;
    private EditText regpassword;
    private EditText conpassword;
    private Button regbutton;
    private EditText reguser;
    private EditText fname;
    private EditText lname;
    private EditText email;
    private EditText address;
    private EditText contactNumber;
    private EditText otherGenderEditText;
    private RadioGroup gender;
    private CheckBox hb1;
    private CheckBox hb2;
    private CheckBox hb3;
    private CheckBox hb4;
    private CheckBox hb5;
    private CheckBox hb6;
    private CheckBox hb7;
    private CheckBox hb8;
    private CheckBox hb9;
    private CheckBox hb10;
    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        try {
            initializeViews();
            setupDatePicker();
            setupSpinners();
            others();

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
            lname = findViewById(R.id.lname);
            email = findViewById(R.id.email);
            address = findViewById(R.id.address);
            contactNumber = findViewById(R.id.contactNumber);
            gender = findViewById(R.id.gender);
            otherGenderEditText = findViewById(R.id.otherGenderEditText);

            spinner1 = findViewById(R.id.squestions);
            spinner2 = findViewById(R.id.squestions2);
            spinner3 = findViewById(R.id.squestions3);

            //checkboxes
            hb1 = findViewById(R.id.hb1);
            hb2 = findViewById(R.id.hb2);
            hb3 = findViewById(R.id.hb3);
            hb4 = findViewById(R.id.hb4);
            hb5 = findViewById(R.id.hb5);
            hb6 = findViewById(R.id.hb6);
            hb7 = findViewById(R.id.hb7);
            hb8 = findViewById(R.id.hb8);
            hb9 = findViewById(R.id.hb9);
            hb10 = findViewById(R.id.hb10);

            capturedImage = findViewById(R.id.capturedImage);
            openCameraButton = findViewById(R.id.openCameraButton);
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

    private void setupSpinners() {
        spinner1 = findViewById(R.id.squestions);
        spinner2 = findViewById(R.id.squestions2);
        spinner3 = findViewById(R.id.squestions3);

        //questions
        String[] questionsWithDefault = new String[]{"Select a question", "First love?",
                "First kiss?", "Messi or Ronaldo?", "Favorite food?", "Mother Maiden's Name?"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, questionsWithDefault);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Track if initial setup is complete
        final boolean[] isInitialSetup = {true};

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Skip validation during initial setup
                if (isInitialSetup[0]) {
                    return;
                }

                // Skip validation if "Select a question" is chosen
                String selected = parent.getItemAtPosition(pos).toString();
                if (selected.equals("Select a question")) {
                    return;
                }

                // Check for duplicates
                if (hasDuplicateSelections(spinner1, spinner2, spinner3)) {
                    showAlert("Select only one");
                    // Reset the current spinner to default
                    ((Spinner) parent).setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinner1.setOnItemSelectedListener(listener);
        spinner2.setOnItemSelectedListener(listener);
        spinner3.setOnItemSelectedListener(listener);

        //default option
        spinner1.setSelection(0);
        spinner2.setSelection(0);
        spinner3.setSelection(0);

        isInitialSetup[0] = false;
    }

    private boolean hasDuplicateSelections(Spinner... spinners) {
        for (int i = 0; i < spinners.length; i++) {
            String current = spinners[i].getSelectedItem().toString();
            if (current.equals("Select a question")) continue;

            for (int j = i + 1; j < spinners.length; j++) {
                String other = spinners[j].getSelectedItem().toString();
                if (current.equals(other)) {
                    return true;
                }
            }
        }
        return false;
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

        /*  if (!validatePhoto()) {
            showAlert("Please take a photo before registering");
            return;
        }*/

        saveUserCredentials();
        display();
    }

    private boolean validatePhoto() {
        return currentPhotoPath != null && !currentPhotoPath.isEmpty();
    }

    private boolean areFieldsFilled() {
        /*return !isEmpty(reguser) &&
                !isEmpty(regpassword) &&
                !isEmpty(conpassword) &&
                !isEmpty(fname) &&
                !isEmpty(lname) &&
                !isEmpty(email) &&
                !isEmpty(address) &&
                !isEmpty(contactNumber) &&
                !isTextViewEmpty(dateEdt) && // Ensure the birthday field is not empty
                gender.getCheckedRadioButtonId() != -1 &&
                isAnyCheckboxChecked() &&
                areSpinnersSelected();*/
        return true;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private boolean isTextViewEmpty(TextView textView) {
        return textView.getText().toString().trim().isEmpty();
    }

    private boolean isAnyCheckboxChecked() {
        return hb1.isChecked() || hb2.isChecked() || hb3.isChecked() ||
                hb4.isChecked() || hb5.isChecked() || hb6.isChecked() ||
                hb7.isChecked() || hb8.isChecked() || hb9.isChecked() ||
                hb10.isChecked();
    }

    private boolean areSpinnersSelected() {
        return !spinner1.getSelectedItem().toString().equals("Select a question") &&
                !spinner2.getSelectedItem().toString().equals("Select a question") &&
                !spinner3.getSelectedItem().toString().equals("Select a question");
    }

    private void display() {
        StringBuilder message = new StringBuilder();

        // Personal Details
        message.append("Username: ").append(reguser.getText().toString()).append("\n\n");
        message.append("Name: ").append(fname.getText().toString())
                .append(" ").append(lname.getText().toString()).append("\n");
        message.append("Email: ").append(email.getText().toString()).append("\n");
        message.append("Address: ").append(address.getText().toString()).append("\n");
        message.append("Contact: ").append(contactNumber.getText().toString()).append("\n");
        message.append("Birthday: ").append(dateEdt.getText().toString()).append("\n\n");

        // Gender
        int selectedId = gender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButton3) {
            String otherGender = otherGenderEditText.getText().toString();
            message.append("Gender: ").append(otherGender).append("\n\n");
        } else {
            message.append("Gender: ")
                    .append(((RadioButton) findViewById(selectedId)).getText().toString())
                    .append("\n\n");
        }

        // Hobbies
        message.append("Hobbies:\n");
        CheckBox[] hobbies = {hb1, hb2, hb3, hb4, hb5, hb6, hb7, hb8, hb9, hb10};
        boolean hasHobbies = false;
        for (CheckBox hobby : hobbies) {
            if (hobby.isChecked()) {
                message.append("- ").append(hobby.getText().toString()).append("\n");
                hasHobbies = true;
            }
        }
        if (!hasHobbies) {
            message.append("No hobbies selected\n");
        }
        message.append("\n");

        //spinner
        message.append("Security Questions:\n");
        message.append("1. ").append(spinner1.getSelectedItem().toString()).append("\n");
        message.append("2. ").append(spinner2.getSelectedItem().toString()).append("\n");
        message.append("3. ").append(spinner3.getSelectedItem().toString()).append("\n");

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
        Intent intent = new Intent(MainActivity2.this, MainActivity.class); // Assuming MainActivity is the menu activity
        startActivity(intent);
        finish(); // Close the current activity
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            capturedImage.setImageBitmap(bitmap);
            capturedImage.setVisibility(View.VISIBLE);
        }
    }

    private void others() {
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton3) {
                    otherGenderEditText.setVisibility(View.VISIBLE);
                } else {
                    otherGenderEditText.setVisibility(View.GONE);
                    otherGenderEditText.getText().clear();
                }
            }
        });
    }

    private void saveUserCredentials() {
        // Create and save user object
        User user = new User();
        user.setUserId(reguser.getText().toString()); // Using username as userId
        user.setName(fname.getText().toString() + " " + lname.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPhone(contactNumber.getText().toString());
        
        // Save to User's SharedPreferences
        user.saveToLocalStorage(this);

        // Keep existing SharedPreferences for backward compatibility
        SharedPreferences sharedPreferences = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", reguser.getText().toString());
        editor.putString("password", regpassword.getText().toString());
        editor.putString("firstName", fname.getText().toString());
        editor.putString("lastName", lname.getText().toString());
        editor.putString("photoPath", currentPhotoPath);
        editor.apply();

        // Log the saved credentials for debugging
        Log.d("MainActivity2", "Saved User Object: " + user.getName());
        Log.d("MainActivity2", "Saved username: " + reguser.getText().toString());
        Log.d("MainActivity2", "Saved password: " + regpassword.getText().toString());
        Log.d("MainActivity2", "Saved firstName: " + fname.getText().toString());
        Log.d("MainActivity2", "Saved lastName: " + lname.getText().toString());
        Log.d("MainActivity2", "Saved photoPath: " + currentPhotoPath);
    }
}
