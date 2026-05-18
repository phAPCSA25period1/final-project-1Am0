/**
 * Represents a single square on the Minesweeper board.
 *
 * Each square tracks:
 * - Whether it contains a mine
 * - Number of adjacent mines (value)
 * - Whether it is revealed or hidden
 * - Whether it is flagged by the player
 */
public class Square {
    private boolean isMine;
    private int value;
    private boolean isHidden;
    private boolean isFlagged;

    /**
     * Constructs a new Square representing an empty, unrevealed board square.
     * Initializes all properties to default values:
     * - Not a mine
     * - Value -1 (uninitialized)
     * - Hidden (not yet revealed)
     * - Not flagged
     */
    public Square() {
        isMine = false;
        value = -1;
        isHidden = true;
    }

    /**
     * Gets the number of adjacent mines for this square.
     * Returns -1 if this is a mine or if the value hasn't been set yet.
     *
     * @return the number of adjacent mines (0-8), or -1 if this is a mine
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the number of adjacent mines for this square.
     * Should only be called on non-mine squares during board initialization.
     *
     * @param _val the number of adjacent mines to set
     */
    public void setValue(int _val) {
        value = _val;
    }

    /**
     * Checks if this square contains a mine.
     *
     * @return true if this square is a mine, false otherwise
     */
    public boolean getIsMine() {
        return isMine;
    }

    /**
     * Marks this square as containing a mine.
     * Should only be called during game initialization.
     */
    public void setMine() {
        isMine = true;
    }

    /**
     * Checks if this square is hidden (not yet revealed).
     *
     * @return true if the square is hidden, false if revealed
     */
    public boolean getHidden() {
        return isHidden;
    }

    /**
     * Reveals this square, making it visible to the player.
     * Should be called when the player clicks on a safe square.
     */
    public void unhide() {
        isHidden = false;
    }

    /**
     * Returns a string representation of this square for debug output.
     * Symbols:
     * - 'F' if flagged and hidden
     * - '#' if hidden and not flagged
     * - 'X' if revealed and is a mine
     * - '0'-'8' if revealed with that many adjacent mines
     *
     * @return a single-character string representing this square's state
     */
    // For debugging
    public String toString() {
        if (isFlagged && isHidden) {
            return "F";
        }
        if (isHidden) {
            return "#";
        } else if (isMine) {
            return "X";
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * Checks if this square has been flagged by the player.
     *
     * @return true if flagged, false otherwise
     */
    public boolean isFlagged() {
        return isFlagged;
    }

    /**
     * Flags this square, marking it as a suspected mine.
     * Should only be called on hidden squares.
     */
    public void flag() {
        isFlagged = true;
    }

    /**
     * Removes the flag from this square.
     * Can be called to correct a mistaken flag placement.
     */
    public void unflag() {
        isFlagged = false;
    }
}
