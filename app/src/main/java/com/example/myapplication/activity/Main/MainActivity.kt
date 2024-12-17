package com.example.myapplication.activity.Main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.activity.AIQA.QAActivity
import com.example.myapplication.activity.Blog.BlogActivity
import com.example.myapplication.activity.Me.MeActivity
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.NetworkUtils.sendPostRequestWithRequest
import com.google.android.gms.common.GoogleApiAvailability
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.OnPermissionCallback
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.Permission
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
            val activity:Activity = this
            val context:Context = this

            XXPermissions.with(this)
                // 申请单个权限
//            .permission(Permission.RECORD_AUDIO)
                // 申请多个权限
//            .permission(Permission.Group.CALENDAR)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(object : OnPermissionCallback {

                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (!allGranted) {
                            Toast.makeText(activity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                            return
                        }
                        Toast.makeText(activity, "获取录音和日历权限成功", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        if (doNotAskAgain) {
                            Toast.makeText(activity, "被永久拒绝授权，请手动授予录音和日历权限", Toast.LENGTH_SHORT).show()
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions)
                        } else {
                            Toast.makeText(activity, "获取录音和日历权限失败", Toast.LENGTH_SHORT).show()

                        }
                    }
                })
        } catch (e:Exception) {
            println("firebase error")
        }



        setContent {
            MyApplicationTheme {
                MainScreen(this)
            }
        }
        FirebaseApp.initializeApp(this)
        // 获取 Firebase token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // 获取到新的 FCM token
            val token = task.result
            Log.d(TAG, "FCM Token: $token")

            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            var myName = sharedPreferences.getString("name", "宝宝名字") ?: "宝宝名字"

            // 你可以在这里将 token 发送到你的服务器或进行其他处理
            lifecycleScope.launch {
                val requestBody = JSONObject().apply{
                    put("userName", myName)
                    put("token", token)
                }
                sendPostRequestWithRequest("api/fcm/token", requestBody.toString())
            }
        }

    }
    companion object {
        private const val TAG = "MainActivity"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CenterAlignedTopAppBarExample() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Home",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}

@Composable
fun ScrollContent(innerPadding: PaddingValues) {
    val range = 1..30

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(range.count()) { index ->
//            Text(text = "- List item number ${index + 1}")
        }
    }
}

sealed class NavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavItem("home", Icons.Filled.Home, "Home")
    object Share : NavItem("share", Icons.Filled.Share, "Blog")
    object QuestionAnswer : NavItem("questionanswer", Icons.Filled.QuestionAnswer, "Q&A")
    object Person : NavItem("person", Icons.Filled.Person, "Me")
}

@Composable
fun MainScreen(activity: Activity) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        CenterAlignedTopAppBarExample()
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) { HomeMainScreen(activity) }
            composable(NavItem.Share.route) { ShareScreen(activity) }
            composable(NavItem.QuestionAnswer.route) { QuestionAnswerScreen(activity) }
            composable(NavItem.Person.route) { PersonScreen(activity) }
        }
    }
}

@Composable
fun HomeMainScreen(activity: Activity) {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    Column(modifier = Modifier.padding(32.dp)) {
        Spacer(modifier = Modifier.height(64.dp))
        CustomButton(
            title = "打卡记录",
            description = getCheckin(activity),
            onClick = {
                val intent = Intent(activity, DailyLogActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        CustomButton(
            title = "闹钟",
            description = getAlarm(activity),
            onClick = {
                val intent = Intent(activity, AlarmActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        CustomButton(
            title = "疫苗",
            description = getImmunization(activity),
            onClick = {
                val intent = Intent(activity, ImmunizationActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavItem.Home,
        NavItem.Share,
        NavItem.QuestionAnswer,
        NavItem.Person
    )
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(activity: Activity) {
    val intent = Intent(activity, MainActivity::class.java)
    activity.startActivity(intent)
    activity.overridePendingTransition(0, 0)
    activity.finish()
}

@Composable
fun ShareScreen(activity: Activity) {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt("selectedItem", 0)
    editor.apply()

    val intent = Intent(activity, BlogActivity::class.java)
    activity.startActivity(intent)
    activity.overridePendingTransition(0, 0)
    activity.finish()
}

@Composable
fun QuestionAnswerScreen(activity: Activity) {
    val intent = Intent(activity, QAActivity::class.java)
    activity.startActivity(intent)
    activity.overridePendingTransition(0, 0)
    activity.finish()
}

@Composable
fun PersonScreen(activity: Activity) {
    val intent = Intent(activity, MeActivity::class.java)
    activity.startActivity(intent)
    activity.overridePendingTransition(0, 0)
    activity.finish()
}


@Composable
fun CustomButton(title: String, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray.copy(alpha = 0.2f))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Black
            )
            Text(
                text = description,
                fontSize = 10.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun getCheckin(activity: Activity): String {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    val babyId: Int = sharedPreferences.getInt("babyId", 0)
    fetchGrowthTracking(sharedPreferences, babyId)

    val checkIns by remember { mutableStateOf(loadCheckIns(sharedPreferences)) }

    val stringBuilder = StringBuilder()
    for (i in checkIns.indices.reversed()) {
        val checkIn = checkIns[i]
        stringBuilder.append("${checkIn.measurementDate}  身高:${checkIn.height}cm 体重:${checkIn.weight}kg\n")
    }
    return stringBuilder.toString()
}

@Composable
fun getImmunization(activity: Activity): String {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    val babyId: Int = sharedPreferences.getInt("babyId", 0)
    fetchImmunizations(sharedPreferences, babyId)

    val immunizations by remember { mutableStateOf(loadImmunizations(sharedPreferences)) }

    val StringBuilder = StringBuilder()
    for (i in immunizations.indices.reversed()) {
        val immunization = immunizations[i]
        StringBuilder.append("${immunization.vaccineName}\n")
    }

    return StringBuilder.toString()
}

@Composable
fun getAlarm(activity: Activity): String {
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    val babyId: Int = sharedPreferences.getInt("babyId", 0)
    fetchAlarms(sharedPreferences, babyId)

    val alarms by remember { mutableStateOf(loadAlarms(sharedPreferences)) }

    val StringBuilder = StringBuilder()
    for (i in alarms.indices.reversed()) {
        val alarm = alarms[i]
        if (alarm.active) {
            StringBuilder.append("${alarm.alarmTime.substring(0, 10)} ${alarm.alarmTime.substring(11)}: ${alarm.activityType} \n")
        }
    }
    val String = StringBuilder.toString()

    return String
}