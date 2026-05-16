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

    public Square() {
        isMine = false;
        value = -1;
        isHidden = true;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int _val) {
        value = _val;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public void setMine() {
        isMine = true;
    }

    public boolean getHidden() {
        return isHidden;
    }

    public void unhide() {
        isHidden = false;
    }

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

    public boolean isFlagged() {
        return isFlagged;
    }

    public void flag() {
        isFlagged = true;
    }

    public void unflag() {
        isFlagged = false;
    }
}
