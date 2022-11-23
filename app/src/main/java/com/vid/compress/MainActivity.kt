package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vid.compress.storage.Disk

import com.vid.compress.ui.callbacks.LoadingCompleteListener
import com.vid.compress.ui.dialogs.ConfirmDeleteDialog
import com.vid.compress.ui.dialogs.PropertiesDialog
import com.vid.compress.ui.dialogs.SortByAlertDialog
import com.vid.compress.ui.models.FileObjectViewModel
import com.vid.compress.ui.models.AlbumViewModel
import com.vid.compress.ui.models.UserSettingsModel
import com.vid.compress.ui.pages.*
import com.vid.compress.ui.theme.*
import kotlinx.coroutines.*
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserSettingsModel.darkModeEnabled=UserSettingsModel.isDarkModeEnabled(this)
        setContent {
            VideoCompressorTheme(darkTheme =  UserSettingsModel.darkModeEnabled,this) {
                Surface(modifier = Modifier.fillMaxSize(), elevation = 1.dp) {
                    ToolBar(this)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

      /*  if(data==null)
            return

        if(requestCode==32){
            val uri=data.data
            val document= uri?.let { DocumentFile.fromTreeUri(applicationContext, it) }
            if(document==null||!document.canWrite()){
                Toast.makeText(applicationContext,"Failed to Grant Permission!",Toast.LENGTH_LONG).show()
                return
            }

           val storage=Disk.getStorage(applicationContext,document.name)

            grantUriPermission(packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val editor = getSharedPreferences("StorageUri", MODE_PRIVATE).edit().apply {
                putString(storage.path,uri.path)
                apply()
            }
            Toast.makeText(this,"Permission Granted!",Toast.LENGTH_SHORT).show()
        }*/
    }
}


private val home=AlbumViewModel()
private val album=AlbumViewModel()
private val history=AlbumViewModel()
private var currentAlbumView=0

@SuppressLint("UnrememberedMutableState")
@Composable
fun FileList(album:AlbumViewModel,context: Context){
    UserSettingsModel.columnCount=UserSettingsModel.columnCount(context)
    val slideOptions= remember { mutableStateOf(false) }
    val state= rememberLazyGridState()
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

        FileOperationLayout(home,context,sliderWidth)

        LazyVerticalGrid(columns =
        when(UserSettingsModel.columnCount){
                "1"-> GridCells.Fixed(1)
                "2"-> GridCells.Fixed(2)
                "3"-> GridCells.Fixed(3)
                "4"-> GridCells.Fixed(4)
            else -> GridCells.Adaptive(100.dp)
        } ,
            state = state,
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center, modifier = Modifier.layoutId("itemList")){
            itemsIndexed(items = album.files){index,file->
                FileCard(file,context,album)
            }
            item {
                Spacer(modifier = Modifier.padding(20.dp))
            }
        }

        if(home.isSelectActive()) {
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
        .padding(top = 10.dp),constraintSet = constraints) {
        hideMenu.value= home.isSelectActive()||album.isSelectActive()|| history.isSelectActive()
        Image(painterResource(id = R.drawable.ic_close),
            contentDescription = "close", modifier = Modifier
                .size(50.dp)
                .layoutId("close")
                .padding(start = 10.dp)
                .clip(shape = CircleShape)
                .clickable {
                    hideMenu.value = false
                    home.clearSelected()
                    album.clearSelected()
                    history.clearSelected()
                })
        Text(text = "Selected Items("+ (home.selected.size+album.selected.size+ history.selected.size)+")",
            style = TextStyle(color =Color.White,
                fontSize = 12.sp,fontWeight = FontWeight.Bold),
            modifier = Modifier
                .layoutId("selectInfo")
                .padding(start = 13.dp))


        Row(modifier= Modifier
            .layoutId("modify")
            .clip(shape = CircleShape)
            .clickable {
                if (!home.isSelectActive() && !album.isSelectActive() && !history.isSelectActive())
                    return@clickable
                val intent = Intent(
                    context.applicationContext,
                    Class.forName("com.vid.compress.ShrinkActivity")
                )
                val array = ArrayList<String>()
                for (i in 0 until home.selected.size)
                    array.add(home.selected[i].filePath)
                // album may contain folders so ignore folders
                for (i in 0 until album.selected.size) {
                    val file = File(album.selected[i].filePath)
                    if (!file.isFile) continue
                    array.add(file.path)
                }

                for (i in 0 until history.selected.size)
                    array.add(history.selected[i].filePath)
                //sends data to the shrinkActivity class
                if (array.isNotEmpty()) {
                    intent.putStringArrayListExtra("selected", array)
                    context.startActivity(intent)
                }
                home.clearSelected()
                album.clearSelected()
                history.clearSelected()
            }
            .padding(10.dp)) {
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


@Composable
fun AlbumImageView(file:FileObjectViewModel,modifier: Modifier){
    if(file.isBitmapReady){
        Image(bitmap= file.thumbnail, contentDescription ="thumbnail",
            modifier = modifier, filterQuality = FilterQuality.Medium, contentScale = ContentScale.Crop)
    }else{
        Image(painter = painterResource(id = R.drawable.ic_play_video_dark), contentDescription ="thumbnail",
            modifier = modifier,contentScale = ContentScale.Crop)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileCard(file: FileObjectViewModel, context: Context, album: AlbumViewModel){

         Box(modifier = Modifier
             .padding(2.dp)
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

            AlbumImageView(file, modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth())
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

                    when(currentAlbumView){
                        0->{
                            home.activateSearch = !home.activateSearch
                            home.restoreFilter()
                        }
                        1->{
                            album.activateSearch = !album.activateSearch
                            album.restoreFilter()
                        }
                        2->{
                            history.activateSearch = !history.activateSearch
                            history.restoreFilter()
                        }
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

            when(currentAlbumView){
                0-> home.filter(phrase = searchPhrase.value)
                1-> album.filter(phrase = searchPhrase.value)
                2-> history.filter(phrase = searchPhrase.value)

            }
    }))
}
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ToolBar(context: Activity){
    val scope= rememberCoroutineScope()
    val scaffoldState= rememberScaffoldState()
    Scaffold (
        scaffoldState=scaffoldState,
        drawerContent = {DrawerView(context)},
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
                if(home.activateSearch|| album.activateSearch|| history.activateSearch) {
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
                                    when (currentAlbumView) {
                                        0 -> home.activateSearch = !home.activateSearch
                                        1 -> album.activateSearch = !album.activateSearch
                                        2 -> history.activateSearch = !history.activateSearch
                                    }
                                })
                        Icon(
                            painterResource(id = R.drawable.ic_sort),
                            contentDescription = "Sort",
                            modifier = Modifier
                                .padding(start = 20.dp, end = 10.dp)
                                .clickable {
                                    //open sort file dialog
                                    when (currentAlbumView) {
                                        0 -> home.showSortOrderDialog.value = true
                                        1 -> album.showSortOrderDialog.value = true
                                        2 -> history.showSortOrderDialog.value = true
                                    }
                                })
                    }})

        }){


      HorizontalPagerView(context)
      PropertiesDialog(home , album, history){
         album.showProperties.value=false
         home.showProperties.value=false
         history.showProperties.value=false
      }
      SortByAlertDialog(home){
          home.showSortOrderDialog.value=false
          home.sort()
      }
      SortByAlertDialog(album){
          album.showSortOrderDialog.value=false
          album.sort()
      }
      SortByAlertDialog(history){
          history.showSortOrderDialog.value=false
          history.sort()
      }

     ConfirmDeleteDialog(album = home) {
         response->
         if(response){
             home.deleteSelectedFiles(context)
         }
        home.showDeleteDialog.value=false
     }
     ConfirmDeleteDialog(album = album) {
         response->
         if(response){
             album.deleteSelectedFiles(context)
         }
         album.showDeleteDialog.value=false
     }
     ConfirmDeleteDialog(album = history) {
         response->
         if(response){
             history.deleteSelectedFiles(context)
         }
         history.showDeleteDialog.value=false
     }
    }
}


@Composable
fun LoadingView(modifier:Modifier){
    Card(elevation = 10.dp, modifier =modifier) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 15.dp)) {
            CircularProgressIndicator( color = ColorTheme1,
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
    val historyState= rememberLazyListState()

    val homeLoadingVisible=remember{ mutableStateOf(false)}
    val albumLoadingVisible= remember { mutableStateOf(false) }
    val historyLoadingVisible=remember{ mutableStateOf(false) }

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
        val size=3
        HorizontalPager(count =size,
                        state = pageState, modifier = Modifier.layoutId("pagerLayout")) { currentPage->
            when(currentPage){
                
                0-> {
                    if (!home.isLoaded)
                        home.fetchFiles(
                            context = context,
                            foldersOnly = false,
                            listener = object : LoadingCompleteListener {
                                override fun finished() {
                                    homeLoadingVisible.value = false
                                }

                                override fun started() {
                                    homeLoadingVisible.value = true
                                }

                            })
                    album.activateSearch = false
                    album.restoreFilter()
                    history.activateSearch = false
                    history.restoreFilter()
                    currentAlbumView = pageState.currentPage

                    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = false),
                        onRefresh = {

                            // load only when no other thread is active to prevent memory leaks
                           if(!homeLoadingVisible.value) {
                               //in case the user refreshed the view while select is on clear select
                               home.clearSelected()
                               home.fetchFiles(
                                   context = context,
                                   foldersOnly = false,
                                   listener = object : LoadingCompleteListener {
                                       override fun finished() {
                                           homeLoadingVisible.value = false
                                       }

                                       override fun started() {
                                           homeLoadingVisible.value = true
                                       }
                                   })
                                }
                        }) {
                        Box(modifier = Modifier.fillMaxSize()) {


                            FileList(home, context)
                            //loading indicator
                            if (homeLoadingVisible.value)
                                LoadingView(
                                    modifier = Modifier
                                        .width(250.dp)
                                        .padding(10.dp)
                                        .align(Alignment.Center)
                                )

                        }

                    }
                }
                
                1->{

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
                    history.activateSearch=false
                    history.restoreFilter()
                    currentAlbumView=pageState.currentPage
                    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = false),
                        onRefresh = {
                            // load only when no other thread is active to prevent memory leaks
                            if(!albumLoadingVisible.value) {
                                //in case the user refreshed the view while select is on clear select
                                album.clearSelected()
                                album.fetchFiles(
                                    context = context,
                                    listener = object : LoadingCompleteListener {
                                        override fun finished() {
                                            albumLoadingVisible.value = false
                                        }

                                        override fun started() {
                                            albumLoadingVisible.value = true
                                        }
                                    })
                            } }) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            albumList(context, album, albumState)
                            //loading indicator
                            if (albumLoadingVisible.value)
                                LoadingView(
                                    modifier = Modifier
                                        .width(250.dp)
                                        .padding(10.dp)
                                        .align(Alignment.Center)
                                )
                        }
                    }

                }

                2->{
                    if(!history.isLoaded) {
                        history.fetchFiles(Disk.getDefaultAppFolder(context), context,
                            listener = object : LoadingCompleteListener {
                                override fun finished() {
                                    historyLoadingVisible.value = false
                                }

                                override fun started() {
                                    historyLoadingVisible.value = true
                                }
                            })
                    }
                    //in case the search was active for previous window
                    album.activateSearch=false
                    album.restoreFilter()
                    home.activateSearch=false
                    home.restoreFilter()
                    currentAlbumView=pageState.currentPage
                    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = false),
                        onRefresh = {
                            // load only when no other thread is active to prevent memory leaks
                            if(!historyLoadingVisible.value) {
                                //in case the user refreshed the view while select is on clear select
                                history.clearSelected()
                                history.fetchFiles(Disk.getDefaultAppFolder(context),
                                    context,
                                    listener = object : LoadingCompleteListener {
                                        override fun finished() {
                                            historyLoadingVisible.value = false
                                        }
                                        override fun started() {
                                            historyLoadingVisible.value = true
                                        }
                                    })

                            }

                        }) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            HistoryList(context, history, historyState)
                            //loading indicator
                            if (historyLoadingVisible.value)
                                LoadingView(
                                    modifier = Modifier
                                        .width(250.dp)
                                        .padding(10.dp)
                                        .align(Alignment.Center)
                                )
                        }
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
                        pageState.animateScrollToPage(2)
                    }}){
                    Text(text = "History", modifier = Modifier.padding(top = 15.dp, bottom = 15.dp),color = Color.White)
                }
            }
    }
}



