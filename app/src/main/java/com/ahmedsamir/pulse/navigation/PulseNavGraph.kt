package com.ahmedsamir.pulse.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ahmedsamir.pulse.feature.auth.presentation.AuthViewModel
import com.ahmedsamir.pulse.feature.auth.presentation.LoginScreen
import com.ahmedsamir.pulse.feature.auth.presentation.RegisterScreen
import com.ahmedsamir.pulse.feature.auth.presentation.SplashScreen
import com.ahmedsamir.pulse.presentation.HomeScreen
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun PulseNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isLoggedIn = authViewModel.isLoggedIn,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(rootNavController = navController)
        }

        composable(Screen.CreatePost.route) {
            com.ahmedsamir.pulse.feature.post.presentation.CreatePostScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("postId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            com.ahmedsamir.pulse.feature.feed.presentation.PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() },
                onProfileClick = { userId ->
                    navController.navigate(Screen.Profile.createRoute(userId))
                },
                onCommentClick = { id ->
                    navController.navigate(Screen.Comments.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.Comments.route,
            arguments = listOf(
                androidx.navigation.navArgument("postId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            com.ahmedsamir.pulse.feature.comments.presentation.CommentsScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() },
                onProfileClick = { userId ->
                    navController.navigate(Screen.Profile.createRoute(userId))
                }
            )
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                androidx.navigation.navArgument("userId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            com.ahmedsamir.pulse.feature.profile.presentation.ProfileScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun PlaceholderFullScreen(title: String) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = androidx.compose.ui.Alignment.Center,
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        androidx.compose.material3.Text(
            text = "$title — Coming Soon",
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        )
    }
}
