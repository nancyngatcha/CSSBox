package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import org.w3c.dom.Element;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class FlexContainerBlockBox extends FlexItemBlockBox {

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


    /** flex direction specified by the style */
    protected CSSProperty.FlexDirection flexDirection;
    /** flex wrap specified by the style */
    protected CSSProperty.FlexWrap flexWrap;
    /** justify content specified by the style */
    protected CSSProperty.JustifyContent justifyContent;
    /** align content specified by the style */
    protected CSSProperty.AlignContent alignContent;
    /** align items specified by the style */
    protected CSSProperty.AlignItems alignItems;

    /** main size of this box (in horizontal (row) container it is width, in vertical it is height) */
    protected int mainSize;
    /** cross size of this box (in horizontal (row) container it is height, in vertical it is width) */
    protected int crossSize;

    /** defines if is main size set by content */
    protected boolean mainSizeSetByCont;

    /** first flex line (row or column based on container direction) of this container */
    protected FlexLine firstFlexLine;

    /**
     * Creates new instance of flex container block box.
     */
    public FlexContainerBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
        firstFlexLine = null;
        mainSizeSetByCont = false;
    }

    /** Converts an inline box to a flex container block box */
    public FlexContainerBlockBox(InlineBox src) {
        super(src);
        typeoflayout = new FlexBoxLayoutManager(this);
        isblock = true;
        firstFlexLine = null;
        mainSizeSetByCont = false;
    }

    /**
     * Sets inceptive main size before layouting its content.
     */
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

    /**
     * Sets inceptive cross size before layouting its content.
     */
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

    /**
     * Loads styles belonging to flex contaieners.
     */
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

    /**
     * Returns true if flex-direction of this container is row or row-reverse.
     * @return true if is row container (horizontal), else if column (vertical)
     */
    public boolean isRowContainer() {
        if (flexDirection == FLEX_DIRECTION_ROW || flexDirection == FLEX_DIRECTION_ROW_REVERSE)
            return true;
        else
            return false;
    }

    /**
     * Returns true if flex-direction of this container is column-reverse or row-reverse.
     * @return true if is reversed container, else if is not
     */
    public boolean isDirectionReversed() {
        if (flexDirection == FLEX_DIRECTION_ROW_REVERSE || flexDirection == FLEX_DIRECTION_COLUMN_REVERSE)
            return true;
        else
            return false;
    }

    /**
     * Main method for layouting content of horizontal (row) container.
     * @param listItems ArrayList with items
     */
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

            if (Item.crossSizeSetByCont) {
                if(Item.getSubBoxNumber() != 0) {
                    Item.content.height = Item.getSubBox(Item.getSubBoxNumber() - 1).getBounds().y
                            + Item.getSubBox(Item.getSubBoxNumber() - 1).getBounds().height;
                    Item.bounds.height = Item.content.height + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
                    if(crossSize < Item.bounds.height)
                        crossSize = Item.bounds.height;
                }
            } else {
                Item.bounds.height = Item.totalHeight();
                Item.content.height = Item.totalHeight() - Item.padding.top - Item.padding.bottom - Item.border.top - Item.border.bottom - Item.margin.top - Item.margin.bottom;
            }

        }


        for (int i = 0; i < listItems.size(); i++) {
            FlexItemBlockBox Item = listItems.get(i);
            if (Item.crossSizeSetByPercentage) {
                for (int j = 0; j < Item.getSubBoxNumber(); j++) {
                    if (j == Item.getSubBoxNumber() - 1)
                        Item.content.height += Item.getSubBox(j).getContentHeight() + Item.getSubBox(j).getContentY();

                }
                Item.bounds.height = Item.content.height + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;

            }

        }

        //set of line height
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).setLineCrossSize();
        }


        for (int i = 0; i < lines.size(); i++) {
            FlexLine flexLine = lines.get(i);

            int sumOfPreviousLinesHeights = 0;
            for (int y = 0; y < i; y++) {
                sumOfPreviousLinesHeights += lines.get(y).getHeight();
            }
            ((FlexLineRow)flexLine).setY(sumOfPreviousLinesHeights);
            flexLine.applyAlignContent(lines, i);

            //set Y coord of items
            flexLine.applyAlignItemsAndSelf();

        }

        //set X coord of items
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).alignByJustifyContent();
        }

        //set container height
        for (int i = 0; i < lines.size(); i++)
            for (int j = 0; j < lines.get(i).itemsInLine.size(); j++)
                fixContainerHeight(lines.get(i).itemsInLine.get(j));
    }


    /**
     * Creates first line to ArrayList of lines in parameter based on direction of container.
     * @param lines ArrayList of line
     * @return new flex line
     */
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

    /**
     * Creates needed flex lines based on direction of container and fill them widh items.
     * @param lines
     * @param items
     */
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

    /**
     * Fixes height of container after layouting items by height of item if needed (for example after align-items: baseline).
     * @param Item flex item
     */
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

    /**
     * Distributes remaining space in main size of container to items based on their flex-grow CSS property in each line.
     * @param lines flex lines with items
     */
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

    /**
     * Distributes overflown (negative) space in main size of container to items based on their flex-shrink CSS property in each line.
     * @param lines flex lines with items
     */
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
                FlexItemBlockBox itemInLine = line.itemsInLine.get(j);

                if(isRowContainer()) {
                    int result = (int) (availwidth - pieceOfOverflowSize * itemInLine.flexShrinkValue);

                    if (itemInLine.hypotheticalMainSize + result < itemInLine.min_size.width && itemInLine.min_size.width != -1) {
                        itemInLine.hypotheticalMainSize = itemInLine.min_size.width;
                    } else if(itemInLine.hypotheticalMainSize + result < itemInLine.getMinimalContentWidth()) {
                        //item should shrink less than its main size content, it is not possible
                        itemInLine.hypotheticalMainSize = itemInLine.getMinimalContentWidth();
                    }else
                        itemInLine.hypotheticalMainSize += result;
                } else {
                    int result = (int) (itemInLine.content.height - pieceOfOverflowSize * itemInLine.flexShrinkValue);

                    if (result < itemInLine.min_size.height && itemInLine.min_size.height != -1) {
                        itemInLine.content.height = itemInLine.min_size.height;
                    } else if(itemInLine.getSubBoxNumber() != 0 && result < itemInLine.getSubBox(itemInLine.getSubBoxNumber()-1).bounds.y + itemInLine.getSubBox(itemInLine.getSubBoxNumber()-1).bounds.height) {
                        //item should shrink less than its cross size content, it is not possible
                        itemInLine.content.height = itemInLine.getSubBox(itemInLine.getSubBoxNumber()-1).bounds.y + itemInLine.getSubBox(itemInLine.getSubBoxNumber()-1).bounds.height;
                    } else
                        itemInLine.content.height = result;

                    itemInLine.bounds.height = itemInLine.content.height +
                            itemInLine.margin.top + itemInLine.margin.bottom +
                            itemInLine.padding.top + itemInLine.padding.bottom +
                            itemInLine.border.top + itemInLine.border.bottom;
                }
            }
        }

    }

    /**
     * Main method for layouting content of vertical (column) container.
     * @param items ArrayList with items
     */
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

            Item.fixRightMargin();

            if(Item.crossSizeSetByCont){
                Item.content.width = Item.getMaximalContentWidth();
                if(Item.content.width > crossSize)
                    Item.content.width = crossSize  - (Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);
            }
            Item.setAvailableWidth(Item.content.width + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);
        }

        for (int i = 0; i < items.size(); i++) {
            FlexItemBlockBox Item = items.get(i);

            if (!Item.contblock)  //item contains inline elements only
                Item.layoutInline();
            else //item contains at least one block element, layout all as blocks
                Item.layoutBlocks();

            Item.fixRightMargin(); //when width was set - margin right was set to container end (like for blocks) which is not right

            if(Item.crossSizeSetByCont){
                Item.content.width = Item.getMaximalContentWidth();
                if(Item.content.width > crossSize)
                    Item.content.width = crossSize  - (Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right);
            }

            Item.bounds.width = Item.content.width + Item.padding.left + Item.padding.right + Item.border.left + Item.border.right + Item.margin.left + Item.margin.right;

            Item.bounds.height = Item.hypotheticalMainSize + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
            Item.content.height = Item.hypotheticalMainSize;

            Item.adjustHeight(this);
            Item.bounds.height = Item.content.height + Item.padding.top + Item.padding.bottom + Item.border.top + Item.border.bottom + Item.margin.top + Item.margin.bottom;
        }

        ArrayList<FlexLine> lines = new ArrayList<>();
        createAndFillLines(lines, items);

        for (int i = 0; i < lines.size(); i++)
            lines.get(i).setLineCrossSize();

        //flex grow and shrink
        handleFlexGrow(lines);
        handleFlexShrink(lines);

        //setting of line height
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).setLineCrossSize();
            if(lines.get(i).height > mainSize)
                mainSize = lines.get(i).height;
        }


        for (int i = 0; i < lines.size(); i++) {
            FlexLine flexLine = lines.get(i);

            int sumOfPreviousLinesWidths = 0;
            for (int y = 0; y < i; y++) {
                sumOfPreviousLinesWidths += lines.get(y).getWidth();
            }
            ((FlexLineColumn)flexLine).setX(sumOfPreviousLinesWidths);
            flexLine.applyAlignContent(lines, i);

            //set X coord of items
            flexLine.applyAlignItemsAndSelf();

        }

        //set Y coord of items
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).alignByJustifyContent();
        }
    }

}
