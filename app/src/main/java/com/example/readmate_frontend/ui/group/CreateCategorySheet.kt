package com.example.readmate_frontend.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.readmate_frontend.data.model.request.CreateCategoryRequest
import com.example.readmate_frontend.data.model.response.books.BookSearchItem
import com.example.readmate_frontend.data.model.response.groups.GroupMemberItem
import com.example.readmate_frontend.viewmodel.GroupHomeViewModel

@Composable
fun CreateCategorySheet(
    onDismiss: () -> Unit,
    onCreate: (CreateCategoryRequest) -> Unit,
    viewModel: GroupHomeViewModel = hiltViewModel()
) {
    var step by remember { mutableStateOf(1) }
    val bookSearch by viewModel.bookSearch.collectAsStateWithLifecycle()
    val membersState by viewModel.membersState.collectAsStateWithLifecycle()
    var memberOrder by remember { mutableStateOf<List<GroupMemberItem>>(emptyList()) }
    var year by remember { mutableIntStateOf(2026) }
    var month by remember { mutableIntStateOf(1) }
    var day by remember { mutableIntStateOf(1) }
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(0) }
    var hoursPerRound by remember { mutableIntStateOf(24) }
    var useStartTime by remember { mutableStateOf(false) }
    var totalPageCountText by remember { mutableStateOf("") }
    var pagesPerRoundText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.resetBookSearch()
        viewModel.loadGroupMembers()
    }

    LaunchedEffect(membersState.members) {
        if (memberOrder.isEmpty() && membersState.members.isNotEmpty()) {
            memberOrder = membersState.members
        }
    }

    Dialog(onDismissRequest = {
        viewModel.resetBookSearch()
        onDismiss()
    }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFCF6)),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .padding(horizontal = 4.dp)
        ) {
            when (step) {
                1 -> StepBookSearch(
                    query = bookSearch.query,
                    isSearching = bookSearch.isSearching,
                    results = bookSearch.results,
                    selectedBook = bookSearch.selectedBook,
                    onSearch = { viewModel.searchBooks(it) },
                    onSelect = { viewModel.selectBook(it) },
                    onNext = { step = 2 }
                )
                2 -> StepMemberOrder(
                    members = memberOrder,
                    onReorder = { memberOrder = it },
                    onNext = { step = 3 }
                )
                3 -> StepReadingTime(
                    year = year, onYearChange = { year = it },
                    month = month, onMonthChange = { month = it },
                    day = day, onDayChange = { day = it },
                    hour = hour, onHourChange = { hour = it },
                    minute = minute, onMinuteChange = { minute = it },
                    hoursPerRound = hoursPerRound, onHoursChange = { hoursPerRound = it },
                    useStartTime = useStartTime, onUseStartTimeChange = { useStartTime = it },
                    totalPageCountText = totalPageCountText, onTotalPageCountChange = { totalPageCountText = it },
                    pagesPerRoundText = pagesPerRoundText, onPagesPerRoundChange = { pagesPerRoundText = it },
                    onConfirm = {
                        val iso = if (useStartTime)
                            "%04d-%02d-%02dT%02d:%02d:00.000Z".format(year, month, day, hour, minute)
                        else null
                        onCreate(
                            CreateCategoryRequest(
                                bookId = bookSearch.selectedBookId!!,
                                readingMode = "EXCHANGE",
                                pagesPerRound = pagesPerRoundText.toIntOrNull() ?: 0,
                                daysPerRound = maxOf(1, hoursPerRound / 24),
                                startAt = iso,
                                memberOrder = memberOrder.map { it.userNum },
                                totalPageCount = totalPageCountText.toIntOrNull() ?: 0
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun StepBookSearch(
    query: String,
    isSearching: Boolean,
    results: List<BookSearchItem>,
    selectedBook: BookSearchItem?,
    onSearch: (String) -> Unit,
    onSelect: (BookSearchItem) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("책 검색", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C4A32))
        Spacer(Modifier.height(16.dp))
        TextField(
            value = query,
            onValueChange = onSearch,
            placeholder = { Text("책 제목 검색", color = Color(0xFFB0A090), fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5EFE6),
                focusedContainerColor = Color(0xFFF5EFE6),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF5C4A32)),
            trailingIcon = {
                if (isSearching) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFFC9A96E), strokeWidth = 2.dp)
            }
        )
        Spacer(Modifier.height(10.dp))
        if (selectedBook != null) SelectedBookRow(selectedBook)
        if (results.isNotEmpty() && selectedBook == null) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFF5EFE6))
            ) {
                items(results.take(5)) { book ->
                    BookResultRow(book = book, onClick = { onSelect(book) })
                    if (book != results.take(5).last()) {
                        HorizontalDivider(color = Color(0xFFE8DDD0), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 14.dp))
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (selectedBook != null) Color(0xFF5C4A32) else Color(0xFFD4C4A8))
                .clickable(enabled = selectedBook != null) { onNext() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("다음", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun SelectedBookRow(book: BookSearchItem) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFF0E9DE)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(book.coverImageUrl).crossfade(true).build(),
            contentDescription = book.title,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text("『${book.title}』", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF5C4A32), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${book.author} / ${book.publisher} / ${book.pubDate.take(4)}", fontSize = 11.sp, color = Color(0xFF9C8E82))
        }
        Text("✓", fontSize = 16.sp, color = Color(0xFFC9A96E), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BookResultRow(book: BookSearchItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(book.coverImageUrl).crossfade(true).build(),
            contentDescription = book.title,
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(book.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF5C4A32), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${book.author} / ${book.publisher}", fontSize = 11.sp, color = Color(0xFF9C8E82))
        }
    }
}

@Composable
private fun StepMemberOrder(
    members: List<GroupMemberItem>,
    onReorder: (List<GroupMemberItem>) -> Unit,
    onNext: () -> Unit
) {
    val itemHeightDp = 52.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeightDp.toPx() }
    var draggingUserNum by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("독서 순서", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C4A32))
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFF5EFE6))) {
                members.forEachIndexed { index, member ->
                    val isDragging = draggingUserNum == member.userNum
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeightDp)
                            .background(if (isDragging) Color(0xFFEDE5D8) else Color.Transparent)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}.", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9C8E82), modifier = Modifier.width(24.dp))
                        Text(member.userName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF5C4A32), modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .pointerInput(member.userNum) {
                                    detectDragGestures(
                                        onDragStart = { draggingUserNum = member.userNum; dragOffsetY = 0f },
                                        onDragEnd = { draggingUserNum = null; dragOffsetY = 0f },
                                        onDragCancel = { draggingUserNum = null; dragOffsetY = 0f },
                                        onDrag = { _, dragAmount ->
                                            dragOffsetY += dragAmount.y
                                            val currentIdx = members.indexOfFirst { it.userNum == draggingUserNum }
                                            if (currentIdx == -1) return@detectDragGestures
                                            val steps = (dragOffsetY / itemHeightPx).toInt()
                                            if (steps != 0) {
                                                val targetIdx = (currentIdx + steps).coerceIn(0, members.lastIndex)
                                                if (targetIdx != currentIdx) {
                                                    val newList = members.toMutableList()
                                                    val item = newList.removeAt(currentIdx)
                                                    newList.add(targetIdx, item)
                                                    onReorder(newList)
                                                    dragOffsetY -= steps * itemHeightPx
                                                }
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("≡", fontSize = 20.sp, color = Color(0xFFB0A090), fontWeight = FontWeight.Bold)
                        }
                    }
                    if (index < members.size - 1) {
                        HorizontalDivider(color = Color(0xFFE8DDD0), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF5C4A32)).clickable { onNext() }.padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("다음", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun StepReadingTime(
    year: Int, onYearChange: (Int) -> Unit,
    month: Int, onMonthChange: (Int) -> Unit,
    day: Int, onDayChange: (Int) -> Unit,
    hour: Int, onHourChange: (Int) -> Unit,
    minute: Int, onMinuteChange: (Int) -> Unit,
    hoursPerRound: Int, onHoursChange: (Int) -> Unit,
    useStartTime: Boolean, onUseStartTimeChange: (Boolean) -> Unit,
    totalPageCountText: String, onTotalPageCountChange: (String) -> Unit,
    pagesPerRoundText: String, onPagesPerRoundChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("독서 시간", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C4A32))
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5EFE6))
                    .clickable { onUseStartTimeChange(!useStartTime) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("시간제한 지정", fontSize = 14.sp, color = Color(0xFF5C4A32), fontWeight = FontWeight.Medium)
                Switch(
                    checked = useStartTime,
                    onCheckedChange = { onUseStartTimeChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFC9A96E),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD4C4A8)
                    )
                )
            }

            if (useStartTime) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5EFE6))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        NumberPicker(displayValue = year.toString(), label = "년", onIncrement = { onYearChange(year + 1) }, onDecrement = { onYearChange(year - 1) })
                        NumberPicker(displayValue = month.toString().padStart(2, '0'), label = "월", onIncrement = { onMonthChange(if (month == 12) 1 else month + 1) }, onDecrement = { onMonthChange(if (month == 1) 12 else month - 1) })
                        NumberPicker(displayValue = day.toString().padStart(2, '0'), label = "일", onIncrement = { onDayChange(if (day == 31) 1 else day + 1) }, onDecrement = { onDayChange(if (day == 1) 31 else day - 1) })
                        NumberPicker(displayValue = hour.toString().padStart(2, '0'), label = "시", onIncrement = { onHourChange((hour + 1) % 24) }, onDecrement = { onHourChange(if (hour == 0) 23 else hour - 1) })
                        NumberPicker(displayValue = minute.toString().padStart(2, '0'), label = "분", onIncrement = { onMinuteChange((minute + 5) % 60) }, onDecrement = { onMinuteChange(if (minute == 0) 55 else minute - 5) })
                    }
                    Text("부터", fontSize = 12.sp, color = Color(0xFF9C8E82), fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5EFE6))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NumberPicker(
                        displayValue = hoursPerRound.toString(),
                        label = "H",
                        onIncrement = { onHoursChange(hoursPerRound + 24) },
                        onDecrement = { onHoursChange(maxOf(24, hoursPerRound - 24)) }
                    )
                    Text("마다", fontSize = 12.sp, color = Color(0xFF9C8E82), fontWeight = FontWeight.Medium)
                }
            }

            TextField(
                value = pagesPerRoundText,
                onValueChange = { if (it.all(Char::isDigit)) onPagesPerRoundChange(it) },
                placeholder = { Text("라운드 당 페이지 수", color = Color(0xFFB0A090), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5EFE6),
                    focusedContainerColor = Color(0xFFF5EFE6),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF5C4A32))
            )

            TextField(
                value = totalPageCountText,
                onValueChange = { if (it.all(Char::isDigit)) onTotalPageCountChange(it) },
                placeholder = { Text("총 페이지 수", color = Color(0xFFB0A090), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5EFE6),
                    focusedContainerColor = Color(0xFFF5EFE6),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF5C4A32))
            )

            Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF5C4A32))
                .clickable { onConfirm() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("생성하기", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun NumberPicker(
    displayValue: String,
    label: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier.size(22.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFEDE5D8)).clickable { onIncrement() },
            contentAlignment = Alignment.Center
        ) {
            Text("▲", fontSize = 8.sp, color = Color(0xFF9C8E82))
        }
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(displayValue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF5C4A32), textAlign = TextAlign.Center)
            Text(label, fontSize = 10.sp, color = Color(0xFF9C8E82))
        }
        Box(
            modifier = Modifier.size(22.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFEDE5D8)).clickable { onDecrement() },
            contentAlignment = Alignment.Center
        ) {
            Text("▼", fontSize = 8.sp, color = Color(0xFF9C8E82))
        }
    }
}