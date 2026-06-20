package com.sum1t.preppy.common.baseusecases

interface SuspendingUseCase<in inpuT, out outpuT> {
    suspend fun invoke(inpuT: inpuT): outpuT
}