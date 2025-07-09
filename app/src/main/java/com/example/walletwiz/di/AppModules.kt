package com.example.walletwiz.di

import androidx.work.WorkManager
import com.example.walletwiz.data.NotificationSettingsRepository
import com.example.walletwiz.data.database.AppDatabase
import com.example.walletwiz.data.repository.ExpenseCategoryRepositoryImpl
import com.example.walletwiz.data.repository.ExpenseRepositoryImpl
import com.example.walletwiz.data.repository.IExpenseCategoryRepository
import com.example.walletwiz.data.repository.IExpenseRepository
import com.example.walletwiz.data.repository.ITagRepository
import com.example.walletwiz.data.repository.TagRepositoryImpl
import com.example.walletwiz.viewmodels.ExpenseCategoryViewModel
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel
import com.example.walletwiz.viewmodels.ExpenseViewModel
import com.example.walletwiz.viewmodels.NotificationSettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.invoke(androidContext()) }
}

val repositoryModule = module {
    single<IExpenseRepository> { ExpenseRepositoryImpl(get<AppDatabase>().expenseDao()) }
    single<IExpenseCategoryRepository> { ExpenseCategoryRepositoryImpl(get<AppDatabase>().expenseCategoryDao()) }
    single<ITagRepository> { TagRepositoryImpl(get<AppDatabase>().tagDao()) }
    single { NotificationSettingsRepository(androidContext()) }
    single { WorkManager.getInstance(androidContext()) }
}

val viewModelModule = module {
    viewModelOf(::ExpenseOverviewViewModel)
    viewModelOf(::ExpenseViewModel)
    viewModelOf(::ExpenseCategoryViewModel)
    viewModelOf(::NotificationSettingsViewModel)
}

val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)
