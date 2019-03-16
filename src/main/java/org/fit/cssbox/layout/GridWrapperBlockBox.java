package org.fit.cssbox.layout;

import org.w3c.dom.Element;

import java.awt.*;

public class GridWrapperBlockBox extends BlockBox {


    public GridWrapperBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        typeoflayout = new GridLayoutManager(this);
        isblock = true;
    }

    public GridWrapperBlockBox(InlineBox src) {
        super(src);
        typeoflayout = new GridLayoutManager(this);
        isblock = true;
    }
}
