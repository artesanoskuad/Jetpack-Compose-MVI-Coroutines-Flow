package com.hoc.flowmvi.data

import com.hoc.flowmvi.core.Mapper
import com.hoc.flowmvi.data.mapper.UserDomainToUserBodyMapper
import com.hoc.flowmvi.data.mapper.UserDomainToUserResponseMapper
import com.hoc.flowmvi.data.mapper.UserResponseToUserDomainMapper
import com.hoc.flowmvi.data.remote.UserApiService
import com.hoc.flowmvi.data.remote.UserBody
import com.hoc.flowmvi.data.remote.UserResponse
import com.hoc.flowmvi.domain.entity.User
import com.hoc.flowmvi.domain.repository.UserRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

@Retention(AnnotationRetention.BINARY)
@Qualifier
private annotation class BaseUrl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
  @ExperimentalTime
  @Binds
  @Singleton
  abstract fun userRepository(impl: UserRepositoryImpl): UserRepository

  @Binds
  abstract fun userResponseToUserMapper(impl: UserResponseToUserDomainMapper): Mapper<UserResponse, User>

  @Binds
  abstract fun userDomainToUserBodyMapper(impl: UserDomainToUserBodyMapper): Mapper<User, UserBody>

  @Binds
  abstract fun userDomainToUserResponseMapper(impl: UserDomainToUserResponseMapper): Mapper<User, UserResponse>

  internal companion object {
    @Provides
    @Singleton
    fun userApiService(retrofit: Retrofit): UserApiService = UserApiService(retrofit = retrofit)

    @Provides
    @Singleton
    fun retrofit(
      @BaseUrl baseUrl: String,
      moshi: Moshi,
      client: OkHttpClient,
    ): Retrofit =
      provideRetrofit(
        baseUrl = baseUrl,
        moshi = moshi,
        client = client,
      )

    @Provides
    @Singleton
    fun moshi(): Moshi = provideMoshi()

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient = provideOkHttpClient()

    @Provides
    @BaseUrl
    fun baseUrl(): String = "https://mvi-coroutines-flow-server.herokuapp.com/"
  }
}

private fun provideMoshi(): Moshi {
  return Moshi
    .Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
}

private fun provideRetrofit(baseUrl: String, moshi: Moshi, client: OkHttpClient): Retrofit {
  return Retrofit.Builder()
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(baseUrl)
    .build()
}

private fun provideOkHttpClient(): OkHttpClient {
  return OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .addInterceptor(
      HttpLoggingInterceptor()
        .apply { level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE }
    )
    .build()
}
