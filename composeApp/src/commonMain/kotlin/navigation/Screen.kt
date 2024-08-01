package navigation

import models.Cliente

//sealed class Screen(val route: String) {
//    object Login : Screen("login")
//    object Register : Screen("Register")
//    object Account : Screen("Account")
//
//    object AdmProducts : Screen("AdmProducts")
//    object AdmCustomers : Screen("AdmCustomers")
//    object AdmSales : Screen("AdmSales")
//
//    object SellerSales : Screen("SellerSales")
//    object SellerShopping : Screen("SellerShopping")
//    object SellerCustomers : Screen("SellerCustomers")
//    data class SellerCustomer(val cliente: Cliente?) : Screen("SellerCustomer")
//    object SellerReports : Screen("SellerReports")
//}

sealed class Screen {
    object Login : Screen()
    object Account : Screen()
    object AdmCustomers : Screen()
    object AdmProducts : Screen()
    object AdmSales : Screen()
    object SellerCustomers : Screen()
    object SellerReports : Screen()
    object SellerSales : Screen()
    object SellerShopping : Screen()
    data class SellerCustomer(val cliente: Cliente?) : Screen()
}
