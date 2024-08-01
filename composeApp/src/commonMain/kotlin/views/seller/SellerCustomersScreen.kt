package views.seller

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import models.Cliente
import org.jetbrains.compose.resources.painterResource
import sportslinekmp.composeapp.generated.resources.Res
import sportslinekmp.composeapp.generated.resources.person_add
import sportslinekmp.composeapp.generated.resources.person_remove
import viewModels.seller.SellerCustomersViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SellerCustomersScreen() {
    val viewModel = remember { SellerCustomersViewModel() }
    val clientesState by viewModel.clientesStateFlow.collectAsState(initial = emptyList())
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }

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
                label = { Text("Buscar clientes...") },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.padding(8.dp))

            Button(
                onClick = {
                    viewModel.goToEditClient(null)
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(8.dp, CircleShape),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.person_add),
                    contentDescription = "Agregar cliente",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(clientesState) { cliente ->
                ClienteItem(cliente = cliente, onDelete = {
                    selectedCliente = cliente
                    showDialog = true
                })
            }
        }

        if (showDialog && selectedCliente != null) {
            DeleteConfirmationDialog(
                onConfirm = {
                    viewModel.deleteCliente(selectedCliente!!)
                    selectedCliente = null
                    showDialog = false
                },
                onCancel = {
                    showDialog = false
                    selectedCliente = null
                }
            )
        }
    }
}

@Composable
private fun ClienteItem(cliente: Cliente, onDelete: () -> Unit) {
    val viewModel = remember { SellerCustomersViewModel() }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = {
            viewModel.goToEditClient(cliente)
        }
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
                        colorFilter = ColorFilter.tint(Color.White), // Tintado de la imagen a blanco
                        modifier = Modifier.size(25.dp) // Tamaño de la imagen
                    )
                }
            }
        }
    }
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
