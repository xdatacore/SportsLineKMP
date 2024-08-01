package views.seller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewModels.seller.SellerReportsViewModel

@Composable
fun SellerReportsScreen(viewModel: SellerReportsViewModel) {
    var selectedReport by remember { mutableStateOf("Ventas por Fecha") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf("") }
    var reportData by remember { mutableStateOf(emptyList<String>()) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Generar Reportes",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedReport)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    text = { Text("Ventas por Fecha") },
                    onClick = { selectedReport = "Ventas por Fecha"; expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Ventas por Cliente") },
                    onClick = { selectedReport = "Ventas por Cliente"; expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Compras") },
                    onClick = { selectedReport = "Compras"; expanded = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedReport) {
            "Ventas por Fecha" -> {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Fecha de Inicio") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Fecha de Fin") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }

            "Ventas por Cliente" -> {
                OutlinedTextField(
                    value = customerId,
                    onValueChange = { customerId = it },
                    label = { Text("ID del Cliente") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                reportData = when (selectedReport) {
                    "Ventas por Fecha" -> viewModel.getSalesReportByDate(startDate, endDate)
                    "Ventas por Cliente" -> viewModel.getSalesReportByCustomer(customerId)
                    "Compras" -> viewModel.getPurchaseReport()
                    else -> emptyList()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Generar Reporte")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            reportData.forEach { data ->
                Text(data, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
