package views.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import models.ClienteProveedor
import org.jetbrains.compose.resources.painterResource
import sportslinekmp.composeapp.generated.resources.Res
import sportslinekmp.composeapp.generated.resources.person_add
import sportslinekmp.composeapp.generated.resources.person_remove
import viewModels.adm.AdmCustomersViewModel

@Composable
fun AdmCustomersScreen(viewModel: AdmCustomersViewModel = remember { AdmCustomersViewModel() }) {
    val clientesState by viewModel.clientesStateFlow.collectAsState(initial = emptyList())
    var searchText by remember { mutableStateOf("") }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedCliente by remember { mutableStateOf<ClienteProveedor?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                label = { Text("Buscar clientes proveedor...") },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.padding(8.dp))

            Button(
                onClick = {
                    selectedCliente = null
                    showAddEditDialog = true
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.person_add),
                    contentDescription = "Agregar cliente",
                    colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.White),
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(clientesState.filter {
                (it.nombre?.contains(
                    searchText,
                    true
                ) == true) || (it.apellido?.contains(searchText, true) == true)
            }) { cliente ->
                ClienteItem(cliente = cliente, onDelete = {
                    selectedCliente = cliente
                    showDeleteDialog = true
                }, onEdit = {
                    selectedCliente = cliente
                    showAddEditDialog = true
                })
            }
        }

        if (showAddEditDialog) {
            AddEditClienteDialog(
                cliente = selectedCliente,
                onDismiss = { showAddEditDialog = false },
                onConfirm = { cliente ->
                    if (selectedCliente == null) {
                        viewModel.addCliente(cliente)
                    } else {
                        viewModel.updateCliente(cliente)
                    }
                    showAddEditDialog = false
                }
            )
        }

        if (showDeleteDialog && selectedCliente != null) {
            DeleteConfirmationDialog(
                onConfirm = {
                    viewModel.deleteCliente(selectedCliente!!)
                    selectedCliente = null
                    showDeleteDialog = false
                },
                onCancel = {
                    showDeleteDialog = false
                    selectedCliente = null
                }
            )
        }
    }
}

@Composable
private fun ClienteItem(cliente: ClienteProveedor, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = onEdit
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Distribuir espacio
            ) {
                Column {
                    Text(text = "Nombre: ${cliente.nombre} ${cliente.apellido}")
                    Text(text = "Email: ${cliente.correo}")
                }

                Button(
                    onClick = onDelete,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(8.dp, CircleShape),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.person_remove),
                        contentDescription = "Eliminar cliente",
                        colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.White), // Tintado de la imagen a blanco
                        modifier = Modifier.size(25.dp) // Tamaño de la imagen
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditClienteDialog(
    cliente: ClienteProveedor? = null,
    onDismiss: () -> Unit,
    onConfirm: (ClienteProveedor) -> Unit
) {
    var nombre by remember { mutableStateOf(cliente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(cliente?.apellido ?: "") }
    var correo by remember { mutableStateOf(cliente?.correo ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (cliente == null) "Agregar Cliente" else "Editar Cliente") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedCliente = ClienteProveedor(
                    idClienteProveedor = (cliente?.idClienteProveedor ?: 0).toString(),
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo
                )
                onConfirm(updatedCliente)
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
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Confirmar eliminación") },
        text = { Text("¿Estás seguro de que deseas eliminar este cliente?") },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}
