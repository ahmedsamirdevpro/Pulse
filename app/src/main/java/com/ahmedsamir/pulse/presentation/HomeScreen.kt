package com.ahmedsamir.pulse.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ahmedsamir.pulse.core.ui.theme.PulseBorder
import com.ahmedsamir.pulse.navigation.BottomNavItem
import com.ahmedsamir.pulse.navigation.Screen
import com.ahmedsamir.pulse.feature.feed.presentation.FeedScreen

@Composable
fun HomeScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = androidx.compose.ui.unit.Dp.Unspecified
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true

                    NavigationBarItem(
                        selected = isSelected,
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
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon
                                else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { rootNavController.navigate(Screen.CreatePost.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                FeedScreen(
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

            composable(Screen.Search.route) {
                com.ahmedsamir.pulse.feature.search.presentation.SearchScreen(
                    onProfileClick = { userId ->
                        rootNavController.navigate(Screen.Profile.createRoute(userId))
                    }
                )
            }

            composable(Screen.Notifications.route) {
                com.ahmedsamir.pulse.feature.notifications.presentation.NotificationsScreen(
                    onProfileClick = { userId ->
                        rootNavController.navigate(Screen.Profile.createRoute(userId))
                    },
                    onPostClick = { postId ->
                        rootNavController.navigate(Screen.PostDetail.createRoute(postId))
                    }
                )
            }

            composable(Screen.Profile.route) {
                val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
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

@Composable
private fun PlaceholderScreen(title: String) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = androidx.compose.ui.Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "$title — Coming Soon",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}