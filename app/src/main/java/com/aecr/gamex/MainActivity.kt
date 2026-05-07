package com.aecr.gamex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.aecr.gamex.data.MainViewModel
import com.aecr.gamex.ui.components.BottomSection
import com.aecr.gamex.ui.components.ExpiredDialog
import com.aecr.gamex.ui.components.WelcomeDialog
import com.aecr.gamex.ui.components.isAppExpired
import com.aecr.gamex.ui.screens.BootAnimationScreen
import com.aecr.gamex.ui.screens.DashboardScreen
import com.aecr.gamex.ui.screens.MyGamesScreen
import com.aecr.gamex.ui.screens.SensitivityScreen
import com.aecr.gamex.ui.theme.AECRGameXTheme
import com.aecr.gamex.ui.theme.GameXColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AECRGameXTheme {
                GameXApp()
            }
        }
    }
}

@Composable
fun GameXApp() {
    val viewModel: MainViewModel = viewModel()
    val navController: NavHostController = rememberNavController()
    val bootTarget by viewModel.bootTarget.collectAsState()

    val expired     = remember { isAppExpired() }
    var showWelcome by remember { mutableStateOf(true) }

    // Màn hình khởi chạy game (boot animation overlay)
    if (bootTarget != null) {
        BootAnimationScreen(
            game = bootTarget!!,
            onAnimationComplete = {
                viewModel.launchGame(bootTarget!!.packageName)
                viewModel.clearBootTarget()
            }
        )
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: String = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = GameXColors.BgPrimary,
        bottomBar = {
            BottomSection(
                currentRoute = currentRoute,
                onNavigate   = { route: String ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController      = navController,
            startDestination   = "dashboard",
            modifier           = Modifier.padding(innerPadding),
            enterTransition    = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 12 } },
            exitTransition     = { fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { -it / 12 } },
            popEnterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { -it / 12 } },
            popExitTransition  = { fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { it / 12 } }
        ) {
            composable("dashboard")   { DashboardScreen(viewModel) }
            composable("sensitivity") { SensitivityScreen(viewModel) }
            composable("my_games")    { MyGamesScreen(viewModel) }
        }
    }

    // Popup: kiểm tra hết hạn trước, rồi mới chào mừng
    when {
        expired     -> ExpiredDialog()
        showWelcome -> WelcomeDialog(onDismiss = { showWelcome = false })
    }
}
