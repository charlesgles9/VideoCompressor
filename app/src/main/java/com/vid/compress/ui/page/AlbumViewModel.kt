package com.vid.compress.ui.page

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.vid.compress.storage.FileObjectViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.vid.compress.storage.FileUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AlbumViewModel() :ViewModel(){
   private lateinit var map:HashMap<String,MutableList<File>>
    var files =mutableStateListOf<FileObjectViewModel>()


    fun isEmpty():Boolean{
        return files.isEmpty()
    }
    fun fetchFiles(foldersOnly: Boolean =true,context: Context){
        viewModelScope.launch {
            val data= mutableListOf<FileObjectViewModel>()
            withContext(Dispatchers.Default) {
                map= FileUtility.fetchVideos(context)
                if (foldersOnly) {
                    map.forEach {
                        data.add(FileObjectViewModel(it.key))
                    }

                } else {
                    map.forEach { album ->
                        album.value.forEach { file ->
                            data.add(FileObjectViewModel(file))
                        }
                    }
                }
            }
            files.addAll(data)
        }
    }




}

fun <T>SnapshotStateList<T>.updateAlbum(newAlbum:List<T>){
    clear()
    addAll(newAlbum)
}

fun <T>SnapshotStateList<T>.updateAlbum(item:T){
    add(item)
}


