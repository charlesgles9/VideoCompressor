package com.vid.compress.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vid.compress.ui.models.AlbumViewModel
import com.vid.compress.ui.theme.lightRed


@Composable
fun ConfirmDeleteDialog(album:AlbumViewModel,onDismiss:(Boolean)->Unit){

    if(album.showDeleteDialog.value) {
        //calculate the number of bytes
        val size by remember { album.selectedSize }
        AlertDialog(
            onDismissRequest = { onDismiss(false) },
            confirmButton = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { //close the dialog
                        onDismiss(false)

                    }, modifier = Modifier.weight(1f)) {
                        Text(text = "Cancel", color = MaterialTheme.colors.onSecondary)
                    }
                    TextButton(onClick = { //delete the file
                        onDismiss(true)
                    }, modifier = Modifier.weight(1f)) {

                        Text(text = "Delete", color = MaterialTheme.colors.onSecondary)
                    }
                }

            },
            title = { Text(text = "Delete Items(" + album.selected.size + ")", fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Are you sure you want to delete these files?",
                        modifier = Modifier.padding(5.dp), color = lightRed,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/*deleted files are not recoverable*/",
                        modifier = Modifier.padding(5.dp), color = lightRed,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Size: $size",
                        modifier = Modifier.padding(5.dp), color = lightRed,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold
                    )
                }
            },
        )
        album.calculateSelectedFileSize()
    }
}