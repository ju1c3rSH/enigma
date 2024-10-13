package homes.gensokyo.enigma.ui.setting

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.util.LogUtils
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
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val updateRatePreference = findPreference<EditTextPreference>("updateRate")
            val clearPref = findPreference<Preference>("clear_preferences")

            clearPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showClearConfirmationDialog()
                true
            }
            updateRatePreference?.setOnPreferenceChangeListener { _, newValue ->
                put("updateRate", newValue as String)
                LogUtils.d("SettingsFragment", "newRate: $newValue")
                true
            }

            val appDetailsPreference = findPreference<Preference>("app_details")
            appDetailsPreference?.setOnPreferenceClickListener {
                showAppDetailsDialog()
                true
            }
            val restartPreference = findPreference<Preference>("restart_app")
            restartPreference?.setOnPreferenceClickListener {
                LogUtils.d("SettingsFragment", "restart_app")
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
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("清除软件数据")
                .setMessage("你确定要清除所有的软件数据及缓存?")
                .setPositiveButton("确定") { _, _ -> clearPreferences() }
                .setNegativeButton("取消", null)
                .show()
        }

        private fun clearPreferences() {
            sharedPreferences!!.edit().clear().apply()
            "Preferences cleared!".toast()
        }

        private fun showAppDetailsDialog() {
            val permissions = getAppPermissions()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("软件使用到的权限")
                .setMessage(permissions.joinToString("\n"))
                .setPositiveButton("确定", null)
                .show()
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
