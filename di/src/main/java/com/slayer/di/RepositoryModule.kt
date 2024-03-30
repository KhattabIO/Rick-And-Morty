package com.slayer.di

import com.slayer.data.repositories.AuthRepoImpl
import com.slayer.data.repositories.CharactersRepoImpl
import com.slayer.domain.repositories.repositories.AuthRepository
import com.slayer.domain.repositories.repositories.CharactersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCharactersRepo(
        repoImpl: CharactersRepoImpl
    ) : CharactersRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepo(authRepoImpl: AuthRepoImpl): AuthRepository
}