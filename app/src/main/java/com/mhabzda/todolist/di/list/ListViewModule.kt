package com.mhabzda.todolist.di.list

import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.mhabzda.todolist.ui.list.ListFragment
import com.mhabzda.todolist.ui.list.navigation.ListRouter
import com.mhabzda.todolist.ui.list.navigation.ListRouterImpl
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
