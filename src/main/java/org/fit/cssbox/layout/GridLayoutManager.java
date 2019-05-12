package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

/**
 * This class represents layout manager for Grid layout
 */
public class GridLayoutManager implements LayoutManager {

    /** Grid Wrapper Block Box*/
    private GridWrapperBlockBox gridbox;

    /**
     * Create a new instance of Grid layout manager
     * @param gridbox grid wrapper
     */
    public GridLayoutManager(GridWrapperBlockBox gridbox) {
        this.gridbox = gridbox;
    }


    /**
     * Layout the sub-elements.
     *
     * @param availw    Maximal width available to the child elements
     * @param force     Use the area even if the used width is greater than maxwidth
     * @param linestart Indicates whether the element is placed at the line start
     * @return <code>true</code> if the box has been succesfully placed
     */
    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("DoLayout -> GridLayoutManager");

        if (!gridbox.displayed) {
            gridbox.getContent().setSize(0, 0);
            gridbox.getBounds().setSize(0, 0);
            return true;
        }
        int myconth = 0;

        System.out.println("contw: " + gridbox.getContentWidth());
        System.out.println("conth: " + gridbox.getContentHeight());
        CSSDecoder dec = new CSSDecoder(gridbox.ctx);
        System.out.println("content width: " + gridbox.content.width);
        System.out.println("total height: " + gridbox.totalHeight());
        System.out.println("content height: " + gridbox.content.height);

        gridbox.isGridAutoColumn = gridbox.isGridAutoColumns(gridbox.getContentWidth());
        gridbox.isGridAutoRow = gridbox.isGridAutoRows(gridbox.getContentHeight());

        gridbox.containsRepeat();
        gridbox.setSizeofGrid();
        gridbox.processAutomaticItems();

        System.out.println("nejvetsi linka sloupce: " + gridbox.maxColumnLine);
        System.out.println("nejvetsi linka radku: " + gridbox.maxRowLine);

        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);



            if (gridbox.isGridAutoColumn || !gridbox.isGridTemplateColumnsNone) {
                if (gridbox.findUnitsForFr(gridbox.gridTemplateColumnsValues, dec, gridbox.gapColumn, gridbox.getContentWidth())) {
                    gridbox.oneFrUnitColumn = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.content.width);
                    System.out.println("1fr pro column je: " + gridbox.oneFrUnitColumn + "px");
                }
                gridbox.flexFactorSum = 0;
                gridbox.sumofpixels = 0;
            }



            //zjistuju sirku grid itemu
            griditem.setWidthOfItem(gridbox);
            griditem.setAvailableWidth(griditem.widthcolumnsforitems);

            System.out.println(griditem.toString());
            System.out.println("available width po zjisteni sirky: " + griditem.widthcolumnsforitems);

            if (gridbox.getContentHeight() <= 0) {
                if (!gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues != null) {
                    gridbox.findUnitsForFr(gridbox.gridTemplateRowsValues, dec, gridbox.gapRow, gridbox.getContentHeight());
                    myconth += gridbox.sumofpixels; /*+ griditem.getContentHeight() + griditem.padding.top + griditem.padding.bottom + griditem.margin.top + griditem.margin.bottom;*/
                    gridbox.sumofpixels = 0;
                    gridbox.flexFactorSum = 0;
                    gridbox.setContentHeight(myconth);
                }
            }

            if (!gridbox.isGridAutoRow && gridbox.isGridTemplateRows) {
                if (gridbox.findUnitsForFr(gridbox.gridTemplateRowsValues, dec, gridbox.gapRow, gridbox.getContentHeight())) {//tady pak zadavat conth pro vysku
                    gridbox.oneFrUnitRow = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.getContentHeight());
                    System.out.println("1fr pro row je: " + gridbox.oneFrUnitRow + "px");
                }
                gridbox.flexFactorSum = 0;
                gridbox.sumofpixels = 0;
            }
        }
        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);
            gridbox.checkColumnLine(griditem.gridItemRowColumnValue.columnStart);
        }

        gridbox.fillColumnsSizesToArray();
        boolean isAuto = gridbox.processColumnAuto();
        if (isAuto) {
            for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
                GridItem griditem = (GridItem) gridbox.getSubBox(i);
                if (griditem.widthcolumnsforitems == 0) {
                    griditem.widthcolumnsforitems = gridbox.arrayofcolumns.get(griditem.gridItemRowColumnValue.columnStart-1);
                    System.out.println("/////////////////////////////////////////////////////////////////////jsem to ja" + griditem.toString());
                    griditem.setAvailableWidth(griditem.widthcolumnsforitems);
                }
            }
        }

        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);
            gridbox.checkNewSizeOfColumnsBigItems();
            if (!griditem.contblock) {
                griditem.layoutInline();
            } else {
                griditem.layoutBlocks();
            }
            griditem.setHeightOfItem(gridbox);
        }

        //pokud nejsou v radku ci sloupci stejne velke itemy uprav je na nejvyssi rozmer
        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);
            gridbox.checkRowLine(griditem.gridItemRowColumnValue.rowStart);
//            gridbox.checkColumnLine(griditem.gridItemRowColumnValue.columnStart);
        }

        gridbox.fillRowsSizesToArray();
//        gridbox.fillColumnsSizesToArray();
//        boolean isAuto = gridbox.processColumnAuto();
//        if (isAuto) {
//            for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
//                GridItem griditem = (GridItem) gridbox.getSubBox(i);
//                if (griditem.widthcolumnsforitems == 0) {
//                    griditem.widthcolumnsforitems = gridbox.arrayofcolumns.get(griditem.gridItemRowColumnValue.columnStart-1);
//                    System.out.println("/////////////////////////////////////////////////////////////////////jsem to ja" + griditem.toString());
//                    griditem.setAvailableWidth(griditem.widthcolumnsforitems);
//                }
//            }
//        }

        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);

            if (!gridbox.checkBeforeNewSizes()) {
                gridbox.checkNewSizeOfRowsBigItems();
//                gridbox.checkNewSizeOfColumnsBigItems();
            }

            griditem.setDistanceInHorizontalDirection(gridbox);
            griditem.setDistanceInVerticalDirection(gridbox);

            System.out.println(griditem.toString());
            System.out.println("distance column from zero: " + griditem.columndistancefromzero);
            System.out.println("distance row from zero: " + griditem.rowdistancefromzero);
            System.out.println("Width column: " + griditem.widthcolumnsforitems);
            System.out.println("width row: " + griditem.widthrowsforitems);
            System.out.println("contblock: " + griditem.contblock);
            System.out.println("souradnice itemu> radek start: " + griditem.gridItemRowColumnValue.rowStart + " sloupec start: " + griditem.gridItemRowColumnValue.columnStart);
            System.out.println("souradnice itemu> radek end: " + griditem.gridItemRowColumnValue.rowEnd + " sloupec end: " + griditem.gridItemRowColumnValue.columnEnd);
            System.out.println("*****************************");

            /*
             * zde probíhá vykreslování itemu
             */
            griditem.content.width = griditem.widthcolumnsforitems - griditem.margin.left - griditem.margin.right - griditem.border.left - griditem.border.right - griditem.padding.left - griditem.padding.right;
            griditem.content.height = griditem.widthrowsforitems - griditem.margin.top - griditem.margin.bottom - griditem.border.top - griditem.border.bottom - griditem.padding.top - griditem.padding.bottom;

            griditem.bounds.width = griditem.widthcolumnsforitems;
            griditem.bounds.height = griditem.widthrowsforitems;

            griditem.bounds.x = griditem.columndistancefromzero;
            griditem.bounds.y = griditem.rowdistancefromzero;

            griditem.setPosition(griditem.bounds.x, griditem.bounds.y);

            /*
             * zde probíhá určení výsledné velikosti kontejneru a následné vykreslení
             */
            if (gridbox.style.getProperty("height") == CSSProperty.Height.AUTO || gridbox.style.getProperty("height") == null) {
                if (gridbox.max_size.height == -1) {
                    if (griditem.bounds.y + griditem.totalHeight() > gridbox.getContentHeight()) {
                        gridbox.setContentHeight(griditem.bounds.y + griditem.totalHeight());
                    }
                } else {
                    if (griditem.bounds.y + griditem.totalHeight() < gridbox.max_size.height) {
                        if (griditem.bounds.y + griditem.totalHeight() > gridbox.getContentHeight()) {
                            gridbox.setContentHeight(griditem.bounds.y + griditem.totalHeight());
                        }
                    } else {
                        gridbox.setContentHeight(gridbox.max_size.height);
                    }
                }
            }
        }
        if (gridbox.getSizeOfArrayOfRows() > gridbox.content.height) gridbox.content.height = gridbox.getSizeOfArrayOfRows();

        System.out.println("content vyska kontejneru> " + gridbox.content.height);
        gridbox.bounds.height += gridbox.content.height + gridbox.emargin.top + gridbox.emargin.bottom +
                gridbox.padding.top + gridbox.padding.bottom + gridbox.border.top + gridbox.border.bottom;
        System.out.println("gridbox bounds height>" + gridbox.bounds.height);

        return true;
    }
}