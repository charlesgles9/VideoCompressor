package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.vid.compress.ui.theme.VideoCompressorTheme

class ShrinkActivity:ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VideoCompressorTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.medium) {
                    Toolbar(context = this)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Toolbar(context:Activity){
    val scope= rememberCoroutineScope()
    val scaffoldState= rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState,
             topBar = { TopAppBar(title = { Text(text = "Configuration")},
                 navigationIcon ={ IconButton(onClick = { context.finish() }) {
                     Icon(Icons.Filled.ArrowBack, contentDescription ="exitActivity" )
                 }} )
             }) {

    }
}