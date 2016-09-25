package at.baya.gol

import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.BooleanProperty
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color._
import scalafx.scene.{Group, Scene}
import scalafx.util.Duration

/**
	* Created by philba on 9/4/16.
	*/
object GolController extends JFXApp {

	stage = new PrimaryStage {
		title = "Game of Life"
		width = 800
		height = 600
		resizable = false

		var running = BooleanProperty(false)
		val cellCanvas = new GolCanvas
		val engine = new GolEngine

		cellCanvas.drawable <== !running

		val timeline = new Timeline {
			cycleCount = Timeline.Indefinite
			keyFrames = KeyFrame(Duration(300), onFinished = (e: ActionEvent) => {
				val nextGeneration = engine.nextGeneration(cellCanvas.getCells)
				cellCanvas.reset()
				cellCanvas.plotCells(nextGeneration)
			})
		}

		scene = new Scene {
			fill = Black
			root = new BorderPane {

				top = new ToolBar {

					val playBtn = new Button {
						text = "Start"
						disable <== running
						handleEvent(ActionEvent.Any) { (e: ActionEvent) =>
							timeline.play()
							running() = true
						}
					}

					val stopBtn = new Button {
						text = "Stop"
						disable <== !running
						handleEvent(ActionEvent.Any) { (e: ActionEvent) =>
							timeline.stop()
							running() = false
						}
					}

					val resetBtn = new Button {
						text = "Reset"
						handleEvent(ActionEvent.Any) { (e: ActionEvent) =>
							timeline.stop()
							cellCanvas.reset()
							running() = false
						}
					}

					content = List(playBtn, stopBtn, resetBtn)

				}

				center = new Group {
					cellCanvas.width <== width
					cellCanvas.height <== height
					children = cellCanvas
				}
			}
		}
	}

}
