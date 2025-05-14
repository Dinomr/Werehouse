package Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wherehouse.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import com.example.wherehouse.MainActivity
import androidx.activity.compose.setContent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var menuVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    // Redirección si ya está logueado
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val intent = Intent(context, com.example.wherehouse.MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            if (context is Activity) (context as Activity).finish()
        }
    }
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
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate("login") }
                )
            }
        }
        // Título centrado
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        // Espacio para imagen
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.iniciar_sesion),
                contentDescription = "Imagen de login",
                modifier = Modifier.size(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Tarjeta naranja con campos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF8AA1A), shape = RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo", color = Color.Black) },
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
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.Black) },
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
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                    Text(text = "Recordar sesión", fontSize = 12.sp, color = Color.Black)
                }
                Text(
                    text = "¿Olvidó su contraseña?",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.clickable {
                        navController.navigate("recuperar_contraseña")
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    // Navegar a la pantalla principal
                                    val intent = Intent(context, com.example.wherehouse.MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                    if (context is Activity) (context as Activity).finish()
                                } else {
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text(
                        text = "INICIAR SESIÓN",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿No tienes cuenta?\nCrear cuenta",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { navController.navigate("crear_cuenta") },
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
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

@Preview(showBackground = true)
@Composable
fun SixScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("crear_cuenta") {
            com.example.wherehouse.screens.CreateAccountScreen(
                navController = navController,
                onSuccess = { navController.navigate("success") }
            )
        }
        composable("success") {
            Screens.SuccessScreen(onInventoryClick = {
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        composable("recuperar_contraseña") { RecuperarContrasenaScreen(navController) }
    }
}

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            // Usuario ya autenticado, redirigir
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContent {
            val navController = rememberNavController()
            LoginScreen(navController)
        }
    }
}

// Nueva pantalla para recuperación de contraseña
@Composable
fun RecuperarContrasenaScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = Firebase.auth
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
                    modifier = Modifier.size(40.dp).clickable { /* menú */ },
                    tint = Color.White
                )
                Image(
                    painter = painterResource(id = com.example.wherehouse.R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate("login") }
                )
            }
        }
        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF8AA1A), shape = RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
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
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        isLoading = true
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Por favor ingresa tu correo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text(
                        text = "ENVIAR CORREO",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}