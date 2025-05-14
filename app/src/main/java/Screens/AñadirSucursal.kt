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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import Screens.HamburgerMenu
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Menu
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun AddBranchScreen(navController: NavController) {
    val auth = Firebase.auth
    val context = LocalContext.current
    val currentUser = auth.currentUser
    var branchName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var responsiblePerson by remember { mutableStateOf("") }
    var menuVisible by remember { mutableStateOf(false) }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    // Cargar sucursales del usuario
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val db = Firebase.firestore
            db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    sucursales = result.documents.map { it.id to (it.getString("nombre") ?: "") }
                }
        }
    }
    if (currentUser == null) {
        // Si no está autenticado, mostrar mensaje y redirigir
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para añadir sucursales", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }
    val userId = currentUser.uid
    val db = Firebase.firestore
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Encabezado igual que FourScreen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .systemBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { menuVisible = true },
                        tint = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Cuenta",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navController.navigate("login") },
                        tint = Color.Gray
                    )
                }
            }
            Text(
                text = "Añadir sucursal",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF8AA1A), shape = RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
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
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text("Nombre de la sucursal", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .height(56.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .height(56.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (sucursales.isNotEmpty()) {
                    OutlinedTextField(
                        value = responsiblePerson,
                        onValueChange = { responsiblePerson = it },
                        label = { Text("Persona a cargo", color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .height(56.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Button(
                    onClick = {
                        if (branchName.isNotEmpty() && address.isNotEmpty() && (sucursales.isEmpty() || responsiblePerson.isNotEmpty())) {
                            val branch = hashMapOf(
                                "nombre" to branchName,
                                "direccion" to address,
                                "responsable" to if (sucursales.isNotEmpty()) responsiblePerson else null,
                                "usuarioId" to userId
                            )
                            db.collection("sucursales").add(branch)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Sucursal añadida", Toast.LENGTH_SHORT).show()
                                    if (sucursales.isEmpty()) {
                                        navController.navigate("add_staff")
                                    } else {
                                        navController.navigate("success_sucursal")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text(
                        text = "AÑADIR SUCURSAL",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "¿NO TIENE PERSONAL A CARGO?",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("add_staff")
                    }
            )
        }
        if (menuVisible) {
            HamburgerMenu(
                visible = true,
                onDismiss = { menuVisible = false },
                onInventarioClick = {
                    menuVisible = false
                    navController.popBackStack("main", false)
                },
                onAddBranchClick = {
                    menuVisible = false
                    // Ya estás en la pantalla de sucursal, así que no navegas a ningún lado
                },
                onAddStaffClick = {
                    menuVisible = false
                    navController.navigate("add_staff")
                },
                onEditStaffClick = {
                    menuVisible = false
                    navController.navigate("editar_staff")
                },
                onLogoutClick = {
                    menuVisible = false
                    Firebase.auth.signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NineScreenPreview() {
    val navController = rememberNavController()
    AddBranchScreen(navController)
}
