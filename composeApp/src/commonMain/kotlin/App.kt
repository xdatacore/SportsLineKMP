import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import modules.initKoin
import navigation.Router
import navigation.Screen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import utils.XPrintln
import viewModels.LoginViewModel
import views.LoginScreen

private var isKoinInitialized = false

fun initializeKoin() {
    if (!isKoinInitialized) {
        initKoin()
        isKoinInitialized = true
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    initializeKoin()

    val currentScreen by Router.currentScreen.collectAsState()
    val loginViewModel = remember { LoginViewModel() }
    val isAuthenticated by loginViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(isAuthenticated) {
        XPrintln.log("Is authenticate: ${isAuthenticated}")
        if (isAuthenticated) {
            Router.navigateTo(Screen.Account)
        } else {
            Router.navigateTo(Screen.Login)
        }
    }

    MaterialTheme {
        if (currentScreen is Screen.Login) {
            LoginScreen(loginViewModel)
        } else {
            MainScreen()
        }
    }
}