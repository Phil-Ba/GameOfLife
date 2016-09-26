package at.baya.gol

import org.junit.runner.RunWith
import org.scalacheck.Gen
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSpec, Matchers}

import scala.math._
import scala.util.Random

/**
	* Created by philba on 9/22/16.
	*/
@RunWith(classOf[JUnitRunner])
class GolEngineTest extends FunSpec with PropertyChecks with Matchers {

	val cut = new GolEngine

	def pointList(points: Long*) = {
		require(points.size % 2 == 0)
		points
			.sliding(2, 2)
			.collect({ case Seq(a, b) => (a, b) })
			.foldLeft(List.empty[(Long, Long)])((list, current) => current :: list)
	}

	val neighbourOffsetGen = for {
		xOffset <- Gen.oneOf(-1, 0, 1)
		yOffset <- Gen.oneOf(-1, 0, 1)
		if xOffset != 0 || yOffset != 0
	} yield {
		(xOffset, yOffset)
	}

	val nonNeighbourOffsetGen = for {
		xOffset <- Gen.choose(-100, 100)
		yOffset <- Gen.choose(-100, 100)
		if abs(xOffset) > 1 || abs(yOffset) > 1
	} yield {
		(xOffset, yOffset)
	}

	describe("For the method areNeighbours") {
		describe("two points") {

			describe("are neighbours if their X and/or Y coordinates differ by exactly 1") {

				it("a point is not neighbour with itself") {
					val result = cut.areNeighbours((50, 50), (50, 50))
					assert(result == false)
				}

				they("(50,50) and (51,51) should be neighbours") {
					val result = cut.areNeighbours((50, 50), (51, 51))
					assert(result == true)
				}

				they("(50,50) and (50,49) should be neighbours") {
					val result = cut.areNeighbours((50, 50), (50, 49))
					assert(result == true)
				}

				they("(50,50) and (51,50) should be neighbours") {
					val result = cut.areNeighbours((50, 50), (50, 49))
					assert(result == true)
				}

				they("(50,500) and (51,50) should be not neighbours") {
					val result = cut.areNeighbours((50, 500), (50, 49))
					assert(result == false)
				}

				they("(500,50) and (50,49) should not be neighbours") {
					val result = cut.areNeighbours((500, 50), (50, 49))
					assert(result == false)
				}

			}
		}
	}

	describe("The method areNeighbours") {
		describe("for all") {

			def pointGen(offsetGen: Gen[(Int, Int)]) = {
				for {
					x1 <- Gen.choose[Long](0, 500)
					y1 <- Gen.choose[Long](0, 500)
					offsets <- offsetGen
				} yield {
					val cell = (x1, y1)
					val cellNeighbour = (x1 + offsets._1, y1 + offsets._2)
					(cell, cellNeighbour)
				}
			}

			describe("invalid points") {
				it("should return false") {

					val nonNeighbourGen = pointGen(nonNeighbourOffsetGen)

					forAll(nonNeighbourGen) {
						(neighbours) => {
							val result = cut.areNeighbours(neighbours._1, neighbours._2)
							assert(result == false)
						}
					}
				}
			}

			describe("valid points") {
				it("should return true") {

					val neighbourGen = pointGen(neighbourOffsetGen)

					forAll(neighbourGen) {
						(neighbours) => {
							val result = cut.areNeighbours(neighbours._1, neighbours._2)
							assert(result == true)
						}
					}
				}
			}
		}

		describe("The method numberOfNeighbours") {

			it("should return 3 neighbours") {
				val result = cut.numberOfNeighbours((50, 50), pointList(50, 51, 49, 51, 51, 51, 52, 52, 48, 48, 48, 50, 52,
					49))
				assert(result == 3)
			}

			it("should return 0 neighbours") {
				val result = cut
					.numberOfNeighbours((50, 50), pointList(52, 52, 48, 48, 48, 50, 52, 49, 100, 50, 51, 100, 51, 52))
				assert(result == 0)
			}

			describe("for all points") {
				val pointsGen = for {
					startingPoint <- Gen.listOfN[Long](2, Gen.choose(1L, 500L)).collect({ case List(a, b) => (a, b) })
					neighbours <- Gen.choose(0, 100)
					nonNeighbours <- Gen.choose(0, 100)
					neighbourOffsets <- Gen.listOfN(neighbours, neighbourOffsetGen)
					nonNeighbourOffsets <- Gen.listOfN(nonNeighbours, nonNeighbourOffsetGen)
				} yield {
					val x = startingPoint._1
					val y = startingPoint._2
					var points = List.empty[(Long, Long)]
					points = points ::: nonNeighbourOffsets.map(tuple => (tuple._1 + x, tuple._2 + y))
					points = points ::: neighbourOffsets.map(tuple => (tuple._1 + x, tuple._2 + y))
					points = Random.shuffle(points)
					(startingPoint, points, neighbours)
				}

				it("should return the correct amount of neighbours") {
					forAll(pointsGen, maxDiscarded(10000)) {
						(testData) => {
							val startingPoint = testData._1
							val points = testData._2
							val neighbours = testData._3
							val result = cut.numberOfNeighbours(startingPoint, points)
							assert(result == neighbours)
						}
					}
				}

			}
		}
	}

	describe("The method neighbours") {
		it("should return the neighbours of a cell") {
			val neighbours = cut.neighbours((50, 50))
			neighbours should contain theSameElementsAs List(
				(49, 49), (49, 50), (49, 51),
				(50, 49), (50, 51),
				(51, 49), (51, 50), (51, 51)
			)
		}
	}

	describe("The method next generation") {
		describe("for a blinker") {
			val blinker = pointList(
				1, 5,
				2, 5,
				3, 5)
			val blinkerAlternate = pointList(
				2, 4,
				2, 5,
				2, 6)
			it("should make the blinker alternate between its two forms") {
				var current: Seq[(Long, Long)] = blinker
				for (i <- 0 to 100) {
					current = cut.nextGeneration(current)
					i % 2 match {
						case 0 => current should contain theSameElementsAs blinkerAlternate
						case 1 => current should contain theSameElementsAs blinker
					}
				}
			}
		}

		describe("for a block") {
			val block = pointList(
				1, 1,
				1, 2,
				2, 1,
				2, 2)
			it("should always return the block") {
				var current: Seq[(Long, Long)] = block
				for (i <- 0 to 100) {
					current = cut.nextGeneration(current)
					current should contain theSameElementsAs block
				}
			}
		}
	}

}
