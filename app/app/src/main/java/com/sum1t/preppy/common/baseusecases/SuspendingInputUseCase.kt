package com.sum1t.preppy.common.baseusecases

interface SuspendingInputUseCase<in inpuT> {
    suspend fun invoke(inpuT: inpuT)
}