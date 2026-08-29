@file:Suppress("DEPRECATION")

package homes.gensokyo.enigma.util

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.preference.PreferenceManager
import homes.gensokyo.enigma.MainApplication

object SettingUtils {
    // 使用 Application context 避免持有已销毁的 Activity 实例；懒加载确保 MainApplication 已初始化
    val sharedPreferences: SharedPreferences? by lazy {
        PreferenceManager.getDefaultSharedPreferences(MainApplication.context)
    }

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
            Boolean::class -> try {
                sharedPreferences!!.getBoolean(
                    name, defaultValue as? Boolean ?: false
                ) as T
            } catch (e: ClassCastException) { defaultValue }

            String::class -> try {
                val str = sharedPreferences!!.getString(name, defaultValue as? String ?: "") as T
                str
            } catch (e: ClassCastException) { defaultValue }

            Int::class -> try {
                sharedPreferences!!.getInt(name, defaultValue as? Int ?: 0) as T
            } catch (e: ClassCastException) { defaultValue }

            Long::class -> try {
                sharedPreferences!!.getLong(name, defaultValue as? Long ?: 0L) as T
            } catch (e: ClassCastException) { defaultValue }

            Float::class -> try {
                sharedPreferences!!.getFloat(name, defaultValue as? Float ?: 0F) as T
            } catch (e: ClassCastException) { defaultValue }

            else -> throw IllegalArgumentException("This type of class is not supported.")
        }
    }
}