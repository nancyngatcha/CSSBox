package org.fit.cssbox.layout;

import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.css.TermNumeric;

public class GridLayoutManager implements LayoutManager {

    private GridWrapperBlockBox gridbox;

    public GridLayoutManager(GridWrapperBlockBox gridbox) {
        this.gridbox = gridbox;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("DoLayout -> GridLayoutManager");
        int oneFrUnitColumn = 0;
        int oneFrUnitRow = 0;

        CSSDecoder dec = new CSSDecoder(gridbox.ctx);

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
            System.out.println("1fr pro row je: " + oneFrUnitRow + "px");
        }

        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);
            TermLengthOrPercent a;
            int tmpForFrUnit;

            //zjistuju sirku grid itemu
            for (int j = griditem.gridItemRowColumnValue.columnStart - 1; j < griditem.gridItemRowColumnValue.columnEnd - 1; j++) {
                a = (TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j);
                gridbox.unit = a.getUnit();
                if (gridbox.unit != TermNumeric.Unit.fr) {
                    griditem.widthcolumnsforitems += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j), false, 0, 0, 0);
                } else {
                    tmpForFrUnit = dec.getLength((TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j), false, 0, 0, 0);
                    griditem.widthcolumnsforitems += (tmpForFrUnit * oneFrUnitColumn);
                }
            }
            //zjistuju vysku grid itemu
            for (int j = griditem.gridItemRowColumnValue.rowStart - 1; j < griditem.gridItemRowColumnValue.rowEnd - 1; j++) {
                a = (TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j);
                gridbox.unit = a.getUnit();
                if (gridbox.unit != TermNumeric.Unit.fr) {
                    griditem.widthrowsforitems += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j), false, 0, 0, 0);
                } else {
                    tmpForFrUnit = dec.getLength((TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j), false, 0, 0, 0);
                    griditem.widthrowsforitems += (tmpForFrUnit * oneFrUnitRow);
                }
            }
            //zjistuju vzdalenost od 0 v horizontalnim smeru
            for (int j = 0; j < griditem.gridItemRowColumnValue.columnStart - 1; j++) {
                a = (TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j);
                gridbox.unit = a.getUnit();
                if (gridbox.unit != TermNumeric.Unit.fr) {
                    griditem.columndistancefromzero += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j), false, 0, 0, 0);
                } else {
                    tmpForFrUnit = dec.getLength((TermLengthOrPercent) gridbox.gridTemplateColumnsValues.get(j), false, 0, 0, 0);
                    griditem.columndistancefromzero += (tmpForFrUnit * oneFrUnitColumn);
                }
            }
            //zjistuju vzdalenost od 0 ve vertikalnim smeru
            for (int j = 0; j < griditem.gridItemRowColumnValue.rowStart - 1; j++) {
                a = (TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j);
                gridbox.unit = a.getUnit();
                if (gridbox.unit != TermNumeric.Unit.fr) {
                    griditem.rowdistancefromzero += dec.getLength((TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j), false, 0, 0, 0);
                } else {
                    tmpForFrUnit = dec.getLength((TermLengthOrPercent) gridbox.gridTemplateRowsValues.get(j), false, 0, 0, 0);
                    griditem.rowdistancefromzero += (tmpForFrUnit * oneFrUnitRow);
                }
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
        }
        return true;
    }
}
