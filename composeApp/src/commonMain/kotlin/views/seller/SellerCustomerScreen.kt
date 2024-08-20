package views.seller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import models.Cliente
import utils.XPrintln
import viewModels.seller.SellerCustomerViewModel

@Composable
fun SellerCustomerScreen(cliente: Cliente?, onBack: () -> Unit) {
    val viewModel = remember { SellerCustomerViewModel() }
    val isNewState = viewModel.isNew.collectAsState()
//    var idCliente by remember { mutableStateOf("") }
//    var nombre by remember { mutableStateOf("") }
//    var apellido by remember { mutableStateOf("") }
//    var correo by remember { mutableStateOf("") }

    val clienteState by viewModel.cliente.collectAsState()

    var message by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        XPrintln.log("Init SellerCustomerScreen ${cliente}")
        if (cliente != null) {
            viewModel.setIsNew(false)
            viewModel.updateCliente(cliente)
        } else {
            viewModel.updateCliente(
                Cliente(
                    idCliente = "",
                    nombre = "",
                    apellido = "",
                    correo = ""
                )
            )
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        clienteState.let {
            it.idCliente?.let { it1 ->
                OutlinedTextField(
                    value = it1,
                    onValueChange = { newId -> viewModel.updateCliente(it.copy(idCliente = newId)) },
                    label = { Text("ID del Cliente") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            it.nombre?.let { it1 ->
                OutlinedTextField(
                    value = it1,
                    onValueChange = { newNombre -> viewModel.updateCliente(it.copy(nombre = newNombre)) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            it.apellido?.let { it1 ->
                OutlinedTextField(
                    value = it1,
                    onValueChange = { newApellido -> viewModel.updateCliente(it.copy(apellido = newApellido)) },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            OutlinedTextField(
                value = it.correo ?: "",
                onValueChange = { newCorreo -> viewModel.updateCliente(it.copy(correo = newCorreo)) },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Button(
                onClick = {
                    runBlocking {
                        val result = viewModel.saveClient()
                        message =
                            if (result) "Cliente guardado con éxito" else "Error al guardar el cliente"
                    }
                    showSnackbar = true
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Guardar")
            }
            Button(
                onClick = {
                    runBlocking {
                        val result = viewModel.deleteClient()
                        message =
                            if (result) "Cliente eliminado con éxito" else "Error al eliminar el cliente"
                    }
                    showSnackbar = true
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Eliminar")
            }
        }
    }
}