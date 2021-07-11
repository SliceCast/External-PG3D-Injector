package com.slicecast

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.widget.EditText
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.slicecast.databinding.ActivityMainBinding
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.ipc.RootService
import java.io.File

class MainActivity : AppCompatActivity(), Handler.Callback {
    lateinit var bind: ActivityMainBinding

    private var consoleList: AppendCallbackList = AppendCallbackList()
    private val myMessenger = Messenger(Handler(Looper.getMainLooper(), this))
    var remoteMessenger: Messenger? = null
    private var serviceTestQueued = false
    private var conn: MSGConnection? = null

    companion object {
        const val TAG = "PG3D Injector"
    }

    private var EditText.value
        get() = this.text.toString()
        set(value) {
            this.setText(value)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        if (Shell.rootAccess()) {
            if (remoteMessenger == null) {
                bind.console.append("startRootServices\n")
                bind.console.append("Result : ")
                serviceTestQueued = true
                val intent = Intent(this, RootServices::class.java)
                conn = MSGConnection()
                RootService.bind(intent, conn!!)
            }
        }
        setContentView (bind.root)

        bind.startPatcher.setOnClickListener {
            if (Shell.rootAccess()) {
                if (remoteMessenger != null) {
                    bind.console.append("RootServiceRunning")
                    bind.console.append("Result : ")
                    testService("com.pixel.gun3d", "libil2cpp.so", 0x1515CDC, "01 00 A0 E3 1E FF 2F E1") //NinjaJump EXAMPLE
                }
            } else {
                bind.console.append("Result : ")
                bind.console.append(Tools.setCode("com.pixel.gun3d", "libil2cpp.so", 0x1515CDC, "01 00 A0 E3 1E FF 2F E1").toString()) //NinjaJumo EXAMPLE
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        val dump = msg.data.getString("result")
        consoleList.add(dump)
        return false
    }

    private fun testService(pkg: String, libname: String, Offset: Int, hex: String) {
        val message: Message = Message.obtain(null, RootServices.MSG_GETINFO)
        message.data.putString("pkg", pkg)
        message.data.putString("fileSo", libname)
        message.data.putInt("offset", Offset)
        message.data.putString("hexNumber", hex)
        message.replyTo = myMessenger
        try {
            remoteMessenger?.send(message)
        } catch (e: RemoteException) {
            Log.e(TAG, "Remote error", e)
        }
    }

    inner class MSGConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "service onServiceConnected")
            remoteMessenger = Messenger(service)
            if (serviceTestQueued) {
                serviceTestQueued = false
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "service onServiceDisconnected")
            remoteMessenger = null
        }
    }

    inner class AppendCallbackList : CallbackList<String?>() {
        override fun onAddElement(s: String?) {
            bind.console.append(s)
            bind.console.append("\n")
            bind.sv.postDelayed({ bind.sv.fullScroll(ScrollView.FOCUS_DOWN) }, 10)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (conn != null) {
            RootService.unbind(conn!!)
        }
    }
}
