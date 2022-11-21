package com.vid.compress.ui.models

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit

class UserSettingsModel {

    companion object{

        fun setDarkModeEnabled(context:Context,value:Boolean){
            val preference=context.getSharedPreferences("userPref", ComponentActivity.MODE_PRIVATE)
           preference.edit {
               putBoolean("darkModeEnabled",value)
               apply()
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