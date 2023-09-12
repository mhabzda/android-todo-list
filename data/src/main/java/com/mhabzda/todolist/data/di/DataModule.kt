package com.mhabzda.todolist.data.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mhabzda.todolist.data.repository.FirestoreTodoItemRepository
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DataModule {
    @Singleton
    @Binds
    internal abstract fun provideTodoRepository(
        firestoreTodoItemRepository: FirestoreTodoItemRepository
    ): TodoItemRepository

    companion object {
        private const val COLLECTION_NAME = "todo_items"

        @Provides
        fun provideTodoCollection(): CollectionReference =
            Firebase.firestore.collection(COLLECTION_NAME)
    }
}