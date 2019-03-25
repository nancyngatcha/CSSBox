package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import org.w3c.dom.Element;
import java.awt.*;

public class GridItem extends BlockBox {

    public static final CSSProperty.GridStartEnd GRID_START_END_AUTO = CSSProperty.GridStartEnd.AUTO;
    public static final CSSProperty.GridStartEnd GRID_START_END_NONE = CSSProperty.GridStartEnd.NONE;
    public static final CSSProperty.GridStartEnd GRID_START_END_SPAN = CSSProperty.GridStartEnd.SPAN;
    public static final CSSProperty.GridStartEnd GRID_START_END_IDENTIFIKATOR = CSSProperty.GridStartEnd.identificator;
    public static final CSSProperty.GridStartEnd GRID_START_END_NUMBER = CSSProperty.GridStartEnd.number;
    public static final CSSProperty.GridStartEnd GRID_START_END_COMPONENT = CSSProperty.GridStartEnd.component_values;


    public CSSProperty.GridStartEnd gridStartEnd;
    GridItemRowColumn gridItemRowColumnValue;

    public GridItem(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        isblock = true;
    }

    public GridItem(InlineBox src) {
        super(src);
        isblock = true;
    }


    @Override
    public void setStyle(NodeData s) {
        super.setStyle(s);
        loadGridStyles();
    }

    public void loadGridStyles() {
        CSSDecoder dec = new CSSDecoder(ctx);
        gridItemRowColumnValue = new GridItemRowColumn();
        gridStartEnd = style.getProperty("grid-row-start");
        if (gridStartEnd == null) gridStartEnd = CSSProperty.GridStartEnd.AUTO;
        else if (gridStartEnd == CSSProperty.GridStartEnd.number){
            gridItemRowColumnValue.rowStart = dec.getLength(getLengthValue("grid-row-start"), false, 0, 0, 0);
        }

        gridStartEnd = style.getProperty("grid-row-end");
        //teoreticky pokud neni nastaven tak se ma dat defaultne auto, ale kdyz je start na 1 tak end bude na 2
        if (gridStartEnd == null && gridItemRowColumnValue.rowStart != 0) {
            //gridStartEnd = CSSProperty.GridStartEnd.AUTO;
            gridItemRowColumnValue.rowEnd = gridItemRowColumnValue.rowStart + 1;
        } else if (gridStartEnd == CSSProperty.GridStartEnd.number){
            gridItemRowColumnValue.rowEnd = dec.getLength(getLengthValue("grid-row-end"), false, 0, 0, 0);
        }


        gridStartEnd = style.getProperty("grid-column-start");
        if (gridStartEnd == null) gridStartEnd = CSSProperty.GridStartEnd.AUTO;
        else if (gridStartEnd == CSSProperty.GridStartEnd.number){
            gridItemRowColumnValue.columnStart = dec.getLength(getLengthValue("grid-column-start"), false, 0, 0, 0);
        }

        gridStartEnd = style.getProperty("grid-column-end");
        //teoreticky pokud neni nastaven tak se ma dat defaultne auto, ale kdyz je start na 1 tak end bude na 2
        if (gridStartEnd == null && gridItemRowColumnValue.columnStart != 0) {
            gridItemRowColumnValue.columnEnd = gridItemRowColumnValue.columnStart + 1;
            //gridStartEnd = CSSProperty.GridStartEnd.AUTO;
        } else if (gridStartEnd == CSSProperty.GridStartEnd.number){
            gridItemRowColumnValue.columnEnd = dec.getLength(getLengthValue("grid-column-end"), false, 0, 0, 0);
        }

        System.out.println(gridItemRowColumnValue.toString());
    }
}
