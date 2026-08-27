package homes.gensokyo.enigma.ui.records

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import homes.gensokyo.enigma.BuildConfig
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.bean.QueryData
import homes.gensokyo.enigma.bean.QueryResponse
import homes.gensokyo.enigma.bean.memberflowbean
import homes.gensokyo.enigma.ui.compose.component.ConsumeRow
import homes.gensokyo.enigma.ui.compose.component.EmptyHint
import homes.gensokyo.enigma.ui.compose.component.FaceRecordRow
import homes.gensokyo.enigma.ui.compose.component.KeyValueRow
import homes.gensokyo.enigma.ui.compose.component.LoadingPlaceholder
import homes.gensokyo.enigma.ui.compose.component.MineSheet
import homes.gensokyo.enigma.ui.compose.component.ScreenHeader
import homes.gensokyo.enigma.ui.compose.component.compactDateTime
import homes.gensokyo.enigma.ui.compose.component.compactTimeHM
import homes.gensokyo.enigma.ui.compose.component.formatAmount
import homes.gensokyo.enigma.viewmodel.DataState
import homes.gensokyo.enigma.bean.UserDataBean
import androidx.compose.runtime.livedata.observeAsState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FlowList(
    records: List<memberflowbean.Data>,
    loading: Boolean,
    onTap: (memberflowbean.Data) -> Unit,
) {
    if (loading && records.isEmpty()) {
        LoadingPlaceholder(Modifier.padding(top = 8.dp))
    } else if (records.isEmpty() && !BuildConfig.DEBUG) {
        EmptyHint(stringResource(R.string.empty_records), Modifier.padding(top = 32.dp))
    } else {
        //DEBUG 且无真实数据时用样例预览版式，release 永远走真实数据/空态
        val display = remember(records) {
            if (records.isEmpty()) debugSampleRecords() else records
        }
        val sections = remember(display) { buildDaySections(display) }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            sections.forEach { sec ->
                stickyHeader(key = "h_${sec.label}") {
                    DayHeader(label = sec.label, total = sec.total)
                }
                items(
                    count = sec.rows.size,
                    key = { sec.rows[it].flowId ?: "${sec.label}_$it" }
                ) { i ->
                    val r = sec.rows[i]
                    ConsumeRow(
                        deviceName = r.deviceName,
                        placeName = r.placeName,
                        timeText = compactTimeHM(r.consumeTime),
                        amount = r.amount,
                        balanceAfter = r.balance,
                        onClick = { onTap(r) }
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

private data class DaySection(val label: String, val total: Double, val rows: List<memberflowbean.Data>)

//仅供 DEBUG 预览版式：相对今天生成跨 4 天的假流水
private fun debugSampleRecords(): List<memberflowbean.Data> {
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    val base = Calendar.getInstance()
    fun row(dayOffset: Int, hour: Int, minute: Int, seq: Int, device: String, place: String, amount: Double, balance: Double) =
        memberflowbean.Data(
            flowId = "dbg_${dayOffset}_$seq",
            deviceName = device,
            placeName = place,
            consumeTime = fmt.format((base.clone() as Calendar).apply {
                add(Calendar.DATE, dayOffset); set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, seq)
            }.time),
            amount = amount,
            balance = balance,
            merchantName = "样例商户",
            serialNumber = "DBG$dayOffset$seq",
            walletName = "主钱包"
        )
    return listOf(
        row(0, 12, 5, 1, "第一食堂一楼", "第一食堂", 13.50, 156.20),
        row(0, 8, 3, 2, "第一食堂一楼", "第一食堂", 6.00, 169.70),
        row(-1, 17, 42, 3, "校园超市", "超市小卖部", 9.90, 175.70),
        row(-1, 11, 58, 4, "第二食堂二楼", "第二食堂", 14.00, 185.70),
        row(-2, 16, 20, 5, "图书馆打印店", "图书馆", 2.50, 199.70),
        row(-2, 12, 10, 6, "第一食堂一楼", "第一食堂", 13.00, 202.20),
        row(-3, 7, 55, 7, "风雨操场小卖部", "风雨操场", 5.00, 215.20)
    )
}

//records 已按 consumeTime 降序，groupBy 保持相遇顺序，节即按时间倒序排列
private fun buildDaySections(records: List<memberflowbean.Data>): List<DaySection> {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val cal = Calendar.getInstance()
    val todayKey = fmt.format(cal.time)
    cal.add(Calendar.DATE, -1)
    val yesterdayKey = fmt.format(cal.time)
    val thisYear = todayKey.take(4)
    return records.groupBy { it.consumeTime?.take(10) ?: "" }.map { (key, rows) ->
        val label = when {
            key == todayKey -> "今天"
            key == yesterdayKey -> "昨天"
            key.length < 10 -> "未知日期"
            key.take(4) != thisYear ->
                "${key.take(4)}年${key.substring(5, 7).toInt()}月${key.substring(8, 10).toInt()}日"
            else -> "${key.substring(5, 7).toInt()}月${key.substring(8, 10).toInt()}日"
        }
        DaySection(label, rows.sumOf { it.amount ?: 0.0 }, rows)
    }
}

@Composable
private fun DayHeader(label: String, total: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "合计 ¥%.2f".format(total),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FaceList(query: QueryResponse?) {
    val datas = remember(query) { query?.datas.orEmpty().sortedByDescending { it.publishTime } }
    if (datas.isEmpty() && !BuildConfig.DEBUG) {
        EmptyHint(stringResource(R.string.empty_records), Modifier.padding(top = 32.dp))
    } else {
        val display = remember(datas) { if (datas.isEmpty()) debugSampleQueries() else datas }
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), modifier = Modifier.fillMaxSize()) {
            items(count = display.size, key = { display[it].id }) { i ->
                val d = display[i]
                FaceRecordRow(
                    title = d.title.ifBlank { d.typeName },
                    subtitle = listOfNotNull(
                        d.typeName.takeIf { it.isNotBlank() },
                        compactDateTime(d.publishTime)
                    ).joinToString(" · "),
                    onClick = {}
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

//仅供 DEBUG 预览版式：假刷脸通知
private fun debugSampleQueries(): List<QueryData> {
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    val base = Calendar.getInstance()
    fun q(dayOffset: Int, hour: Int, minute: Int, seq: Int, title: String): QueryData =
        QueryData(
            id = 900000 + seq,
            tenantId = 1, originId = 1,
            typeCode = 3, typeName = "人脸支付",
            title = title,
            publisherName = "智威校园", publisherCode = null,
            receiverName = null, receiverCode = null,
            copyPersonName = "家长", copyPersonCode = "dbg",
            content = "", picture = "",
            publishTime = fmt.format((base.clone() as Calendar).apply {
                add(Calendar.DATE, dayOffset); set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
            }.time),
            status = 1, isRead = 0,
            reserver1 = "", reserver2 = "", reserver3 = null, reserver4 = "", reserver5 = null
        )
    return listOf(
        q(0, 12, 31, 1, "第三食堂消费提醒"),
        q(0, 7, 50, 2, "第一食堂早餐提醒"),
        q(-1, 18, 5, 3, "校园超市消费提醒"),
        q(-2, 12, 15, 4, "第二食堂消费提醒")
    )
}
