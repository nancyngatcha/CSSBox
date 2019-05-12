package org.fit.cssbox.layout;

import cz.vutbr.web.css.*;
import cz.vutbr.web.csskit.TermIdentImpl;
import org.w3c.dom.Element;

import java.awt.*;

/**
 * This class represents grid item, which is child of GridWrapperBlockBox
 *
 * @author Ondra
 */
public class GridItem extends BlockBox {

    public static final CSSProperty.GridStartEnd GRID_START_END_AUTO = CSSProperty.GridStartEnd.AUTO;
    public static final CSSProperty.GridStartEnd GRID_START_END_NUMBER = CSSProperty.GridStartEnd.number;
    public static final CSSProperty.GridStartEnd GRID_START_END_COMPONENT = CSSProperty.GridStartEnd.component_values;

    /**
     * Declare coordinates of grid item
     */
    protected GridItemRowColumn gridItemRowColumnValue;

    /**
     * Width of item
     */
    protected int widthcolumnsforitems;

    /**
     * Height of item
     */
    protected int widthrowsforitems;

    /**
     * Distance of grid item from left edge
     */
    protected int columndistancefromzero;

    /**
     * Distance of grid item from top edge
     */
    protected int rowdistancefromzero;

    /**
     * Indicates count of column gaps in explicit grid
     */
    protected int w;

    /**
     * Indicates count of row gaps in explicit grid
     */
    protected int h;

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
     * Loads styles for grid item
     */
    public void loadGridItemStyles() {
        gridItemRowColumnValue = new GridItemRowColumn();

        gridItemColumnProcessing();
        gridItemRowProcessing();

        System.out.println(gridItemRowColumnValue.toString());
    }

    /**
     * Loads column coordinates for grid items
     */
    protected void gridItemColumnProcessing() {
        CSSProperty.GridStartEnd gridStartEnd;
        CSSDecoder dec = new CSSDecoder(ctx);
        gridStartEnd = style.getProperty("grid-column-start");
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;

        if (gridStartEnd == GRID_START_END_NUMBER) {
            gridItemRowColumnValue.columnStart = dec.getLength(getLengthValue("grid-column-start"), false, 0, 0, 0);
        } else if (gridStartEnd == GRID_START_END_AUTO) {
            gridItemRowColumnValue.columnStart = 0;
        } else if (gridStartEnd == GRID_START_END_COMPONENT) {
            gridItemRowColumnValue.columnStartSpan = style.getValue(TermList.class, "grid-column-start");
        }

        gridStartEnd = style.getProperty("grid-column-end");
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;
        if (gridStartEnd == GRID_START_END_AUTO && gridItemRowColumnValue.columnStart != 0) {
            gridItemRowColumnValue.columnEnd = gridItemRowColumnValue.columnStart + 1;
        } else if (gridStartEnd == GRID_START_END_NUMBER) {
            gridItemRowColumnValue.columnEnd = dec.getLength(getLengthValue("grid-column-end"), false, 0, 0, 0);
            if (gridItemRowColumnValue.columnStart == gridItemRowColumnValue.columnEnd) {
                gridItemRowColumnValue.columnEnd = gridItemRowColumnValue.columnStart + 1;
            } else if (gridItemRowColumnValue.columnStart > gridItemRowColumnValue.columnEnd) {
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
     * Loads row coordinates for grid items
     */
    protected void gridItemRowProcessing() {
        CSSProperty.GridStartEnd gridStartEnd;
        CSSDecoder dec = new CSSDecoder(ctx);
        gridStartEnd = style.getProperty("grid-row-start");
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;

        if (gridStartEnd == GRID_START_END_NUMBER) {
            gridItemRowColumnValue.rowStart = dec.getLength(getLengthValue("grid-row-start"), false, 0, 0, 0);
        } else if (gridStartEnd == CSSProperty.GridStartEnd.identificator) {
            TermIdentImpl neco = style.getValue(TermIdentImpl.class, "grid-row-start");
        } else if (gridStartEnd == GRID_START_END_AUTO) {
            gridItemRowColumnValue.rowStart = 0;
        } else if (gridStartEnd == GRID_START_END_COMPONENT) {
            gridItemRowColumnValue.rowStartSpan = style.getValue(TermList.class, "grid-row-start");
        }

        gridStartEnd = style.getProperty("grid-row-end");
        if (gridStartEnd == null) gridStartEnd = GRID_START_END_AUTO;
        if (gridStartEnd == GRID_START_END_AUTO && gridItemRowColumnValue.rowStart != 0) {
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
            gridItemRowColumnValue.rowEndSpan = style.getValue(TermList.class, "grid-row-end");
            gridItemRowColumnValue.rowEnd = dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.rowEndSpan.get(1), false, 0, 0, 0) + gridItemRowColumnValue.rowStart;
        } else if (gridStartEnd == GRID_START_END_AUTO) {
            if (gridItemRowColumnValue.rowStartSpan != null) {
                gridItemRowColumnValue.rowStart = 1;
                gridItemRowColumnValue.rowEnd = dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.rowStartSpan.get(1), false, 0, 0, 0) + 1;
                gridItemRowColumnValue.rowStartSpan = null;
            } else {
                gridItemRowColumnValue.rowEnd = 0;
            }
        }

        if (gridItemRowColumnValue.rowStartSpan != null) {
            gridItemRowColumnValue.rowStart = gridItemRowColumnValue.rowEnd - dec.getLength((TermLengthOrPercent) gridItemRowColumnValue.rowStartSpan.get(1), false, 0, 0, 0);
        }
        if (gridItemRowColumnValue.rowStart == 0 && gridItemRowColumnValue.rowEnd != 0) {
            gridItemRowColumnValue.rowStart = gridItemRowColumnValue.rowEnd - 1;
        }
    }

    /**
     * Sets relevant width of grid items, inc. grid-gaps that intersects
     *
     * @param gridbox parrent box of grid item
     */
    public void setWidthOfItem(GridWrapperBlockBox gridbox) {
        CSSDecoder dec = new CSSDecoder(ctx);
        int countGridColumnGapsInItem;
        for (int j = gridItemRowColumnValue.columnStart - 1; j < gridItemRowColumnValue.columnEnd - 1; j++) {
            //very basic solution of repeat notation
            if (gridbox.gridTemplateColumnsValues == null && gridbox.arrayofcolumns.size() != 0) {
                widthcolumnsforitems += gridbox.arrayofcolumns.get(j);
                break;
            }

            if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues == null) {
                widthcolumnsforitems += gridbox.gridAutoColumns;
            } else if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues != null) {
                System.out.println("velikost explicitni mrizky: " + gridbox.gridTemplateColumnsValues.size());
                if ((j + 1) >= gridbox.gridTemplateColumnsValues.size() + 1) {
                    widthcolumnsforitems += gridbox.gridAutoColumns;
                } else if ((j + 1) < gridbox.gridTemplateColumnsValues.size()) {
                    widthcolumnsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentWidth());
                } else {
                    widthcolumnsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentWidth());
                }
            } else {
                if (gridbox.isGridTemplateColumnsNone) widthcolumnsforitems = gridbox.getContentWidth();
                else {
                    if (gridbox.isGridTemplateColumnsAuto) {
                        widthcolumnsforitems = gridbox.getContentWidth();
                    } else {
                        if (gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentWidth()) == -1) {
                            if (gridItemRowColumnValue.columnEnd - gridItemRowColumnValue.columnStart > 1) {
                                if (gridbox.gridTemplateColumnsValues.get(j).getValue().toString().equals("min-content") ||
                                        gridbox.gridTemplateColumnsValues.get(j).getValue().toString().equals("max-content"))
                                    widthcolumnsforitems = 0;
                                else widthcolumnsforitems = 0;
                            } else {
                                if (gridbox.gridTemplateColumnsValues.get(gridItemRowColumnValue.columnStart - 1).getValue().toString().equals("min-content")) {
                                    widthcolumnsforitems = getMinimalWidth();
                                } else if (gridbox.gridTemplateColumnsValues.get(gridItemRowColumnValue.columnStart - 1).getValue().toString().equals("max-content")) {
                                    widthcolumnsforitems = getMaximalWidth();
                                } else if (gridbox.gridTemplateColumnsValues.get(gridItemRowColumnValue.columnStart - 1).getValue().toString().equals("auto")) {
                                    widthcolumnsforitems = 0;
                                } else {
                                    widthcolumnsforitems = gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentWidth());
                                }
                            }
                        } else {
                            widthcolumnsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentWidth());
                        }
                    }
                }
            }
        }
        countGridColumnGapsInItem = gridItemRowColumnValue.columnEnd - gridItemRowColumnValue.columnStart - 1;
        widthcolumnsforitems += countGridColumnGapsInItem * gridbox.gapColumn;
    }

    /**
     * Sets relevant height of grid items, inc. grid-gaps that intersects
     *
     * @param gridbox parrent box of grid item
     */
    public void setHeightOfItem(GridWrapperBlockBox gridbox) {
        CSSDecoder dec = new CSSDecoder(ctx);
        int countGridRowGapsInItem;
        for (int j = gridItemRowColumnValue.rowStart - 1; j < gridItemRowColumnValue.rowEnd - 1; j++) {
            if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues == null) {
                if (gridbox.gridTemplateRowsValues == null) {
                    if (gridItemRowColumnValue.rowStart > 1) {
                        if (gridbox.gridAutoRows == 0) {
                            widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                        } else
                            widthrowsforitems += gridbox.gridAutoRows;
                    } else {
                        if (gridItemRowColumnValue.rowStart == 1) {
                            if (gridbox.gridAutoRows == 0) {
                                widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                            } else
                                widthrowsforitems += gridbox.gridAutoRows;
                        } else {
                            widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                        }
                    }
                } else widthrowsforitems += gridbox.gridAutoRows;
            } else if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues != null) {
                if ((j + 1) >= gridbox.gridTemplateRowsValues.size() + 1) {
                    if (gridbox.isMinContentAutoRow || gridbox.isMaxContentAutoRow) {
                        widthrowsforitems += getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                    } else widthrowsforitems += gridbox.gridAutoRows;
                } else if ((j + 1) < gridbox.gridTemplateRowsValues.size()) {
                    if (gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight()) == -1) {
                        widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                    } else {
                        widthrowsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight());
                    }
                } else {
                    if (gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight()) == -1) {
                        widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                    } else {
                        widthrowsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight());
                    }
                }
            } else {
                if (!gridbox.isGridTemplateRows) {
                    if (gridbox.isGridTemplateRowsAuto) {
                        widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                    } else {
                        if (containsBlocks() || containsFlow()) {
                            widthrowsforitems = getSubBox(getSubBoxNumber() - 1).getContentY() + getSubBox(getSubBoxNumber() - 1).getContentHeight();
                            widthrowsforitems += margin.top + margin.bottom + border.top + border.bottom + padding.top + padding.bottom;
                        }
                    }
                } else {
                    if (gridItemRowColumnValue.rowStart <= gridbox.gridTemplateRowsValues.size()) {
                        if (gridbox.gridTemplateRowsValues.get(gridItemRowColumnValue.rowStart - 1).getValue().toString().equals("auto") ||
                                gridbox.gridTemplateRowsValues.get(gridItemRowColumnValue.rowStart - 1).getValue().toString().equals("min-content") ||
                                gridbox.gridTemplateRowsValues.get(gridItemRowColumnValue.rowStart - 1).getValue().toString().equals("max-content")) {
                            widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                        } else {
                            if (gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight()) == -1) {
                                widthrowsforitems += gridbox.gapRow;
                            } else
                                widthrowsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight());
                        }
                    } else {
                        if (!gridbox.isGridAutoRow) {
                            widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                        } else {
                            if (gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight()) == -1) {
                                widthrowsforitems = getContentHeight() + padding.top + padding.bottom + border.top + border.bottom + margin.top + margin.bottom;
                            } else {
                                widthrowsforitems += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight());
                            }
                        }
                    }
                }
            }
        }
        countGridRowGapsInItem = gridItemRowColumnValue.rowEnd - gridItemRowColumnValue.rowStart - 1;
        widthrowsforitems += countGridRowGapsInItem * gridbox.gapRow;
    }

    /**
     * Sets relevant distance from left edge, inc. grid-gaps that intersects
     *
     * @param gridbox parrent box of grid item
     */
    public void setDistanceInHorizontalDirection(GridWrapperBlockBox gridbox) {
        CSSDecoder dec = new CSSDecoder(ctx);
        for (int j = 0; j < gridItemRowColumnValue.columnStart - 1; j++) {
            if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues == null) {
                columndistancefromzero += gridbox.gridAutoColumns;
            } else if (gridbox.isGridAutoColumn && gridbox.gridTemplateColumnsValues != null) {
                if (gridItemRowColumnValue.columnStart >= gridbox.gridTemplateColumnsValues.size() + 1) {
                    columndistancefromzero = gridbox.sumOfLengthForGridTemplateColumnRow(dec, gridbox.gridTemplateColumnsValues, gridbox.oneFrUnitColumn, gridbox.getContentWidth());
                    w++;
                } else {
                    columndistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentWidth());
                }
            } else {
                if (!gridbox.containsOnlyUnit(gridbox.gridTemplateColumnsValues)) {
                    if (gridItemRowColumnValue.columnStart == 1) {
                        columndistancefromzero = 0;
                    } else {
                        columndistancefromzero += gridbox.arrayofcolumns.get(j);
                    }
                } else {
                    columndistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateColumnsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentWidth());
                }
            }
        }
        columndistancefromzero += (gridItemRowColumnValue.columnStart - 1) * gridbox.gapColumn;
        if (w != 0) {
            columndistancefromzero += (w - gridbox.gridTemplateColumnsValues.size()) * gridbox.gridAutoColumns;
        }
    }

    /**
     * Sets relevant distance from top edge, inc. grid-gaps that intersects
     *
     * @param gridbox parrent box of grid item
     */
    public void setDistanceInVerticalDirection(GridWrapperBlockBox gridbox) {
        CSSDecoder dec = new CSSDecoder(ctx);
        for (int j = 0; j < gridItemRowColumnValue.rowStart - 1; j++) {
            if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues == null) {
                rowdistancefromzero += gridbox.arrayofrows.get(j);
            } else if (gridbox.isGridAutoRow && gridbox.gridTemplateRowsValues != null) {
                if (gridItemRowColumnValue.rowStart >= gridbox.gridTemplateRowsValues.size() + 1) {
                    if (gridbox.sumOfLengthForGridTemplateColumnRow(dec, gridbox.gridTemplateRowsValues, gridbox.oneFrUnitRow, gridbox.getContentHeight()) == -1) {
                        rowdistancefromzero += gridbox.arrayofrows.get(j);
                    } else {
//                        rowdistancefromzero = gridbox.sumOfLengthForGridTemplateColumnRow(dec, gridbox.gridTemplateRowsValues, gridbox.oneFrUnitRow, gridbox.getContentHeight());
                        rowdistancefromzero += gridbox.arrayofrows.get(j);
//                        h++;
                    }
                } else {
                    if (gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight()) == -1) {
                        rowdistancefromzero += gridbox.arrayofrows.get(j);
                    } else {
                        rowdistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitColumn, gridbox.getContentHeight());
                    }
                }
            } else {
                if (gridbox.isGridTemplateRowsAuto) {
                    rowdistancefromzero += gridbox.arrayofrows.get(j);
                } else {
                    if (!gridbox.containsOnlyUnit(gridbox.gridTemplateRowsValues)) {
                        if (gridItemRowColumnValue.rowStart == 1) {
                            rowdistancefromzero = 0;
                        } else {
                            rowdistancefromzero += gridbox.arrayofrows.get(j);
                        }
                    } else {
                        if (gridItemRowColumnValue.rowStart > gridbox.gridTemplateRowsValues.size()) {
                            rowdistancefromzero += gridbox.arrayofrows.get(j);
                        } else
                            rowdistancefromzero += gridbox.findSizeOfGridItem(dec, gridbox.gridTemplateRowsValues, j, gridbox.oneFrUnitRow, gridbox.getContentHeight());
                    }
                }
            }
        }
        rowdistancefromzero += (gridItemRowColumnValue.rowStart - 1) * gridbox.gapRow;
//        if (h != 0) {
//            rowdistancefromzero += (h - gridbox.gridTemplateRowsValues.size()) * gridbox.gridAutoRows;
//        }
    }
}
