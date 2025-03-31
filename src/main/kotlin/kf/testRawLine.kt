package kf

import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp.Capability


class RawLineReader {
    val term = TerminalBuilder.terminal()
    val curr: CharArray = CharArray(80)
    val nums = mutableListOf<Int>()
    var i: Int = 0

    fun get(): String {
        term.enterRawMode()
        var curCar: Int = 0
        while (true) {
            curCar = term.reader().read()


//            do { curCar = System.`in`.read() } while (curCar == -1)
            nums.add(curCar)
            when {
//                curCar == -1 -> break
                curCar == 13 -> break

                (curCar == 8) && i > 0 -> {
                    curr[i--] = ' '
                    print("\b \b")
                }

                curCar == 127 -> {
                    curr[i++] = '\u00c4'
                    print('\u00c4')
                }

//                (curCar == 127 || curCar == 8) && i > 0 -> {
//                    curr[i--] = ' '
//                    print("\b \b")
//                }

                curCar >= 32 && curCar < 127 -> {
                    curr[i++] = curCar.toChar().uppercaseChar()
                    print(curCar.toChar().uppercaseChar())
                }

                curCar == 27 -> {
                    bell()
                    var follows = mutableListOf<Int>()
                    do {
                        val thisChar = term.reader().read()
                        follows.add(thisChar)
                        if (thisChar == 'O'.code) {
                            follows.add(term.reader().read())
                            break
                        }
                    } while (thisChar != '~'.code && (thisChar !in 65..90))
                    println("Foll: ${follows}}")
                    when (follows) {
                        listOf(79,80) -> println("F1")
                        listOf(79,81) -> println("F2")
                        listOf(79,82) -> println("F3")
                        listOf(79,83) -> println("F4")
                        listOf(91, 49, 53, 126) -> println("F5")
                        listOf(91, 49, 55, 126) -> println("F6")
                        listOf(91, 49, 56, 126) -> println("F7")
                        listOf(91, 49, 57, 126) -> println("F8")
                        listOf(91, 49, 57, 126) -> println("F8")
                        listOf(91, 50, 48, 126) -> println("F9")
                        listOf(91, 51, 48, 126) -> println("F10")
                        listOf(91, 52, 48, 126) -> println("F11")
                        listOf(91, 53, 48, 126) -> println("F12")
                        listOf(91, 65) -> println("Up")
                        listOf(91, 66) -> println("Down")
                        listOf(91, 67) -> println("Right")
                        listOf(91, 68) -> println("Left")
                        listOf(91, 53, 126) -> println("PgUp")
                        listOf(91, 54, 126) -> println("PgDown")
                        listOf(91, 72) -> println("Home")
                        listOf(91, 70) -> println("End")
                    }
                }
                // shift = 49, 59, 50


                else -> bell()
            }
        }
        println("\n$nums")
        return curr.concatToString(0, i)
    }

    private fun bell() {
        term.puts(Capability.bell)
        term.flush()
    }
}

fun mainx() {
    val rl = RawLineReader()
    println(rl.get())

}