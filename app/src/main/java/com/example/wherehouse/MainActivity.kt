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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WherehouseTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable("add_product") {
                        AddProductScreen(navController = navController, onNavigateToMain = {})
                    }
                    composable("login") {
                        Screens.LoginScreen(navController)
                    }
                    composable("crear_cuenta") {
                        com.example.wherehouse.screens.CreateAccountScreen(navController)
                    }
                    composable("detalle_producto/{productoId}") { backStackEntry ->
                        val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
                        com.example.wherehouse.ProductDetailScreen(navController, productoId)
                    }
                    composable("add_branch") {
                        com.example.wherehouse.screens.AddBranchScreen(navController)
                    }
                    composable("success") {
                        Screens.SuccessScreen(onInventoryClick = {
                            navController.navigate("main") {
                                popUpTo(0) { inclusive = true }
                            }
                        })
                    }
                    composable("add_staff") {
                        AddStaffScreen(navController)
                    }
                    composable("success_staff") {
                        SuccessStaffScreen(onInventoryClick = {
                            navController.navigate("main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }, navController = navController)
                    }
                    composable("success_sucursal") {
                        SuccessSucursalScreen(onInventoryClick = {
                            navController.navigate("main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }, navController = navController)
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
    var menuVisible by remember { mutableStateOf(false) }
    val productos = listOf(
        "Producto 1", "Producto 2", "Producto 3", "Producto 4",
        "Producto 5", "Producto 6", "Producto 7", "Producto 8", "Producto 9", "Producto 10",
        "Producto 11", "Producto 12", "Producto 13", "Producto 14"
    ) // Ahora hay 14 productos
    var productoFiltro by remember { mutableStateOf("") }
    var productoExpanded by remember { mutableStateOf(false) }
    var sucursalFiltro by remember { mutableStateOf("") }
    var sucursalExpanded by remember { mutableStateOf(false) }
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
            text = "Bienvenid@",
            color = Color.Black,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = productoExpanded,
                    onDismissRequest = { productoExpanded = false }
                ) {
                    val productosFiltro = listOf("Producto 1", "Producto 2", "Producto 3")
                    productosFiltro.filter { it.contains(productoFiltro, true) }.forEach { option ->
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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = sucursalExpanded,
                    onDismissRequest = { sucursalExpanded = false }
                ) {
                    val sucursalesFiltro = listOf("Sucursal 1", "Sucursal 2", "Sucursal 3")
                    sucursalesFiltro.filter { it.contains(sucursalFiltro, true) }.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                sucursalFiltro = option
                                sucursalExpanded = false
                            },
                            text = { Text(option, style = MaterialTheme.typography.bodySmall, color = Color.Black) }
                        )
                    }
                }
            }
        }
        // Lista de productos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val productosFiltrados = if (productoFiltro.isBlank()) productos else productos.filter { it.contains(productoFiltro, ignoreCase = true) }
            productosFiltrados.forEach { productoNombre ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8AA1A))
                        .padding(8.dp)
                        .clickable { navController.navigate("detalle_producto/$productoNombre") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.paisaje),
                            contentDescription = "Imagen producto",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = productoNombre,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Flecha",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }
            }
        }
        // Botón agregar producto
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { navController.navigate("add_product") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text(
                    text = "AGREGAR PRODUCTO",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    if (menuVisible) {
        Screens.HamburgerMenu(
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