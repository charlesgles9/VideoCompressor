package com.vid.compress.storage;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

public class Disk {

    public static long SIZE_KB = 1024L;
    public static long SIZE_MB = SIZE_KB * SIZE_KB;
    public static long SIZE_GB = SIZE_MB * SIZE_KB;
    private static final DecimalFormat format = new DecimalFormat("#.##");


    public static File[]getDirs(Context context){
        File[] dirs = context.getExternalFilesDirs(null);
        for (int i = 0; i < dirs.length; i++) {
            File file = dirs[i];
            if (file != null)
                dirs[i] = new File(file.getPath().substring(0, file.getPath().indexOf("Android")));
        }
        return dirs;
    }

    public static File getStorage(Context context,String name){
        final File []files=getDirs(context);
             for(File file:files){
                 if(file.getName().equals(name)){
                     return file;
                 }
             }
             return null;
    }

    public static File getInternalCacheDir(Context context){
        return new File(context.getExternalFilesDirs(null)[0],"Compressed");
    }

    public static File createDefaultAppFolder(Context context){
        File parent=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return FileUtility.Companion.createFolder(parent,"ShrinkCompressor");
    }

    public static String getDefaultFolder(){
        return (new File(Environment.DIRECTORY_DCIM,"ShrinkCompressor")).getPath();
    }

    public static long totalMemory(File file){
        StatFs statFs= new StatFs(file.getAbsolutePath());
        return (statFs.getBlockCountLong()*statFs.getBlockSizeLong());
    }

    public static long freeMemory(File file){
        StatFs statFs=new StatFs(file.getAbsolutePath());
        return (statFs.getAvailableBlocksLong()*statFs.getBlockSizeLong());
    }

    public static long usedMemory(File file){
        long total=totalMemory(file);
        long free=freeMemory(file);

        return total-free;
    }

    public static boolean isSpaceEnough(File dir,long bytesToWrite){
        return freeMemory(dir) >= bytesToWrite;
    }

    public static String getSize(long bytes){
        if(bytes<SIZE_KB )
            return bytes+" bytes";
        if(bytes < SIZE_MB)
            return format.format(bytes/(float)SIZE_KB)+" Kb";
        if(bytes < SIZE_GB)
            return format.format((float)bytes/(float)SIZE_MB)+" Mb";
        return format.format((float)bytes/(float)SIZE_GB)+" Gb";

    }


}
