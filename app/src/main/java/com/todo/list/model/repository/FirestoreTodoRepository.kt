package com.todo.list.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentFilter
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.model.mapper.TodoItemMapper
import com.todo.list.model.mapper.toCreationDateString
import com.todo.list.utils.isNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.joda.time.DateTime
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
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

    override fun observeItemsChanges(): Flow<Unit> =
        callbackFlow { observeSnapshots(this) }

    override suspend fun getItem(id: DateTime): Result<TodoItem> =
        suspendCoroutine { continuation ->
            getItemById(id)
                .addOnSuccessListener { continuation.resume(Result.success(todoDocumentMapper.map(it.getDocument()))) }
                .addOnFailureListener { continuation.resume(Result.failure(it)) }
        }

    override suspend fun deleteItem(id: DateTime): Result<Unit> =
        suspendCoroutine { continuation ->
            getItemById(id)
                .addOnSuccessListener {
                    val documentRef = it.getDocument().reference
                    documentRef.delete()
                        .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                        .addOnFailureListener { error -> continuation.resume(Result.failure(error)) }
                }
                .addOnFailureListener {
                    continuation.resume(Result.failure(it))
                }
        }

    private fun getItemById(id: DateTime) =
        todoCollection
            .whereEqualTo(CREATION_DATE_KEY, id.toCreationDateString())
            .get()

    override suspend fun saveItem(item: TodoItem): Result<Unit> =
        suspendCoroutine { continuation ->
            todoCollection
                .add(todoItemMapper.map(item))
                .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                .addOnFailureListener { continuation.resume(Result.failure(it)) }
        }

    override suspend fun editItem(item: TodoItem): Result<Unit> {
        val deleteResult = deleteItem(item.creationDate)
        if (deleteResult.isFailure) return deleteResult
        return saveItem(item)
    }

    private suspend fun observeSnapshots(scope: ProducerScope<Unit>) {
        val registration = todoCollection.addSnapshotListener { querySnapshot, exception ->
            if (exception.isNotNull()) {
                return@addSnapshotListener
            }

            if (querySnapshot.isNotNull()) {
                scope.trySendBlocking(Unit)
            }
        }

        scope.awaitClose { registration.remove() }
    }

    private fun QuerySnapshot.getDocument() = documents.first()
}
