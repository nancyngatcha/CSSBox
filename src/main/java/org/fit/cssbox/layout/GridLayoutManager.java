package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import java.util.ArrayList;

public class GridLayoutManager implements LayoutManager {

    private GridWrapperBlockBox gridbox;

    public GridLayoutManager(GridWrapperBlockBox gridbox) {
        this.gridbox = gridbox;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("DoLayout -> GridLayoutManager");

        if (!gridbox.displayed) {
            gridbox.getContent().setSize(0, 0);
            gridbox.getBounds().setSize(0, 0);
            return true;
        }

//        int contw = gridbox.getContentWidth();
//        int conth = gridbox.getContentHeight();
        int countGridColumnGapsInItem = 0;
        int countGridRowGapsInItem = 0;
        int oneFrUnitColumn = 0;  //presunout do kontejneru ----------------------------------------------------------------------------
        int oneFrUnitRow = 0;
        int myconth = 0;
        int itemcontheight = 0;

        System.out.println("contw: " + gridbox.getContentWidth());
        System.out.println("conth: " + gridbox.getContentHeight());
        CSSDecoder dec = new CSSDecoder(gridbox.ctx);
        //gridbox.GridGapProcessing(dec);
        System.out.println("content width: " + gridbox.content.width);
        System.out.println("total height: " + gridbox.totalHeight());
        System.out.println("content height: " + gridbox.content.height);

        gridbox.isGridAutoColumn = gridbox.isGridAutoColumns(gridbox.getContentWidth(), dec);
        gridbox.isGridAutoRow = gridbox.isGridAutoRows(gridbox.getContentHeight(), dec);


        /*
         * Zkouska na zjisteni velikosti mrizky atd.
         */
//        for (int i = 1; i <= gridbox.getSubBoxNumber(); i++) {
////            GridItem griditem = (GridItem) gridbox.getSubBox(i - 1);
////            if (griditem.gridItemRowColumnValue.columnEnd > gridbox.maxColumnLine) {
////                gridbox.maxColumnLine = griditem.gridItemRowColumnValue.columnEnd;
////            } else if (griditem.gridItemRowColumnValue.columnEnd == 0 && griditem.gridItemRowColumnValue.columnStart == 0 && gridbox.maxColumnLine == 1){
////                if (gridbox.isGridAutoFlowRow) {
////                    if (gridbox.isGridTemplateColumns) {
////                        gridbox.maxColumnLine = gridbox.gridTemplateColumnsValues.size() + 1;
////                    } else {
////                        gridbox.maxColumnLine = 2;
////                    }
////                } else {
////                    gridbox.maxColumnLine = i + 1 ;
////                }
////            }
////            if (griditem.gridItemRowColumnValue.rowEnd > gridbox.maxRowLine) {
////                gridbox.maxRowLine = griditem.gridItemRowColumnValue.rowEnd;
////            } else if (griditem.gridItemRowColumnValue.rowEnd == 0 && griditem.gridItemRowColumnValue.rowStart == 0 && gridbox.maxRowLine == 1){
////                if (!gridbox.isGridAutoFlowRow) {
////                    if (gridbox.isGridTemplateRows) {
////                        gridbox.maxRowLine = gridbox.gridTemplateRowsValues.size() + 1;
////                    } else {
////                        gridbox.maxRowLine = 2;
////                    }
////                } else {
////                    gridbox.maxRowLine = i + 1;
////                }
////            }
////        }

        gridbox.setSizeofGrid();

        //toto je mrizka s pevnyma itemama a jen hledam volny prostory + kdyztak rozsiruju mrizku
        //+ dodelano i pro auto itemy
        if (gridbox.isGridAutoFlowRow) {
            //  if (gridbox.maxColumnLine != 1 && gridbox.maxRowLine != 1) {
            for (int a = 1; a < gridbox.maxRowLine; a++) {
                ArrayList<Integer> columnID = new ArrayList<>();
                for (int b = 1; b < gridbox.maxColumnLine; b++) {
                    columnID.add(b);
                }
                for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
                    GridItem griditem = (GridItem) gridbox.getSubBox(i);
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
                        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
                            GridItem griditem = (GridItem) gridbox.getSubBox(i);
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
                for (int w = 0; w < gridbox.getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) gridbox.getSubBox(w);
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
                for (int w = 0; w < gridbox.getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) gridbox.getSubBox(w);
                    if (griditem.gridItemRowColumnValue.rowStart == 0 && griditem.gridItemRowColumnValue.columnStart == 0) {
                        if (a <= gridbox.maxRowLine) {
                            gridbox.maxRowLine += 1;
                            break;
                        }
                    }
                }
            }
            //   }
        } else {
            /*if (gridbox.maxColumnLine != 1 && gridbox.maxRowLine != 1) {*/
            for (int a = 1; a < gridbox.maxColumnLine; a++) {
                ArrayList<Integer> rowID = new ArrayList<>();
                for (int b = 1; b < gridbox.maxRowLine; b++) {
                    rowID.add(b);
                }
                for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
                    GridItem griditem = (GridItem) gridbox.getSubBox(i);
                    if (griditem.gridItemRowColumnValue.columnStart == a) {
                        for (int c = griditem.gridItemRowColumnValue.rowStart; c < griditem.gridItemRowColumnValue.rowEnd; c++) {
                            rowID.remove((Integer) c);
                        }
                    }
                }
                if (a > 1) {
                    for (int y = 1; y < a; y++) {
                        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
                            GridItem griditem = (GridItem) gridbox.getSubBox(i);
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
                for (int w = 0; w < gridbox.getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) gridbox.getSubBox(w);
                    if (griditem.gridItemRowColumnValue.columnStart == 0 && griditem.gridItemRowColumnValue.rowStart == 0) {
                        if (rowID.isEmpty()) break;
                        griditem.gridItemRowColumnValue.columnStart = a;
                        griditem.gridItemRowColumnValue.columnEnd = a + 1;
                        griditem.gridItemRowColumnValue.rowStart = rowID.get(0);
                        rowID.remove(0);
                        griditem.gridItemRowColumnValue.rowEnd = griditem.gridItemRowColumnValue.rowStart + 1;
                    }
                }
                for (int w = 0; w < gridbox.getSubBoxNumber(); w++) {
                    GridItem griditem = (GridItem) gridbox.getSubBox(w);
                    if (griditem.gridItemRowColumnValue.rowStart == 0 && griditem.gridItemRowColumnValue.columnStart == 0) {
                        if (a < gridbox.maxColumnLine) {
                            gridbox.maxColumnLine += 1;
                            break;
                        }
                    }
                }
            }
            //}
        }
        System.out.println("nejvetsi linka sloupce: " + gridbox.maxColumnLine);
        System.out.println("nejvetsi linka radku: " + gridbox.maxRowLine);

        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
            GridItem griditem = (GridItem) gridbox.getSubBox(i);

            if (!gridbox.isGridAutoColumn && !gridbox.isGridTemplateColumnsNone) {
                if (gridbox.findUnitsForFr(gridbox.gridTemplateColumnsValues, dec, gridbox.gapColumn, gridbox.getContentWidth())) {
                    oneFrUnitColumn = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.content.width);
                    System.out.println("1fr pro column je: " + oneFrUnitColumn + "px");
                }
                gridbox.flexFactorSum = 0;
                gridbox.sumofpixels = 0;
            }


            //zjistuju sirku grid itemu
            for (int j = griditem.gridItemRowColumnValue.columnStart - 1; j < griditem.gridItemRowColumnValue.columnEnd - 1; j++) {
                if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues == null) {
                    griditem.widthcolumnsforitems += gridbox.gridAutoColumns;
                } else if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues != null) {
                    // nejakej pokus o min-content ale zatim neuspesne
                    // if (gridbox.isMinContentAutoColumn) {
                    //     griditem.widthcolumnsforitems = griditem.getMinimalContentWidth() + griditem.padding.left + griditem.padding.right + griditem.margin.left + griditem.margin.right;
                    // } else {
                    if ((j + 1) >= gridbox.gridTemplateColumnsValues.size() + 1) {
                        griditem.widthcolumnsforitems += gridbox.gridAutoColumns;
                    } else if ((j + 1) < gridbox.gridTemplateColumnsValues.size()) {
                        griditem.widthcolumnsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, oneFrUnitColumn, gridbox.getContentWidth());
                    } else {
                        griditem.widthcolumnsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, oneFrUnitColumn, gridbox.getContentWidth());
                    }
                    // }
                } else {
                    if (gridbox.isGridTemplateColumnsNone) {
                        griditem.widthcolumnsforitems = gridbox.getContentWidth();
                    } else {
                        griditem.widthcolumnsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, oneFrUnitColumn, gridbox.getContentWidth());
                    }
                }
                countGridColumnGapsInItem = griditem.gridItemRowColumnValue.columnEnd - griditem.gridItemRowColumnValue.columnStart - 1;
            }
            griditem.widthcolumnsforitems += countGridColumnGapsInItem * gridbox.gapColumn;

//            if (griditem.style.getProperty("width") != null) {
//                System.out.println("jsem tu-------------------------------------------------------------------------");
//                griditem.widthcolumnsforitems = dec.getLength(griditem.getLengthValue("width"), false, 0, 0, gridbox.getContentWidth());
//                System.out.println("nov8 veliksot je> " + griditem.widthcolumnsforitems);
//            }


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
                    oneFrUnitRow = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.getContentHeight());
                    System.out.println("1fr pro row je: " + oneFrUnitRow + "px");
                }
                gridbox.flexFactorSum = 0;
                gridbox.sumofpixels = 0;
            }

            if (!griditem.contblock) {
                griditem.layoutInline();
            } else {
                griditem.layoutBlocks();
            }

            //plati jen pro grid-template-rows: none;
            //nedokonaly plati jen jednosmerne, coz znamena pokud nejaky pozdejsi item bude vetsi, predesly se dle nej uz nezvetsi
//            if (!gridbox.isGridTemplateRows) {
//                if (itemcontheight < griditem.getSubBox(griditem.getSubBoxNumber() - 1).getContentY() + griditem.getSubBox(griditem.getSubBoxNumber() - 1).getContentHeight())
//                    itemcontheight = griditem.getSubBox(griditem.getSubBoxNumber() - 1).getContentY() + griditem.getSubBox(griditem.getSubBoxNumber() - 1).getContentHeight();
//            }

            //zjistuju vysku grid itemu
            for (int j = griditem.gridItemRowColumnValue.rowStart - 1; j < griditem.gridItemRowColumnValue.rowEnd - 1; j++) {
                if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues == null) {
                    griditem.widthrowsforitems += gridbox.gridAutoRows;
                } else if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues != null) {
                    if ((j + 1) >= gridbox.gridTemplateRowsValues.size() + 1) {
                        griditem.widthrowsforitems += gridbox.gridAutoRows;
                    } else if ((j + 1) < gridbox.gridTemplateRowsValues.size()) {
                        griditem.widthrowsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, oneFrUnitRow, gridbox.getContentHeight());
                    } else {
                        griditem.widthrowsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, oneFrUnitRow, gridbox.getContentHeight());
                    }
                } else {
                    if (!gridbox.isGridTemplateRows) {
                        if (gridbox.style.getProperty("min-height") != null) {
                            griditem.widthrowsforitems = dec.getLength(gridbox.getLengthValue("min-height"), false, 0,0,gridbox.getContentHeight());
                        } else if (gridbox.style.getProperty("height") != null) {
                            griditem.widthrowsforitems = dec.getLength(gridbox.getLengthValue("height"), false, 0,0,gridbox.getContentHeight());
                        } else if (gridbox.style.getProperty("max-height") != null) {
                            griditem.widthrowsforitems = dec.getLength(gridbox.getLengthValue("max-height"), false, 0,0,gridbox.getContentHeight());
                        } else {
                            griditem.widthcolumnsforitems = griditem.getSubBox(griditem.getSubBoxNumber() - 1).getContentY() + griditem.getSubBox(griditem.getSubBoxNumber() - 1).getContentHeight();
                        }
//                        System.out.println("velikost vvysky je> " + griditem.widthrowsforitems);
                        System.out.println("velikost vvysky je> " + griditem.widthrowsforitems);
                    } else {
                        griditem.widthrowsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, oneFrUnitRow, gridbox.getContentHeight());
                    }
                }
                countGridRowGapsInItem = griditem.gridItemRowColumnValue.rowEnd - griditem.gridItemRowColumnValue.rowStart - 1;
            }
            griditem.widthrowsforitems += countGridRowGapsInItem * gridbox.gapRow;

            //zjistuju vzdalenost od 0 v horizontalnim smeru
            for (int j = 0; j < griditem.gridItemRowColumnValue.columnStart - 1; j++) {
                if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues == null) {
                    griditem.columndistancefromzero += gridbox.gridAutoColumns;
                } else if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues != null) {
                    if (griditem.gridItemRowColumnValue.columnStart >= gridbox.gridTemplateColumnsValues.size() + 1) {
                        griditem.columndistancefromzero = gridbox.sumOfLengthForGridTemplateColumnRow(dec, gridbox.gridTemplateColumnsValues, oneFrUnitColumn, gridbox.getContentWidth());
                        griditem.w++;
                    } else {
                        griditem.columndistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, oneFrUnitColumn, gridbox.getContentWidth());
                    }
                } else {
                    griditem.columndistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, oneFrUnitColumn, gridbox.getContentWidth());
                }
            }
            griditem.columndistancefromzero += (griditem.gridItemRowColumnValue.columnStart - 1) * gridbox.gapColumn;
            if (griditem.w != 0) {
                griditem.columndistancefromzero += (griditem.w - gridbox.gridTemplateColumnsValues.size()) * gridbox.gridAutoColumns;
            }


            //zjistuju vzdalenost od 0 ve vertikalnim smeru
            for (int j = 0; j < griditem.gridItemRowColumnValue.rowStart - 1; j++) {
                if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues == null) {
                    griditem.rowdistancefromzero += gridbox.gridAutoRows;
                } else if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues != null) {
                    if (griditem.gridItemRowColumnValue.rowStart >= gridbox.gridTemplateRowsValues.size() + 1) {
                        griditem.rowdistancefromzero = gridbox.sumOfLengthForGridTemplateColumnRow(dec, gridbox.gridTemplateRowsValues, oneFrUnitRow, gridbox.getContentHeight());
                        griditem.h++;
                    } else {
                        griditem.rowdistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, oneFrUnitRow, gridbox.getContentHeight());
                    }
                } else {
                    griditem.rowdistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, oneFrUnitRow, gridbox.getContentHeight());
                }
            }
            griditem.rowdistancefromzero += (griditem.gridItemRowColumnValue.rowStart - 1) * gridbox.gapRow;
            if (griditem.h != 0) {
                griditem.rowdistancefromzero += (griditem.h - gridbox.gridTemplateRowsValues.size()) * gridbox.gridAutoRows;
            }

            System.out.println("distance column from zero: " + griditem.columndistancefromzero);
            System.out.println("distance row from zero: " + griditem.rowdistancefromzero);
            System.out.println("Width column: " + griditem.widthcolumnsforitems);
            System.out.println("width row: " + griditem.widthrowsforitems);
            System.out.println("contblock: " + griditem.contblock);
            System.out.println("souradnice itemu> radek start: " + griditem.gridItemRowColumnValue.rowStart + " sloupec start: " + griditem.gridItemRowColumnValue.columnStart);
//            System.out.println("min content itemu je : " + (griditem.minContentValue));
//            System.out.println("min content itemu vcetne p a m:" + (griditem.getMinimalContentWidth() + griditem.padding.left + griditem.padding.right + griditem.margin.left + griditem.margin.right));
            System.out.println("*****************************");


//            if (!griditem.contblock) {
//                griditem.layoutInline();
//            } else {
//                griditem.layoutBlocks();
//            }

//            griditem.doLayout(griditem.widthcolumnsforitems, true, true);


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
                        myconth = griditem.bounds.y + griditem.totalHeight();
                        gridbox.setContentHeight(myconth);
                    }
                } else {
                    if (griditem.bounds.y + griditem.totalHeight() < gridbox.max_size.height) {
                        if (griditem.bounds.y + griditem.totalHeight() > gridbox.getContentHeight()) {
                            myconth = griditem.bounds.y + griditem.totalHeight();
                            gridbox.setContentHeight(myconth);
                        }
                    } else {
                        gridbox.setContentHeight(gridbox.max_size.height);
                    }
                }
            }
        }
        System.out.println("content vyska kontejneru> " + gridbox.content.height);
        gridbox.bounds.height += gridbox.content.height + gridbox.emargin.top + gridbox.emargin.bottom +
                gridbox.padding.top + gridbox.padding.bottom + gridbox.border.top + gridbox.border.bottom;
        System.out.println("gridbox bounds height>" + gridbox.bounds.height);
//        System.out.println(gridbox.parent.toString());
//        System.out.println(gridbox.parent.parent.bounds.height);

        return true;
    }
}