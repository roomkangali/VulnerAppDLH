package com.dlh.vulnerapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipSlipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val output = outputView()

        // The archive path can be supplied via IPC; falls back to a demo archive.
        val zipPath = intent.getStringExtra("zip_path") ?: createDemoZip().absolutePath
        val targetDir = File(cacheDir, "unzipped").also { it.mkdirs() }

        try {
            val names = unzip(File(zipPath), targetDir)
            output.text = "Extracted to:\n${targetDir.absolutePath}\n\n$names"
        } catch (e: Exception) {
            output.text = "Error: ${e.message}"
        }
    }

    private fun unzip(zipFile: File, targetDir: File): String {
        val names = StringBuilder()
        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry: ZipEntry? = zis.nextEntry
            while (entry != null) {
                // VULNERABILITY: Zip Slip (path traversal on extraction).
                // entry.name is trusted verbatim; a crafted "../" name escapes
                // targetDir and overwrites arbitrary files. There is no
                // getCanonicalPath() prefix check before writing.
                val outFile = File(targetDir, entry.name)
                outFile.parentFile?.mkdirs()
                FileOutputStream(outFile).use { zis.copyTo(it) }
                names.append(entry.name).append('\n')
                entry = zis.nextEntry
            }
        }
        return names.toString()
    }

    private fun createDemoZip(): File {
        val zip = File(cacheDir, "demo.zip")
        ZipOutputStream(FileOutputStream(zip)).use { zos ->
            zos.putNextEntry(ZipEntry("readme.txt"))
            zos.write("hello from demo zip".toByteArray())
            zos.closeEntry()
        }
        return zip
    }

    private fun outputView(): TextView {
        val tv = TextView(this)
        tv.setPadding(32, 32, 32, 32)
        tv.setTextColor(Color.parseColor("#00FF00"))
        tv.typeface = Typeface.MONOSPACE
        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#121212"))
            addView(tv)
        }
        setContentView(scroll)
        return tv
    }
}
