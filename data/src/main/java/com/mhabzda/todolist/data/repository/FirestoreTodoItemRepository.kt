package com.mhabzda.todolist.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.CREATION_DATE_TIME_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentMapper
import com.mhabzda.todolist.data.mapper.TodoItemMapper
import com.mhabzda.todolist.data.time.CurrentTimeProvider
import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// TODO add tests
internal class FirestoreTodoItemRepository @Inject constructor(
    private val todoCollection: CollectionReference,
    private val todoItemMapper: TodoItemMapper,
    private val todoDocumentMapper: TodoDocumentMapper,
    private val currentTimeProvider: CurrentTimeProvider,
) : TodoItemRepository {

    override suspend fun getItems(pageSize: Int, itemIdFrom: String?): Result<List<TodoItem>> =
        queryItems(pageSize = pageSize, itemIdFrom = itemIdFrom)
            .mapCatching { documents -> documents.map { todoDocumentMapper.map(it) } }

    private suspend fun queryItems(pageSize: Int, itemIdFrom: String?): Result<List<DocumentSnapshot>> =
        (if (itemIdFrom == null) Result.success(null) else getItemById(itemIdFrom))
            .mapCatching { document ->
                suspendCoroutine { continuation ->
                    todoCollection
                        .orderBy(CREATION_DATE_TIME_KEY)
                        .run { if (document == null) this else startAfter(document) }
                        .limit(pageSize.toLong())
                        .get()
                        .addOnSuccessListener { continuation.resume(it.documents) }
                        .addOnFailureListener { continuation.resumeWithException(it) }
                }
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

    override suspend fun createItem(title: String, description: String, iconUrl: String?): Result<Unit> =
        suspendCoroutine { continuation ->
            todoCollection
                .add(
                    todoItemMapper.map(
                        title = title,
                        description = description,
                        iconUrl = iconUrl,
                        creationTime = currentTimeProvider.getCurrentDateTime(),
                    ),
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
                        ),
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
