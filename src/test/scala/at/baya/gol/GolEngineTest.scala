package at.baya.gol

import org.junit.runner.RunWith
import org.scalacheck.Gen
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks

/**
	* Created by philba on 9/22/16.
	*/
@RunWith(classOf[JUnitRunner])
class GolEngineTest extends FunSpec with PropertyChecks {

	val cut = new GolEngine

	describe("Two points") {

		describe("are neighbours if their X and/or Y coordinates differ by exactly 1") {

			they("(50,50) and (51,51) should be neighbours") {
				val result = cut.areNeighbours((50, 50), (51, 51))
				assert(result == true)
			}

			they("(50,50) and (50,49) should be neighbours") {
				val result = cut.areNeighbours((50, 50), (50, 49))
				assert(result == true)
			}

			they("they should be neighbours for all valid points") {
				val offsetGen = for {
					xOffset <- Gen.oneOf(-1, 0, 1)
					yOffset <- Gen.oneOf(-1, 0, 1)
					if xOffset != 0 || yOffset != 0
				} yield {
					(xOffset, yOffset)
				}

				val neighbourGen = for {
					x1 <- Gen.choose[Long](0, 500)
					y1 <- Gen.choose[Long](0, 500)
					offsets <- offsetGen
				} yield {
					val cell = (x1, y1)
					val cellNeighbour = (x1 + offsets._1, y1 + offsets._2)
					(cell, cellNeighbour)
				}

				forAll(neighbourGen) {
					(neighbours) => {
						val result = cut.areNeighbours(neighbours._1, neighbours._2)
						assert(result == true)
					}
				}
			}
		}

	}
}
