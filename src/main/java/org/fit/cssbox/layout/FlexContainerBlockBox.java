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

    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_FLEX_START = CSSProperty.JustifyContent.FLEX_START;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_FLEX_END = CSSProperty.JustifyContent.FLEX_END;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_CENTER = CSSProperty.JustifyContent.CENTER;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_SPACE_BETWEEN = CSSProperty.JustifyContent.SPACE_BETWEEN;
    public static final CSSProperty.JustifyContent JUSTIFY_CONTENT_SPACE_AROUND = CSSProperty.JustifyContent.SPACE_AROUND;

    public static final CSSProperty.AlignContent ALIGN_CONTENT_FLEX_START = CSSProperty.AlignContent.FLEX_START;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_FLEX_END = CSSProperty.AlignContent.FLEX_END;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_CENTER = CSSProperty.AlignContent.CENTER;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_SPACE_BETWEEN = CSSProperty.AlignContent.SPACE_BETWEEN;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_SPACE_AROUND = CSSProperty.AlignContent.SPACE_AROUND;
    public static final CSSProperty.AlignContent ALIGN_CONTENT_STRETCH = CSSProperty.AlignContent.STRETCH;

    public static final CSSProperty.AlignItems ALIGN_ITEMS_FLEX_START = CSSProperty.AlignItems.FLEX_START;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_FLEX_END = CSSProperty.AlignItems.FLEX_END;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_CENTER = CSSProperty.AlignItems.CENTER;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_BASELINE = CSSProperty.AlignItems.BASELINE;
    public static final CSSProperty.AlignItems ALIGN_ITEMS_STRETCH = CSSProperty.AlignItems.STRETCH;


    protected CSSProperty.FlexDirection flexDirection;
    protected CSSProperty.FlexWrap flexWrap;
    protected CSSProperty.JustifyContent justifyContent;
    protected CSSProperty.AlignContent alignContent;
    protected CSSProperty.AlignItems alignItems;

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
        if (isDirectionRow()) {
            mainSpace = getContentWidth();
        } else {
            mainSpace = getContentHeight();
        }
    }

    public void setCrossSpace() {
        if (isDirectionRow()) {
            crossSpace = getContentHeight();
        } else {
            crossSpace = getContentWidth();
        }

    }

    @Override
    public void setStyle(NodeData s) {
        super.setStyle(s);
        loadFlexContainerStyles();
    }

    public void loadFlexContainerStyles() {
        flexDirection = style.getProperty("flex-direction");
        if (flexDirection == null) flexDirection = FLEX_DIRECTION_ROW;

        flexWrap = style.getProperty("flex-wrap");
        if (flexWrap == null) flexWrap = FLEX_WRAP_NOWRAP;

        justifyContent = style.getProperty("justify-content");
        if (justifyContent == null) justifyContent = JUSTIFY_CONTENT_FLEX_START;

        alignContent = style.getProperty("align-content");
        if (alignContent == null) alignContent = ALIGN_CONTENT_STRETCH;

        alignItems = style.getProperty("align-items");
        if (alignItems == null) alignItems = ALIGN_ITEMS_STRETCH;

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

    protected boolean isCrossSpaceSetByContent() {

        if(isDirectionRow()) {
            if (style.getProperty("height") == null || style.getProperty("min-height") == null ||
                    style.getProperty("height") == CSSProperty.Height.AUTO ||
                    style.getProperty("height") == CSSProperty.Height.percentage  || style.getProperty("min-height") == CSSProperty.MinHeight.percentage) {
                return true;
            } else {
                return false;
            }
        } else {
            if (style.getProperty("width") == null || style.getProperty("min-width") == null ||
                    style.getProperty("width") == CSSProperty.Width.AUTO ||
                    style.getProperty("width") == CSSProperty.Width.percentage  || style.getProperty("min-width") == CSSProperty.MinWidth.percentage) {
                return true;
            } else {
                return false;
            }

        }
    }

    protected void layoutItems(ArrayList<FlexItemBlockBox> list) {
        ArrayList <FlexLine> lines = new ArrayList<>();
        FlexLine line = firstLine;
        if(line == null)
            line = new FlexLine(this);
        lines.add(line);


        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox Item = list.get(i);
            Item.flexBasisValue = Item.setFlexBasisValue(this);
            Item.hypoteticalMainSize = Item.boundFlexBasisByMinAndMaxValues(Item.flexBasisValue);

            Item.fixMargins(); //when width was set (and flex-basis not) - margin right was set to container end

            if (isDirectionRow())
                Item.setAvailableWidth(Item.hypoteticalMainSize + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);
            else
                Item.setAvailableWidth(content.width);
        }

        if(flexWrap == FLEX_WRAP_WRAP) {
            handleFlexGrow(list, mainSpace);
            handleFlexShrink(list, mainSpace);

        } else if(flexWrap == FLEX_WRAP_NOWRAP){
            handleFlexGrowNowrap(list, mainSpace);
            handleFlexShrinkNoWrap(list, mainSpace);

        }
        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox Item = list.get(i);

            if (isDirectionRow())
                Item.setAvailableWidth(Item.hypoteticalMainSize + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);
            else
                Item.setAvailableWidth(content.width);


        }


        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox Item = list.get(i);

            if (!Item.contblock)  //block elements containing inline elements only
                Item.layoutInline();
            else //block elements containing block elements
                Item.layoutBlocks();

            Item.fixMargins();


            if (isDirectionRow()) {
                //ROW
                Item.bounds.width = Item.hypoteticalMainSize + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;
                Item.content.width = Item.hypoteticalMainSize;


                //todo, pri row - pokud je kontejner height v % nebo nezadano a item ma height v %, tak to hodi nulu(i pro min-height)

                    Item.bounds.height = Item.totalHeight();
                    Item.content.height = Item.totalHeight() - Item.padding.top - Item.padding.bottom - Item.border.top - Item.border.bottom - Item.margin.top - Item.margin.bottom;


            } else {
                //COLUMN
                Item.bounds.height = Item.hypoteticalMainSize + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
                Item.content.height = Item.hypoteticalMainSize;

                //flex basis ani height nenastaveno
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
        }


        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox Item = list.get(i);
            if(isDirectionRow()) {
                if(Item.crossSizeSetByPercentage){
                    //je zadano height: [percentage] nebo height: auto
                        for (int j = 0; j < Item.getSubBoxNumber(); j++){
                            if(j == Item.getSubBoxNumber() -1)
                                Item.content.height += Item.getSubBox(j).getContentHeight() + Item.getSubBox(j).getContentY();

                        }
                        Item.bounds.height = Item.content.height + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;

                }
            }
        }



        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox Item = list.get(i);
            System.out.println("\nITEM: " + Item + "\nšířka: " + Item.bounds.width + "\nvýška: " + Item.bounds.height + "\ncontent šířka: " + Item.content.width +"\ncontent výška: " + Item.content.height);
            boolean result = line.registerItemAndSetItsPosition(Item);

            if(!result) {
                //make new line

                FlexLine newLine = new FlexLine(this);
                lines.add(newLine);
                newLine.registerItemAndSetItsPosition(Item);
                line = newLine;
            }

            //zvetseni vysky kontejneru, pokud mozno
        }

        for(int i = 0; i < lines.size(); i++){
                FlexLine flexLine = lines.get(i);
            System.out.println("VELIKOST LINE: " + flexLine.getHeight());
                int sumOfLineAboveHeights = 0;

                for (int y = 0; y < i; y++) {
                    sumOfLineAboveHeights += lines.get(y).getHeight();
                }
                flexLine.setY(sumOfLineAboveHeights);
                flexLine.applyAlignContent(flexLine, lines, i);
            //not first item in row, does it fit into line?


            for(int j = 0; j < flexLine.itemsInLine.size(); j++) {
                int sumOfItemsWidths = 0;

                for(int k = 0; k < j; k++)
                    sumOfItemsWidths += flexLine.itemsInLine.get(k).getWidth();

                if(j == 0){

                    int x;
                    if(flexDirection == FlexContainerBlockBox.FLEX_DIRECTION_ROW_REVERSE){
                        x = mainSpace - flexLine.itemsInLine.get(j).bounds.width;
                    } else {
                        x = 0;
                    }

                    flexLine.setYPositionToItem(flexLine.itemsInLine.get(j), x );
                    continue;
                }
            flexLine.setPositionAndAdaptHeight(flexLine.itemsInLine.get(j), sumOfItemsWidths);
            }
        }

        alignByJustifyContent(lines);
        for(int i = 0; i < lines.size(); i++)
            for(int j = 0; j < lines.get(i).itemsInLine.size(); j++)
                fixContainerHeight(lines.get(i).itemsInLine.get(j));
    }

    protected void fixContainerHeight(FlexItemBlockBox Item){
        if (!hasFixedHeight()) {
            //height is not fixed
            if (isDirectionRow()) {
                //row
                if(max_size.height == -1){
                    //container has no height limit
                    if (Item.bounds.y + Item.totalHeight() > crossSpace) {
                        //Item is higher than container so far
                        crossSpace = Item.bounds.y + Item.totalHeight();
                        setContentHeight(crossSpace);
                    }


                } else {
                    //container has max height limit
                    if(Item.bounds.y + Item.totalHeight() < max_size.height){
                        //it fits to max
                        if (Item.bounds.y + Item.totalHeight() > crossSpace) {
                            crossSpace = Item.bounds.y + Item.totalHeight();
                            setContentHeight(crossSpace);
                        }
                    } else {
                        //it doesn't fit to max, set max
                        setContentHeight(max_size.height);
                    }
                }

            } else {
                mainSpace += Item.bounds.height;
                setContentHeight(mainSpace);

            }
        }
    }

    private void increaseItemsByFlexGrow(ArrayList<FlexItemBlockBox> list, int containerContWidth, int widthOfItemsInRow, int firstItem, int lastItem){
        int countOfFlexGrowValue = 0;

        if(firstItem == lastItem){
            FlexItemBlockBox onlyItemInRow = list.get(firstItem);
            if(onlyItemInRow.availwidth <= containerContWidth)
                onlyItemInRow.hypoteticalMainSize = containerContWidth - onlyItemInRow.margin.left - onlyItemInRow.margin.right - onlyItemInRow.padding.left - onlyItemInRow.padding.right - onlyItemInRow.border.left - onlyItemInRow.border.right;

            return;
        }

        for (int j = firstItem; j <= lastItem; j++){
               FlexItemBlockBox itemInRow = list.get(j);
               countOfFlexGrowValue += itemInRow.flexGrowValue;
        }

        if(countOfFlexGrowValue == 0)
            return;

        int ratio = (containerContWidth - widthOfItemsInRow) / countOfFlexGrowValue;
        int rest = 0;


        for (int j = firstItem; j <= lastItem; j++){
            FlexItemBlockBox itemInRow = list.get(j);
            int result = (int) (availwidth + ratio * itemInRow.flexGrowValue);
            if(itemInRow.hypoteticalMainSize + result > itemInRow.max_size.width && itemInRow.max_size.width != -1) {
                itemInRow.hypoteticalMainSize = itemInRow.max_size.width;
                rest += result - itemInRow.max_size.width;
            } else
                itemInRow.hypoteticalMainSize += result;
        }

        //todo: distribute rest taken of max width size
        System.out.println("ZBYTEK: " + rest);


    }

    private void handleFlexGrow(ArrayList<FlexItemBlockBox> list, int containerContWidth){

        int flexGrowCount = 0;
        for (int i =0; i < list.size(); i++){
            flexGrowCount += list.get(i).flexGrowValue;
        }
        if(flexGrowCount == 0) return; //every item has flex-grow = 0

        int widthOfItemsInRow = 0;
        boolean isFirstItemInRow = true;
        int indexOfFirstItem = 0;
        int i;
        for (i = 0; i < list.size(); i++) {
            FlexItemBlockBox item = list.get(i);
            if(isFirstItemInRow && indexOfFirstItem != 0){
                isFirstItemInRow = false;
                indexOfFirstItem = i -1;
            }
            int hypotWidth = item.hypoteticalMainSize + item.padding.left + item.padding.right + item.border.left + item.border.right + item.margin.left + item.margin.right;
            if(widthOfItemsInRow  + hypotWidth > containerContWidth){
                increaseItemsByFlexGrow(list, containerContWidth, widthOfItemsInRow, indexOfFirstItem, i-1);
                isFirstItemInRow = true;
                indexOfFirstItem = i;
                widthOfItemsInRow = hypotWidth;
            } else {
                widthOfItemsInRow += hypotWidth;
            }

            if(i == list.size() -1){
                increaseItemsByFlexGrow(list, containerContWidth, widthOfItemsInRow, indexOfFirstItem, i);

            }
        }

    }

    private void handleFlexShrink(ArrayList<FlexItemBlockBox> list, int containerContWidth) {
        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox item = list.get(i);
            int hypotWidth = item.hypoteticalMainSize + item.padding.left + item.padding.right + item.border.left + item.border.right + item.margin.left + item.margin.right;
            if(hypotWidth > containerContWidth && item.flexShrinkValue > 0)
                item.hypoteticalMainSize = containerContWidth - item.margin.left - item.margin.right - item.padding.left - item.padding.right - item.border.left - item.border.right;
        }
    }

    private void handleFlexGrowNowrap(ArrayList<FlexItemBlockBox> list, int containerContWidth){
        int widthOfItems = 0;
        int rest = 0;

        int countOfFlexGrowValue = 0;

        for (int j = 0; j < list.size(); j++){
            FlexItemBlockBox item = list.get(j);
            countOfFlexGrowValue += item.flexGrowValue;
        }

        if(countOfFlexGrowValue == 0)
            return;

        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox item = list.get(i);
            widthOfItems += item.hypoteticalMainSize + item.padding.left + item.padding.right + item.border.left + item.border.right + item.margin.left + item.margin.right;
        }

        int ratio = (containerContWidth - widthOfItems) / countOfFlexGrowValue;

        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox item = list.get(i);
            if(containerContWidth > widthOfItems) {
                int result = (int) (availwidth + ratio * item.flexGrowValue);
                if(item.hypoteticalMainSize + result > item.max_size.width && item.max_size.width != -1) {
                    item.hypoteticalMainSize = item.max_size.width;
                    rest += result - item.max_size.width;
                } else
                    item.hypoteticalMainSize += result;
            }
        }
    }

    private void handleFlexShrinkNoWrap(ArrayList<FlexItemBlockBox> list, int containerContWidth) {
        //todo udelat flex shrink pro no wrap
        int widthOfItems = 0;
        int totalShrinkValue = 0;
        int sizeToShrink = 0;
        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox item = list.get(i);
            widthOfItems += item.hypoteticalMainSize + item.padding.left + item.padding.right + item.border.left + item.border.right + item.margin.left + item.margin.right;
        }

        if(totalShrinkValue == 0)
            return;

        if (widthOfItems > containerContWidth)
            sizeToShrink = widthOfItems - containerContWidth;
        else
            return;

        int ratio = (sizeToShrink) / totalShrinkValue;

        for (int i = 0; i < list.size(); i++) {
            FlexItemBlockBox item = list.get(i);
            int result = (int) (availwidth - ratio * item.flexShrinkValue);

            item.hypoteticalMainSize += result;
            if(item.hypoteticalMainSize < item.min_size.width && item.min_size.width != -1)
                item.hypoteticalMainSize = item.min_size.width;
            if(item.hypoteticalMainSize < item.getMinimalContentWidth())
                item.hypoteticalMainSize = item.getMinimalContentWidth();
        }
    }

    private void alignByJustifyContent(ArrayList<FlexLine> lines){
         for (int i = 0; i < lines.size(); i++) {
             FlexLine line = lines.get(i);
             int widthOfItems = 0;
             int totalWidthOfItems = 0;
             for(int j = 0; j < line.itemsInLine.size(); j++)
                 totalWidthOfItems += line.itemsInLine.get(j).getWidth();

             if((isDirectionReversed() || justifyContent == JUSTIFY_CONTENT_FLEX_END) && justifyContent != JUSTIFY_CONTENT_SPACE_BETWEEN) {
                 for (int j = line.itemsInLine.size() - 1; j >= 0; j--) {
                     FlexItemBlockBox item = line.itemsInLine.get(j);
                     applyJustifyContent(line, item, widthOfItems, j, totalWidthOfItems);
                     widthOfItems += item.getWidth();

                 }
             } else {
                 for (int j = 0; j < line.itemsInLine.size(); j++) {
                     FlexItemBlockBox item = line.itemsInLine.get(j);
                     applyJustifyContent(line, item, widthOfItems, j, totalWidthOfItems);
                     widthOfItems += item.getWidth();
                 }
             }
         }
    }

    private void applyJustifyContent(FlexLine line , FlexItemBlockBox item, int widthOfItems, int j, int totalWidthOfItems){

        if  (justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_FLEX_END) {
            if (isDirectionReversed())
                item.setPosition(widthOfItems, item.bounds.y);
            else
                item.setPosition(mainSpace - widthOfItems - item.bounds.width , item.bounds.y);

        } else if (justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_CENTER) {
            int halfOfRemainSpace = (mainSpace) / 2;
            item.setPosition(halfOfRemainSpace - totalWidthOfItems/2 + widthOfItems, item.bounds.y);

        } else if (justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_SPACE_BETWEEN) {
            if(line.itemsInLine.size()-1 == 0 || totalWidthOfItems > mainSpace){
                //only one item or items are outside of container - it works like start
                if (isDirectionReversed())
                    item.setPosition(mainSpace - widthOfItems - item.bounds.width , item.bounds.y);
                else
                    item.setPosition(widthOfItems, item.bounds.y);

            } else {
                int spaceBetween = 0;

                if(isDirectionReversed()) {
                    if(j == 0) {
                        item.setPosition(mainSpace-item.bounds.width, item.bounds.y);
                    } else if (j == line.itemsInLine.size()-1) {
                        item.setPosition(0, item.bounds.y);
                    } else {//it is not first item in row
                        spaceBetween = line.getRemainingWidthSpace() / (line.itemsInLine.size() - 1);
                        item.setPosition(spaceBetween * (line.itemsInLine.size()-1 -j) + totalWidthOfItems - widthOfItems - item.bounds.width, item.bounds.y);
                    }
                } else {
                    if (j == 0) {
                        item.setPosition(0, item.bounds.y);
                    } else {//it is not first item in row
                        spaceBetween = line.getRemainingWidthSpace() / (line.itemsInLine.size() - 1);
                        item.setPosition(spaceBetween * j + widthOfItems, item.bounds.y);
                    }
                }
            }

        } else if (justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_SPACE_AROUND) {
            if(line.itemsInLine.size()-1 == 0 || totalWidthOfItems > mainSpace){
                //only one item - it works like center
                int halfOfRemainSpace = (bounds.x + mainSpace) / 2;
                item.setPosition(halfOfRemainSpace - totalWidthOfItems/2 + widthOfItems, item.bounds.y);
            } else {
                //more than one item
                int spaceAround = line.getRemainingWidthSpace() / (line.itemsInLine.size());
                spaceAround /= 2;
                if(isDirectionReversed()){
                        item.setPosition(spaceAround + (line.itemsInLine.size()-1 -j) * 2 * spaceAround + widthOfItems, item.bounds.y);
                } else {
                        item.setPosition(j * 2 * spaceAround + widthOfItems + spaceAround, item.bounds.y);
                }
            }

        }
    }

}
