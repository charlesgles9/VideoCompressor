package com.vid.compress

import android.os.Bundle
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
import com.vid.compress.permisions.PermissionHelper
import com.vid.compress.storage.FileUtility
import com.vid.compress.ui.theme.VideoCompressorTheme
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
    LazyVerticalGrid(cells = GridCells.Fixed(3),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(horizontal = 10.dp)){
        items(files) {file->
            fileCard(file)
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
                    .clip(CircleShape)
                    .border(5.dp, MaterialTheme.colors.primary, CircleShape))
            Text(text = file.name)

    }

}

@Composable
fun toolBar(files: MutableList<File>){
    Scaffold (topBar = {
        TopAppBar(title = { Text(text = "Compress")},
                  navigationIcon = {Icon(Icons.Filled.Menu, contentDescription = "Drawer Icon")},
                  elevation = 10.dp, actions = {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", modifier = Modifier.padding(start = 20.dp,end=20.dp))
                Icon(Icons.Filled.MoreVert, contentDescription = "More",modifier = Modifier.padding(start = 20.dp,end=20.dp))
            })
    }){

       fileList(files = files)
    }
}

@Composable
fun messageCard(title:String) {

        Row(modifier = Modifier.padding(all = 8.dp),) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "Profile", modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            var isExpanded by remember { mutableStateOf(false) }
            Column(modifier = Modifier
                .padding(top = 8.dp, start = 5.dp)
                .clickable { isExpanded = !isExpanded }) {
                Text(text = "Hello $title!", style = MaterialTheme.typography.h5)
                Text(text = "bloody Hell this is a very amazing day to practice my skills! $title\n i Will be a good coder!\n it's my destiny\n i will overcome\n its in my power\n i will get it\n"
                    ,style = MaterialTheme.typography.subtitle2, maxLines = if(isExpanded) Int.MAX_VALUE else 2)
                var text by remember { mutableStateOf(TextFieldValue("")) }
                OutlinedTextField(value = text, onValueChange = {newText-> text=newText},
                    label = {Text(text = "label")},
                    placeholder = { Text(text = "Hello")}
                )
            }

    }


}



