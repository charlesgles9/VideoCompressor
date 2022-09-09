package com.vid.compress.storage

import android.content.Context
import android.database.MergeCursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import java.io.*

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


     fun getFileName(path:String):String{
         return path.substring(
             path.lastIndexOf("/")+1)
     }

     fun getExtension(path:String):String{
         val last=path.lastIndexOf(".")
         return if(last>=0) path.substring(last) else ""
     }

     fun getMimeType(file:File): String{
         val ext= getExtension(file.path)
         return if(ext.isNotEmpty())
             MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.substring(1))?:"application/octet-stream"
         else "application/octet-stream"
     }

     fun saveFileFromUri(context: Context,uri:Uri,destination:String){
         var stream:InputStream?=null
         var bos:BufferedOutputStream?=null
         try {
             stream=context.contentResolver.openInputStream(uri)
             bos= BufferedOutputStream(FileOutputStream(destination,false))
             val buffer= ByteArray(1024, init ={0})
             stream?.read(buffer)
             do {
                 bos.write(buffer)
             }while (stream?.read(buffer)!=-1)

         }catch (e:Exception){
             e.printStackTrace()
         }finally {
             try {
                 stream?.close()
                 bos?.close()
             }catch (io:IOException){
                 io.printStackTrace()
             }
         }
     }

     fun createFolder(context:Context,parent:Uri,name:String):Boolean{
         return DocumentFile.fromTreeUri(context,parent)
                ?.createDirectory(name)?.exists()?:false
     }

     fun createFile(context: Context,parent: Uri,name: String):Boolean{
         return DocumentFile.fromTreeUri(context,parent)
               ?.createFile("",name)?.exists()?:false
     }



    }
}