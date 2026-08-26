package homes.gensokyo.enigma.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.bean.UserDataBean
import homes.gensokyo.enigma.bean.memberflowbean
import homes.gensokyo.enigma.ui.compose.component.ConsumeRow
import homes.gensokyo.enigma.ui.compose.component.EmptyHint
import homes.gensokyo.enigma.ui.compose.component.SectionTitle
import homes.gensokyo.enigma.ui.compose.component.StatCard
import homes.gensokyo.enigma.ui.compose.component.TicketCard
import homes.gensokyo.enigma.ui.compose.component.LoadingPlaceholder
import homes.gensokyo.enigma.viewmodel.DataState
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun OverviewScreen(
    studentData: LiveData<DataState<UserDataBean>>,
    memberFlow: LiveData<memberflowbean?>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by studentData.observeAsState()
    val flow by memberFlow.observeAsState()
    //ViewModel 已按 consumeTime 降序投递；今日消费额只在数据变化时重算
    val records = remember(flow) { flow?.datas.orEmpty() }
    val todayTotal = remember(records) { records.sumOf { it.amount ?: 0.0 } }
    val loading = state !is DataState.Success

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            val data = (state as? DataState.Success)?.data
            TicketCard(
                studentName = data?.studentName ?: "加载中",
                className = data?.className ?: "",
                cardNumber = data?.cardNumber ?: "",
                balance = data?.balance ?: "--",
                consumptionCount = data?.consumptionCount ?: "--",
                loading = loading
            )
        }
        if (state is DataState.Error) {
            item {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = stringResource(R.string.error_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.today_consume),
                    value = "¥%.2f".format(todayTotal),
                    loading = loading,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.today_count),
                    value = records.size.toString(),
                    loading = loading,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                SectionTitle(stringResource(R.string.recent_consumption))
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onViewAll, Modifier.padding(end = 8.dp)) {
                    Text(stringResource(R.string.view_all))
                }
            }
        }
        if (loading && records.isEmpty()) {
            item { LoadingPlaceholder() }
        } else if (records.isEmpty()) {
            item { EmptyHint(stringResource(R.string.empty_records)) }
        } else {
            items(count = minOf(records.size, 8), key = { records[it].flowId ?: "$it" }) { i ->
                val r = records[i]
                ConsumeRow(
                    deviceName = r.deviceName,
                    placeName = r.placeName,
                    consumeTime = r.consumeTime,
                    amount = r.amount,
                    balanceAfter = r.balance,
                    onClick = onViewAll
                )
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}
