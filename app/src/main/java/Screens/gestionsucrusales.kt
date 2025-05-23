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
import androidx.compose.material3.AlertDialog

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
    var filtroNombre by remember { mutableStateOf("") }
    var showSugerenciasNombre by remember { mutableStateOf(false) }
    var staffList by remember { mutableStateOf(listOf<Pair<String, Map<String, Any>>>()) }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var staffPendienteId by remember { mutableStateOf("") }
    var sucursalAnteriorNombre by remember { mutableStateOf("") }
    val nombresUnicos = sucursales.mapNotNull { it.second["nombre"] as? String }.distinct().filter { it.contains(filtroNombre, true) && filtroNombre.isNotBlank() }
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

    // Reemplazo LaunchedEffect(currentUser) por un listener en tiempo real:
    DisposableEffect(currentUser) {
        var sucursalesListener: ListenerRegistration? = null
        if (currentUser != null) {
            sucursalesListener = db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        sucursales = snapshot.documents.map { it.id to (it.data ?: emptyMap()) }
                    }
                }
        }
        onDispose {
            sucursalesListener?.remove()
        }
    }

    // Listener en tiempo real para staff
    DisposableEffect(currentUser) {
        var staffListener: ListenerRegistration? = null
        if (currentUser != null) {
            staffListener = db.collection("staff").whereEqualTo("usuarioId", currentUser.uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        staffList = snapshot.documents.map { it.id to (it.data ?: emptyMap()) }
                    }
                }
        }
        onDispose {
            staffListener?.remove()
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
            // Filtro debajo del título
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
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
            // Lista de sucursales
            val sucursalesFiltradas = sucursales.filter {
                filtroNombre.isBlank() || (it.second["nombre"] as? String)?.contains(filtroNombre, ignoreCase = true) == true
            }
            if (sucursalesFiltradas.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sucursalesFiltradas) { (id, datos) ->
                        val nombre = datos["nombre"] as? String ?: "(Sin nombre)"
                        val direccion = datos["direccion"] as? String ?: ""
                        val encargado = datos["responsable"] as? String
                        val imagenUrl = datos["imagenUrl"] as? String
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF8AA1A))
                                .padding(8.dp)
                                .clickable { selectedSucursalId = id },
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
                                        contentDescription = "Imagen sucursal",
                                        modifier = Modifier.size(40.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Imagen sucursal",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nombre,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (direccion.isNotBlank()) {
                                    Text(
                                        text = direccion,
                                        color = Color.DarkGray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (!encargado.isNullOrBlank()) {
                                    Text(
                                        text = encargado,
                                        color = Color.Black,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    Text(
                                        text = "Sin encargado",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodyMedium
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
        if (selectedSucursalId.isNotEmpty() && sucursales.any { it.first == selectedSucursalId }) {
            Box(
                Modifier.fillMaxSize().background(Color(0xCC000000)).clickable { selectedSucursalId = "" },
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
                            modifier = Modifier.size(32.dp).clickable { selectedSucursalId = "" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar datos de la sucursal", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar sucursal",
                            tint = Color.Red,
                            modifier = Modifier.size(32.dp).clickable {
                                isLoading = true
                                db.collection("sucursales").document(selectedSucursalId).delete()
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Sucursal eliminada", Toast.LENGTH_SHORT).show()
                                        selectedSucursalId = ""
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Error al eliminar sucursal: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    // Staff sin sucursal asignada o asignados a esta sucursal
                    val staffSinSucursal = remember(staffList, selectedSucursalId) {
                        staffList.filter {
                            val sucursalStaff = it.second["sucursalId"] as? String
                            sucursalStaff.isNullOrBlank() || sucursalStaff == selectedSucursalId
                        }
                    }
                    var expandedStaff by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedStaff = true },
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(50)),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                        ) {
                            Text(
                                if (responsable.isNotBlank()) responsable else "Selecciona persona a cargo",
                                color = if (responsable.isNotBlank()) Color.Black else Color.Gray,
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
                                val tieneSucursal = !sucursalStaffId.isNullOrBlank() && sucursalStaffId != selectedSucursalId
                                DropdownMenuItem(
                                    onClick = {
                                        if (tieneSucursal) {
                                            staffPendienteId = id
                                            sucursalAnteriorNombre = sucursales.find { it.first == sucursalStaffId }?.second?.get("nombre") as? String ?: ""
                                            showDialogConfirmacion = true
                                        } else {
                                            responsable = staff["nombre"] as? String ?: ""
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
                                    responsable = ""
                                    expandedStaff = false
                                },
                                text = { Text("Sin encargado", color = Color.Gray) }
                            )
                        }
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
                                    val staff = staffList.find { it.first == staffPendienteId }?.second
                                    val nombreStaff = staff?.get("nombre") as? String ?: ""
                                    responsable = nombreStaff
                                    showDialogConfirmacion = false
                                }) { Text("Sí, reasignar") }
                            },
                            dismissButton = {
                                Row {
                                    TextButton(onClick = {
                                        responsable = ""
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
                                        val nombreStaff = staff?.get("nombre") as? String ?: ""
                                        responsable = nombreStaff
                                        showDialogConfirmacion = false
                                    }) { Text("Dejar anterior sin staff") }
                                }
                            }
                        )
                    }
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
                                        // Actualizar staff automáticamente
                                        if (responsable.isNotBlank()) {
                                            // Buscar el staff seleccionado por nombre
                                            val staffResponsable = staffList.find { (id, staff) -> staff["nombre"] == responsable }
                                            // Quitar la sucursal a cualquier otro staff que la tuviera
                                            staffList.forEach { (id, staff) ->
                                                val sucursalStaff = staff["sucursalId"] as? String
                                                if (sucursalStaff == selectedSucursalId && staff["nombre"] != responsable) {
                                                    db.collection("staff").document(id).update("sucursalId", "")
                                                }
                                            }
                                            // Asignar la sucursal al staff responsable
                                            staffResponsable?.let { (id, _) ->
                                                db.collection("staff").document(id).update("sucursalId", selectedSucursalId)
                                            }
                                        } else {
                                            // Si no hay responsable, quitar la sucursal a todos los staff
                                            staffList.forEach { (id, staff) ->
                                                val sucursalStaff = staff["sucursalId"] as? String
                                                if (sucursalStaff == selectedSucursalId) {
                                                    db.collection("staff").document(id).update("sucursalId", "")
                                                }
                                            }
                                        }
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
        if (selectedSucursalId.isEmpty() && !menuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 64.dp, end = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navController.navigate("add_branch") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text(
                        text = "AÑADIR SUCURSAL",
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
