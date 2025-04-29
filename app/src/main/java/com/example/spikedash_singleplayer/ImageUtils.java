package com.example.spikedash_singleplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    /**
     * Encodes a bitmap image to base64 string
     * @param bitmap The bitmap to encode
     * @return Base64 encoded string
     */
    public static String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compress the bitmap to reduce size
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * Decodes a base64 string to bitmap
     * @param base64String The base64 string to decode
     * @return Decoded bitmap
     */
    public static Bitmap decodeImage(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Compresses a base64 image string to reduce size
     * @param base64Image The base64 image to compress
     * @return Compressed base64 image string
     */
    public static String compressImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }
        
        try {
            // Decode to bitmap
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            
            // Resize bitmap if too large
            if (bitmap.getWidth() > 800 || bitmap.getHeight() > 800) {
                float aspectRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
                int width = 800;
                int height = Math.round(width / aspectRatio);
                
                if (height > 800) {
                    height = 800;
                    width = Math.round(height * aspectRatio);
                }
                
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            
            // Compress and re-encode
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] compressedBytes = baos.toByteArray();
            return Base64.encodeToString(compressedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return base64Image; // Return original on error
        }
    }
}