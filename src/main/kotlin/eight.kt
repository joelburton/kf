//import kotlin.time.TimeSource
//
//fun solveNQueens(n: Int): List<List<String>> {
//    val solutions = mutableListOf<List<String>>()
//    val queens = IntArray(n) // queens[row] = column
//
//    fun isSafe(row: Int, col: Int): Boolean {
//        for (prevRow in 0 until row) {
//            val prevCol = queens[prevRow]
//            if (prevCol == col || Math.abs(prevCol - col) == row - prevRow) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun backtrack(row: Int) {
//        if (row == n) {
//            solutions.add(queens.map { col ->
//                ".".repeat(n).replaceRange(col..col, "Q")
//            })
//            return
//        }
//
//        for (col in 0 until n) {
//            if (isSafe(row, col)) {
//                queens[row] = col
//                backtrack(row + 1)
//            }
//        }
//    }
//
//    backtrack(0)
//    return solutions
//}
//
//fun doit() {
//    val timeMarkCreated = TimeSource.Monotonic.markNow()
//
//    val solutions = solveNQueens(8)
////    solutions.forEach { solution ->
////        solution.forEach { row -> println(row) }
////        println()
////    }
//    println("Total solutions: ${solutions.size}")
//
//    println("Time elapsed: ${timeMarkCreated.elapsedNow()}")
//
//}
//fun main() {
//    for (i in 1..100) doit()
//}
