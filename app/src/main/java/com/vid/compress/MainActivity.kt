package com.vid.compress

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    toolBar(this)
                }
            }
        }
    }
}




@SuppressLint("UnrememberedMutableState")
@Composable
fun fileList(album:AlbumViewModel,state:LazyGridState,scrollInfo: ScrollInfo){

      val columnCount=3
      val first by derivedStateOf { state.firstVisibleItemIndex}

      if(!scrollInfo.isEqualTo(first)){
          val last=first+state.layoutInfo.visibleItemsInfo.size+columnCount*2
          for(i in first*columnCount until last*columnCount){
              if(!album.isEmpty()&&album.size()>i){
                  scrollInfo.firstVisibleItem=first
                //  album.get(i).loadThumbnail()
                  album.get(i).setDirCount()
                  album.get(i).setVideoLength()
              }
          }
      }
    LazyVerticalGrid(columns = GridCells.Fixed(3),
        state = state,
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center){

        items(items= album.files) { file->
            fileCard(file)

        }

        item {
            Spacer(modifier = Modifier.padding(20.dp))
        }
    }

}

@Composable
fun fileCard(file:FileObjectViewModel){


     Card(elevation = 5.dp, modifier = Modifier.padding(3.dp)) {
         Box {

             /*GlideImage(imageModel = file.filePath,
                 imageOptions = ImageOptions(alignment = Alignment.Center,
                     contentScale = ContentScale.Crop)
             )*/


             if(file.thumbnailLoader.thumbnail!=null) {
                 Image(
                     bitmap = file.thumbnailLoader.thumbnail!!,
                     contentDescription = "Icon",
                     modifier = Modifier
                         .align(Alignment.Center)
                         .padding(2.dp)
                         .size(100.dp))
             }else{
                 Image(
                     painter = painterResource(id = R.drawable.ic_play_video_dark),
                     contentDescription = "Icon",
                     modifier = Modifier
                         .align(Alignment.Center)
                         .padding(2.dp)
                         .size(100.dp))
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
fun toolBar(context: Context){
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
                  elevation = 10.dp, actions = {
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
        HorizontalPager(count =size,
                        state = pageState) { currentPage->
            when(currentPage){
                
                0->{
                    val state= rememberLazyGridState()
                    val album=AlbumViewModel()
                    val scrollInfo=ScrollInfo(-1)
                    album.fetchFiles(context=context,foldersOnly = false)
                    fileList(album,state,scrollInfo)
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



