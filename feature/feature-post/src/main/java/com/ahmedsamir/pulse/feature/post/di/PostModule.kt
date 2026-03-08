package com.ahmedsamir.pulse.feature.post.di

import com.ahmedsamir.pulse.feature.post.data.repository.PostRepositoryImpl
import com.ahmedsamir.pulse.feature.post.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository
}