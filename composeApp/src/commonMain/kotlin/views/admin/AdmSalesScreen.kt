package views.admin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import models.ClienteProveedor
import models.Producto
import org.jetbrains.compose.resources.painterResource
import sportslinekmp.composeapp.generated.resources.Res
import sportslinekmp.composeapp.generated.resources.add
import sportslinekmp.composeapp.generated.resources.remove
import viewModels.adm.AdmSalesViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdmSalesScreen() {
    val viewModel = remember { AdmSalesViewModel() }
    val productosState by viewModel.productosStateFlow.collectAsState(initial = emptyList())
    val clientesProveedorState by viewModel.clientesProveedorStateFlow.collectAsState(initial = emptyList())
    var searchText by remember { mutableStateOf("") }
    var selectedClienteProovedor by remember { mutableStateOf<ClienteProveedor?>(null) }
    var selectedProductos by remember { mutableStateOf<Map<Producto, Int>>(emptyMap()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar productos...") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(200.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(productosState.filter {
                it?.idProducto?.contains(
                    searchText,
                    true
                ) == true
            }) { producto ->
                if (producto != null) {
                    ProductoItem(producto = producto, onUpdate = { cantidad ->
                        val newSelectedProductos = selectedProductos.toMutableMap()
                        newSelectedProductos[producto] = cantidad
                        selectedProductos = newSelectedProductos
                    })
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Seleccionar Cliente proveedor:", modifier = Modifier.padding(end = 8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    TextField(
                        value = selectedClienteProovedor?.let { "${it.nombre} ${it.apellido}" }
                            ?: "",
                        onValueChange = {},
                        label = { Text("Cliente proveedor") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        clientesProveedorState.forEach { cliente ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedClienteProovedor = cliente
                                    isDropdownExpanded = false
                                },
                                text = {
                                    if (cliente != null) {
                                        Text("${cliente.nombre} ${cliente.apellido}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                runBlocking {
                    viewModel.completeSale(selectedClienteProovedor, selectedProductos)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Completar Venta")
        }
    }
}

@Composable
private fun ProductoItem(producto: Producto, onUpdate: (Int) -> Unit) {
    var cantidad by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(250.dp) // Set fixed height for consistency
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(), // Ensure the content fills the Card
            verticalArrangement = Arrangement.SpaceBetween // Space content evenly
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = producto.idProducto ?: "Producto",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₡${producto.precioVenta}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (cantidad > 0) cantidad--; onUpdate(cantidad) }) {
                    Icon(
                        painter = painterResource(Res.drawable.remove),
                        contentDescription = "Disminuir cantidad"
                    )
                }
                Text(
                    text = cantidad.toString(),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                IconButton(onClick = { cantidad++; onUpdate(cantidad) }) {
                    Icon(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = "Aumentar cantidad"
                    )
                }
            }
        }
    }
}