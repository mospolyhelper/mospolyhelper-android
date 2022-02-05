package com.mospolytech.features.account.personal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.mospolytech.domain.account.model.Order
import com.mospolytech.domain.account.model.Personal
import com.mospolytech.domain.account.model.print
import com.mospolytech.features.base.core.utils.ClickListener
import com.mospolytech.features.base.core.utils.format
import org.koin.androidx.compose.getViewModel

@Composable
fun PersonalScreen(viewModel: PersonalViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    PersonalContent(state,
    backListener = {viewModel.exit()})
}

@Composable
fun PersonalContent(state: PersonalState, backListener: ClickListener) {
    Scaffold(topBar = {
        TopAppBar(title = {Text("Информация", fontSize = 22.sp)},
            navigationIcon = { IconButton(onClick = { backListener.invoke() }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Назад") } })
    }) {
       Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
           when {
               state.isPersonalLoading -> {
                   CircularProgressIndicator()
               }
               state.isPersonalError -> {
                   Text("error")
               }
               else -> {
                   state.personal?.let {
                       Personal(personal = it)
                   }
               }
           }
           when {
               state.isOrdersLoading -> {
                   CircularProgressIndicator()
               }
               state.isOrdersError -> {
                   Text("error")
               }
               else -> {
                   LazyColumn(Modifier.fillMaxWidth()) {
                       items(state.orders) {
                           Spacer(modifier = Modifier.padding(2.dp))
                           Order(order = it)
                       }
                   }
               }
           }
       }

    }
}

@Composable
fun Personal(personal: Personal) {
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(5.dp)) {
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (image, name) = createRefs()
            Text(personal.name, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(image.start)
                width = Dimension.fillToConstraints
            })
            Image(
                painter = rememberImagePainter(
                    data = personal.avatarUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(image) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
            )
        }
            IconText(personal.type.print(), Icons.Filled.Star)
            IconText(personal.faculty, Icons.Filled.Place)
            IconText(personal.direction, Icons.Filled.AccountCircle)
            IconText(personal.course.toString(), Icons.Filled.Place)
            IconText(personal.group, Icons.Filled.Person)
            IconText(personal.startDate?.format())
        }
    }
}

@Composable
fun Order(order: Order) {
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        ConstraintLayout(
            Modifier
                .padding(5.dp)
                .heightIn(min = 50.dp)) {
            val (number, date, info, icon) = createRefs()
            Text(text = "Приказ №${order.number}",
                modifier = Modifier.constrainAs(number) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(date.end)
                    width = Dimension.fillToConstraints
                })
            Text(text = order.date.format(),
                modifier = Modifier.constrainAs(date) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                })
            order.additionalInfo?.let {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "Информация",
                modifier = Modifier.padding(end = 8.dp, start = 10.dp)
                    .constrainAs(icon) {
                    start.linkTo(parent.start)
                    linkTo(top = number.bottom, bottom = parent.bottom, bias = 1f)
                })
                Text(text = order.additionalInfo.orEmpty(),
                    modifier = Modifier.constrainAs(info) {
                        start.linkTo(icon.end)
                        bottom.linkTo(icon.bottom)
                        top.linkTo(icon.top)
                    })
            }
        }
    }
}

@Composable
fun IconText(text: String?, icon: ImageVector? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(2.dp)) {
        text?.let {
            icon?.let {
                Icon(icon, "", modifier = Modifier.padding(end = 8.dp))
            }
            Text(it)
        }
    }

}
