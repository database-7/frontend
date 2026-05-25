package com.example.readmate_frontend.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readmate_frontend.R

@Composable
fun Logo2(FontSize : TextUnit, FontSize2 : TextUnit){

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "ReadMate",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF817052),
            fontSize = FontSize
        )
        Text(
            "책을 읽는 또 하나의 방법",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB79E73),
            fontSize = FontSize2
        )
    }
}

@Composable
fun Logo( onAlarmClick:() -> Unit = {} ){
    Spacer(modifier = Modifier.height(84.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "ReadMate",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF817052),
            fontSize = 32.sp
        )
        Image(
            painter = painterResource(id = R.drawable.ic_bell),
            contentDescription = "알림",
            modifier = Modifier
                .size(32.dp)
                .clickable{ onAlarmClick() }
        )
    }
    Spacer(modifier = Modifier.height(48.dp))

}

@Composable
fun Logo3( onAlarmClick:() -> Unit = {} ){
    Spacer(modifier = Modifier.height(84.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "ReadMate",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF817052),
            fontSize = 32.sp
        )
        Image(
            painter = painterResource(id = R.drawable.ic_bell),
            contentDescription = "알림",
            modifier = Modifier
                .size(32.dp)
                .clickable{ onAlarmClick() }
        )
    }
}