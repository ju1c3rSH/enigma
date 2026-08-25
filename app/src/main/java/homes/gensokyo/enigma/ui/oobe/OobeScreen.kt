@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package homes.gensokyo.enigma.ui.oobe

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import homes.gensokyo.enigma.R

private fun stepLabel(step: Int): Int = when (step) {
    1 -> R.string.oobe_step_school
    2 -> R.string.oobe_step_class
    else -> R.string.oobe_step_personal
}

@Composable
fun OobeScreen(
    state: OobeUiState,
    onQueryChange: (String) -> Unit,
    onSelectSchool: (homes.gensokyo.enigma.bean.School) -> Unit,
    onSelectGrade: (Int) -> Unit,
    onSelectClass: (Int) -> Unit,
    onChangeSchool: () -> Unit,
    onNameChange: (String) -> Unit,
    onCardChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier
            .fillMaxSize()
            .background(scheme.background)
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (state.step > 1) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.oobe_prev),
                        tint = scheme.onSurface
                    )
                }
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = stringResource(R.string.oobe_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = scheme.onSurface
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.oobe_step_of, state.step),
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(stepLabel(state.step)),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = scheme.primary
            )
        }
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { state.step / 3f },
            modifier = Modifier.fillMaxWidth(),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        state.error?.let { err ->
            Spacer(Modifier.height(10.dp))
            Surface(
                color = scheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        AnimatedContent(
            targetState = state.step,
            transitionSpec = {
                val forward = targetState > initialState
                (slideInHorizontally { if (forward) it / 4 else -it / 4 } + fadeIn()) togetherWith
                    (slideOutHorizontally { if (forward) -it / 4 else it / 4 } + fadeOut())
            },
            label = "oobe_step"
        ) { step ->
            when (step) {
                1 -> StepSchool(state, onQueryChange, onSelectSchool)
                2 -> StepClass(state, onSelectGrade, onSelectClass, onChangeSchool, onNext)
                else -> StepPersonal(state, onNameChange, onCardChange, onSubmit)
            }
        }
    }
}

@Composable
private fun StepSchool(
    state: OobeUiState,
    onQueryChange: (String) -> Unit,
    onSelectSchool: (homes.gensokyo.enigma.bean.School) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            singleLine = true,
            label = { Text(stringResource(R.string.oobe_school_hint)) },
            placeholder = { Text(stringResource(R.string.oobe_search_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.oobe_clear))
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        Spacer(Modifier.height(8.dp))
        when {
            state.searching -> Row(
                Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) { CircularProgressIndicator(Modifier.size(28.dp)) }
            state.query.isBlank() -> Box(
                Modifier.fillMaxWidth().padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.oobe_search_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            state.schools.isEmpty() -> Box(
                Modifier.fillMaxWidth().padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.oobe_no_result),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> LazyColumn {
                items(count = state.schools.size, key = { state.schools[it].schoolId }) { i ->
                    val school = state.schools[i]
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSchool(school) }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = school.schoolName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        school.address?.takeIf { it.isNotBlank() }?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun StepClass(
    state: OobeUiState,
    onSelectGrade: (Int) -> Unit,
    onSelectClass: (Int) -> Unit,
    onChangeSchool: () -> Unit,
    onNext: () -> Unit,
) {
    var gradeExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.selectedSchool?.schoolShortName?.take(1)?.ifBlank { null }
                            ?: state.selectedSchool?.schoolName?.take(1)?.ifBlank { "校" } ?: "校",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = state.selectedSchool?.schoolName ?: "--",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onChangeSchool) {
                    Text(stringResource(R.string.oobe_change_school))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (state.classesLoading) {
            Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(28.dp))
            }
        } else {
            DropdownField(
                label = stringResource(R.string.oobe_grade),
                value = state.grades.find { it.gradeId == state.selectedGradeId }?.gradeName,
                options = state.grades.map { it.gradeName },
                expanded = gradeExpanded,
                onExpandedChange = { gradeExpanded = it },
                onPick = { idx ->
                    onSelectGrade(state.grades[idx].gradeId)
                    gradeExpanded = false
                }
            )
            Spacer(Modifier.height(12.dp))
            DropdownField(
                label = stringResource(R.string.oobe_class),
                value = state.classes.find { it.classId == state.selectedClassId }?.className,
                options = state.classes.map { it.className },
                enabled = state.selectedGradeId != null,
                expanded = classExpanded,
                onExpandedChange = { classExpanded = it },
                onPick = { idx ->
                    onSelectClass(state.classes[idx].classId)
                    classExpanded = false
                }
            )
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onNext,
            enabled = state.selectedClassId != null && !state.classesLoading,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.oobe_next))
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DropdownField(
    label: String,
    value: String?,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onPick: (Int) -> Unit,
    enabled: Boolean = true,
) {
    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = value ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            placeholder = { Text(stringResource(R.string.oobe_pick_first)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled) },
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { onExpandedChange(false) }) {
            options.forEachIndexed { idx, opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { onPick(idx) }
                )
            }
        }
    }
}

@Composable
private fun StepPersonal(
    state: OobeUiState,
    onNameChange: (String) -> Unit,
    onCardChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val nameFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { nameFocus.requestFocus() }
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            singleLine = true,
            label = { Text(stringResource(R.string.oobe_name_label)) },
            supportingText = { Text(stringResource(R.string.oobe_name_helper)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nameFocus)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.cardNumber,
            onValueChange = onCardChange,
            singleLine = true,
            label = { Text(stringResource(R.string.oobe_card_label)) },
            supportingText = { Text(stringResource(R.string.oobe_card_helper)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboard?.hide()
                if (state.name.isNotBlank() && state.cardNumber.isNotBlank()) onSubmit()
            }),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                keyboard?.hide()
                onSubmit()
            },
            enabled = state.name.isNotBlank() && state.cardNumber.isNotBlank() && !state.submitting,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.submitting) {
                CircularProgressIndicator(
                    Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(stringResource(R.string.oobe_submit))
        }
    }
}
