package at.baya.gol

import scala.math._

/**
	* Created by philba on 9/22/16.
	*/
class GolEngine {

	type Point = (Long, Long)

	private[gol] def areNeighbours(point1: Point, point2: Point) = {
		val x1 = point1._1
		val y1 = point1._2
		val x2 = point2._1
		val y2 = point2._2
		abs(x1 - x2) == 1 || abs(y1 - y2) == 1
	}

}
