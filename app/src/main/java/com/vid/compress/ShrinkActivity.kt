package com.vid.compress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
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
import com.vid.compress.services.DataBridge
import com.vid.compress.services.ShrinkService
import com.vid.compress.storage.Disk
import com.vid.compress.ui.models.FileObjectViewModel
import com.vid.compress.storage.FileUtility
import com.vid.compress.storage.PathUtils
import com.vid.compress.ui.models.UserSettingsModel
import com.vid.compress.ui.models.VideoCompressModel
import com.vid.compress.ui.pages.ShrinkToolbar
import com.vid.compress.ui.theme.LighterBlue
import com.vid.compress.ui.theme.VideoCompressorTheme
import com.vid.compress.ui.theme.lightRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class ShrinkActivity:ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selected = intent.getStringArrayListExtra("selected")
        val files = ArrayList<VideoCompressModel>()
        val externalShared = onSharedIntent()

        // in case the videos are send from shared intent
        externalShared.forEach { path ->
            files.add(VideoCompressModel(FileObjectViewModel(path)))
        }
        //in case the videos are sent form the main Activity
        if (selected != null)
            for (i in 0 until selected.size) files.add(
                VideoCompressModel(
                    FileObjectViewModel(
                        selected[i]
                    )
                )
            )
        else if (files.isEmpty())
            finish()
        setContent {
            VideoCompressorTheme(darkTheme = UserSettingsModel.isDarkModeEnabled(this), this) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    ShrinkToolbar(context = this, files)
                }
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun onSharedIntent(): MutableList<String> {
        val files = mutableListOf<String>()
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                val uri = intent.data
                uri?.apply {
                    val path: String = PathUtils.mediaUriToFilePath(this@ShrinkActivity, uri)
                    files.add(path)
                }
            }
            Intent.ACTION_SEND -> {
                val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                uri.apply {
                    val path = PathUtils.mediaUriToFilePath(this@ShrinkActivity, uri)
                    files.add(path)
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                val array = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
                array?.forEach {
                    files.add(PathUtils.mediaUriToFilePath(this@ShrinkActivity, it as Uri))
                }
            }
        }
        return files
    }
}

