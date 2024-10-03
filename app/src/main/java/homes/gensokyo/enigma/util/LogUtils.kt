package homes.gensokyo.enigma.util

import android.content.pm.ApplicationInfo
import android.util.Log
import homes.gensokyo.enigma.BuildConfig

class LogUtils {
    companion object {
        //val isDebuggable  =  ApplicationInfo.FLAG_DEBUGGABLE
        fun d(tag: String, msg: String) {

            if (BuildConfig.DEBUG) {
                Log.d(tag, msg)
            }
        }
    }
}