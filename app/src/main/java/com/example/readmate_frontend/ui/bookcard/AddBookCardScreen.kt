package com.example.readmate_frontend.ui.bookcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.readmate_frontend.R
import com.example.readmate_frontend.data.model.response.books.BookSearchItem
import com.example.readmate_frontend.data.model.response.bookcards.BookStatus
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmate_frontend.viewmodel.AddBookCardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun todayFormatted(): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd (EEE)", Locale.ENGLISH)
    return sdf.format(Calendar.getInstance().time)
}

fun calendarFromFormatted(dateStr: String): Calendar {
    return try {
        val sdf = SimpleDateFormat("yyyy.MM.dd (EEE)", Locale.ENGLISH)
        Calendar.getInstance().also { it.time = sdf.parse(dateStr) ?: it.time }
    } catch (e: Exception) { Calendar.getInstance() }
}

fun toApiDate(dateStr: String): String {
    return try {
        val input = SimpleDateFormat("yyyy.MM.dd (EEE)", Locale.ENGLISH)
        val output = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        output.format(input.parse(dateStr) ?: return dateStr)
    } catch (e: Exception) { dateStr }
}

@Composable
fun BookSearchDialog(
    onDismiss: () -> Unit,
    onBookSelected: (BookSearchItem) -> Unit,
    viewModel: AddBookCardViewModel
) {
    val searchState by viewModel.searchUiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = { viewModel.clearSearch(); onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { viewModel.clearSearch(); onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(360.dp)
                    .heightIn(min = 200.dp, max = 580.dp)
                    .clickable { },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F0))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("책 검색", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C4A32))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("제목 또는 저자", fontSize = 13.sp, color = Color(0xFFB0A090)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF5C4A32)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFFCBB38A),
                                unfocusedBorderColor = Color(0xFFE8E0D5),
                                cursorColor = Color(0xFF5C4A32)
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF5C4A32))
                                .clickable { viewModel.searchBooks(query) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = "검색",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        searchState.isLoading -> {
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF817052))
                            }
                        }
                        searchState.error != null -> {
                            Text("오류: ${searchState.error}", color = Color.Red, fontSize = 13.sp)
                        }
                        searchState.results.isEmpty() -> {
                            Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                                Text("검색어를 입력하고 검색해주세요.", color = Color(0xFFB0A090), fontSize = 13.sp)
                            }
                        }
                        else -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(searchState.results) { book ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .clickable { onBookSelected(book); viewModel.clearSearch(); onDismiss() }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (book.coverImageUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = book.coverImageUrl,
                                                contentDescription = book.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.width(40.dp).height(56.dp).clip(RoundedCornerShape(6.dp))
                                            )
                                        } else {
                                            Box(modifier = Modifier.width(40.dp).height(56.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFD4C4A8)))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(book.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C4A32), maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("${book.author} · ${book.publisher}", fontSize = 11.sp, color = Color(0xFFA8926E), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(4.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StarRating(rating: Int, onRatingChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            Text(
                text = if (i <= rating) "★" else "☆",
                fontSize = 20.sp,
                color = Color(0xFFCBB38A),
                modifier = Modifier.clickable { onRatingChange(i) }
            )
        }
    }
}

@Composable
fun AddBookCardScreen(
    modifier: Modifier = Modifier,
    onAlarmClick: () -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: AddBookCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedBook by viewModel.selectedBook.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    var startDate by remember { mutableStateOf(todayFormatted()) }
    var endDate by remember { mutableStateOf(todayFormatted()) }
    var content by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var showStartDialog by remember { mutableStateOf(false) }
    var showEndDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSuccess()
    }

    if (showSearchDialog) {
        BookSearchDialog(
            onDismiss = { showSearchDialog = false },
            onBookSelected = { book -> viewModel.selectBook(book) },
            viewModel = viewModel
        )
    }

    if (showStartDialog) {
        val cal = calendarFromFormatted(startDate)
        android.app.DatePickerDialog(context, { _, y, m, d ->
            val newCal = Calendar.getInstance().also { it.set(y, m, d) }
            startDate = SimpleDateFormat("yyyy.MM.dd (EEE)", Locale.ENGLISH).format(newCal.time)
            if (newCal.after(calendarFromFormatted(endDate))) endDate = startDate
            showStartDialog = false
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    if (showEndDialog) {
        val startCal = calendarFromFormatted(startDate)
        val endCal = calendarFromFormatted(endDate)
        android.app.DatePickerDialog(context, { _, y, m, d ->
            val newCal = Calendar.getInstance().also { it.set(y, m, d) }
            endDate = SimpleDateFormat("yyyy.MM.dd (EEE)", Locale.ENGLISH).format(newCal.time)
            showEndDialog = false
        }, endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH))
            .also { it.datePicker.minDate = startCal.timeInMillis }
            .show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(onAlarmClick = onAlarmClick)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSearchDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0EBE3)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedBook != null && selectedBook!!.coverImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = selectedBook!!.coverImageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = null,
                                tint = Color(0xFFC4B49A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (selectedBook != null) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "『${selectedBook!!.title}』",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5C4A32),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 21.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${selectedBook!!.author} · ${selectedBook!!.publisher}",
                                fontSize = 12.sp,
                                color = Color(0xFF9E8C7A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            StarRating(rating = rating, onRatingChange = { rating = it })
                        }
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("책을 선택해주세요", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E8C7A))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("탭해서 책을 검색하세요", fontSize = 12.sp, color = Color(0xFFB0A090))
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("독서 상태", fontSize = 12.sp, color = Color(0xFFB0A090), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("읽는 중", "완독").forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Color(0xFF5C4A32) else Color(0xFFF0EBE3))
                                    .clickable { selectedTab = index }
                                    .padding(horizontal = 18.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF9C8E82)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF5EFE4))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("독서 기간", fontSize = 12.sp, color = Color(0xFFB0A090), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF5EFE4))
                                .clickable { showStartDialog = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_calendar),
                                contentDescription = null,
                                tint = Color(0xFFB0A090),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(startDate.take(10), fontSize = 12.sp, color = Color(0xFF5C4A32))
                        }

                        Text("—", fontSize = 14.sp, color = Color(0xFFB0A090))

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == 1) Color(0xFFF5EFE4) else Color(0xFFFAF8F5))
                                .clickable(enabled = selectedTab == 1) { showEndDialog = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_calendar),
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color(0xFFB0A090) else Color(0xFFD4C4A8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                if (selectedTab == 1) endDate.take(10) else "미정",
                                fontSize = 12.sp,
                                color = if (selectedTab == 1) Color(0xFF5C4A32) else Color(0xFFB0A090)
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("독서 기록", fontSize = 12.sp, color = Color(0xFFB0A090), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = {
                            Text(
                                "읽은 소감이나 기억하고 싶은 내용을 남겨보세요.",
                                color = Color(0xFFB0A090),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF5C4A32), lineHeight = 22.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5EFE4),
                            unfocusedContainerColor = Color(0xFFF5EFE4),
                            focusedBorderColor = Color(0xFFCBB38A),
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color(0xFF5C4A32)
                        )
                    )
                }
            }

            if (uiState.error != null) {
                Text("오류: ${uiState.error}", color = Color.Red, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    viewModel.addBookCard(
                        review = content,
                        rating = rating,
                        startDate = toApiDate(startDate),
                        endDate = if (selectedTab == 1) toApiDate(endDate) else null,
                        status = if (selectedTab == 0) BookStatus.READING else BookStatus.COMPLETED
                    )
                },
                enabled = !uiState.isLoading && selectedBook != null,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5C4A32),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFEDE5D8),
                    disabledContentColor = Color(0xFFB0A090)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("등록하기", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}