package com.sum1t.preppy.common.baseusecases

interface InputUseCase<in inpuT> {
    fun invoke(inpuT: inpuT)
}