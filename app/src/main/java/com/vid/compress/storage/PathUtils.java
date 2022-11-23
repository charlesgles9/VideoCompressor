package com.vid.compress.storage;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PathUtils {


    public static String mediaUriToFilePath(Context context,Uri uri){
        Cursor cursor=null;
        try{
            String[]projection={MediaStore.Video.Media.DATA};
            cursor=context.getContentResolver().query(uri,projection,null,null,null);
            int index=cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(index);
        }finally {
            if(cursor!=null)
                cursor.close();
        }
    }

}