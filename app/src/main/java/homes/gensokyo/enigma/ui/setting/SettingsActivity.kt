package homes.gensokyo.enigma.ui.setting

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.SettingUtils.put
import homes.gensokyo.enigma.util.SettingUtils.sharedPreferences
import homes.gensokyo.enigma.util.TextUtils.toast

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var intervalMillis: Long = 15000
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val updateRatePreference = findPreference<EditTextPreference>("updateRate")
            val savedRate = get("updateRate", "15000")?.toLongOrNull() ?: 15000
            val clearPref = findPreference<Preference>("clear_preferences")

            intervalMillis = savedRate
            clearPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showClearConfirmationDialog()
                true
            }

            updateRatePreference?.setOnPreferenceChangeListener { preference, newValue ->
                val newRate = (newValue as String)
                if (newRate != null) {
                    put("updateRate", newRate)
                    Log.d("SettingsFragment", "newRate: $newRate")
                    true
                } else { false }
            }

            val appDetailsPreference = findPreference<Preference>("app_details")
            appDetailsPreference?.setOnPreferenceClickListener {
                showAppDetailsDialog()
                true
            }
            val restartPreference = findPreference<Preference>("restart_app")
            restartPreference?.setOnPreferenceClickListener {
                Log.d("SettingsFragment", "restart_app")
                restartApp()
                true
            }
        }



    private fun restartApp() {
        val context = requireContext()
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
        private fun showClearConfirmationDialog() {
            val context = requireContext()
            AlertDialog.Builder(context).apply {
                setTitle("清除软件数据")
                setMessage("你确定要清除所有的软件数据及缓存?")
                setPositiveButton("确定") { _, _ ->
                    clearPreferences()
                }
                setNegativeButton("取消", null)
                create()
                show()
            }
        }

        private fun clearPreferences() {

            val editor = sharedPreferences!!.edit()
            editor.clear()
            editor.apply()
            "Preferences cleared!".toast()
        }
        private fun showAppDetailsDialog() {
            val permissions = getAppPermissions()
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle("软件使用到的权限")
            dialogBuilder.setMessage(permissions.joinToString("\n"))
            dialogBuilder.setPositiveButton("确定", null)
            dialogBuilder.show()
        }

        private fun getAppPermissions(): List<String> {
            val packageInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                PackageManager.GET_PERMISSIONS
            )
            return packageInfo.requestedPermissions?.toList() ?: emptyList()
        }

    }
}