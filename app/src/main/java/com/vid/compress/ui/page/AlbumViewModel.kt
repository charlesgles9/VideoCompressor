package com.vid.compress.ui.page

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.vid.compress.storage.FileObjectViewModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.viewModelScope
import com.vid.compress.storage.FileUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class AlbumViewModel() :ViewModel(){
   private lateinit var map:HashMap<String,MutableList<File>>
   private var filterBucket= mutableListOf<FileObjectViewModel>()
   var files =mutableStateListOf<FileObjectViewModel>()
   var selected= mutableStateListOf<FileObjectViewModel>()
   var activateSearch by mutableStateOf(false)
   var directory=""
   var isLoaded=false
   var active=false
    fun isEmpty():Boolean{
        return files.isEmpty()
    }

    fun size():Int{
        return files.size
    }

    fun get(index:Int):FileObjectViewModel{
        return files[index]
    }

    fun addSelectFile(file: FileObjectViewModel){
        selected.add(file)
    }

    fun removeSelectFile(file: FileObjectViewModel){
        selected.removeAll{ it.filePath.equals(file.filePath) }
    }

    fun clearSelected(){
        selected.forEach { file->file.selected=false }
        selected.clear()
    }

    fun isSelectActive():Boolean{
        return selected.isNotEmpty()
    }

    fun filter(phrase:String){
        files.removeAll {file->
            val value=!file.fileName.lowercase(Locale.ROOT).contains(phrase.lowercase(Locale.ROOT))
              if(value)
                  filterBucket.add(file)
            return@removeAll value
        }
    }


    fun restoreFilter(){
        if(filterBucket.isNotEmpty()) {
            files.addAll(filterBucket)
            filterBucket.clear()
        }
    }

    fun setFolder(key:String){

        //reset the album to the origin
        if(key==""){
            files.clear()
            map.forEach {
                files.add(FileObjectViewModel(it.key))
            }
            directory=""
            return
        }

        if(!map.containsKey(key))
            return

        //remove previously attached files
        files.clear()

        // add the files of the specific folder only
        val data=map[key]

        data?.forEach {file ->
            files.add(FileObjectViewModel(file))
        }

        directory=File(key).name

    }


    fun fetchFiles(foldersOnly: Boolean =true,context: Context){

        viewModelScope.launch {
            isLoaded=true
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
                data.sortBy {File(it.filePath).lastModified() }
            }
            files.addAll(data)
        }
    }




}

fun <T>SnapshotStateList<T>.update(newAlbum:List<T>){
    clear()
    addAll(newAlbum)
}

fun <T>SnapshotStateList<T>.update(item:T){
    add(item)
}


