package com.pedro.schwarz.goalstracker.di.modules

import androidx.room.Room
import com.pedro.schwarz.goalstracker.database.AppDatabase
import com.pedro.schwarz.goalstracker.database.dao.GoalDAO
import com.pedro.schwarz.goalstracker.repository.AuthRepository
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DATABASE_NAME = "goals_tracker.db"

val databaseModules = module {
    single { Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME).build() }
}

val daoModules = module {
    single<GoalDAO> { get<AppDatabase>().getGoalDAO() }
    single<AuthRepository> { AuthRepository() }
}

val uiModules = module { }

val viewModelModules = module {
    viewModel<AuthViewModel> { AuthViewModel(get()) }
}