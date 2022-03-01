package com.todo.list.di.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.todo.list.model.repository.FirestoreTodoRepository
import com.todo.list.model.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun provideTodoRepository(firestoreTodoRepository: FirestoreTodoRepository): TodoRepository

    companion object {
        private const val COLLECTION_NAME = "todo_items"

        @Provides
        fun provideTodoCollection(): CollectionReference {
            return Firebase.firestore.collection(COLLECTION_NAME)
        }
    }
}
