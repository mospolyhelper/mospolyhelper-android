package com.mospolytech.features.schedule.menu.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mospolytech.features.base.utils.ClickListener
import com.mospolytech.features.base.utils.ContentAlpha
import com.mospolytech.features.base.utils.MaterialTheme3
import com.mospolytech.features.base.utils.WithContentAlpha
import com.mospolytech.features.schedule.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleCard(
    onScheduleClick: ClickListener
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(120.dp)
            .fillMaxWidth(0.6f),
        shape = RoundedCornerShape(16.dp),
        onClick = onScheduleClick
    ) {
        Column(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
            Text(text = stringResource(R.string.sch_schedule))
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.sch_now).uppercase(),
                style = MaterialTheme3.typography.labelSmall,
                color = MaterialTheme3.colorScheme.secondary
            )
            WithContentAlpha(alpha = ContentAlpha.medium) {
                Text(
                    text = "Информационные системы и технологии",
                    style = MaterialTheme3.typography.bodySmall
                )
            }
        }
    }
}