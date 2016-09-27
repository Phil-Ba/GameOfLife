package at.baya.gol

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSpec
import org.scalatest.concurrent.Eventually

import scalafx.beans.property.BooleanProperty

/**
	* Created by philba on 9/26/16.
	*/
class GolCanvasTest extends FunSpec with MockFactory with Eventually {

	describe("When the drawable property changes") {
		val timesAddHandlerIsCalledDuringInitialization = 1

		def fixture = new {
			val addStub = stubFunction[Unit]("addStub")
			val removeStub = stubFunction[Unit]("removeStub")
			val cut = new GolCanvas {
				override private[gol] def addHandlers(): Unit = addStub()

				override private[gol] def removeHandlers(): Unit = removeStub()
			}
		}

		it("from the initial state to false, remove should be called once and add once") {
			val f = fixture
			val newValue = BooleanProperty(false)
			f.cut.drawable <== newValue

			f.removeStub verify() once()
			f.addStub verify() repeated timesAddHandlerIsCalledDuringInitialization times()
		}

		it("from the initial state to false and back to true again, remove should be called once and add once for the " +
			"initial change. For the second change only add should be called") {
			val f = fixture
			val initialDrawable = BooleanProperty(false)
			f.cut.drawable <== initialDrawable

			initialDrawable() = true
			inSequence {
				f.addStub verify() repeated timesAddHandlerIsCalledDuringInitialization times()
				f.removeStub verify() once()
				f.removeStub verify() never()
				f.addStub verify() once()
			}

		}
	}

}
