package com.vid.compress.storage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.File

class FileObjectViewModel(file:File) :ViewModel(){
    var selected by mutableStateOf(false)
    var fileName by mutableStateOf(file.name)
    var filePath by mutableStateOf(file.path)
    var directoryCount by mutableStateOf("0")

    fun toggleSelected(){
        selected=!selected
    }

}