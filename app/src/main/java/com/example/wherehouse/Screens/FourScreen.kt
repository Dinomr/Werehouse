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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wherehouse.Encabezado
import com.example.wherehouse.ui.theme.WherehouseTheme
import com.example.wherehouse.R

class AddProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WherehouseTheme {
                AddProductScreen()
            }
        }
    }
}

@Composable
fun AddProductScreen() {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Encabezado()
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
        )
        Text(
            text = "Añadir\nproducto",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFF8AA1A), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Añadir imagen",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))

            )

            Spacer(modifier = Modifier.height(8.dp))

            val nombreProducto = remember { mutableStateOf("") }
            OutlinedTextField(
                value = nombreProducto.value,
                onValueChange = { nombreProducto.value = it },
                label = { Text("Nombre del\n producto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.White),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )


            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Descripción del\n producto") },
                modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color.White),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color.White),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Precio de\n compra") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White),
                    textStyle = TextStyle(textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Precio de\nventa") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White),
                    textStyle = TextStyle(textAlign = TextAlign.Center)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            DropdownMenuExample()
        }
    }
}

@Composable
fun DropdownMenuExample() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Seleccionar sucursal") }
    val options = listOf("Sucursal 1", "Sucursal 2", "Sucursal 3")

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true },colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
            Text(selectedOption,color = Color.Black )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    selectedOption = option
                    expanded = false
                }, text = { Text(selectedOption, color = Color.Black) })
            }
        }

    }
}

@Preview
@Composable
fun FourScreen() {
    WherehouseTheme {
        AddProductScreen()
    }
}