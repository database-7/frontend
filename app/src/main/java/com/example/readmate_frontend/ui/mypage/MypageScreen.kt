package com.example.readmate_frontend.ui.mypage

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmate_frontend.ui.component.MyButton
import com.example.readmate_frontend.viewmodel.ProfileUiState
import com.example.readmate_frontend.viewmodel.ProfileViewModel

@Composable
fun MypageScreen(
    modifier: Modifier = Modifier,
    onAlarmClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onFAQClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNicknameDialog by remember { mutableStateOf(false) }

    val currentName = if (uiState is ProfileUiState.Success) {
        (uiState as ProfileUiState.Success).data.userName
    } else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Logo(onAlarmClick = onAlarmClick)

        Card(
            modifier = Modifier
                .height(120.dp)
                .width(360.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showNicknameDialog = true }
                ),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EFE4))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    when (val state = uiState) {
                        is ProfileUiState.Loading -> {
                            CircularProgressIndicator(
                                color = Color(0xFFC9A96E),
                                modifier = Modifier.size(40.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        is ProfileUiState.Success -> {
                            val userName = state.data.userName
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(Color(0xFFC4B49A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.first().toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF817052)
                                )
                            }
                            Spacer(modifier = Modifier.width(24.dp))
                            Column {
                                Text(
                                    if (userName.isNotEmpty()) userName else "사용자",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${if (userName.isNotEmpty()) userName else "사용자"} 님, 반갑습니다!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFA8926E)
                                )
                            }
                        }
                        is ProfileUiState.Error -> {
                            Text(state.message, fontSize = 12.sp, color = Color(0xFFE57373))
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFE8E0D5))
            Spacer(modifier = Modifier.height(20.dp))
        }

        MyButton(text = "계정관리", onClick = onAccountClick)
        Spacer(modifier = Modifier.height(8.dp))
        MyButton(text = "FAQ", onClick = onFAQClick)
        Spacer(modifier = Modifier.height(8.dp))
        MyButton(text = "로그아웃", onClick = onLogoutClick)

        if (showNicknameDialog) {
            NicknameChangeDialog(
                currentName = currentName,
                onDismiss = { showNicknameDialog = false },
                onConfirm = { newName ->
                    showNicknameDialog = false
                    viewModel.changeNickname(newName)
                }
            )
        }
    }
}


@Composable
fun NicknameChangeDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFCF6)),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "닉네임 변경",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = {
                            Text(
                                "새 닉네임 입력",
                                color = Color(0xFFB0A090),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5EFE6),
                            focusedContainerColor = Color(0xFFF5EFE6),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = Color(0xFF5C4A32)
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (newName.isNotBlank()) Color(0xFF5C4A32)
                                else Color(0xFFD4C4A8)
                            )
                            .clickable(enabled = newName.isNotBlank()) {
                                onConfirm(newName)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "변경",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MypagePreview() {
    MypageScreen()
}