package com.vid.compress.storage
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FileObjectViewModel(private val file:File) :ViewModel(){
    var selected by mutableStateOf(false)
    var fileName by mutableStateOf(file.name)
    var filePath by mutableStateOf(file.path)
    var directoryCount by mutableStateOf("0")
    var thumbnailLoader by mutableStateOf(ThumbnailLoader(file))
    constructor(path:String):this(File(path)){}
    fun toggleSelected(){
        selected=!selected
    }

    fun isFolder():Boolean{
        return file.isDirectory
    }
    fun loadThumbnail(){
        if(!thumbnailLoader.loaded)
        viewModelScope.launch {
            var thumbnail: Bitmap?
            withContext(Dispatchers.Default){
               thumbnail= thumbnailLoader.loadBitmap()
                if(thumbnail!=null){
                    thumbnail=Bitmap.createScaledBitmap(thumbnail!!,100,100,false)
                }
               thumbnailLoader.thumbnail=thumbnail?.asImageBitmap()
            }

        }

    }
    fun setDirCount(){
        if(file.isDirectory){
        viewModelScope.launch {
            countDirectory()
        }}else{

            directoryCount=Disk.getSize(file.length())
       }
    }

    private suspend fun countDirectory(){
         withContext(Dispatchers.Default){
              directoryCount= ("items " + (file.list(FileUtility.videoFilter)?.size.toString()?:"0"))
        }
    }


    class ThumbnailLoader(private val file: File){
        var thumbnail: ImageBitmap?=null
        var loaded=false
        fun loadBitmap():Bitmap?{
            loaded=true
            val path=if(file.isFile) file.path
               else  //in case it's a folder try to take it's first content
              file.path+File.separator+file.list(FileUtility.videoFilter)?.get(0)
            if(path==null) return null
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThumbnailUtils.createVideoThumbnail(File(path),Size(100,100), CancellationSignal())
            }else{
                ThumbnailUtils.createVideoThumbnail(path,MediaStore.Video.Thumbnails.MINI_KIND)
            }
        }
    }

}