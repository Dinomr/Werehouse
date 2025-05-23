package Screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wherehouse.ui.theme.WherehouseTheme
import androidx.compose.material.icons.Icons
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import com.example.wherehouse.MainActivity
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.BorderStroke
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import android.widget.Toast
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

class AddProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WherehouseTheme {
                val navController = rememberNavController()
                AddProductScreen(navController = navController, onNavigateToMain = {})
            }
        }
    }
}

@Composable
fun AddProductScreen(navController: NavController, onNavigateToMain: () -> Unit) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage
    val currentUser = auth.currentUser
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para añadir productos", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precioCompra by remember { mutableStateOf("") }
    var precioVenta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var menuVisible by remember { mutableStateOf(false) }
    var sucursalId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var onAddStaffClick: (() -> Unit)? = null
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    // Cargar sucursales del usuario
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    sucursales = result.documents.map { it.id to (it.getString("nombre") ?: "") }
                }
        }
    }
    fun navigateToMain() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) (context as Activity).finish()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Encabezado con icono de menú y barra de estado negro
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
                        tint = Color.White // Mejor visibilidad sobre fondo negro
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
                text = "Añadir producto",
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
                    leadingIcon = {
                        Text("$", color = Color.Black)
                    },
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
                    leadingIcon = {
                        Text("$", color = Color.Black)
                    },
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
                if (sucursales.isNotEmpty()) {
                    SucursalDropdownProducto(db = db, currentUser = currentUser) { id -> sucursalId = id }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Button(
                    onClick = {
                        if (currentUser == null) {
                            Toast.makeText(context, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (nombre.isBlank() || cantidad.isBlank() || precioCompra.isBlank() || precioVenta.isBlank() || descripcion.isBlank() || (sucursales.isNotEmpty() && sucursalId.isBlank())) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        fun guardarProducto(urlImagen: String?) {
                            val producto = hashMapOf(
                                "nombre" to nombre,
                                "cantidad" to cantidad.toIntOrNull(),
                                "precioCompra" to precioCompra.toDoubleOrNull(),
                                "precioVenta" to precioVenta.toDoubleOrNull(),
                                "descripcion" to descripcion,
                                "usuarioId" to currentUser.uid,
                                "sucursalId" to if (sucursales.isNotEmpty()) sucursalId else null,
                                "imagenUrl" to (urlImagen ?: "")
                            )
                            db.collection("productos").add(producto)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "Producto creado", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack("main", false)
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
                                    guardarProducto(uri.toString())
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(context, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            guardarProducto(null)
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
                            text = "CREAR NUEVO PRODUCTO",
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
                    navigateToMain()
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
                    // Implementa la lógica para cerrar sesión
                }
            )
        }
    }
}

@Composable
fun SucursalDropdownProducto(db: com.google.firebase.firestore.FirebaseFirestore, currentUser: com.google.firebase.auth.FirebaseUser?, onSucursalSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("¿A qué sucursal pertenece?") }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) } // (id, nombre)
    // Cargar sucursales del usuario
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("sucursales").whereEqualTo("usuarioId", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    sucursales = result.documents.map { it.id to (it.getString("nombre") ?: "") }
                }
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Text(selectedOption, color = Color.Black, modifier = Modifier.fillMaxWidth())
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            sucursales.forEach { (id, nombre) ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption = nombre
                        expanded = false
                        onSucursalSelected(id)
                    },
                    text = { Text(nombre, color = Color.Black) }
                )
            }
        }
    }
}

@Composable
fun HamburgerMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    onInventarioClick: () -> Unit,
    onAddBranchClick: () -> Unit,
    onGestionSucursalesClick: () -> Unit,
    onAddStaffClick: (() -> Unit)?,
    onEditStaffClick: (() -> Unit)?,
    onLogoutClick: (() -> Unit)? = null
) {
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val auth = Firebase.auth
    val isLoggedIn = auth.currentUser != null
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { with(density) { -300.dp.roundToPx() } }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { with(density) { -300.dp.roundToPx() } })
    ) {
        LaunchedEffect(visible) {
            if (visible) keyboardController?.hide()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.95f))
                .systemBarsPadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onDismiss() })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Menú",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AñadirProducto(text = "Inventario", onClick = { onDismiss(); onInventarioClick() })
                AñadirProducto(text = "Añadir sucursal", onClick = { onDismiss(); onAddBranchClick() })
                AñadirProducto(text = "Gestionar sucursales", onClick = { onDismiss(); onGestionSucursalesClick() })
                AñadirProducto(text = "Añadir staff", onClick = { onDismiss(); onAddStaffClick?.invoke() })
                AñadirProducto(text = "Administrar staff", onClick = { onDismiss(); onEditStaffClick?.invoke() })
                Spacer(modifier = Modifier.weight(1f))
                if (isLoggedIn && onLogoutClick != null) {
                    Button(
                        onClick = { onDismiss(); onLogoutClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text("Cerrar sesión", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AñadirProducto(text: String, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8AA1A)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview
@Composable
fun FourScreen() {
    val navController = rememberNavController()
    AddProductScreen(navController = navController, onNavigateToMain = {})
}