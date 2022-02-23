package com.todo.list.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentFilter
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.model.mapper.TodoItemMapper
import com.todo.list.utils.isNotNull
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
    private val todoCollection: CollectionReference,
    private val todoItemMapper: TodoItemMapper,
    private val todoDocumentMapper: TodoDocumentMapper,
    private val todoDocumentFilter: TodoDocumentFilter
) : TodoRepository {
    override fun fetchTodoItems(pageSize: Int): Flow<PagingData<TodoItem>> =
        Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false)) {
            TodoItemPagingSource(todoCollection, todoDocumentMapper, todoDocumentFilter)
        }.flow

    override fun observeItemsChanges(): Observable<Any> {
        return Observable.create { emitter ->
            observeSnapshots(emitter)
        }
    }

    override fun deleteItem(item: TodoItem): Completable {
        return Completable.create { emitter ->
            val document = todoItemMapper.map(item)
            todoCollection
                .whereEqualTo(CREATION_DATE_KEY, document[CREATION_DATE_KEY])
                .get()
                .addOnSuccessListener {
                    val documentRef = it.documents.first().reference
                    documentRef.delete()
                        .addOnSuccessListener { emitter.onComplete() }
                        .addOnFailureListener { error -> emitter.onError(error) }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    override fun saveItem(item: TodoItem): Completable {
        return Completable.create { emitter ->
            todoCollection
                .add(todoItemMapper.map(item))
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun editItem(item: TodoItem): Completable {
        return deleteItem(item)
            .andThen(saveItem(item))
    }

    private fun observeSnapshots(emitter: ObservableEmitter<Any>) {
        val registration = todoCollection.addSnapshotListener { querySnapshot, exception ->
            if (exception.isNotNull()) {
                return@addSnapshotListener
            }

            if (querySnapshot.isNotNull()) {
                emitter.onNext(Any())
            }
        }

        emitter.setCancellable {
            registration.remove()
        }
    }
}
