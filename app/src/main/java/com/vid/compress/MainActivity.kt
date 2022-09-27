package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vid.compress.permisions.PermissionHelper
import com.vid.compress.ui.callbacks.LoadingCompleteListener
import com.vid.compress.ui.models.FileObjectViewModel
import com.vid.compress.ui.models.AlbumViewModel
import com.vid.compress.ui.pages.DrawerView
import com.vid.compress.ui.pages.ScrollInfo
import com.vid.compress.ui.pages.albumList
import com.vid.compress.ui.theme.*
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHelper.grantStorageReadWrite(this)



        setContent {
            VideoCompressorTheme(darkTheme = false,this) {
                Surface(modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
                    ToolBar(this)


                }
            }
        }
    }

}


private val home=AlbumViewModel()
private val album=AlbumViewModel()
private var currentAlbumView=0
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


            Text(text = "Items: " + home.selected.size + "/" + home.size(),
                modifier = Modifier
                    .padding(bottom = 50.dp, top = 10.dp)
                    .align(Alignment.CenterHorizontally),
                style= TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold))

            Box(modifier= Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .clickable {
                    val intent = Intent(
                        context.applicationContext,
                        Class.forName("com.vid.compress.VideoPlayerActivity")
                    )

                    val list = ArrayList<String>()
                    if (currentAlbumView == 0) {
                        home.selected.forEach { file ->
                            list.add(file.filePath)
                        }
                    } else if (currentAlbumView == 1) {
                        album.selected.forEach { file ->
                            list.add(file.filePath)
                        }
                    }
                    intent.putStringArrayListExtra("uriList", list)
                    context.startActivity(intent)

                }
                .background(color = IconBackground, shape = CustomShape2) ) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = "PlayAll",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp), tint = Color.White)
            }

            Box(modifier= Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .clickable {
                    if (currentAlbumView == 0)
                        home.selectAll()
                    else if (currentAlbumView == 1)
                        album.selectAll()
                }
                .background(color = IconBackground, shape = CustomShape2) ) {
                Icon(painter = painterResource(id = R.drawable.ic_select_all), contentDescription = "selectAll",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp), tint = Color.White)
            }

            Box(modifier= Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .clickable { /*send intent*/ }
                .background(color = IconBackground, shape = CustomShape2) ) {
                Icon(Icons.Outlined.Send, contentDescription = "send",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp), tint = Color.White)
            }



            Box(modifier= Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .clickable { /*share intent*/ }
                .background(color = IconBackground, shape = CustomShape2) ) {
                Icon(Icons.Outlined.Share, contentDescription = "share",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),tint = Color.White)
            }

            Box(modifier= Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .clickable { /*file info*/ }
                .background(color = IconBackground, shape = CustomShape2) ) {
                Icon(Icons.Outlined.Info, contentDescription = "Info",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),tint = Color.White)
            }

            Box(modifier= Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .clickable { /*delete file*/ }
                .background(color = IconBackground, shape = CustomShape2) ) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),tint = Color.White)
            }


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

        if(home.isSelectActive())
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

        hideMenu.value= home.isSelectActive()
        Image(painterResource(id = R.drawable.ic_close),
            contentDescription = "close", modifier = Modifier
                .size(50.dp)
                .layoutId("close")
                .padding(start = 10.dp)
                .clickable {
                    hideMenu.value = false
                    home.clearSelected()
                })
        Text(text = "Selected Items("+ home.selected.size+")",
            style = TextStyle(color =Color.White,
                fontSize = 12.sp,fontWeight = FontWeight.Bold),
            modifier = Modifier
                .layoutId("selectInfo")
                .padding(start = 13.dp))

        Row(modifier= Modifier
            .layoutId("modify")
            .clickable {
                if (!home.isSelectActive())
                    return@clickable
                val intent = Intent(
                    context.applicationContext,
                    Class.forName("com.vid.compress.ShrinkActivity")
                )
                val array = ArrayList<String>()
                for (i in 0 until home.selected.size)
                    array.add(home.selected[i].filePath)
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
fun FileCard(file: FileObjectViewModel, context: Context, album: AlbumViewModel){
         Box(modifier = Modifier
             .padding(1.dp)
             .combinedClickable(onClick = {
                 file.selected = !file.selected
                 if (file.selected) {
                     album.addSelectFile(file)
                 } else {
                     album.removeSelectFile(file)
                 }

             }, onLongClick = {

                 val intent = Intent(context, Class.forName("com.vid.compress.VideoPlayerActivity"))
                 val list = ArrayList<String>()
                 list.add(file.filePath)
                 intent.putStringArrayListExtra("uriList", list)
                 context.startActivity(intent)
             })
             .border(width = 5.dp, color = if (file.selected) SelectColor else Color.Transparent)) {

             if(file.thumbnailLoaded){
                 Image(bitmap = file.thumbnail, contentDescription ="thumbnail",
                     modifier = Modifier
                         .align(Alignment.Center)
                         .fillMaxWidth(), contentScale = ContentScale.Crop)
             }else{
                 Image(painter = painterResource(id = R.drawable.ic_play_video_dark), contentDescription ="thumbnail",
                     modifier = Modifier
                         .align(Alignment.Center)
                         .fillMaxWidth(), contentScale = ContentScale.Crop)
             }
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
      file.loadBitmap(context)

}

@Composable
fun SearchView(){
    val searchPhrase= remember { mutableStateOf("") }
    OutlinedTextField(value = searchPhrase.value,
        onValueChange = { value -> searchPhrase.value = value },
        label = { Text(text = "search") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        trailingIcon = {
            Icon(Icons.Filled.Close, contentDescription = "search",
                modifier = Modifier.clickable {
                    if(currentAlbumView==0) {
                        home.activateSearch = !home.activateSearch
                        home.restoreFilter()
                    }else if(currentAlbumView==1){
                        album.activateSearch = !album.activateSearch
                        album.restoreFilter()
                    }
                })
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        singleLine = true,
        shape = CircleShape, colors =
        TextFieldDefaults.textFieldColors(textColor = Color.White, cursorColor = SelectColor,
         unfocusedLabelColor = Color.White, focusedLabelColor = Color.White, focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor =Color.Transparent, leadingIconColor = Color.White, trailingIconColor = Color.White )
    , keyboardActions = KeyboardActions(onSearch = {
            if(currentAlbumView==0)
            home.filter(phrase = searchPhrase.value)
             else
                if(currentAlbumView==1)
            album.filter(phrase = searchPhrase.value)

    })
    )
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
                if(home.activateSearch|| album.activateSearch) {
                    SearchView()
                }else{
                        Icon(
                            painter = painterResource(id = R.drawable.ic_videocam),
                            contentDescription = "Play",
                            modifier = Modifier.padding(start = 10.dp, end = 20.dp))
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp)
                                .clickable {
                                    if (currentAlbumView == 0)
                                        home.activateSearch = !home.activateSearch
                                    else
                                        if (currentAlbumView == 1)
                                            album.activateSearch = !album.activateSearch
                                })
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More",
                            modifier = Modifier.padding(start = 20.dp, end = 10.dp))
                    }})

        }){


      HorizontalPagerView(context)
    }
}


@Composable
fun loadingView(modifier:Modifier){
    Card(elevation = 10.dp, modifier =modifier) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 15.dp)) {
            CircularProgressIndicator( color = LighterBlue,
                modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerView(context: Context){
    val  coroutineScope= rememberCoroutineScope()
    val pageState= rememberPagerState()
    val albumState= rememberLazyListState()
    val homeState= rememberLazyGridState()
    val homeLoadingVisible=remember{ mutableStateOf(false)}
    val albumLoadingVisible= remember { mutableStateOf(false) }
    val constraints= ConstraintSet {
        val pagerLayout=createRefFor("pagerLayout")
        val pagerTabs=createRefFor("pagerTabs")
        constrain(pagerLayout){
            top.linkTo(pagerTabs.bottom)
        }
        constrain(pagerTabs){
            top.linkTo(parent.top)
        }

    }

    ConstraintLayout(modifier = Modifier.fillMaxSize(), constraintSet = constraints) {
        
        //create pages
        val size=2
        val homeScrollInfo=ScrollInfo(-1)
        HorizontalPager(count =size,
                        state = pageState, modifier = Modifier.layoutId("pagerLayout")) { currentPage->
            when(currentPage){
                
                0->{
                    if(!home.isLoaded)
                    home.fetchFiles(context=context,foldersOnly = false, listener = object :LoadingCompleteListener{
                        override fun finished() {
                            homeLoadingVisible.value=false
                        }

                        override fun started() {
                            homeLoadingVisible.value=true
                        }

                    })
                    album.activateSearch=false
                    album.restoreFilter()
                    currentAlbumView=pageState.currentPage
                    Box(modifier = Modifier.fillMaxSize()){

                        FileList(home,homeState,homeScrollInfo,context)
                        //loading indicator
                        if(homeLoadingVisible.value)
                           loadingView( modifier = Modifier
                               .width(250.dp)
                               .padding(10.dp)
                               .align(Alignment.Center))

                    }

                }
                
                1->{
                    val scrollInfo=ScrollInfo(-1)
                    if(!album.isLoaded)
                        album.fetchFiles(context=context,listener = object :LoadingCompleteListener{
                            override fun finished() {
                             albumLoadingVisible.value=false
                            }

                            override fun started() {
                                albumLoadingVisible.value=true
                            }
                        })
                    home.activateSearch=false
                    home.restoreFilter()
                    currentAlbumView=pageState.currentPage
                    Box(modifier=Modifier.fillMaxSize()) {
                        albumList(context,album,albumState,scrollInfo)
                        //loading indicator
                        if(albumLoadingVisible.value)
                            loadingView( modifier = Modifier
                                .width(250.dp)
                                .padding(10.dp)
                                .align(Alignment.Center))
                    }

                }
            }
        }


            TabRow(selectedTabIndex = pageState.currentPage,
                indicator ={ tabPositions ->
                    TabRowDefaults.Indicator(modifier = Modifier.tabIndicatorOffset(tabPositions[pageState.currentPage]))
                }, modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("pagerTabs"), contentColor = SelectColor) {
                Tab(selected = pageState.currentPage==0,
                    onClick = {
                        coroutineScope.launch {
                        pageState.animateScrollToPage(0)
                    }}){
                    Text(text = "HomePage",
                        modifier = Modifier.padding(top = 15.dp, bottom = 15.dp), color = Color.White)
                }

                Tab(selected = pageState.currentPage==1,
                    onClick = {coroutineScope.launch {
                        pageState.animateScrollToPage(1)
                    }}){
                    Text(text = "Albums", modifier = Modifier.padding(top = 15.dp, bottom = 15.dp),color = Color.White)
                }
                Tab(selected = pageState.currentPage==1,
                    onClick = {coroutineScope.launch {
                        pageState.animateScrollToPage(1)
                    }}){
                    Text(text = "History", modifier = Modifier.padding(top = 15.dp, bottom = 15.dp),color = Color.White)
                }
            }



    }
}



