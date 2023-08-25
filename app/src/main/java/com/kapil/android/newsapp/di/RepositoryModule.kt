package com.kapil.android.newsapp.di

import com.kapil.android.newsapp.data.repository.RepositoryImpl
import com.kapil.android.newsapp.domain.repository.RepositoryInterface
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
    abstract fun bindRepositoryInterface(
        repositoryImpl: RepositoryImpl
    ):RepositoryInterface
}