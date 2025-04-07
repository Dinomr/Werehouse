package Screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wherehouse.R
import com.example.wherehouse.ui.theme.WherehouseTheme

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome text
        Text(
            text = "Bienvenido a tu\nsistema de inventario",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF002366),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Cajas",
            modifier = Modifier
                .height(150.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Login form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF8AA1A))
                .padding(16.dp)
        ) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var rememberMe by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = rememberMe, onCheckedChange = { rememberMe = it })
                    Text(text = "Recordar sesión")
                }
                Text(text = "¿Olvidó su contraseña?", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* Acción de iniciar sesión */ },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "INICIAR SESIÓN", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿No tienes cuenta? Crear cuenta",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SixScreen() {
    WherehouseTheme {
        LoginScreen()
    }
}