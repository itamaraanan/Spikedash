package com.example.spikedash_singleplayer.Managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    private static final int MAX_IMAGE_DIMENSION = 800;
    private static final int COMPRESSION_QUALITY = 50;
    private static final int MAX_BASE64_SIZE = 500000;

    /**
     * Encodes a bitmap image to base64 string with proper compression
     * @param bitmap The bitmap to encode
     * @return Base64 encoded string
     */
    public static String encodeImage(Bitmap bitmap) {
        // First resize the bitmap if it's too large
        bitmap = resizeBitmapIfNeeded(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Start with a higher quality
        int quality = COMPRESSION_QUALITY;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        // If the resulting base64 would be too large, compress further
        byte[] imageBytes = baos.toByteArray();
        int base64Size = Base64.encodeToString(imageBytes, Base64.DEFAULT).length();

        // Compress more if needed
        while (base64Size > MAX_BASE64_SIZE && quality > 10) {
            baos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            imageBytes = baos.toByteArray();
            base64Size = Base64.encodeToString(imageBytes, Base64.DEFAULT).length();
            Log.d("ImageUtils", "Reduced quality to " + quality + ", base64 size: " + base64Size);
        }

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * Resizes a bitmap to appropriate dimensions if needed
     * @param bitmap The bitmap to resize
     * @return Resized bitmap
     */
    private static Bitmap resizeBitmapIfNeeded(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // If bitmap is already small enough, return it unchanged
        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return bitmap;
        }

        // Calculate new dimensions while maintaining aspect ratio
        float aspectRatio = (float) width / (float) height;
        int newWidth, newHeight;

        if (width > height) {
            newWidth = MAX_IMAGE_DIMENSION;
            newHeight = Math.round(newWidth / aspectRatio);
        } else {
            newHeight = MAX_IMAGE_DIMENSION;
            newWidth = Math.round(newHeight * aspectRatio);
        }

        Log.d("ImageUtils", "Resizing image from " + width + "x" + height +
                " to " + newWidth + "x" + newHeight);

        // Create and return the resized bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
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
            Log.e("ImageUtils", "Error decoding image: " + e.getMessage());
            return null;
        }
    }
}