package com.sum1t.preppy.ir

import androidx.lifecycle.ViewModel

class RemoteViewModel(
    private val sendTvCommand: SendTvCommandUseCase
) : ViewModel() {

    fun power() {
        sendTvCommand(TvCommand.POWER)
    }

    fun volumeUp() {
        sendTvCommand(TvCommand.VOLUME_UP)
    }

    fun volumeDown() {
        sendTvCommand(TvCommand.VOLUME_DOWN)
    }

    fun mute() {
        sendTvCommand(TvCommand.MUTE)
    }
}