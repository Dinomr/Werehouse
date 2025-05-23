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
import androidx.compose.material.icons.filled.Delete
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
import com.example.wherehouse.R
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
import androidx.compose.foundation.border
import com.google.firebase.firestore.ListenerRegistration


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
    var isDataLoaded by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var staffId by remember { mutableStateOf("") }
    var sucursalId by remember { mutableStateOf("") }
    var store by remember { mutableStateOf("") }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    // Filtros debajo del título
    var filtroNombre by remember { mutableStateOf("") }
    var filtroId by remember { mutableStateOf("") }
    var showSugerenciasNombre by remember { mutableStateOf(false) }
    var showSugerenciasId by remember { mutableStateOf(false) }
    val nombresUnicos = staffList.mapNotNull { it.second["nombre"] as? String }.distinct().filter { it.contains(filtroNombre, true) && filtroNombre.isNotBlank() }
    val idsUnicos = staffList.mapNotNull { it.second["staffId"] as? String }.distinct().filter { it.contains(filtroId, true) && filtroId.isNotBlank() }

    // Sucursales con y sin encargado
    val sucursalesConEncargado = staffList.mapNotNull { it.second["sucursalId"] as? String }
    val sucursalesDisponibles = sucursales.filter { sucursal ->
        !sucursalesConEncargado.contains(sucursal.first) || sucursal.first == sucursalId
    }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var sucursalSeleccionadaPendiente by remember { mutableStateOf("") }
    var encargadoActual by remember { mutableStateOf("") }

    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para editar staff", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }

    // Reemplazo LaunchedEffect(currentUser) por un listener en tiempo real:
    DisposableEffect(currentUser) {
        var staffListener: ListenerRegistration? = null
        var sucursalesListener: ListenerRegistration? = null
        if (currentUser != null) {
            staffListener = db.collection("staff").whereEqualTo("usuarioId", currentUser.uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        staffList = snapshot.documents.map { it.id to (it.data ?: emptyMap()) }
                    }
                }
            sucursalesListener = db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        sucursales = snapshot.documents.map { it.id to (it.getString("nombre") ?: "") }
                    }
                }
        }
        onDispose {
            staffListener?.remove()
            sucursalesListener?.remove()
        }
    }

    // Cuando selecciona un staff, cargar sus datos
    LaunchedEffect(selectedStaffId) {
        if (selectedStaffId.isNotEmpty()) {
        val staff = staffList.find { it.first == selectedStaffId }?.second
        if (staff != null) {
            nombre = staff["nombre"] as? String ?: ""
            staffId = staff["staffId"] as? String ?: ""
            sucursalId = staff["sucursalId"] as? String ?: ""
            store = staff["store"] as? String ?: ""
                imageUrl = staff["imagenUrl"] as? String
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
            text = "Editar STAFF",
            style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )
            // Filtros debajo del título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = filtroNombre,
                        onValueChange = {
                            filtroNombre = it
                            showSugerenciasNombre = it.isNotBlank()
                        },
                        label = { Text("Filtrar por nombre", color = Color.Black, style = MaterialTheme.typography.bodySmall) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().background(Color.White),
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
                    if (showSugerenciasNombre && nombresUnicos.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                nombresUnicos.forEach { option ->
                                    Text(
                                        text = option,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                filtroNombre = option
                                                showSugerenciasNombre = false
                                            }
                                            .padding(12.dp),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = filtroId,
                        onValueChange = {
                            filtroId = it
                            showSugerenciasId = it.isNotBlank()
                        },
                        label = { Text("Filtrar por ID", color = Color.Black, style = MaterialTheme.typography.bodySmall) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().background(Color.White),
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
                    if (showSugerenciasId && idsUnicos.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                idsUnicos.forEach { option ->
                                    Text(
                                        text = option,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                filtroId = option
                                                showSugerenciasId = false
                                            }
                                            .padding(12.dp),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // Lista de empleados
            val staffFiltrado = staffList.filter {
                (filtroNombre.isBlank() || (it.second["nombre"] as? String)?.contains(filtroNombre, ignoreCase = true) == true) &&
                (filtroId.isBlank() || (it.second["staffId"] as? String)?.contains(filtroId, ignoreCase = true) == true)
            }
            if (staffFiltrado.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(staffFiltrado) { (id, staff) ->
                    val nombreStaff = staff["nombre"] as? String ?: "(Sin nombre)"
                        val sucursalNombre = sucursales.find { it.first == (staff["sucursalId"] as? String) }?.second ?: ""
                        val imagenUrl = staff["imagenUrl"] as? String
                        Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF8AA1A))
                                .padding(8.dp)
                                .clickable { selectedStaffId = id },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!imagenUrl.isNullOrBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imagenUrl),
                                        contentDescription = "Imagen staff",
                                        modifier = Modifier.size(40.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Imagen staff",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nombreStaff,
                            color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (sucursalNombre.isNotBlank()) {
                                    Text(
                                        text = "Sucursal: $sucursalNombre",
                                        color = Color.DarkGray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay resultados", color = Color.Gray)
                }
            }
        }
        // Formulario de edición montado encima (penúltimo)
        if (selectedStaffId.isNotEmpty() && staffList.any { it.first == selectedStaffId }) {
            Box(
                Modifier.fillMaxSize().background(Color(0xCC000000)).clickable { selectedStaffId = "" },
                contentAlignment = Alignment.Center
            ) {
            Column(
                modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .background(Color.White, shape = RoundedCornerShape(20.dp))
                        .border(BorderStroke(2.dp, Color(0xFFF8AA1A)), RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(32.dp).clickable { selectedStaffId = "" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar datos del staff", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar staff",
                            tint = Color.Red,
                            modifier = Modifier.size(32.dp).clickable {
                                isLoading = true
                                db.collection("staff").document(selectedStaffId).delete()
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Staff eliminado", Toast.LENGTH_SHORT).show()
                                        selectedStaffId = ""
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Error al eliminar staff: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        )
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
                                contentDescription = "Imagen staff",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (!imageUrl.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Imagen staff",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Imagen staff",
                                tint = Color.Gray,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la persona *", color = Color.Black) },
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
                    value = staffId,
                    onValueChange = { staffId = it },
                    label = { Text("ID del staff *", color = Color.Black) },
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
                var sucursalExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { sucursalExpanded = true },
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(50)),
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
                            sucursalesDisponibles.forEach { (id, nombreSucursal) ->
                                val encargado = staffList.find { it.second["sucursalId"] == id }?.second?.get("nombre") as? String
                                val staffEncargadoId = staffList.find { it.second["sucursalId"] == id }?.first
                            DropdownMenuItem(
                                onClick = {
                                        if (encargado != null && id != sucursalId && staffEncargadoId != selectedStaffId) {
                                            sucursalSeleccionadaPendiente = id
                                            encargadoActual = encargado
                                            showDialogConfirmacion = true
                                        } else {
                                    sucursalId = id
                                    sucursalExpanded = false
                                        }
                                    },
                                    text = {
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(nombreSucursal, color = Color.Black)
                                            if (encargado != null && id != sucursalId && staffEncargadoId != selectedStaffId) {
                                                Text("(Encargado: $encargado)", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    if (showDialogConfirmacion) {
                        AlertDialog(
                            onDismissRequest = { showDialogConfirmacion = false },
                            title = { Text("¿Reasignar encargado?") },
                            text = {
                                Text("Esta sucursal ya tiene un encargado ($encargadoActual). ¿Deseas reasignar el encargado a este staff?\n\nTe recomendamos editar el staff sin sucursal asignada para evitar inconsistencias.")
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    // Quitar la sucursal al staff anterior
                                    val staffEncargadoId = staffList.find { it.second["sucursalId"] == sucursalSeleccionadaPendiente }?.first
                                    if (staffEncargadoId != null) {
                                        db.collection("staff").document(staffEncargadoId).update("sucursalId", "")
                                    }
                                    sucursalId = sucursalSeleccionadaPendiente
                                    sucursalExpanded = false
                                    showDialogConfirmacion = false
                                }) {
                                    Text("Sí, reasignar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDialogConfirmacion = false
                                }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                            if (nombre.isBlank() || staffId.isBlank() || sucursalId.isBlank()) {
                                Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                            fun guardarStaff(urlImagen: String?) {
                        val staff = hashMapOf(
                            "nombre" to nombre,
                            "staffId" to staffId,
                            "sucursalId" to sucursalId,
                                    "store" to store,
                                    "usuarioId" to currentUser.uid,
                                    "imagenUrl" to (urlImagen ?: "")
                        )
                        db.collection("staff").document(selectedStaffId).set(staff)
                            .addOnSuccessListener {
                                isLoading = false
                                        Toast.makeText(context, "Staff actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                        selectedStaffId = ""
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Error al actualizar staff: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            if (imageUri != null) {
                                val userId = Firebase.auth.currentUser?.uid ?: ""
                                val storageRef = Firebase.storage.reference.child("users/$userId/${System.currentTimeMillis()}.jpg")
                                storageRef.putFile(imageUri!!)
                                    .continueWithTask { task ->
                                        if (!task.isSuccessful) throw task.exception ?: Exception("Error al subir imagen")
                                        storageRef.downloadUrl
                                    }
                                    .addOnSuccessListener { uri ->
                                        guardarStaff(uri.toString())
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                        Toast.makeText(context, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                guardarStaff(imageUrl)
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
        // Menú hamburguesa igual que MainActivity (último)
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
        // Botón inferior fijo
        if (selectedStaffId.isEmpty() && !menuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 64.dp, end = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navController.navigate("add_staff") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text(
                        text = "AÑADIR STAFF",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
