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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wherehouse.R
import com.example.wherehouse.ui.theme.WherehouseTheme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.BorderStroke
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.storage.ktx.storage

@Composable
fun AddStaffScreen(navController: NavController) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser
    val context = LocalContext.current
    var personName by remember { mutableStateOf("") }
    var staffId by remember { mutableStateOf("") }
    var store by remember { mutableStateOf("") }
    var menuVisible by remember { mutableStateOf(false) }
    var sucursalId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    // Verificar autenticación
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para añadir staff", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }

    // Cargar sucursales al inicio
    LaunchedEffect(currentUser) {
        try {
            isLoading = true
            db.collection("sucursales")
                .whereEqualTo("usuarioId", currentUser.uid)
                .get()
                .addOnSuccessListener { result ->
                    sucursales = result.documents.mapNotNull { doc ->
                        val nombre = doc.getString("nombre")
                        if (nombre != null) doc.id to nombre else null
                    }
                    isDataLoaded = true
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al cargar sucursales: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            isLoading = false
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Text(
                text = "Añadir STAFF",
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
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Imagen staff",
                            tint = Color.Gray,
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
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
                    label = { Text("ID *", color = Color.Black) },
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
                    value = store,
                    onValueChange = { store = it },
                    label = { Text("Tienda a cargo", color = Color.Black) },
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

                // Dropdown de sucursales
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
                        sucursales.forEach { (id, nombre) ->
                            DropdownMenuItem(
                                onClick = {
                                    sucursalId = id
                                    sucursalExpanded = false
                                },
                                text = { Text(nombre, color = Color.Black) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (personName.isBlank() || staffId.isBlank() || sucursalId.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        val staff = hashMapOf(
                            "nombre" to personName,
                            "staffId" to staffId,
                            "sucursalId" to sucursalId,
                            "usuarioId" to currentUser.uid,
                            "store" to store
                        )
                        db.collection("staff").add(staff)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Staff añadido exitosamente", Toast.LENGTH_SHORT).show()
                                navController.navigate("success_staff")
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error al añadir staff: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Text(
                            text = "AÑADIR STAFF",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "¿NO TIENE SUCURSAL?",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("add_branch") }
            )
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

@Composable
fun SucursalDropdown(db: com.google.firebase.firestore.FirebaseFirestore, currentUser: com.google.firebase.auth.FirebaseUser?, onSucursalSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("¿A qué sucursal pertenece?") }
    var sucursales by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            try {
                db.collection("sucursales")
                    .whereEqualTo("usuarioId", currentUser.uid)
                    .get()
                    .addOnSuccessListener { result ->
                        sucursales = result.documents.map { it.id to (it.getString("nombre") ?: "") }
                        isLoading = false
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                    }
            } catch (e: Exception) {
                isLoading = false
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
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black
                )
            } else {
                Text(selectedOption, color = Color.Black, modifier = Modifier.fillMaxWidth())
            }
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

@Preview(showBackground = true)
@Composable
fun StaffScreenPreview() {
    val navController = rememberNavController()
    AddStaffScreen(navController)
}

