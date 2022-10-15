package com.vid.compress.storage


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.vid.compress.MainActivity
import java.io.File


class MIMETypesHelper(private val context: Context, file: File) {
    private val file: File
    private var intent: Intent? = null

    init {
        this.file = file
    }

    fun startDefault() {
       /* if (!isStoragePermissionGranted(file)) {
            getStoragePermission(file)
            return
        }*/
        FileUtility.fileToUri(
            context, file) { path, uri ->
            intent = OpenWithDefaults(context, getMimeType(file), uri)
            if (intent != null) context.startActivity(intent) else Toast.makeText(
                context,
                "No app found to open this file",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun startNoDefaults(MimeType: String?) {
        /*if (!isStoragePermissionGranted(file)) {
            getStoragePermission(file)
            return
        }*/
        FileUtility.fileToUri(
            context, file,
            OnScanCompletedListener { path, uri ->
                intent = OpenFileAs(context, MimeType, uri)
                if (intent != null) context.startActivity(intent) else Toast.makeText(
                    context,
                    "No app found to open this file",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    fun startShare() {
       /* if (!isStoragePermissionGranted(file)) {
            getStoragePermission(file)
            return
        }*/
        FileUtility.fileToUri(
            context, file,
            OnScanCompletedListener { path, uri ->
                intent = OpenShareFile(context, uri)
                if (intent != null) context.startActivity(intent)
            })
    }

    private fun getMimeType(file: File): String? {
         val extension=FileUtility.getExtension(file.name)
        return if (extension.lastIndexOf(".") == -1)
            null else MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            extension.substring(extension.lastIndexOf(".") + 1)
        )
    }

    @Throws(IllegalArgumentException::class)
    private fun OpenWithDefaults(context: Context, MimeType: String?, uri: Uri): Intent? {

        // exit if completely unavailable exit
        if (MimeType == null) return null
        /*try {
            uri = FileHandleUtil.walkToFile(file, context).getUri();// FileProvider.getUriForFile(context,context.getPackageName()+".provider",file);
        }catch (Exception e){
            Toast.makeText(context,"No app found to open this file",Toast.LENGTH_LONG).show();
            return null;
        }*/
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, MimeType)

        // check if there is a default file opener for this extension
        val packageManager = context.packageManager
        val defaults = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        try {
            if (!defaults!!.activityInfo.name.endsWith("ResolverActivity")) {
                return intent
            }
        } catch (ignored: NullPointerException) {
        }

        // retrieve all valid apps for our intent
        val targets = ArrayList<Intent>()
        val appInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (appInfoList.isEmpty()) {
            return null
        }
        for (appInfo in appInfoList) {
            // don't include self in the list
            val packageName = appInfo.activityInfo.packageName
            if (packageName == context.packageName) continue
            val target = Intent()
            target.setPackage(packageName)
            targets.add(target)
        }
        val chooserIntent = Intent.createChooser(targets.removeAt(targets.size - 1), "Open with")
            .putExtra(Intent.EXTRA_INITIAL_INTENTS, targets)
        chooserIntent.action = Intent.ACTION_VIEW
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        chooserIntent.setDataAndType(uri, MimeType)
        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return chooserIntent
    }

    private fun OpenFileAs(context: Context, MimeType: String?, uri: Uri): Intent? {
        // exit if completely unavailable exit
        if (MimeType == null) return null
        // Uri uri= FileHandleUtil.walkToFile(file,context).getUri(); // FileProvider.getUriForFile(context,context.getPackageName()+".provider",file);
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, MimeType)
        val packageManager = context.packageManager
        // retrieve all valid apps for our intent
        val targets = ArrayList<Intent>()
        val appInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (appInfoList.isEmpty()) {
            return null
        }
        for (appInfo in appInfoList) {
            // don't include self in the list
            val packageName = appInfo.activityInfo.packageName
            if (packageName == context.packageName) continue
            val target = Intent()
            target.setPackage(packageName)
            targets.add(target)
        }
        val chooserIntent = Intent.createChooser(targets.removeAt(targets.size - 1), "Open with")
            .putExtra(Intent.EXTRA_INITIAL_INTENTS, targets)
        chooserIntent.action = Intent.ACTION_VIEW
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        chooserIntent.setDataAndType(uri, MimeType)
        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return chooserIntent
    }

    private fun OpenShareFile(context: Context, uri: Uri): Intent? {
        val MimeType = getMimeType(file) ?: return null
        // exit if completely unavailable exit
        // Uri uri= FileHandleUtil.walkToFile(file,context).getUri();//  FileProvider.getUriForFile(context,context.getPackageName()+".provider",file);
        val intent = Intent()
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setDataAndType(uri, MimeType)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return Intent.createChooser(intent, "Share")
    }


    private fun getStoragePermission(file: File) {
        /*val activity = context as MainActivity
        val storage: String = DiskUtils.getInstance().getStartDirectory(file)
        if (!PermissionsHelper.getInstance().uriValid(File(storage), context)) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            activity.startActivityForResult(intent, 32)
        }*/
    }
}