package com.quangvinh.tic.tac.toe

import java.awt.Color
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import kotlin.collections.ArrayList

/**
 * @author ServantOfEvil
 */

fun main() {
    Game()
}

class Game : ActionListener {

    /**
     * Game board.
     */
    private var buttons: Array<Array<JButton>>

    /**
     * Current game state.
     */
    private var gameState: GameState

    /**
     * turn.
     */
    private var turn: Int = 0

    /**
     * Main frame.
     */
    private val jFrame: JFrame = JFrame("Tic tac toe")

    /**
     * X icon.
     */
    private val xIcon = ImageIcon("".javaClass.getResource("/images/x.png"))

    /**
     * O icon.
     */
    private val oIcon = ImageIcon("".javaClass.getResource("/images/o.png"))

    init {
        jFrame.setBounds(200, 200, 400, 300)
        jFrame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        val contentPane = JPanel()
        contentPane.layout = GridLayout(3, 3)
        jFrame.contentPane = contentPane

        gameState = GameState()

        // Init game board...
        buttons = Array(3) { i ->
            Array(3) { j ->
                JButton(
                    buttonIcon(i, j), i, j
                ).apply {
                    background = Color.WHITE
                }.also { it.addActionListener(this) }
            }
        }

        for (i in 0..2)
            for (j in 0..2)
                contentPane.add(buttons[i][j])

        jFrame.isVisible = true
    }

    private fun updateButtons() {
        for (i in 0..2)
            for (j in 0..2)
                buttons[i][j].run {
                    icon = buttonIcon(i, j)
                    background = Color.WHITE
                }
    }

    private fun buttonIcon(i: Int, j: Int) = when (gameState.state[i][j]) {
        100 -> xIcon
        -100 -> oIcon
        else -> null
    }

    override fun actionPerformed(e: ActionEvent?) {

        if (gameState.calculateBenefit(null) == -1) {
            val button: JButton = e?.source as JButton

            // The game is uncompleted
            if (gameState.state[button.row][button.col] == 0) {

                // Perform player's turn
                gameState.state[button.row][button.col] = if (turn and 1 == 1) -100 else 100
                for (next in gameState.nextStates) if (gameState == next) {
                    gameState = next
                    break
                }

                val nextStates: Vector<GameState> = Vector()

                // Perform computer's turn, 60% is a "smart" move
                if ((1..3).random() != 1) {
                    for (next in gameState.nextStates) if (gameState.benefit + 1 == next.benefit) {
                        nextStates.addElement(next)
                    }
                } else if (gameState.nextStates.size > 0) nextStates.addElement(gameState.nextStates[(0 until gameState.nextStates.size).random()])

                if (nextStates.size > 0) gameState = nextStates.elementAt((0 until nextStates.size).random())
                updateButtons()
            }
        }

        // Check current game state
        val currentState = gameState.calculateBenefit(buttons)
        if (currentState != -1) {
            if (JOptionPane.showConfirmDialog(
                    null,
                    when (currentState) {
                        -100 -> "You lose!"
                        100 -> "You win!"
                        else -> "Draw!"
                    }.plus(" Play again?"), "Notification", JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION
            ) {
                gameState = GameState()
                updateButtons()
            }
        }

    }

}

class GameState(
    val state: Array<IntArray> = arrayOf(
        intArrayOf(0, 0, 0),
        intArrayOf(0, 0, 0),
        intArrayOf(0, 0, 0)
    ),
    depth: Int = 0
) {

    val nextStates: ArrayList<GameState> = ArrayList()
    var benefit: Int = 0

    init {
        benefit = calculateBenefit(null)
        if (benefit == -1) {
            // Calculate next states
            for (i in state.indices) {
                for (j in state[0].indices)
                    if (state[i][j] == 0) {
                        // Add a new state
                        val newStatus = copyState()
                        newStatus[i][j] = if (depth and 1 == 0) 100 else -100
                        nextStates.add(GameState(newStatus, depth + 1))
                    }
            }
            updateBenefit(
                if (depth and 1 == 0) {
                    // Max
                    { a, b -> a < b }
                } else {
                    // Min
                    { a, b -> a >= b }
                }
            )
        }

    }

    private fun updateBenefit(compare: (a: Int, b: Int) -> Boolean) {
        benefit = nextStates[0].benefit
        for (nextState in nextStates) {
            if (compare(benefit, nextState.benefit)) benefit = nextState.benefit
        }
        benefit -= 1
    }

    /**
     * Calculate benefit for this Game state.
     * @param buttons to be highlighted in last state.
     */
    fun calculateBenefit(buttons: Array<Array<JButton>>?): Int {
        for (i in state.indices) {
            if (state[i][0] == state[i][1] && state[i][1] == state[i][2] && state[i][1] != 0) {
                buttons?.run {
                    get(i)[0].background = Color.ORANGE
                    get(i)[1].background = Color.ORANGE
                    get(i)[2].background = Color.ORANGE
                }
                return state[i][1]
            } else if (state[0][i] == state[1][i] && state[1][i] == state[2][i] && state[1][i] != 0) {
                buttons?.run {
                    get(0)[i].background = Color.ORANGE
                    get(1)[i].background = Color.ORANGE
                    get(2)[i].background = Color.ORANGE
                }
                return state[1][i]
            }
        }

        if (state[0][0] == state[1][1] && state[1][1] == state[2][2] && state[1][1] != 0) {
            buttons?.run {
                get(0)[0].background = Color.ORANGE
                get(1)[1].background = Color.ORANGE
                get(2)[2].background = Color.ORANGE
            }
            return state[1][1]
        }

        if (state[0][2] == state[1][1] && state[1][1] == state[2][0] && state[1][1] != 0) {
            buttons?.run {
                get(0)[2].background = Color.ORANGE
                get(1)[1].background = Color.ORANGE
                get(2)[0].background = Color.ORANGE
            }
            return state[1][1]
        }

        for (i in state.indices)
            for (j in state[0].indices)
                if (state[i][j] == 0) return -1

        return 0
    }

    /**
     * Copy current state.
     */
    private fun copyState(): Array<IntArray> {
        val rs = arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        )

        for (i in state.indices) {
            for (j in state[0].indices)
                rs[i][j] = state[i][j]
        }

        return rs
    }

    override fun equals(other: Any?): Boolean {
        if (other is GameState) {
            for (i in state.indices) {
                for (j in state[0].indices)
                    if (other.state[i][j] != state[i][j]) return false
            }
            return true
        }

        return false
    }

    override fun hashCode(): Int {
        var result = state.contentDeepHashCode()
        result = 31 * result + nextStates.hashCode()
        result = 31 * result + benefit
        return result
    }

}

class JButton(icon: ImageIcon?, var row: Int, var col: Int) : javax.swing.JButton(icon)