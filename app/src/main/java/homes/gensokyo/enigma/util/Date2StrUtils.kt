package homes.gensokyo.enigma.util

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


object DateUtils {
    @SuppressLint("SimpleDateFormat")
    private val dataFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    fun Date2Str (offSet: Int?, isMonthStart: Boolean = false) : String {
        return try {
            dataFmt.timeZone = TimeZone.getTimeZone("UTC+8")
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time


            offSet?.let { calendar.add(Calendar.DAY_OF_MONTH, it) }

            if(isMonthStart) calendar.set(Calendar.DAY_OF_MONTH, 1)
            LogUtils.d("DateUtils", "Date2Str: ${calendar.time}")
            dataFmt.format(calendar.time)


        } catch (e : Exception){
            //ToastUtils.showToastOnUiThread(MainActivity().applicationContext,"日期转换失败", 3)
            Log.e("DateUtils", "Date2Str: ", e)
            "fuck you idiot editor"
        }
    }


}