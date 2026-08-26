package homes.gensokyo.enigma.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.bean.QueryResponse
import homes.gensokyo.enigma.bean.memberflowbean
import homes.gensokyo.enigma.ui.compose.component.ConsumeRow
import homes.gensokyo.enigma.ui.compose.component.EmptyHint
import homes.gensokyo.enigma.ui.compose.component.FaceRecordRow
import homes.gensokyo.enigma.ui.compose.component.KeyValueRow
import homes.gensokyo.enigma.ui.compose.component.LoadingPlaceholder
import homes.gensokyo.enigma.ui.compose.component.MineSheet
import homes.gensokyo.enigma.ui.compose.component.ScreenHeader
import homes.gensokyo.enigma.ui.compose.component.formatAmount
import homes.gensokyo.enigma.viewmodel.DataState
import homes.gensokyo.enigma.bean.UserDataBean
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    studentData: LiveData<DataState<UserDataBean>>,
    memberFlowAll: LiveData<memberflowbean?>,
    queryData: LiveData<QueryResponse>,
    modifier: Modifier = Modifier,
) {
    var tab by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<memberflowbean.Data?>(null) }
    var mineOpen by remember { mutableStateOf(false) }

    val flow by memberFlowAll.observeAsState()
    val queries by queryData.observeAsState()
    //ViewModel 已按 consumeTime 降序投递
    val records = flow?.datas.orEmpty()

    Column(modifier.fillMaxSize()) {
        ScreenHeader(
            title = stringResource(R.string.records_title),
            studentData = studentData,
            onAvatarClick = { mineOpen = true }
        )
        SingleChoiceSegmentedButtonRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            SegmentedButton(
                selected = tab == 0,
                onClick = { tab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text(stringResource(R.string.flow_tab)) }
            SegmentedButton(
                selected = tab == 1,
                onClick = { tab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text(stringResource(R.string.face_tab)) }
        }
        when (tab) {
            0 -> FlowList(records, loading = flow == null) { selected = it }
            else -> FaceList(queries)
        }
    }

    if (mineOpen) {
        MineSheet(studentData = studentData, onDismiss = { mineOpen = false })
    }

    selected?.let { record ->
        ModalBottomSheet(onDismissRequest = { selected = null }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = formatAmount(record.amount),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                KeyValueRow(stringResource(R.string.detail_device), record.deviceName ?: "--")
                KeyValueRow(stringResource(R.string.detail_place), record.placeName ?: "--")
                KeyValueRow(stringResource(R.string.detail_time), record.consumeTime ?: "--")
                KeyValueRow(stringResource(R.string.detail_balance), formatAmount(record.balance))
                KeyValueRow(stringResource(R.string.detail_merchant), record.merchantName ?: "--")
                KeyValueRow(stringResource(R.string.detail_serial), record.serialNumber ?: "--")
                KeyValueRow(stringResource(R.string.detail_wallet), record.walletName ?: "--")
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FlowList(
    records: List<memberflowbean.Data>,
    loading: Boolean,
    onTap: (memberflowbean.Data) -> Unit,
) {
    if (loading && records.isEmpty()) {
        LoadingPlaceholder(Modifier.padding(top = 8.dp))
    } else if (records.isEmpty()) {
        EmptyHint(stringResource(R.string.empty_records), Modifier.padding(top = 32.dp))
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = records.size, key = { records[it].flowId ?: "f$it" }) { i ->
                val r = records[i]
                ConsumeRow(
                    deviceName = r.deviceName,
                    placeName = r.placeName,
                    consumeTime = r.consumeTime,
                    amount = r.amount,
                    balanceAfter = r.balance,
                    onClick = { onTap(r) }
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FaceList(query: QueryResponse?) {
    val datas = remember(query) { query?.datas.orEmpty().sortedByDescending { it.publishTime } }
    if (datas.isEmpty()) {
        EmptyHint(stringResource(R.string.empty_records), Modifier.padding(top = 32.dp))
    } else {
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), modifier = Modifier.fillMaxSize()) {
            items(count = datas.size, key = { datas[it].id }) { i ->
                val d = datas[i]
                FaceRecordRow(
                    icon = Icons.Filled.Face,
                    title = d.title.ifBlank { d.typeName },
                    subtitle = listOfNotNull(d.typeName.takeIf { it.isNotBlank() }, d.publishTime)
                        .joinToString(" · "),
                    onClick = {}
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
