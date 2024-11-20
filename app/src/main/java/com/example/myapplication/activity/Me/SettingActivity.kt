package com.example.myapplication.activity.Me

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.activity.Main.LoginActivity
import com.example.myapplication.activity.Main.saveLoginStatus
import com.example.myapplication.ui.theme.MyApplicationTheme

class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SettingScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingScreen(activity: Activity) {
    var backFlag by remember { mutableStateOf(false) }

    var quitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "设置",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { backFlag = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()
                    .padding((innerPadding))
                    .padding(start = 16.dp, end = 16.dp)
        ) {
            ButtonWithBars("账户设置") {
                val intent = Intent(activity, AccountActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            ButtonWithBars("消息提示") {
            }
            Divider(color = Color.LightGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))
            ButtonWithBars("关于") {
            }
            ButtonWithBars("帮助") {
            }
            Divider(color = Color.LightGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))
            ButtonWithBars("登出") {
                quitDialog = true
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
        }

    }
    if (backFlag) {
        val intent = Intent(activity, MeActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    if(quitDialog) {
        AlertDialog(
            onDismissRequest = { quitDialog = false },
            text = { Text(
                text = "你确定要退出账户吗？",
                modifier = Modifier.padding(16.dp)
            ) },
            confirmButton = {
                TextButton(
                    onClick = {
                        quitDialog = false
                        saveLoginStatus(activity, false)
                        val intent = Intent(activity, LoginActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { quitDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}