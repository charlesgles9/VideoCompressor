package com.vid.compress

import android.annotation.SuppressLint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vid.compress.permisions.PermissionHelper
import com.vid.compress.storage.FileUtility
import com.vid.compress.ui.page.DrawerView
import com.vid.compress.ui.page.HorizontalPagerContent
import com.vid.compress.ui.page.albumList
import com.vid.compress.ui.theme.VideoCompressorTheme
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHelper.grantStorageReadWrite(this)
        val map=FileUtility.fetchVideos(this)
        val it=map.iterator()
            it.next()
        val array=it.next().value

        setContent {
            VideoCompressorTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
                  toolBar(files = array)
                }
            }


        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun fileList(files:MutableList<File>){
    val state=rememberLazyListState()
    LazyVerticalGrid(cells = GridCells.Fixed(3),
        state = state,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)){

        items(files) {file->
            fileCard(file)

        }

        item {
            Spacer(modifier = Modifier.padding(100.dp))
        }
    }

}

@Composable
fun fileCard(file:File){

        Column{
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Icon",
                modifier = Modifier
                    .border(5.dp, MaterialTheme.colors.primary))
            Text(text = file.name)

    }

}

@Composable
fun toolBar(files: MutableList<File>){
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

      HorizontalPagerView(files)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerView(files: MutableList<File>){
    Box(modifier = Modifier.fillMaxSize()) {
        
        //create pages
        val size=2
        val pageState= rememberPagerState()
        HorizontalPager(count =size,
                        state = pageState) { currentPage->
            when(currentPage){
                
                0->{
                    fileList(files = files)
                }
                
                1->{
                    albumList(mutableList = files)
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



