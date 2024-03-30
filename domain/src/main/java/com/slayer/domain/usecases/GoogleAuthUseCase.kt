package com.slayer.domain.usecases

import com.slayer.domain.repositories.repositories.AuthRepository
import javax.inject.Inject

class GoogleAuthUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(token : String) = authRepository.loginWithGoogle(token)
}