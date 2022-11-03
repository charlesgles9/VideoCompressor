package com.vid.compress.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.vid.compress.R
import com.vid.compress.ui.models.AlbumViewModel
import com.vid.compress.ui.models.FileObjectViewModel
import com.vid.compress.ui.theme.CustomShape1
import com.vid.compress.ui.theme.SelectColor
import com.vid.compress.ui.theme.Shapes


@SuppressLint("UnrememberedMutableState")
@Composable
fun HistoryList(context: Context, album:AlbumViewModel, state: LazyListState){
    val first by derivedStateOf { state.firstVisibleItemIndex}
    val slideOptions= remember { mutableStateOf(false) }
    val sliderWidth= animateDpAsState(targetValue =if(slideOptions.value) 90.dp else 0.dp )
    val constraints= ConstraintSet {
        val itemList=createRefFor("itemList")
        val options=createRefFor("options")
        val search=createRefFor("search")
        val fold=createRefFor("fold")
        constrain(itemList){
            end.linkTo(options.start)
        }
        constrain(options){
            end.linkTo(parent.end)
        }
        constrain(fold){
            centerVerticallyTo(parent)
            end.linkTo(options.start)
        }
    }
    ConstraintLayout(modifier = Modifier.fillMaxSize(), constraintSet = constraints) {
        slideOptions.value=slideOptions.value&&album.isSelectActive()
        FileOperationLayout(album,context,sliderWidth)
        LazyColumn(modifier = Modifier.fillMaxSize().layoutId("itemList"), state = state){

            itemsIndexed(album.files){ index, item ->
                HistoryItem(context,item, album)
                if(index<album.files.lastIndex) {
                    Divider(
                        color = MaterialTheme.colors.onSecondary,
                        thickness = 0.5.dp
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.padding(50.dp))
            }
        }
        if(album.isSelectActive()) {
            Row(modifier = Modifier
                .layoutId("fold")
                .background(color = Color.DarkGray, shape = CustomShape1)
                .clickable { slideOptions.value = !slideOptions.value }) {
                Image(
                    painter = painterResource(id = if (slideOptions.value) R.drawable.ic_fold else R.drawable.ic_unfold),
                    contentDescription = "fold", modifier = Modifier.size(40.dp)
                )
                Text(
                    text = if (slideOptions.value) "CLOSE" else "OPEN", color = Color.White,
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }

}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItem(context: Context, file: FileObjectViewModel, album: AlbumViewModel){

    BoxWithConstraints (modifier =
    Modifier
        .fillMaxWidth().border(width = 2.dp, color = if (file.selected) SelectColor else Color.Transparent,shape= Shapes.medium)
        .combinedClickable(onClick = {
                //select a file
                if (album.isSelectActive()) {
                    file.selected = !file.selected
                    if (file.selected)
                        album.addSelectFile(file)
                    else
                        album.removeSelectFile(file)
                }else{
                    //play the file
                    val intent = Intent(context, Class.forName("com.vid.compress.VideoPlayerActivity"))
                    val list = ArrayList<String>()
                    list.add(file.filePath)
                    intent.putStringArrayListExtra("uriList", list)
                    context.startActivity(intent)
                }



        }, onLongClick = {

            file.selected = !file.selected
            if (file.selected) {
                album.addSelectFile(file)
            } else {
                album.removeSelectFile(file)
            }
        })){
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

                if(file.isBitmapReady){
                    Image(bitmap = file.thumbnail, contentDescription ="thumbnail",
                        modifier = Modifier
                            .layoutId("thumbnail").padding(5.dp)
                            .size(50.dp), contentScale = ContentScale.Crop)
                }else{
                    Image(painter = painterResource(id = R.drawable.ic_play_video_dark), contentDescription ="thumbnail",
                        modifier = Modifier
                            .layoutId("thumbnail").padding(5.dp)
                            .size(50.dp), contentScale = ContentScale.Crop)
                }


            Text(text = file.fileName,
                style = TextStyle(color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp), maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(0.8f).padding(end = 5.dp)
                    .layoutId("directoryName"))
            Text(text = file.filePath,
                style= TextStyle(color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.Normal, fontSize = 11.sp), maxLines = 1,overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(0.5f).padding(end = 5.dp)
                    .layoutId("directoryPath"))
            Text(text = file.directoryCount,
                style= TextStyle(color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.Normal, fontSize = 10.sp), maxLines = 1,
                modifier = Modifier
                    .layoutId("directoryCount")
                    .padding(end = 15.dp))
        }
    }
    file.loadVideoDetails()
    if(!file.isFolder()) {
        file.loadBitmap(context)
    }
}