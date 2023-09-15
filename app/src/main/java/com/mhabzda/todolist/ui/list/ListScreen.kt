package com.mhabzda.todolist.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.mhabzda.todolist.R
import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.theme.defaultMargin
import com.mhabzda.todolist.utils.format

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ListScreen(
    lazyPagingItems: LazyPagingItems<TodoItem>,
    navigateToAddItem: () -> Unit,
    navigateToEditItem: (String) -> Unit,
    deleteItem: (String) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddItem,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        },
        content = {
            val refreshLoadState = lazyPagingItems.loadState.refresh
            val appendLoadState = lazyPagingItems.loadState.append
            val isRefreshing = refreshLoadState == LoadState.Loading
            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = { lazyPagingItems.refresh() },
            )

            val itemToDeleteId = remember { mutableStateOf<String?>(null) }

            Box(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(count = lazyPagingItems.itemCount) { index ->
                        val item = lazyPagingItems[index]
                        item?.let {
                            ItemView(
                                item = item,
                                navigateToEditItem = navigateToEditItem,
                                displayDeleteDialog = { itemId -> itemToDeleteId.value = itemId }
                            )
                            Divider()
                        }
                    }

                    if (appendLoadState == LoadState.Loading) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    if (appendLoadState is LoadState.Error) {
                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = defaultMargin),
                                text = stringResource(id = R.string.list_error_description_append),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                if (refreshLoadState is LoadState.Error) {
                    ErrorDialog(
                        errorMessage = stringResource(id = R.string.list_error_description_refresh),
                        retryAction = { lazyPagingItems.refresh() },
                    )
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                )

                // TODO add loading on deletion
                itemToDeleteId.value?.let {
                    DeleteDialogAlert(
                        confirmAction = { deleteItem(it) },
                        dismissAction = { itemToDeleteId.value = null },
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ItemView(
    item: TodoItem,
    navigateToEditItem: (String) -> Unit,
    displayDeleteDialog: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { navigateToEditItem(item.id) },
                onLongClick = { displayDeleteDialog(item.id) },
            )
    ) {
        GlideImage(
            model = item.iconUrl,
            contentScale = ContentScale.Fit,
            loading = placeholder(R.drawable.ic_check_box),
            failure = placeholder(R.drawable.ic_check_box),
            contentDescription = null,
        )

        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .weight(1f),
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .weight(1f),
                text = item.creationDateTime.format(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// TODO unify dialogs
@Composable
private fun DeleteDialogAlert(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.delete_item_dialog_title)) },
        text = {
            Text(
                text = stringResource(id = R.string.delete_item_dialog_message),
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        onDismissRequest = { dismissAction() },
        confirmButton = {
            TextButton(onClick = confirmAction) {
                Text(
                    text = stringResource(id = R.string.delete_item_dialog_delete_button),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = dismissAction) {
                Text(
                    text = stringResource(id = R.string.delete_item_dialog_cancel_button),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    )
}

@Composable
private fun ErrorDialog(errorMessage: String, retryAction: () -> Unit) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.error_title)) },
        text = {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = retryAction) {
                Text(
                    text = stringResource(id = R.string.retry),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
    )
}