package com.example.sprint_2_kotlin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sprint_2_kotlin.ui.theme.Sprint2KotlinTheme
import com.example.sprint_2_kotlin.view.AuthScreen
import com.example.sprint_2_kotlin.view.GuideScreen
import com.example.sprint_2_kotlin.view.NewsFeedScreen
import com.example.sprint_2_kotlin.view.NewsItemDetailScreen
import com.example.sprint_2_kotlin.view.ProfileScreen

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Sprint2KotlinTheme {
                val navController = rememberNavController()
                var showBiometricPrompt by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Auth.route,
                        modifier = Modifier.padding(innerPadding),
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400)
                            ) + fadeIn(animationSpec = tween(400))
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400)
                            ) + fadeOut(animationSpec = tween(400))
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400)
                            ) + fadeIn(animationSpec = tween(400))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400)
                            ) + fadeOut(animationSpec = tween(400))
                        }
                    ) {
                        // Auth Screen
                        composable(
                            route = Screen.Auth.route,
                            enterTransition = {
                                fadeIn(animationSpec = tween(600))
                            },
                            exitTransition = {
                                fadeOut(animationSpec = tween(300)) +
                                        scaleOut(targetScale = 0.95f, animationSpec = tween(300))
                            }
                        ) {
                            AuthScreen(
                                onLoginSuccess = {
                                    println("DEBUG MainActivity: onLoginSuccess called, navigating to NewsFeed")
                                    navController.navigate(Screen.NewsFeed.route) {
                                        popUpTo(Screen.Auth.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // News Feed Screen
                        composable(
                            route = Screen.NewsFeed.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                    animationSpec = tween(500)
                                ) + fadeIn(animationSpec = tween(500))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400))
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400))
                            }
                        ) {
                            NewsFeedScreen(
                                onNewsItemClick = { newsItemId ->
                                    navController.navigate(
                                        Screen.NewsItemDetail.createRoute(newsItemId)
                                    )
                                },
                                onNavigateToGuide = {
                                    navController.navigate(Screen.Guide.route)
                                },
                                onNavigateToProfile = {
                                    navController.navigate(Screen.Profile.route)
                                }
                            )
                        }

                        // News Item Detail Screen
                        composable(
                            route = Screen.NewsItemDetail.route,
                            arguments = listOf(
                                navArgument("newsItemId") {
                                    type = NavType.IntType
                                }
                            ),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400)) +
                                        scaleIn(initialScale = 0.9f, animationSpec = tween(400))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400))
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400)) +
                                        scaleOut(targetScale = 0.9f, animationSpec = tween(400))
                            }
                        ) { backStackEntry ->
                            val newsItemId = backStackEntry.arguments?.getInt("newsItemId") ?: 0
                            val userProfileId = backStackEntry.arguments?.getInt(  "userProfileId")?:0

                            NewsItemDetailScreen(
                                newsItemId = newsItemId,
                                userProfileId = userProfileId,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Guide Screen
                        composable(
                            route = Screen.Guide.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400))
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400))
                            }
                        ) {
                            GuideScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Profile Screen
                        composable(
                            route = Screen.Profile.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400))
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400))
                            }
                        ) {
                            ProfileScreen(
                                onLogout = {
                                    navController.navigate(Screen.Auth.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToHome = {
                                    navController.navigate(Screen.NewsFeed.route) {
                                        popUpTo(Screen.NewsFeed.route) { inclusive = true }
                                    }
                                },
                                onNavigateToGuide = {
                                    navController.navigate(Screen.Guide.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        println("DEBUG: showBiometricPrompt() function called")
        val executor = ContextCompat.getMainExecutor(this)

        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
                    or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        println("DEBUG: canAuthenticate = $canAuthenticate")

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            println("DEBUG: Biometric not available, calling onFailure()")
            onFailure()
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock with Fingerprint")
            .setSubtitle("Authenticate to access your News Feed")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    println("DEBUG: onAuthenticationSucceeded called")
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    println("DEBUG: onAuthenticationError - code: $errorCode, message: $errString")
                    onFailure()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    println("DEBUG: onAuthenticationFailed called")
                }
            }
        )

        println("DEBUG: Showing biometric prompt dialog")
        biometricPrompt.authenticate(promptInfo)
    }
}