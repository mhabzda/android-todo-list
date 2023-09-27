package com.mhabzda.todolist.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhabzda.todolist.R
import com.mhabzda.todolist.item.ItemContract.ItemEffect.Close
import com.mhabzda.todolist.item.ItemContract.ItemEffect.DisplayMessage
import com.mhabzda.todolist.item.ItemContract.ItemEffect.DisplayMessageRes
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitDescription
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitIconUrl
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitTitle
import com.mhabzda.todolist.theme.marginDefault
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO split into components
//  & hide keyboard on outside click and back button
//  & change buttons corners
//  & Previews
//  & Error on title field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsState()
    val effects = viewModel.effects

    var titleText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var iconUrlText by remember { mutableStateOf("") }

    LaunchedEffect(effects) {
        effects.collectLatest {
            when (it) {
                is InitTitle -> titleText = it.title
                is InitDescription -> descriptionText = it.description
                is InitIconUrl -> iconUrlText = it.iconUrl
                is DisplayMessageRes -> launch { viewModel.showSnackbar(context.resources.getString(it.messageRes)) }
                is DisplayMessage -> launch { viewModel.showSnackbar(it.message) }
                Close -> navigateBack()
            }
        }
    }

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
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
            )
        },
        content = { contentPadding ->
            Box {
                Column(
                    modifier = Modifier
                        .padding(
                            start = marginDefault,
                            top = contentPadding.calculateTopPadding() + marginDefault,
                            end = marginDefault,
                            bottom = marginDefault,
                        )
                ) {
                    val descriptionFocusRequester = remember { FocusRequester() }
                    val iconUrlFocusRequester = remember { FocusRequester() }

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault),
                        value = titleText,
                        onValueChange = {
                            if (it.length <= 30) titleText = it
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.item_title_hint),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { descriptionFocusRequester.requestFocus() },
                        ),
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault)
                            .focusRequester(descriptionFocusRequester),
                        value = descriptionText,
                        onValueChange = {
                            if (it.length <= 200) descriptionText = it
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.item_description_hint),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { iconUrlFocusRequester.requestFocus() },
                        ),
                        maxLines = 5,
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault)
                            .focusRequester(iconUrlFocusRequester),
                        value = iconUrlText,
                        onValueChange = { iconUrlText = it },
                        label = {
                            Text(
                                text = stringResource(id = R.string.item_icon_url_hint),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Uri
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.onButtonClick(title = titleText, description = descriptionText, iconUrl = iconUrlText) },
                        ),
                        maxLines = 5,
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = marginDefault)
                            .weight(1f)
                    ) {
                        Button(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .defaultMinSize(minHeight = 48.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            onClick = { viewModel.onButtonClick(title = titleText, description = descriptionText, iconUrl = iconUrlText) }
                        ) {
                            Text(text = stringResource(id = state.value.buttonText))
                        }
                    }
                }

                if (state.value.isLoading) {
                    val interactionSource = remember { MutableInteractionSource() }
                    CircularProgressIndicator(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {},
                            )
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
    )
}