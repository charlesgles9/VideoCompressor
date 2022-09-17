package com.vid.compress

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
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

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.vid.compress.ui.theme.CustomShape1
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




@SuppressLint("UnrememberedMutableState")
@Composable
fun FileList(album:AlbumViewModel,state:LazyGridState,scrollInfo: ScrollInfo,context: Context){

      val columnCount=2
      val first by derivedStateOf { state.firstVisibleItemIndex}

      if(!scrollInfo.isEqualTo(first)){
          val last=first+remember { derivedStateOf { state.layoutInfo.visibleItemsInfo.size+columnCount*2 } }.value
          for(i in first*columnCount until last*columnCount){
              if(!album.isEmpty()&&album.size()>i){
                  scrollInfo.firstVisibleItem=first
                  album.get(i).loadVideoDetails()
              }
          }
      }

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
        Column(modifier = Modifier
            .background(Color.Blue)
            .fillMaxHeight()
            .width(sliderWidth.value)
            .layoutId("options")) {
        }

        LazyVerticalGrid(columns = GridCells.Fixed(columnCount),
            state = state,
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center, modifier = Modifier.layoutId("itemList")){
            items(items= album.files, key = {file -> file.filePath}) { file->
                FileCard(file,context)
            }
            item {
                Spacer(modifier = Modifier.padding(20.dp))
            }
        }

        Row(modifier= Modifier
            .layoutId("fold")
            .background( color=Color.DarkGray, shape = CustomShape1)
            .clickable { slideOptions.value = !slideOptions.value }) {
            Image(painter = painterResource(id =if(slideOptions.value) R.drawable.ic_fold else R.drawable.ic_unfold),
                contentDescription ="fold", modifier = Modifier.size(40.dp))
            Text(text = if(slideOptions.value)"CLOSE" else "OPEN", color = Color.White,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically))
        }
    }




}

@Composable
fun FileCard(file:FileObjectViewModel,context: Context){
     Card(elevation = 5.dp, modifier = Modifier.padding(1.dp)) {
         Box {
             GlideImage(imageModel = file.filePath,
                 imageOptions = ImageOptions(alignment = Alignment.Center,
                     contentScale = ContentScale.Crop),
                 requestOptions = {
                     RequestOptions().placeholder(context.getDrawable(R.drawable.ic_play_video_dark)).override(100,100).diskCacheStrategy(
                         DiskCacheStrategy.ALL).centerCrop()
                 }, modifier = Modifier
                     .align(Alignment.Center)
                     .sizeIn(100.dp, 100.dp, Dp.Unspecified, Dp.Unspecified))

             Column(
                 modifier = Modifier
                     .align(Alignment.BottomStart)
                     .padding(start = 10.dp, bottom = 5.dp)) {

             }
             Text(text = file.directoryCount, maxLines = 1,
                 overflow = TextOverflow.Ellipsis,
                 modifier = Modifier
                     .align(Alignment.BottomStart)
                     .padding(start = 10.dp, bottom = 5.dp),
                 style = TextStyle(color= Color.Black,
                                   fontSize = 12.sp,
                                   fontWeight = FontWeight.Normal))
             Text(text = file.videoLength, maxLines = 1,
                 overflow = TextOverflow.Ellipsis,
                 modifier = Modifier
                     .align(Alignment.TopStart)
                     .padding(start = 10.dp, top = 5.dp),
                  style = TextStyle(color= Color.Black,
                     fontSize = 12.sp,
                     fontWeight = FontWeight.Normal))
         }
     }




}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ToolBar(context: Context){
    val scope= rememberCoroutineScope()
    val scaffoldState= rememberScaffoldState()
    Scaffold (
        scaffoldState=scaffoldState,
        drawerContent = {DrawerView()},
        topBar = {
        TopAppBar(title = { Text(text = "Compress")},
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
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", modifier = Modifier.padding(start = 20.dp,end=20.dp))
                Icon(Icons.Filled.MoreVert, contentDescription = "More",modifier = Modifier.padding(start = 20.dp,end=20.dp))
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
        val homeAlbum=AlbumViewModel()
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
        
        val  coroutineScope= rememberCoroutineScope()
        
        Button(onClick = { 
            coroutineScope.launch {
                pageState.animateScrollToPage(if(pageState.currentPage==0)1 else 0)
            }
        }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text(text = "Next Page")
        }
    }
}



