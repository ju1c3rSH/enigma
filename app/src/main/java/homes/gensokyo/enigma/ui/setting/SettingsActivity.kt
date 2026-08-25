package homes.gensokyo.enigma.ui.setting

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.ui.compose.theme.EnigmaTheme
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.SettingUtils.put
import homes.gensokyo.enigma.util.SettingUtils.sharedPreferences
import homes.gensokyo.enigma.util.TextUtils.toast

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnigmaTheme {
                SettingsScreen(onBack = { finish() })
            }
        }
    }
}

private fun restartApp(context: Context) {
    val pm = context.packageManager
    val intent = pm.getLaunchIntentForPackage(context.packageName)
    val mainIntent = Intent.makeRestartActivityTask(intent?.component)
    context.startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

private fun getAppPermissions(context: Context): List<String> {
    val packageInfo = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_PERMISSIONS
    )
    return packageInfo.requestedPermissions?.toList() ?: emptyList()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    var editDialogKey by remember { mutableStateOf<String?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var showAppDetails by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.oobe_prev)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SectionLabel(stringResource(R.string.common_setting_header))
            SettingCard {
                SettingRow(
                    icon = Icons.Filled.Notifications,
                    title = stringResource(R.string.update_rate),
                    value = get("updateRate", "60000") + " ms"
                ) { editDialogKey = "updateRate" }
                CardDivider()
                SettingRow(
                    icon = Icons.Filled.Refresh,
                    title = stringResource(R.string.dashboard_update_limit),
                    value = get("dashboard_update_limit", 50).toString() + " " + stringResource(R.string.days_unit)
                ) { editDialogKey = "dashboard_update_limit" }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel(stringResource(R.string.danger_section_header))
            SettingCard {
                SettingRow(
                    icon = Icons.Filled.Delete,
                    title = stringResource(R.string.clear_preferences),
                    subtitle = stringResource(R.string.clear_preferences_summary),
                    tint = scheme.error
                ) { showClearConfirm = true }
                CardDivider()
                SettingRow(
                    icon = Icons.Filled.Info,
                    title = stringResource(R.string.app_details_title),
                    subtitle = stringResource(R.string.app_details_summary)
                ) { showAppDetails = true }
                CardDivider()
                SettingRow(
                    icon = Icons.Filled.Refresh,
                    title = stringResource(R.string.restart_app_title),
                    subtitle = stringResource(R.string.restart_app_summary)
                ) { restartApp(context) }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel(stringResource(R.string.ComingSoon_header))
            SettingCard {
                SwitchSettingRow(
                    icon = Icons.Filled.Info,
                    title = stringResource(R.string.help_us_better),
                    checked = false,
                    enabled = false,
                    onCheckedChange = {}
                )
                CardDivider()
                SwitchSettingRow(
                    icon = Icons.Filled.Notifications,
                    title = stringResource(R.string.attachment_title),
                    subtitle = stringResource(R.string.auto_sync_summary_off),
                    checked = false,
                    enabled = false,
                    onCheckedChange = {}
                )
            }
            Spacer(Modifier.height(28.dp))
        }
    }

    editDialogKey?.let { key ->
        val isRate = key == "updateRate"
        NumberEditDialog(
            title = stringResource(if (isRate) R.string.update_rate else R.string.dashboard_update_limit),
            hint = stringResource(if (isRate) R.string.update_rate_hint else R.string.dashboard_update_limit_hint),
            presets = if (isRate) listOf("30000", "60000", "300000") else listOf("30", "50", "90"),
            initial = if (isRate) get(key, "60000") else get(key, 50).toString(),
            onDismiss = { editDialogKey = null },
            onConfirm = { value ->
                if (isRate) put(key, value) else put(key, value.toInt())
                LogUtils.d("SettingsScreen", "$key -> $value")
                editDialogKey = null
            }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.clear_preferences)) },
            text = { Text(stringResource(R.string.clear_confirm_text)) },
            confirmButton = {
                TextButton(onClick = {
                    sharedPreferences!!.edit().clear().apply()
                    showClearConfirm = false
                    context.getString(R.string.cleared_toast).toast()
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showAppDetails) {
        AlertDialog(
            onDismissRequest = { showAppDetails = false },
            title = { Text(stringResource(R.string.app_details_title)) },
            text = { Text(getAppPermissions(context).joinToString("\n")) },
            confirmButton = {
                TextButton(onClick = { showAppDetails = false }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showAppDetails = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 10.dp)
    )
}

@Composable
private fun SettingCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(Modifier.padding(vertical = 4.dp)) { content() }
    }
}

@Composable
private fun CardDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = 64.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
}

@Composable
private fun RowIcon(icon: ImageVector, tint: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(36.dp)
            .background(tint.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    subtitle: String? = null,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(icon, tint)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = scheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
            }
        }
        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SwitchSettingRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    subtitle: String? = null,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(icon, if (enabled) scheme.primary else scheme.onSurfaceVariant)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) scheme.onSurface else scheme.onSurfaceVariant
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

@Composable
private fun NumberEditDialog(
    title: String,
    hint: String,
    presets: List<String>,
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var value by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = { v -> value = v.filter { it.isDigit() } },
                    singleLine = true,
                    label = { Text(hint) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        stringResource(R.string.presets_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    presets.forEach { p ->
                        TextButton(onClick = { value = p }) { Text(p) }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (value.isNotBlank()) onConfirm(value) },
                enabled = value.isNotBlank()
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
