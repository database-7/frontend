package com.example.readmate_frontend.ui.bookcard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.readmate_frontend.R
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmate_frontend.viewmodel.BookCardViewModel

data class BookCardData(
    val title: String,
    val author: String,
    val publisher: String,
    val year: String,
    val rating: Int,
    val isCompleted: Boolean,
    val startDate: String,
    val endDate: String,
    val days: String,
    val noteDate: String,
    val note: String,
    val coverImageUrl: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookCardDetailScreen(
    cardId: Int,
    modifier: Modifier = Modifier,
    onAlarmClick: () -> Unit = {},
    onCardListClick: () -> Unit = {},
    onAddBookCardClick: () -> Unit = {},
    initialPage: Int = 0,
    viewModel: BookCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(cardId) {
        viewModel.loadAllCards(cardId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Logo(onAlarmClick = onAlarmClick)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 21.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_list),
                contentDescription = "목록",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCardListClick() },
                tint = Color(0xFFCBB38A)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "독서카드 생성",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onAddBookCardClick() },
                tint = Color(0xFFCBB38A)
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF817052))
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("오류: ${uiState.error}", color = Color.Red)
                }
            }
            uiState.cards.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("독서 카드가 없습니다.", color = Color(0xFF9C8E82))
                }
            }
            else -> {
                val pagerState = rememberPagerState(
                    initialPage = uiState.initialPage,
                    pageCount = { uiState.cards.size }
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val card = uiState.cards[page]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BookInfoCard(card)
                        ReadingStatusCard(card)
                        if (card.note.isNotEmpty()) NoteCard(card)
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "${pagerState.currentPage + 1}/${uiState.cards.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBB38A),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StarRatingReadOnly(rating: Int) {
    Row {
        for (i in 1..5) {
            Text(
                text = if (i <= rating) "★" else "☆",
                fontSize = 24.sp,
                color = Color(0xFFCBB38A)
            )
        }
    }
}

@Composable
fun BookInfoCard(card: BookCardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (card.coverImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = card.coverImageUrl,
                    contentDescription = card.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(72.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFD4C4A8))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = card.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${card.author} / ${card.publisher} / ${card.year}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9E8C7A)
                )
                Spacer(modifier = Modifier.height(8.dp))
                StarRatingReadOnly(card.rating)
            }
        }
    }
}

@Composable
fun ReadingStatusCard(card: BookCardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row {
                StatusTab(text = "완독", isSelected = card.isCompleted)
                Spacer(modifier = Modifier.width(8.dp))
                StatusTab(text = "읽는 중", isSelected = !card.isCompleted)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = card.startDate, fontSize = 14.sp, color = Color(0xFF6B5E54))
                Text(text = "  —  ", fontSize = 14.sp, color = Color(0xFFC9B8A8))
                Text(text = card.endDate, fontSize = 14.sp, color = Color(0xFF6B5E54))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = card.days, fontSize = 12.sp, color = Color(0xFFC9B8A8))
            }
        }
    }
}

@Composable
fun StatusTab(text: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF3A3028) else Color.Transparent)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else Color(0xFFC9B8A8),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFFFFFCF6) else Color(0xFF9C8E82)
        )
    }
}

@Composable
fun NoteCard(card: BookCardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "독서 기록",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B5E54)
                )
                Text(text = card.noteDate, fontSize = 12.sp, color = Color(0xFFC9B8A8))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = card.note,
                fontSize = 14.sp,
                color = Color(0xFF3A3028),
                lineHeight = 24.sp
            )
        }
    }
}