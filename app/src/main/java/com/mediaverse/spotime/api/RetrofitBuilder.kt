package com.mediaverse.spotime.api

import com.mediaverse.spotime.authentication.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    fun provideSpotifyApi(
        tokenManager: TokenManager,
        onUnauthorized: () -> Unit,
    ): SpotifyApi {
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(
                    Interceptor { chain ->
                        val original = chain.request()
                        val requestBuilder = original.newBuilder()
                        tokenManager.getAccessToken()?.let {
                            requestBuilder.addHeader("Authorization", "Bearer $it")
                        }
                        val request = requestBuilder.build()
                        val response = chain.proceed(request)

                        if (response.code == 401) {
                            onUnauthorized()
                        }

                        response
                    },
                ).build()

        return Retrofit
            .Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(SpotifyApi::class.java)
    }
}
