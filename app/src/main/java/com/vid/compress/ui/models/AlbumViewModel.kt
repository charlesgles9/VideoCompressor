package com.vid.compress.ui.models

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.vid.compress.ui.models.FileObjectViewModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.vid.compress.storage.Disk
import com.vid.compress.storage.FileUtility
import com.vid.compress.ui.callbacks.LoadingCompleteListener
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
   var showProperties= mutableStateOf(false)
   var selectedSize= mutableStateOf("calculating...")
    fun isEmpty():Boolean{
        return files.isEmpty()
    }

    fun size():Int{
        return files.size
    }

    fun get(index:Int): FileObjectViewModel {
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
        val removeAll = files.removeAll { file ->
            val value =
                !file.fileName.lowercase(Locale.ROOT).contains(phrase.lowercase(Locale.ROOT))
            if (value) {
                filterBucket.add(file)
            }
            return@removeAll value
        }
    }


    fun calculateSelectedFileSize(){
        viewModelScope.launch {
            var nBytes=0L
            withContext(Dispatchers.Default){
                selected.forEach { path->
                    val file=File(path.filePath)
                    //size of file in bytes
                    if(file.isFile)
                    nBytes+= file.length()
                    else
                        // if it's a folder then get all files from that folder
                        map[file.path]?.forEach { subFile->
                            nBytes+= subFile.length()
                        }
                }
            }
            selectedSize.value=Disk.getSize(nBytes)
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


    fun fetchFiles(foldersOnly: Boolean =true,context: Context,listener:LoadingCompleteListener){
        viewModelScope.launch {
            isLoaded=true
            listener.started()
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
                data.sortByDescending {File(it.filePath).lastModified() }
            }
            files.addAll(data)
            listener.finished()
        }
    }

    private var selectFlag=false
    fun selectAll() {
        selectFlag = !selectFlag
        if (selectFlag) {
             selected.clear()
            files.forEach { file ->
                file.selected = true
                addSelectFile(file)
            }
        } else {
            files.forEach { file ->
                file.selected = false
            }
            selected.clear()
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


