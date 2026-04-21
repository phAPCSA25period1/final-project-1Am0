public class Square {
    private boolean isMine;
    private int value;
    private boolean isHidden;

    public Square()
    {
        isMine = false;
        value = -1;
        isHidden = true;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int _val)
    {
        value = _val;
    }

    public boolean getIsMine()
    {
        return isMine;
    }

    public void setMine()
    {
        isMine = true;
    }

    public boolean getHidden()
    {
        return isHidden;
    }

    public void unhide()
    {
        isHidden = false;
    }

    // For debugging
    public String toString()
    {
        if (isHidden)
        {
            return "#";
        }
        else if (isMine)
        {
            return "X";
        }
        else
        {
            return String.valueOf(value);
        }
    }
}
