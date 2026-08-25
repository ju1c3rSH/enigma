package homes.gensokyo.enigma.ui.compose.component

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.bean.UserDataBean
import homes.gensokyo.enigma.ui.about.AboutActivity
import homes.gensokyo.enigma.ui.setting.SettingsActivity
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.viewmodel.DataState
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun ScreenHeader(
    title: String,
    studentData: LiveData<DataState<UserDataBean>>,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit,
) {
    val state by studentData.observeAsState()
    val data = (state as? DataState.Success)?.data
    val avatarUrl = data?.headSculpture?.takeIf { it.isNotBlank() }
        ?.let { AppConstants.personPhotoBase + it }
    val name = data?.studentName ?: "卡"
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onAvatarClick) {
            AvatarImage(url = avatarUrl, name = name, size = 34.dp)
        }
        Text(text = name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineSheet(
    studentData: LiveData<DataState<UserDataBean>>,
    onDismiss: () -> Unit,
) {
    val state by studentData.observeAsState()
    val data = (state as? DataState.Success)?.data
    val context = LocalContext.current
    val avatarUrl = data?.headSculpture?.takeIf { it.isNotBlank() }
        ?.let { AppConstants.personPhotoBase + it }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarImage(
                    url = avatarUrl,
                    name = data?.studentName ?: "卡",
                    size = 56.dp
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = data?.studentName ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = listOfNotNull(data?.studentNamePinyin, data?.className).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            KeyValueRow(stringResource(R.string.card_number_label), data?.cardNumber ?: "--")
            KeyValueRow(stringResource(R.string.balance_label), data?.balance?.let { "¥$it" } ?: "--")
            KeyValueRow(stringResource(R.string.consume_count_label), data?.consumptionCount ?: "--")
            HorizontalDivider(Modifier.padding(vertical = 6.dp))
            Row(Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.mine_settings)) }
                TextButton(
                    onClick = { context.startActivity(Intent(context, AboutActivity::class.java)) },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.mine_about)) }
            }
            Text(
                text = stringResource(R.string.disclaimer),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
