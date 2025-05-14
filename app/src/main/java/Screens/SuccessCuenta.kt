package Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wherehouse.Encabezado
import com.example.wherehouse.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun SuccessScreen(onInventoryClick: () -> Unit, navController: NavController? = null) {
    var menuVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Se ha creado la cuenta con éxito",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(50.dp,50.dp,50.dp,50.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.creacion_exito),
                contentDescription = "Cuenta creada",
                modifier = Modifier
                    .size(250.dp)
                    .padding(vertical = 10.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onInventoryClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8AA1A)),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(20.dp, 100.dp, 20.dp, 100.dp)
                    .height(50.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "VAMOS A TU INVENTARIO",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
    if (menuVisible && navController != null) {
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
            },
            onEditStaffClick = {
                menuVisible = false
                navController.navigate("editar_staff")
            },
            onLogoutClick = {
                menuVisible = false
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo("main") { inclusive = true }
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SecondScreen() {
    val navController = androidx.navigation.compose.rememberNavController()
    Encabezado()
    SuccessScreen(onInventoryClick = {}, navController = navController)
}
