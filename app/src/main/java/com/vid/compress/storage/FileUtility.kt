package com.vid.compress.storage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.MergeCursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileUtility {

    companion object{
      const val CAT="LogFile"

      val videoFilter= FilenameFilter { dir, name ->
          return@FilenameFilter getExtension(name).lowercase(Locale.ROOT) == ".mp4"||
                     getExtension(name).lowercase(Locale.ROOT) == ".mkv"||
                     getExtension(name).lowercase(Locale.ROOT) == ".3gp"
      }



      fun fetchVideos(folder:File):HashMap<String,MutableList<File>>{
          val map=HashMap<String,MutableList<File>>()
          val files=folder.list()
          val list= mutableListOf<File>()
          files?.forEach { file ->
              list.add(File(folder,file))
          }
          map[folder.path] = list
          return map
      }
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
              if(file.exists() and (file.length()!=0L)) {
                  val np = map[file.parent!!]
                  if (np != null)
                      np.add(file)
                  else
                      map[file.parent!!] = mutableListOf(file)
              }
              cursor.moveToNext()
          }
          return map
      }

     fun getUriFromSharedPreference(file:File,context: Context):Uri?{
         val permissions=context.contentResolver.persistedUriPermissions
         permissions.forEach{
              permission->
             val document=DocumentFile.fromTreeUri(context,permission.uri)
             if(document?.canWrite() == true&&document.name.equals(file.name)){
                 return document.uri
             }
         }
         return null
     }

  
     fun getStoragePermission(file:File, context: Activity){
         val uri= getUriFromSharedPreference(file,context)
        if(uri==null){
            with(context) { startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),32) }
        }
     }

    fun createDocumentFolder(document:DocumentFile?,name:String):Boolean{
          return document?.createDirectory(name)?.exists()?:false
      }

    fun createFolder(parent:File,name: String):File{
        return  File(parent.path+"/"+ name).apply {
                mkdirs()
                mkdir()
            }
    }

    fun moveTo(origin:File,destination: File){
        origin.renameTo(destination)
    }

    fun createFile(parent: File,name: String):File{
        return File(parent.path,name).apply {
            createNewFile()
        }
    }

    fun deleteFile(file: File):Boolean{
        return  file.delete()
    }

     fun moveFile(source:File,storage:File,context: Context):Boolean{

         var sourceDocument=
             getUriFromSharedPreference(storage,context)?.let {
                 DocumentFile.fromTreeUri(context,
                     it
                 )
             }
         var destinationDocument=
             getUriFromSharedPreference(storage,context)?.let {
                 DocumentFile.fromTreeUri(context,
                     it
                 )
             }
         val paths=source.path.substring(storage.path.length).split("/")
         if(sourceDocument!=null) {
             paths.forEach { path ->
                 if (path.isNotEmpty()) {
                     sourceDocument = sourceDocument?.findFile(path)
                 }
             }

             //in case the folder doesn't exist
             createDocumentFolder(destinationDocument,"ShrinkCompressor")
             destinationDocument= destinationDocument?.findFile("ShrinkCompressor")

             // move the file
             sourceDocument?.parentFile?.uri?.let {
                 destinationDocument?.uri?.let { it1 ->
                     sourceDocument?.uri?.let { it2 ->
                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                             DocumentsContract.moveDocument(context.contentResolver, it2,
                                 it, it1)
                         }
                     }
                 }
             }
         }
         return destinationDocument?.findFile(source.name)?.exists()?:false
     }

     fun fileToUri(context: Context,file: File,
         scanListener:MediaScannerConnection.OnScanCompletedListener){
         MediaScannerConnection.scanFile(context, arrayOf(file.path),null,scanListener)
     }

    fun fileToUri(
        context: Context, inFiles: ArrayList<File>,
        scanListener: MediaScannerConnection.OnScanCompletedListener){
        val outFiles=Array(inFiles.size, init ={index->inFiles[index].path})
        MediaScannerConnection.scanFile(context, outFiles,null,scanListener)
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

     fun getDirectorySize(dir:File):String{
         var bytes=0L
         val files=mutableListOf<File>()
         val lst=dir.listFiles()
         lst?.forEach { file->
             if(file.isDirectory) files.add(file)
             else bytes+=file.length()
         }
         // count the sub-directories
         while (files.isNotEmpty()){
             val found= mutableListOf<File>()
             files.forEach { file->
                 if(file.isDirectory) {
                     val innerFileList = file.listFiles()
                     innerFileList?.forEach { innerFile ->
                         if(innerFile.isDirectory)
                             found.add(innerFile)
                          else
                              bytes+=innerFile.length()
                     }
                 }else
                     bytes+=file.length()
             }
             files.clear()
             files.addAll(found)
             found.clear()
         }
        return Disk.getSize(bytes)
     }


     fun saveFileFromUri(context: Context,uri:Uri,destination:String){
         var stream:InputStream?=null
         var bos:BufferedOutputStream?=null
         try {
             stream=context.contentResolver.openInputStream(uri)
             bos= BufferedOutputStream(FileOutputStream(destination,false))
             val buffer= ByteArray(1024)
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

    fun renameFile(context: Context,uri: Uri,name: String){
        DocumentFile.fromTreeUri(context,uri)?.renameTo(name)
    }



    }
}