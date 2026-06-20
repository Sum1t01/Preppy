package com.sum1t.preppy.ir

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun RemoteScreen(
    vm: RemoteViewModel = koinViewModel()
) {

    Column(
        modifier = Modifier.padding(24.dp)
            .fillMaxSize()
            .clickable{
                vm.power()
            }
    ) {

        Button(
            onClick = { vm.power() }
        ) {
            Text("POWER")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { vm.volumeUp() }
        ) {
            Text("VOL +")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { vm.volumeDown() }
        ) {
            Text("VOL -")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { vm.mute() }
        ) {
            Text("MUTE")
        }
    }
}