package com.vid.compress.storage
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.vid.compress.R
import com.vid.compress.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FileObjectViewModel(private val file:File) :ViewModel(){
    var selected by mutableStateOf(false)
    var update by mutableStateOf(false)
    var fileName by mutableStateOf(file.name)
    var filePath by mutableStateOf(file.path)
    var detailsLoaded=false
    var directoryCount by mutableStateOf("0")
    var videoLength by mutableStateOf("00:00:00")
    var videoResolution by mutableStateOf(Pair<Any,Any>(0,0))
    lateinit var thumbnail:ImageBitmap
    var thumbnailLoaded by mutableStateOf(false)
    constructor(path:String):this(File(path)){}
    fun toggleSelected(){
        selected=!selected
    }

    fun isFolder():Boolean{
        return file.isDirectory
    }

    fun loadBitmap(context: Context){
        if(!thumbnailLoaded)
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                val bmp=Glide.with(context).load(file).submit(100,100).get().toBitmap()
                val scaled=Bitmap.createScaledBitmap(bmp,100,100,false)
                thumbnail=scaled.asImageBitmap()
                thumbnailLoaded=true
            }
        }
    }

    fun loadVideoDetails(){
        if(detailsLoaded)
            return
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (file.isDirectory) {
                    directoryCount =
                        ("items " + (file.list(FileUtility.videoFilter)?.size.toString() ?: "0"))
                } else {
                    directoryCount = Disk.getSize(file.length())
                    setVideoLength()
                }
                detailsLoaded = true
            }}
    }

     fun fetchVideoResolution(){
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                val media= MediaPlayer()
                try {
                    media.setDataSource(file.path)
                    media.prepare()
                    videoResolution = Pair(media.videoWidth, media.videoHeight)
                    media.release()
                }catch (e:Exception){
                    e.printStackTrace()
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
