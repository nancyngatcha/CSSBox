package org.fit.cssbox.layout;

import cz.vutbr.web.css.*;
import org.w3c.dom.Element;
import java.awt.*;


public class GridWrapperBlockBox extends BlockBox {

    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_ROW = CSSProperty.GridAutoFlow.ROW;
    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_COLUMN = CSSProperty.GridAutoFlow.COLUMN;

    protected CSSProperty.GridTemplateRowsColumns gridTemplateColumns;

    //tyto urcite zustanou
    protected TermList gridTemplateRowsValues;
    protected TermList gridTemplateColumnsValues;
    protected TermList gridTemplateAreasValues;
    protected int gridAutoColumns;
    protected int gridAutoRows;
    protected TermLength.Unit unit;
    protected int gapRow;
    protected int gapColumn;
    protected int flexFactorSum;
    protected int sumofpixels;

    protected boolean isGridAutoColumn;
    protected boolean isGridAutoRow;

    protected boolean isMinContentAutoColumn;
    protected boolean isMaxContentAutoColumn;

    protected boolean isMinContentAutoRow;
    protected boolean isMaxContentAutoRow;

    protected int maxColumnLine;

    protected int maxRowLine;

    protected boolean isGridTemplateColumns;
    protected boolean isGridTemplateRows;

    protected boolean isGridAutoFlowRow;

    protected boolean isGridTemplateColumnsNone;


    public GridWrapperBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        typeoflayout = new GridLayoutManager(this);
        isblock = true;
        flexFactorSum = 0;
        isGridAutoColumn = false;
        isGridAutoRow = false;
        isMinContentAutoColumn = false;
        isMaxContentAutoColumn = false;
        isMinContentAutoRow = false;
        isMaxContentAutoRow = false;
        maxColumnLine = 1;
        maxRowLine = 1;
    }

    public GridWrapperBlockBox(InlineBox src) {
        super(src);
        typeoflayout = new GridLayoutManager(this);
        isblock = true;
        sumofpixels = 0;
        isGridAutoColumn = false;
        isGridAutoRow = false;
        isMinContentAutoColumn = false;
        isMaxContentAutoColumn = false;
        isMinContentAutoRow = false;
        isMaxContentAutoRow = false;
        maxColumnLine = 1;
        maxRowLine = 1;
    }

    @Override
    public void setStyle(NodeData s) {
        super.setStyle(s);
        loadGridWrapperStyles();
    }

    /**
     *
     */
    public void loadGridWrapperStyles() {
        CSSDecoder dec = new CSSDecoder(ctx);
        CSSProperty.GridTemplateAreas gridTemplateAreas = style.getProperty("grid-template-areas");

        //pokud neni nastaven pouzije se default hodnota
        if (gridTemplateAreas == null) gridTemplateAreas = CSSProperty.GridTemplateAreas.NONE;

        if (gridTemplateAreas == CSSProperty.GridTemplateAreas.list_values) {
            gridTemplateAreasValues = style.getValue(TermList.class, "grid-template-areas");
            System.out.println("grid-template-areas: " + gridTemplateAreasValues);
        } else if (gridTemplateAreas == CSSProperty.GridTemplateAreas.NONE) {
            System.out.println("grid-template-areas: none.");
        }


        GridGapProcessing(dec);
        GridTemplateRowsColumnsProcessing();
        GridAutoFlowProcessing();
        System.out.println("\n\n\n");
    }

    /**
     *
     * @param dec
     */
    protected void GridGapProcessing(CSSDecoder dec) {

        CSSProperty.GridGap gridColumnGap = style.getProperty("grid-column-gap");
        CSSProperty.GridGap gridRowGap = style.getProperty("grid-row-gap");

        //pokud neni nastaven pouzije se default hodnota
        if (gridRowGap == null) gridRowGap = CSSProperty.GridGap.NORMAL;

        if (gridRowGap == CSSProperty.GridGap.length) {
            gapRow = dec.getLength(getLengthValue("grid-row-gap"), false, 0, 0, 0);
//            System.out.println("grid-row-gap: " + gapRow);
        } else if (gridRowGap == CSSProperty.GridGap.NORMAL) {
            System.out.println("grid-row-gap: normal; prevedeno na 0");
            gapRow = 0;
        }

        //pokud neni nastaven pouzije se default hodnota
        if (gridColumnGap == null) gridColumnGap = CSSProperty.GridGap.NORMAL;

        if (gridColumnGap == CSSProperty.GridGap.length) {
            gapColumn = dec.getLength(getLengthValue("grid-column-gap"), false, 0, 0, 0);
//            System.out.println("grid-column-gap: " + gapColumn);
        } else if (gridColumnGap == CSSProperty.GridGap.NORMAL) {
            System.out.println("rid-column-gap: normal; prevedeno na 0");
            gapColumn = 0;
        }
    }

    /**
     *
     */
    protected void GridTemplateRowsColumnsProcessing() {
        CSSProperty.GridTemplateRowsColumns gridTemplateRows = style.getProperty("grid-template-rows");
        //pokud neni nastaven pouzije se default hodnota
        if (gridTemplateRows == null) gridTemplateRows = CSSProperty.GridTemplateRowsColumns.NONE;

        if (gridTemplateRows == CSSProperty.GridTemplateRowsColumns.list_values) {
            gridTemplateRowsValues = style.getValue(TermList.class, "grid-template-rows");
            isGridTemplateRows = true;
            System.out.println("grid-template-rows: " + gridTemplateRowsValues);
        } else if (gridTemplateRows == CSSProperty.GridTemplateRowsColumns.AUTO) {
            System.out.println("grid-template-rows: auto.");
        } else {
            System.out.println("grid-template-rows: none");
            isGridTemplateRows = false;
        }

        //zpracovani grid-template-columns
        gridTemplateColumns = style.getProperty("grid-template-columns");
        //pokud neni nastaven pouzije se default hodnota
        if (gridTemplateColumns == null) gridTemplateColumns = CSSProperty.GridTemplateRowsColumns.NONE;

        if (gridTemplateColumns == CSSProperty.GridTemplateRowsColumns.list_values) {
            gridTemplateColumnsValues = style.getValue(TermList.class, "grid-template-columns");
            isGridTemplateColumns = true;
            System.out.println("grid-template-columns: " + gridTemplateColumnsValues);
        } else if (gridTemplateColumns == CSSProperty.GridTemplateRowsColumns.AUTO) {
            System.out.println("grid-template-columns: auto.");
        } else {
            isGridTemplateColumnsNone = true;
            System.out.println("grid-template-columns: none");
        }
    }

    /**
     *
     */
    protected void GridAutoFlowProcessing() {
        //zpracovani grid-auto-flow
        CSSProperty.GridAutoFlow gridAutoFlow = style.getProperty("grid-auto-flow");

        if (gridAutoFlow == null) gridAutoFlow = GRID_AUTO_FLOW_ROW;

        if (gridAutoFlow == GRID_AUTO_FLOW_ROW) {
            System.out.println("grid-auto-flow: row");
            isGridAutoFlowRow = true;
        } else if (gridAutoFlow == GRID_AUTO_FLOW_COLUMN) {
            isGridAutoFlowRow = false;
            System.out.println("grid-auto-flow: column");
        }
    }

    /**
     *
     * @param tmp
     * @param dec
     * @param gap
     * @param contw
     * @return
     */
    protected boolean findUnitsForFr(TermList tmp, CSSDecoder dec, int gap, int contw) {
        TermLengthOrPercent a;
        boolean containFr = false;
        int b;
        int j = 0;
        for (int i = 0; i < tmp.size(); i++) {
            a = (TermLengthOrPercent) tmp.get(i);
            unit = a.getUnit();

            if (unit == TermNumeric.Unit.fr) {
                b = dec.getLength(a, false, 0, 0, contw);
                flexFactorSum += b;
                if (flexFactorSum < 1) {
                    flexFactorSum = 1;
                }
                containFr = true;
            } else {
                b = dec.getLength(a, false, 0, 0, contw);
                sumofpixels += b;
            }
            j++;
        }
        sumofpixels += (j - 1) * gap;
//        System.out.println("flex-faktor-sum je: " + flexFactorSum);
//        System.out.println("sum-of-pixels je: " + sumofpixels);
        return containFr;
    }

    /**
     *
     * @param flexFactorSum
     * @param sumofpixels
     * @param availableContent
     * @return
     */
    protected int computingFrUnits(int flexFactorSum, int sumofpixels, int availableContent) {
        if (flexFactorSum == 0) return 0;
        else return (availableContent - sumofpixels) / flexFactorSum;
    }

    /**
     *
     * @param contw
     * @param dec
     * @return
     */
    protected boolean isGridAutoColumns(int contw, CSSDecoder dec) {
        CSSProperty.GridAutoRowsColumns gAC = style.getProperty("grid-auto-columns");
        if (gAC == null) {
            gAC = CSSProperty.GridAutoRowsColumns.AUTO;
            return false;
        }
        if (gAC == CSSProperty.GridAutoRowsColumns.length) {
            System.out.println("grid-auto-columns jsou cislo nebo procenta");
            gridAutoColumns = dec.getLength(getLengthValue("grid-auto-columns"), false, 0, 0, contw);
            System.out.println("A velikost je: " + gridAutoColumns);
        } else if (gAC == CSSProperty.GridAutoRowsColumns.AUTO) {
            System.out.println("grid-auto-columns: auto;");
        } else if (gAC == CSSProperty.GridAutoRowsColumns.MIN_CONTENT) {
            isMinContentAutoColumn = true;
            System.out.println("grid-auto-columns: min-content");
        } else if (gAC == CSSProperty.GridAutoRowsColumns.MAX_CONTENT) {
            isMaxContentAutoColumn = true;
            System.out.println("grid-auto-columns: max-content");
        }
        return true;
    }

    /**
     *
     * @param conth
     * @param dec
     * @return
     */
    protected boolean isGridAutoRows(int conth, CSSDecoder dec) {
        CSSProperty.GridAutoRowsColumns gAC = style.getProperty("grid-auto-rows");
        if (gAC == null) {
            gAC = CSSProperty.GridAutoRowsColumns.AUTO;
            return false;
        }
        if (gAC == CSSProperty.GridAutoRowsColumns.length) {
            gridAutoRows = dec.getLength(getLengthValue("grid-auto-rows"), false, 0, 0, conth);
        } else if (gAC == CSSProperty.GridAutoRowsColumns.AUTO) {
            System.out.println("grid-auto-rows: auto");
        } else if (gAC == CSSProperty.GridAutoRowsColumns.MIN_CONTENT) {
            isMinContentAutoRow = true;
            System.out.println("grid-auto-rows: min-content");
        } else if (gAC == CSSProperty.GridAutoRowsColumns.MAX_CONTENT) {
            isMaxContentAutoRow = true;
            System.out.println("grid-auto-rows: max-content");
        }
        return true;
    }

    /**
     *
     * @param dec
     * @param tl
     * @param oneFrUnit
     * @param cont
     * @return
     */
    protected int sumOfLengthForGridTemplateColumnRow(CSSDecoder dec, TermList tl, int oneFrUnit, int cont) {
        int c = 0;

        for (int k = 0; k < tl.size(); k++) {
            TermLengthOrPercent a = (TermLengthOrPercent) tl.get(k);
            unit = a.getUnit();
            if (unit != TermNumeric.Unit.fr) {
                c += dec.getLength((TermLengthOrPercent) tl.get(k), false, 0, 0, cont);
            } else {
                int tmpForFrUnit = dec.getLength((TermLengthOrPercent) tl.get(k), false, 0, 0, cont);
                c += (tmpForFrUnit * oneFrUnit);
            }
        }
        return c;
    }

    /**
     *
     * @param dec
     * @param tl
     * @param i
     * @param oneFrUnit
     * @param cont
     * @return
     */
    protected int findSizeOfGridItem(CSSDecoder dec, TermList tl, int i, int oneFrUnit, int cont) {
        int length;
        TermLengthOrPercent a = (TermLengthOrPercent) tl.get(i);
        unit = a.getUnit();
        if (unit != TermNumeric.Unit.fr) {
            length = dec.getLength((TermLengthOrPercent) tl.get(i), false, 0, 0, cont);
        } else {
            int tmpForFrUnit = dec.getLength((TermLengthOrPercent) tl.get(i), false, 0, 0, cont);
            length = (tmpForFrUnit * oneFrUnit);
        }
        return length;
    }

    /**
     *
     */
    protected void setSizeofGrid() {
        for (int i = 1; i <= getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) getSubBox(i - 1);
            if (griditem.gridItemRowColumnValue.columnEnd > maxColumnLine) {
                maxColumnLine = griditem.gridItemRowColumnValue.columnEnd;
            } else if (griditem.gridItemRowColumnValue.columnEnd == 0 && griditem.gridItemRowColumnValue.columnStart == 0 && maxColumnLine == 1){
                if (isGridAutoFlowRow) {
                    if (isGridTemplateColumns) maxColumnLine = gridTemplateColumnsValues.size() + 1;
                    else maxColumnLine = 2;
                } else maxColumnLine = i + 1;
            }
            if (griditem.gridItemRowColumnValue.rowEnd > maxRowLine) {
                maxRowLine = griditem.gridItemRowColumnValue.rowEnd;
            } else if (griditem.gridItemRowColumnValue.rowEnd == 0 && griditem.gridItemRowColumnValue.rowStart == 0 && maxRowLine == 1){
                if (!isGridAutoFlowRow) {
                    if (isGridTemplateRows) maxRowLine = gridTemplateRowsValues.size() + 1;
                    else maxRowLine = 2;
                } else maxRowLine = i + 1;
            }
        }
    }
}
