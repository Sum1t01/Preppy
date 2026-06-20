package com.sum1t.preppy.common.baseusecases

interface BaseUseCase<in inpuT, out outpuT> {
    fun invoke(inpuT: inpuT): outpuT
}