package Screens

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WherehouseTheme {
                MenuScreen()
            }
        }
    }
}

@Composable
fun MenuScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton (onClick = { /* Acción de regreso */ }) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Regresar")
            }
            Spacer(modifier = Modifier.width(8.dp).padding(25.dp))
            Text(text = "Menú", style = MaterialTheme.typography.displaySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton(text = "Inventario")
            MenuButton(text = "Añadir producto")
            MenuButton(text = "Añadir almacén")
            MenuButton(text = "Cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp).padding(50.dp, 700.dp, 25.dp, 50.dp))
        Divider(color = Color.Black, thickness = 1.dp)

        Text(
            text = "Este menú se despliega del menú",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            style = MaterialTheme.typography.headlineMedium

            )
    }
}

@Composable
fun MenuButton(text: String) {
    Button(
        onClick = { /* Acción del botón */ },
        modifier = Modifier.fillMaxWidth().height(90.dp).padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8AA1A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text = text, color = Color.Black,style = MaterialTheme.typography.bodyLarge)
    }

}

@Preview
@Composable
fun ThirdScreen() {
    WherehouseTheme {
        MenuScreen()
    }
}