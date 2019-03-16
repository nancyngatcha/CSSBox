package org.fit.cssbox.layout;

import org.w3c.dom.Element;

import java.awt.*;

public class GridItem extends BlockBox {

    public GridItem(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        isblock = true;
    }

    public GridItem(InlineBox src) {
        super(src);
        isblock = true;
    }
}
