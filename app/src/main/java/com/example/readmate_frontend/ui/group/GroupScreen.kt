package com.example.readmate_frontend.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readmate_frontend.R
import com.example.readmate_frontend.data.model.response.groups.GroupItem
import com.example.readmate_frontend.data.model.response.groups.MemberRole
import com.example.readmate_frontend.ui.component.Logo
import com.example.readmate_frontend.viewmodel.GroupViewModel

@Composable
fun GroupScreen(
    modifier: Modifier = Modifier,
    onAlarmClick: () -> Unit = {},
    onGroupClick: (Int) -> Unit = {},
    viewModel: GroupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var groupToEdit by remember { mutableStateOf<GroupItem?>(null) }
    var groupToDelete by remember { mutableStateOf<GroupItem?>(null) }
    var groupToLeave by remember { mutableStateOf<GroupItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Logo(onAlarmClick = onAlarmClick)

        Spacer(modifier = Modifier.height(8.dp))

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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(horizontal = 21.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.groups) { group ->
                        val isOwner = uiState.userRoles[group.groupId] == MemberRole.OWNER
                        GroupCard(
                            group = group,
                            inviteCode = uiState.inviteCodes[group.groupId] ?: "-",
                            isOwner = isOwner,
                            onGroupClick = { onGroupClick(group.groupId) },
                            onEdit = { groupToEdit = group },
                            onDelete = { groupToDelete = group },
                            onLeave = { groupToLeave = group }
                        )
                    }
                    item {
                        AddGroupCard(onClick = { showCreateDialog = true })
                    }
                    item {
                        JoinGroupCard(onClick = { showJoinDialog = true })
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                showCreateDialog = false
                viewModel.createGroup(name)
            }
        )
    }

    if (showJoinDialog) {
        JoinGroupDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = { inviteCode ->
                showJoinDialog = false
                viewModel.joinGroup(inviteCode)
            }
        )
    }

    groupToEdit?.let { group ->
        EditGroupDialog(
            currentName = group.name,
            onDismiss = { groupToEdit = null },
            onEdit = { newName ->
                groupToEdit = null
                viewModel.updateGroup(group.groupId, newName)
            }
        )
    }

    groupToDelete?.let { group ->
        ConfirmDialog(
            title = "그룹 삭제",
            message = "\"${group.name}\" 그룹을 삭제하시겠어요?\n삭제 후 복구가 불가능합니다.",
            confirmText = "삭제",
            confirmColor = Color(0xFFD9534F),
            onDismiss = { groupToDelete = null },
            onConfirm = {
                groupToDelete = null
                viewModel.deleteGroup(group.groupId)
            }
        )
    }

    groupToLeave?.let { group ->
        ConfirmDialog(
            title = "그룹 떠나기",
            message = "\"${group.name}\" 그룹에서 나가시겠어요?",
            confirmText = "떠나기",
            confirmColor = Color(0xFFD9534F),
            onDismiss = { groupToLeave = null },
            onConfirm = {
                groupToLeave = null
                viewModel.leaveGroup(group.groupId)
            }
        )
    }
}

@Composable
fun GroupCard(
    group: GroupItem,
    inviteCode: String,
    isOwner: Boolean,
    onGroupClick: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onLeave: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .clickable { onGroupClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EFE6)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBB38A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = group.name.firstOrNull()?.toString() ?: "G",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column {
                    Text(
                        text = group.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C4A32),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "초대코드: $inviteCode",
                        fontSize = 11.sp,
                        color = Color(0xFF9C8E82),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_loadmore),
                        contentDescription = "더보기",
                        tint = Color(0xFF9C8E82),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFFFFFCF6))
                ) {
                    if (isOwner) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "수정하기",
                                    fontSize = 14.sp,
                                    color = Color(0xFF5C4A32)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "삭제하기",
                                    fontSize = 14.sp,
                                    color = Color(0xFFD9534F)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "떠나기",
                                    fontSize = 14.sp,
                                    color = Color(0xFFD9534F)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onLeave()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddGroupCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EBE3)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "+",
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFFCBB38A)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "그룹 만들기",
                fontSize = 13.sp,
                color = Color(0xFF9C8E82)
            )
        }
    }
}

@Composable
fun JoinGroupCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE8E0)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "→",
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFFCBB38A)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "그룹 참여하기",
                fontSize = 13.sp,
                color = Color(0xFF9C8E82)
            )
        }
    }
}

@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFCF6)),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "그룹 만들기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        placeholder = {
                            Text("그룹 이름 입력", color = Color(0xFFB0A090), fontSize = 14.sp)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5EFE6),
                            focusedContainerColor = Color(0xFFF5EFE6),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF5C4A32))
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (groupName.isNotBlank()) Color(0xFF5C4A32) else Color(0xFFD4C4A8))
                            .clickable(enabled = groupName.isNotBlank()) { onCreate(groupName) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("생성", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }

                Text(
                    text = if (groupName.isEmpty()) "그룹 이름을 입력하고 만들어주세요."
                    else "$groupName 그룹을 생성합니다.",
                    fontSize = 12.sp,
                    color = Color(0xFFB0A090),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EditGroupDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit
) {
    var groupName by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFCF6)),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "그룹 수정하기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        placeholder = {
                            Text("그룹 이름 입력", color = Color(0xFFB0A090), fontSize = 14.sp)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5EFE6),
                            focusedContainerColor = Color(0xFFF5EFE6),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF5C4A32))
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (groupName.isNotBlank()) Color(0xFF5C4A32) else Color(0xFFD4C4A8))
                            .clickable(enabled = groupName.isNotBlank()) { onEdit(groupName) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("수정", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun JoinGroupDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFCF6)),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "그룹 참여하기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        value = inviteCode,
                        onValueChange = { inviteCode = it },
                        placeholder = {
                            Text("초대 코드 입력", color = Color(0xFFB0A090), fontSize = 14.sp)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5EFE6),
                            focusedContainerColor = Color(0xFFF5EFE6),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF5C4A32))
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (inviteCode.isNotBlank()) Color(0xFF5C4A32) else Color(0xFFD4C4A8))
                            .clickable(enabled = inviteCode.isNotBlank()) { onJoin(inviteCode) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("참여", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }

                Text(
                    text = if (inviteCode.isEmpty()) "초대 코드를 입력하고 참여해주세요."
                    else "\"$inviteCode\" 코드로 참여합니다.",
                    fontSize = 12.sp,
                    color = Color(0xFFB0A090),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color = Color(0xFF5C4A32),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFCF6)),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4A32)
                )
                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color(0xFF9C8E82),
                    lineHeight = 20.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 취소 버튼
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0EBE3))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("취소", fontSize = 14.sp, color = Color(0xFF9C8E82), fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(confirmColor)
                            .clickable { onConfirm() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(confirmText, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}