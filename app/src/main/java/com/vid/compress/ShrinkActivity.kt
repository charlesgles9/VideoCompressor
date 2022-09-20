package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.glide.GlideImage
import com.vid.compress.storage.Disk
import com.vid.compress.storage.FileObjectViewModel
import com.vid.compress.storage.FileUtility
import com.vid.compress.ui.theme.VideoCompressorTheme
import kotlinx.coroutines.coroutineScope
import java.io.File
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KProperty

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
            VideoCompressorTheme(darkTheme = false) {
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
              PagerView(files = files)
    }
}


@Composable
fun VideoDetails(file:FileObjectViewModel){
    Column(modifier = Modifier.fillMaxWidth()) {

        Card(elevation = 10.dp, modifier = Modifier.padding(5.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)) {
            GlideImage(
                imageModel = "", modifier = Modifier
                    .size(150.dp, 150.dp)
                    .padding(5.dp))
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
                            ), modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }
        }
}
@Composable
fun VideoLayout(file: FileObjectViewModel){
    val state=LazyListState()
   LazyColumn(state =state  ){
       item {
           VideoDetails(file)
       }

       item{
           ResolutionPicker()
       }

   }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerView(files:ArrayList<FileObjectViewModel>){
    val pageState= rememberPagerState()
    HorizontalPager(count = files.size, state = pageState) {
        currentPage->
        VideoLayout(file = files[currentPage])
    }

}