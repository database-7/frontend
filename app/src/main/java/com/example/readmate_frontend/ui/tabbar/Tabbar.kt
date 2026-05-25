package com.example.readmate_frontend.ui.tabbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.readmate_frontend.R

data class TabItem(
    val route: String,
    val icon: Int
)

val tabs = listOf(
    TabItem("home", R.drawable.ic_home),
    TabItem("group", R.drawable.ic_group),
    TabItem("bookcardlist", R.drawable.ic_bookcard),
    TabItem("mypage", R.drawable.ic_mypage),
)

val bookCardRoutes = setOf("bookcarddetail/{cardId}ㅍ", "bookcardlist", "addbookcard")
val groupRoutes = setOf("group","grouphome/{groupId}")

@Composable
fun BottomTabBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFFEFE7D7)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(52.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = currentRoute == tab.route ||
                        (tab.route == "bookcardlist" && currentRoute in bookCardRoutes) ||
                        (tab.route == "group" && currentRoute in groupRoutes)

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = tab.icon),
                        contentDescription = tab.route,
                        modifier = Modifier.fillMaxSize(),
                        tint = if (isSelected) Color(0xFF817052) else Color(0xFFDCC8A1)
                    )
                }
            }
        }
    }
}