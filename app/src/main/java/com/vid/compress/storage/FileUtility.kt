package com.vid.compress.storage

import android.content.Context
import android.database.MergeCursor
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.util.Log
import java.io.File

class FileUtility {

    companion object{
      const val CAT="LogFile"
        // arranges all the files folder wise
      fun fetchVideos(context: Context):HashMap<String,MutableList<File>>{
          val map=HashMap<String,MutableList<File>>()
          val columns= arrayOf(MediaStore.Video.Media.DATA)
          val cursor= MergeCursor(arrayOf(
              context.contentResolver.query(
                  MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                  columns, null, null,MediaStore.MediaColumns.DATE_ADDED),
              context.contentResolver.query(
                  MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                  columns, null, null,MediaStore.MediaColumns.DATE_ADDED)))
          cursor.moveToFirst()
          while (!cursor.isAfterLast) {
              val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
              val file=File(path)
              val np= map[file.parent!!]
              if(np!=null)
                  np.add(file)
              else
                  map[file.parent!!] =mutableListOf(file)
              cursor.moveToNext()
          }
          return map
      }

     fun fileToUri(context: Context,file: File,
         scanListener:MediaScannerConnection.OnScanCompletedListener){
         MediaScannerConnection.scanFile(context, arrayOf(file.path),null,scanListener)
     }

    }
}