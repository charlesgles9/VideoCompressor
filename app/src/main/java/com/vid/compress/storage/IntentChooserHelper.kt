package com.vid.compress.storage
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import java.io.File


class IntentChooserHelper(private val context:Context,private val files:ArrayList<File>) {

    //share to mimeType
    fun sendMultiple(){
        // make sure it's not a directory
        if(files[0].isDirectory) {
            Toast.makeText(context,"cannot send Folders",Toast.LENGTH_SHORT).show()
            return
        }
        val uris: ArrayList<Uri> = ArrayList()
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        FileUtility.fileToUri(context,files){ path,uri->
            uris.add(uri)
            if(uris.size>=files.size){
                intent.type = getMimeType(File(path))
                fetchDefaults(intent)
                context.startActivity(Intent.createChooser(intent,"Send"))
            }
        }
    }

    private fun getMimeType(file: File): String? {
        val extension=FileUtility.getExtension(file.name)
        return if (extension.lastIndexOf(".") == -1)
            null else MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            extension.substring(extension.lastIndexOf(".") + 1)
        )
    }

    private  fun fetchDefaults(intent:Intent){
        // check if there is a default file opener for this extension
        val packageManager = context.packageManager
        val defaults = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        try {
            if (!defaults!!.activityInfo.name.endsWith("ResolverActivity")) {
                return
            }
        } catch (ignored: NullPointerException) {
        }

        // retrieve all valid apps for our intent
        val targets = ArrayList<Intent>()
        val appInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (appInfoList.isEmpty()) {
            return
        }
        for (appInfo in appInfoList) {
            // don't include self in the list
            val packageName = appInfo.activityInfo.packageName
            if (packageName == context.packageName) continue
            val target = Intent()
            target.setPackage(packageName)
            targets.add(target)
        }
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets)
    }

    // share to all
    fun shareMultiple(){
        // make sure it's not a directory
        if(files[0].isDirectory) {
            Toast.makeText(context,"cannot send Folders",Toast.LENGTH_SHORT).show()
            return
        }
        val uris: ArrayList<Uri> = ArrayList()
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "*/*"
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        FileUtility.fileToUri(context,files){ path,uri->
            uris.add(uri)
            if(uris.size>=files.size){
                context.startActivity(Intent.createChooser(intent,"Share"))
            }
        }
    }
}