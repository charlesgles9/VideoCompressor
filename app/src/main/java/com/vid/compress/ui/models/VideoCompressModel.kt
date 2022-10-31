package com.vid.compress.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration

class VideoCompressModel(val file:FileObjectViewModel): ViewModel() {

    var selectedResolution by mutableStateOf("Original")
    var disableAudio by mutableStateOf(false)
    var minBitRateEnabled by mutableStateOf(false)


    fun getVideoConfiguration():Configuration{
       val config=Configuration(keepOriginalResolution = (selectedResolution=="Original"))
        config.disableAudio=disableAudio
        config.isMinBitrateCheckEnabled=minBitRateEnabled
        config.videoBitrate=null

        if(selectedResolution.contains("x")){
            val split=selectedResolution.split("x")
            val w=split[0].toDouble()
            val h=split[1].toDouble()
            config.videoWidth=w
            config.videoHeight=h
        }else {
            when(selectedResolution){

                "Quality Very Low"->{
                    config.quality=VideoQuality.VERY_LOW
                }
                "Quality Low"->{
                    config.quality=VideoQuality.LOW
                }
                "Quality Medium"->{
                    config.quality=VideoQuality.MEDIUM
                }
                "Quality High"->{
                    config.quality=VideoQuality.HIGH
                }
                "Quality Very High"->{
                    config.quality=VideoQuality.VERY_HIGH
                }
            }
        }

        return config
    }
}