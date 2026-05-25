package com.example.readmate_frontend.ui.bookcard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.readmate_frontend.R
import com.example.readmate_frontend.data.model.request.UpdateBookcardRequest
import com.example.readmate_frontend.data.model.response.bookcards.BookStatus
import com.example.readmate_frontend.data.model.response.bookcards.BookcardListResponse
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmatefrontend.viewmodel.BookcardListViewModel
import kotlin.math.roundToInt

data class FilterTab(val label: String, val status: BookStatus?)

val filterTabs = listOf(
    FilterTab("ALL", null),
    FilterTab("READING", BookStatus.READING),
    FilterTab("COMPLETED", BookStatus.COMPLETED)
)

@Composable
fun BookCardListScreen(
    modifier: Modifier = Modifier,
    onAlarmClick: () -> Unit = {},
    onCardDetailClick: (Int) -> Unit = {},
    onAddBookCardClick: () -> Unit = {},
    viewModel: BookcardListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(filterTabs[0]) }

    LaunchedEffect(Unit) {
        viewModel.loadCards()
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
                painter = painterResource(R.drawable.ic_card),
                contentDescription = "카드",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { uiState.cards.firstOrNull()?.let { onCardDetailClick(it.id) } },
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

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 21.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterTabs) { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Color(0xFF5C4A32) else Color(0xFFF0EBE3)
                        )
                        .clickable {
                            selectedTab = tab
                            viewModel.loadCards(tab.status)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color(0xFFFFFCF6) else Color(0xFF9C8E82)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                    Text(
                        "아직 생성된 독서 카드가 없습니다.",
                        color = Color(0xFF9C8E82),
                        fontSize = 14.sp
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 21.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.cards) { card ->
                        SwipeableBookCardItem(
                            card = card,
                            onClick = { onCardDetailClick(card.id) },
                            onDelete = { viewModel.deleteCard(card.id) },
                            onEdit = { request -> viewModel.updateCard(card.id, request) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun SwipeableBookCardItem(
    card: BookcardListResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (UpdateBookcardRequest) -> Unit
) {
    val density = LocalDensity.current
    val actionWidthPx = with(density) { 160.dp.toPx() }

    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        label = "swipe"
    )

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        offsetX = if (offsetX < -actionWidthPx / 2) -actionWidthPx else 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-actionWidthPx, 0f)
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(IntrinsicSize.Max)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(
                        Color(0xFF5C7A5C),
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
                    .clickable {
                        offsetX = 0f
                        showEditDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("수정", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(
                        Color(0xFFD9534F),
                        RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                    )
                    .clickable {
                        offsetX = 0f
                        showDeleteDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("삭제", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Box(modifier = Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }) {
            BookCardListItem(
                card = card,
                onClick = {
                    if (offsetX != 0f) offsetX = 0f else onClick()
                }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("삭제 확인", fontWeight = FontWeight.Bold) },
            text = { Text("『${card.title}』 카드를 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) { Text("삭제", color = Color(0xFFD9534F), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            }
        )
    }

    if (showEditDialog) {
        EditBookCardDialog(
            card = card,
            onDismiss = { showEditDialog = false },
            onConfirm = { request ->
                showEditDialog = false
                onEdit(request)
            }
        )
    }
}

@Composable
fun EditBookCardDialog(
    card: BookcardListResponse,
    onDismiss: () -> Unit,
    onConfirm: (UpdateBookcardRequest) -> Unit
) {
    var review by remember { mutableStateOf(card.review ?: "") }
    var rating by remember { mutableIntStateOf(card.rating ?: 0) }
    var startDate by remember { mutableStateOf(card.startDate ?: "") }
    var endDate by remember { mutableStateOf(card.endDate ?: "") }
    var status by remember { mutableStateOf(card.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "카드 수정",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4A32)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // 상태
                Text("상태", fontSize = 13.sp, color = Color(0xFF9C8E82))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        BookStatus.READING to "읽는 중",
                        BookStatus.COMPLETED to "완독"
                    ).forEach { (s, label) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (status == s) Color(0xFF5C4A32) else Color(0xFFF0EBE3)
                                )
                                .clickable { status = s }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                label,
                                fontSize = 13.sp,
                                color = if (status == s) Color.White else Color(0xFF9C8E82)
                            )
                        }
                    }
                }

                Text("별점", fontSize = 13.sp, color = Color(0xFF9C8E82))
                Row {
                    for (i in 1..5) {
                        Text(
                            text = if (i <= rating) "★" else "☆",
                            fontSize = 24.sp,
                            color = Color(0xFFCBB38A),
                            modifier = Modifier.clickable { rating = i }
                        )
                    }
                }

                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("시작일 (yyyy-MM-dd)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("종료일 (yyyy-MM-dd)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text("독서 기록") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    UpdateBookcardRequest(
                        review = review.ifEmpty { null },
                        rating = if (rating > 0) rating else null,
                        startDate = startDate.ifEmpty { null },
                        endDate = endDate.ifEmpty { null },
                        status = status
                    )
                )
            }) {
                Text("저장", color = Color(0xFF5C4A32), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Composable
fun BookCardListItem(
    card: BookcardListResponse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (card.coverImageUrl.isNotEmpty()) {
            AsyncImage(
                model = card.coverImageUrl,
                contentDescription = card.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(56.dp)
                    .height(76.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(76.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFD4C4A8))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "『${card.title}』",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4A32),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${card.author} / ${card.publisher}",
                fontSize = 13.sp,
                color = Color(0xFF9E8C7A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            StatusBadge(card.status)
        }
    }
}

@Composable
fun StatusBadge(status: BookStatus) {
    val (label, bg, fg) = when (status) {
        BookStatus.READING -> Triple("READING", Color(0xFFEAF3DE), Color(0xFF3B6D11))
        BookStatus.COMPLETED -> Triple("COMPLETED", Color(0xFFF0EBE3), Color(0xFF817052))
        else -> Triple(status.name, Color(0xFFF0EBE3), Color(0xFF817052))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}