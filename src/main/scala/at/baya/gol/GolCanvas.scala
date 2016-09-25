package at.baya.gol

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import com.typesafe.scalalogging.StrictLogging

import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color

/**
	* Created by philba on 9/8/16.
	*/
class GolCanvas extends Canvas with StrictLogging {


	private val CellSize = 10
	private var cells: Set[(Long, Long)] = Set.empty
	private val gc = graphicsContext2D
	val drawable = BooleanProperty(true)
	gc.fill = Color.DarkGray

	width = 800
	height = 600

	def reset() = {
		cells = Set.empty
		gc.clearRect(0, 0, width.value, height.value)
	}

	def getCells = cells.toList

	private val clickEventHandler = new EventHandler[MouseEvent] {
		override def handle(event: MouseEvent): Unit = {
			val x = ((event.x - (event.x % CellSize)) / CellSize).toLong
			val y = ((event.y - (event.y % CellSize)) / CellSize).toLong

			if (cells.contains((x, y))) {
				removeCell(x, y)
			}
			else {
				plotCell(x, y)
			}
		}
	}

	private def addHandlers() = {
		addEventHandler(MouseEvent.MOUSE_CLICKED, clickEventHandler)
		addEventHandler(MouseEvent.MOUSE_DRAGGED, clickEventHandler)
	}

	private def removeHandlers() = {
		removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEventHandler)
		removeEventHandler(MouseEvent.MOUSE_DRAGGED, clickEventHandler)
	}

	addHandlers()

	drawable.onChange((_, _, newValue) => {
		if (newValue) {
			addHandlers()
		} else {
			removeHandlers()
		}
	})

	def plotCell(x: Long, y: Long): Unit = {
		gc.fillRect(x * CellSize, y * CellSize, CellSize, CellSize)
		cells = cells + ((x, y))
	}

	def plotCells(cells: Seq[(Long, Long)]) = cells.foreach(cell => plotCell(cell._1, cell._2))

	def removeCell(x: Long, y: Long) = {
		//		for some reason clear works differently then fill
		gc.clearRect(x * CellSize + 1, y * CellSize + 1, CellSize - 1, CellSize - 1)
		cells = cells - ((x, y))
	}

}
