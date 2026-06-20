package com.sum1t.preppy.common.baseusecases

interface OutputUseCase<out outpuT> {
    fun invoke(): outpuT
}