package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import org.w3c.dom.Element;

import java.awt.*;

public class FlexContainerBlockBox extends BlockBox {

    protected CSSProperty.FlexDirection flexDirection;
    protected CSSProperty.FlexWrap flexWrap;
    protected CSSProperty.JustifyContent justifyContent;
    protected CSSProperty.AlignContent alignContent;
    protected CSSProperty.AlignItems alignItems;

    protected boolean isDirectionRow;
    protected boolean isDirectionReversed;
    protected int mainSpace;
    protected int crossSpace;


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

    @Override
    public void setStyle(NodeData s)
    {
        super.setStyle(s);
        loadFlexContainerStyles();

    }

    public void loadFlexContainerStyles(){
        flexDirection = style.getProperty("flex-direction");
        if (flexDirection == null) flexDirection = CSSProperty.FlexDirection.ROW;
        isDirectionRow = isDirectionRow();
        isDirectionReversed = isDirectionReversed();

        flexWrap = style.getProperty("flex-wrap");
        if (flexWrap == null) flexWrap = CSSProperty.FlexWrap.NOWRAP;

        justifyContent = style.getProperty("justify-content");
        if (justifyContent == null) justifyContent = CSSProperty.JustifyContent.FlexStart;

        alignContent = style.getProperty("align-content");
        if (alignContent == null) alignContent= CSSProperty.AlignContent.Stretch;

        alignItems = style.getProperty("align-items");
        if (alignItems == null) alignItems= CSSProperty.AlignItems.Stretch;
    }

    public boolean isDirectionRow(){
        if(flexDirection == CSSProperty.FlexDirection.ROW || flexDirection == CSSProperty.FlexDirection.ROW_REVERSE)
            return true;
        else
            return false;
    }

    public boolean isDirectionReversed(){
        if(flexDirection == CSSProperty.FlexDirection.COLUMN_REVERSE || flexDirection == CSSProperty.FlexDirection.ROW_REVERSE)
            return true;
        else
            return false;
    }

}
