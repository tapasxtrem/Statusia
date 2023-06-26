package com.example.statusia.classes;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopyObserver extends ContentObserver {
    private Context context;
    private Uri sourceUri;
    private String destinationFolderPath;

    public FileCopyObserver(Context context, Uri sourceUri, String destinationFolderPath) {
        super(new Handler());
        this.context = context;
        this.sourceUri = sourceUri;
        this.destinationFolderPath = destinationFolderPath;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.e(TAG, "onChange:");
        if (uri.toString().startsWith(sourceUri.toString())) {
            // A new file is created in the source folder
            String sourceFilePath = getPathFromUri(context, uri);
            Log.e(TAG, "onChange: uri"+uri);
            Log.e(TAG, "onChange: sourceFilePath: "+sourceFilePath);
            String destinationFilePath = destinationFolderPath + "/" + getFileNameFromUri(context, uri);
            copyFile(sourceFilePath, destinationFilePath);
        }
    }

    private String getPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        String path = null;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    @SuppressLint("Range")
    private String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            cursor.close();
        }
        return fileName;
    }

//    private void copyFile(String sourceFilePath, String destinationFilePath) {
//        // Implement your file copy logic here
//        // You can use FileInputStream and FileOutputStream or any other method of your choice
//    }

    private void copyFile(String sourceFilePath, String destinationFilePath) {
        try {
            File sourceFile = new File(sourceFilePath);
            File destinationFile = new File(destinationFilePath);

            FileInputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

