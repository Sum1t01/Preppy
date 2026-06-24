package com.sum1t.preppy.di

import android.util.Log
import com.sum1t.data.sqldelight.AppDatabase
import com.sum1t.preppy.common.utils.JsonHelper
import com.sum1t.preppy.data.ApiService
import com.sum1t.preppy.data.repository.grade.GradesRepositoryImpl
import com.sum1t.preppy.data.usecase.grade.FetchGradesUseCaseImpl
import com.sum1t.preppy.domain.repository.grade.GradesRepository
import com.sum1t.preppy.domain.usecase.grade.FetchGradesUseCase
import com.sum1t.preppy.presentation.screens.levelSelection.LevelSelectionViewModel
import com.moczul.ok2curl.CurlInterceptor
import com.sum1t.preppy.BuildConfig
import com.sum1t.preppy.data.NotificationSchedulerImpl
import com.sum1t.preppy.data.datastore.UserPreferencesDataStoreImpl
import com.sum1t.preppy.data.repository.subject.SubjectRepositoryImpl
import com.sum1t.preppy.data.usecase.subject.FetchSubjectsByGradeUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.GetDarkModeUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.GetOnboardingStatusUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.SetDarkModeUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.SetOnboardingStatusUseCaseImpl
import com.sum1t.preppy.domain.repository.subject.SubjectRepository
import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.subject.FetchSubjectsByGradeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetOnboardingStatusUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetOnboardingStatusUseCase
import com.sum1t.preppy.presentation.screens.home.HomeViewModel
import com.sum1t.preppy.presentation.screens.onboarding.OnboardingViewModel
import com.sum1t.preppy.presentation.screens.subjectSelection.SubjectSelectionViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import com.sum1t.preppy.data.db.DatabaseFactory
import com.sum1t.preppy.data.repository.question.QuestionRepositoryImpl
import com.sum1t.preppy.data.usecase.SyncNotificationStateUseCaseImpl
import com.sum1t.preppy.data.usecase.grade.SaveGradesUseCaseImpl
import com.sum1t.preppy.data.usecase.question.FetchQuestionsByTopicIdsUseCaseImpl
import com.sum1t.preppy.data.usecase.question.GetAttemptedQuestionsCountUseCaseImpl
import com.sum1t.preppy.data.usecase.question.GetQuestionsForQuizUseCaseImpl
import com.sum1t.preppy.data.usecase.question.GetTotalQuestionsCountUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.GetHapticsEnabledUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.GetLevelSubjectSelectedUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.GetNotificationEnabledUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.GetThemePaletteUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.IsUserLoginEnabledUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.SetHapticsEnabledUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.SetLevelSubjectSelectedUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.SetNotificationEnabledUseCaseImpl
import com.sum1t.preppy.data.usecase.userpreferences.SetThemePaletteUseCaseImpl
import com.sum1t.preppy.domain.repository.notification.NotificationScheduler
import com.sum1t.preppy.domain.repository.question.QuestionRepository
import com.sum1t.preppy.domain.usecase.ScheduleNotificationsUseCase
import com.sum1t.preppy.domain.usecase.SyncNotificationStateUseCase
import com.sum1t.preppy.domain.usecase.grade.SaveGradesUseCase
import com.sum1t.preppy.domain.usecase.question.FetchQuestionsByTopicIdsUseCase
import com.sum1t.preppy.domain.usecase.question.GetAttemptedQuestionsCountUseCase
import com.sum1t.preppy.domain.usecase.question.GetQuestionsForQuizUseCase
import com.sum1t.preppy.domain.usecase.question.GetTotalQuestionsCountUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetLevelSubjectSelectedUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetNotificationEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetThemePaletteUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.IsUserLoginEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetLevelSubjectSelectedUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetNotificationEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetThemePaletteUseCase
import com.sum1t.preppy.ir.irModule
import com.sum1t.preppy.presentation.screens.flashcard.FlashCardViewModel
import com.sum1t.preppy.presentation.screens.profile.ProfileViewModel
import com.sum1t.preppy.presentation.screens.quiz.QuizViewModel
import com.sum1t.preppy.presentation.screens.setting.SettingsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.scope.get
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModules by lazy {
    listOf(
        localStoreModule,
        coroutineModule,
        databaseModule,
        contextModule,
        viewModelModule,
        networkModule,
        repoModule,
        useCaseModule,
        workerModule,

        irModule
    )
}

val viewModelModule: Module = module {
    factory { LevelSelectionViewModel(get(), get(), get(), get(), get()) }
    factory { SubjectSelectionViewModel(get(), get(), get(), get()) }
    factory { HomeViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get(), get(), get(), get(), get(),get()) }
    factory { OnboardingViewModel(get(), get(), get(), get()) }
    factory { SettingsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { FlashCardViewModel(get(), get()) }
    factory { QuizViewModel(get(), get()) }
}

val workerModule: Module = module {
    // Scheduler
    single<NotificationScheduler> {
        NotificationSchedulerImpl(get())
    }
}

val localStoreModule: Module = module {
    single<UserPreferencesDataStore> { UserPreferencesDataStoreImpl(androidContext()) }
}

val databaseModule: Module = module {
    single { DatabaseFactory.create(androidContext()) }
    single { get<AppDatabase>().appDatabaseQueries }
}

val contextModule: Module = module {
//    factory { androidContext() }
}

val networkModule: Module = module {
    single { provideKtorHttpClient() }
    single { ApiService(get()) }

}

val repoModule: Module = module {

    factory<GradesRepository> { GradesRepositoryImpl(get(), get()) }
    factory<SubjectRepository> { SubjectRepositoryImpl(get()) }
    factory<QuestionRepository> { QuestionRepositoryImpl(get(), get(), get()) }
}

val coroutineModule: Module = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
}


val useCaseModule: Module = module {
    factory<FetchGradesUseCase> { FetchGradesUseCaseImpl(get(), get()) }
    factory<FetchSubjectsByGradeUseCase> { FetchSubjectsByGradeUseCaseImpl(get()) }
    factory<GetDarkModeUseCase> { GetDarkModeUseCaseImpl(get()) }
    factory<SetDarkModeUseCase> { SetDarkModeUseCaseImpl(get()) }
    factory<GetOnboardingStatusUseCase> { GetOnboardingStatusUseCaseImpl(get()) }
    factory<SetOnboardingStatusUseCase> { SetOnboardingStatusUseCaseImpl(get()) }
    factory<SetLevelSubjectSelectedUseCase> { SetLevelSubjectSelectedUseCaseImpl(get()) }
    factory<GetLevelSubjectSelectedUseCase> { GetLevelSubjectSelectedUseCaseImpl(get()) }
    factory<GetThemePaletteUseCase> { GetThemePaletteUseCaseImpl(get()) }
    factory<SetThemePaletteUseCase> { SetThemePaletteUseCaseImpl(get()) }
    factory<GetHapticsEnabledUseCase> { GetHapticsEnabledUseCaseImpl(get()) }
    factory<SetHapticsEnabledUseCase> { SetHapticsEnabledUseCaseImpl(get()) }
    factory<GetNotificationEnabledUseCase> { GetNotificationEnabledUseCaseImpl(get()) }
    factory<SetNotificationEnabledUseCase> { SetNotificationEnabledUseCaseImpl(get()) }
    factory<FetchQuestionsByTopicIdsUseCase> { FetchQuestionsByTopicIdsUseCaseImpl(get()) }
    factory<GetTotalQuestionsCountUseCase> { GetTotalQuestionsCountUseCaseImpl(get()) }
    factory<GetAttemptedQuestionsCountUseCase> { GetAttemptedQuestionsCountUseCaseImpl(get()) }
    factory<SaveGradesUseCase> { SaveGradesUseCaseImpl(get()) }
    factory<GetQuestionsForQuizUseCase> { GetQuestionsForQuizUseCaseImpl(get()) }
    factory<IsUserLoginEnabledUseCase> { IsUserLoginEnabledUseCaseImpl(get()) }

    factory {
        ScheduleNotificationsUseCase(get())
    }

    single<SyncNotificationStateUseCase> { SyncNotificationStateUseCaseImpl(get(), get(), get()) }
}


fun provideKtorHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)

                if (BuildConfig.DEBUG) {
                    addInterceptor(CurlInterceptor { message ->
                        Log.d("NetworkLog", message)
                    })
                }
            }
        }

        install(ContentNegotiation) {
            json(JsonHelper.json)
        }

        install(HttpCookies) {
            // This will store cookies in memory
            storage = AcceptAllCookiesStorage()
        }

        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

        defaultRequest {
            url(BuildConfig.BASE_URL)
        }

    }
}


