package com.vid.compress
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.vid.compress.storage.Disk
import com.vid.compress.storage.FileUtility
import com.vid.compress.ui.models.UserSettingsModel
import com.vid.compress.ui.pages.StartView
import com.vid.compress.ui.theme.VideoCompressorTheme

class StartUpActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // special storage access permission
        val document= FileUtility.getUriFromSharedPreference(Disk.getDirs(this)[0],this)
            ?.let { DocumentFile.fromTreeUri(this, it)

            }
        if(document?.canWrite() == true){
            //go to main Activity
            val intent: Intent = Intent(this.applicationContext,
                Class.forName("com.vid.compress.MainActivity"))
                this.startActivity(intent)
            this.finish()
        }
        setContent {
            VideoCompressorTheme(darkTheme = UserSettingsModel.isDarkModeEnabled(this),this) {
                Surface(modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
                   StartView(this)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data==null)
            return

        if(requestCode==32){
            val uri=data.data
            val document= uri?.let { DocumentFile.fromTreeUri(applicationContext, it) }
            if(document==null||!document.canWrite()){
                Toast.makeText(applicationContext,"Failed to Grant Permission!", Toast.LENGTH_LONG).show()
                return
            }

            val storage= Disk.getStorage(applicationContext,document.name)

            grantUriPermission(packageName,uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val editor = getSharedPreferences("StorageUri", MODE_PRIVATE).edit().apply {
                putString(storage.path,uri.path)
                apply()
            }
            Toast.makeText(this,"Permission Granted!", Toast.LENGTH_SHORT).show()
        }
    }
}