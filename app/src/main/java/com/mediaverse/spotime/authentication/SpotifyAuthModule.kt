package com.mediaverse.spotime.authentication

import android.content.Context
import com.mediaverse.spotime.api.SpotifyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyAuthModule {
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context,
    ): TokenManager = TokenManager(context)

    @Provides
    @Singleton
    fun provideAuthService(
        @ApplicationContext context: Context,
    ): AuthorizationService = AuthorizationService(context)

    @Provides
    @Singleton
    fun provideSpotifyApi(tokenManager: TokenManager): SpotifyApi {
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(
                    object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original: Request = chain.request()
                            val requestBuilder = original.newBuilder()
                            tokenManager.getAccessToken()?.let {
                                requestBuilder.addHeader("Authorization", "Bearer $it")
                            }
                            val request = requestBuilder.build()
                            val response = chain.proceed(request)
                            if (response.code == 401) {
                                tokenManager.clear()
                            }
                            return response
                        }
                    },
                ).build()

        return Retrofit
            .Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApi::class.java)
    }
}
