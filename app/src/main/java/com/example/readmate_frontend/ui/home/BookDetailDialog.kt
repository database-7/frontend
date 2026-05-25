package com.example.readmate_frontend.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

data class BookDetail(
    val isbn13: String = "",
    val title: String,
    val author: String = "",
    val pubDate: String = "",
    val coverImageUrl: String = "",
    val summary: String = ""
)

@Composable
fun BookDetailDialog(
    book: BookDetail,
    onDismiss: () -> Unit,
    onBookCardClick: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(336.dp)
                    .wrapContentHeight()
                    .clickable { },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F0))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // 책 표지 + 기본 정보
                    Row(verticalAlignment = Alignment.Top) {
                        if (book.coverImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = book.coverImageUrl,
                                contentDescription = book.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(104.dp)
                                    .height(148.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(104.dp)
                                    .height(148.dp)
                                    .background(Color(0xFFC4B49A), RoundedCornerShape(8.dp))
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "『${book.title}』",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5C4A32)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = book.author,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF817052)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = book.pubDate,
                                fontSize = 12.sp,
                                color = Color(0xFFA8926E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFFE8E0D5))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "줄거리",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF817052)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFF0EBE3), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = book.summary.ifEmpty { "줄거리 정보가 없습니다." },
                            fontSize = 12.sp,
                            color = Color(0xFF9E8C7A),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE8E0D5),
                                contentColor = Color(0xFF817052)
                            )
                        ) {
                            Text("뒤로가기", fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onBookCardClick,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF817052),
                                contentColor = Color(0xFFFAF6F0)
                            )
                        ) {
                            Text("사이트로 이동", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}