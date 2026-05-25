package com.example.readmate_frontend.ui.mypage

import android.accounts.Account
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmate_frontend.ui.component.MyButton
import com.example.readmate_frontend.viewmodel.DeregisterUiState
import com.example.readmate_frontend.viewmodel.DeregisterViewModel

@Composable
fun AccountScreen(
    onAlarmClick:() -> Unit = {},
    onEditPasswoedClick:() -> Unit = {},
    onDeregisterSuccess: () -> Unit = {},
    viewModel: DeregisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is DeregisterUiState.Success) {
            onDeregisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Logo(onAlarmClick = onAlarmClick)
        Spacer(modifier = Modifier.height(16.dp))
        MyButton("비밀번호 변경", onEditPasswoedClick)
        Spacer(modifier = Modifier.height(8.dp))
        MyButton("계정 탈퇴", onClick = { showDialog = true })

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("계정 탈퇴", fontWeight = FontWeight.Bold) },
                text = { Text("정말 탈퇴하시겠어요?\n탈퇴 후 데이터는 복구되지 않습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            viewModel.deregister()
                        }
                    ) {
                        if (uiState is DeregisterUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("탈퇴하기", color = Color(0xFFE57373), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("취소", color = Color(0xFF817052))
                    }
                }
            )
        }

        if (uiState is DeregisterUiState.Error) {
            Text(
                text = (uiState as DeregisterUiState.Error).message,
                color = Color(0xFFE57373),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Preview
@Composable
fun AccountPreview(){
    AccountScreen()
}