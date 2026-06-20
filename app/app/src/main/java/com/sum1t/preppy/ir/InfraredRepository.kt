package com.sum1t.preppy.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

interface InfraredRepository {
    fun send(command: TvCommand)
    fun hasIrEmitter(): Boolean
}

class SendTvCommandUseCase(
    private val repository: InfraredRepository
) {
    operator fun invoke(command: TvCommand) {
        repository.send(command)
    }
}

class InfraredRepositoryImpl(
    private val irManager: ConsumerIrManager
) : InfraredRepository {

    override fun hasIrEmitter(): Boolean {
        return irManager.hasIrEmitter()
    }

    override fun send(command: TvCommand) {

        if (!hasIrEmitter()) {
            throw IllegalStateException("No IR emitter")
        }

        val pattern = when (command) {

            TvCommand.POWER -> WhirlpoolAcSweep.next()

            TvCommand.VOLUME_UP,
            TvCommand.VOLUME_DOWN,
            TvCommand.MUTE -> return  // AC: ignore TV-only commands

            else -> throw IllegalArgumentException("Unknown command: $command")
        }

        repeat(5) {
            irManager.transmit(WhirlpoolAcSweep.FREQUENCY, pattern)
            Thread.sleep(120)
        }
    }
}

object WhirlpoolAcSweep {

    const val FREQUENCY = 38000

    private var index = 0

    // Base template (VERY generic AC-like frame structure)
    private val base = intArrayOf(
        9000, 4500,
        560, 560,
        560, 1690,
        560, 560,
        560, 1690,
        560, 560,
        560, 560,
        560, 1690,
        560, 560
    )

    /**
     * Generates variations to try to hit real Whirlpool protocol.
     */
    fun next(): IntArray {

        val variant = base.copyOf()

        when (index % 6) {

            0 -> tweak(variant, 4500)
            1 -> tweak(variant, 4300)
            2 -> tweak(variant, 5000)

            3 -> stretch(variant, 1.1)
            4 -> stretch(variant, 0.9)

            5 -> noise(variant)
        }

        index++
        return variant
    }

    private fun tweak(arr: IntArray, headerSpace: Int) {
        arr[1] = headerSpace
    }

    private fun stretch(arr: IntArray, factor: Double) {
        for (i in arr.indices) {
            arr[i] = (arr[i] * factor).toInt()
        }
    }

    private fun noise(arr: IntArray) {
        for (i in 2 until arr.size) {
            arr[i] += if (i % 2 == 0) 20 else -20
        }
    }
}

object WhirlpoolMagicoolAcIr {

    // Most ACs use 36–38kHz; Whirlpool commonly works around 38kHz
    const val FREQUENCY = 38000

    /**
     * ⚠️ THIS IS A PLACEHOLDER FRAME.
     * Replace with a real captured IR RAW dump from your AC remote.
     */
    fun power(): IntArray {
        return intArrayOf(

            // Header
            9000, 4500,

            // Example data (NOT real Whirlpool frame)
            560, 560,
            560, 560,
            560, 1690,
            560, 560,
            560, 1690,
            560, 560,

            // Footer gap (optional depending on protocol)
            560, 20000
        )
    }
}

val irModule = module {

    single<ConsumerIrManager> {

        androidContext().getSystemService(
            Context.CONSUMER_IR_SERVICE
        ) as ConsumerIrManager
    }

//    single<ConsumerIrManager?> {
//
//        androidContext().getSystemService(
//            Context.CONSUMER_IR_SERVICE
//        ) as? ConsumerIrManager
//    }

    single<InfraredRepository> {
        InfraredRepositoryImpl(get())
    }

    factory {
        SendTvCommandUseCase(get())
    }

    viewModel {
        RemoteViewModel(get())
    }
}