package com.mhabzda.todolist.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mhabzda.todolist.data.mapper.TodoDocumentFilter
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.CREATION_DATE_TIME_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentMapper
import com.mhabzda.todolist.data.mapper.TodoItemMapper
import com.mhabzda.todolist.data.time.CurrentDateTimeProvider
import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FirestoreTodoItemRepository @Inject constructor(
    private val todoCollection: CollectionReference,
    private val todoItemMapper: TodoItemMapper,
    private val todoDocumentMapper: TodoDocumentMapper,
    private val todoDocumentFilter: TodoDocumentFilter,
    private val currentDateTimeProvider: CurrentDateTimeProvider,
) : TodoItemRepository {

    // TODO decide how to handle filtering and errors
    override suspend fun getItems(pageSize: Int, itemIdFrom: String?): List<TodoItem> =
        queryItems(pageSize = pageSize, itemIdFrom = itemIdFrom)
            .documents
            .filter { document -> todoDocumentFilter.filter(document) }
            .map { document -> todoDocumentMapper.map(document) }

    private suspend fun queryItems(pageSize: Int, itemIdFrom: String?): QuerySnapshot {
        val lastItem = if (itemIdFrom == null) null else getItemById(itemIdFrom).getOrThrow()

        return Tasks.await(
            todoCollection
                .orderBy(CREATION_DATE_TIME_KEY)
                .run { if (lastItem == null) this else startAfter(lastItem) }
                .limit(pageSize.toLong())
                .get()
        )
    }

    override suspend fun getItem(id: String): Result<TodoItem> =
        getItemById(id).mapCatching { todoDocumentMapper.map(it) }

    override suspend fun deleteItem(id: String): Result<Unit> =
        getItemById(id).mapCatching { document ->
            suspendCoroutine { continuation ->
                document.reference.delete()
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
        }

    override suspend fun saveItem(title: String, description: String, iconUrl: String?): Result<Unit> =
        suspendCoroutine { continuation ->
            todoCollection
                .add(
                    todoItemMapper.map(
                        title = title,
                        description = description,
                        iconUrl = iconUrl,
                        creationDate = currentDateTimeProvider.getCurrentDateTime(),
                    )
                )
                .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                .addOnFailureListener { continuation.resume(Result.failure(it)) }
        }

    override suspend fun editItem(id: String, title: String, description: String, iconUrl: String?): Result<Unit> =
        getItemById(id).mapCatching { document ->
            suspendCoroutine { continuation ->
                document.reference
                    .update(
                        todoItemMapper.map(
                            title = title,
                            description = description,
                            iconUrl = iconUrl,
                        )
                    )
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
        }

    private suspend fun getItemById(id: String): Result<DocumentSnapshot> =
        suspendCoroutine { continuation ->
            todoCollection.document(id).get()
                .addOnSuccessListener { continuation.resume(Result.success(it)) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
}