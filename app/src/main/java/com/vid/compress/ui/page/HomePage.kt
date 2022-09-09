package com.vid.compress.ui.page

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vid.compress.R

@Composable
fun DrawerView(){
    LazyColumn{

        item {
            drawerTitleLayout()
        }

        item {
            drawerHeader(title = "Rate Us")
        }
        item {
           drawerHeader(title = "Give FeedBack")
        }
        item {
            drawerHeader(title = "Contribute")
        }
    }
}

@Composable
fun drawerHeader(title:String){
    Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp),
        border = BorderStroke(1.dp, color = Color.Gray)) {
        Text(text = title,
            style = TextStyle(fontWeight = FontWeight.Bold,
            fontSize = 15.sp, color = MaterialTheme.colors.onSecondary))
    }
}

@Composable
fun drawerItem(title: String,icon:ImageVector){
    Row{
        Text(
            text = title,
            style = TextStyle(fontSize = 8.sp,
            color = MaterialTheme.colors.onSecondary))
        Icon(icon , contentDescription =title,
            modifier = Modifier
                .size(24.dp))
    }

}
@Composable
fun drawerTitleLayout(){
    Column {

        Image(painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "DrawerIcon", modifier = Modifier.fillMaxHeight(0.3f))
        Text(text = "Shrink compressor!")
        Divider(thickness = 1.dp)
    }
}