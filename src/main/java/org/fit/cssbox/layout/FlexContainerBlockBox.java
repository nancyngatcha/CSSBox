package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import org.w3c.dom.Element;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

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

    protected int mainSize;
    protected int crossSize;

    protected boolean mainSizeSetByCont;

    protected FlexLine firstFlexLine;


    public FlexContainerBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
        firstFlexLine = null;
        mainSizeSetByCont = false;
    }

    public FlexContainerBlockBox(InlineBox src) {
        super(src);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
        firstFlexLine = null;
        mainSizeSetByCont = false;
    }

    public void setInceptiveMainSize() {
        if (isRowContainer()) {
            mainSize = getContentWidth();
        } else {
            if(style.getProperty("height") == null || (style.getProperty("height") == CSSProperty.Height.percentage && parent.style.getProperty("height") == null)) {
                mainSize = 0;
                mainSizeSetByCont = true;
            } else
                mainSize = getContentHeight();
        }
    }

    public void setInceptiveCrossSize() {
        if (isRowContainer()) {
            crossSize = getContentHeight();
        } else {
            crossSize = getContentWidth();
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

    public boolean isRowContainer() {
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

    protected void layoutItemsInRow(ArrayList<FlexItemBlockBox> listItems) {
        //sort listItems according to flex property order
        Collections.sort(listItems);

        for (int i = 0; i < listItems.size(); i++) {
            FlexItemBlockBox Item = listItems.get(i);
            Item.hypotheticalMainSize = Item.setHypotheticalMainSize(this);
            Item.hypotheticalMainSize = Item.boundFlexBasisByMinAndMax(Item.hypotheticalMainSize);
            Item.fixRightMargin();

        }

        ArrayList<FlexLine> lines = new ArrayList<>();
        createAndFillLines(lines, listItems);

        handleFlexGrow(lines);
        handleFlexShrink(lines);

        for (int i = 0; i < listItems.size(); i++) {
            FlexItemBlockBox Item = listItems.get(i);
            Item.setAvailableWidth(Item.hypotheticalMainSize + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);
            Item.setCrossSize(this);
        }


        for (int i = 0; i < listItems.size(); i++) {
            FlexItemBlockBox Item = listItems.get(i);

            if (!Item.contblock)  //block elements containing inline elements only
                Item.layoutInline();
            else //block elements containing block elements
                Item.layoutBlocks();

            Item.fixRightMargin();

            Item.bounds.width = Item.hypotheticalMainSize + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;
            Item.content.width = Item.hypotheticalMainSize;


            //todo, pri row - pokud je kontejner height v % nebo nezadano a item ma height v %, tak to hodi nulu(i pro min-height)

            Item.bounds.height = Item.totalHeight();
            Item.content.height = Item.totalHeight() - Item.padding.top - Item.padding.bottom - Item.border.top - Item.border.bottom - Item.margin.top - Item.margin.bottom;


        }


        for (int i = 0; i < listItems.size(); i++) {
            FlexItemBlockBox Item = listItems.get(i);
            if (Item.crossSizeSetByPercentage) {
                //je zadano height: [percentage] nebo height: auto
                for (int j = 0; j < Item.getSubBoxNumber(); j++) {
                    if (j == Item.getSubBoxNumber() - 1)
                        Item.content.height += Item.getSubBox(j).getContentHeight() + Item.getSubBox(j).getContentY();

                }
                Item.bounds.height = Item.content.height + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;

            }

        }

        //nastaveni velikosti line
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).setLineCrossSize();
        }


        for (int i = 0; i < lines.size(); i++) {
            FlexLine flexLine = lines.get(i);

            int sumOfPreviousLinesHeights = 0;
            for (int y = 0; y < i; y++) {
                sumOfPreviousLinesHeights += lines.get(y).getHeight();
            }
            flexLine.setY(sumOfPreviousLinesHeights);
            flexLine.applyAlignContent(lines, i);

            //set Y coord of items
            flexLine.applyAlignItemsAndSelf();

        }

        //set X coord of items
        alignByJustifyContent(lines);

        //set container height
        for (int i = 0; i < lines.size(); i++)
            for (int j = 0; j < lines.get(i).itemsInLine.size(); j++)
                fixContainerHeight(lines.get(i).itemsInLine.get(j));
    }


    private FlexLine createFirstLineToList(ArrayList<FlexLine> lines) {
        FlexLine line = firstFlexLine;
        if (line == null) {
            if(isRowContainer())
                line = new FlexLineRow(this);
            else
                line = new FlexLineColumn(this);
        }
        lines.add(line);
        return line;
    }

    private void createAndFillLines(ArrayList<FlexLine> lines, ArrayList<FlexItemBlockBox> items) {
        FlexLine line = createFirstLineToList(lines);

        for(int i = 0; i< items.size();i++){
            FlexItemBlockBox Item = items.get(i);
            //try set item to line
            boolean result = line.registerItem(Item);
            if (!result) {
                //this item does not fit anymore
                //make new line and register item to it

                FlexLine newLine;
                if(isRowContainer()) {
                    newLine = new FlexLineRow(this);
                } else {
                    newLine = new FlexLineColumn(this);
                }
                lines.add(newLine);
                newLine.registerItem(Item);
                line = newLine;
            }
        }

        if(flexWrap == FLEX_WRAP_WRAP_REVERSE)
            Collections.reverse(lines);
    }

    protected void fixContainerHeight(FlexItemBlockBox Item){
        if (!hasFixedHeight()) {
            //height is not fixed
            if (isRowContainer()) {
                //row
                if(max_size.height == -1){
                    //container has no height limit
                    if (Item.bounds.y + Item.totalHeight() > crossSize) {
                        //Item is higher than container so far
                        crossSize = Item.bounds.y + Item.totalHeight();
                        setContentHeight(crossSize);
                    }


                } else {
                    //container has max height limit
                    if(Item.bounds.y + Item.totalHeight() < max_size.height){
                        //it fits to max
                        if (Item.bounds.y + Item.totalHeight() > crossSize) {
                            crossSize = Item.bounds.y + Item.totalHeight();
                            setContentHeight(crossSize);
                        }
                    } else {
                        //it doesn't fit to max, set max
                        setContentHeight(max_size.height);
                    }
                }

            } else {
                mainSize += Item.bounds.height;
                setContentHeight(mainSize);

            }
        }
    }

    private void handleFlexGrow(ArrayList<FlexLine> lines){
        //todo: distribute rest of positive size when some item after shrink use its max width size

        for (FlexLine line : lines ){
            int countOfFlexGrowValue = 0;
            int mainSizeOfItemsInLine = 0;
            for (int j = 0; j < line.itemsInLine.size(); j++){
                FlexItemBlockBox itemInRow = line.itemsInLine.get(j);
                countOfFlexGrowValue += itemInRow.flexGrowValue;
                if(isRowContainer()) {
                    mainSizeOfItemsInLine += itemInRow.hypotheticalMainSize + itemInRow.margin.left + itemInRow.margin.right +
                            itemInRow.padding.left + itemInRow.padding.right +
                            itemInRow.border.left + itemInRow.border.right;
                } else {
                    mainSizeOfItemsInLine += itemInRow.bounds.height;
                }
            }
            if(countOfFlexGrowValue == 0)
                continue;

            float  pieceOfRemainSize = ((float) mainSize - (float)mainSizeOfItemsInLine) / (float)countOfFlexGrowValue;
            if(pieceOfRemainSize <= 0)
                continue;

            for (int j = 0; j < line.itemsInLine.size(); j++){
                FlexItemBlockBox itemInRow = line.itemsInLine.get(j);

                if(isRowContainer()) {
                    int result = (int) (availwidth + pieceOfRemainSize * itemInRow.flexGrowValue);
                    if (itemInRow.hypotheticalMainSize + result > itemInRow.max_size.width && itemInRow.max_size.width != -1) {
                        itemInRow.hypotheticalMainSize = itemInRow.max_size.width;
                    } else
                        itemInRow.hypotheticalMainSize += result;
                } else {
                    int result = (int) (itemInRow.content.height + pieceOfRemainSize * itemInRow.flexGrowValue);
                    if (itemInRow.content.height + result > itemInRow.max_size.height&& itemInRow.max_size.height != -1) {
                        itemInRow.content.height = itemInRow.max_size.height;
                    } else
                        itemInRow.content.height = result;

                    itemInRow.bounds.height = itemInRow.content.height +
                            itemInRow.margin.top + itemInRow.margin.bottom +
                            itemInRow.padding.top + itemInRow.padding.bottom +
                            itemInRow.border.top + itemInRow.border.bottom;
                }
            }
        }
    }

    private void handleFlexShrink(ArrayList<FlexLine> lines) {
        //todo: distribute rest of negative size when some item after shrink use its min width size or minimal content

        for (FlexLine line : lines ){
            int countOfFlexShrinkValue = 0;
            int mainSizeOfItemsInLine = 0;

            for (int j = 0; j < line.itemsInLine.size(); j++){
                FlexItemBlockBox itemInRow = line.itemsInLine.get(j);
                countOfFlexShrinkValue += itemInRow.flexShrinkValue;
                if(isRowContainer()) {
                    mainSizeOfItemsInLine += itemInRow.hypotheticalMainSize + itemInRow.margin.left + itemInRow.margin.right +
                            itemInRow.padding.left + itemInRow.padding.right +
                            itemInRow.border.left + itemInRow.border.right;
                } else {
                    mainSizeOfItemsInLine += itemInRow.bounds.height;
                }
            }

            if(countOfFlexShrinkValue == 0 || mainSize == 0)
                continue;

            float pieceOfOverflowSize = ((float) mainSizeOfItemsInLine - (float)mainSize) / (float)countOfFlexShrinkValue;
            if(pieceOfOverflowSize <= 0)
                continue;

            for (int j = 0; j < line.itemsInLine.size(); j++){
                FlexItemBlockBox itemInRow = line.itemsInLine.get(j);

                if(isRowContainer()) {
                    int result = (int) (availwidth - pieceOfOverflowSize * itemInRow.flexShrinkValue);

                    if (itemInRow.hypotheticalMainSize + result < itemInRow.min_size.width && itemInRow.min_size.width != -1) {
                        itemInRow.hypotheticalMainSize = itemInRow.min_size.width;
                    } else if(itemInRow.hypotheticalMainSize + result < itemInRow.getMinimalContentWidth()) {
                        //item should shrink less than its main size content, it is not possible
                        itemInRow.hypotheticalMainSize = itemInRow.getMinimalContentWidth();
                    }else
                        itemInRow.hypotheticalMainSize += result;
                } else {
                    int result = (int) (itemInRow.content.height - pieceOfOverflowSize * itemInRow.flexShrinkValue);

                    if (result < itemInRow.min_size.height && itemInRow.min_size.height != -1) {
                        itemInRow.content.height = itemInRow.min_size.height;
                    } else if(itemInRow.getSubBoxNumber() != 0 && result < itemInRow.getSubBox(getSubBoxNumber()-1).bounds.y + itemInRow.getSubBox(getSubBoxNumber()-1).bounds.height) {
                        //item should shrink less than its cross size content, it is not possible
                        itemInRow.content.height = itemInRow.getSubBox(getSubBoxNumber()-1).bounds.y + itemInRow.getSubBox(getSubBoxNumber()-1).bounds.height;
                    } else
                        itemInRow.content.height = result;

                    itemInRow.bounds.height = itemInRow.content.height +
                            itemInRow.margin.top + itemInRow.margin.bottom +
                            itemInRow.padding.top + itemInRow.padding.bottom +
                            itemInRow.border.top + itemInRow.border.bottom;
                }
            }
        }

    }

    private void alignByJustifyContent(ArrayList<FlexLine> lines){
         for (int i = 0; i < lines.size(); i++) {
             FlexLine flexLine = lines.get(i);
             int widthOfPreviousItems = 0;
             int totalWidthOfItems = 0;
             for(int j = 0; j < flexLine.itemsInLine.size(); j++)
                 totalWidthOfItems += flexLine.itemsInLine.get(j).getWidth();

             if((isDirectionReversed() || justifyContent == JUSTIFY_CONTENT_FLEX_END) && justifyContent != JUSTIFY_CONTENT_SPACE_BETWEEN && justifyContent != JUSTIFY_CONTENT_FLEX_START) {
                 for (int j = flexLine.itemsInLine.size() - 1; j >= 0; j--) {
                     FlexItemBlockBox item = flexLine.itemsInLine.get(j);
                     applyJustifyContent(flexLine, item, widthOfPreviousItems, j, totalWidthOfItems);
                     widthOfPreviousItems += item.getWidth();

                 }
             } else {
                 for (int j = 0; j < flexLine.itemsInLine.size(); j++) {
                     FlexItemBlockBox item = flexLine.itemsInLine.get(j);
                     applyJustifyContent(flexLine, item, widthOfPreviousItems, j, totalWidthOfItems);
                     widthOfPreviousItems += item.getWidth();
                 }
             }
         }
    }

    private void applyJustifyContent(FlexLine line , FlexItemBlockBox item, int widthOfPreviousItems, int j, int totalWidthOfItems){

        if  (justifyContent == JUSTIFY_CONTENT_FLEX_END) {
            if (isDirectionReversed())
                item.setPosition(widthOfPreviousItems, item.bounds.y);
            else
                item.setPosition(mainSize - widthOfPreviousItems - item.bounds.width , item.bounds.y);

        } else if (justifyContent == JUSTIFY_CONTENT_CENTER) {
            int halfOfRemainSpace = (mainSize) / 2;
            item.setPosition(halfOfRemainSpace - totalWidthOfItems/2 + widthOfPreviousItems, item.bounds.y);

        } else if (justifyContent == JUSTIFY_CONTENT_SPACE_BETWEEN) {
            if(line.itemsInLine.size()-1 == 0 || totalWidthOfItems > mainSize){
                //only one item or items are outside of container - it works like start
                if (isDirectionReversed())
                    item.setPosition(mainSize - widthOfPreviousItems - item.bounds.width , item.bounds.y);
                else
                    item.setPosition(widthOfPreviousItems, item.bounds.y);

            } else {
                int spaceBetween = 0;

                if(isDirectionReversed()) {
                    if(j == 0) {
                        item.setPosition(mainSize -item.bounds.width, item.bounds.y);
                    } else if (j == line.itemsInLine.size()-1) {
                        item.setPosition(0, item.bounds.y);
                    } else {//it is not first item in row
                        spaceBetween = line.getRemainingMainSpace() / (line.itemsInLine.size() - 1);
                        item.setPosition(spaceBetween * (line.itemsInLine.size()-1 -j) + totalWidthOfItems - widthOfPreviousItems - item.bounds.width, item.bounds.y);
                    }
                } else {
                    if (j == 0) {
                        item.setPosition(0, item.bounds.y);
                    } else {//it is not first item in row
                        spaceBetween = line.getRemainingMainSpace() / (line.itemsInLine.size() - 1);
                        item.setPosition(spaceBetween * j + widthOfPreviousItems, item.bounds.y);
                    }
                }
            }

        } else if (justifyContent == JUSTIFY_CONTENT_SPACE_AROUND) {
            if(line.itemsInLine.size()-1 == 0 || totalWidthOfItems > mainSize){
                //only one item - it works like center
                int halfOfRemainSpace = (bounds.x + mainSize) / 2;
                item.setPosition(halfOfRemainSpace - totalWidthOfItems/2 + widthOfPreviousItems, item.bounds.y);
            } else {
                //more than one item
                int spaceAround = line.getRemainingMainSpace() / (line.itemsInLine.size());
                spaceAround /= 2;
                if(isDirectionReversed()){
                        item.setPosition(spaceAround + (line.itemsInLine.size()-1 -j) * 2 * spaceAround + widthOfPreviousItems, item.bounds.y);
                } else {
                        item.setPosition(j * 2 * spaceAround + widthOfPreviousItems + spaceAround, item.bounds.y);
                }
            }

        } else {
            //flex start
            if (isDirectionReversed())
                item.setPosition(mainSize - widthOfPreviousItems - item.bounds.width , item.bounds.y);
            else
                item.setPosition(widthOfPreviousItems, item.bounds.y);
        }
    }

    protected void layoutItemsInColumn(ArrayList<FlexItemBlockBox> items){
        //sort items according to flex property order
        Collections.sort(items);

        //setting height for item (main size)
        for (int i = 0; i < items.size(); i++) {
            FlexItemBlockBox Item = items.get(i);
            Item.hypotheticalMainSize = Item.setHypotheticalMainSize(this);
            Item.hypotheticalMainSize = Item.boundFlexBasisByMinAndMax(Item.hypotheticalMainSize);
        }

            //setting width for item (cross size)
        for (int i = 0; i < items.size(); i++) {
            FlexItemBlockBox Item = items.get(i);

            Item.setCrossSize(this);
            if(Item.crossSizeSetByCont){
                Item.content.width = Item.getMaximalContentWidth();
            }
            Item.fixRightMargin();

            Item.setAvailableWidth(Item.content.width + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);

        }



        for (int i = 0; i < items.size(); i++) {
            FlexItemBlockBox Item = items.get(i);

            if (!Item.contblock)  //block elements containing inline elements only
                Item.layoutInline();
            else //block elements containing block elements
                Item.layoutBlocks();

            Item.fixRightMargin(); //when width was set - margin right was set to container end (like for blocks) which is not right

            if(Item.crossSizeSetByCont){
                Item.content.width = Item.getMaximalContentWidth();
            }

            Item.bounds.width = Item.content.width + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;

            Item.bounds.height = Item.hypotheticalMainSize + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
            Item.content.height = Item.hypotheticalMainSize;

            Item.adjustHeight(this);
            Item.bounds.height = Item.content.height + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
        }

        ArrayList<FlexLine> lines = new ArrayList<>();
        createAndFillLines(lines, items);

        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).setLineCrossSize();
        }
        //flex grow and shrink

        handleFlexGrow(lines);
        handleFlexShrink(lines);


        //ZDE SE NASTAVUJE POZICE

        int sumOfLineWidths = 0;

        for (int i = 0; i < lines.size(); i++) {
            FlexLine lineColumn = lines.get(i);
            int sumOfItemHeights = 0;
            for(int j = 0; j < lineColumn.itemsInLine.size(); j++){
                FlexItemBlockBox item = lineColumn.itemsInLine.get(j);
                item.setPosition(sumOfLineWidths, sumOfItemHeights);
                sumOfItemHeights += item.getHeight();

            }

            sumOfLineWidths+= lineColumn.getWidth();
        }

    }

}
