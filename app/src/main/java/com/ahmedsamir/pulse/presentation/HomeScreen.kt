package com.ahmedsamir.pulse.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ahmedsamir.pulse.feature.notifications.presentation.NotificationsViewModel
import com.ahmedsamir.pulse.navigation.BottomNavItem
import com.ahmedsamir.pulse.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(rootNavController: NavController) {

    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val notificationsUiState by notificationsViewModel.uiState.collectAsState()
    val unreadCount = notificationsUiState.unreadCount

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            if (item is BottomNavItem.Notifications && unreadCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ) {
                                            Text(
                                                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                                color = Color.White
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onBackground,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == BottomNavItem.Home.route) {
                FloatingActionButton(
                    onClick = { rootNavController.navigate(Screen.CreatePost.route) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post",
                        tint = Color.White
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                com.ahmedsamir.pulse.feature.feed.presentation.FeedScreen(
                    onPostClick = { postId ->
                        rootNavController.navigate(Screen.PostDetail.createRoute(postId))
                    },
                    onProfileClick = { userId ->
                        rootNavController.navigate(Screen.Profile.createRoute(userId))
                    },
                    onCommentClick = { postId ->
                        rootNavController.navigate(Screen.Comments.createRoute(postId))
                    }
                )
            }

            composable(BottomNavItem.Search.route) {
                com.ahmedsamir.pulse.feature.search.presentation.SearchScreen(
                    onProfileClick = { userId ->
                        rootNavController.navigate(Screen.Profile.createRoute(userId))
                    }
                )
            }

            composable(BottomNavItem.Notifications.route) {
                com.ahmedsamir.pulse.feature.notifications.presentation.NotificationsScreen(
                    onProfileClick = { userId ->
                        rootNavController.navigate(Screen.Profile.createRoute(userId))
                    },
                    onPostClick = { postId ->
                        rootNavController.navigate(Screen.PostDetail.createRoute(postId))
                    },
                    viewModel = notificationsViewModel
                )
            }

            composable(BottomNavItem.Profile.route) {
                val currentUserId =
                    com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                com.ahmedsamir.pulse.feature.profile.presentation.ProfileScreen(
                    userId = currentUserId,
                    onNavigateBack = {},
                    onEditProfile = { rootNavController.navigate(Screen.EditProfile.route) },
                    onLogout = {
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}