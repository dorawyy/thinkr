package com.example.thinkr.di

import com.example.thinkr.data.remote.client.HttpClientFactory
import com.example.thinkr.data.remote.auth.AuthApi
import com.example.thinkr.data.remote.auth.IAuthApi
import com.example.thinkr.data.remote.chat.ChatApi
import com.example.thinkr.data.remote.chat.IChatApi
import com.example.thinkr.data.remote.document.DocumentApi
import com.example.thinkr.data.remote.document.IDocumentApi
import com.example.thinkr.data.remote.study.IStudyApi
import com.example.thinkr.data.remote.study.StudyApi
import com.example.thinkr.data.remote.subscription.ISubscriptionApi
import com.example.thinkr.data.remote.subscription.SubscriptionApi
import com.example.thinkr.data.repositories.auth.AuthRepository
import com.example.thinkr.data.repositories.auth.IAuthRepository
import com.example.thinkr.data.repositories.chat.ChatRepository
import com.example.thinkr.data.repositories.chat.IChatRepository
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.doc.IDocRepository
import com.example.thinkr.data.repositories.flashcards.FlashcardsRepository
import com.example.thinkr.data.repositories.flashcards.IFlashcardsRepository
import com.example.thinkr.data.repositories.quiz.IQuizRepository
import com.example.thinkr.data.repositories.quiz.QuizRepository
import com.example.thinkr.data.repositories.subscription.ISubscriptionRepository
import com.example.thinkr.data.repositories.subscription.SubscriptionRepository
import com.example.thinkr.data.repositories.user.IUserRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.example.thinkr.ui.chat.ChatViewModel
import com.example.thinkr.ui.document_options.DocumentOptionsViewModel
import com.example.thinkr.ui.document_upload.DocumentUploadViewModel
import com.example.thinkr.ui.flashcards.FlashcardsViewModel
import com.example.thinkr.ui.home.HomeViewModel
import com.example.thinkr.ui.landing.LandingViewModel
import com.example.thinkr.ui.payment.PaymentViewModel
import com.example.thinkr.ui.profile.ProfileViewModel
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
    singleOf(::AuthApi).bind<IAuthApi>()
    singleOf(::ChatApi).bind<IChatApi>()
    singleOf(::DocumentApi).bind<IDocumentApi>()
    singleOf(::StudyApi).bind<IStudyApi>()
    singleOf(::SubscriptionApi).bind<ISubscriptionApi>()
    singleOf(::AuthRepository).bind<IAuthRepository>()
    singleOf(::DocRepository).bind<IDocRepository>()
    singleOf(::FlashcardsRepository).bind<IFlashcardsRepository>()
    singleOf(::UserRepository).bind<IUserRepository>()
    singleOf(::SubscriptionRepository).bind<ISubscriptionRepository>()
    singleOf(::ChatRepository).bind<IChatRepository>()
    singleOf(::QuizRepository).bind<IQuizRepository>()
    viewModelOf(::LandingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::PaymentViewModel)
    viewModelOf(::DocumentUploadViewModel)
    viewModelOf(::DocumentOptionsViewModel)
    viewModelOf(::FlashcardsViewModel)
    viewModelOf(::PaymentViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::ChatViewModel)
}
