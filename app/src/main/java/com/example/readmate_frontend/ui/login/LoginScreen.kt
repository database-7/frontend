package com.example.readmate_frontend.ui.login

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.readmate_frontend.ui.component.LoginTextField
import com.example.readmate_frontend.ui.component.Logo2
import com.example.readmate_frontend.viewmodel.LoginUiState
import com.example.readmate_frontend.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onHomeClick: () -> Unit = {},
    onSignupClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
){
    val userId       by viewModel.userId.collectAsState()
    val password     by viewModel.password.collectAsState()
    val uiState  by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
            onHomeClick()
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

        LoginTextField("아이디", userId, viewModel::onUserIdChange)
        Spacer(modifier = Modifier.height(16.dp))
        LoginTextField("비밀번호", password, viewModel::onPasswordChange)

        if (uiState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = (uiState as LoginUiState.Error).message,
                color = Color(0xFFE57373),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 36.dp)
            )
        }

        Spacer(modifier = Modifier.height(160.dp))

        Button(
            onClick = { viewModel.login() },
            enabled = uiState !is LoginUiState.Loading,
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
            if (uiState is LoginUiState.Loading) {      // ← 로딩 스피너
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("로그인", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                "아직 계정이 없으신가요?",
                fontSize = 20.sp,
                color = Color(0xFFB79E73),
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { onSignupClick() }
            ) {
                Text(
                    "회원가입",
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
fun LoginPreview(){
    LoginScreen()
}