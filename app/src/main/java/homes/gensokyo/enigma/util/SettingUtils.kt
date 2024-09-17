@file:Suppress("DEPRECATION")

package homes.gensokyo.enigma.util

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.preference.PreferenceManager
import homes.gensokyo.enigma.MainActivity.Companion.context

object SettingUtils {
    val DEVICE = "device"
    val sharedPreferences: SharedPreferences? =
        PreferenceManager.getDefaultSharedPreferences(context);

    inline fun <reified T> put(name: String, value: T) {
        when (value) {
            is Boolean -> sharedPreferences!!.edit().putBoolean(name, value).apply()

            is String -> sharedPreferences!!.edit().putString(name, value).apply()

            is Int -> sharedPreferences!!.edit().putInt(name, value).apply()

            is Long -> sharedPreferences!!.edit().putLong(name, value).apply()

            is Float -> sharedPreferences!!.edit().putFloat(name, value).apply()
        }
    }

    @SuppressLint("SdCardPath")
    inline fun <reified T> get(name: String, defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> sharedPreferences!!.getBoolean(
                name, defaultValue as? Boolean ?: false
            ) as T

            String::class -> {
                val str = sharedPreferences!!.getString(name, defaultValue as? String ?: "") as T
                if(str==""){
                    "/sdcard/"
                }
                str
            }

            Int::class -> sharedPreferences!!.getInt(name, defaultValue as? Int ?: 0) as T
            Long::class -> sharedPreferences!!.getLong(name, defaultValue as? Long ?: 0L) as T
            Float::class -> sharedPreferences!!.getFloat(name, defaultValue as? Float ?: 0F) as T
            else -> throw IllegalArgumentException("This type of class is not supported.")
        }
    }
}