package com.example.spikedash_singleplayer;

import static android.opengl.ETC1.decodeImage;

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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

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
        hasUsernameChange = true;
        hasImageChange = true;
        user = getIntent().getParcelableExtra("user");
        imProfilePicture = findViewById(R.id.profilePicture);
        uploadImage();

        etUsername = findViewById(R.id.editUsername);
        etUsername.setText(user.getUsername());
        tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(user.email);
        btnConfirm = findViewById(R.id.btnConfirmChanges);
        btnConfirm.setOnClickListener(this);
        initializeCameraAndGallery();


    }
    private void uploadImage(){
        if (user != null && user.getBase64Image() != null && !user.getBase64Image().isEmpty()) {
            try {
                // Convert base64 string to bitmap using ImageUtils
                Bitmap profileBitmap = ImageUtils.decodeImage(user.getBase64Image());
                if (profileBitmap != null) {
                    // Set the bitmap to the ImageView
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

                    imProfilePicture.setImageBitmap(bitmap);
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
        if(v == btnBack) {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }

        if (v == btnEditPicture) {
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
        if (v == btnConfirm) {

            String newUsername = etUsername.getText().toString().trim();


            if (newUsername.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newUsername.equals(user.username)) {
                hasUsernameChange = true;
            }

            if (base64Pic != null) {
                hasImageChange = true;
            }

            if (!hasUsernameChange && !hasImageChange) {
                Toast.makeText(ProfileActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
                return;
            }

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
                                user.username = newUsername;
                            }
                            if (hasImageChange) {
                                user.setBase64Image(base64Pic);
                            }

                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Failed to update profile: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}