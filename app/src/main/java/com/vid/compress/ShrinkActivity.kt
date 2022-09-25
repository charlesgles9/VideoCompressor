package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.glide.GlideImage
import com.vid.compress.storage.Disk
import com.vid.compress.ui.models.FileObjectViewModel
import com.vid.compress.storage.FileUtility
import com.vid.compress.ui.theme.VideoCompressorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class ShrinkActivity:ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selected=intent.getStringArrayListExtra("selected")
        val files=ArrayList<FileObjectViewModel>()
        if(selected!=null)
        for(i in 0 until selected.size) files.add(FileObjectViewModel(selected[i]))
        else
            finish()
        setContent {
            VideoCompressorTheme(darkTheme = false,this) {
                Surface(modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.medium) {
                    Toolbar(context = this,files)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Toolbar(context:Activity,files:ArrayList<FileObjectViewModel>){
    val scope= rememberCoroutineScope()
    val scaffoldState= rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState,
             topBar = { TopAppBar(title = { Text(text = "Shrink")},
                 navigationIcon ={ IconButton(onClick = { context.finish() }) {
                     Icon(Icons.Filled.ArrowBack, contentDescription ="exitActivity" )
                 }} )
             }) {
              PagerView(files = files,context)
    }
}


@Composable
fun VideoDetails(file: FileObjectViewModel, context:Context){
    Column(modifier = Modifier.fillMaxWidth()) {

        Card(elevation = 10.dp, modifier = Modifier.padding(5.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)) {
            Box(Modifier.fillMaxWidth()) {
                GlideImage(
                    imageModel = file.filePath, modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .align(Alignment.Center)
                        .padding(5.dp), requestOptions = {
                        RequestOptions().override(300,300).diskCacheStrategy(
                            DiskCacheStrategy.ALL).fitCenter() })
                Image(painter = painterResource(
                    id = R.drawable.ic_play_video_dark),
                    contentDescription = "fileThumbnail",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(60.dp)
                        .align(Alignment.Center))
            }

            Divider(color = MaterialTheme.colors.onSecondary, thickness = 0.5.dp)
            Text(
                text = file.filePath,
                style = TextStyle(
                    MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                    fontWeight = FontWeight.Light), modifier = Modifier.padding(5.dp))

            Divider(color = MaterialTheme.colors.onSecondary, thickness = 0.5.dp)
            Text(
                text = "Resolution: "+file.videoResolution.first+"*"+file.videoResolution.second+"|"+FileUtility.getExtension(file.fileName),
                style = TextStyle(
                    MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                    fontWeight = FontWeight.Light), modifier = Modifier.padding(5.dp))
            Divider(color = MaterialTheme.colors.onSecondary, thickness = 0.5.dp)
            Text(
                text ="Size: "+ Disk.getSize(File(file.filePath).length()),
                style = TextStyle(
                    MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                    fontWeight = FontWeight.Light), modifier = Modifier.padding(5.dp))
            }
        }
    }

    file.fetchVideoResolution()

}



@Composable
fun ResolutionPicker(){
    val constants= listOf("Original","Medium Quality","Low Quality",
          "240x180","360x270","480x360","640x480","800x600","1024x768","1280x960")
    var expanded by remember{ mutableStateOf(false) }
    var selected by remember { mutableStateOf("Original")}
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp), elevation = 10.dp) {

        val constraints= ConstraintSet{
               val title=createRefFor("title")
               val dropDownPlaceHolder=createRefFor("dropDownPlaceHolder")
               val dropDownLayout=createRefFor("dropDownLayout")
              constrain(title){
                  start.linkTo(parent.start)
                  top.linkTo(parent.top)
              }
            constrain(dropDownPlaceHolder){
                top.linkTo(title.bottom)
            }
            constrain(dropDownLayout){
                top.linkTo(dropDownPlaceHolder.bottom)
                start.linkTo(dropDownPlaceHolder.end)
            }
        }
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp), constraintSet = constraints) {
        Text(
            text = "Select Resolution", style = TextStyle(
                MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                fontWeight = FontWeight.Light), modifier = Modifier
                .padding(5.dp)
                .layoutId("title"))
          Row(modifier = Modifier
              .width(150.dp)
              .clickable { expanded = !expanded }
              .shadow(1.dp, shape = RoundedCornerShape(5.dp))
              .padding(5.dp)
              .layoutId("dropDownPlaceHolder")) {

              Icon(Icons.Filled.KeyboardArrowDown, contentDescription ="ArrowDown",
                  modifier = Modifier
                      .padding(end = 1.dp)
                      .align(Alignment.CenterVertically) )
              Text(
                  text = selected,
                  style = TextStyle(
                      MaterialTheme.colors.onSecondary, fontSize = 13.sp,
                      fontWeight = FontWeight.Light), modifier = Modifier
                      .padding(1.dp)
                      .align(Alignment.CenterVertically) )
          }

            DropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(150.dp)
                    .height(300.dp)
                    .layoutId("dropDownLayout")) {
                constants.forEach { label ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        selected=label
                    }) {
                        Text(
                            text = label, style = TextStyle(
                                MaterialTheme.colors.onSecondary, fontSize = 11.sp,
                                fontWeight = FontWeight.Light
                            ), modifier = Modifier.padding(5.dp))
                    } }
            }
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalTabs(files:ArrayList<FileObjectViewModel>, state:PagerState, scope:CoroutineScope){

    ScrollableTabRow(selectedTabIndex = state.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(modifier =
            Modifier.tabIndicatorOffset(tabPositions[state.currentPage]).padding(5.dp)) }
           , backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colors.primary, edgePadding = 5.dp) {
        files.forEachIndexed { index, file ->
            Tab(selected = state.currentPage == index, onClick = {
                scope.launch {
                    state.animateScrollToPage(index)
                }
            }) {
                PagerTabs(file = file)
            }
        }

    }


}

@Composable
fun PagerTabs(file: FileObjectViewModel){

    Card(modifier = Modifier.padding(2.dp), elevation = 5.dp) {
        GlideImage(
            imageModel = file.filePath, modifier = Modifier
                .size(80.dp)
                .padding(5.dp), requestOptions = {
                RequestOptions().override(90,90).diskCacheStrategy(
                    DiskCacheStrategy.ALL).fitCenter() })
    }

}

@Composable
fun VideoLayout(file: FileObjectViewModel, context: Context){
    val state=LazyListState()
   LazyColumn(state =state  ){
       item {
           VideoDetails(file,context)
       }

       item{
           ResolutionPicker()
       }

   }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerView(files:ArrayList<FileObjectViewModel>, context: Context){
    val pageState= rememberPagerState()
    val scope= rememberCoroutineScope()
    Column {
        HorizontalPager(count = files.size, state = pageState) { currentPage ->
            VideoLayout(file = files[currentPage], context)
        }
       HorizontalTabs(files = files, state =pageState , scope =scope )
    }

}