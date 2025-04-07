package com.example.wherehouse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.example.wherehouse.R
import com.example.wherehouse.ui.theme.Black
import com.example.wherehouse.ui.theme.WherehouseTheme

@Composable
fun AddBranchScreen() {

    var branchName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var responsiblePerson by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8AA1A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Añadir sucursal",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Imagen de la sucursal",
                tint = Color.Gray,
                modifier = Modifier.size(70.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campo: Nombre de la sucursal
        OutlinedTextField(
            value = branchName,
            onValueChange = { branchName = it },
            label = { Text("Nombre de la sucursal") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo: Dirección
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo: Persona a cargo
        OutlinedTextField(
            value = responsiblePerson,
            onValueChange = { responsiblePerson = it },
            label = { Text("Persona a cargo") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Acción para añadir sucursal */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(
                text = "AÑADIR SUCURSAL",
                color = Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿NO TIENE PERSONAL A UN RESPONSABLE?",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                // Acción al hacer clic
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NineScreen() {
    WherehouseTheme {
        AddBranchScreen()
    }
}
