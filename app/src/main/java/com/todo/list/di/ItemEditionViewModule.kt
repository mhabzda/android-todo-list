package com.todo.list.di

import com.todo.list.ui.item.edition.ItemEditionActivity
import com.todo.list.ui.item.edition.ItemEditionContract
import com.todo.list.ui.item.edition.ItemEditionPresenter
import com.todo.list.ui.parcel.TodoItemParcelable
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ItemEditionViewModule {
    @Binds
    abstract fun provideItemEditionView(itemEditionActivity: ItemEditionActivity): ItemEditionContract.View

    @Binds
    abstract fun provideItemEditionPresenter(presenter: ItemEditionPresenter): ItemEditionContract.Presenter

    companion object {
        @JvmStatic
        @Provides
        fun provideItemParcelable(itemEditionActivity: ItemEditionActivity): TodoItemParcelable {
            return itemEditionActivity.provideTodoItemParcelable()
        }
    }
}
