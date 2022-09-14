package com.vid.compress.ui.page

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.vid.compress.R
import com.vid.compress.storage.FileObjectViewModel
import java.io.File


@Composable
fun albumList(context: Context){

   val albumViewModel=AlbumViewModel()
    LazyColumn(modifier = Modifier.fillMaxSize()){
        itemsIndexed(albumViewModel.files){index, item ->
            albumItem(item)
            if(index<albumViewModel.files.lastIndex)
            Divider(color = MaterialTheme.colors.onSecondary,
                thickness = 0.5.dp)
        }
        item {
            Spacer(modifier = Modifier.padding(50.dp))
        }
    }
    albumViewModel.fetchFiles(context=context)
}


@Composable
fun albumItem(fileViewModel: FileObjectViewModel){

    BoxWithConstraints (modifier = Modifier.fillMaxWidth()){
      val constraints=  ConstraintSet {

           val thumbnail=createRefFor("thumbnail")
           val directoryName=createRefFor("directoryName")
           val directoryCount=createRefFor("directoryCount")
           val directoryPath=createRefFor("directoryPath")

           constrain(thumbnail){
               start.linkTo(parent.start)
           }
           constrain(directoryName){
               start.linkTo(thumbnail.end)
               centerVerticallyTo(parent)
           }
           constrain(directoryPath){
               top.linkTo(directoryName.bottom)
               start.linkTo(thumbnail.end)
           }
           constrain(directoryCount){
               end.linkTo(parent.end)
               centerVerticallyTo(directoryPath)
           }
       }
        ConstraintLayout(constraints, modifier = Modifier.fillMaxWidth()) {
            Image(painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription ="fileThumbnail" ,
                modifier = Modifier
                    .layoutId("thumbnail")
                    .padding(5.dp)
                    .size(50.dp))
            Text(text = fileViewModel.fileName,
                style = TextStyle(color = MaterialTheme.colors.onSecondary,
                fontWeight = FontWeight.Bold, fontSize = 15.sp), maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .layoutId("directoryName"))
            Text(text = fileViewModel.filePath,
                 style= TextStyle(color = MaterialTheme.colors.onSecondary,
                 fontWeight = FontWeight.Normal, fontSize = 11.sp), maxLines = 1,
                 modifier = Modifier
                     .fillMaxWidth(0.5f)
                     .layoutId("directoryPath"))
            Text(text = fileViewModel.directoryCount,
                style= TextStyle(color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.Normal, fontSize = 10.sp), maxLines = 1,
                modifier = Modifier
                    .layoutId("directoryCount")
                    .padding(end = 15.dp))

            fileViewModel.setDirCount()
        }
    }

}