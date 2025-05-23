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
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Add
import coil.compose.rememberAsyncImagePainter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.AlertDialog

@Composable
fun AddBranchScreen(navController: NavController) {
    val auth = Firebase.auth
    val context = LocalContext.current
    val currentUser = auth.currentUser
    var branchName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var menuVisible by remember { mutableStateOf(false) }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        imageUri = uri
    }
    val db = Firebase.firestore
    var staffList by remember { mutableStateOf(listOf<Pair<String, Map<String, Any>>>()) }
    var selectedStaffId by remember { mutableStateOf("") }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var staffPendienteId by remember { mutableStateOf("") }
    var sucursalAnteriorNombre by remember { mutableStateOf("") }
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
    // Cargar staff libre (sin sucursal asignada)
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("staff").whereEqualTo("usuarioId", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    staffList = result.documents.map { it.id to (it.data ?: emptyMap()) }
                }
        }
    }
    val staffSinSucursal = staffList.filter { (id, staff) ->
        val sucursalStaff = staff["sucursalId"] as? String
        sucursalStaff.isNullOrBlank()
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
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Imagen sucursal",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Imagen de la sucursal",
                                tint = Color.Gray,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                        // Ícono de +
                        Box(
                            modifier = Modifier.align(Alignment.BottomEnd).size(36.dp).clip(RoundedCornerShape(50)).background(Color(0xFFF8AA1A)).clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar imagen",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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
                if (staffList.isNotEmpty()) {
                    var expandedStaff by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedStaff = true },
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(50)),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                        ) {
                            Text(
                                if (selectedStaffId.isNotBlank()) staffList.find { it.first == selectedStaffId }?.second?.get("nombre") as? String ?: "Selecciona persona a cargo" else "Selecciona persona a cargo",
                                color = if (selectedStaffId.isNotBlank()) Color.Black else Color.Gray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        DropdownMenu(
                            expanded = expandedStaff,
                            onDismissRequest = { expandedStaff = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            staffList.forEach { (id, staff) ->
                                val nombreStaff = staff["nombre"] as? String ?: "(Sin nombre)"
                                val sucursalStaffId = staff["sucursalId"] as? String
                                val sucursalStaffNombre = sucursales.find { it.first == sucursalStaffId }?.second
                                val tieneSucursal = !sucursalStaffId.isNullOrBlank()
                                DropdownMenuItem(
                                    onClick = {
                                        if (tieneSucursal) {
                                            staffPendienteId = id
                                            sucursalAnteriorNombre = sucursalStaffNombre ?: ""
                                            showDialogConfirmacion = true
                                        } else {
                                            selectedStaffId = id
                                            expandedStaff = false
                                        }
                                    },
                                    text = {
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(nombreStaff, color = Color.Black)
                                            if (tieneSucursal) {
                                                Text("*", color = Color.Red)
                                            }
                                        }
                                    }
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    selectedStaffId = ""
                                    expandedStaff = false
                                },
                                text = { Text("Sin encargado", color = Color.Gray) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // Diálogo de confirmación para reasignar staff
                if (showDialogConfirmacion) {
                    AlertDialog(
                        onDismissRequest = { showDialogConfirmacion = false },
                        title = { Text("¿Reasignar staff?") },
                        text = {
                            Text("El staff seleccionado ya está asignado a la sucursal '$sucursalAnteriorNombre'. ¿Deseas reasignarlo a la nueva sucursal, dejar la sucursal anterior sin staff o seleccionar un staff sin sucursal?")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                selectedStaffId = staffPendienteId
                                showDialogConfirmacion = false
                            }) { Text("Sí, reasignar") }
                        },
                        dismissButton = {
                            Row {
                                TextButton(onClick = {
                                    selectedStaffId = ""
                                    showDialogConfirmacion = false
                                }) { Text("Seleccionar otro staff") }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = {
                                    // Dejar la sucursal anterior sin staff
                                    val staff = staffList.find { it.first == staffPendienteId }?.second
                                    val sucursalStaffId = staff?.get("sucursalId") as? String
                                    if (!sucursalStaffId.isNullOrBlank()) {
                                        db.collection("staff").document(staffPendienteId).update("sucursalId", "")
                                    }
                                    selectedStaffId = staffPendienteId
                                    showDialogConfirmacion = false
                                }) { Text("Dejar anterior sin staff") }
                            }
                        }
                    )
                }
                Button(
                    onClick = {
                        val staffObligatorio = staffSinSucursal.isNotEmpty() || (selectedStaffId.isNotBlank() && staffList.find { it.first == selectedStaffId }?.second?.get("sucursalId") != null)
                        if (branchName.isNotEmpty() && address.isNotEmpty() && (!staffObligatorio || selectedStaffId.isNotEmpty())) {
                            val branch = hashMapOf(
                                "nombre" to branchName,
                                "direccion" to address,
                                "responsable" to if (selectedStaffId.isNotEmpty()) staffList.find { it.first == selectedStaffId }?.second?.get("nombre") else null,
                                "usuarioId" to userId
                            )
                            db.collection("sucursales").add(branch)
                                .addOnSuccessListener { sucursalRef ->
                                    // Actualizar staff automáticamente
                                    if (selectedStaffId.isNotEmpty()) {
                                        val staff = staffList.find { it.first == selectedStaffId }?.second
                                        val sucursalStaffId = staff?.get("sucursalId") as? String
                                        if (!sucursalStaffId.isNullOrBlank() && sucursalStaffId != sucursalRef.id) {
                                            // Si el staff estaba asignado a otra sucursal, quitarlo de la anterior
                                            db.collection("staff").document(selectedStaffId).update("sucursalId", sucursalRef.id)
                                        } else {
                                            // Asignar la sucursal al staff responsable
                                            db.collection("staff").document(selectedStaffId).update("sucursalId", sucursalRef.id)
                                        }
                                    }
                                    Toast.makeText(context, "Sucursal añadida", Toast.LENGTH_SHORT).show()
                                    navController.navigate("success_sucursal")
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
                onGestionSucursalesClick = {
                    menuVisible = false
                    navController.navigate("gestion_sucursales")
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
