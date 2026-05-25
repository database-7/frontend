package com.example.readmate_frontend.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import com.example.readmate_frontend.ui.component.MyTextField2
import com.example.readmate_frontend.viewmodel.EditPasswordUiState
import com.example.readmate_frontend.viewmodel.EditPasswordViewModel

@Composable
fun EditPasswordScreen(
    onAlarmClick:() -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: EditPasswordViewModel = hiltViewModel()
) {
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is EditPasswordUiState.Success) {
            onSuccess()
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
        MyTextField2(
            placeholderText = "변경 비밀번호",
            value = newPassword,
            onValueChange = viewModel::onNewPasswordChange
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyTextField2(
            placeholderText = "변경 비밀번호 확인",
            value = confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange
        )

        if (uiState is EditPasswordUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as EditPasswordUiState.Error).message,
                color = Color(0xFFE57373),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.editPassword() },
            enabled = uiState !is EditPasswordUiState.Loading,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .width(360.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCBB38A),
                contentColor = Color.White
            )
        )
        {
            if (uiState is EditPasswordUiState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("변경하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Preview
@Composable
fun EditPasswordPreview(){
    EditPasswordScreen()
}