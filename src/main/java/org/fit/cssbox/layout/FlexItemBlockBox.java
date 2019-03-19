package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import org.w3c.dom.Element;

import java.awt.*;

public class FlexItemBlockBox extends BlockBox {

    /** flex basis specified by the style */

    protected CSSProperty.AlignSelf alignSelf;
    protected CSSProperty.FlexBasis flexBasis;
    protected CSSProperty.FlexGrow flexGrow;
    protected CSSProperty.FlexShrink flexShrink;
    protected CSSProperty.Order order;

    public FlexItemBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        isblock = true;

    }

    public FlexItemBlockBox(InlineBox src) {
        super(src);
        isblock = true;
    }

    @Override
    public void setStyle(NodeData s)
    {
        super.setStyle(s);
        loadFlexItemsStyles();

    }

    public void loadFlexItemsStyles(){

        alignSelf = style.getProperty("align-self");
        if (alignSelf == null) alignSelf = CSSProperty.AlignSelf.Auto;

        flexBasis = style.getProperty("flex-basis");
        if (flexBasis == null) flexBasis = CSSProperty.FlexBasis.AUTO;

        flexGrow = style.getProperty("flex-grow");
        if (flexGrow == null) flexGrow = CSSProperty.FlexGrow.number;

        flexShrink = style.getProperty("flex-shrink");
        if (flexShrink == null) flexShrink = CSSProperty.FlexShrink.number;

        order = style.getProperty("order");
        if (order == null) order = CSSProperty.Order.integer;


        CSSDecoder dec = new CSSDecoder(ctx);

    }

}
