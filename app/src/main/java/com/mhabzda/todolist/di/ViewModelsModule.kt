package com.mhabzda.todolist.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mhabzda.todolist.di.annotation.ViewModelKey
import com.mhabzda.todolist.di.factory.ViewModelFactory
import com.mhabzda.todolist.ui.item.ItemViewModel
import com.mhabzda.todolist.ui.list.ListViewModel
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
    @ViewModelKey(ItemViewModel::class)
    abstract fun bindItemCreationViewModel(viewModel: ItemViewModel): ViewModel
}
