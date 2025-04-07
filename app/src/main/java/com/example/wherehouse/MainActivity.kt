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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WherehouseTheme () {
                Previewtxt()

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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Previewtxt(){
    Encabezado()
    Inicio()
    Desplegable()
    Final()
}