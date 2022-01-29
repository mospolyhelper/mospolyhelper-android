package com.mospolytech.features.base.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mospolytech.features.base.utils.ClickListener
import com.mospolytech.features.base.utils.FluentIcons
import com.mospolytech.features.base.utils.MaterialTheme3

@Composable
fun PrimaryTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: ClickListener,
    showLoading: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.smallTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {

    SmallTopAppBar(
        title = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f, fill = true),
                    style = MaterialTheme3.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                if (showLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(26.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Spacer(Modifier.size(26.dp))
                }
            }
        },
        modifier.height(50.dp),
        navigationIcon = {
            BackIconButton(onBackClick)
        },
        actions,
        colors,
        scrollBehavior
    )
}

@Composable
fun BackIconButton(onBackClick: ClickListener) {
    IconButton(onClick = onBackClick) {
        Icon(painter = painterResource(FluentIcons.ic_fluent_arrow_left_20_filled), contentDescription = null)
    }
}