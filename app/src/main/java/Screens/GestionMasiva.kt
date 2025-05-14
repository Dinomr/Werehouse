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

@Composable
fun GestionMasivaScreen(navController: NavController, esIncremento: Boolean) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser
    val context = LocalContext.current
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Debes iniciar sesión para modificar productos", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
        return
    }
    var productos by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var cantidades by remember { mutableStateOf(mapOf<String, String>()) }
    var isLoading by remember { mutableStateOf(false) }
    // Cargar productos
    LaunchedEffect(currentUser) {
        db.collection("productos").whereEqualTo("usuarioId", currentUser.uid)
            .get().addOnSuccessListener { result ->
                productos = result.documents.map { it.data?.plus("id" to it.id) ?: emptyMap() }
                cantidades = productos.associate { it["id"].toString() to (it["cantidad"]?.toString() ?: "0") }
            }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = if (esIncremento) "Incrementar cantidades" else "Decrementar cantidades",
            style = MaterialTheme.typography.displaySmall,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        productos.forEach { producto ->
            val id = producto["id"].toString()
            val nombre = producto["nombre"] as? String ?: ""
            val cantidadActual = producto["cantidad"]?.toString() ?: "0"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8AA1A), shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(nombre, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                OutlinedTextField(
                    value = cantidades[id] ?: cantidadActual,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            cantidades = cantidades.toMutableMap().apply { put(id, value) }
                        }
                    },
                    label = { Text("Cantidad", color = Color.Black) },
                    singleLine = true,
                    modifier = Modifier.width(90.dp).background(Color.White, RoundedCornerShape(8.dp)),
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
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                isLoading = true
                var error = false
                productos.forEach { producto ->
                    val id = producto["id"].toString()
                    val cantidadOriginal = (producto["cantidad"] as? Number)?.toInt() ?: 0
                    val cantidadNueva = (cantidades[id]?.toIntOrNull() ?: cantidadOriginal)
                    val resultado = if (esIncremento) cantidadOriginal + cantidadNueva else cantidadOriginal - cantidadNueva
                    if (resultado < 0) error = true
                }
                if (error) {
                    isLoading = false
                    Toast.makeText(context, "No puedes dejar cantidades negativas", Toast.LENGTH_LONG).show()
                    return@Button
                }
                // Actualizar cantidades
                productos.forEach { producto ->
                    val id = producto["id"].toString()
                    val cantidadOriginal = (producto["cantidad"] as? Number)?.toInt() ?: 0
                    val cantidadNueva = (cantidades[id]?.toIntOrNull() ?: cantidadOriginal)
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
    }
}
