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
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time
            dataFmt.timeZone = TimeZone.getTimeZone("UTC+8")
            if (offSet != null) calendar.add(Calendar.DAY_OF_MONTH, offSet)
            if(isMonthStart) calendar.set(Calendar.DAY_OF_MONTH, 1)
            val offsetDate = calendar.time
            dataFmt.format(offsetDate)
        } catch (e : Exception){
            //ToastUtils.showToastOnUiThread(MainActivity().applicationContext,"日期转换失败", 3)
            Log.e("DateUtils", "Date2Str: ", e)
            "fuck you idiot editor"
        }
    }


}