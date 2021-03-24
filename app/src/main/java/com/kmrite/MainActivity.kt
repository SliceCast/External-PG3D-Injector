package com.kmrite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Sample Usage
        val pkg = "com.sumanto.app"
        val lib = "libUdin.so"
        val offset = 0x44
        val hex = "00 00 00 00"
        val mytext = findViewById<AppCompatTextView>(R.id.sample)
        findViewById<AppCompatButton>(R.id.start).setOnClickListener {
//            Prefent freezing Apps
            Thread {
                try  {
                    val you = Tools.setCode(pkg, lib, offset, hex).toString()
                    synchronized(this) {
                        runOnUiThread {
                            mytext.text = you
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}