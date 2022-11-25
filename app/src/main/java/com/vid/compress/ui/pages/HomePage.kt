package com.vid.compress.ui.pages

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vid.compress.R
import com.vid.compress.ui.models.UserSettingsModel
import com.vid.compress.ui.theme.LightGray
import com.vid.compress.ui.theme.LighterBlue

@Composable
fun DrawerView(context:Context){

    LazyColumn{

        item {
            drawerTitleLayout()
        }

        item {
            drawerHeader(title = "Rate Us",Icons.Default.Favorite){

            }
            Divider(
                color = MaterialTheme.colors.onSecondary,
                thickness = 0.5.dp
            )
        }
        item {
           drawerHeader(title = "Give FeedBack",Icons.Default.Email){

           }
            Divider(
                color = MaterialTheme.colors.onSecondary,
                thickness = 0.5.dp
            )
        }
        item {
            drawerHeader(title = "User Preference",Icons.Default.Settings){

            }

        }
        item {
            drawerItemToggle(UserSettingsModel.isDarkModeEnabled(context),title = "DarkModeEnabled"){
                 value-> UserSettingsModel.setDarkModeEnabled(context, value)
          }
        }
        item {
            drawerItemDropDown(UserSettingsModel.columnCount(context),"Number of Columns: ",arrayOf("Adaptive","1","2","3","4")){
                value-> UserSettingsModel.setColumnCount(context,value)
          }
        }
        item {
            drawerItemDropDown(UserSettingsModel.thumbnailSize(context).toString(),"Thumbnail: ",arrayOf("150","200","300","500")){
                value-> UserSettingsModel.setThumbnailSize(context,value.toInt())
            }
        }
        item{
            Divider(
                color = MaterialTheme.colors.onSecondary,
                thickness = 0.5.dp
            )
        }

    }
}

@Composable
fun drawerHeader(title:String,icon:ImageVector,callback: () -> Unit){
    Column( modifier = Modifier
        .fillMaxWidth()
        .clickable {
            callback()
        }) {
        Row {
            Icon(icon, contentDescription ="Icon" , modifier = Modifier.padding(15.dp))
            Text(text = title,
                style = TextStyle(fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = MaterialTheme.colors.onSecondary),
             modifier = Modifier.align(Alignment.CenterVertically))
        }

    }
}

@Composable
fun drawerItemText(title: String,icon:ImageVector){
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
fun drawerItemToggle(value:Boolean,title: String,callback:(Boolean)->Unit){
    var checked by remember {
        mutableStateOf(value)
    }
    Row(modifier= Modifier
        .padding(start = 10.dp, end = 15.dp)
        .fillMaxWidth()
        .height(50.dp)
        .shadow(0.5.dp, ambientColor = Color.LightGray, shape = Shapes().medium)){
        Text(
            text = title,
            style = TextStyle(fontSize = 12.sp,
                color = MaterialTheme.colors.onSecondary),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp))
        Switch(checked = checked, onCheckedChange = {
            value->
            checked=value
            callback(value)
        }, modifier = Modifier.align(Alignment.CenterVertically),
        colors = SwitchDefaults.colors(checkedThumbColor = LighterBlue, uncheckedThumbColor = LightGray))
    }

}

@Composable
fun drawerItemDropDown(value:String,title: String,items:Array<Any>,callback:(String)->Unit){
    var expanded by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(value) }

    Row(modifier = Modifier
        .padding(start = 10.dp, end = 15.dp)
        .fillMaxWidth()
        .height(50.dp)
        .shadow(0.5.dp, ambientColor = Color.LightGray, shape = Shapes().medium)
        .clickable {
            expanded = !expanded
        }) {
        Text(text = title, style = TextStyle(fontSize = 12.sp,
                color = MaterialTheme.colors.onSecondary),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp)
                .weight(1f))
        Text(text = current, style = TextStyle(fontSize = 12.sp,
            color = MaterialTheme.colors.onSecondary),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(0.5f))
        if(expanded){
            Icon(Icons.Default.ArrowDropDown, contentDescription ="Up", tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .rotate(180f))
        }else{
            Icon(Icons.Default.ArrowDropDown, contentDescription ="Drop", tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f))
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded=!expanded },
            modifier = Modifier.align(Alignment.CenterVertically)) {
            items.forEach { item->
                DropdownMenuItem(onClick = {
                    current=item.toString()
                    expanded=!expanded
                    callback(item.toString())
                }) {
                    Text(text = item as String, style = TextStyle(MaterialTheme.colors.onSecondary, fontSize = 11.sp,
                        fontWeight = FontWeight.Light
                    ), modifier = Modifier.padding(5.dp))
                }
            }
        }
    }
}

@Composable
fun drawerTitleLayout(){
    Column(modifier=Modifier.fillMaxWidth().padding(10.dp)) {
        Image(painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "DrawerIcon", modifier = Modifier.fillMaxHeight(0.3f))
        Text(text = "Shrink compressor!", style = TextStyle(fontSize = 20.sp,
            color = MaterialTheme.colors.onSecondary, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic))
        Divider(thickness = 1.dp, color = Color.White)
    }
}