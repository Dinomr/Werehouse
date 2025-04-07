package com.example.wherehouse.navigation

import Screens.SecondScreen
import Screens.ThirdScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wherehouse.S.AppScreens

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.SecondScreen.route) {
        composable(route = AppScreens.SecondScreen.route){
            SecondScreen()
        }
        composable(route = AppScreens.ThirdScreen.route){
            ThirdScreen()
        }
    }
}