package com.example.readmate_frontend.di

import com.example.readmate_frontend.data.api.BookcardsApi
import com.example.readmate_frontend.data.api.BooksApi
import com.example.readmate_frontend.data.api.CategoriesApi
import com.example.readmate_frontend.data.api.CommentsApi
import com.example.readmate_frontend.data.api.GroupsApi
import com.example.readmate_frontend.data.api.HomeApi
import com.example.readmate_frontend.data.api.NotificationsApi
import com.example.readmate_frontend.data.api.PostsApi
import com.example.readmate_frontend.data.api.RoundsApi
import com.example.readmate_frontend.data.api.UsersApi
import com.example.readmate_frontend.data.local.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->                                    // ← 추가
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${TokenStore.token}")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://52.78.248.76:8080")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUsersApi(retrofit: Retrofit): UsersApi =
        retrofit.create(UsersApi::class.java)

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi =
        retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideBookcardsApi(retrofit: Retrofit): BookcardsApi =
        retrofit.create(BookcardsApi::class.java)

    @Provides
    @Singleton
    fun provideBooksApi(retrofit: Retrofit): BooksApi =
        retrofit.create(BooksApi::class.java)

    @Provides
    @Singleton
    fun provideGroupsApi(retrofit: Retrofit): GroupsApi =
        retrofit.create(GroupsApi::class.java)

    @Provides
    @Singleton
    fun provideCategoriesApi(retrofit: Retrofit): CategoriesApi =
        retrofit.create(CategoriesApi::class.java)

    @Provides
    @Singleton
    fun provideRoundsApi(retrofit: Retrofit): RoundsApi =
        retrofit.create(RoundsApi::class.java)

    @Provides
    @Singleton
    fun providePostsApi(retrofit: Retrofit): PostsApi =
        retrofit.create(PostsApi::class.java)

    @Provides
    @Singleton
    fun provideCommentsApi(retrofit: Retrofit): CommentsApi =
        retrofit.create(CommentsApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationsApi(retrofit: Retrofit): NotificationsApi =
        retrofit.create(NotificationsApi::class.java)
}