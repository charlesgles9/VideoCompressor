package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.Player
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.vid.compress.permisions.PermissionHelper
import com.vid.compress.storage.FileObjectViewModel
import com.vid.compress.storage.FileUtility
import com.vid.compress.ui.page.AlbumViewModel
import com.vid.compress.ui.page.DrawerView
import com.vid.compress.ui.page.ScrollInfo
import com.vid.compress.ui.page.albumList
import com.vid.compress.ui.theme.Background1
import com.vid.compress.ui.theme.CustomShape1
import com.vid.compress.ui.theme.SelectColor
import com.vid.compress.ui.theme.VideoCompressorTheme
import kotlinx.coroutines.*
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHelper.grantStorageReadWrite(this)

        setContent {
            VideoCompressorTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
                    ToolBar(this)

                }
            }
        }
    }
}


private val homeAlbum=AlbumViewModel()

@SuppressLint("UnrememberedMutableState")
@Composable
fun FileList(album:AlbumViewModel,state:LazyGridState,scrollInfo: ScrollInfo,context: Context){
    val columnCount=2
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
        Column(modifier = Modifier
            .background(color = MaterialTheme.colors.primary)
            .fillMaxHeight()
            .width(sliderWidth.value)
            .layoutId("options")) {
        }

        LazyVerticalGrid(columns = GridCells.Fixed(columnCount),
            state = state,
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center, modifier = Modifier.layoutId("itemList")){
            items(items= album.files, key = {file -> file.filePath}) { file->
                FileCard(file,context,album)
            }
            item {
                Spacer(modifier = Modifier.padding(20.dp))
            }
        }

        if(homeAlbum.isSelectActive())
        Row(modifier= Modifier
            .layoutId("fold")
            .background(color = Color.DarkGray, shape = CustomShape1)
            .clickable { slideOptions.value = !slideOptions.value }) {
            Image(painter = painterResource(id =if(slideOptions.value) R.drawable.ic_fold else R.drawable.ic_unfold),
                contentDescription ="fold", modifier = Modifier.size(40.dp))
            Text(text = if(slideOptions.value)"CLOSE" else "OPEN", color = Color.White,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterVertically))
        }
    }

}

@Composable
fun BottomNavigationOptions(context: Activity){

    val hideMenu= remember { mutableStateOf(false)}
    val slideUpDown= animateDpAsState(targetValue = if(hideMenu.value) 60.dp else 0.dp)
    val constraints= ConstraintSet {

        val close=createRefFor("close")
        val selectInfo=createRefFor("selectInfo")
        val modify=createRefFor("modify")

        constrain(close){
            start.linkTo(parent.start)
            centerVerticallyTo(parent)
        }

        constrain(selectInfo){
            start.linkTo(close.end)
            centerVerticallyTo(parent)
        }

        constrain(modify){
            end.linkTo(parent.end)
            centerVerticallyTo(parent)
        }
    }
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colors.primary)
        .height(slideUpDown.value)
        .padding(top = 10.dp)
        .clickable { }, constraintSet = constraints) {

        hideMenu.value= homeAlbum.isSelectActive()
        Image(painterResource(id = R.drawable.ic_close),
            contentDescription = "close", modifier = Modifier
                .size(50.dp)
                .layoutId("close")
                .padding(start = 10.dp)
                .clickable {
                    hideMenu.value = false
                    homeAlbum.clearSelected()
                })
        Text(text = "Selected Items("+ homeAlbum.selected.size+")",
            style = TextStyle(color =Color.White,
                fontSize = 12.sp,fontWeight = FontWeight.Bold),
            modifier = Modifier
                .layoutId("selectInfo")
                .padding(start = 13.dp))

        Row(modifier= Modifier
            .layoutId("modify")
            .clickable {
                if (!homeAlbum.isSelectActive())
                    return@clickable
                val intent = Intent(
                    context.applicationContext,
                    Class.forName("com.vid.compress.ShrinkActivity")
                )
                val array = ArrayList<String>()
                for (i in 0 until homeAlbum.selected.size)
                    array.add(homeAlbum.selected[i].filePath)
                intent.putStringArrayListExtra("selected", array)
                context.startActivity(intent)
            }
            .padding(end = 10.dp)) {
            Text( modifier = Modifier.align(Alignment.CenterVertically)
                ,style = TextStyle(color =Color.White,
                fontSize = 13.sp,fontWeight = FontWeight.Bold), text = "Modify")
            Image(painterResource(id = R.drawable.ic_fold),
                contentDescription = "next", modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically))
        }

        }


}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileCard(file:FileObjectViewModel,context: Context,album: AlbumViewModel){

         Box(modifier = Modifier
             .padding(1.dp).combinedClickable(onClick = {
                 file.selected = !file.selected
                 if (file.selected) {
                     album.addSelectFile(file)
                 } else {
                     album.removeSelectFile(file)
                 }

             }, onLongClick = {}).border(width = 5.dp, color = if (file.selected) SelectColor else Color.Transparent)) {
             GlideImage(imageModel = file.filePath,
                 imageOptions = ImageOptions(alignment = Alignment.Center,
                     contentScale = ContentScale.Crop), previewPlaceholder =R.drawable.ic_play_video_dark ,
                 requestOptions = {
                     RequestOptions().placeholder(context.getDrawable(R.drawable.ic_play_video_dark)).override(100,100).diskCacheStrategy(
                         DiskCacheStrategy.ALL).centerCrop()
                 }, modifier = Modifier
                     .align(Alignment.Center)
                     .sizeIn(100.dp, 100.dp, Dp.Unspecified, Dp.Unspecified))

             Column(
                 modifier = Modifier
                     .align(Alignment.BottomStart)
                     .background(color = Background1)
                     .fillMaxWidth()
                     .padding(start = 5.dp, bottom = 5.dp)) {


                 Text(
                     text = file.directoryCount, maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                     modifier = Modifier.padding(top = 5.dp),
                     style = TextStyle(
                         color = Color.White,
                         fontSize = 11.sp,
                         fontWeight = FontWeight.Normal))
                 Text(
                     text = file.videoLength, maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                     modifier = Modifier
                         .padding(top = 5.dp),
                     style = TextStyle(
                         color = Color.White,
                         fontSize = 11.sp,
                         fontWeight = FontWeight.Normal))
             }
         }

      file.loadVideoDetails()

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ToolBar(context: Activity){
    val scope= rememberCoroutineScope()
    val scaffoldState= rememberScaffoldState()
    Scaffold (
        scaffoldState=scaffoldState,
        drawerContent = {DrawerView()},
        bottomBar = { BottomNavigationOptions(context)},
        topBar = {
        TopAppBar(title = { Text(text = "Compress", fontSize = 15.sp)},
                  navigationIcon = {
                      IconButton(onClick = {
                          scope.launch {
                              scaffoldState.drawerState.open()
                          }
                      }) {
                          Icon(Icons.Filled.Menu, contentDescription = "Drawer Icon")

                      }

                    },
                  elevation = 2.dp, actions = {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_videocam),
                            contentDescription = "Play",
                            modifier = Modifier.padding(start = 10.dp, end = 20.dp))
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp))
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More",
                            modifier = Modifier.padding(start = 20.dp, end = 10.dp))
                    })
        }){


      HorizontalPagerView(context)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerView(context: Context){
    Box(modifier = Modifier.fillMaxSize()) {
        
        //create pages
        val size=2
        val pageState= rememberPagerState()
        val homeState= rememberLazyGridState()

        val homeScrollInfo=ScrollInfo(-1)
        HorizontalPager(count =size,
                        state = pageState) { currentPage->
            when(currentPage){
                
                0->{
                    if(!homeAlbum.isLoaded)
                    homeAlbum.fetchFiles(context=context,foldersOnly = false)
                    FileList(homeAlbum,homeState,homeScrollInfo,context)
                }
                
                1->{
                    val state= rememberLazyListState()
                    val album=AlbumViewModel()
                    val scrollInfo=ScrollInfo(-1)
                        album.fetchFiles(context=context)
                    albumList(album,state,scrollInfo)

                }
            }
        }
        
      /*  val  coroutineScope= rememberCoroutineScope()
        
        Button(onClick = { 
            coroutineScope.launch {
                pageState.animateScrollToPage(if(pageState.currentPage==0)1 else 0)
            }
        }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text(text = "Next Page")
        }*/
    }
}



