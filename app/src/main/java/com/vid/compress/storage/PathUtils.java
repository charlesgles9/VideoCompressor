package com.vid.compress.storage;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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