package com.example.wherehouse

import android.content.res.Configuration
import android.graphics.Bitmap.Config
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat.Style
import com.example.wherehouse.ui.theme.WherehouseTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import Screens.FourScreen
import androidx.compose.foundation.clickable
import Screens.AddProductScreen
import androidx.compose.foundation.BorderStroke
import androidx.navigation.NavController
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import Screens.AddStaffScreen
import Screens.SuccessStaffScreen
import Screens.SuccessSucursalScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import android.widget.Toast
import com.google.firebase.firestore.ListenerRegistration
import androidx.compose.runtime.DisposableEffect
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.CollectionReference
import androidx.compose.ui.platform.LocalContext
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WherehouseTheme {
                val navController = rememberNavController()
                val auth = Firebase.auth
                
                // Verificar sesión al inicio
                LaunchedEffect(Unit) {
                    if (auth.currentUser == null) {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            MainScreen(navController)
                        }
                    }
                    composable("add_product") {
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            AddProductScreen(navController = navController, onNavigateToMain = {})
                        }
                    }
                    composable("add_staff") {
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            Screens.AddStaffScreen(navController)
                        }
                    }
                    composable("editar_staff") {
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            Screens.EditarStaffScreen(navController)
                        }
                    }
                    composable("gestion_sucursales") {
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            Screens.GestionSucursalesScreen(navController)
                        }
                    }
                    composable("login") {
                        Screens.LoginScreen(navController)
                    }
                    composable("crear_cuenta") {
                        com.example.wherehouse.screens.CreateAccountScreen(navController)
                    }
                    composable("success") {
                        Screens.SuccessScreen(onInventoryClick = {
                            navController.navigate("main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }, navController = navController)
                    }
                    composable("success_staff") {
                        Screens.SuccessStaffScreen(onInventoryClick = {
                            navController.navigate("main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }, navController = navController)
                    }
                    composable("success_sucursal") {
                        Screens.SuccessSucursalScreen(onInventoryClick = {
                            navController.navigate("main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }, navController = navController)
                    }
                    composable("detalle_producto/{productoId}") { backStackEntry ->
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
                            com.example.wherehouse.ProductDetailScreen(navController, productoId)
                        }
                    }
                    composable("add_branch") {
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            com.example.wherehouse.screens.AddBranchScreen(navController)
                        }
                    }
                    composable("gestion_masiva?esIncremento={esIncremento}") { backStackEntry ->
                        if (auth.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            val esIncremento = backStackEntry.arguments?.getString("esIncremento")?.toBoolean() ?: true
                            Screens.GestionMasivaScreen(navController, esIncremento)
                        }
                    }
                    composable("recuperar_contraseña") {
                        Screens.RecuperarContrasenaScreen(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun Encabezado() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 30.dp), // Ajusta según sea necesario
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 0.dp),
                tint = Color.Black
            )


            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Cuenta",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 0.dp),
                tint = Color.Gray
            )
        }


        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Inicio(){
    Column (modifier = Modifier.padding(70.dp,100.dp,70.dp,500.dp)) {
        Mytext("Bienvenid@",
            Color.Black,
            MaterialTheme.typography.displayMedium)

    }
}

@Composable
fun Final() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp, 700.dp, 25.dp, 50.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(50.dp))
            .background(Color.White)
            .padding(vertical = 16.dp, horizontal = 32.dp)
    ) {
        Text(
            text = "AGREGAGAR\nPRODUCTO",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Produc() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF8AA1A))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Imagen producto",
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(20.dp))


        Text(
            text = "Nombre del\nproducto",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Flecha",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun Desplegable() {
    Column(
        modifier = Modifier
            .padding(29.dp, 190.dp, 29.dp, 170.dp)
            .verticalScroll(rememberScrollState())
    ) {
        repeat(15) {
            Produc()
        }
    }
}
@Composable
fun Mytext (text: String,color: Color,style: TextStyle){
    Text(text, color = color, style = style)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser
    val context = LocalContext.current
    if (currentUser == null) {
        // Si no está autenticado, mostrar mensaje y redirigir
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para acceder al inventario", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }
    var menuVisible by remember { mutableStateOf(false) }
    var productos by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) } // (id, nombre)
    var productoFiltro by remember { mutableStateOf("") }
    var productoExpanded by remember { mutableStateOf(false) }
    var sucursalFiltro by remember { mutableStateOf("") }
    var sucursalExpanded by remember { mutableStateOf(false) }
    var sucursalFiltroId by remember { mutableStateOf("") }
    // Escuchar productos y sucursales en tiempo real
    DisposableEffect(currentUser) {
        var productosListener: ListenerRegistration? = null
        var sucursalesListener: ListenerRegistration? = null
        if (currentUser != null) {
            productosListener = db.collection("productos").whereEqualTo("usuarioId", currentUser.uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        productos = snapshot.documents.map { it.data?.plus("id" to it.id) ?: emptyMap() }
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
            productosListener?.remove()
            sucursalesListener?.remove()
        }
    }
    val hayProductos = productos.isNotEmpty()
    val isLoggedIn = currentUser != null
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
        // Bienvenida
        Text(
            text = if (isLoggedIn) "Bienvenid@" else "Debes iniciar sesión",
            color = Color.Black,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        if (hayProductos) {
            // Menús desplegables de filtro en un Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = productoExpanded,
                    onExpandedChange = { productoExpanded = !productoExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = productoFiltro,
                        onValueChange = { productoFiltro = it },
                        label = { Text("Filtrar por producto", color = Color.Black, style = MaterialTheme.typography.bodySmall) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                        singleLine = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth().background(Color.White),
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
                        expanded = productoExpanded,
                        onDismissRequest = { productoExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        productos.mapNotNull { it["nombre"] as? String }.distinct().filter { it.contains(productoFiltro, true) }.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    productoFiltro = option
                                    productoExpanded = false
                                },
                                text = { Text(option, style = MaterialTheme.typography.bodySmall, color = Color.Black) }
                            )
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = sucursalExpanded,
                    onExpandedChange = { sucursalExpanded = !sucursalExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = sucursalFiltro,
                        onValueChange = { sucursalFiltro = it },
                        label = { Text("Filtrar por sucursal", color = Color.Black, style = MaterialTheme.typography.bodySmall) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                        singleLine = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth().background(Color.White),
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
            }
        }
        // Lista de productos filtrados
        if (hayProductos) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val productosFiltrados = productos.filter {
                    (productoFiltro.isBlank() || (it["nombre"] as? String)?.contains(productoFiltro, ignoreCase = true) == true) &&
                    (sucursalFiltroId.isBlank() || it["sucursalId"] == sucursalFiltroId)
                }
                productosFiltrados.forEach { producto ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF8AA1A))
                            .padding(8.dp)
                            .clickable { navController.navigate("detalle_producto/${producto["id"]}") },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            // Mostrar imagen si existe
                            val imagenUrl = producto["imagenUrl"] as? String
                            if (!imagenUrl.isNullOrBlank()) {
                                androidx.compose.foundation.Image(
                                    painter = rememberAsyncImagePainter(imagenUrl),
                                    contentDescription = "Imagen producto",
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.paisaje),
                                    contentDescription = "Imagen producto",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = producto["nombre"] as? String ?: "",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge
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
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Cantidad: ${producto["cantidad"] ?: "-"}",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, start = 64.dp, end = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                val tieneSucursales = sucursales.isNotEmpty()
                Button(
                    onClick = {
                        if (isLoggedIn) {
                            if (tieneSucursales) navController.navigate("add_product")
                            else navController.navigate("add_branch")
                        } else {
                            navController.navigate("login")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text(
                        text = if (isLoggedIn) (if (tieneSucursales) "AÑADIR PRODUCTO" else "CREAR SUCURSAL") else "INICIAR SESIÓN",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        // Botones inferiores
        if (hayProductos) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.navigate("gestion_masiva?esIncremento=false") },
                    modifier = Modifier
                        .height(48.dp)
                        .width(64.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Rojo
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "-",
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { navController.navigate("add_product") },
                    modifier = Modifier
                        .height(48.dp)
                        .width(180.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "AGREGAR PRODUCTO",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { navController.navigate("gestion_masiva?esIncremento=true") },
                    modifier = Modifier
                        .height(48.dp)
                        .width(64.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Verde pastel
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp)
                    )
                }
            }
        }
    }
    if (menuVisible) {
        Screens.HamburgerMenu(
            visible = true,
            onDismiss = { menuVisible = false },
            onInventarioClick = {
                navController.popBackStack("main", false)
            },
            onAddBranchClick = {
                navController.navigate("add_branch")
            },
            onGestionSucursalesClick = {
                navController.navigate("gestion_sucursales")
            },
            onAddStaffClick = {
                navController.navigate("add_staff")
            },
            onEditStaffClick = {
                navController.navigate("editar_staff")
            },
            onLogoutClick = {
                Firebase.auth.signOut()
                navController.navigate("login") {
                    popUpTo("main") { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun AddProductScreen(navController: NavController, onNavigateToMain: () -> Unit) {
    // Usa navController.navigate("login") o "crear_cuenta" según lo necesites
}

@Composable
fun LoginScreen(navController: NavController) {
    // Para ir a registro: navController.navigate("crear_cuenta")
}

@Composable
fun CreateAccountScreen(navController: NavController) {
    // Para ir a login: navController.navigate("login")
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Previewtxt(){
    Encabezado()
    Inicio()
    Desplegable()
    Final()
}