package com.vid.compress.services

import com.vid.compress.ui.models.VideoCompressModel
import java.util.Stack


class DataBridge {

    companion object{
     private val data=Stack<VideoCompressModel>()
     private var original=0
     var percent=0
     var active=false
      fun push(files:ArrayList<VideoCompressModel>){
          files.forEach{
              file->
              data.push(file)
          }
          original+=files.size
      }

      fun pop():VideoCompressModel{

          return data.pop()
      }

      fun peek():VideoCompressModel{
          return data.peek()
      }

     fun isEmpty():Boolean{
         return data.isEmpty()
     }

     fun clear(){
         data.clear()
         percent=0
         original=0
     }

     fun currentSize():Int{
         return data.size
     }

     fun originalSize():Int{
         return original
     }



     fun asList():ArrayList<VideoCompressModel>{
         val array=ArrayList<VideoCompressModel>()
          data.forEach { uri-> array.add(uri) }
         return array
     }


    }
}