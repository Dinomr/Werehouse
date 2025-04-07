package com.example.wherehouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wherehouse.ui.theme.WherehouseTheme
import com.example.wherehouse.ui.theme.White

@Composable
fun TransferScreen() {
    var bodegaActual by remember { mutableStateOf("") }
    var productoActual by remember { mutableStateOf("") }
    var bodegaDestino by remember { mutableStateOf("") }
    var productoDestino by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gestión de transferencia\nentre sucursales",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8AA1A))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Icono de transferencia",
                    tint = Color.Black,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bodega actual",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bodegaActual,
                    onValueChange = { bodegaActual = it },
                    label = { Text("Bodega") },
                    modifier = Modifier.fillMaxWidth().background(White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productoActual,
                    onValueChange = { productoActual = it },
                    label = { Text("Producto") },
                    modifier = Modifier.fillMaxWidth().background(White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bodega destino",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bodegaDestino,
                    onValueChange = { bodegaDestino = it },
                    label = { Text("Bodega") },
                    modifier = Modifier.fillMaxWidth().background(White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productoDestino,
                    onValueChange = { productoDestino = it },
                    label = { Text("Producto") },
                    modifier = Modifier.fillMaxWidth().background(White)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .border(2.dp, Color.Black, RoundedCornerShape(50))
                .padding(vertical = 8.dp, horizontal = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Realizar transferencia",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Icono flecha",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TenScreen() {
    WherehouseTheme {
        TransferScreen()
    }
}
