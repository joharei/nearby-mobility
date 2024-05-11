package app.reitan.nearby_mobility.features.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.reitan.common.models.Operator
import app.reitan.nearby_mobility.R
import kotlin.math.pow
import kotlin.math.sqrt

@Preview
@Composable
fun ScooterMarker(@PreviewParameter(OperatorParameterProvider::class) operator: Operator) {
    Box {
        Canvas(Modifier.size(24.dp, 27.dp)) {
            val (width, height) = size
            val radius = width / 2
            val arrowWidth = width / 3
            val pathRadius = arrowWidth / 8.5f

            drawCircle(
                color = Color.White,
                radius = radius,
                center = Offset(radius, radius),
            )

            val path = Path().apply {
                val y = radius + sqrt(radius.pow(2) - (arrowWidth / 2).pow(2))
                moveTo(radius - arrowWidth / 2, y)
                relativeQuadraticBezierTo(pathRadius, 0f, pathRadius * 2, pathRadius)
                lineTo(radius - pathRadius, height - pathRadius)
                quadraticBezierTo(
                    radius, height + pathRadius, radius + pathRadius, height - pathRadius
                )
                lineTo(radius + arrowWidth / 2 - pathRadius * 2, y + pathRadius)
                relativeQuadraticBezierTo(pathRadius, -pathRadius, pathRadius * 2, -pathRadius)
                close()
            }
            drawPath(
                path = path,
                color = Color.White,
            )
        }

        val color = when (operator) {
            Operator.Ryde -> Color(0xFF6DB85E)
            Operator.Tier -> Color(0xFF00103C)
            Operator.Voi -> Color(0xFFF46C62)
            else -> Color.Transparent
        }
        val icon = when (operator) {
            Operator.Ryde -> R.drawable.ic_ryde
            Operator.Tier -> R.drawable.ic_tier
            Operator.Voi -> R.drawable.ic_voi
            else -> null
        }

        if (icon != null) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .padding(3.dp)
                    .background(color = color, shape = CircleShape)
                    .padding(2.5.dp),
                painter = painterResource(icon),
                contentDescription = null,
            )
        }
    }
}

class OperatorParameterProvider : CollectionPreviewParameterProvider<Operator>(
    listOf(
        Operator.Ryde,
        Operator.Tier,
        Operator.Voi,
        Operator.Unknown,
    )
)
