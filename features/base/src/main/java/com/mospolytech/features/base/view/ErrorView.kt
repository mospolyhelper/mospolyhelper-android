package com.mospolytech.features.base.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.mospolytech.features.base.utils.ClickListener

@Composable
fun ErrorView(message: String = "Произошла ошибка", retryAction: ClickListener) {
    ConstraintLayout {
        val (button, text) = createRefs()
        Text(text = message,
            modifier = Modifier.constrainAs(text) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(button.start)
                width = Dimension.fillToConstraints
        })
        Button(onClick = { retryAction.invoke() },
        modifier = Modifier.constrainAs(text) {
            start.linkTo(text.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
        }) {
            Text("Повторить")
        }
    }
}