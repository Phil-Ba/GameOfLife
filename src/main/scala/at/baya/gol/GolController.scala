package at.baya.gol

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color._
import scalafx.scene.{Group, Scene}

/**
	* Created by philba on 9/4/16.
	*/
object GolController extends JFXApp {

	stage = new PrimaryStage {
		title = "ScalaFX Hello World"
		val cellCanvas = new GolCanvas
		//		width = 800
		//		height = 600
		resizable = false

		scene = new Scene {
			fill = Black
			root = new BorderPane {
				center = new Group {
					children = cellCanvas
				}
			}
		}
	}

}
