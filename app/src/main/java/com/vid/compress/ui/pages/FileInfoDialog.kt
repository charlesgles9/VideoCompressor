package com.vid.compress.ui.pages

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.vid.compress.ui.models.AlbumViewModel
import com.vid.compress.util.DateUtils
import java.io.File


@Composable
fun PropertiesDialog(album: AlbumViewModel,onDismiss:()->Unit){

    if(album.showProperties.value){
        AlertDialog(onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = { album.showProperties.value=false}) {
                    Text(text = "Close")
                }
            }, title = { Text(text = "Properties")}, text = { FileProperties(album = album) })
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




















