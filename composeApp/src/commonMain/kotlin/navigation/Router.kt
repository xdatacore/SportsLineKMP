package navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import utils.XPrintln

object Router {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen

    val screenStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        XPrintln.log("Router.navigateTo($screen)")
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
        XPrintln.log("Router.screenStack(${screenStack})")
    }

    fun navigateBack() {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
        }
    }

    fun reset() {
        screenStack.clear()
        _currentScreen.value = Screen.Login
        XPrintln.log("Router.reset()")
    }
}
