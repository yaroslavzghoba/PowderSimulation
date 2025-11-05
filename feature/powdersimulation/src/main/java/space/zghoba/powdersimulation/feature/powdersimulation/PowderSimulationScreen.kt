package space.zghoba.powdersimulation.feature.powdersimulation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import space.zghoba.powdersimulation.core.model.Board
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.RectCoordinates
import space.zghoba.powdersimulation.feature.powdersimulation.utils.PowderSimulationConstants
import space.zghoba.powdersimulation.feature.powdersimulation.utils.denormaliseTo
import space.zghoba.powdersimulation.feature.powdersimulation.utils.normaliseFrom
import space.zghoba.powdersimulation.feature.powdersimulation.utils.toColor
import kotlin.math.roundToInt


@Composable
fun PowderSimulationScreen(
    viewModel: PowderSimulationViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .defaultMinSize(
                minWidth = PowderSimulationConstants.SCREEN_MIN_WIDTH,
                minHeight = PowderSimulationConstants.SCREEN_MIN_HEIGHT,
            )
    ) {
        PowderSimulationBoard(
            board = uiState.value.board,
            onSetMaterial = { coordinates ->
                val event = PowderSimulationEvent.SetMaterialAt(coordinates)
                viewModel.onEvent(event = event)
            },
            modifier = Modifier
                .weight(1f)
                .requiredSize(
                    width = PowderSimulationConstants.BOARD_CELL_MIN_SIZE
                            * PowderSimulationConstants.BOARD_WIDTH,
                    height = PowderSimulationConstants.BOARD_CELL_MIN_SIZE
                            * PowderSimulationConstants.BOARD_HEIGHT,
                )
                .border(color = Color.Red, width = 2.dp),
        )
        MaterialSelectionBar(
            materials = uiState.value.materials,
            selectedMaterialIndex = uiState.value.selectedMaterialIndex,
            onSelectMaterial = { index ->
                val event = PowderSimulationEvent.SelectMaterial(index = index)
                viewModel.onEvent(event = event)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(PowderSimulationConstants.MATERIAL_BUTTON_HEIGHT)
        )
    }
}


@Composable
private fun PowderSimulationBoard(
    board: Board,
    onSetMaterial: (coordinates: RectCoordinates) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: Implement board dragging
    // TODO: Implement board zooming

    val cellSizeDp = PowderSimulationConstants.BOARD_CELL_MIN_SIZE
    val cellSize = with(LocalDensity.current) {
        Size(width = cellSizeDp.toPx(), height = cellSizeDp.toPx())
    }
    var canvasSize: Size? by remember { mutableStateOf(null) }

    Canvas(
        modifier = modifier
            .pointerInput(board.width, board.height) {
                detectDragGestures { change, _ ->
                    if (canvasSize == null) return@detectDragGestures

                    val cellPositionX = change.position.x
                        .normaliseFrom(min = 0f, max = canvasSize!!.width)
                        .denormaliseTo(min = 0f, max = board.width.toFloat() - 1)
                        .roundToInt()
                    val cellPositionY = board.height - 1 - change.position.y
                        .normaliseFrom(min = 0f, max = canvasSize!!.height)
                        .denormaliseTo(min = 0f, max = board.height.toFloat() - 1)
                        .roundToInt()
                    val cellCoordinates = RectCoordinates(x = cellPositionX, y = cellPositionY)
                    onSetMaterial(cellCoordinates)
                }
            }
            .onGloballyPositioned { layoutCoordinates ->
                canvasSize = layoutCoordinates.size.toSize()
            }
    ) {
        (board.height - 1 downTo 0).forEach { y ->
            (0 until board.width).forEach { x ->
                val cellCoordinates = RectCoordinates(x, y)
                val material = board.getCell(cellCoordinates)
                val topLeftOffset = Offset(
                    x = cellSize.width * x,
                    y = canvasSize!!.height - cellSize.height * (y + 1),
                )
                drawRect(
                    color = material.color.toColor(),
                    topLeft = topLeftOffset,
                    size = cellSize,
                )
            }
        }
    }
}

@Composable
private fun MaterialSelectionBar(
    materials: List<Material>,
    selectedMaterialIndex: Int,
    onSelectMaterial: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = PowderSimulationConstants.MATERIAL_BUTTON_HEIGHT)
            .horizontalScroll(state = rememberScrollState())
    ) {
        materials.forEachIndexed { index, material ->
            val onSelect: () -> Unit = { onSelectMaterial(index) }
            MaterialButton(
                material = material,
                selected = index == selectedMaterialIndex,
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun MaterialButton(
    material: Material,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderWidth = when (selected) {
        true -> PowderSimulationConstants.SELECTED_MATERIAL_BORDER_WIDTH
        false -> PowderSimulationConstants.UNSELECTED_MATERIAL_BORDER_WIDTH
    }
    val borderColor = when (selected) {
        true -> PowderSimulationConstants.SELECTED_MATERIAL_BORDER_COLOR
        false -> PowderSimulationConstants.UNSELECTED_MATERIAL_BORDER_COLOR
    }

    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = PowderSimulationConstants.MATERIAL_BUTTON_WIDTH,
                minHeight = PowderSimulationConstants.MATERIAL_BUTTON_HEIGHT,
            )
            .background(color = material.color.toColor())
            .border(border = BorderStroke(width = borderWidth, color = borderColor))
            .clickable(
                // TODO: Provide a value for the "onClickLabel" parameter
                role = Role.Button,
                onClick = onSelect,
            )
    )
}