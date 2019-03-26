package org.fit.cssbox.layout;

import cz.vutbr.web.css.*;
import org.w3c.dom.Element;

import java.awt.*;


public class GridWrapperBlockBox extends BlockBox {

    public static final CSSProperty.GridTemplateAreas GRID_TEMPLATE_AREAS_NONE = CSSProperty.GridTemplateAreas.NONE;
    public static final CSSProperty.GridTemplateAreas GRID_TEMPLATE_AREAS_LIST = CSSProperty.GridTemplateAreas.list_values;

    public static final CSSProperty.GridAutoRowsColumns GRID_AUTO_ROWS_COLUMNS_AUTO = CSSProperty.GridAutoRowsColumns.AUTO;
    public static final CSSProperty.GridAutoRowsColumns GRID_AUTO_ROWS_COLUMNS_LENGTH = CSSProperty.GridAutoRowsColumns.length;
    public static final CSSProperty.GridAutoRowsColumns GRID_AUTO_ROWS_COLUMNS_LIST = CSSProperty.GridAutoRowsColumns.list_values;

    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_ROW = CSSProperty.GridAutoFlow.ROW;
    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_COLUMN = CSSProperty.GridAutoFlow.COLUMN;
    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_DENSE = CSSProperty.GridAutoFlow.DENSE;
    public static final CSSProperty.GridAutoFlow GRID_AUTO_FLOW_COMPONENT = CSSProperty.GridAutoFlow.component_values;


    public static final CSSProperty.Grid GRID_NONE = CSSProperty.Grid.NONE;
    public static final CSSProperty.Grid GRID_AUTO_FLOW = CSSProperty.Grid.AUTO_FLOW;
    public static final CSSProperty.Grid GRID_COMPONENT = CSSProperty.Grid.component_values;

    private CSSProperty.GridTemplateAreas gridTemplateAreas;
    private CSSProperty.GridAutoRowsColumns gridAutoRowsColumns;
    private CSSProperty.GridAutoFlow gridAutoFlow;
    private CSSProperty.Grid grid;


    //tyto urcite zustanou
    protected TermList gridTemplateRowsValues;
    protected TermList gridTemplateColumnsValues;
    protected TermList gridTemplateAreasValues;
    protected TermLength.Unit unit;
    protected int gapRow;
    protected int gapColumn;
    protected int flexFactorSum;
    protected int sumofpixels;

    public GridWrapperBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        typeoflayout = new GridLayoutManager(this);
        isblock = true;
        flexFactorSum = 0;
        gapColumn = 0;
        gapRow = 0;
    }

    public GridWrapperBlockBox(InlineBox src) {
        super(src);
        typeoflayout = new GridLayoutManager(this);
        isblock = true;
        sumofpixels = 0;
        gapColumn = 0;
        gapRow = 0;
    }

    @Override
    public void setStyle(NodeData s) {
        super.setStyle(s);
        loadGridWrapperStyles();
    }

    public void loadGridWrapperStyles() {
        CSSDecoder dec = new CSSDecoder(ctx);


        CSSProperty.GridTemplateAreas gridTemplateAreas = style.getProperty("grid-template-areas");
        if (gridTemplateAreas == null) gridTemplateAreas = CSSProperty.GridTemplateAreas.list_values;
        else if (gridTemplateAreas == CSSProperty.GridTemplateAreas.list_values) {
            gridTemplateAreasValues = style.getValue(TermList.class, "grid-template-areas");
            System.out.println("grid-template-areas: " + gridTemplateAreasValues);
        } else if (gridTemplateAreas == CSSProperty.GridTemplateAreas.NONE) {
            System.out.println("grid-template-rows: none.");
        }

//        GridGapProcessing(dec);
        GridTemplateRowsColumnsProcessing(dec);
        System.out.println("\n\n\n");
    }

    protected void GridGapProcessing(CSSDecoder dec) {

        CSSProperty.GridGap gridColumnGap = style.getProperty("grid-column-gap");
        CSSProperty.GridGap gridRowGap = style.getProperty("grid-row-gap");

        if (gridRowGap == null) gridRowGap = CSSProperty.GridGap.component_values;
        else if (gridRowGap == CSSProperty.GridGap.length) {
            gapRow = dec.getLength(getLengthValue("grid-row-gap"), false, 0, 0, 0);
            System.out.println("grid-row-gap: " + gapRow);
        } else if (gridRowGap == CSSProperty.GridGap.NORMAL) {
            System.out.println("grid-row-gap: normal; prevedeno na 0");
            gapRow = 0;
        }

        if (gridColumnGap == null) gridColumnGap = CSSProperty.GridGap.component_values;
        else if (gridColumnGap == CSSProperty.GridGap.length) {
            gapColumn = dec.getLength(getLengthValue("grid-column-gap"), false, 0, 0, 0);
            System.out.println("grid-column-gap: " + gapColumn);
        } else if (gridColumnGap == CSSProperty.GridGap.NORMAL) {
            System.out.println("rid-column-gap: normal; prevedeno na 0");
            gapColumn = 0;
        }
    }

    protected void GridTemplateRowsColumnsProcessing(CSSDecoder dec) {
        //zpracovani grid-template-rows
        CSSProperty.GridTemplateRowsColumns gridTemplateRows = style.getProperty("grid-template-rows");
        if (gridTemplateRows == null) gridTemplateRows = CSSProperty.GridTemplateRowsColumns.list_values;
        else if (gridTemplateRows == CSSProperty.GridTemplateRowsColumns.list_values) {
            gridTemplateRowsValues = style.getValue(TermList.class, "grid-template-rows");
            System.out.println("grid-template-rows: " + gridTemplateRowsValues);
        } else if (gridTemplateRows == CSSProperty.GridTemplateRowsColumns.AUTO) {
            System.out.println("grid-template-rows: auto.");
        } else {
            System.out.println("grid-template-rows: none");
        }

        //zpracovani grid-template-columns
        CSSProperty.GridTemplateRowsColumns gridTemplateColumns = style.getProperty("grid-template-columns");
        if (gridTemplateColumns == null) gridTemplateColumns = CSSProperty.GridTemplateRowsColumns.list_values;
        else if (gridTemplateColumns == CSSProperty.GridTemplateRowsColumns.list_values) {
            gridTemplateColumnsValues = style.getValue(TermList.class, "grid-template-columns");
//            for (int i = 0; i < gridTemplateColumnsValues.size(); i++) {
//                findUnitsForFr((TermLengthOrPercent) gridTemplateColumnsValues.get(i), dec);
//            }
//            findUnitsForFr(gridTemplateColumnsValues, dec);
//            computingFrUnits(flexFactorSum, sumofpixels);
           // findUnitsForFr(gridTemplateColumnsValues, dec);

            System.out.println("grid-template-columns: " + gridTemplateColumnsValues);
        } else if (gridTemplateColumns == CSSProperty.GridTemplateRowsColumns.AUTO) {
            System.out.println("grid-template-columns: auto.");
        } else {
            System.out.println("grid-template-columns: none");
        }
    }

    protected boolean findUnitsForFr(TermList tmp, CSSDecoder dec, int gap) {
        TermLengthOrPercent a;
        boolean containFr = false;
        int b;
        int j = 0;
        for (int i = 0; i < tmp.size(); i++) {
            a = (TermLengthOrPercent) tmp.get(i);
            unit = a.getUnit();

            if (unit == TermNumeric.Unit.fr) {
                b = dec.getLength(a, false, 0, 0, 0);
                flexFactorSum += b;
                containFr = true;
            } else {
                b = dec.getLength(a, false, 0, 0, 0);
                sumofpixels += b;
            }
            j++;
        }
//        System.out.println("j: " + j);
        sumofpixels += (j - 1) * gap;
//        System.out.println("flex-faktor-sum je: " + flexFactorSum);
//        System.out.println("sum-of-pixels je: " + sumofpixels);
        return containFr;
    }

    protected int computingFrUnits(int flexFactorSum, int sumofpixels, int availableContent) {
        if (flexFactorSum == 0) return 0;
        else return (availableContent  - sumofpixels) / flexFactorSum;
    }
}
