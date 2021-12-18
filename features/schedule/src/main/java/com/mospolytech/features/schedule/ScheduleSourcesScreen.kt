package com.mospolytech.features.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.LocalTextStyle
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
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.mospolytech.domain.schedule.model.ScheduleSourceFull
import org.koin.androidx.compose.getViewModel

@Composable
fun ScheduleSourcesScreen(viewModel: ScheduleViewModel = getViewModel()) {
    val sources by remember { mutableStateOf(listOf<ScheduleSourceFull>()) }
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(sources) { source ->
            SourceItem(source)
        }
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
            InitialAvatar(url = source.avatarUrl, source.title)
            Spacer(Modifier.width(5.dp))
            Column {
                Text(text = source.title)
                Text(text = source.description)
            }
        }
        Spacer(Modifier.height(5.dp))
    }
}

@Composable
fun InitialAvatar(url: String, initials: String = "") {
    if (url.isEmpty()) {
        val str = initials.split('-').joinToString(separator = "") { it.take(1) }
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
        ) {
            Box {
                Text(
                    text = str,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    } else {
        Image(
            painter = rememberImagePainter(
                data = url,
                builder = {
                    transformations(CircleCropTransformation())
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}


@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        //softWrap = softWrap,
        softWrap = false,
        maxLines = maxLines,
        style = scaledTextStyle,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * (textLayoutResult.size.width / textLayoutResult.multiParagraph.width))
            } else {
                readyToDraw = true
            }
        }
    )
}