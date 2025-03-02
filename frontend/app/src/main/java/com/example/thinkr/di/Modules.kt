package com.example.thinkr.di

import com.example.thinkr.data.remote.HttpClientFactory
import com.example.thinkr.data.remote.IRemoteApi
import com.example.thinkr.data.remote.RemoteApi
import com.example.thinkr.data.repositories.auth.IAuthRepository
import com.example.thinkr.data.repositories.auth.AuthRepository
import com.example.thinkr.data.repositories.doc.IDocRepository
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.flashcards.IFlashcardsRepository
import com.example.thinkr.data.repositories.flashcards.FlashcardsRepository
import com.example.thinkr.data.repositories.user.IUserRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.example.thinkr.ui.home.HomeScreenViewModel
import com.example.thinkr.ui.landing.LandingScreenViewModel
import com.example.thinkr.ui.payment.PaymentViewModel
import com.example.thinkr.ui.profile.ProfileViewModel
import com.example.thinkr.ui.document_upload.DocumentUploadViewModel
import com.example.thinkr.ui.document_options.DocumentOptionsViewModel
import com.example.thinkr.ui.flashcards.FlashcardsViewModel
import com.example.thinkr.ui.chat.ChatViewModel
import com.example.thinkr.ui.quiz.QuizViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single { HttpClientFactory.create(get()) }
    singleOf(::RemoteApi).bind<IRemoteApi>()
    singleOf(::AuthRepository).bind<IAuthRepository>()
    singleOf(::DocRepository).bind<IDocRepository>()
    singleOf(::FlashcardsRepository).bind<IFlashcardsRepository>()
    singleOf(::UserRepository).bind<IUserRepository>()
    viewModelOf(::LandingScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::PaymentViewModel)
    viewModelOf(::DocumentUploadViewModel)
    viewModelOf(::DocumentOptionsViewModel)
    viewModelOf(::FlashcardsViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::ChatViewModel)
}
