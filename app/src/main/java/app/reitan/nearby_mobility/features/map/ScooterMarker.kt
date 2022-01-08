package app.reitan.nearby_mobility.features.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.reitan.shared.models.Operator
import kotlin.math.pow
import kotlin.math.sqrt

@Preview
@Composable
fun ScooterMarker() {
    Canvas(Modifier.size(240.dp, 260.dp)) {
        val (width, height) = size
        val radius = width / 2
        val arrowWidth = width / 5
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
            quadraticBezierTo(radius, height + pathRadius, radius + pathRadius, height - pathRadius)
            lineTo(radius + arrowWidth / 2 - pathRadius * 2, y + pathRadius)
            relativeQuadraticBezierTo(pathRadius, -pathRadius, pathRadius * 2, -pathRadius)
            close()
        }
        drawPath(
            path = path,
            color = Color.White,
        )
    }
}

object OperatorParameterProvider : CollectionPreviewParameterProvider<Operator>(
    listOf(
        Operator.Ryde,
        Operator.Tier,
        Operator.Voi,
        Operator.Unknown,
    )
)
