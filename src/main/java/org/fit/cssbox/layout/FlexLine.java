package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

import java.util.ArrayList;

public class FlexLine {
    /**
     * The FlexContainerBlockBox containing the lines
     */
    private FlexContainerBlockBox parent;

    /**
     * Total width in pixels (for horizontal alignment)
     */
    private int width;

    private int height;

    /**
     * The Y position of this line top
     */
    private int y;

    private boolean isFirstItem;

    protected int remainingWidthSpace;

    protected int savedHeight;

    protected ArrayList<FlexItemBlockBox> itemsInLine;

    private FlexItemBlockBox itemWithMaxTopPadding;

    public FlexLine(FlexContainerBlockBox parent) {
        this.parent = parent;
        y = 0;
        height = 0;
        itemsInLine = new ArrayList<>();
        isFirstItem = true;
        width = parent.getContentWidth();
        remainingWidthSpace = width;
        itemWithMaxTopPadding = null;
        savedHeight = -1;
    }

    public FlexContainerBlockBox getParent() {
        return parent;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRemainingWidthSpace() {
        return remainingWidthSpace;
    }

    public void setRemainingWidthSpace(int remainingWidthSpace) {
        this.remainingWidthSpace = remainingWidthSpace;
        if(this.remainingWidthSpace < 0)
            this.remainingWidthSpace = 0;
    }

    protected boolean registerItemAndSetItsPosition(FlexItemBlockBox item) {

        if(parent.flexWrap == CSSProperty.FlexWrap.NOWRAP && parent.hasFixedHeight())
            setHeight(parent.getContentHeight());


        if (isFirstItem) {
            if (getHeight() < item.getHeight())
                setHeight(item.getHeight());
            setRemainingWidthSpace(remainingWidthSpace - item.getWidth());
            itemsInLine.add(item);

            itemWithMaxTopPadding = item;


            isFirstItem = false;

            return true;
        }

        //not first item in row, does it fit into line?
//        int sumOfItemsWidths = 0;
//        for(int i = 0; i < itemsInLine.size(); i++)
//            sumOfItemsWidths += itemsInLine.get(i).getWidth();


        if((item.totalWidth() > remainingWidthSpace) && parent.flexWrap != FlexContainerBlockBox.FLEX_WRAP_NOWRAP)
            return false;

        if(!((FlexContainerBlockBox)(item.parent)).isDirectionRow()) return false;
        //vejde se na radek

        //je treba zmenit vysku radku?
//        setPositionAndAdaptHeight(item, sumOfItemsWidths);
        if (getHeight() < item.getHeight())
            setHeight(item.getHeight());


        setRemainingWidthSpace(remainingWidthSpace - item.getWidth());
        itemsInLine.add(item);


        return true;


    }

    protected void setPositionAndAdaptHeight(FlexItemBlockBox item, int sumOfItemsWidths){
        int x;
        if(parent.flexDirection == FlexContainerBlockBox.FLEX_DIRECTION_ROW_REVERSE){
            x = parent.mainSpace  - sumOfItemsWidths - item.bounds.width;
        } else {
            x = sumOfItemsWidths;
        }

        if (getHeight() < item.getHeight()) {
            if(!(parent.flexWrap == CSSProperty.FlexWrap.NOWRAP && parent.hasFixedHeight()))
                setHeight(item.getHeight());

            //zmenila se velikost line(pro itemy, ktere jiz byly v itemsInLine)
             if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_STRETCH) {
                    for (int y = 0; y < itemsInLine.size(); y++) {
                        if((itemsInLine.get(y).alignSelf == CSSProperty.AlignSelf.AUTO && parent.alignItems == CSSProperty.AlignItems.STRETCH)  || itemsInLine.get(y).alignSelf == CSSProperty.AlignSelf.STRETCH ) {

                            //if item is set by height value, it does not stretch
                            if (!itemsInLine.get(y).hasFixedHeight() && !itemsInLine.get(y).isSetAlignSelf() && itemsInLine.get(y).alignSelf != CSSProperty.AlignSelf.STRETCH) {

                                itemsInLine.get(y).bounds.height = getHeight();
                                if (itemsInLine.get(y).bounds.height > itemsInLine.get(y).max_size.height && itemsInLine.get(y).max_size.height != -1) {
                                    itemsInLine.get(y).bounds.height = itemsInLine.get(y).max_size.height + itemsInLine.get(y).padding.top + itemsInLine.get(y).padding.bottom + itemsInLine.get(y).border.top + itemsInLine.get(y).border.bottom + itemsInLine.get(y).margin.top + itemsInLine.get(y).margin.bottom;
                                }
                                itemsInLine.get(y).content.height = itemsInLine.get(y).bounds.height - itemsInLine.get(y).padding.top - itemsInLine.get(y).padding.bottom - itemsInLine.get(y).border.top - itemsInLine.get(y).border.bottom - itemsInLine.get(y).margin.top - itemsInLine.get(y).margin.bottom;
                            }
                        }

                    }
                } else if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_FLEX_END) {
                    for (int c = 0; c < itemsInLine.size(); c++) {
                            itemsInLine.get(c).setPosition(itemsInLine.get(c).bounds.x, y + getHeight() - itemsInLine.get(c).getHeight());
                    }
                } else if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_CENTER) {
                    for (int c = 0; c < itemsInLine.size(); c++) {
                            itemsInLine.get(c).setPosition(itemsInLine.get(c).bounds.x, y + (getHeight() - itemsInLine.get(c).getHeight()) / 2);
                    }
                }
        }

        setYPositionToItem(item,x);

        //fixing line height because of align-items baseline
        if (getHeight() < item.bounds.y - y +item.getHeight()) {
            if (!(parent.flexWrap == CSSProperty.FlexWrap.NOWRAP && parent.hasFixedHeight()))
                setHeight(item.bounds.y - y + item.getHeight());
        }

        for (int c = 0; c < itemsInLine.size(); c++) {
            if(itemsInLine.get(c).alignSelf != CSSProperty.AlignSelf.AUTO){
                considerAlignSelf(itemsInLine.get(c), itemsInLine.get(c).bounds.x);
            }
        }
    }

    protected void setYPositionToItem(FlexItemBlockBox item, int x ){

        if(considerAlignSelf(item, x))
            return;

        if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_STRETCH) {
            alignStretch(item, x);
        } else if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_FLEX_START)
            item.setPosition(x, y);

        else if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_FLEX_END) {
            item.setPosition(x, y + getHeight() - item.getHeight());
        } else if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_CENTER)
            item.setPosition(x, y + (getHeight() - item.getHeight()) / 2);
        else if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_BASELINE) {
            alignBaseline(item, x);
        } else {
            item.setPosition(x, y);
        }


    }


    private boolean considerAlignSelf(FlexItemBlockBox item, int x){
        if(item.alignSelf == CSSProperty.AlignSelf.AUTO)
            return false;

            if(item.alignSelf == CSSProperty.AlignSelf.FLEX_START) {
                item.setPosition(x, y);

            } else if(item.alignSelf == CSSProperty.AlignSelf.FLEX_END) {
                 item.setPosition(x, y + getHeight() - item.getHeight());
            } else if(item.alignSelf == CSSProperty.AlignSelf.CENTER) {
                 item.setPosition(x, y + (getHeight() - item.getHeight()) / 2);
            } else if(item.alignSelf == CSSProperty.AlignSelf.BASELINE) {
               alignBaseline(item, x);
            } else {
                //STRETCH
                alignStretch(item, x);
            }
            return true;
    }


    private void alignBaseline(FlexItemBlockBox item, int x){
        if (item.getPadding().top > itemWithMaxTopPadding.getPadding().top) {
            itemWithMaxTopPadding = item;
        }
        for (int j = 0; j < itemsInLine.size(); j++) {
            if((itemsInLine.get(j).alignSelf == CSSProperty.AlignSelf.AUTO && parent.alignItems == CSSProperty.AlignItems.BASELINE)  || itemsInLine.get(j).alignSelf == CSSProperty.AlignSelf.BASELINE ) {
                if (itemsInLine.get(j) != itemWithMaxTopPadding) {
                    itemsInLine.get(j).setPosition(itemsInLine.get(j).bounds.x, y + itemWithMaxTopPadding.getPadding().top + itemWithMaxTopPadding.getMargin().top - itemsInLine.get(j).getPadding().top - itemsInLine.get(j).getMargin().top);
                    if (getHeight() < (itemWithMaxTopPadding.getPadding().top + itemWithMaxTopPadding.getMargin().top - itemsInLine.get(j).getPadding().top - itemsInLine.get(j).getMargin().top + itemsInLine.get(j).bounds.height)) {
                        setHeight((itemWithMaxTopPadding.getPadding().top + itemWithMaxTopPadding.getMargin().top - itemsInLine.get(j).getPadding().top - itemsInLine.get(j).getMargin().top + itemsInLine.get(j).bounds.height));

                    }
                    parent.fixContainerHeight(itemsInLine.get(j));
                }

            item.setPosition(x, y + itemWithMaxTopPadding.getPadding().top + itemWithMaxTopPadding.getMargin().top - item.getPadding().top - item.getMargin().top);
            } else {
                item.setPosition(x, y);
            }
        }
    }

    private void alignStretch(FlexItemBlockBox item, int x){
        if(!item.hasFixedHeight()) {
            item.bounds.height = getHeight();
            if (item.bounds.height > item.max_size.height && item.max_size.height != -1) {
                item.bounds.height = item.max_size.height + item.padding.top + item.padding.bottom + item.border.top + item.border.bottom + item.margin.top + item.margin.bottom;
            }
        }

        if(parent.flexWrap == CSSProperty.FlexWrap.NOWRAP){
            if(parent.hasFixedHeight() && getHeight() > parent.getContentHeight()) {
                if(!item.hasFixedHeight()) {
                    if(parent.style.getProperty("height") != CSSProperty.Height.length)
                        parent.setContentHeight(getHeight());

                    item.bounds.height = parent.getContentHeight();
                    if (item.bounds.height > item.max_size.height && item.max_size.height != -1) {
                        item.bounds.height = item.max_size.height + item.padding.top + item.padding.bottom + item.border.top + item.border.bottom + item.margin.top + item.margin.bottom;
                    }
                }
            }

        }
        item.content.height = item.bounds.height - item.padding.top - item.padding.bottom - item.border.top - item.border.bottom - item.margin.top - item.margin.bottom;

        item.setPosition(x, y);

    }

    protected void applyAlignContent(FlexLine flexLine,ArrayList<FlexLine> lines, int countOfPreviousLines){
        if(parent.crossSpace == 0 || parent.flexWrap == CSSProperty.FlexWrap.NOWRAP) //is container height set? is container nowrap?
            return;

        int remainingHeightSpace = parent.crossSpace;
        for (int iline = 0; iline < lines.size(); iline++) {
            FlexLine line = lines.get(iline);
            if(line.savedHeight == -1)
                remainingHeightSpace -= line.getHeight();
            else
                remainingHeightSpace -= line.savedHeight;
        }
        if(parent.alignContent == CSSProperty.AlignContent.FLEX_START){
            return;
        } else if(parent.alignContent == CSSProperty.AlignContent.FLEX_END){
            flexLine.setY(flexLine.getY()+remainingHeightSpace);
        } else if(parent.alignContent == CSSProperty.AlignContent.CENTER){
            flexLine.setY(flexLine.getY()+remainingHeightSpace/2);
        } else if(parent.alignContent == CSSProperty.AlignContent.STRETCH){
            flexLine.savedHeight = flexLine.getHeight();
            flexLine.setHeight(flexLine.getHeight() + remainingHeightSpace/lines.size());

        } else if(parent.alignContent == CSSProperty.AlignContent.SPACE_BETWEEN){
                flexLine.setY(flexLine.getY()+(countOfPreviousLines)*remainingHeightSpace/(lines.size()-1));
        } else if(parent.alignContent == CSSProperty.AlignContent.SPACE_AROUND){
            if(countOfPreviousLines == 0)
                flexLine.setY(flexLine.getY()+ remainingHeightSpace/ 2 / lines.size());
            else
                flexLine.setY(flexLine.getY() + remainingHeightSpace/ 2 / lines.size() +(countOfPreviousLines)*remainingHeightSpace/ lines.size()+1);

        }
    }

}
