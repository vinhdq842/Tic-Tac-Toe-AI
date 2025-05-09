package com.quangvinh.tic.tac.toe

import java.awt.Color

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
     * Calculate benefit for this game state.
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
     * Copy the current state.
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
