package com.vid.compress.ui.models

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel

class UserSettingsModel :ViewModel(){

    companion object{

        //helps us change the theme in real time
        var darkModeEnabled by mutableStateOf(false)
        var columnCount by mutableStateOf("Adaptable")
        fun setDarkModeEnabled(context:Context,value:Boolean){
            val preference=context.getSharedPreferences("userPref", ComponentActivity.MODE_PRIVATE)
           preference.edit {
               putBoolean("darkModeEnabled",value)
               apply()
               darkModeEnabled=value
           }
        }

        fun setThumbnailSize(context:Context,value:Int){
            val preference=context.getSharedPreferences("userPref", ComponentActivity.MODE_PRIVATE)
           preference.edit {
               putInt("thumbSize",value)
               apply()
           }
        }

        fun setColumnCount(context:Context,value: String){
            val preference=context.getSharedPreferences("userPref", ComponentActivity.MODE_PRIVATE)
            preference.edit {
                putString("columnCount",value)
                apply()
                columnCount=value
            }
        }
        fun isDarkModeEnabled(context:Context):Boolean{
            val preference=context.getSharedPreferences("userPref", ComponentActivity.MODE_PRIVATE)
            return preference.getBoolean("darkModeEnabled",false)
        }

        fun thumbnailSize(context:Context):Int{
            val preference=context.getSharedPreferences("userPref", ComponentActivity.MODE_PRIVATE)
            return preference.getInt("thumbSize",150)
        }

        fun columnCount(context:Context): String{
            val preference=context.getSharedPreferences("userPref", ComponentActivity.MODE_PRIVATE)
            return preference.getString("columnCount","Adaptable")?:"Adaptable"
        }
    }
}