package com.example.readmate_frontend.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readmate_frontend.ui.component.Logo2


@Composable
fun SplashScreen(
    onLoginClick: () -> Unit = {},
    onSignupClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(212.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp)
        ) {
            Logo2(48.sp,18.sp)
        }

        Spacer(modifier = Modifier.height(360.dp))

        Button(
            onClick = { onLoginClick() },
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
            Text(
                "로그인 하러 가기",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                "아직 계정이 없으신가요?",
                fontSize = 18.sp,
                color = Color(0xFFB79E73),
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { onSignupClick() }
            ) {
                Text(
                    "회원가입",
                    fontSize = 18.sp,
                    color = Color(0xFF817052),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashPreview(){
    SplashScreen()
}