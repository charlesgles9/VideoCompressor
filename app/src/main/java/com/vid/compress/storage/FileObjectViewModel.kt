package com.vid.compress.storage
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
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
import com.vid.compress.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class FileObjectViewModel(private val file:File) :ViewModel(){
    var selected by mutableStateOf(false)
    var update by mutableStateOf(false)
    var fileName by mutableStateOf(file.name)
    var filePath by mutableStateOf(file.path)
    var directoryCount by mutableStateOf("0")
    var thumbnailLoader by mutableStateOf(ThumbnailLoader(file))
    var videoLength by mutableStateOf("00:00:00")
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


    fun setVideoLength(){
        if(file.isDirectory)
            return
        viewModelScope.launch {
            var time:String?="0"
            withContext(Dispatchers.Default) {
                val meta = MediaMetadataRetriever()
                try {
                    meta.setDataSource(file.path)
                    time= meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            if(time!=null){
              videoLength=DateUtils.getDate(time!!.toLong())
            }
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
        fun loadBitmap(): Bitmap? {
            loaded = true
            val path = if (file.isFile){ file.path
              }else {  //in case it's a folder try to take it's first content
                  val files=file.list(FileUtility.videoFilter)
                if((files?.size ?: 0) == 0)
                    return null
                file.path + File.separator + files?.get(0)
            }
            if(path==null) return null
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    return ThumbnailUtils.createVideoThumbnail(
                        File(path),
                        Size(100, 100),
                        CancellationSignal()
                    )
                }catch (io:IOException){
                    io.printStackTrace()
                }
            }else{
                return ThumbnailUtils.createVideoThumbnail(path,MediaStore.Video.Thumbnails.MINI_KIND)
            }
            return null
        }
    }

}