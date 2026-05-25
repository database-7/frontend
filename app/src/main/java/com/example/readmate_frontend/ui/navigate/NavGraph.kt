package com.example.readmate_frontend.ui.navigate

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.readmate_frontend.data.local.TokenStore
import com.example.readmate_frontend.ui.alarm.AlarmScreen
import com.example.readmate_frontend.ui.bookcard.BookCardListScreen
import com.example.readmate_frontend.ui.bookcard.AddBookCardScreen
import com.example.readmate_frontend.ui.bookcard.BookCardDetailScreen
import com.example.readmate_frontend.ui.group.CreatePostScreen
import com.example.readmate_frontend.ui.group.GroupHomeScreen
import com.example.readmate_frontend.ui.group.GroupScreen
import com.example.readmate_frontend.ui.home.HomeScreen
import com.example.readmate_frontend.ui.splash.SplashScreen
import com.example.readmate_frontend.ui.login.LoginScreen
import com.example.readmate_frontend.ui.mypage.AccountScreen
import com.example.readmate_frontend.ui.mypage.EditPasswordScreen
import com.example.readmate_frontend.ui.mypage.MypageScreen
import com.example.readmate_frontend.ui.register.SignupScreen
import com.example.readmate_frontend.ui.screen.PostDetailScreen
import com.example.readmate_frontend.ui.tabbar.BottomTabBar

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onLoginClick = { navController.navigate("login") },
                onSignupClick = { navController.navigate("regirster") }
            )
        }
        composable("login") {
            LoginScreen(
                onHomeClick = { navController.navigate("home") },
                onSignupClick = { navController.navigate("regirster") },
                )
        }
        composable("regirster") {
            SignupScreen(
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("home") {
            Scaffold(
                bottomBar = { BottomTabBar(navController) }
            ) { padding ->
                HomeScreen(
                    modifier = Modifier.padding(padding),
                    onAlarmClick = { navController.navigate("alarm") }
                )
            }
        }

        composable("group") {
            Scaffold(
                bottomBar = { BottomTabBar(navController) }
            ) { padding ->
                GroupScreen(
                    modifier = Modifier.padding(padding),
                    onAlarmClick = { navController.navigate("alarm") },
                    onGroupClick = { groupId -> navController.navigate("grouphome/$groupId") }
                )
            }
        }

        composable(
            route = "grouphome/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: 0
            Scaffold(
                bottomBar = { BottomTabBar(navController) }
            ) { padding ->
                GroupHomeScreen(
                    groupId = groupId,
                    modifier = Modifier.padding(padding),
                    onAlarmClick = { navController.navigate("alarm") },
                    onCreatePostClick = { gId, categoryId ->
                        navController.navigate("createpost/$gId/$categoryId")
                    },
                    onPostClick = { gId, postId ->
                        navController.navigate("postdetail/$gId/$postId")
                    }
                )
            }
        }

        composable(
            route = "createpost/{groupId}/{categoryId}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType },
                navArgument("categoryId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: 0
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            CreatePostScreen(
                groupId = groupId,
                categoryId = categoryId,
                onAlarmClick = { navController.navigate("alarm") },
                onPostSuccess = { navController.popBackStack() }
            )
        }

        composable("postdetail/{groupId}/{postId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull() ?: return@composable
            val postId  = backStackEntry.arguments?.getString("postId")?.toIntOrNull()  ?: return@composable
            PostDetailScreen(
                groupId = groupId,
                postId  = postId,
                onBack  = { navController.popBackStack() }
            )
        }

        composable("bookcardlist") {
            Scaffold(
                bottomBar = { BottomTabBar(navController) }
            ) { padding ->
                BookCardListScreen(
                    modifier = Modifier.padding(padding),
                    onAlarmClick = { navController.navigate("alarm") },
                    onCardDetailClick = { cardId ->
                        navController.navigate("bookcarddetail/$cardId")
                    },
                    onAddBookCardClick = { navController.navigate("addbookcard") }
                )
            }
        }

        composable(
            route = "bookcarddetail/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId") ?: 0

            Scaffold(
                bottomBar = { BottomTabBar(navController) }
            ) { padding ->
                BookCardDetailScreen(
                    cardId = cardId,
                    modifier = Modifier.padding(padding),
                    onAlarmClick = { navController.navigate("alarm") },
                    onCardListClick = { navController.navigate("bookcardlist") },
                    onAddBookCardClick = { navController.navigate("addbookcard") }
                )
            }
        }

        composable("addbookcard") {
            Scaffold(
                bottomBar = { BottomTabBar(navController) }
            ) { padding ->
                AddBookCardScreen(
                    modifier = Modifier.padding(padding),
                    onAlarmClick = { navController.navigate("alarm") },
                    onSuccess = { navController.popBackStack() }
                )
            }
        }

        composable("mypage") {
            Scaffold(
                bottomBar = { BottomTabBar(navController) }
            ) { padding ->
                MypageScreen(
                    modifier = Modifier.padding(padding),
                    onAlarmClick = { navController.navigate("alarm") },
                    onAccountClick = { navController.navigate("account") },
                    onFAQClick = { navController.navigate("FAQ") },
                    onLogoutClick = {
                        TokenStore.token = ""
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("editpassword") {
            EditPasswordScreen(
                onAlarmClick = { navController.navigate("alarm") },
                onSuccess = { navController.popBackStack() }
                )
        }

        composable("account"){
            AccountScreen(
                onAlarmClick = { navController.navigate("alarm") },
                onEditPasswoedClick = { navController.navigate("editpassword") },
                onDeregisterSuccess = {
                    navController.navigate("splash") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("FAQ"){

        }

        composable("alarm") {
            AlarmScreen( onBackClick = { navController.popBackStack() } )
        }
    }
}