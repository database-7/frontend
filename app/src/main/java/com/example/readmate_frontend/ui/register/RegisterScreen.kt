package com.example.readmate_frontend.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.readmate_frontend.ui.component.Logo2
import com.example.readmate_frontend.ui.component.SignupTextField
import com.example.readmate_frontend.viewmodel.RegisterUiState
import com.example.readmate_frontend.viewmodel.RegisterViewModel


@Composable
fun SignupScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val userId       by viewModel.userId.collectAsState()
    val password     by viewModel.password.collectAsState()
    val userName     by viewModel.userName.collectAsState()
    val uiState      by viewModel.uiState.collectAsState()

    var passwordConfirm by remember { mutableStateOf("") }
    var passwordMismatch by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            onRegisterSuccess()
            onLoginClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(160.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp)
        ) {
            Logo2(48.sp,18.sp)
        }
        Spacer(modifier = Modifier.height(80.dp))


        SignupTextField("닉네임", userName, viewModel::onUserNameChange)
        Spacer(modifier = Modifier.height(16.dp))
        SignupTextField("아이디", userId, viewModel::onUserIdChange)
        Spacer(modifier = Modifier.height(16.dp))
        SignupTextField("비밀번호", password, viewModel::onPasswordChange, isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        SignupTextField(
            placeholderText = "비밀번호 확인",
            value = passwordConfirm,
            onValueChange = {
                passwordConfirm = it
                passwordMismatch = it != password
            },
            isPassword = true
        )
        if (passwordMismatch) {
            Text(
                "비밀번호가 일치하지 않습니다.",
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (uiState is RegisterUiState.Error) {
            Text(
                (uiState as RegisterUiState.Error).message,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        Button(
            onClick = {
                if (password != passwordConfirm) {
                    passwordMismatch = true
                } else {
                    viewModel.register()
                }
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(360.dp)
                .height(76.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCBB38A),
                contentColor = Color.White
            )
        )
        {
            if (uiState is RegisterUiState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("회원가입", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "이미 계정이 있으신가요?",
                fontSize = 20.sp,
                color = Color(0xFFB79E73),
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { onLoginClick() }
            ) {
                Text(
                    "로그인",
                    fontSize = 20.sp,
                    color = Color(0xFF817052),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun SignupPreview(){
    SignupScreen()
}