package com.example.spikedash_singleplayer;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageUtils {

    public static String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static Bitmap convertStringToBitmap(String base64String) {
        byte[] data = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }
}