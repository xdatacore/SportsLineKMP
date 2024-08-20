package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.Usuario
import navigation.Router
import utils.SecurityStorage
import viewModels.AccountViewModel

@Composable
fun AccountScreen(viewModel: AccountViewModel) {
    var id by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(1) }
    var message by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val usuario = viewModel.getCurrentUser()
        if (usuario != null) {
            nombreUsuario = usuario.nombreUsuario
            clave = usuario.clave
            tipo = usuario.tipo
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            readOnly = true,
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Clave") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = tipo.toString(),
            onValueChange = { tipo = it.toIntOrNull() ?: 1 },
            label = { Text("Tipo") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Button(
                onClick = {
                    val result = viewModel.deleteUser(Usuario(nombreUsuario, clave, tipo))
                    message =
                        if (result) "Usuario eliminado con éxito" else "Error al eliminar el usuario"
                    showSnackbar = true
                    if (result) onLogout()
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Eliminar")
            }

            Button(
                onClick = {
                    val result = viewModel.saveUser(Usuario(nombreUsuario, clave, tipo))
                    message =
                        if (result) "Usuario guardado con éxito" else "Error al guardar el usuario"
                    showSnackbar = true
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Guardar")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showSnackbar) {
            Snackbar(
                action = {
                    Button(onClick = { showSnackbar = false }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(message)
            }
        }

        Button(
            onClick = { onLogout() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Logout")
        }
    }
}

fun onLogout() {
    SecurityStorage.clearAll()
    Router.reset()
}
