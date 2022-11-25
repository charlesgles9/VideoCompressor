package com.vid.compress.ui.pages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
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
import com.vid.compress.services.DataBridge
import com.vid.compress.services.ShrinkService
import com.vid.compress.storage.Disk
import com.vid.compress.storage.FileUtility

import com.vid.compress.ui.models.VideoCompressModel
import com.vid.compress.ui.theme.LighterBlue
import com.vid.compress.ui.theme.lightRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File





@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ShrinkToolbar(context: Activity, files:ArrayList<VideoCompressModel>){
    val scope= rememberCoroutineScope()
    val scaffoldState= rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text(text = "Shrink") },
            navigationIcon ={ IconButton(onClick = { context.finish() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription ="exitActivity" )
            }
            } )
        }) {
        PagerView(files = files,context)
    }
}


@Composable
fun VideoDetails(videoModel: VideoCompressModel, context: Context){
    Column(modifier = Modifier.fillMaxWidth()) {

        Card(elevation = 10.dp, modifier = Modifier.padding(5.dp)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    GlideImage(
                        imageModel = videoModel.file.filePath, modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .align(Alignment.Center)
                            .padding(5.dp), requestOptions = {
                            RequestOptions().override(300,300).diskCacheStrategy(
                                DiskCacheStrategy.ALL).fitCenter() })
                    Image(painter = painterResource(
                        id = com.vid.compress.R.drawable.ic_play_video_dark),
                        contentDescription = "fileThumbnail",
                        modifier = Modifier
                            .padding(5.dp)
                            .size(60.dp)
                            .align(Alignment.Center))
                }

                Divider(color = MaterialTheme.colors.onSecondary, thickness = 0.5.dp)
                Text(
                    text = videoModel.file.filePath,
                    style = TextStyle(
                        MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                        fontWeight = FontWeight.Light), modifier = Modifier.padding(5.dp))

                Divider(color = MaterialTheme.colors.onSecondary, thickness = 0.5.dp)
                Text(
                    text = "Resolution: "+videoModel.file.originalResolution.first+"*"+videoModel.file.originalResolution.second+"|"+
                            FileUtility.getExtension(videoModel.file.fileName),
                    style = TextStyle(
                        MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                        fontWeight = FontWeight.Light), modifier = Modifier.padding(5.dp))
                Divider(color = MaterialTheme.colors.onSecondary, thickness = 0.5.dp)
                Text(
                    text ="Size: "+ Disk.getSize(File(videoModel.file.filePath).length()),
                    style = TextStyle(
                        MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                        fontWeight = FontWeight.Light), modifier = Modifier.padding(5.dp))
            }
        }
    }

    videoModel.file.fetchVideoResolution()

}



@Composable
fun ResolutionPicker(videoModel: VideoCompressModel){
    val constants= listOf("Original","Quality Very Low","Quality Low",
        "Quality Medium","Quality High","Quality Very High",
        "240x136","240x180","360x270","480x360","640x360","640x480","800x600","1024x576","1024x768","1280x960",
        "1600x900","1920x1080","2048x1152","2560x1152","2560x1440","4096x2304")
    var expanded by remember{ mutableStateOf(false) }
    Card(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp), elevation = 10.dp) {


        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)) {
            Text(
                text = "Select Resolution", style = TextStyle(
                    MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                    fontWeight = FontWeight.Light), modifier = Modifier
                    .padding(5.dp))
            Row(modifier = Modifier
                .width(150.dp)
                .clickable { expanded = !expanded }
                .shadow(1.dp, shape = RoundedCornerShape(5.dp))
                .padding(5.dp)) {

                Icon(
                    Icons.Filled.KeyboardArrowDown, contentDescription ="ArrowDown",
                    modifier = Modifier
                        .padding(end = 1.dp)
                        .align(Alignment.CenterVertically) )
                Text(
                    text = videoModel.selectedResolution,
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
                    .height(300.dp)) {
                constants.forEach { label ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        videoModel.selectedResolution=label
                    }) {
                        Text(
                            text = label, style = TextStyle(
                                MaterialTheme.colors.onSecondary, fontSize = 11.sp,
                                fontWeight = FontWeight.Light
                            ), modifier = Modifier.padding(5.dp))
                    } }
            }
            Row(modifier = Modifier.fillMaxWidth()){
                Text(
                    text = "Disable Audio", style = TextStyle(
                        MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                        fontWeight = FontWeight.Light), modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.CenterVertically)
                        .width(100.dp))
                Checkbox(checked =videoModel.disableAudio , onCheckedChange = {value->
                    videoModel.disableAudio=value
                },colors= CheckboxDefaults.colors(checkedColor = LighterBlue))
            }
            Row(modifier = Modifier.fillMaxWidth()){
                Text(
                    text = "min BitRate", style = TextStyle(
                        MaterialTheme.colors.onSecondary, fontSize = 12.sp,
                        fontWeight = FontWeight.Light), modifier = Modifier
                        .padding(5.dp)
                        .width(100.dp)
                        .align(Alignment.CenterVertically))
                Checkbox(checked =videoModel.minBitRateEnabled , onCheckedChange = {value->
                    videoModel.minBitRateEnabled =value
                },colors= CheckboxDefaults.colors(checkedColor = LighterBlue))
                Text(
                    text = "/*caps the minimum bitrate to 2mbps*/", style = TextStyle(
                        lightRed, fontSize = 10.sp,
                        fontWeight = FontWeight.Light), modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.CenterVertically))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalTabs(files:ArrayList<VideoCompressModel>, state: PagerState, scope: CoroutineScope){

    ScrollableTabRow(selectedTabIndex = state.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(modifier =
            Modifier
                .tabIndicatorOffset(tabPositions[state.currentPage])
                .padding(5.dp)) }
        , backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colors.primary, edgePadding = 5.dp) {
        files.forEachIndexed { index, file ->
            Tab(selected = state.currentPage == index, onClick = {
                scope.launch {
                    state.animateScrollToPage(index)
                }
            }) {
                PagerTabs(videoModel = file)
            }
        }

    }


}

@Composable
fun PagerTabs(videoModel: VideoCompressModel){

    Card(modifier = Modifier.padding(2.dp), elevation = 5.dp) {
        GlideImage(
            imageModel = videoModel.file.filePath, modifier = Modifier
                .size(80.dp)
                .padding(5.dp), requestOptions = {
                RequestOptions().override(90,90).diskCacheStrategy(
                    DiskCacheStrategy.ALL).fitCenter() })
    }

}

@Composable
fun VideoLayout(file: VideoCompressModel, context: Context){
    val state= LazyListState()
    LazyColumn(state =state  ){
        item {
            VideoDetails(file,context)
        }

        item{
            ResolutionPicker(file)
        }

    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerView(files:ArrayList<VideoCompressModel>, context: Context){
    val pageState= rememberPagerState()
    val scope= rememberCoroutineScope()
    val constraints= ConstraintSet{
        val compressLayout=createRefFor("compressLayout")
        constrain(compressLayout){
            bottom.linkTo(parent.bottom)
            centerHorizontallyTo(parent)
        }

    }
    ConstraintLayout(constraintSet = constraints, modifier = Modifier.fillMaxSize()) {
        Column {
            HorizontalPager(count = files.size, state = pageState) { currentPage ->
                VideoLayout(file = files[currentPage], context)
            }
            HorizontalTabs(files = files, state = pageState, scope = scope)
        }
        TextButton(onClick = {
            /*compress files on the background*/
            val array: ArrayList<VideoCompressModel> =ArrayList()
            array.addAll(files)
            DataBridge.push(array)
            //if the service has already been started don't launch it again
            if(!DataBridge.active)
                context.startService(Intent(context, ShrinkService::class.java))
            (context as Activity).finish()
        },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .background(lightRed, shape = RoundedCornerShape(10.dp))
                .border(5.dp, Color.Red, shape = RoundedCornerShape(10.dp))
                .layoutId("compressLayout")
        ) {
            Text(
                text = "COMPRESS", style = TextStyle(
                    Color.White, fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                ), modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }

}