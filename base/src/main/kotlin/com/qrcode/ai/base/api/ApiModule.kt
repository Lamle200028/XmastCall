package com.qrcode.ai.base.api

import android.content.Context
import android.util.Log
import com.bkplus.hitranslator.base.BuildConfig
import com.bkplus.hitranslator.base.R
import com.qrcode.ai.base.api.translate.RingtonesApi
import com.qrcode.ai.base.injection.BaseSourceApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    companion object {
        const val CACHE_SIZE: Long = 10 * 1024 * 1024
        const val CACHE_TIME_SEC = 30
        const val TIME_OUT: Long = 60
    }

    private val cacheInterceptor: Interceptor
        get() = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val response = chain.proceed(chain.request())
                val cacheControl =
                    CacheControl.Builder().maxAge(CACHE_TIME_SEC, TimeUnit.SECONDS).build()

                response.newBuilder().header("Cache-Control", cacheControl.toString()).build()

                return response
            }
        }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.i("OkHttp", message)
            }
        })
        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return interceptor
    }

    @Provides
    @Singleton
    fun provideHeadersInterceptor(): Interceptor {
        return Interceptor { chain ->
            val initialRequest = chain.request()
            val request =
                initialRequest.newBuilder().addHeader("Accept", "application/json").build()

            return@Interceptor chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    @BaseSourceApi("Main")
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        headersInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http-cache"), CACHE_SIZE)
        return OkHttpClient.Builder().addInterceptor(headersInterceptor)
            .addInterceptor(loggingInterceptor).addInterceptor(cacheInterceptor).cache(cache)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    @Singleton
    @BaseSourceApi("Mock")
    fun provideMockRestAdapter(
        @ApplicationContext context: Context,
        @BaseSourceApi("Mock") okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder().client(okHttpClient)
            .baseUrl(context.getString(R.string.api_end_point))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    }

    @Provides
    @Singleton
    @BaseSourceApi("Main")
    fun provideRestAdapter(
        @ApplicationContext context: Context,
        @BaseSourceApi("Main") okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder().client(okHttpClient)
            .baseUrl("https://api.dev.bkplus.tech/ringtone/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideRingtonesApi(@BaseSourceApi("Main") restAdapter: Retrofit): RingtonesApi {
        return restAdapter.create(RingtonesApi::class.java)
    }
}
