package org.fit.cssbox.layout;

import cz.vutbr.web.css.*;
import cz.vutbr.web.csskit.fn.RepeatImpl;
import org.w3c.dom.Element;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class represents Grid container
 *
 * @author Ondra
 */
public class GridWrapperBlockBox extends BlockBox {

    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_ROW = CSSProperty.GridAutoFlow.ROW;
    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_COLUMN = CSSProperty.GridAutoFlow.COLUMN;

    /** Represents row tracks in explicit grid */
    protected TermList gridTemplateRowsValues;

    /** Represents column tracks in explicit grid */
    protected TermList gridTemplateColumnsValues;

    /** Size of column tracks in implicit grid */
    protected int gridAutoColumns;

    /** Size of row tracks in implicit grid */
    protected int gridAutoRows;

    /** Size of row gaps in grid container */
    protected int gapRow;

    /** Size of column gaps in grid container */
    protected int gapColumn;

    /** Count of fr units in explicit grid */
    protected int flexFactorSum;

    /** Count of fixed tracks in explicit grid */
    protected int sumofpixels;

    /** Are specified columns of implicit grid? */
    protected boolean isGridAutoColumn;

    /** Are specified rows of implicit grid? */
    protected boolean isGridAutoRow;

    /** Is specified size min-content of columns in implicit grid? */
    protected boolean isMinContentAutoColumn;

    /** Is specified size max-content of columns in implicit grid? */
    protected boolean isMaxContentAutoColumn;

    /** Is specified size min-content of rows in implicit grid? */
    protected boolean isMinContentAutoRow;

    /** Is specified size max-content of rows in implicit grid? */
    protected boolean isMaxContentAutoRow;

    /** Max column line in container */
    protected int maxColumnLine;

    /** Max row line in container */
    protected int maxRowLine;

    /** Are specified columns in explicit grid? */
    protected boolean isGridTemplateColumns;

    /** Are specified rows in explicit grid? */
    protected boolean isGridTemplateRows;

    /** Are specified columns in explicit grid? */
    protected boolean isGridTemplateColumnsNone;

    /** Is specified auto flow for automatic grid items? */
    protected boolean isGridAutoFlowRow;

    /** Size of 1fr (in pixels) in explicit grid */
    protected int oneFrUnitColumn;

    /** Size of 1fr (in pixels) in explicit grid */
    protected int oneFrUnitRow;

    /** Is specified only one auto value in explicit grid? */
    protected boolean isGridTemplateRowsAuto;

    /** Is specified only one auto value in explicit grid? */
    protected boolean isGridTemplateColumnsAuto;

    /** There are new rows values (in pixels) after computing auto, min-content or max-content values*/
    protected ArrayList<Integer> arrayofrows = new ArrayList<>();

    /** There are new columns values (in pixels) after computing auto, min-content or max-content values*/
    protected ArrayList<Integer> arrayofcolumns = new ArrayList<>();


    /**
     * Creates new instance of grid container
     */
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


    /**
     * Converts an inline box to a grid container
     */
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
     * Loads styles for grid container
     */
    public void loadGridWrapperStyles() {

//        CSSProperty.GridTemplateAreas gridTemplateAreas = style.getProperty("grid-template-areas");
//
//        //pokud neni nastaven pouzije se default hodnota
//        if (gridTemplateAreas == null) gridTemplateAreas = CSSProperty.GridTemplateAreas.NONE;
//
//        if (gridTemplateAreas == CSSProperty.GridTemplateAreas.list_values) {
//            gridTemplateAreasValues = style.getValue(TermList.class, "grid-template-areas");
//            System.out.println("grid-template-areas: " + gridTemplateAreasValues);
//        } else if (gridTemplateAreas == CSSProperty.GridTemplateAreas.NONE) {
//            System.out.println("grid-template-areas: none.");
//        }


        GridGapProcessing();
        GridTemplateRowsColumnsProcessing();
        GridAutoFlowProcessing();
        System.out.println("\n\n\n");
    }


    /**
     * Loads sizes of grid-column-gap and grid-row-gap in a grid container
     */
    protected void GridGapProcessing() {
        CSSDecoder dec = new CSSDecoder(ctx);
        CSSProperty.GridGap gridColumnGap = style.getProperty("grid-column-gap");
        CSSProperty.GridGap gridRowGap = style.getProperty("grid-row-gap");
        if (gridRowGap == null) gridRowGap = CSSProperty.GridGap.NORMAL;

        if (gridRowGap == CSSProperty.GridGap.length) {
            gapRow = dec.getLength(getLengthValue("grid-row-gap"), false, 0, 0, 0);
        } else if (gridRowGap == CSSProperty.GridGap.NORMAL) {
            gapRow = 0;
        }

        if (gridColumnGap == null) gridColumnGap = CSSProperty.GridGap.NORMAL;
        if (gridColumnGap == CSSProperty.GridGap.length) {
            gapColumn = dec.getLength(getLengthValue("grid-column-gap"), false, 0, 0, 0);
        } else if (gridColumnGap == CSSProperty.GridGap.NORMAL) {
            gapColumn = 0;
        }
    }


    /**
     * Loads sizes of grid tracks (rows, columns) in a grid container (explicit grid)
     */
    protected void GridTemplateRowsColumnsProcessing() {
        CSSProperty.GridTemplateRowsColumns gridTemplateRows = style.getProperty("grid-template-rows");
        if (gridTemplateRows == null) gridTemplateRows = CSSProperty.GridTemplateRowsColumns.NONE;

        if (gridTemplateRows == CSSProperty.GridTemplateRowsColumns.list_values) {
            gridTemplateRowsValues = style.getValue(TermList.class, "grid-template-rows");
            isGridTemplateRows = true;
            System.out.println("grid-template-rows: " + gridTemplateRowsValues);
        } else if (gridTemplateRows == CSSProperty.GridTemplateRowsColumns.AUTO) {
            isGridTemplateRowsAuto = true;
            System.out.println("grid-template-rows: auto.");
        } else {
            System.out.println("grid-template-rows: none");
            isGridTemplateRows = false;
        }

        CSSProperty.GridTemplateRowsColumns gridTemplateColumns = style.getProperty("grid-template-columns");
        if (gridTemplateColumns == null) gridTemplateColumns = CSSProperty.GridTemplateRowsColumns.NONE;

        if (gridTemplateColumns == CSSProperty.GridTemplateRowsColumns.list_values) {
            gridTemplateColumnsValues = style.getValue(TermList.class, "grid-template-columns");
            isGridTemplateColumns = true;
            System.out.println("grid-template-columns: " + gridTemplateColumnsValues);
        } else if (gridTemplateColumns == CSSProperty.GridTemplateRowsColumns.AUTO) {
            isGridTemplateColumnsAuto = true;
            System.out.println("grid-template-columns: auto.");
        } else {
            isGridTemplateColumnsNone = true;
            System.out.println("grid-template-columns: none");
        }
    }


    /**
     * Loads the direction of placing automatic items
     */
    protected void GridAutoFlowProcessing() {
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
     * Find fr units int grid tracks. If find it counts their number and and calculates all fixed units
     *
     * @param tmp size and count of grid tracks
     * @param dec decoder to get values of units
     * @param gap size of gap
     * @param contw available width
     * @return true if contains fr unit, false if not
     */
    protected boolean findUnitsForFr(TermList tmp, CSSDecoder dec, int gap, int contw) {
        TermLength.Unit unit;
        TermLengthOrPercent a;
        if (tmp == null) return false;

        for (int i = 0; i < tmp.size(); i++) {
            if (tmp.get(i).getValue().toString().equals("auto") ||
                    tmp.get(i).getValue().toString().equals("min-content") ||
                    tmp.get(i).getValue().toString().equals("max-content")) {
                return false;
            }
        }
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
        return containFr;
    }


    /**
     * Calculates size of 1fr unit
     *
     * @param flexFactorSum count of fr units in grid track
     * @param sumofpixels size of fixed units
     * @param availableContent available content
     * @return zero or size of 1fr in pixels
     */
    protected int computingFrUnits(int flexFactorSum, int sumofpixels, int availableContent) {
        if (flexFactorSum == 0) return 0;
        else return (availableContent - sumofpixels) / flexFactorSum;
    }


    /**
     * Loads sizes of grid tracks (columns) in a grid container (implicit grid)
     *
     * @param contw available width
     * @return false if is auto, true if not auto
     */
    protected boolean isGridAutoColumns(int contw) {
        CSSDecoder dec = new CSSDecoder(ctx);
        CSSProperty.GridAutoRowsColumns gAC = style.getProperty("grid-auto-columns");
        if (gAC == null) {
            gAC = CSSProperty.GridAutoRowsColumns.AUTO;
        }
        if (gAC == CSSProperty.GridAutoRowsColumns.length) {
            System.out.println("grid-auto-columns jsou cislo nebo procenta");
            gridAutoColumns = dec.getLength(getLengthValue("grid-auto-columns"), false, 0, 0, contw);
            System.out.println("A velikost je: " + gridAutoColumns);
        } else if (gAC == CSSProperty.GridAutoRowsColumns.AUTO) {
            System.out.println("grid-auto-columns: auto;");
            return false;
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
     * Loads sizes of grid tracks (rows) in a grid container (implicit grid)
     *
     * @param conth available height
     * @return false if is auto, true if not auto
     */
    protected boolean isGridAutoRows(int conth) {
        CSSDecoder dec = new CSSDecoder(ctx);
        CSSProperty.GridAutoRowsColumns gAC = style.getProperty("grid-auto-rows");
        if (gAC == null) {
            gAC = CSSProperty.GridAutoRowsColumns.AUTO;
        }
        if (gAC == CSSProperty.GridAutoRowsColumns.length) {
            gridAutoRows = dec.getLength(getLengthValue("grid-auto-rows"), false, 0, 0, conth);
        } else if (gAC == CSSProperty.GridAutoRowsColumns.AUTO) {
            System.out.println("grid-auto-rows: auto");
            return false;
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
     * Calculates size of explicit grid
     *
     * @param dec decoder to get values of units
     * @param tl size and count of grid tracks
     * @param oneFrUnit 1fr in pixel units
     * @param cont available space
     * @return -1 if contains auto,min-content or max-content, number if contains only numbers
     */
    protected int sumOfLengthForGridTemplateColumnRow(CSSDecoder dec, TermList tl, int oneFrUnit, int cont) {
        int c = 0;
        for (int k = 0; k < tl.size(); k++) {
            if (tl.get(k).getValue().toString().equals("auto") ||
                    tl.get(k).getValue().toString().equals("min-content") ||
                    tl.get(k).getValue().toString().equals("max-content")) {
                return -1;
            }
        }

        TermLength.Unit unit;
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
     * Calculates size of grid item, if in explicit grid are only numbers
     *
     * @param dec decoder to get values of units
     * @param tl size and count of grid tracks
     * @param oneFrUnit 1fr in pixel units
     * @param cont available space
     * @return -1 if contains auto,min-content or max-content, size of grid item
     */
    protected int findSizeOfGridItem(CSSDecoder dec, TermList tl, int i, int oneFrUnit, int cont) {
        int length;

        try {
            if (tl.get(i).getValue().toString().equals("auto") ||
                    tl.get(i).getValue().toString().equals("min-content") ||
                    tl.get(i).getValue().toString().equals("max-content")) {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }

        TermLength.Unit unit;
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
     * Determines the grid size based on fixed items
     * Grid size can be updated
     */
    protected void setSizeofGrid() {
        for (int i = 1; i <= getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) getSubBox(i - 1);
            if (griditem.gridItemRowColumnValue.columnEnd > maxColumnLine) {
                maxColumnLine = griditem.gridItemRowColumnValue.columnEnd;
            } else if (griditem.gridItemRowColumnValue.columnEnd == 0 && griditem.gridItemRowColumnValue.columnStart == 0 && maxColumnLine == 1) {
                if (isGridAutoFlowRow) {
                    if (isGridTemplateColumns) maxColumnLine = gridTemplateColumnsValues.size() + 1;
                    else maxColumnLine = 2;
                } else maxColumnLine = i + 1;
            }
            if (griditem.gridItemRowColumnValue.rowEnd > maxRowLine) {
                maxRowLine = griditem.gridItemRowColumnValue.rowEnd;
            } else if (griditem.gridItemRowColumnValue.rowEnd == 0 && griditem.gridItemRowColumnValue.rowStart == 0 && maxRowLine == 1) {
                if (!isGridAutoFlowRow) {
                    if (isGridTemplateRows) maxRowLine = gridTemplateRowsValues.size() + 1;
                    else maxRowLine = 2;
                } else maxRowLine = i + 1;
            }
        }

        if (gridTemplateColumnsValues != null) {
            if (maxColumnLine < gridTemplateColumnsValues.size() + 1) {
                maxColumnLine = gridTemplateColumnsValues.size() + 1;
            }
        }

        if (arrayofcolumns != null) {
            if (maxColumnLine < arrayofcolumns.size() + 1) {
                maxColumnLine = arrayofcolumns.size() + 1;
            }
        }

        if (gridTemplateRowsValues != null) {
            if (maxRowLine < gridTemplateRowsValues.size() + 1) {
                maxRowLine = gridTemplateRowsValues.size() + 1;
            }
        }
    }


    /**
     * Allocates item coordinates based on available grid space
     * If space is insufficient, it is automatically extended
     */
    public void processAutomaticItems() {
        if (isGridAutoFlowRow) {
            for (int a = 1; a < maxRowLine; a++) {
                ArrayList<Integer> columnID = new ArrayList<>();
                for (int b = 1; b < maxColumnLine; b++) {
                    columnID.add(b);
                }
                for (int i = 0; i < getSubBoxNumber(); i++) {
                    GridItem griditem = (GridItem) getSubBox(i);
                    if (griditem.gridItemRowColumnValue.rowStart == a) {
                        for (int c = griditem.gridItemRowColumnValue.columnStart; c < griditem.gridItemRowColumnValue.columnEnd; c++) {
                            columnID.remove((Integer) c);
                        }
                    }
                }
                System.out.println("radek> " + a);
                System.out.println("vystup array pred smazanim roztazenych itemu> " + columnID);
                if (a > 1) {
                    for (int y = 1; y < a; y++) {
                        for (int i = 0; i < getSubBoxNumber(); i++) {
                            GridItem griditem = (GridItem) getSubBox(i);
                            if (griditem.gridItemRowColumnValue.rowStart == y) {
                                if (griditem.gridItemRowColumnValue.rowEnd >= a + 1) {
                                    for (int c = griditem.gridItemRowColumnValue.columnStart; c < griditem.gridItemRowColumnValue.columnEnd; c++) {
                                        columnID.remove((Integer) c);
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("radek> " + a);
                System.out.println("vystup array> " + columnID);
                //prirazeni souradnic automatickym itemum
                for (int w = 0; w < getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) getSubBox(w);
                    if (griditem.gridItemRowColumnValue.rowStart == 0 && griditem.gridItemRowColumnValue.columnStart == 0) {
                        if (columnID.isEmpty()) break;
                        griditem.gridItemRowColumnValue.rowStart = a;
                        griditem.gridItemRowColumnValue.rowEnd = a + 1;
                        griditem.gridItemRowColumnValue.columnStart = columnID.get(0);
                        columnID.remove(0);
                        griditem.gridItemRowColumnValue.columnEnd = griditem.gridItemRowColumnValue.columnStart + 1;
                    }
                }
                //existuji jeste nejaky itemy ktery jsou potreba usadit do mrizky? pokud ano rozsir mrizku
                for (int w = 0; w < getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) getSubBox(w);
                    if (griditem.gridItemRowColumnValue.rowStart == 0 && griditem.gridItemRowColumnValue.columnStart == 0) {
                        if (a <= maxRowLine) {
                            maxRowLine += 1;
                            break;
                        }
                    }
                }
            }
        } else {
            for (int a = 1; a < maxColumnLine; a++) {
                ArrayList<Integer> rowID = new ArrayList<>();
                for (int b = 1; b < maxRowLine; b++) {
                    rowID.add(b);
                }
                for (int i = 0; i < getSubBoxNumber(); i++) {
                    GridItem griditem = (GridItem) getSubBox(i);
                    if (griditem.gridItemRowColumnValue.columnStart == a) {
                        for (int c = griditem.gridItemRowColumnValue.rowStart; c < griditem.gridItemRowColumnValue.rowEnd; c++) {
                            rowID.remove((Integer) c);
                        }
                    }
                }
                if (a > 1) {
                    for (int y = 1; y < a; y++) {
                        for (int i = 0; i < getSubBoxNumber(); i++) {
                            GridItem griditem = (GridItem) getSubBox(i);
                            if (griditem.gridItemRowColumnValue.columnStart == y) {
                                if (griditem.gridItemRowColumnValue.columnEnd >= a + 1) {
                                    for (int c = griditem.gridItemRowColumnValue.rowStart; c < griditem.gridItemRowColumnValue.rowEnd; c++) {
                                        rowID.remove((Integer) c);
                                    }
                                }
                            }
                        }

                    }
                }
                System.out.println("sloupec> " + a);
                System.out.println("vystup array> " + rowID);
                for (int w = 0; w < getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) getSubBox(w);
                    if (griditem.gridItemRowColumnValue.columnStart == 0 && griditem.gridItemRowColumnValue.rowStart == 0) {
                        if (rowID.isEmpty()) break;
                        griditem.gridItemRowColumnValue.columnStart = a;
                        griditem.gridItemRowColumnValue.columnEnd = a + 1;
                        griditem.gridItemRowColumnValue.rowStart = rowID.get(0);
                        rowID.remove(0);
                        griditem.gridItemRowColumnValue.rowEnd = griditem.gridItemRowColumnValue.rowStart + 1;
                    }
                }
                for (int w = 0; w < getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) getSubBox(w);
                    if (griditem.gridItemRowColumnValue.rowStart == 0 && griditem.gridItemRowColumnValue.columnStart == 0) {
                        if (a < maxColumnLine) {
                            maxColumnLine += 1;
                            break;
                        }
                    }
                }
            }
        }
    }


    /**
     * Searches for the largest height in the row and assigns it to all items in the row
     *
     * @param rowstart start row coordinate of grid item
     */
    public void checkRowLine(int rowstart) {
        int maxheight = 0;
        GridItem griditem;
        for (int i = 0; i < getSubBoxNumber(); i++) {
            griditem = (GridItem) getSubBox(i);
            if (griditem.gridItemRowColumnValue.rowStart == rowstart) {
                if ((griditem.gridItemRowColumnValue.rowEnd - griditem.gridItemRowColumnValue.rowStart) < 2) {
                    if (griditem.widthrowsforitems > maxheight) {
                        maxheight = griditem.widthrowsforitems;
                    }
                }
            }
        }
        for (int i = 0; i < getSubBoxNumber(); i++) {
            griditem = (GridItem) getSubBox(i);
            if (griditem.gridItemRowColumnValue.rowStart == rowstart) {
                if ((griditem.gridItemRowColumnValue.rowEnd - griditem.gridItemRowColumnValue.rowStart) < 2) {
                    griditem.widthrowsforitems = maxheight;
                }
            }
        }
    }


    /**
     * Searches for the largest width in the column and assigns it to all items in the column
     *
     * @param columnstart start column coordinate of grid item
     */
    public void checkColumnLine(int columnstart) {
        int maxwidth = 0;
        GridItem griditem;
        for (int i = 0; i < getSubBoxNumber(); i++) {
            griditem = (GridItem) getSubBox(i);
            if (griditem.gridItemRowColumnValue.columnStart == columnstart) {
                if ((griditem.gridItemRowColumnValue.columnEnd - griditem.gridItemRowColumnValue.columnStart) < 2) {
                    if (griditem.widthcolumnsforitems > maxwidth) {
                        maxwidth = griditem.widthcolumnsforitems;
                    }
                }
            }
        }
        for (int i = 0; i < getSubBoxNumber(); i++) {
            griditem = (GridItem) getSubBox(i);
            if (griditem.gridItemRowColumnValue.columnStart == columnstart) {
                if ((griditem.gridItemRowColumnValue.columnEnd - griditem.gridItemRowColumnValue.columnStart) < 2) {
                    griditem.widthcolumnsforitems = maxwidth;
                }
            }
        }
    }


    /**
     * Detects the current grid size (in column) and fills the list with the values of each column
     */
    public void fillColumnsSizesToArray() {
        int maxactualcolumnline = 0;
        for (int i = 0; i < getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) getSubBox(i);
            if (griditem.gridItemRowColumnValue.columnEnd > maxactualcolumnline) {
                maxactualcolumnline = griditem.gridItemRowColumnValue.columnEnd;
            }
        }

        for (int a = 1; a < maxactualcolumnline; a++) {
            for (int i = 0; i < getSubBoxNumber(); i++) {
                GridItem griditem = (GridItem) getSubBox(i);
                if (griditem.gridItemRowColumnValue.columnStart == a) {
                    if (griditem.gridItemRowColumnValue.columnEnd - griditem.gridItemRowColumnValue.columnStart == 1) {
                        arrayofcolumns.add(griditem.widthcolumnsforitems);
                        break;
                    }
                }
            }
        }
    }


    /**
     * Detects the current grid size (in row) and fills the list with the values of each row
     */
    public void fillRowsSizesToArray() {
        if (gridTemplateRowsValues != null) {
            int maxactualrowline = 0;
            for (int i = 0; i < getSubBoxNumber(); i++) {
                GridItem griditem = (GridItem) getSubBox(i);
                if (griditem.gridItemRowColumnValue.rowEnd > maxactualrowline) {
                    maxactualrowline = griditem.gridItemRowColumnValue.rowEnd;
                }
            }
            onlyFill(maxactualrowline);

            if (arrayofrows.size() < gridTemplateRowsValues.size()) {
                for (int i = arrayofrows.size(); i < gridTemplateRowsValues.size(); i++) {
                    if (gridTemplateRowsValues.get(i).getValue().toString().equals("auto") ||
                        gridTemplateRowsValues.get(i).getValue().toString().equals("min-content") ||
                        gridTemplateRowsValues.get(i).getValue().toString().equals("max-content")) {
                            arrayofrows.add(i, 0);
                    } else {
                        CSSDecoder decoder = new CSSDecoder(ctx);
                        arrayofrows.add(i, decoder.getLength((TermLengthOrPercent) gridTemplateRowsValues.get(i), false, 0, 0, getContentWidth()));
                    }
                }
            }
        } else {
            int maxactualrowline = 0;
            for (int i = 0; i < getSubBoxNumber(); i++) {
                GridItem griditem = (GridItem) getSubBox(i);
                if (griditem.gridItemRowColumnValue.rowEnd > maxactualrowline) {
                    maxactualrowline = griditem.gridItemRowColumnValue.rowEnd;
                }
            }
            onlyFill(maxactualrowline);
        }
    }


    /**
     * Auxiliary method to fillRowsSizesToArray() method, that only fill list
     *
     * @param maxactualrowline actual row size of grid
     */
    public void onlyFill(int maxactualrowline) {
        for (int a = 1; a < maxactualrowline; a++) {
            for (int i = 0; i < getSubBoxNumber(); i++) {
                GridItem griditem = (GridItem) getSubBox(i);
                if (griditem.gridItemRowColumnValue.rowStart == a) {
                    if (griditem.gridItemRowColumnValue.rowEnd - griditem.gridItemRowColumnValue.rowStart == 1) {
                        arrayofrows.add(griditem.widthrowsforitems);
                        break;
                    }
                }
            }
        }
    }


    /**
     * Calculates sizes of tracks in list
     *
     * @return total size of tracks (inc. gaps) in list
     */
    public int getSizeOfArrayOfRows() {
        int count = 0;
        for (int i = 0; i < arrayofrows.size(); i++) {
            count += arrayofrows.get(i);
        }
        count += (arrayofrows.size() -1) * gapRow;
        return count;
    }


    /**
     * Checks if track contains auto, min-content or max-content
     *
     * @return control number
     */
    public boolean checkBeforeNewSizes() {
        int count = 0;
        if (gridTemplateRowsValues != null) {
            for (int k = 0; k < gridTemplateRowsValues.size(); k++) {
                if (((gridTemplateRowsValues.get(k).getValue().toString().equals("auto") ||
                        gridTemplateRowsValues.get(k).getValue().toString().equals("min-content") ||
                        gridTemplateRowsValues.get(k).getValue().toString().equals("max-content")) && gridAutoRows != 0) ||
                        ((gridTemplateRowsValues.get(k).getValue().toString().equals("auto") ||
                                gridTemplateRowsValues.get(k).getValue().toString().equals("min-content") ||
                                gridTemplateRowsValues.get(k).getValue().toString().equals("max-content")) && gridAutoRows == 0)) {
                    count++;
                }
            }
        } else count = 1;
        return count == 0;
    }


    /**
     * Calculates new size of grid items, which take up more than one track (in row)
     */
    public void checkNewSizeOfRowsBigItems() {
        GridItem griditem;
        System.out.println("array radku> " + arrayofrows);
        for (int i = 0; i < getSubBoxNumber(); i++) {
            griditem = (GridItem) getSubBox(i);

            if ((griditem.gridItemRowColumnValue.rowEnd - griditem.gridItemRowColumnValue.rowStart) > 1) {
                griditem.widthrowsforitems = 0;
                for (int j = griditem.gridItemRowColumnValue.rowStart; j < griditem.gridItemRowColumnValue.rowEnd; j++) {
                    try {
                        griditem.widthrowsforitems += arrayofrows.get(j - 1);
                    } catch (IndexOutOfBoundsException e) {
                        if (gridAutoRows != 0) {
                            griditem.widthrowsforitems += gridAutoRows;
                        } else {
                            griditem.widthrowsforitems += gapRow;
                        }
                    }
                }
                griditem.widthrowsforitems += ((griditem.gridItemRowColumnValue.rowEnd - griditem.gridItemRowColumnValue.rowStart) - 1) * gapRow;
            }
        }
    }


    /**
     * Calculates new size of grid items, which take up more than one track (in column)
     */
    public void checkNewSizeOfColumnsBigItems() {
        GridItem griditem;
        System.out.println("array sloupcu> " + arrayofcolumns);
        for (int i = 0; i < getSubBoxNumber(); i++) {
            griditem = (GridItem) getSubBox(i);
            if ((griditem.gridItemRowColumnValue.columnEnd - griditem.gridItemRowColumnValue.columnStart) > 1) {
                griditem.widthcolumnsforitems = 0;
                for (int j = griditem.gridItemRowColumnValue.columnStart; j < griditem.gridItemRowColumnValue.columnEnd; j++) {
                    try {
                        griditem.widthcolumnsforitems += arrayofcolumns.get(j - 1);
                    } catch (IndexOutOfBoundsException e) {
                        if (gridAutoColumns != 0) {
                            griditem.widthcolumnsforitems += gridAutoColumns;
                        } else {
                            griditem.widthcolumnsforitems += gapColumn;
                        }
                    }
                }
                griditem.widthcolumnsforitems += ((griditem.gridItemRowColumnValue.columnEnd - griditem.gridItemRowColumnValue.columnStart) - 1) * gapColumn;
            }
        }
    }


    /**
     * Calculates size of auto value
     * First detects auto value and calculates the appropriate values and then
     * auto value (in pixels) is stored in the list
     *
     * @return true if contains auto, false if not
     */
    public boolean processColumnAuto() {
        int content = getContentWidth();
        int size = 0;
        int countgaps;
        int countofauto = 0;
        int finalautovalue;

        for (int i = 0; i < arrayofcolumns.size(); i++) {
            if (arrayofcolumns.get(i) != 0) {
                size += arrayofcolumns.get(i);
            } else {
                countofauto++;
            }
        }
        countgaps = (arrayofcolumns.size() - 1) * gapColumn;

        if (countofauto != 0) {
            finalautovalue = (content - size - countgaps) / countofauto;

            System.out.println("coutnt" + finalautovalue);
            for (int i = 0; i < arrayofcolumns.size(); i++) {
                if (arrayofcolumns.get(i) == 0) {
                    arrayofcolumns.remove(i);
                    arrayofcolumns.add(i, finalautovalue);
                }
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Checks if contains only unit tracks
     *
     * @param tmp size and count of grid tracks
     * @return false if not contains only units, true if contins
     */
    public boolean containsOnlyUnit(TermList tmp) {
        if (tmp != null) {
            for (int i = 0; i < tmp.size(); i++) {
                if (tmp.get(i).getValue().toString().equals("auto") ||
                        tmp.get(i).getValue().toString().equals("min-content") ||
                        tmp.get(i).getValue().toString().equals("max-content")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Calculate how many repeated sizes is specified.
     */
    public void containsRepeat() {
        //very basic solution of repeat notation
        RepeatImpl repeat;
        Term term;
        if (gridTemplateColumnsValues != null) {
            term = gridTemplateColumnsValues.get(0);
            try {
                repeat = (RepeatImpl) term;
            } catch (Exception e) {
                  return;
            }
            TermFunction.Repeat.Unit a = repeat.getNumberOfRepetitions();
            gridTemplateColumnsValues = null;
            CSSDecoder dec = new CSSDecoder(ctx);
            for (int i = 0; i < a.getNumberOfRepetitions(); i++) {
                arrayofcolumns.add(i, dec.getLength((TermLengthOrPercent) repeat.getRepeatedTerms().get(0), false, 0, 0, getContentWidth()));
            }
        }
    }
}