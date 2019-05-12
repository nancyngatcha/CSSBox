package org.fit.cssbox.layout;

import cz.vutbr.web.css.TermList;

/**
 * This class represents a coordinates of grid items - row-start, row-end, column-start, column-end
 *
 * @author Ondra
 */
public class GridItemRowColumn {
    /**
     * row-start number
     */
    public int rowStart = 0;

    /**
     * row-end number
     */
    public int rowEnd = 0;

    /**
     * column-start number
     */
    public int columnStart = 0;

    /**
     * column-end number
     */
    public int columnEnd = 0;

    /**
     * row-start span
     */
    public TermList rowStartSpan = null;

    /**
     * row-end span
     */
    public TermList rowEndSpan = null;

    /**
     * column-start span
     */
    public TermList columnStartSpan = null;

    /**
     * column-end span
     */
    public TermList columnEndSpan = null;


    /**
     * Creates a new coordinates, initialized to zero.
     */
    public GridItemRowColumn() {

    }

    /**
     * Creates a new coordinates with the specified numbers.
     * @param rs row-start
     * @param re row-end
     * @param cs column-start
     * @param ce column-end
     */
    public GridItemRowColumn(int rs, int re, int cs, int ce) {
        rowStart = rs;
        rowEnd = re;
        columnStart = cs;
        columnEnd = ce;
    }

    /**
     * Creates a coordinates from an existing one.
     * @param src the source length set
     */
    public GridItemRowColumn(GridItemRowColumn src) {
        rowStart = src.rowStart;
        rowEnd = src.rowEnd;
        columnStart = src.columnStart;
        columnEnd = src.columnEnd;
    }


    /**
     * Returns a string representation of the grid item coordinates
     * @return A string in the [row-start, rowe-nd, column-start, column-end] format
     */
    @Override
    public String toString() {
        return "grid-row-start: " + rowStart + " grid-row-end: " + rowEnd + " grid-column-start: " + columnStart +  " grid-column-end: " + columnEnd;
    }
}
