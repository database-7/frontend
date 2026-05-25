package com.example.readmate_frontend.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.readmate_frontend.data.model.response.home.ArticleApiItem
import com.example.readmate_frontend.data.model.response.home.BookApiItem
import com.example.readmate_frontend.ui.component.Logo


@Composable
fun SectionHeader(title: String, icon: ImageVector, iconTint: Color, iconBg: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C4A32))
    }
}

@Composable
fun RankBadge(rank: Int) {
    val (bg, fg) = when (rank) {
        1 -> Color(0xFFE8C97A) to Color(0xFF6B4D00)
        2 -> Color(0xFFD4D4D4) to Color(0xFF3A3A3A)
        3 -> Color(0xFFD4A07A) to Color(0xFF5C2D00)
        else -> Color(0xFFEDE5D8) to Color(0xFF817052)
    }
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text("$rank", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

@Composable
fun BookCard(index: Int, item: BookApiItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFAF6F0))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RankBadge(rank = index + 1)
        Spacer(modifier = Modifier.width(10.dp))

        if (item.coverImageUrl.isNotEmpty()) {
            AsyncImage(
                model = item.coverImageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(36.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFD4C4A8))
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4A32),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                item.author,
                fontSize = 11.sp,
                color = Color(0xFFA8926E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Composable
fun BookBox(items: List<BookApiItem>, onItemClick: (BookDetail) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            BookCard(index = index, item = item) {
                onItemClick(
                    BookDetail(
                        isbn13 = item.isbn13,
                        title = item.title,
                        author = item.author,
                        pubDate = item.pubDate,
                        coverImageUrl = item.coverImageUrl,
                        summary = ""
                    )
                )
            }
        }
    }
}

fun parsePubDate(pubDate: String): String {
    return try {
        val parts = pubDate.split(" ")
        "${parts[3]}.${
            when (parts[2]) {
                "Jan" -> "01"; "Feb" -> "02"; "Mar" -> "03"; "Apr" -> "04"
                "May" -> "05"; "Jun" -> "06"; "Jul" -> "07"; "Aug" -> "08"
                "Sep" -> "09"; "Oct" -> "10"; "Nov" -> "11"; "Dec" -> "12"
                else -> "00"
            }
        }.${parts[1]}"
    } catch (e: Exception) { "" }
}

@Composable
fun ArticleCard(index: Int, item: ArticleApiItem) {
    val context = LocalContext.current
    val cleanTitle = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    val date = parsePubDate(item.pubDate)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFAF6F0))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                context.startActivity(intent)
            }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF0EBE3))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("추천", fontSize = 10.sp, color = Color(0xFF817052), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(date, fontSize = 10.sp, color = Color(0xFFC4B49A))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            cleanTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C4A32),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun ArticleBox(items: List<ArticleApiItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            ArticleCard(index = index, item = item)
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAlarmClick: () -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    var selectedBook by remember { mutableStateOf<BookDetail?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Logo(onAlarmClick = onAlarmClick)

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
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text("안녕하세요", fontSize = 14.sp, color = Color(0xFFA8926E))
                    Text(
                        "${uiState.userName}님, 오늘도 좋은 하루 되세요 :)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF817052)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    Text(
                        "🏆 월간 베스트",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF817052)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BookBox(items = uiState.bestseller, onItemClick = { selectedBook = it })
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    color = Color(0xFFE8E0D5)
                )


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "🆕 신간 베스트",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF817052)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BookBox(items = uiState.newBooks, onItemClick = { selectedBook = it })
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    color = Color(0xFFE8E0D5)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "📰 추천 기사",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF817052))
                }
                Spacer(modifier = Modifier.height(8.dp))
                ArticleBox(items = uiState.articles)
            }
        }

        Spacer(modifier = Modifier.height(160.dp))
    }

    selectedBook?.let { book ->
        BookDetailDialog(
            book = book,
            onDismiss = { selectedBook = null }
        )
    }
}