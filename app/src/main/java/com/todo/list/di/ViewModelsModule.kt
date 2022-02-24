package com.todo.list.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todo.list.di.annotation.ViewModelKey
import com.todo.list.di.factory.ViewModelFactory
import com.todo.list.ui.itemcreation.ItemCreationViewModel
import com.todo.list.ui.list.ListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class ViewModelsModule {
    @Singleton
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ListViewModel::class)
    abstract fun bindListViewModel(viewModel: ListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemCreationViewModel::class)
    abstract fun bindItemCreationViewModel(viewModel: ItemCreationViewModel): ViewModel
}
