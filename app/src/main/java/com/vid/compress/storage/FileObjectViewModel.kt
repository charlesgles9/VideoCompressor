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
    var videoLength by mutableStateOf("00:00:00")
    constructor(path:String):this(File(path)){}
    fun toggleSelected(){
        selected=!selected
    }

    fun isFolder():Boolean{
        return file.isDirectory
    }

    fun loadVideoDetails(){
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if(file.isDirectory) {
                    directoryCount =
                        ("items " + (file.list(FileUtility.videoFilter)?.size.toString() ?: "0"))
                }else{
                    directoryCount=Disk.getSize(file.length())
                    setVideoLength()
                }
            }
        }
    }


    private fun setVideoLength(){
        if(file.isDirectory)
            return
        var time:String?="0"
        val meta = MediaMetadataRetriever()
        try {
            meta.setDataSource(file.path)
            time= meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        }catch (e:Exception){
            e.printStackTrace()
        }
        if(time!=null)
            videoLength=DateUtils.getDate(time.toLong())

        }
    }
