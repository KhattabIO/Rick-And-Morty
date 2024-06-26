package com.slayer.domain.usecases.auth

import com.slayer.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = authRepository.login(email, password)
}