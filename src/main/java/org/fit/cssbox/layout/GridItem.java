package org.fit.cssbox.layout;

import cz.vutbr.web.css.*;
import cz.vutbr.web.csskit.TermIdentImpl;
import org.w3c.dom.Element;

import java.awt.*;

public class GridItem extends BlockBox {

    public static final CSSProperty.GridStartEnd GRID_START_END_AUTO = CSSProperty.GridStartEnd.AUTO;
    public static final CSSProperty.GridStartEnd GRID_START_END_NUMBER = CSSProperty.GridStartEnd.number;
    public static final CSSProperty.GridStartEnd GRID_START_END_COMPONENT = CSSProperty.GridStartEnd.component_values;


    public CSSProperty.GridStartEnd gridStartEnd;
    GridItemRowColumn gridItemRowColumnValue;

    int widthcolumnsforitems;
    int widthrowsforitems;
    int columndistancefromzero;
    int rowdistancefromzero;

    int w;
    int h;

    public GridItem(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        isblock = true;
        widthcolumnsforitems = 0;
        widthrowsforitems = 0;
        columndistancefromzero = 0;
        rowdistancefromzero = 0;
        w = 0;
        h = 0;
    }

    public GridItem(InlineBox src) {
        super(src);
        isblock = true;
        widthcolumnsforitems = 0;
        widthrowsforitems = 0;
        columndistancefromzero = 0;
        rowdistancefromzero = 0;
        w = 0;
        h = 0;
    }


    @Override
    public void setStyle(NodeData s) {
        super.setStyle(s);
        loadGridItemStyles();
    }

    /**
     *
     */
    public void loadGridItemStyles() {
        CSSDecoder dec = new CSSDecoder(ctx);
        gridItemRowColumnValue = new GridItemRowColumn();

        gridItemColumnProcessing(dec);
        gridItemRowProcessing(dec);

        System.out.println(gridItemRowColumnValue.toString());
    }

    /**
     *
     * @param dec
     */
    protected void gridItemColumnProcessing(CSSDecoder dec) {
        gridStartEnd = style.getProperty("grid-column-start");
        //pokud neni nastaven pouzije se default hodnota
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;


        if (gridStartEnd == GRID_START_END_NUMBER) {
            gridItemRowColumnValue.columnStart = dec.getLength(getLengthValue("grid-column-start"), false, 0, 0, 0);
        } else if (gridStartEnd == GRID_START_END_AUTO) {
            gridItemRowColumnValue.columnStart = 0;
        } else if (gridStartEnd == GRID_START_END_COMPONENT) {
            gridItemRowColumnValue.columnStartSpan = style.getValue(TermList.class, "grid-column-start");
        }

        gridStartEnd = style.getProperty("grid-column-end");
        //pokud neni nastaven pouzije se default hodnota
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;

        //teoreticky pokud neni nastaven tak se ma dat defaultne auto, ale kdyz je start na 1 tak end bude na 2
        if (gridStartEnd == GRID_START_END_AUTO && gridItemRowColumnValue.columnStart != 0) {
            gridItemRowColumnValue.columnEnd = gridItemRowColumnValue.columnStart + 1;
            //gridStartEnd = CSSProperty.GridStartEnd.AUTO;
        } else if (gridStartEnd == GRID_START_END_NUMBER) {
            gridItemRowColumnValue.columnEnd = dec.getLength(getLengthValue("grid-column-end"), false, 0, 0, 0);
            if (gridItemRowColumnValue.columnStart == gridItemRowColumnValue.columnEnd) {
                gridItemRowColumnValue.columnEnd = gridItemRowColumnValue.columnStart + 1;
            }
            else if (gridItemRowColumnValue.columnStart > gridItemRowColumnValue.columnEnd) {
                int tmp;
                tmp = gridItemRowColumnValue.columnStart;
                gridItemRowColumnValue.columnStart = gridItemRowColumnValue.columnEnd;
                gridItemRowColumnValue.columnEnd = tmp;
            }
        } else if (gridStartEnd == GRID_START_END_COMPONENT) {
            //span
            gridItemRowColumnValue.columnEndSpan = style.getValue(TermList.class, "grid-column-end");
            gridItemRowColumnValue.columnEnd = dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.columnEndSpan.get(1), false, 0, 0, 0) + gridItemRowColumnValue.columnStart;
        } else if (gridStartEnd == GRID_START_END_AUTO) {
            if (gridItemRowColumnValue.columnStartSpan != null) {
                gridItemRowColumnValue.columnStart = 1;
                gridItemRowColumnValue.columnEnd = dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.columnStartSpan.get(1), false, 0, 0, 0) + 1;
                gridItemRowColumnValue.columnStartSpan = null;
            } else {
                gridItemRowColumnValue.columnEnd = 0;
            }

        }

        if (gridItemRowColumnValue.columnStartSpan != null) {
            gridItemRowColumnValue.columnStart = gridItemRowColumnValue.columnEnd - dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.columnStartSpan.get(1), false, 0, 0, 0);
        }

        if (gridItemRowColumnValue.columnStart == 0 && gridItemRowColumnValue.columnEnd != 0) {
            gridItemRowColumnValue.columnStart = gridItemRowColumnValue.columnEnd - 1;
        }
    }

    /**
     *
     * @param dec
     */
    protected void gridItemRowProcessing(CSSDecoder dec) {
        gridStartEnd = style.getProperty("grid-row-start");
        //pokud neni nastaven pouzije se default hodnota
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;

        if (gridStartEnd == GRID_START_END_NUMBER) {
            gridItemRowColumnValue.rowStart = dec.getLength(getLengthValue("grid-row-start"), false, 0, 0, 0);
        } else if (gridStartEnd == CSSProperty.GridStartEnd.identificator) {
            TermIdentImpl neco = style.getValue(TermIdentImpl.class, "grid-row-start");
            System.out.println("identifikator grid-row-end: " + neco);
        } else if (gridStartEnd == GRID_START_END_AUTO) {
            gridItemRowColumnValue.rowStart = 0;
        } else if (gridStartEnd == GRID_START_END_COMPONENT) {
            gridItemRowColumnValue.rowStartSpan = style.getValue(TermList.class, "grid-row-start");
        }

        gridStartEnd = style.getProperty("grid-row-end");
        //pokud neni nastaven pouzije se default hodnota
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;

        //teoreticky pokud neni nastaven tak se ma dat defaultne auto, ale kdyz je start na 1 tak end bude na 2
        if (gridStartEnd == GRID_START_END_AUTO && gridItemRowColumnValue.rowStart != 0) {
            //gridStartEnd = CSSProperty.GridStartEnd.AUTO;
            gridItemRowColumnValue.rowEnd = gridItemRowColumnValue.rowStart + 1;
        } else if (gridStartEnd == GRID_START_END_NUMBER) {
            gridItemRowColumnValue.rowEnd = dec.getLength(getLengthValue("grid-row-end"), false, 0, 0, 0);
            if (gridItemRowColumnValue.rowStart == gridItemRowColumnValue.rowEnd) {
                gridItemRowColumnValue.rowEnd = gridItemRowColumnValue.rowStart + 1;
            } else if (gridItemRowColumnValue.rowStart > gridItemRowColumnValue.rowEnd) {
                int tmp;
                tmp = gridItemRowColumnValue.rowStart;
                gridItemRowColumnValue.rowStart = gridItemRowColumnValue.rowEnd;
                gridItemRowColumnValue.rowEnd = tmp;
            }
        } else if (gridStartEnd == GRID_START_END_COMPONENT) {
            //span
            gridItemRowColumnValue.rowEndSpan = style.getValue(TermList.class, "grid-row-end");
            gridItemRowColumnValue.rowEnd = dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.rowEndSpan.get(1), false, 0, 0, 0) + gridItemRowColumnValue.rowStart;
        } else if (gridStartEnd == GRID_START_END_AUTO) {
            if (gridItemRowColumnValue.rowStartSpan != null) {
                gridItemRowColumnValue.rowStart = 1;
                gridItemRowColumnValue.rowEnd = dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.rowStartSpan.get(1), false, 0, 0, 0) + 1;
                gridItemRowColumnValue.rowStartSpan = null;
            } else {
                gridItemRowColumnValue.columnEnd = 0;
            }
        }

        if (gridItemRowColumnValue.rowStartSpan != null) {
            gridItemRowColumnValue.rowStart = gridItemRowColumnValue.rowEnd - dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.rowStartSpan.get(1), false, 0, 0, 0);
        }
        if (gridItemRowColumnValue.rowStart == 0 && gridItemRowColumnValue.rowEnd != 0) {
            gridItemRowColumnValue.rowStart = gridItemRowColumnValue.rowEnd - 1;
        }
    }
}
