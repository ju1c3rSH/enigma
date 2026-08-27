package homes.gensokyo.enigma

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import homes.gensokyo.enigma.BuildConfig
import homes.gensokyo.enigma.MainApplication
import homes.gensokyo.enigma.`interface`.GithubApiService
import homes.gensokyo.enigma.`interface`.ReleaseResponse
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.ui.compose.theme.EnigmaTheme
import homes.gensokyo.enigma.ui.oobe.OOBEActivity
import homes.gensokyo.enigma.ui.overview.OverviewScreen
import homes.gensokyo.enigma.ui.records.RecordsScreen
import homes.gensokyo.enigma.util.NetworkUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.TextUtils.compareVersions
import homes.gensokyo.enigma.util.TextUtils.toast
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    companion object {
        // 保留全局引用，供非 Compose 代码（Util/Service）使用
        lateinit var context: android.content.Context
        lateinit var gson: com.google.gson.Gson
        var repository: UserRepository = MainApplication.repository
        var apiService = MainApplication.apiService
        var githubApiService = MainApplication.githubApiService
    }

    private val usrViewModel: UsrdataModel by viewModels(
        factoryProducer = { UsrdataModelFactory(UserRepository()) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        gson = MainApplication.gson
        apiService = MainApplication.apiService
        githubApiService = MainApplication.githubApiService
        repository = MainApplication.repository

        if (get("isFirst", true)) {
            startActivity(Intent(this, OOBEActivity::class.java))
            finish()
            return
        }
        enableEdgeToEdge()
        setContent {
            EnigmaTheme {
                AppRoot(usrViewModel)
            }
        }
    }
}

@Composable
private fun AppRoot(vm: UsrdataModel) {
    val navController = rememberNavController()

    if (!NetworkUtils().isNetworkAvailable(MainApplication.context)) {
        "No internet connection available".toast()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    Triple("home", R.string.navigation_home, R.drawable.ic_home_black_24dp),
                    Triple("records", R.string.navigation_dashboard, R.drawable.ic_dashboard_black_24dp)
                )
                items.forEach { (route, labelRes, iconRes) ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(iconRes), contentDescription = null) },
                        label = { Text(stringResource(labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(navController, startDestination = "home") {
                composable("home") {
                    OverviewScreen(
                        studentData = vm.studentData,
                        memberFlow = vm.memberFlow,
                        onViewAll = { navController.navigate("records") }
                    )
                }
                composable("records") {
                    RecordsScreen(
                        studentData = vm.studentData,
                        memberFlowAll = vm.memberFlowAll,
                        queryData = vm.queryData
                    )
                }
            }
        }
        UpdateChecker()
    }
}

@Composable
private fun UpdateChecker() {
    var show by remember { mutableStateOf(false) }
    var release by remember { mutableStateOf<ReleaseResponse?>(null) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val resp = MainApplication.githubApiService.getLatestRelease()
                if (compareVersions(BuildConfig.VERSION_NAME, resp.tag_name) < 0) {
                    release = resp
                    show = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    if (show && release != null) {
        val r = release!!
        AlertDialog(
            onDismissRequest = { show = false },
            title = { Text("新版本可用") },
            text = {
                Text("当前版本: ${BuildConfig.VERSION_NAME}\n最新版本: ${r.tag_name}\n${r.name}\n\n${r.body}")
            },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/ju1c3rSH/enigma/releases/latest")
                    )
                    MainApplication.context.startActivity(intent)
                    show = false
                }) { Text("更新") }
            },
            dismissButton = {
                TextButton(onClick = { show = false }) { Text("取消") }
            }
        )
    }
}
