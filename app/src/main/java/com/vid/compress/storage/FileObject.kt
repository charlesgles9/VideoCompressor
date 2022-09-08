package com.vid.compress.storage

import java.io.File

class FileObject (val file:File){

    val selected=false

    constructor(path:String):this(File(path)){}


}