import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import navigation.Router
import navigation.Screen
import utils.SecurityStorage
import viewModels.AccountViewModel
import viewModels.adm.AdmCustomersViewModel
import viewModels.adm.AdmProductsViewModel
import viewModels.seller.SellerReportsViewModel
import viewModels.seller.SellerSalesViewModel
import viewModels.seller.SellerShoppingViewModel
import views.AccountScreen
import views.admin.AdmCustomersScreen
import views.admin.AdmProductsScreen
import views.admin.AdmSalesScreen
import views.seller.SellerCustomerScreen
import views.seller.SellerCustomersScreen
import views.seller.SellerReportsScreen
import views.seller.SellerSalesScreen
import views.seller.SellerShoppingScreen

//@Composable
//fun MainScreen() {
//    val currentScreen by Router.currentScreen.collectAsState()
//    var userProfileId = 0;
//
//    if (SecurityStorage.getUserProfileId() != null) {
//        userProfileId = SecurityStorage.getUserProfileId()!!;
//    }
//
//    XPrintln.log("currentScreen: $currentScreen")
//    XPrintln.log("userProfileId: $userProfileId")
//
//    Column(modifier = Modifier.fillMaxSize()) {
//
//        TabBar(userProfileId = userProfileId, onTabSelected = { screen ->
//            Router.navigateTo(screen)
//        })
//
//        when (val screen = currentScreen) {
//            is Screen.Account -> AccountScreen(AccountViewModel())
//
//            is Screen.AdmCustomers -> AdmCustomersScreen(AdmCustomersViewModel())
//            is Screen.AdmProducts -> AdmProductsScreen(AdmProductsViewModel())
//            is Screen.AdmSales -> AdmSalesScreen(AdmSalesViewModel())
//
//            is Screen.SellerCustomers -> SellerCustomersScreen()
//            is Screen.SellerCustomer -> SellerCustomerScreen(screen.cliente){
//                Router.navigateBack()
//            }
//            is Screen.SellerReports -> SellerReportsScreen(SellerReportsViewModel())
//            is Screen.SellerSales -> SellerSalesScreen(SellerSalesViewModel())
//            is Screen.SellerShopping -> SellerShoppingScreen(SellerShoppingViewModel())
//
//            else -> Text("Unknown screen")
//        }
//    }
//}

@Composable
fun MainScreen() {
    val currentScreen by Router.currentScreen.collectAsState()
    var userProfileId = 0

    if (SecurityStorage.getUserProfileId() != null) {
        userProfileId = SecurityStorage.getUserProfileId()!!
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabBar(userProfileId = userProfileId, onTabSelected = { screen ->
            Router.navigateTo(screen)
        })

        when (val screen = currentScreen) {
            is Screen.Account -> AccountScreen(AccountViewModel())

            is Screen.AdmCustomers -> AdmCustomersScreen(AdmCustomersViewModel())
            is Screen.AdmProducts -> AdmProductsScreen(AdmProductsViewModel())
            is Screen.AdmSales -> AdmSalesScreen()

            is Screen.SellerCustomers -> SellerCustomersScreen()
            is Screen.SellerCustomer -> SellerCustomerScreen(screen.cliente) {
                Router.navigateBack()
            }

            is Screen.SellerReports -> SellerReportsScreen(SellerReportsViewModel())
            is Screen.SellerSales -> SellerSalesScreen(SellerSalesViewModel())
            is Screen.SellerShopping -> SellerShoppingScreen(SellerShoppingViewModel())

            else -> Text("Unknown screen")
        }
    }
}

@Composable
fun TabBar(userProfileId: Int, onTabSelected: (Screen) -> Unit) {
    val commonTabs = listOf("Cuenta" to Screen.Account)
    val specificTabs = when (userProfileId) {
        1 -> listOf(
            "Productos" to Screen.AdmProducts,
            "Clientes" to Screen.AdmCustomers,
            "Ventas" to Screen.AdmSales
        )

        2 -> listOf(
            "Ventas" to Screen.SellerSales,
            "Compras" to Screen.SellerShopping,
            "Clientes" to Screen.SellerCustomers,
            "Reportes" to Screen.SellerReports
        )

        else -> emptyList()
    }

    val allTabs = specificTabs + commonTabs
    val currentScreen by Router.currentScreen.collectAsState()
    var selectedIndex = allTabs.indexOfFirst { it.second == currentScreen }

    if (selectedIndex == -1 && Router.screenStack.isNotEmpty()) {
        val previousScreen = Router.screenStack.last()
        selectedIndex = allTabs.indexOfFirst { it.second == previousScreen }
    }

    TabRow(selectedTabIndex = if (selectedIndex >= 0) selectedIndex else 0) {
        allTabs.forEachIndexed { index, (title, screen) ->
            Tab(
                selected = selectedIndex == index,
                onClick = {
                    onTabSelected(screen)
                },
                text = { Text(title) }
            )
        }
    }
}