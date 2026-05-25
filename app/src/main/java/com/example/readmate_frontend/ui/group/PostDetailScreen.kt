package com.example.readmate_frontend.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readmate_frontend.data.model.response.posts.CommentItem
import com.example.readmate_frontend.data.model.response.posts.PostDetailData
import com.example.readmate_frontend.ui.viewmodel.PostDetailState
import com.example.readmate_frontend.ui.viewmodel.PostDetailViewModel

private val BgColor        = Color(0xFFFFFCF6)
private val Brown          = Color(0xFF5C4A32)
private val BrownLight     = Color(0xFF7A6A58)
private val BrownMuted     = Color(0xFF9C8E82)
private val BrownFaint     = Color(0xFFB0A090)
private val CardBg         = Color(0xFFF5EFE4)
private val TagBg          = Color(0xFFEDE5D8)
private val Accent         = Color(0xFFC9A96E)
private val DividerColor   = Color(0xFFE8DDD0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    groupId: Int,
    postId: Int,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state          by viewModel.state.collectAsStateWithLifecycle()
    val comments       by viewModel.comments.collectAsStateWithLifecycle()
    val commentLoading by viewModel.commentLoading.collectAsStateWithLifecycle()

    var commentInput    by remember { mutableStateOf("") }
    var editingCommentId by remember { mutableStateOf<Int?>(null) }
    var deleteTargetId  by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(groupId, postId) {
        viewModel.loadPostDetail(groupId, postId)
    }

    // 삭제 확인 다이얼로그
    if (deleteTargetId != null) {
        AlertDialog(
            onDismissRequest = { deleteTargetId = null },
            containerColor = BgColor,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("댓글 삭제", fontWeight = FontWeight.Bold, color = Brown, fontSize = 16.sp)
            },
            text = {
                Text(
                    "댓글을 삭제할까요?\n삭제하면 복구할 수 없어요.",
                    color = BrownLight, fontSize = 14.sp, lineHeight = 20.sp
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brown)
                        .clickable {
                            viewModel.deleteComment(deleteTargetId!!)
                            deleteTargetId = null
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
                        .background(TagBg)
                        .clickable { deleteTargetId = null }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("취소", color = BrownMuted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor),
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("←", fontSize = 20.sp, color = Brown)
                    }
                },
                title = {
                    Text("게시물", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Brown)
                }
            )
        },
        bottomBar = {
            CommentInputBar(
                value = commentInput,
                isEditing = editingCommentId != null,
                isLoading = commentLoading,
                onValueChange = { commentInput = it },
                onSend = {
                    val text = commentInput.trim()
                    if (text.isNotEmpty()) {
                        if (editingCommentId != null) {
                            viewModel.updateComment(editingCommentId!!, text)
                            editingCommentId = null
                        } else {
                            viewModel.createComment(text)
                        }
                        commentInput = ""
                    }
                },
                onCancelEdit = {
                    editingCommentId = null
                    commentInput = ""
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is PostDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Accent) }
            }
            is PostDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(s.message, color = BrownMuted, fontSize = 14.sp) }
            }
            is PostDetailState.Success -> {
                PostDetailContent(
                    data = s.data,
                    comments = comments,
                    currentUserName = viewModel.currentUserName,
                    modifier = Modifier.padding(padding),
                    onEditComment = { comment ->
                        editingCommentId = comment.commentId
                        commentInput = comment.content
                    },
                    onDeleteComment = { deleteTargetId = it }
                )
            }
        }
    }
}

@Composable
private fun PostDetailContent(
    data: PostDetailData,
    comments: List<CommentItem>,
    currentUserName: String,
    modifier: Modifier = Modifier,
    onEditComment: (CommentItem) -> Unit,
    onDeleteComment: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 라운드 태그
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(TagBg)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${data.roundNumber}라운드",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrownMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(TagBg)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "pp. ${data.startPage}–${data.endPage}",
                        fontSize = 12.sp,
                        color = BrownMuted
                    )
                }
            }
        }

        // 제목
        item {
            Text(
                text = data.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Brown,
                modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 6.dp)
            )
        }

        // 작성자 & 날짜
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = data.author, fontSize = 13.sp, color = BrownMuted)
                Text(
                    text = data.submittedAt.take(10).replace("-", "."),
                    fontSize = 13.sp,
                    color = BrownFaint
                )
            }
        }

        item {
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                color = DividerColor
            )
        }

        // 본문
        item {
            Text(
                text = data.content,
                fontSize = 15.sp,
                lineHeight = 26.sp,
                color = BrownLight,
                modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp)
            )
        }

        item {
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = DividerColor
            )
        }

        // 댓글 헤더
        item {
            Text(
                text = "댓글 ${comments.size}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Brown,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
            )
        }

        if (comments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("첫 번째 댓글을 남겨보세요", color = BrownFaint, fontSize = 13.sp)
                }
            }
        }

        items(comments, key = { it.commentId }) { comment ->
            CommentCard(
                comment = comment,
                isOwn = comment.author == currentUserName,
                onEdit = { onEditComment(comment) },
                onDelete = { onDeleteComment(comment.commentId) }
            )
        }
    }
}

@Composable
private fun CommentCard(
    comment: CommentItem,
    isOwn: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.author,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Brown
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = comment.createdAt.take(10).replace("-", "."),
                    fontSize = 11.sp,
                    color = BrownFaint
                )
            }
            if (isOwn) {
                Box {
                    TextButton(
                        onClick = { menuExpanded = true },
                        contentPadding = PaddingValues(horizontal = 4.dp),
                    ) {
                        Text("•••", fontSize = 13.sp, color = BrownMuted)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = BgColor
                    ) {
                        DropdownMenuItem(
                            text = { Text("수정", fontSize = 14.sp, color = Brown) },
                            onClick = { menuExpanded = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("삭제", fontSize = 14.sp, color = Color(0xFFD97B6C)) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = comment.content,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = BrownLight
        )

        Spacer(Modifier.height(10.dp))
        Divider(color = DividerColor)
    }
}

@Composable
private fun CommentInputBar(
    value: String,
    isEditing: Boolean,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onCancelEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(BgColor)
    ) {
        if (isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TagBg)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("댓글 수정 중", fontSize = 12.sp, color = BrownMuted)
                TextButton(onClick = onCancelEdit) {
                    Text("취소", fontSize = 12.sp, color = BrownMuted)
                }
            }
        }
        Divider(color = DividerColor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("댓글을 입력하세요", fontSize = 14.sp, color = BrownFaint)
                },
                maxLines = 4,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = DividerColor,
                    cursorColor = Accent,
                    focusedTextColor = Brown,
                    unfocusedTextColor = Brown
                )
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (value.isNotBlank() && !isLoading) Accent else TagBg)
                    .clickable(enabled = value.isNotBlank() && !isLoading) { onSend() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "전송",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (value.isNotBlank()) Color.White else BrownFaint
                    )
                }
            }
        }
    }
}