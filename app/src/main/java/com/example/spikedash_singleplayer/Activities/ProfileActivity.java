package com.example.spikedash_singleplayer.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spikedash_singleplayer.Managers.ImageUtils;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.User;
import com.example.spikedash_singleplayer.Managers.VibrationManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack;
    User user;
    String base64Pic;
    TextView tvEmail;
    EditText etUsername;
    ImageView imProfilePicture;
    Button btnConfirm;
    ImageButton btnEditPicture;
    LinearLayout btnChangePassword;
    ActivityResultLauncher<Intent> cameraLauncher, galleryLauncher;
    Dialog d;
    boolean hasUsernameChange;
    boolean hasImageChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnEditPicture = findViewById(R.id.btnEditPicture);
        btnEditPicture.setOnClickListener(this);
        hasUsernameChange = false;
        hasImageChange = false;
        user = getIntent().getParcelableExtra("user");
        imProfilePicture = findViewById(R.id.profilePicture);
        base64Pic = user.getBase64Image();
        uploadImage();

        etUsername = findViewById(R.id.editUsername);
        etUsername.setText(user.getUsername());
        tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(user.getEmail());
        btnConfirm = findViewById(R.id.btnConfirmChanges);
        btnConfirm.setOnClickListener(this);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        initializeCameraAndGallery();


    }
    private void uploadImage(){
        if (user != null && user.getBase64Image() != null && !user.getBase64Image().isEmpty()) {
            try {
                Bitmap profileBitmap = ImageUtils.decodeImage(user.getBase64Image());
                if (profileBitmap != null) {
                    imProfilePicture.setImageBitmap(profileBitmap);
                } else {
                    // Fallback to default if conversion failed
                    imProfilePicture.setImageResource(R.drawable.ic_profile);
                }
            } catch (Exception e) {
                // Handle any exceptions and use default image
                imProfilePicture.setImageResource(R.drawable.ic_profile);
                Log.e("ProfileActivity", "Error loading profile picture: " + e.getMessage());
            }
        } else {
            // Use default image if no base64 image is available
            imProfilePicture.setImageResource(R.drawable.ic_profile);
        }
    }
    private void  initializeCameraAndGallery() {
        // Initialize the camera and gallery launchers
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bitmap bitmap = null;
                Intent intent = result.getData();
                // Check if the image is in the extras
                if (intent.getExtras() != null) {
                    bitmap = (Bitmap) intent.getExtras().get("data");
                }
                // If not, try to get it from the URI
                if (bitmap == null && intent.getData() != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // If we have a bitmap, convert it to base64 and set it to the ImageView
                if (bitmap != null) {
                    base64Pic = ImageUtils.encodeImage(bitmap);
                    // Show preview
                    imProfilePicture.setImageBitmap(bitmap);
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                // Get the URI of the selected image
                Uri imageUri = result.getData().getData(); // get Uri

                if (imageUri != null) {
                    try {
                        // Convert URI to Bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                        // Convert to base64
                        base64Pic = ImageUtils.encodeImage(bitmap);

                        // Show preview
                        imProfilePicture.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        SoundManager.play("click");

        if(v == btnBack) {
            setResult(RESULT_OK);
            finish();
        }

        if (v == btnEditPicture) {
            // Show dialog to choose between camera and gallery
            d = new Dialog(this);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.setContentView(R.layout.image_dialog);
            LinearLayout btnGallery = d.findViewById(R.id.btnGallery);
            LinearLayout btnCamera = d.findViewById(R.id.btnCamera);
            ImageButton btnClose = d.findViewById(R.id.btnClose);

            // Set click listeners for the buttons
            btnCamera.setOnClickListener(view -> {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
                d.dismiss();
            });
            // Open gallery to select an image
            btnGallery.setOnClickListener(view -> {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
                d.dismiss();
            });
            // Close button to dismiss the dialog
            btnClose.setOnClickListener(view -> d.dismiss());

            d.show();
        }
        if (v == btnChangePassword) {
            // Open ForgotActivity to change password
            Intent intent = new Intent(ProfileActivity.this, ForgotActivity.class);
            startActivity(intent);
        }
        if (v == btnConfirm) {
            //trigger profile update
            String newUsername = etUsername.getText().toString().trim();

            // Check if the username is empty
            if (newUsername.isEmpty()) {
                SoundManager.play("error");
                Toast.makeText(ProfileActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the username is the same as the current one
            hasUsernameChange = !newUsername.equals(user.getUsername());
            // Check if the image has changed
            hasImageChange = base64Pic != null && !base64Pic.equals(user.getBase64Image());

            // If no changes were made, show a message and return
            if (!hasUsernameChange && !hasImageChange) {
                Toast.makeText(ProfileActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show a progress dialog while saving changes
            Dialog progressDialog = new Dialog(this);
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
            if (tvMessage != null) {
                tvMessage.setText("Saving changes...");
            }
            progressDialog.show();

            // Reference to user in database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid());

            // Create a map for batch updates
            java.util.Map<String, Object> updates = new java.util.HashMap<>();

            // Add username to updates if changed
            if (hasUsernameChange) {
                updates.put("username", newUsername);
            }
            // Add profile image to updates if changed
            if (hasImageChange) {
                updates.put("base64Image", base64Pic);
            }

            // Update the database with all changes at once
            userRef.updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Update the user object with new values
                            if (hasUsernameChange) {
                                user.setUsername(newUsername);
                            }
                            if (hasImageChange) {
                                user.setBase64Image(base64Pic);
                            }

                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            SoundManager.play("win");
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("user", user);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            SoundManager.play("error");
                            Toast.makeText(ProfileActivity.this,
                                    "Failed to update profile: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}