package views.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import models.Producto
import org.jetbrains.compose.resources.painterResource
import sportslinekmp.composeapp.generated.resources.Res
import sportslinekmp.composeapp.generated.resources.add
import sportslinekmp.composeapp.generated.resources.delete_forever
import sportslinekmp.composeapp.generated.resources.edit
import viewModels.adm.AdmProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmProductsScreen(viewModel: AdmProductsViewModel) {
    val productosState by viewModel.productosStateFlow.collectAsState(initial = emptyList())
    var searchText by remember { mutableStateOf("") }
    var selectedProductos by remember { mutableStateOf<Map<Producto, Int>>(emptyMap()) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEditProductDialog by remember { mutableStateOf<Producto?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar productos...") },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.padding(8.dp))

            Button(
                onClick = { showAddProductDialog = true },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.add),
                    contentDescription = "Agregar cliente",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(200.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(productosState.filter {
                it?.idProducto?.contains(searchText, true) == true
            }) { producto ->
                if (producto != null) {
                    ProductoItem(
                        producto = producto,
                        onUpdate = { cantidad ->
                            val newSelectedProductos = selectedProductos.toMutableMap()
                            newSelectedProductos[producto] = cantidad
                            selectedProductos = newSelectedProductos
                        },
                        onEdit = { showEditProductDialog = it },
                        onDelete = { viewModel.deleteProducto(it) }
                    )
                }
            }
        }
    }

    if (showAddProductDialog) {
        ProductDialog(
            onDismiss = { showAddProductDialog = false },
            onConfirm = { producto ->
                viewModel.addProducto(producto)
            }
        )
    }

    showEditProductDialog?.let { producto ->
        ProductDialog(
            producto = producto,
            onDismiss = { showEditProductDialog = null },
            onConfirm = { updatedProducto ->
                viewModel.updateProducto(updatedProducto)
            }
        )
    }
}

@Composable
fun ProductDialog(
    producto: Producto? = null,
    onDismiss: () -> Unit,
    onConfirm: (Producto) -> Unit
) {
    var idProducto by remember { mutableStateOf(producto?.idProducto ?: "") }
    var precioVenta by remember { mutableStateOf(producto?.precioVenta?.toString() ?: "") }
    var inventario by remember { mutableStateOf(producto?.inventario?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (producto == null) "Agregar Producto" else "Editar Producto") },
        text = {
            Column {
                OutlinedTextField(
                    value = idProducto,
                    onValueChange = { idProducto = it },
                    label = { Text("Producto ID") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = precioVenta,
                    onValueChange = { precioVenta = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = inventario,
                    onValueChange = { inventario = it },
                    label = { Text("Inventario") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedProducto = Producto(
                    idProducto = idProducto,
                    precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                    inventario = inventario.toIntOrNull() ?: 0
                )
                onConfirm(updatedProducto)
                onDismiss()
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ProductoItem(
    producto: Producto,
    onUpdate: (Int) -> Unit,
    onEdit: (Producto) -> Unit,
    onDelete: (Producto) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = producto.idProducto ?: "Producto",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₡${(producto.precioVenta)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onDelete(producto) }) {
                    Icon(
                        painter = painterResource(Res.drawable.delete_forever),
                        contentDescription = "Eliminar producto"
                    )
                }
                Text(
                    text = " ",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                IconButton(onClick = { onEdit(producto) }) {
                    Icon(
                        painter = painterResource(Res.drawable.edit),
                        contentDescription = "Editar producto"
                    )
                }
            }
        }
    }
}
