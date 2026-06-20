package com.sum1t.preppy.common.baseusecases

interface SuspendingOutputUseCase<out outpuT> {
    suspend fun invoke(): outpuT
}