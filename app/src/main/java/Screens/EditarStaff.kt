package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wherehouse.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun EditarStaffScreen(navController: NavController) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser
    val context = LocalContext.current
    var menuVisible by remember { mutableStateOf(false) }
    var staffList by remember { mutableStateOf(listOf<Pair<String, Map<String, Any>>>()) }
    var selectedStaffId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Campos editables
    var nombre by remember { mutableStateOf("") }
    var staffId by remember { mutableStateOf("") }
    var sucursalId by remember { mutableStateOf("") }
    var store by remember { mutableStateOf("") }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    if (currentUser == null) {
        // Si no está autenticado, mostrar mensaje y redirigir
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para editar staff", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }

    // Cargar staff del usuario
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("staff").whereEqualTo("usuarioId", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    staffList = result.documents.map { it.id to (it.data ?: emptyMap()) }
                }
        }
    }
    // Cargar sucursales del usuario
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    sucursales = result.documents.map { it.id to (it.getString("nombre") ?: "") }
                }
        }
    }
    // Cuando selecciona un staff, cargar sus datos
    LaunchedEffect(selectedStaffId) {
        val staff = staffList.find { it.first == selectedStaffId }?.second
        if (staff != null) {
            nombre = staff["nombre"] as? String ?: ""
            staffId = staff["staffId"] as? String ?: ""
            sucursalId = staff["sucursalId"] as? String ?: ""
            store = staff["store"] as? String ?: ""
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Encabezado
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
                    modifier = Modifier.size(40.dp).clickable { menuVisible = true },
                    tint = Color.White
                )
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Cuenta",
                    modifier = Modifier.size(40.dp).clickable { navController.navigate("login") },
                    tint = Color.Gray
                )
            }
        }
        Text(
            text = "Editar STAFF",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        // Lista de empleados
        if (staffList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(staffList) { (id, staff) ->
                    val nombreStaff = staff["nombre"] as? String ?: "(Sin nombre)"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .clickable { selectedStaffId = id }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = nombreStaff,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        // Formulario de edición
        if (selectedStaffId.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8AA1A), shape = RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la persona *", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .height(56.dp),
                    textStyle = TextStyle(color = Color.Black, textAlign = TextAlign.Start, fontSize = MaterialTheme.typography.bodyMedium.fontSize),
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
                    value = staffId,
                    onValueChange = { staffId = it },
                    label = { Text("ID del staff *", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .height(56.dp),
                    textStyle = TextStyle(color = Color.Black, textAlign = TextAlign.Start, fontSize = MaterialTheme.typography.bodyMedium.fontSize),
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
                // Dropdown para sucursal (estilo consistente)
                var sucursalExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { sucursalExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(50)),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Text(
                            sucursales.find { it.first == sucursalId }?.second ?: "¿A qué sucursal pertenece?",
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    DropdownMenu(
                        expanded = sucursalExpanded,
                        onDismissRequest = { sucursalExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        sucursales.forEach { (id, nombreSucursal) ->
                            DropdownMenuItem(
                                onClick = {
                                    sucursalId = id
                                    sucursalExpanded = false
                                },
                                text = { Text(nombreSucursal, color = Color.Black) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (nombre.isBlank() || staffId.isBlank()) {
                            Toast.makeText(context, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        val staff = hashMapOf(
                            "nombre" to nombre,
                            "staffId" to staffId,
                            "sucursalId" to sucursalId,
                            "usuarioId" to (currentUser?.uid ?: "")
                        )
                        db.collection("staff").document(selectedStaffId).set(staff)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Staff actualizado", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Text(
                            text = "GUARDAR CAMBIOS",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
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
                navController.navigate("add_branch")
            },
            onAddStaffClick = {
                menuVisible = false
                navController.navigate("add_staff")
            },
            onEditStaffClick = {
                menuVisible = false // Ya está en editar staff
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
