package com.pedro.schwarz.goalstracker.di.modules

import com.pedro.schwarz.goalstracker.repository.AuthRepository
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModules = module { }

val daoModules = module {
    single<AuthRepository> { AuthRepository() }
}

val uiModules = module { }

val viewModelModules = module {
    viewModel<AuthViewModel> { AuthViewModel(get()) }
}