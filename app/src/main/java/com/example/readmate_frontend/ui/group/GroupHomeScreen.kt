package com.example.readmate_frontend.ui.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readmate_frontend.R
import com.example.readmate_frontend.data.model.response.rounds.MyStatusData
import com.example.readmate_frontend.data.model.response.rounds.PostItem
import com.example.readmate_frontend.ui.component.Logo3
import com.example.readmate_frontend.viewmodel.GroupHomeViewModel

@Composable
fun GroupHomeScreen(
    groupId: Int,
    modifier: Modifier = Modifier,
    onAlarmClick: () -> Unit = {},
    onPostClick: (groupId: Int, postId: Int) -> Unit = { _, _ -> },
    onCreatePostClick: (Int, Int) -> Unit = { _, _ -> },
    viewModel: GroupHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val postsState by viewModel.postsState.collectAsStateWithLifecycle()
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var showCreateCategorySheet by remember { mutableStateOf(false) }
    var deleteCategoryTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }
    val isOwner by viewModel.isOwner.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotMyTurnDialog by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.loadGroupHome(groupId)
    }

    LaunchedEffect(uiState.groupHome) {
        val categories = uiState.groupHome?.categories ?: return@LaunchedEffect
        val first = categories.firstOrNull() ?: return@LaunchedEffect
        if (selectedCategoryId == null || categories.none { it.categoryId == selectedCategoryId }) {
            selectedCategoryId = first.categoryId
            viewModel.loadPosts(first.categoryId)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                selectedCategoryId?.let { viewModel.loadPosts(it) }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    deleteCategoryTarget?.let { (categoryId, categoryName) ->
        AlertDialog(
            onDismissRequest = { deleteCategoryTarget = null },
            containerColor = Color(0xFFFFFCF6),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "카테고리 삭제",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32),
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "『$categoryName』 카테고리를 삭제할까요?\n삭제하면 복구할 수 없어요.",
                    color = Color(0xFF7A6A58),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF5C4A32))
                        .clickable {
                            viewModel.deleteCategory(categoryId)
                            deleteCategoryTarget = null
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("삭제", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEDE5D8))
                        .clickable { deleteCategoryTarget = null }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("취소", color = Color(0xFF9C8E82), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    if (showNotMyTurnDialog) {
        AlertDialog(
            onDismissRequest = { showNotMyTurnDialog = false },
            containerColor = Color(0xFFFFFCF6),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "글 작성 불가",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32),
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "현재 사용자님의 턴이 아닙니다.\n순서가 되면 작성할 수 있어요.",
                    color = Color(0xFF7A6A58),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF5C4A32))
                        .clickable { showNotMyTurnDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("확인", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Logo3(onAlarmClick = onAlarmClick)
            Spacer(modifier = Modifier.height(20.dp))

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFC9A96E))
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "불러오기 실패: ${uiState.error}",
                            color = Color(0xFF9C8E82),
                            fontSize = 14.sp
                        )
                    }
                }

                uiState.groupHome != null -> {
                    val group = uiState.groupHome!!

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 21.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = group.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5C4A32)
                        )
                        Text(
                            text = "멤버 ${group.memberCount}명",
                            fontSize = 12.sp,
                            color = Color(0xFF9C8E82)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 21.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        group.categories.forEach { category ->
                            CategoryTab(
                                label = category.name,
                                isSelected = selectedCategoryId == category.categoryId,
                                onClick = {
                                    selectedCategoryId = category.categoryId
                                    viewModel.loadPosts(category.categoryId)
                                },
                                onLongClick = {
                                    if (isOwner) {
                                        deleteCategoryTarget = Pair(category.categoryId, category.name)
                                    }
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFEDE5D8))
                                .clickable { showCreateCategorySheet = true }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                "+ 카테고리",
                                fontSize = 12.sp,
                                color = Color(0xFF9C8E82),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    val selectedCategory = group.categories.find { it.categoryId == selectedCategoryId }

                    when {
                        group.categories.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "안녕하세요! 👋",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5C4A32)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "카테고리를 생성해주세요.",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9C8E82)
                                    )
                                    Text(
                                        "위의 '+ 카테고리' 버튼을 눌러 시작해보세요!",
                                        fontSize = 13.sp,
                                        color = Color(0xFFB0A090)
                                    )
                                }
                            }
                        }

                        postsState.isLoading -> {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFC9A96E))
                            }
                        }

                        postsState.roundsWithPosts.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        "게시물을 작성해주세요!",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF9C8E82)
                                    )
                                    Text(
                                        "첫 번째 글을 남겨보세요.",
                                        fontSize = 13.sp,
                                        color = Color(0xFFB0A090)
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 21.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                postsState.roundsWithPosts.forEach { roundWithPosts ->
                                    item {
                                        RoundHeader(
                                            roundNumber = roundWithPosts.round.roundNumber,
                                            startPage = roundWithPosts.round.startPage,
                                            endPage = roundWithPosts.round.endPage,
                                            isOngoing = roundWithPosts.round.status == "ONGOING",
                                            myStatus = if (roundWithPosts.round.status == "ONGOING") postsState.myStatus else null,
                                            currentTurnUserName = if (roundWithPosts.round.status == "ONGOING") postsState.currentTurnUserName else null,
                                            onPressure = if (roundWithPosts.round.status == "ONGOING") {
                                                postsState.currentTurnUserId?.let { userId ->
                                                    { viewModel.sendPressure(userId) }
                                                }
                                            } else null
                                        )
                                    }

                                    if (roundWithPosts.posts.isEmpty() && roundWithPosts.round.status == "ONGOING") {
                                        item { HiddenPostCard() }
                                    } else {
                                        items(roundWithPosts.posts) { post ->
                                            val bookInfo = buildString {
                                                val title = selectedCategory?.bookTitle?.takeIf { it.isNotBlank() }
                                                val author = selectedCategory?.bookAuthor?.takeIf { it.isNotBlank() }
                                                if (title != null) append(title)
                                                if (title != null && author != null) append(" / ")
                                                if (author != null) append(author)
                                            }
                                            PostCard(
                                                post = post,
                                                bookInfo = bookInfo,
                                                onClick = { onPostClick(groupId, post.postId) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.groupHome != null && selectedCategoryId != null) {
            Image(
                painter = painterResource(id = R.drawable.ic_plus2),
                contentDescription = "글 작성",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 32.dp)
                    .size(52.dp)
                    .clickable {
                        val myStatus = postsState.myStatus
                        when {
                            myStatus?.myTurn == true -> onCreatePostClick(groupId, selectedCategoryId!!)
                            else -> showNotMyTurnDialog = true
                        }
                    }
            )
        }
    }

    if (showCreateCategorySheet) {
        CreateCategorySheet(
            onDismiss = { showCreateCategorySheet = false },
            onCreate = { request ->
                viewModel.createCategory(request)
                showCreateCategorySheet = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (isSelected) Color(0xFFFFFCF6) else Color(0xFF9C8E82),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFFC9A96E) else Color(0xFFEDE5D8))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}

@Composable
fun PostCard(
    post: PostItem,
    bookInfo: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EFE4)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.userName,
                    fontSize = 12.sp,
                    color = Color(0xFFC9A96E),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = post.submittedAt.take(10).replace("-", "."),
                    fontSize = 11.sp,
                    color = Color(0xFFB0A090)
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = post.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4A32),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = post.content,
                fontSize = 13.sp,
                color = Color(0xFF7A6A58),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 19.sp
            )

            if (bookInfo.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = bookInfo,
                    fontSize = 11.sp,
                    color = Color(0xFF9C8E82),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HiddenPostCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EFE4)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "🔒  소감문을 작성해야 볼 수 있습니다",
                fontSize = 13.sp,
                color = Color(0xFFB0A090),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RoundHeader(
    roundNumber: Int,
    startPage: Int,
    endPage: Int,
    isOngoing: Boolean,
    myStatus: MyStatusData? = null,
    currentTurnUserName: String? = null,
    onPressure: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "round $roundNumber  ·  ${startPage}p ~ ${endPage}p",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5C4A32)
            )
            if (isOngoing) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFC9A96E))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("진행 중", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }

        if (isOngoing) {
            when {
                myStatus?.myTurn == true -> {
                    Text(
                        "현재 내 턴입니다 ✍️",
                        fontSize = 12.sp,
                        color = Color(0xFFC9A96E),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                currentTurnUserName != null -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "현재 ${currentTurnUserName}님 턴입니다",
                            fontSize = 12.sp,
                            color = Color(0xFF9C8E82)
                        )
                        if (onPressure != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEDE5D8))
                                    .clickable { onPressure() }
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "📣 독촉",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9C8E82),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}