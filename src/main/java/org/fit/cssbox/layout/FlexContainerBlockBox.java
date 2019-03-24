package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import org.w3c.dom.Element;

import java.awt.*;

public class FlexContainerBlockBox extends BlockBox {

    public static final CSSProperty.FlexDirection FLEX_DIRECTION_ROW = CSSProperty.FlexDirection.ROW;
    public static final CSSProperty.FlexDirection FLEX_DIRECTION_ROW_REVERSE = CSSProperty.FlexDirection.ROW_REVERSE;
    public static final CSSProperty.FlexDirection FLEX_DIRECTION_COLUMN = CSSProperty.FlexDirection.COLUMN;
    public static final CSSProperty.FlexDirection FLEX_DIRECTION_COLUMN_REVERSE = CSSProperty.FlexDirection.COLUMN_REVERSE;

    public static final CSSProperty.FlexWrap FLEX_WRAP_NOWRAP = CSSProperty.FlexWrap.NOWRAP;
    public static final CSSProperty.FlexWrap FLEX_WRAP_WRAP = CSSProperty.FlexWrap.WRAP;
    public static final CSSProperty.FlexWrap FLEX_WRAP_REVERSE = CSSProperty.FlexWrap.WRAP_REVERSE;

    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_FLEX_START = CSSProperty.JustifyContent.FlexStart;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_FLEX_END = CSSProperty.JustifyContent.FlexEnd;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_CENTER = CSSProperty.JustifyContent.Center;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_SPACE_BETWEEN = CSSProperty.JustifyContent.SpaceBetween;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_SPACE_AROUND = CSSProperty.JustifyContent.SpaceAround;

    public static final CSSProperty.AlignContent ALIGN_CONTENT_FLEX_START = CSSProperty.AlignContent.FlexStart;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_FLEX_END = CSSProperty.AlignContent.FlexEnd;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_CENTER = CSSProperty.AlignContent.Center;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_SPACE_BETWEEN = CSSProperty.AlignContent.SpaceBetween;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_SPACE_AROUND = CSSProperty.AlignContent.SpaceAround;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_STRETCH = CSSProperty.AlignContent.Stretch;

    public static final CSSProperty.AlignItems ALIGN_ITEMS_FLEX_START = CSSProperty.AlignItems.FlexStart;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_FLEX_END = CSSProperty.AlignItems.FlexEnd;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_CENTER = CSSProperty.AlignItems.Center;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_SPACE_BASELINE = CSSProperty.AlignItems.Baseline;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_STRETCH = CSSProperty.AlignItems.Stretch;


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

    public int getMainSpace() {
        return mainSpace;
    }

    public void setMainSpace() {
        if(isDirectionRow) {
            mainSpace = getContent().width;
        } else {
            mainSpace = getContent().height;
        }
    }

    public int getCrossSpace() {
        return crossSpace;
    }

    public void setCrossSpace() {
        if(isDirectionRow) {
            crossSpace = getContent().height;
        } else {
            crossSpace = getContent().width;
        }

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
