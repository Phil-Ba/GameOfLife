package at.baya.gol

import com.typesafe.scalalogging.StrictLogging

import scala.math._

/**
	* Created by philba on 9/22/16.
	*/
class GolEngine extends StrictLogging {

	type Point = (Long, Long)


	def nextGeneration(currentGeneration: Seq[Point]): Seq[Point] = {
		logger.debug("currentGeneration: {}", currentGeneration.mkString)

		val possibleCells = currentGeneration.flatMap(neighbours)
		logger.debug("possibleCells: {}", possibleCells.mkString)

		val nextGeneration = possibleCells.filter(point => {
			val number = numberOfNeighbours(point, currentGeneration)
			number == 2 || number == 3
		})
		logger.debug("nextCells: {}", nextGeneration.mkString)
		nextGeneration
	}

	private[gol] def neighbours(point: Point): Seq[Point] = {
		val x = point._1
		val y = point._2
		for {
			xOffset <- -1 to 1
			yOffset <- -1 to 1
		} yield {
			(x + xOffset, y + yOffset)
		}
	}

	private[gol] def numberOfNeighbours(point: Point, points: Seq[Point]): Long = {
		points.count(areNeighbours(point, _))
	}

	private[gol] def areNeighbours(point1: Point, point2: Point): Boolean = {
		if (point1.equals(point2)) {
			return false
		}

		val x1 = point1._1
		val y1 = point1._2
		val x2 = point2._1
		val y2 = point2._2


		abs(x1 - x2) <= 1 && abs(y1 - y2) <= 1
	}

}
