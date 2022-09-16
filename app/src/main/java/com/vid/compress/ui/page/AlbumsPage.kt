package com.vid.compress.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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


@Composable
fun albumList(album:AlbumViewModel, state:LazyListState,scrollInfo: ScrollInfo){
    val first by derivedStateOf { state.firstVisibleItemIndex}
    if(!scrollInfo.isEqualTo(first)){
        val last=first+state.layoutInfo.visibleItemsInfo.size+5
       for(i in first until last){
          if(!album.isEmpty()&&album.size()>i){
              scrollInfo.firstVisibleItem=first
              //album.get(i).loadThumbnail()
              album.get(i).setDirCount()
        }
      }
    }
    LazyColumn(modifier = Modifier.fillMaxSize(), state = state){

        if(album.directory!="")
        item {
            moveBack(album = album)
        }

        itemsIndexed(album.files){ index, item ->
            albumItem(item, album)
            if(index<album.files.lastIndex)
            Divider(color = MaterialTheme.colors.onSecondary,
                thickness = 0.5.dp)
        }
        item {
            Spacer(modifier = Modifier.padding(50.dp))
        }
    }

}

@Composable
fun moveBack(album: AlbumViewModel){

  Card(modifier = Modifier.fillMaxWidth().clickable { album.setFolder("") }, elevation = 1.dp) {
      Row(
          modifier = Modifier
              .fillMaxWidth()
              .width(120.dp)
              .padding(10.dp)
      ) {

          Icon(
              painter = painterResource(id = R.drawable.ic_arrow_back),
              contentDescription = "previous", modifier = Modifier.padding(start = 10.dp)
          )
          Text(
              text = album.directory,
              style = TextStyle(
                  color = MaterialTheme.colors.onSecondary,
                  fontWeight = FontWeight.Bold, fontSize = 15.sp
              ), maxLines = 1,
              modifier = Modifier
                  .fillMaxWidth(0.3f)
                  .padding(start = 10.dp, bottom = 10.dp)
          )

      }
  }

}

@Composable
fun albumItem(fileViewModel: FileObjectViewModel,album: AlbumViewModel){

    BoxWithConstraints (modifier =
    Modifier
        .fillMaxWidth()
        .clickable {
            if (fileViewModel.isFolder())
                album.setFolder(fileViewModel.filePath)

        }){
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
            if(fileViewModel.thumbnailLoader.thumbnail!=null){
                Image(
                    bitmap = fileViewModel.thumbnailLoader.thumbnail!!,
                    contentDescription = "fileThumbnail",
                    modifier = Modifier
                        .layoutId("thumbnail")
                        .padding(5.dp)
                        .size(50.dp)
                )
            }else {
                Image(
                    painter = painterResource(
                        id =if(fileViewModel.isFolder()) R.drawable.ic_folder
                             else
                        R.drawable.ic_play_video_dark),
                    contentDescription = "fileThumbnail",
                    modifier = Modifier
                        .layoutId("thumbnail")
                        .padding(5.dp)
                        .size(50.dp)
                )
            }
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


        }
    }

}