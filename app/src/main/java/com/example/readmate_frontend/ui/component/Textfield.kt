package com.example.readmate_frontend.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupTextField(
    placeholderText: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
        placeholder = {
            if (placeholderText.isNotEmpty()) {
                Text(
                    text = placeholderText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF817052)
                )
            }
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .width(360.dp)
            .height(60.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFF817052),
            unfocusedTextColor = Color(0xFF817052),
            focusedContainerColor = Color(0xFFFFFCF6),
            unfocusedContainerColor = Color(0xFFFFFCF6),
            focusedPlaceholderColor = Color(0xFF817052),
            unfocusedPlaceholderColor = Color(0xFF817052),
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    placeholderText: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
        placeholder = {
            if (placeholderText.isNotEmpty()) {
                Text(
                    text = placeholderText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF817052)
                )
            }
        },
        modifier = Modifier
            .width(360.dp)
            .height(60.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFF817052),
            unfocusedTextColor = Color(0xFF817052),
            focusedContainerColor = Color(0xFFFFFCF6),
            unfocusedContainerColor = Color(0xFFFFFCF6),
            focusedPlaceholderColor = Color(0xFF817052),
            unfocusedPlaceholderColor = Color(0xFF817052),
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField2(
    placeholderText: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
        placeholder = {
            if (placeholderText.isNotEmpty()) {
                Text(
                    text = placeholderText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF817052)
                )
            }
        },
        modifier = Modifier
            .width(360.dp)
            .height(60.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFF817052),
            unfocusedTextColor = Color(0xFF817052),
            focusedContainerColor = Color(0xFFFFFCF6),
            unfocusedContainerColor = Color(0xFFFFFCF6),
            focusedPlaceholderColor = Color(0xFF817052),
            unfocusedPlaceholderColor = Color(0xFF817052),
        )
    )
}