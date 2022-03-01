package com.todo.list.di.list

import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.todo.list.ui.list.ListFragment
import com.todo.list.ui.list.navigation.ListRouter
import com.todo.list.ui.list.navigation.ListRouterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ListViewModule {
    @Binds
    abstract fun provideListRouter(listRouter: ListRouterImpl): ListRouter

    companion object {
        @Provides
        fun provideNavigationController(listFragment: ListFragment): NavController =
            listFragment.findNavController()
    }
}
