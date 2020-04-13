package com.pedro.schwarz.goalstracker.di.modules

import androidx.room.Room
import com.pedro.schwarz.goalstracker.database.AppDatabase
import com.pedro.schwarz.goalstracker.database.dao.GoalDAO
import com.pedro.schwarz.goalstracker.database.dao.MilestoneDAO
import com.pedro.schwarz.goalstracker.repository.AuthRepository
import com.pedro.schwarz.goalstracker.repository.GoalRepository
import com.pedro.schwarz.goalstracker.repository.MilestoneRepository
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.CategoryAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.GoalAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.MilestoneAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.PriorityAdapter
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalDetailsViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalFormViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DATABASE_NAME = "goals_tracker.db"

val databaseModules = module {
    single { Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME).build() }
}

val daoModules = module {
    single<GoalDAO> { get<AppDatabase>().getGoalDAO() }
    single<MilestoneDAO> { get<AppDatabase>().getMilestoneDAO() }
    single<AuthRepository> { AuthRepository() }
    single<GoalRepository> { GoalRepository(get()) }
    single<MilestoneRepository> { MilestoneRepository(get()) }
}

val uiModules = module {
    factory<GoalAdapter> { GoalAdapter() }
    factory<CategoryAdapter> { CategoryAdapter() }
    factory<PriorityAdapter> { PriorityAdapter() }
    factory<MilestoneAdapter> { MilestoneAdapter() }
}

val viewModelModules = module {
    viewModel<AuthViewModel> { AuthViewModel(get()) }
    viewModel<GoalsViewModel> { GoalsViewModel(get()) }
    viewModel<GoalFormViewModel> { GoalFormViewModel(get()) }
    viewModel<GoalDetailsViewModel> { GoalDetailsViewModel(get(), get()) }
}