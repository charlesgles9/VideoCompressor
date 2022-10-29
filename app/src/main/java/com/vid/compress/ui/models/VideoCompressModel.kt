package com.vid.compress.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class VideoCompressModel(val file:FileObjectViewModel): ViewModel() {

    var selectedResolution by mutableStateOf("Original")
    var disableAudio by mutableStateOf(false)
    var minBitRateEnabled by mutableStateOf(false)
}