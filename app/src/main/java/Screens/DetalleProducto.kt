package com.example.wherehouse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.compose.rememberNavController
import Screens.SucursalDropdownProducto
import Screens.HamburgerMenu
import androidx.compose.foundation.BorderStroke
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import android.widget.Toast

@Composable
fun ProductDetailScreen(navController: NavController, productoId: String) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage
    val currentUser = auth.currentUser
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para ver el detalle del producto", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }
    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precioCompra by remember { mutableStateOf("") }
    var precioVenta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var menuVisible by remember { mutableStateOf(false) }
    var selectedSucursal by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    LaunchedEffect(productoId) {
        db.collection("productos").document(productoId).get().addOnSuccessListener { doc ->
            nombre = doc.getString("nombre") ?: ""
            cantidad = doc.getLong("cantidad")?.toString() ?: ""
            precioCompra = doc.getDouble("precioCompra")?.toString() ?: ""
            precioVenta = doc.getDouble("precioVenta")?.toString() ?: ""
            descripcion = doc.getString("descripcion") ?: ""
            selectedSucursal = doc.getString("sucursalId") ?: ""
            imagenUrl = doc.getString("imagenUrl") ?: ""
        }
    }
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
                text = "Detalle del producto",
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
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = "+",
                                color = Color.Gray,
                                style = MaterialTheme.typography.displayLarge
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto", color = Color.Black) },
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
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        value = precioCompra,
                        onValueChange = { precioCompra = it },
                    label = { Text("Precio de compra", color = Color.Black) },
                    leadingIcon = { Text("$", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        value = precioVenta,
                        onValueChange = { precioVenta = it },
                    label = { Text("Precio de venta", color = Color.Black) },
                    leadingIcon = { Text("$", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del producto", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .height(100.dp),
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
                Spacer(modifier = Modifier.height(12.dp))
                // Menú desplegable de sucursal
                Screens.SucursalDropdownProducto(db = db, currentUser = currentUser, onSucursalSelected = {})
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (currentUser == null) {
                            Toast.makeText(context, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (nombre.isBlank() || cantidad.isBlank() || precioCompra.isBlank() || precioVenta.isBlank() || descripcion.isBlank() || selectedSucursal.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        fun actualizarProducto(urlImagen: String?) {
                            val producto = hashMapOf(
                                "nombre" to nombre,
                                "cantidad" to cantidad.toIntOrNull(),
                                "precioCompra" to precioCompra.toDoubleOrNull(),
                                "precioVenta" to precioVenta.toDoubleOrNull(),
                                "descripcion" to descripcion,
                                "usuarioId" to currentUser.uid,
                                "sucursalId" to selectedSucursal,
                                "imagenUrl" to (urlImagen ?: imagenUrl)
                            )
                            db.collection("productos").document(productoId).set(producto)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        if (imageUri != null) {
                            val ref = storage.reference.child("productos/${currentUser.uid}/${System.currentTimeMillis()}.jpg")
                            ref.putFile(imageUri!!)
                                .continueWithTask { task ->
                                    if (!task.isSuccessful) throw task.exception ?: Exception("Error al subir imagen")
                                    ref.downloadUrl
                                }
                                .addOnSuccessListener { uri ->
                                    actualizarProducto(uri.toString())
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(context, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            actualizarProducto(null)
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
                        text = "ACTUALIZAR PRODUCTO",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    }
                }
            }
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
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EightScreenPreview() {
    val navController = rememberNavController()
    ProductDetailScreen(navController, "")
}
