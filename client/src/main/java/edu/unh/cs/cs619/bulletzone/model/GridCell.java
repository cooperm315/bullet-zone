package edu.unh.cs.cs619.bulletzone.model;

import android.graphics.drawable.Drawable;

public class GridCell {
    protected long val;
    Drawable background;
    Drawable foreground;
    Drawable middleGround;
    protected String cellType;


    public GridCell(long val, Drawable background, Drawable foreground, String cellType) {
        this.val = val;
        this.background = background;
        this.foreground = foreground;
        this.cellType = cellType;
    }

    public GridCell() {
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public void setVal(long val) {
        this.val = val;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void setForeground(Drawable foreground) {
        this.foreground = foreground;
    }

    public void setMiddleGround(Drawable middleGround) {this.middleGround = middleGround;}

    /**
     *
     * @return the background of the image
     */
    public Drawable getBackground() {return background;}

    /**
     *
     * @return the foreground of the image
     */
    public Drawable getForeground() {return foreground;}

    /**
     *
     * @return the middle ground of the image (structure)
     */
    public Drawable getMiddleGround() {return middleGround;}

    /**
     *
     * @return The cell type as a string associated with the grid (determined by GridCellFactory)
     */
    public String getCellType() {return cellType;}

    /**
     *
     * @return The integer value pulled from the server that is used to determine which cell type it is
     */
    public long getVal() {return val;}
}
