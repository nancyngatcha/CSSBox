package org.fit.cssbox.layout;

import cz.vutbr.web.css.TermList;

public class GridItemRowColumn {

    public int rowStart = 0;

    public int rowEnd = 0;

    public int columnStart = 0;

    public int columnEnd = 0;

    public TermList rowStartSpan = null;

    public TermList rowEndSpan = null;

    public TermList columnStartSpan = null;

    public TermList columnEndSpan = null;

    public GridItemRowColumn() {

    }

    public GridItemRowColumn(int rs, int re, int cs, int ce) {
        rowStart = rs;
        rowEnd = re;
        columnStart = cs;
        columnEnd = ce;
    }

    public GridItemRowColumn(GridItemRowColumn src) {
        rowStart = src.rowStart;
        rowEnd = src.rowEnd;
        columnStart = src.columnStart;
        columnEnd = src.columnEnd;
    }

    @Override
    public String toString() {
        return "grid-row-start: " + rowStart + " grid-row-end: " + rowEnd + " grid-column-start: " + columnStart +  " grid-column-end: " + columnEnd;
    }
}
