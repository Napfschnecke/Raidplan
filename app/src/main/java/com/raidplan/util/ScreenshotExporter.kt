package com.raidplan.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.raidplan.R
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder


class ScreenshotExporter {

    var screen: View? = null

    fun getScreenShot(view: View): Bitmap {
        screen = view
        val screenView: View = view
        screenView.isDrawingCacheEnabled = true
        val bitmap: Bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }

    fun store(bm: Bitmap, fileName: String, context: Context, boss: String) {
        val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/Raidplans"
        val dir = File(dirPath)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dirPath, fileName)
        file.createNewFile()
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        sendImage(context, boss)
    }

    private fun sendImage(context: Context, boss: String) {
        val uri = Uri.parse("/storage/emulated/0/Pictures/Raidplans/plan.png")
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.`package` = "com.whatsapp"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Raidposition $boss")
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
        sharingIntent.type = "image/png"
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(sharingIntent)
        } catch (ex: ActivityNotFoundException) {
            screen?.let { v ->
                Snackbar.make(
                    v,
                    context.resources.getString(R.string.no_whatsapp),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setBackgroundTint(context.resources.getColor(R.color.colorAccentMuted))
                    show()
                }
            }
        }
    }
}