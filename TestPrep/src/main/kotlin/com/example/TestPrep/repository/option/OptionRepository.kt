package com.example.TestPrep.repository.option

import com.example.TestPrep.model.option.Option
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OptionRepository: JpaRepository<Option, Long> {
}