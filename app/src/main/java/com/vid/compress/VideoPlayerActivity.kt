package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.vid.compress.ui.theme.VideoCompressorTheme
import java.io.File

class VideoPlayerActivity: ComponentActivity() {

  lateinit var  exoplayer:ExoPlayer
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uriList=intent.getStringArrayListExtra("uriList")
        if(uriList==null||uriList.isEmpty())
            finish()

         exoplayer=ExoPlayer.Builder(this).build()
            .also {
                    player->
                     val list=List(uriList?.size?:0,
                         init = {index ->createMediaItem(uriList!![index])})
                         player.setMediaItems(list)
                         player.prepare()

            }
        setContent {
            VideoCompressorTheme(darkTheme = true,this) {
                Surface(modifier = Modifier.fillMaxSize()) {
                   MainView(fileUris = uriList!!, exoplayer =exoplayer , activity =this )
                }
            }
        }

    }


    private fun createMediaItem(uri:String):MediaItem{
        return MediaItem.Builder().setUri( Uri.fromFile(File(uri))).build()
    }
    override fun onPause() {
        super.onPause()
        exoplayer.pause()
    }

    override fun onResume() {
        super.onResume()
        exoplayer.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoplayer.release()
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(fileUris: ArrayList<String>,exoplayer:ExoPlayer,activity:Activity){
    var showAppBar by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf(File(fileUris[exoplayer.currentMediaItemIndex]).name)}
    val mContext=LocalContext.current
    exoplayer.addListener(object :Player.Listener{
        override fun onPlayerError(error: PlaybackException) {
            Toast.makeText(mContext,error.message,Toast.LENGTH_SHORT).show()
        }
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            fileName=File(fileUris[exoplayer.currentMediaItemIndex]).name
        }

    })

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(modifier = Modifier.fillMaxSize(),factory = { context ->
            StyledPlayerView(context).apply {
                player = exoplayer
                layoutParams =
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                setControllerVisibilityListener(StyledPlayerView.ControllerVisibilityListener { visibility ->
                    showAppBar=visibility==View.VISIBLE
                })

            }
        })
        if(showAppBar)
            TopAppBar(title = { Text(text =fileName,
                fontSize = 12.sp, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Drawer Icon")
                    }
                }, modifier = Modifier.align(Alignment.TopStart).fillMaxWidth())
    }





}


