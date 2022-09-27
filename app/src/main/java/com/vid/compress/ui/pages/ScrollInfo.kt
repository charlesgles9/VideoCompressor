package com.vid.compress.ui.pages

data class ScrollInfo(var firstVisibleItem:Int){

    fun isEqualTo(first:Int):Boolean{
        return firstVisibleItem==first
    }
}
