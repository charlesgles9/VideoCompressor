package com.vid.compress.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vid.compress.ui.models.AlbumViewModel
import com.vid.compress.util.DateUtils
import java.io.File


@Composable
fun PropertiesDialog(album1: AlbumViewModel,album2: AlbumViewModel,album3: AlbumViewModel,onDismiss:()->Unit){

    if(album1.showProperties.value||album2.showProperties.value||album3.showProperties.value){
        AlertDialog(onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    album1.showProperties.value=false
                    album2.showProperties.value=false
                    album3.showProperties.value=false
                }) {
                    Text(text = "Close")
                }
            }, title = { Text(text = "Properties")}, text = {
                if( album1.showProperties.value) {
                    FileProperties(album = album1)
                }
                if(album2.showProperties.value) {
                    FileProperties(album = album2)
                }
                if(album3.showProperties.value) {
                    FileProperties(album = album3)
                }
            })
    }
}

@Composable
fun FileProperties(album:AlbumViewModel){
    //calculate the number of bytes
    var size by remember { album.selectedSize }
        Column (modifier=Modifier.background(color = Color.White)){

            if(album.selected.size>1) {
                Text(text = "Size", color = MaterialTheme.colors.primary)
                Text(text = size)
                Text(text = "Item Count", color = MaterialTheme.colors.primary)
                Text(text = album.selected.size.toString() + " Items")
            }else if(album.selected.size==1){
                Text(text = "Path", color = MaterialTheme.colors.primary)
                Text(text = album.selected[0].filePath)
                Text(text = "Name", color = MaterialTheme.colors.primary)
                Text(text = album.selected[0].fileName)
                Text(text = "Date", color = MaterialTheme.colors.primary)
                DateUtils.getDate(File(album.selected[0].filePath).lastModified())
                    ?.let { Text(text = it) }
                Text(text = "Size", color = MaterialTheme.colors.primary)
                Text(text = size)

            }
        }

    album.calculateSelectedFileSize()
}




















