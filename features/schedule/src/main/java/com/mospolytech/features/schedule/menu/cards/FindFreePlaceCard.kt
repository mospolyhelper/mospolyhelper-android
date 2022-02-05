package com.mospolytech.features.schedule.menu.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mospolytech.features.base.core.utils.ClickListener
import com.mospolytech.features.schedule.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FindFreePlaceCard(
    onFreePlaceClick: ClickListener
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onFreePlaceClick
    ) {
        Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
            Text(text = stringResource(R.string.sch_find_free_place))
        }
    }
}