package Screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image

@Composable
fun GestionSucursalesScreen(navController: NavController) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser
    val context = LocalContext.current
    var menuVisible by remember { mutableStateOf(false) }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, Map<String, Any>>>()) } // (id, datos)
    var selectedSucursalId by remember { mutableStateOf("") }
    var nombreSucursal by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var responsable by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para gestionar sucursales", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }

    // Cargar sucursales del usuario
    LaunchedEffect(currentUser) {
        isLoading = true
        db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid).get()
            .addOnSuccessListener { result ->
                sucursales = result.documents.map { it.id to (it.data ?: emptyMap()) }
                isDataLoaded = true
                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar sucursales: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }
    // Cuando selecciona una sucursal, cargar sus datos
    LaunchedEffect(selectedSucursalId) {
        if (selectedSucursalId.isNotEmpty()) {
            val sucursal = sucursales.find { it.first == selectedSucursalId }?.second
            if (sucursal != null) {
                nombreSucursal = sucursal["nombre"] as? String ?: ""
                direccion = sucursal["direccion"] as? String ?: ""
                responsable = sucursal["responsable"] as? String ?: ""
                imageUrl = sucursal["imagenUrl"] as? String
                imageUri = null
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(Color.White)) {
            // Encabezado igual que MainActivity
            Box(
                modifier = Modifier.fillMaxWidth().background(Color.Black).systemBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 8.dp),
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
                text = "Gestionar sucursales",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )
            // Lista de sucursales
            if (sucursales.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(sucursales) { (id, datos) ->
                        val nombre = datos["nombre"] as? String ?: "(Sin nombre)"
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White)
                                .clickable { selectedSucursalId = id }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = nombre,
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
        }
        // Formulario de edición montado encima
        if (selectedSucursalId.isNotEmpty() && sucursales.any { it.first == selectedSucursalId }) {
            Box(
                Modifier.fillMaxSize().background(Color(0xCC000000)).clickable { selectedSucursalId = "" },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.95f).background(Color.White, shape = RoundedCornerShape(20.dp)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(32.dp).clickable { selectedSucursalId = "" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar datos de la sucursal", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)).background(Color.White)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Imagen sucursal",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (!imageUrl.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Imagen sucursal",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Imagen sucursal",
                                tint = Color.Gray,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = nombreSucursal,
                        onValueChange = { nombreSucursal = it },
                        label = { Text("Nombre de la sucursal *", color = Color.Black) },
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).height(56.dp),
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
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección", color = Color.Black) },
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).height(56.dp),
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
                        value = responsable,
                        onValueChange = { responsable = it },
                        label = { Text("Persona a cargo", color = Color.Black) },
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).height(56.dp),
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
                    Button(
                        onClick = {
                            if (nombreSucursal.isBlank()) {
                                Toast.makeText(context, "Completa el nombre de la sucursal", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            fun guardarSucursal(urlImagen: String?) {
                                val sucursal = hashMapOf(
                                    "nombre" to nombreSucursal,
                                    "direccion" to direccion,
                                    "responsable" to responsable,
                                    "usuarioId" to currentUser.uid,
                                    "imagenUrl" to (urlImagen ?: "")
                                )
                                db.collection("sucursales").document(selectedSucursalId).set(sucursal)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Sucursal actualizada exitosamente", Toast.LENGTH_SHORT).show()
                                        selectedSucursalId = ""
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Error al actualizar sucursal: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            if (imageUri != null) {
                                val userId = Firebase.auth.currentUser?.uid ?: ""
                                val storageRef = Firebase.storage.reference.child("users/$userId/sucursales/${System.currentTimeMillis()}.jpg")
                                storageRef.putFile(imageUri!!)
                                    .continueWithTask { task ->
                                        if (!task.isSuccessful) throw task.exception ?: Exception("Error al subir imagen")
                                        storageRef.downloadUrl
                                    }
                                    .addOnSuccessListener { uri ->
                                        guardarSucursal(uri.toString())
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                guardarSucursal(imageUrl)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(50)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color.Black),
                        enabled = !isLoading
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
        }
        // Menú hamburguesa igual que MainActivity
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
