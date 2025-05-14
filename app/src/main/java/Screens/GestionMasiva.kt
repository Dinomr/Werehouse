package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.clickable

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GestionMasivaScreen(navController: NavController, esIncremento: Boolean) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser
    val context = LocalContext.current

    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para modificar productos", Toast.LENGTH_LONG).show()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
        return
    }

    var productos by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var cantidades by remember { mutableStateOf(mapOf<String, String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var sucursalFiltro by remember { mutableStateOf("") }
    var sucursalFiltroId by remember { mutableStateOf("") }
    var sucursalExpanded by remember { mutableStateOf(false) }
    var menuVisible by remember { mutableStateOf(false) }

    // Cargar productos y sucursales
    LaunchedEffect(currentUser) {
        db.collection("productos").whereEqualTo("usuarioId", currentUser.uid)
            .get().addOnSuccessListener { result ->
                productos = result.documents.map { it.data?.plus("id" to it.id) ?: emptyMap() }
                cantidades = productos.associate { it["id"].toString() to "0" }
            }
        
        db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid)
            .get().addOnSuccessListener { result ->
                sucursales = result.documents.map { it.id to (it.getString("nombre") ?: "") }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(16.dp)
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
            text = if (esIncremento) "Incrementar cantidades" else "Decrementar cantidades",
            style = MaterialTheme.typography.displaySmall,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Filtro de sucursales
        if (sucursales.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = sucursalExpanded,
                onExpandedChange = { sucursalExpanded = !sucursalExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = sucursalFiltro,
                    onValueChange = { sucursalFiltro = it },
                    label = { Text("Filtrar por sucursal", color = Color.Black) },
                    textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .background(Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )
                ExposedDropdownMenu(
                    expanded = sucursalExpanded,
                    onDismissRequest = { sucursalExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    sucursales.filter { it.second.contains(sucursalFiltro, true) }.forEach { (id, nombre) ->
                        DropdownMenuItem(
                            onClick = {
                                sucursalFiltro = nombre
                                sucursalFiltroId = id
                                sucursalExpanded = false
                            },
                            text = { Text(nombre, style = MaterialTheme.typography.bodySmall, color = Color.Black) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de productos
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val productosFiltrados = productos.filter {
                sucursalFiltroId.isBlank() || it["sucursalId"] == sucursalFiltroId
            }
            
            productosFiltrados.forEach { producto ->
                val id = producto["id"].toString()
                val nombre = producto["nombre"] as? String ?: ""
                val cantidadActual = (producto["cantidad"] as? Number)?.toInt() ?: 0
                val cantidadModificar = cantidades[id]?.toIntOrNull() ?: 0
                val cantidadResultante = if (esIncremento) {
                    cantidadActual + cantidadModificar
                } else {
                    cantidadActual - cantidadModificar
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8AA1A))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        val imagenUrl = producto["imagenUrl"] as? String
                        if (!imagenUrl.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(imagenUrl),
                                contentDescription = "Imagen producto",
                                modifier = Modifier.size(40.dp)
                            )
                        } else {
                            Text(
                                text = nombre.take(1).uppercase(),
                                color = Color.Gray,
                                style = MaterialTheme.typography.displayLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(nombre, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Cantidad actual: $cantidadActual",
                            color = Color.Black.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Cantidad resultante: $cantidadResultante",
                            color = if (cantidadResultante < 0) Color.Red else Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val sucursalNombre = sucursales.find { it.first == producto["sucursalId"] }?.second ?: ""
                        if (sucursalNombre.isNotBlank()) {
                            Text(
                                text = "Sucursal: $sucursalNombre",
                                color = Color.DarkGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    OutlinedTextField(
                        value = cantidades[id] ?: "",
                        onValueChange = { value ->
                            if (value.all { it.isDigit() }) {
                                cantidades = cantidades.toMutableMap().apply { put(id, value) }
                            }
                        },
                        placeholder = { Text(nombre.take(1).uppercase(), color = Color.LightGray) },
                        label = { Text("Cantidad a ${if (esIncremento) "sumar" else "restar"}", color = Color.Black) },
                        singleLine = true,
                        modifier = Modifier.width(90.dp).height(48.dp).background(Color.White, RoundedCornerShape(8.dp)),
                        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, textAlign = TextAlign.Center),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                isLoading = true
                var error = false
                productos.forEach { producto ->
                    val id = producto["id"].toString()
                    val cantidadOriginal = (producto["cantidad"] as? Number)?.toInt() ?: 0
                    val cantidadNueva = (cantidades[id]?.toIntOrNull() ?: 0)
                    val resultado = if (esIncremento) cantidadOriginal + cantidadNueva else cantidadOriginal - cantidadNueva
                    if (resultado < 0) error = true
                }
                if (error) {
                    isLoading = false
                    Toast.makeText(context, "No puedes dejar cantidades negativas", Toast.LENGTH_LONG).show()
                    return@Button
                }
                productos.forEach { producto ->
                    val id = producto["id"].toString()
                    val cantidadOriginal = (producto["cantidad"] as? Number)?.toInt() ?: 0
                    val cantidadNueva = (cantidades[id]?.toIntOrNull() ?: 0)
                    val resultado = if (esIncremento) cantidadOriginal + cantidadNueva else cantidadOriginal - cantidadNueva
                    db.collection("productos").document(id).update("cantidad", resultado)
                }
                isLoading = false
                Toast.makeText(context, "Cantidades actualizadas", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("CONFIRMAR", color = Color.White, style = MaterialTheme.typography.bodyLarge)
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
