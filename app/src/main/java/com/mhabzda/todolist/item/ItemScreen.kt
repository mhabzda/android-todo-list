package com.mhabzda.todolist.item

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhabzda.todolist.R
import com.mhabzda.todolist.item.ItemContract.ItemEffect
import com.mhabzda.todolist.item.ItemContract.ItemEffect.Close
import com.mhabzda.todolist.item.ItemContract.ItemEffect.DisplayMessage
import com.mhabzda.todolist.item.ItemContract.ItemEffect.DisplayMessageRes
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitDescription
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitIconUrl
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitTitle
import com.mhabzda.todolist.item.ItemContract.ItemViewState
import com.mhabzda.todolist.theme.TodoListTheme
import com.mhabzda.todolist.theme.marginDefault
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    ItemScreen(
        viewState = viewModel.state,
        effects = viewModel.effects,
        showSnackbar = viewModel::showSnackbar,
        onButtonClick = viewModel::onButtonClick,
        navigateBack = navigateBack,
    )
}

@Composable
fun ItemScreen(
    viewState: StateFlow<ItemViewState>,
    effects: SharedFlow<ItemEffect>,
    showSnackbar: suspend (String) -> Unit,
    onButtonClick: (String, String, String) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current
    val state = viewState.collectAsState()

    var titleText by remember { mutableStateOf("") }
    var showTitleError by remember { mutableStateOf(false) }
    var descriptionText by remember { mutableStateOf("") }
    var iconUrlText by remember { mutableStateOf("") }

    LaunchedEffect(effects) {
        effects.collectLatest {
            when (it) {
                is InitTitle -> titleText = it.title
                is InitDescription -> descriptionText = it.description
                is InitIconUrl -> iconUrlText = it.iconUrl
                ItemEffect.DisplayTitleError -> showTitleError = true
                is DisplayMessageRes -> launch { showSnackbar(context.resources.getString(it.messageRes)) }
                is DisplayMessage -> launch { showSnackbar(it.message) }
                Close -> navigateBack()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { ItemTopAppBar(navigateBack, localFocusManager) },
        content = { contentPadding ->
            Box {
                Column(
                    modifier = Modifier
                        .pointerInput(Unit) { detectTapGestures(onTap = { localFocusManager.clearFocus() }) }
                        .padding(
                            start = marginDefault,
                            top = contentPadding.calculateTopPadding() + marginDefault,
                            end = marginDefault,
                            bottom = marginDefault,
                        ),
                ) {
                    val descriptionFocusRequester = remember { FocusRequester() }
                    val iconUrlFocusRequester = remember { FocusRequester() }

                    ItemOutlineTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault),
                        value = titleText,
                        onValueChange = {
                            if (it.length <= 30) {
                                titleText = it
                                showTitleError = false
                            }
                        },
                        hintResId = R.string.item_title_hint,
                        isError = showTitleError,
                        errorMessageResId = R.string.item_empty_title_message,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { descriptionFocusRequester.requestFocus() },
                        ),
                    )

                    ItemOutlineTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault)
                            .focusRequester(descriptionFocusRequester),
                        value = descriptionText,
                        onValueChange = {
                            if (it.length <= 200) descriptionText = it
                        },
                        hintResId = R.string.item_description_hint,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { iconUrlFocusRequester.requestFocus() },
                        ),
                        maxLines = 5,
                    )

                    ItemOutlineTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault)
                            .focusRequester(iconUrlFocusRequester),
                        value = iconUrlText,
                        onValueChange = { iconUrlText = it },
                        hintResId = R.string.item_icon_url_hint,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Uri,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onButtonClick(titleText, descriptionText, iconUrlText)
                                localFocusManager.clearFocus()
                            },
                        ),
                        maxLines = 5,
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault)
                            .weight(1f),
                    ) {
                        Button(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .defaultMinSize(minHeight = 48.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            onClick = {
                                onButtonClick(titleText, descriptionText, iconUrlText)
                                localFocusManager.clearFocus()
                            },
                        ) {
                            Text(text = stringResource(id = state.value.buttonText))
                        }
                    }
                }

                if (state.value.isLoading) {
                    FullScreenLoading()
                }
            }
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ItemTopAppBar(navigateBack: () -> Unit, localFocusManager: FocusManager) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    localFocusManager.clearFocus()
                    navigateBack()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
    )
}

@Composable
private fun ItemOutlineTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes hintResId: Int,
    isError: Boolean = false,
    @StringRes errorMessageResId: Int? = null,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    maxLines: Int = Int.MAX_VALUE,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(id = hintResId),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        isError = isError,
        supportingText = {
            if (isError && errorMessageResId != null) {
                Text(
                    text = stringResource(id = errorMessageResId),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
    )
}

@Composable
private fun FullScreenLoading() {
    CircularProgressIndicator(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.6f))
            .pointerInput(Unit) { detectTapGestures(onTap = {}) }
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
    )
}

@Preview
@Composable
fun ItemScreenPreview() {
    TodoListTheme {
        ItemScreen(
            viewState = MutableStateFlow(ItemViewState()),
            effects = MutableSharedFlow(),
            showSnackbar = {},
            onButtonClick = { _, _, _ -> },
            navigateBack = {},
        )
    }
}

@Preview
@Composable
fun ItemScreenPreviewLoading() {
    TodoListTheme {
        ItemScreen(
            viewState = MutableStateFlow(ItemViewState(isLoading = true)),
            effects = MutableSharedFlow(),
            showSnackbar = {},
            onButtonClick = { _, _, _ -> },
            navigateBack = {},
        )
    }
}
