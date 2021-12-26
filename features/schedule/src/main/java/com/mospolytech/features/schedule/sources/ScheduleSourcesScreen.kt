package com.mospolytech.features.schedule.sources

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.features.base.utils.*
import com.mospolytech.features.base.view.InitialAvatar
import com.mospolytech.features.schedule.main.ScheduleViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ScheduleSourcesScreen(viewModel: ScheduleSourcesViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()
    
    ScheduleSourcesContent(
        state,
        viewModel::onSelectSourceType
    )
}

@Composable
fun ScheduleSourcesContent(
    state: ScheduleSourceState,
    onSourceTypeSelected: TypedListener<ScheduleSources>
) {
    
    Column {
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.sourceTypes) { sourceType ->
                SourceType(
                    sourceType,
                    sourceType == state.selectedSourceType,
                    onSourceTypeSelected
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.sources) { source ->
                SourceItem(source)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SourceType(
    sourceType: ScheduleSources,
    isSelected: Boolean,
    onSourceTypeSelected: TypedListener<ScheduleSources>
) {
    val color = if (isSelected) MaterialTheme3.colorScheme.primary else MaterialTheme3.colorScheme.surface
    val textColor = if (isSelected) MaterialTheme3.colorScheme.onPrimary else MaterialTheme3.colorScheme.onSurface
    Card(
        onClick = { onSourceTypeSelected(sourceType) },
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        backgroundColor = color
    ) {
        Text(
            text = sourceType.toString(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            color = textColor
        )
    }
}

@Composable
fun SourceItem(source: ScheduleSourceFull, onItemClick: () -> Unit = { }) {
    Column(
        Modifier
            .clickable(onClick = onItemClick)
            .fillMaxWidth()
    ) {
        Spacer(Modifier.height(5.dp))
        Row {
            Spacer(Modifier.width(5.dp))
            val initials = when (source.type) {
                ScheduleSources.Group -> source.title.split('-').joinToString(separator = "") { it.take(1) }
                ScheduleSources.Teacher -> source.title.split(' ').joinToString(separator = "") { it.take(1) }
                ScheduleSources.Student -> source.title.split(' ').joinToString(separator = "") { it.take(1) }
                ScheduleSources.Place -> source.title
                ScheduleSources.Subject -> source.title.split(' ').joinToString(separator = "") { it.take(1) }
                ScheduleSources.Complex -> source.title.split(' ').joinToString(separator = "") { it.take(1) }
            }
            InitialAvatar(url = source.avatarUrl, initials)
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = source.title,
                    style = MaterialTheme3.typography.titleSmall
                )
                WithContentAlpha(alpha = ContentAlpha.medium) {
                    Text(
                        text = source.description,
                        style = MaterialTheme3.typography.bodySmall
                    )
                }
            }
        }
        Spacer(Modifier.height(5.dp))
    }
}


//@Composable
//fun AutoSizeText(
//    text: String,
//    modifier: Modifier = Modifier,
//    color: Color = Color.Unspecified,
//    fontSize: TextUnit = TextUnit.Unspecified,
//    fontStyle: FontStyle? = null,
//    fontWeight: FontWeight? = null,
//    fontFamily: FontFamily? = null,
//    letterSpacing: TextUnit = TextUnit.Unspecified,
//    textDecoration: TextDecoration? = null,
//    textAlign: TextAlign? = null,
//    lineHeight: TextUnit = TextUnit.Unspecified,
//    overflow: TextOverflow = TextOverflow.Clip,
//    softWrap: Boolean = true,
//    maxLines: Int = Int.MAX_VALUE,
//    onTextLayout: (TextLayoutResult) -> Unit = {},
//    style: TextStyle = LocalTextStyle.current
//) {
//    var scaledTextStyle by remember { mutableStateOf(style) }
//    var readyToDraw by remember { mutableStateOf(false) }
//
//    Text(
//        text,
//        modifier = modifier.drawWithContent {
//            if (readyToDraw) {
//                drawContent()
//            }
//        },
//        color = color,
//        fontSize = fontSize,
//        fontStyle = fontStyle,
//        fontWeight = fontWeight,
//        fontFamily = fontFamily,
//        letterSpacing = letterSpacing,
//        textDecoration = textDecoration,
//        textAlign = textAlign,
//        lineHeight = lineHeight,
//        overflow = overflow,
//        //softWrap = softWrap,
//        softWrap = false,
//        maxLines = maxLines,
//        style = scaledTextStyle,
//        onTextLayout = { textLayoutResult ->
//            if (textLayoutResult.didOverflowWidth) {
//                scaledTextStyle =
//                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * (textLayoutResult.size.width / textLayoutResult.multiParagraph.width))
//            } else {
//                readyToDraw = true
//            }
//        }
//    )
//}