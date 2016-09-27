package at.baya.gol

import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.Eventually
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scalafx.beans.property.BooleanProperty

/**
	* Created by philba on 9/26/16.
	*/
@RunWith(classOf[JUnitRunner])
class GolCanvasTest extends FunSpec with MockFactory with Eventually with Matchers {

	def fixture = new {
		val cut = new GolCanvas
	}

	describe("When the drawable property changes") {
		val timesAddHandlerIsCalledDuringInitialization = 1

		def mockFixture = new {
			val addStub = stubFunction[Unit]("addStub")
			val removeStub = stubFunction[Unit]("removeStub")
			val cut = new GolCanvas {
				override private[gol] def addHandlers(): Unit = addStub()

				override private[gol] def removeHandlers(): Unit = removeStub()
			}
		}

		it("from the initial state to false, remove should be called once and add once") {
			val f = mockFixture
			val newValue = BooleanProperty(false)
			f.cut.drawable <== newValue

			f.removeStub verify() once()
			f.addStub verify() repeated timesAddHandlerIsCalledDuringInitialization times()
		}

		it("from the initial state to false and back to true again, remove should be called once and add once for the " +
			"initial change. For the second change only add should be called") {
			val f = mockFixture
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

	describe("When the canvas is initialized") {
		val cut = fixture.cut
		it("should be empty") {
			cut.getCells shouldBe empty
		}

		describe("When a cell is plotted on an empty canvas") {
			val x = 2
			val y = 3
			val cut = fixture.cut

			it("it should afterwards be the only cell on the canvas") {
				cut.plotCell(x, y)
				cut.getCells should contain only ((x, y))
			}

			describe("When the same cell is removed") {
				it("the canvas should be empty again") {
					cut.removeCell(x, y)
					cut.getCells shouldBe empty
				}
			}
		}

		describe("When a number of cells are plotted on an empty canvas") {
			val cut = fixture.cut
			val cells = Seq[(Long, Long)]((1, 4), (55, 23), (43, 123))

			they("should all be present on the canvas afterwards") {
				cut.plotCells(cells)
				cut.getCells should contain theSameElementsAs cells
			}
		}

		describe("After drawing on a canvas") {
			val cut = fixture.cut
			cut.plotCells(Seq[(Long, Long)]((23, 45), (78, 902)))

			describe("if you reset the canvas") {
				cut.reset()

				it("should be empty") {
					cut.getCells shouldBe empty
				}
			}
		}

	}

}
