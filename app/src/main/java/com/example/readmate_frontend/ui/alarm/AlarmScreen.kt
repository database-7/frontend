package com.example.readmate_frontend.ui.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readmate_frontend.R
import com.example.readmate_frontend.data.model.local.LocalNotificationItem
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmate_frontend.viewmodel.AlarmViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6))
    ) {
        Logo()

        // 헤더 + 전체 삭제
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "알림",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4A32)
            )
            if (notifications.isNotEmpty()) {
                Text(
                    "전체 삭제",
                    fontSize = 12.sp,
                    color = Color(0xFF9C8E82),
                    modifier = Modifier.clickable { viewModel.clearAll() }
                )
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "알림이 없습니다",
                    fontSize = 14.sp,
                    color = Color(0xFFB0A090)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    AlarmItem(item = item)
                }
            }
        }
    }
}

@Composable
fun AlarmItem(item: LocalNotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5EFE4))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_smile),
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = Color(0xFFCBB38A)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4A32)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.body,
                fontSize = 11.sp,
                color = Color(0xFF9E8C7A),
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTime(item.receivedAt),
                fontSize = 10.sp,
                color = Color(0xFFB0A090)
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val sdf = SimpleDateFormat("MM.dd HH:mm", Locale.KOREA)
    return sdf.format(Date(millis))
}