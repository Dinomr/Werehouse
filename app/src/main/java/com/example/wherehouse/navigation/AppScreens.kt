package com.example.wherehouse.S

sealed class AppScreens (val route: String)
{
    object FirstScreen: AppScreens("first_screen")
    object SecondScreen: AppScreens ("second_screen")
    object ThirdScreen: AppScreens ("ThirdScreen")
    object FourScreen: AppScreens ("FourScreen")
    object FiveScreen: AppScreens ("FiveScreen")
}