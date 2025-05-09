package com.quangvinh.tic.tac.toe

import java.awt.Color
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.Vector
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.WindowConstants

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
    private val jFrame: JFrame = JFrame("Tic-Tac-Toe")

    /**
     * X icon.
     */
    private val xIcon = ImageIcon(javaClass.getResource("/images/x.png"))

    /**
     * O icon.
     */
    private val oIcon = ImageIcon(javaClass.getResource("/images/o.png"))

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

        for (i in 0..2) for (j in 0..2) contentPane.add(buttons[i][j])

        jFrame.isVisible = true
    }

    private fun updateButtons() {
        for (i in 0..2) for (j in 0..2) buttons[i][j].run {
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

                // Perform computer's turn, 80% is a "smart" move
                if ((1..5).random() != 1) {
                    for (next in gameState.nextStates) if (gameState.benefit + 1 == next.benefit) {
                        nextStates.addElement(next)
                    }
                } else if (gameState.nextStates.size > 0) nextStates.addElement(gameState.nextStates[(0 until gameState.nextStates.size).random()])

                if (nextStates.size > 0) gameState =
                    nextStates.elementAt((0 until nextStates.size).random())
                updateButtons()
            }
        }

        // Check current game state
        val currentState = gameState.calculateBenefit(buttons)
        if (currentState != -1) {
            if (JOptionPane.showConfirmDialog(
                    null, when (currentState) {
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
