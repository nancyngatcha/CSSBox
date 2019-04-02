package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import org.w3c.dom.Element;

import java.awt.*;
import java.util.ArrayList;

public class FlexContainerBlockBox extends BlockBox {

    public static final CSSProperty.FlexDirection FLEX_DIRECTION_ROW = CSSProperty.FlexDirection.ROW;
    public static final CSSProperty.FlexDirection FLEX_DIRECTION_ROW_REVERSE = CSSProperty.FlexDirection.ROW_REVERSE;
    public static final CSSProperty.FlexDirection FLEX_DIRECTION_COLUMN = CSSProperty.FlexDirection.COLUMN;
    public static final CSSProperty.FlexDirection FLEX_DIRECTION_COLUMN_REVERSE = CSSProperty.FlexDirection.COLUMN_REVERSE;

    public static final CSSProperty.FlexWrap FLEX_WRAP_NOWRAP = CSSProperty.FlexWrap.NOWRAP;
    public static final CSSProperty.FlexWrap FLEX_WRAP_WRAP = CSSProperty.FlexWrap.WRAP;
    public static final CSSProperty.FlexWrap FLEX_WRAP_WRAP_REVERSE = CSSProperty.FlexWrap.WRAP_REVERSE;

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

    protected FlexLine firstLine;

    protected int totalHeightOfItems = 0;

    public FlexContainerBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
        firstLine = null;
    }

    public FlexContainerBlockBox(InlineBox src) {
        super(src);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
        firstLine = null;
    }

    public void setMainSpace() {
        if (isDirectionRow) {
            mainSpace = getContent().width;
            if (mainSpace > getMaximalContentWidth())
                mainSpace = getMaximalContentWidth();
        } else {
            mainSpace = getContent().height;
        }
    }

    public void setCrossSpace() {
        if (isDirectionRow) {
            crossSpace = getContent().height;
        } else {
            crossSpace = getContent().width;
        }

    }

    @Override
    public void setStyle(NodeData s) {
        super.setStyle(s);
        loadFlexContainerStyles();
    }

    public void loadFlexContainerStyles() {
        flexDirection = style.getProperty("flex-direction");
        if (flexDirection == null) flexDirection = CSSProperty.FlexDirection.ROW;
        isDirectionRow = isDirectionRow();
        isDirectionReversed = isDirectionReversed();

        flexWrap = style.getProperty("flex-wrap");
        if (flexWrap == null) flexWrap = CSSProperty.FlexWrap.NOWRAP;

        justifyContent = style.getProperty("justify-content");
        if (justifyContent == null) justifyContent = CSSProperty.JustifyContent.FlexStart;

        alignContent = style.getProperty("align-content");
        if (alignContent == null) alignContent = CSSProperty.AlignContent.Stretch;

        alignItems = style.getProperty("align-items");
        if (alignItems == null) alignItems = CSSProperty.AlignItems.Stretch;
    }

    public boolean isDirectionRow() {
        if (flexDirection == FLEX_DIRECTION_ROW || flexDirection == FLEX_DIRECTION_ROW_REVERSE)
            return true;
        else
            return false;
    }

    public boolean isDirectionReversed() {
        if (flexDirection == FLEX_DIRECTION_ROW_REVERSE || flexDirection == FLEX_DIRECTION_COLUMN_REVERSE)
            return true;
        else
            return false;
    }

    protected void layoutItems(ArrayList<FlexItemBlockBox> list) {
        ArrayList <FlexLine> lines = new ArrayList<>();
        FlexLine line = firstLine;
        if(line == null)
            line = new FlexLine(this, 0);
        lines.add(line);

        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox Item = list.get(i);
            System.out.println(Item);
            Item.flexBasisValue = Item.setFlexBasisValue(this);
            Item.hypoteticalMainSize = Item.boundFlexBasisByMinAndMaxValues(Item.flexBasisValue);

//            if (Item.hypoteticalMainSize < Item.getMinimalContentWidth()) {
//                Item.hypoteticalMainSize = Item.getMinimalContentWidth();
//                Item.bounds.width = Item.getMinimalContentWidth() + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;
//                Item.content.width = Item.getMinimalContentWidth();
//            }

            if (isDirectionRow())
                Item.setAvailableWidth(Item.hypoteticalMainSize + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);
            else
                Item.setAvailableWidth(content.width);

            if (!Item.contblock)  //block elements containing inline elements only
                Item.layoutInline();
            else //block elements containing block elements
                Item.layoutBlocks();


            Item.fixMargins(this);

            if (isDirectionRow()) {
                Item.bounds.width = Item.hypoteticalMainSize + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;
                Item.content.width = Item.hypoteticalMainSize;

                Item.bounds.height = Item.totalHeight();
                if (Item.flexBasisSetByCont) {
                    int compwidth = 0;
                    for (int x = 0; x < Item.getSubBoxNumber(); x++) {
                        Box subbox = Item.getSubBox(x);
                        compwidth += subbox.getContentWidth();
                    }
                    if (compwidth > Item.flexBasisValue)
                        compwidth = Item.flexBasisValue;
                    Item.content.width = compwidth;
                    Item.bounds.width = compwidth + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;

                }

            } else {
                Item.bounds.height = Item.hypoteticalMainSize + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
                Item.content.height = Item.hypoteticalMainSize;
                if (Item.flexBasisSetByCont) {
                    int compheight = 0;
                    for (int x = 0; x < Item.getSubBoxNumber(); x++) {
                        if (x + 1 == Item.getSubBoxNumber()) {
                            Box subbox = Item.getSubBox(x);
                            compheight = subbox.getContentY() + subbox.getContentHeight();
                        }
                    }

                    if (compheight > Item.hypoteticalMainSize)
                        compheight = Item.hypoteticalMainSize;

                    Item.content.height = compheight;
                    Item.bounds.height = compheight + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
                }


                if(Item.content.width < Item.getMinimalContentWidth()) {
                    Item.bounds.width = Item.getMinimalContentWidth() + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;
                    Item.content.width = Item.getMinimalContentWidth();
                }



                Item.bounds.width = Item.totalWidth();
            }



            totalHeightOfItems += Item.bounds.height;
        }


        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox Item = list.get(i);
            boolean result = line.registerItemAndSetItsPosition(Item);
            if(!result) {
                int sumOfLineAboveHeights = 0;

                for (int y = 0; y < lines.size(); y++) {
                    sumOfLineAboveHeights += lines.get(y).getHeight();
                }
                FlexLine newLine = new FlexLine(this, sumOfLineAboveHeights);
                lines.add(newLine);
                newLine.registerItemAndSetItsPosition(Item);
                line = newLine;
            }

//            System.out.println("BOUNDS\nvyska: " + Item.bounds.height);
//            System.out.println("sirka: " + Item.bounds.width);
//            System.out.println("CONTENT\nvyska: " + Item.content.height);
//            System.out.println("sirka: " + Item.content.width);

//          System.out.println("\nflexBasisValue(unbounded): " + subbox.flexBasisValue);
//          System.out.println("hypoteticalMainSize(bounded): " + subbox.hypoteticalMainSize);
//          System.out.println("flexGrowValue: " + subbox.flexGrowValue);
//          System.out.println("flexShrinkValue: " + subbox.flexShrinkValue);
//          System.out.println("ORDER: " + subbox.flexOrderValue);
//
//          System.out.println("Containing block: " + subbox.getContainingBlockBox());

            //zvetseni vysky kontejneru, pokud mozno
            CSSDecoder dec = new CSSDecoder(ctx);

            if(max_size.height == -1 || Item.bounds.y + Item.totalHeight() < max_size.height) {
                if (style.getProperty("height") == null
                        || style.getProperty("height") == CSSProperty.Height.AUTO) {
                    if (isDirectionRow()) {

                        if (Item.bounds.y + Item.totalHeight() > crossSpace) {
                            crossSpace = Item.bounds.y + Item.totalHeight();
                            setContentHeight(crossSpace);
                        }

                    } else {
                        mainSpace += Item.bounds.height;
                        setContentHeight(mainSpace);

                    }
                }
            }
        }

    }
}
