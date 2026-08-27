package homes.gensokyo.enigma.ui.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import homes.gensokyo.enigma.ui.compose.theme.AccentGold

fun formatAmount(amount: Double?): String =
    if (amount == null) "--" else "¥%.2f".format(amount)

//"2026-08-26 12:33:11" -> "08-26 12:33"
fun compactDateTime(s: String?): String =
    if (s == null || s.length < 16) s ?: "--" else s.substring(5, 10) + " " + s.substring(11, 16)

//"2026-08-26 12:33:11" -> "12:33"
fun compactTimeHM(s: String?): String =
    if (s == null || s.length < 16) "--" else s.substring(11, 16)

@Composable
private fun RowSkeleton(
    leading: @Composable () -> Unit,
    title: String,
    subtitle: String,
    trailingMain: String?,
    trailingSub: String? = null,
    trailingMainTone: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading()
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = scheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (trailingMain != null) {
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = trailingMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = trailingMainTone
                )
                if (trailingSub != null) {
                    Text(
                        text = trailingSub,
                        style = MaterialTheme.typography.labelSmall,
                        color = scheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

//品牌金点徽章：白/浅盘 + 金点，呼应启动图标的破盘语言
@Composable
fun DotBadge(modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(34.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(8.dp).background(AccentGold, CircleShape))
    }
}

@Composable
fun FaceBadge(modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(34.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Face,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ConsumeRow(
    deviceName: String?,
    placeName: String?,
    timeText: String?,
    amount: Double?,
    balanceAfter: Double?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth()) {
        RowSkeleton(
            leading = { DotBadge() },
            title = deviceName ?: placeName ?: "未知设备",
            subtitle = listOfNotNull(
                placeName?.takeIf { it != deviceName && !it.isNullOrBlank() },
                timeText
            ).joinToString(" · "),
            trailingMain = formatAmount(amount),
            trailingMainTone = if ((amount ?: 0.0) < 0) scheme.error else scheme.onSurface,
            trailingSub = "余额 ${formatAmount(balanceAfter)}",
            onClick = onClick
        )
        HorizontalDivider(color = scheme.outlineVariant.copy(alpha = 0.4f))
    }
}

@Composable
fun FaceRecordRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth()) {
        RowSkeleton(
            leading = { FaceBadge() },
            title = title,
            subtitle = subtitle,
            trailingMain = null,
            onClick = onClick
        )
        HorizontalDivider(
            modifier = Modifier.padding(start = 62.dp),
            color = scheme.outlineVariant.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun KeyValueRow(key: String, value: String, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.width(88.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
