package org.fit.cssbox.layout;

import cz.vutbr.web.css.TermLengthOrPercent;

import java.awt.*;

public class GridLayoutManager implements LayoutManager {

    private GridWrapperBlockBox gridbox;

    public GridLayoutManager(GridWrapperBlockBox gridbox) {
        this.gridbox = gridbox;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("DoLayout -> GridLayoutManager");
        int oneFrUnitColumn;
        int oneFrUnitRow;

        //fsdfsdf

        CSSDecoder dec = new CSSDecoder(gridbox.ctx);


//        for (int i = 0; i < gridbox.endChild; i++) {
//            System.out.println("pocet deti: " + gridbox.getSubBox(i).toString());
//        }
//        System.out.println("vyska content " + gridbox.content.getHeight());
//        System.out.println("sirka content " + gridbox.content.getWidth());

//        int contw = gridbox.getContainingBlock().width;
        System.out.println("content width: " + gridbox.content.width);
        gridbox.GridGapProcessing(dec);


        if (gridbox.findUnitsForFr(gridbox.gridTemplateColumnsValues, dec, gridbox.gapColumn)) {
            oneFrUnitColumn = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.content.width);
            gridbox.flexFactorSum = 0;
            gridbox.sumofpixels = 0;
            System.out.println("1fr pro column je: " + oneFrUnitColumn + "px");
        }


        if (gridbox.findUnitsForFr(gridbox.gridTemplateRowsValues, dec, gridbox.gapRow)) {
            oneFrUnitRow = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.content.height);
            gridbox.flexFactorSum = 0;
            gridbox.sumofpixels = 0;
        }

        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);

            for (int j = griditem.gridItemRowColumnValue.columnStart -1; j < griditem.gridItemRowColumnValue.columnEnd -1; j++) {
                griditem.widthcolumnsforitems += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j), false, 0, 0, 0);
            }

            for (int j = griditem.gridItemRowColumnValue.rowStart -1; j < griditem.gridItemRowColumnValue.rowEnd -1; j++) {
                griditem.widthrowsforitems += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j), false, 0, 0, 0);
            }

            for (int j = 0; j < griditem.gridItemRowColumnValue.columnStart-1; j++) {
                griditem.columndistancefromzero += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j), false, 0, 0, 0);
            }

            for (int j = 0; j < griditem.gridItemRowColumnValue.rowStart - 1; j++) {
                griditem.rowdistancefromzero += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j), false, 0, 0, 0);
            }

            System.out.println("distance column from zero: " + griditem.columndistancefromzero);
            System.out.println("distance row from zero: " + griditem.rowdistancefromzero);
            System.out.println("Width column: " + griditem.widthcolumnsforitems);
            System.out.println("width row: " + griditem.widthrowsforitems);
            System.out.println("*****************************");

            griditem.content.width = griditem.widthcolumnsforitems - griditem.margin.left - griditem.margin.right - griditem.border.left - griditem.border.right - griditem.padding.left - griditem.padding.right;
            griditem.content.height = griditem.widthrowsforitems - griditem.margin.top - griditem.margin.bottom - griditem.border.top - griditem.border.bottom - griditem.padding.top - griditem.padding.bottom;

            griditem.bounds.width = griditem.widthcolumnsforitems;
            griditem.bounds.height = griditem.widthrowsforitems;

            griditem.bounds.x = griditem.columndistancefromzero;
            griditem.bounds.y = griditem.rowdistancefromzero;

            griditem.setPosition(griditem.bounds.x, griditem.bounds.y);

//            griditem.bounds = new Rectangle(10, 50, 20, 60);
        }


//        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
//            GridItem subbox = (GridItem) gridbox.getSubBox(i);
//            System.out.println("czzz: " + subbox.gridStartEnd);
//            if (subbox.gridStartEnd  == CSSProperty.GridStartEnd.valueOf("number")) {
//                subbox.gridStartEndValue = dec.getLength(subbox.getLengthValue("grid-column-start"), false, 0, 0, contw);
//                System.out.println("Sloupec: " +subbox.gridStartEndValue);
//               // System.out.println("JSEM V IF");
//            } else
//                System.out.println("fdsfdsfds");
////            System.out.println(subbox.getSubBox(i).toString());
//        }
        return true;
    }
}
