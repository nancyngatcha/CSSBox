package org.fit.cssbox.layout;

import org.w3c.dom.Element;

import java.awt.*;

public class FlexContainerBlockBox extends BlockBox {

    public FlexContainerBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
    }

    public FlexContainerBlockBox(InlineBox src) {
        super(src);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
    }
}
