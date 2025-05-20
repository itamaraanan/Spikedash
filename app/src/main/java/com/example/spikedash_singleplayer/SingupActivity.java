package com.example.spikedash_singleplayer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class SingupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText etEmail, etPassword, etConfirmPassword, etUsername;
    LinearLayout btnSingup;
    ActivityResultLauncher<Intent> cameraLauncher, galleryLauncher;
    String base64Pic = null;
    ImageButton btnBack, btnShowPassword, btnShowConfirmPassword, btnAddImage;
    ImageView ivProfilePicture;

    Dialog d;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnAddImage = findViewById(R.id.btnAddPicture);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        btnShowConfirmPassword = findViewById(R.id.btnShowConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etUsername = findViewById(R.id.etUsername);
        btnSingup = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);
        ivProfilePicture = findViewById(R.id.profilePicture);

        btnSingup.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnShowPassword.setOnClickListener(this);
        btnShowConfirmPassword.setOnClickListener(this);
        btnAddImage.setOnClickListener(this);

        initializeCameraAndGallery();
    }

    private void  initializeCameraAndGallery() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bitmap bitmap = null;
                Intent intent = result.getData();

                if (intent.getExtras() != null) {
                    bitmap = (Bitmap) intent.getExtras().get("data");
                }

                if (bitmap == null && intent.getData() != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    base64Pic = ImageUtils.encodeImage(bitmap);
                    Log.d("img base64", "Image encoded successfully");

                    ivProfilePicture.setImageBitmap(bitmap);
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData(); // get Uri

                if (imageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                        // Convert to base64
                        base64Pic = ImageUtils.encodeImage(bitmap);
                        Log.d("img base64", "Image encoded successfully");

                        // Show preview
                        ivProfilePicture.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void createAccount(String email, String password, String username) {
        // Show progress dialog
        showProgressDialog("Creating account...");

        DatabaseReference usernamesRef = FirebaseDatabase.getInstance().getReference("usernames");

        usernamesRef.child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    hideProgressDialog();
                    Toast.makeText(this, "Username already taken. Try another one.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    writeNewUser(user.getUid(), username, email);
                                } else {
                                    hideProgressDialog();
                                    Toast.makeText(this, "Signup failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                hideProgressDialog();
                Toast.makeText(this, "Error checking username.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeNewUser(String userId, String username, String email) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        User user = new User(username, email, userId, userRef.getKey());

        // Set profile image if available
        if (base64Pic != null) {
            if (base64Pic.length() > 500_000) {
                Toast.makeText(this, "Image is too large. Using default profile picture.", Toast.LENGTH_SHORT).show();
            } else {
                user.setBase64Image(base64Pic);
            }
        }

        // Save user data
        userRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // ✅ Set default owned and equipped background
                userRef.child("ownedBackgrounds").child("default_background").setValue(true);
                userRef.child("equippedBackground").setValue("default_background");

                // ✅ Set default owned and equipped skin
                userRef.child("ownedSkins").child("default_skin").setValue(true);
                userRef.child("equippedSkin").setValue("default_skin");

                userRef.child("settings").child("sound").setValue(0.5);
                userRef.child("settings").child("bgm").setValue(0.5);
                userRef.child("settings").child("vibration").setValue(true);

                userRef.child("difficultyMultiplier").setValue(1.0f);

                userRef.child("friendRequests").setValue(null);
                // ✅ Register the username
                DatabaseReference usernamesRef = FirebaseDatabase.getInstance().getReference("usernames");
                usernamesRef.child(username).setValue(userId).addOnCompleteListener(usernameTask -> {
                    hideProgressDialog();

                    if (usernameTask.isSuccessful()) {
                        Toast.makeText(SingupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SingupActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SingupActivity.this, "Failed to register username.", Toast.LENGTH_SHORT).show();
                        FirebaseUser u = mAuth.getCurrentUser();
                        if (u != null) u.delete();
                    }
                });
            } else {
                hideProgressDialog();
                Toast.makeText(SingupActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                FirebaseUser u = mAuth.getCurrentUser();
                if (u != null) u.delete();
            }
        });
    }


    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        if (tvMessage != null && message != null) {
            tvMessage.setText(message);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSingup) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String username = etUsername.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            createAccount(email, password, username);
        } else if (v == btnBack) {
            Intent intent = new Intent(SingupActivity.this, MenuActivity.class);
            startActivity(intent);
        } else if (v == btnShowPassword) {
            if (etPassword.getTransformationMethod() == null) {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                btnShowPassword.setImageResource(R.drawable.ic_closed_eye_24);
            } else {
                etPassword.setTransformationMethod(null);
                btnShowPassword.setImageResource(R.drawable.ic_opened_eye_24);
            }
        } else if (v == btnShowConfirmPassword) {
            if (etConfirmPassword.getTransformationMethod() == null) {
                etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                btnShowConfirmPassword.setImageResource(R.drawable.ic_closed_eye_24);
            } else {
                etConfirmPassword.setTransformationMethod(null);
                btnShowConfirmPassword.setImageResource(R.drawable.ic_opened_eye_24);
            }
        } else if (v == btnAddImage) {
            d = new Dialog(this);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.setContentView(R.layout.image_dialog);
            LinearLayout btnGallery = d.findViewById(R.id.btnGallery);
            LinearLayout btnCamera = d.findViewById(R.id.btnCamera);
            ImageButton btnClose = d.findViewById(R.id.btnClose);

            btnCamera.setOnClickListener(view -> {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
                d.dismiss();
            });

            btnGallery.setOnClickListener(view -> {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
                d.dismiss();
            });

            btnClose.setOnClickListener(view -> d.dismiss());

            d.show();
        }
    }
}