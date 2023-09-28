package com.mhabzda.todolist.data.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mhabzda.todolist.data.repository.FirestoreTodoItemRepository
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Singleton
    @Binds
    internal abstract fun provideTodoRepository(
        firestoreTodoItemRepository: FirestoreTodoItemRepository,
    ): TodoItemRepository

    companion object {
        @Provides
        fun provideTodoCollection(): CollectionReference =
            Firebase.firestore.collection("todo_items")
    }
}
