package com.vid.compress.util
import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


class DateUtils {
    companion object{
        fun getDate(ll: Long): String? {
            @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("MM/dd/yyy")
            return dateFormat.format(ll)
        }
        fun getDateHHmmss(time:Long):String{
            val str=StringBuffer()
            if((time/1000)<60*60){
                val format=SimpleDateFormat("mm:ss", Locale.US)
                str.append("00:"+format.format(Date(time)))
            }else{
                val format=SimpleDateFormat("hh:mm:ss", Locale.US)
                str.append(format.format(Date(time)))
            }
            return str.toString()
        }
    }
}