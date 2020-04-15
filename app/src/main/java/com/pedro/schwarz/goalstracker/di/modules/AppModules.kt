package com.pedro.schwarz.goalstracker.di.modules

import androidx.room.Room
import com.pedro.schwarz.goalstracker.database.AppDatabase
import com.pedro.schwarz.goalstracker.database.dao.CheckpointDAO
import com.pedro.schwarz.goalstracker.database.dao.GoalDAO
import com.pedro.schwarz.goalstracker.database.dao.MilestoneDAO
import com.pedro.schwarz.goalstracker.repository.AuthRepository
import com.pedro.schwarz.goalstracker.repository.CheckpointRepository
import com.pedro.schwarz.goalstracker.repository.GoalRepository
import com.pedro.schwarz.goalstracker.repository.MilestoneRepository
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.*
import com.pedro.schwarz.goalstracker.ui.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DATABASE_NAME = "goals_tracker.db"

val databaseModules = module {
    single { Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME).build() }
}

val daoModules = module {
    single<GoalDAO> { get<AppDatabase>().getGoalDAO() }
    single<MilestoneDAO> { get<AppDatabase>().getMilestoneDAO() }
    single<CheckpointDAO> { get<AppDatabase>().getCheckpointDAO() }
    single<AuthRepository> { AuthRepository() }
    single<GoalRepository> { GoalRepository(get()) }
    single<MilestoneRepository> { MilestoneRepository(get()) }
    single<CheckpointRepository> { CheckpointRepository(get()) }
}

val uiModules = module {
    factory<GoalAdapter> { GoalAdapter() }
    factory<CategoryAdapter> { CategoryAdapter() }
    factory<PriorityAdapter> { PriorityAdapter() }
    factory<MilestoneAdapter> { MilestoneAdapter() }
    factory<CheckpointAdapter> { CheckpointAdapter() }
}

val viewModelModules = module {
    viewModel<AuthViewModel> { AuthViewModel(get()) }
    viewModel<GoalsViewModel> { GoalsViewModel(get()) }
    viewModel<GoalFormViewModel> { GoalFormViewModel(get()) }
    viewModel<GoalDetailsViewModel> { GoalDetailsViewModel(get(), get()) }
    viewModel<CheckpointsViewModel> { CheckpointsViewModel(get()) }
    viewModel<CheckpointFormViewModel> { CheckpointFormViewModel(get()) }
    viewModel<CheckpointDetailsViewModel> { CheckpointDetailsViewModel(get()) }
}