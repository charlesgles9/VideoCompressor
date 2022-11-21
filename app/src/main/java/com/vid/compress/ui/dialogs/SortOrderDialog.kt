package com.vid.compress.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vid.compress.ui.models.AlbumViewModel
import com.vid.compress.ui.theme.LighterBlue
import androidx.compose.foundation.selection.selectable as selectable


@Composable
fun SortByAlertDialog(album:AlbumViewModel,onDismiss:()->Unit){
    if(album.showSortOrderDialog.value) {
        AlertDialog(onDismissRequest = onDismiss, confirmButton = {
            TextButton(onClick = {
                //close the dialog
                onDismiss()
            }) {
                Text(text = "Confirm",color = MaterialTheme.colors.onSecondary)
            }
        }, title = { Text(text = "Sort By") }, text = {
            SortLayout(album)
        })
    }
}

@Composable
fun SortLayout(album: AlbumViewModel){
    val options= album.sortOder
    val (selectedOption,onOptionSelected)= remember { mutableStateOf(album.sortFlag) }

    Column (modifier = Modifier.fillMaxWidth()){
        options.forEach{
            option->
            Row(modifier = Modifier
                .fillMaxWidth()
                .selectable(selected = (option == selectedOption),
                    onClick = {
                        onOptionSelected(option)
                        album.sortFlag = option
                    })
                .padding(5.dp)) {
              RadioButton(selected = (option==selectedOption),
                  onClick = {
                      onOptionSelected(option)
                      album.sortFlag=option
                  }, colors = RadioButtonDefaults.colors(selectedColor = LighterBlue))
                Text(text = option,
                   modifier = Modifier.padding(5.dp))
            }
        }
    }
}