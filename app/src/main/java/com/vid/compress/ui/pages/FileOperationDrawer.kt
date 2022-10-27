package com.vid.compress.ui.pages

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vid.compress.storage.IntentChooserHelper
import com.vid.compress.ui.models.AlbumViewModel

import com.vid.compress.ui.theme.CustomShape2
import com.vid.compress.ui.theme.IconBackground
import java.io.File


@Composable
fun FileOperationLayout(album:AlbumViewModel,context:Context,sliderWidth:State<Dp>){
    Column(modifier = Modifier
        .background(color = MaterialTheme.colors.primary)
        .fillMaxHeight()
        .width(sliderWidth.value)
        .layoutId("options")) {


        Text(text = "Items: " + album.selected.size + "/" + album.size(),
            modifier = Modifier
                .padding(bottom = 50.dp, top = 10.dp)
                .align(Alignment.CenterHorizontally),
            style= TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        )

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
                album.selected.forEach { file ->
                    list.add(file.filePath)
                }
                intent.putStringArrayListExtra("uriList", list)
                context.startActivity(intent)

            }
            .background(color = IconBackground, shape = CustomShape2) ) {
            Icon(
                Icons.Outlined.PlayArrow, contentDescription = "PlayAll",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp), tint = Color.White)
        }

        Box(modifier= Modifier
            .size(50.dp)
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 10.dp)
            .clickable {
                album.selectAll()
            }
            .background(color = IconBackground, shape = CustomShape2) ) {
            Icon(painter = painterResource(id = com.vid.compress.R.drawable.ic_select_all), contentDescription = "selectAll",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp), tint = Color.White)
        }

        Box(modifier= Modifier
            .size(50.dp)
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 10.dp)
            .clickable { /*send intent*/
                val files=ArrayList<File>()
                album.selected.forEach { file->
                    files.add(File(file.filePath))
                }
                IntentChooserHelper(context,files).sendMultiple()
            }
            .background(color = IconBackground, shape = CustomShape2) ) {
            Icon(
                Icons.Outlined.Send, contentDescription = "send",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp), tint = Color.White)
        }



        Box(modifier= Modifier
            .size(50.dp)
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 10.dp)
            .clickable { /*share intent*/
              val files=ArrayList<File>()
                album.selected.forEach { file->
                    files.add(File(file.filePath))
                }
                IntentChooserHelper(context,files).shareMultiple()
            }
            .background(color = IconBackground, shape = CustomShape2) ) {
            Icon(
                Icons.Outlined.Share, contentDescription = "share",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp),tint = Color.White)
        }

        Box(modifier= Modifier
            .size(50.dp)
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 10.dp)
            .clickable { /*file info*/
                album.showProperties.value=true
            }
            .background(color = IconBackground, shape = CustomShape2) ) {
            Icon(
                Icons.Outlined.Info, contentDescription = "Info",
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
            Icon(
                Icons.Outlined.Delete, contentDescription = "Delete",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp),tint = Color.White)
        }


    }
}