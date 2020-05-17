package com.todo.list.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.todo.list.model.FirestoreTodoRepository
import com.todo.list.model.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class RepositoryModule {
  @Binds
  abstract fun provideTodoRepository(firestoreTodoRepository: FirestoreTodoRepository): TodoRepository

  companion object {
    private const val COLLECTION_NAME = "todo_items"

    @JvmStatic
    @Provides
    fun provideTodoCollection(): CollectionReference {
      return Firebase.firestore.collection(COLLECTION_NAME)
    }
  }
}