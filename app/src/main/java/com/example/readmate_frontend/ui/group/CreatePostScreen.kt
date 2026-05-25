package com.example.readmate_frontend.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmate_frontend.viewmodel.CreatePostState
import com.example.readmate_frontend.viewmodel.PostViewModel

@Composable
fun CreatePostScreen(
    groupId: Int,
    categoryId: Int,
    onAlarmClick: () -> Unit = {},
    onPostSuccess: () -> Unit = {},
    viewModel: PostViewModel = hiltViewModel()
) {
    val currentRound by viewModel.currentRound.collectAsStateWithLifecycle()
    val createState by viewModel.createState.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val isValid = title.isNotBlank() && content.isNotBlank()

    LaunchedEffect(Unit) {
        viewModel.loadCurrentRound(groupId, categoryId)
    }

    LaunchedEffect(createState) {
        if (createState is CreatePostState.Success) {
            viewModel.resetState()
            onPostSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6))
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Logo(onAlarmClick = onAlarmClick)

        Text(
            "소감 작성",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C4A32)
        )

        currentRound?.let {
            Text(
                "${it.roundNumber}라운드 · ${it.startPage}p ~ ${it.endPage}p",
                fontSize = 13.sp,
                color = Color(0xFF9C8E82),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("제목을 입력하세요.", color = Color(0xFFA8926E), fontSize = 15.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFC9A96E),
                unfocusedBorderColor = Color(0xFFF5EFE4),
                focusedContainerColor = Color(0xFFF5EFE4),
                unfocusedContainerColor = Color(0xFFF5EFE4)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = Color(0xFF817052)),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { if (it.length <= 500) content = it },
            placeholder = { Text("독서 소감을 자유롭게 남겨보세요...", color = Color(0xFFA8926E), fontSize = 15.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFC9A96E),
                unfocusedBorderColor = Color(0xFFF5EFE4),
                focusedContainerColor = Color(0xFFF5EFE4),
                unfocusedContainerColor = Color(0xFFF5EFE4)
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 15.sp,
                color = Color(0xFF817052),
                lineHeight = 24.sp
            )
        )

        Text(
            "${content.length} / 500",
            fontSize = 12.sp,
            color = Color(0xFFA8926E),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.createPost(groupId, categoryId, title, content)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isValid) Color(0xFF5C4A32) else Color(0xFFD4C4A8),
                contentColor = Color.White
            ),
            enabled = isValid && createState !is CreatePostState.Loading
        ) {
            if (createState is CreatePostState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("등록", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        if (createState is CreatePostState.Error) {
            Text(
                (createState as CreatePostState.Error).message,
                fontSize = 12.sp,
                color = Color(0xFFD9534F),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}