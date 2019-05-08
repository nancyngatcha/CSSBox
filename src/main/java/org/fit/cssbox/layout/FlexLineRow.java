package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

import java.util.ArrayList;

/**
 * Flex line for "storing" and aligning flex items, which are in the same line in horizontal (row) container.
 */
public class FlexLineRow extends FlexLine{

    /** saved start remaining height used for align-content: stretch */
    protected int savedHeight;

    /** y coordinate of top side of flex line */
    protected int y;

    /**
     * Creates new instance of Flex line for horizontal (row) container.
     * @param owner container
     */
    public FlexLineRow(FlexContainerBlockBox owner) {
        this.owner = owner;
        y = 0;
        height = 0;
        itemsInLine = new ArrayList<>();
        isFirstItem = true;
        width = owner.getContentWidth();
        remainingMainSpace = width;
        refItem = null;
        savedHeight = -1;
    }


    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    @Override
    protected void setPositionAndAdjustCrossSize(FlexItemBlockBox item){
        if (getHeight() < item.getHeight()) {
            if(!(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP && owner.hasFixedHeight()))
                setHeight(item.getHeight());

            //zmenila se velikost line(pro itemy, ktere jiz byly v itemsInLine)
             if (owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_STRETCH) {
                    for (int y = 0; y < itemsInLine.size(); y++) {
                        FlexItemBlockBox itemInLine = itemsInLine.get(y);
                        if((itemInLine.alignSelf == FlexItemBlockBox.ALIGN_SELF_AUTO && owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_STRETCH)
                                || itemInLine.alignSelf == FlexItemBlockBox.ALIGN_SELF_STRETCH) {

                            //if item is set by height value, it does not stretch
                            if (!itemInLine.hasFixedHeight() && !itemInLine.isNotAlignSelfAuto() && itemInLine.alignSelf != FlexItemBlockBox.ALIGN_SELF_STRETCH) {

                                itemInLine.bounds.height = getHeight();
                                if (itemInLine.bounds.height >itemInLine.max_size.height && itemInLine.max_size.height != -1) {
                                    itemInLine.bounds.height = itemInLine.max_size.height + itemInLine.padding.top + itemInLine.padding.bottom +
                                            itemInLine.border.top + itemInLine.border.bottom +
                                            itemInLine.margin.top + itemInLine.margin.bottom;
                                }
                                itemInLine.content.height = itemInLine.bounds.height - itemInLine.padding.top - itemInLine.padding.bottom -
                                        itemInLine.border.top - itemInLine.border.bottom - itemInLine.margin.top - itemInLine.margin.bottom;
                            }
                        }

                    }
                } else if (owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_FLEX_END) {
                    for (int c = 0; c < itemsInLine.size(); c++) {
                            itemsInLine.get(c).setPosition(itemsInLine.get(c).bounds.x, y + getHeight() - itemsInLine.get(c).getHeight());
                    }
                } else if (owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_CENTER) {
                    for (int c = 0; c < itemsInLine.size(); c++) {
                            itemsInLine.get(c).setPosition(itemsInLine.get(c).bounds.x, y + (getHeight() - itemsInLine.get(c).getHeight()) / 2);
                    }
                }
        }

        setCrossCoordToItem(item);

        //fixing line height because of align-items baseline
        if (getHeight() < item.bounds.y - y +item.getHeight()) {
            if (!(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP && owner.hasFixedHeight()))
                setHeight(item.bounds.y - y + item.getHeight());
        }

        for (int c = 0; c < itemsInLine.size(); c++) {
            if(itemsInLine.get(c).alignSelf != CSSProperty.AlignSelf.AUTO){
                applyAlignSelf(itemsInLine.get(c));
            }
        }
    }

    @Override
    protected void setLineCrossSize() {
        if (owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP) {
            setHeight(owner.crossSize);
            return;
        }
            int height = 0;
            for (FlexItemBlockBox item : itemsInLine) {
                if (height < item.bounds.height)
                    height = item.bounds.height;
            }
            setHeight(height);

    }

    @Override
    protected void applyAlignItemsAndSelf() {
        for (int j = 0; j < itemsInLine.size(); j++) {
            if (j == 0) {
                //is first item, so it is easier to handle (it does not need to fix previous items)
                setCrossCoordToItem(itemsInLine.get(j));
                continue;
            }
            setPositionAndAdjustCrossSize(itemsInLine.get(j));
        }
    }

    @Override
    protected void setCrossCoordToItem(FlexItemBlockBox item){

        if(applyAlignSelf(item))
            return;

         if (owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_FLEX_START)
             if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE)
                 item.setPosition(0, y + getHeight() - item.getHeight());
             else
                 item.setPosition(0, y);

        else if (owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_FLEX_END)
            if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE)
                item.setPosition(0, y);
            else
                item.setPosition(0, y + getHeight() - item.getHeight());

        else if (owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_CENTER)
            item.setPosition(0, y + (getHeight() - item.getHeight()) / 2);

        else if (owner.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_BASELINE)
            alignBaseline(item);
        else
            alignStretch(item);
    }

    @Override
    protected boolean applyAlignSelf(FlexItemBlockBox item){
        if(!item.isNotAlignSelfAuto())
            return false;


        if (getHeight() < item.getHeight())
            if(!(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP && owner.hasFixedHeight()))
                setHeight(item.getHeight());

        if(item.alignSelf == CSSProperty.AlignSelf.INHERIT) {
            CSSProperty.AlignSelf parentAlignSelf =  owner.style.getProperty("align-self");
            if(parentAlignSelf != null && parentAlignSelf != CSSProperty.AlignSelf.AUTO && parentAlignSelf != CSSProperty.AlignSelf.INHERIT && parentAlignSelf != CSSProperty.AlignSelf.INITIAL){
                item.alignSelf = parentAlignSelf;
            }
        } else if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_FLEX_START)
            if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE)
                item.setPosition(0, y + getHeight() - item.getHeight());
            else
                item.setPosition(0, y);
        else if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_FLEX_END)
            if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE)
                item.setPosition(0, y);
            else
                item.setPosition(0, y + getHeight() - item.getHeight());
        else if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_CENTER)
            item.setPosition(0, y + (getHeight() - item.getHeight()) / 2);
        else if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_BASELINE)
            alignBaseline(item);
        else //STRETCH
            alignStretch(item);
    return true;
    }

    /**
     * Aligns item according to align-items (or align-self) value baseline.
     * @param item item to align
     */
    private void alignBaseline(FlexItemBlockBox item){
        if (item.getPadding().top + item.getMargin().top + item.ctx.getBaselineOffset() > refItem.getPadding().top + refItem.getMargin().top + refItem.ctx.getBaselineOffset()) {
            refItem = item;
        }
        for (int j = 0; j < itemsInLine.size(); j++) {
            FlexItemBlockBox itemInLine = itemsInLine.get(j);
            if((!itemInLine.isNotAlignSelfAuto() && owner.alignItems == CSSProperty.AlignItems.BASELINE)  || itemInLine.alignSelf == CSSProperty.AlignSelf.BASELINE ) {
                if (itemInLine != refItem) {
                    itemInLine.setPosition(itemInLine.bounds.x, y + refItem.getPadding().top + refItem.getMargin().top+ refItem.ctx.getBaselineOffset()
                            - itemInLine.getPadding().top - itemInLine.getMargin().top - itemInLine.ctx.getBaselineOffset());
                    if (getHeight() < (refItem.getPadding().top + refItem.getMargin().top + refItem.ctx.getBaselineOffset()
                            - itemInLine.getPadding().top - itemInLine.getMargin().top  - itemInLine.ctx.getBaselineOffset() + itemInLine.bounds.height)) {
                        setHeight((refItem.getPadding().top + refItem.getMargin().top  + refItem.ctx.getBaselineOffset()- itemInLine.getPadding().top - itemInLine.getMargin().top - itemInLine.ctx.getBaselineOffset() + itemInLine.bounds.height));

                    }
                    owner.fixContainerHeight(itemsInLine.get(j));
                }

                item.setPosition(0, y + refItem.getPadding().top + refItem.getMargin().top+ refItem.ctx.getBaselineOffset() - item.getPadding().top - item.getMargin().top - item.ctx.getBaselineOffset());
            } else {
                item.setPosition(0, y);
            }
        }
    }

    /**
     * Aligns item according to align-items (or align-self) value stretch.
     * @param item item to align
     */
    private void alignStretch(FlexItemBlockBox item){
        if(!item.hasFixedHeight()) {
            item.bounds.height = getHeight();
            if (item.bounds.height > item.max_size.height && item.max_size.height != -1) {
                item.bounds.height = item.max_size.height + item.padding.top + item.padding.bottom + item.border.top + item.border.bottom + item.margin.top + item.margin.bottom;
            }
        }

        if(owner.flexWrap == CSSProperty.FlexWrap.NOWRAP){
            if(owner.hasFixedHeight() && getHeight() > owner.getContentHeight()) {
                if(!item.hasFixedHeight()) {
                    if(owner.style.getProperty("height") != CSSProperty.Height.length)
                        owner.setContentHeight(getHeight());

                    item.bounds.height = owner.getContentHeight();
                    if (item.bounds.height > item.max_size.height && item.max_size.height != -1) {
                        item.bounds.height = item.max_size.height + item.padding.top + item.padding.bottom + item.border.top + item.border.bottom + item.margin.top + item.margin.bottom;
                    }
                }
            }

        }
        item.content.height = item.bounds.height - item.padding.top - item.padding.bottom - item.border.top - item.border.bottom - item.margin.top - item.margin.bottom;

        if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE)
            item.setPosition(0, y + getHeight() - item.getHeight());
        else
            item.setPosition(0, y);

    }

    protected void applyAlignContent(ArrayList<FlexLine> lines, int countOfPreviousLines){
        if(owner.crossSize == 0 || owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP) //is container height set? is container nowrap?
            return;

        int remainingHeightSpace = owner.crossSize;
        for (int iline = 0; iline < lines.size(); iline++) {
            FlexLineRow line = (FlexLineRow) lines.get(iline);
            if(line.savedHeight == -1)
                remainingHeightSpace -= line.getHeight();
            else
                remainingHeightSpace -= line.savedHeight;
        }

        if(owner.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_FLEX_START){
            if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE)
                setY(getY()+remainingHeightSpace);
            else
                return; //was already set

        } else if(owner.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_FLEX_END){
            if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE)
                return; //was already set
            else
                setY(getY()+remainingHeightSpace);

        } else if(owner.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_CENTER){
            setY(getY()+remainingHeightSpace/2); //wrap reverse is same (just centerize)

        } else if(owner.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_STRETCH){
            savedHeight = getHeight();
            setHeight(getHeight() + remainingHeightSpace/lines.size()); //wrap reverse is same

        } else if(owner.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_SPACE_BETWEEN){
            if(lines.size() != 1)
                setY(getY()+(countOfPreviousLines)*remainingHeightSpace/(lines.size()-1)); //wrap reverse is same
            else
                if(owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_WRAP_REVERSE) //wrap reverse is same as flex-end
                    setY(getY()+remainingHeightSpace);
                else
                    return; //was already set as flex-start
        } else if(owner.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_SPACE_AROUND){
            if(countOfPreviousLines == 0)
                setY(getY()+ remainingHeightSpace/ 2 / lines.size());
            else
                setY(getY() + remainingHeightSpace/ 2 / lines.size() +(countOfPreviousLines)*remainingHeightSpace/ lines.size()+1);

        }
    }

    @Override
    protected void alignByJustifyContent(){
        int widthOfPreviousItems = 0;
        int totalWidthOfItems = 0;
        for(int j = 0; j < itemsInLine.size(); j++)
            totalWidthOfItems += itemsInLine.get(j).getWidth();

        if((owner.isDirectionReversed() || owner.justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_FLEX_END) &&
                owner.justifyContent != FlexContainerBlockBox.JUSTIFY_CONTENT_SPACE_BETWEEN &&
                owner.justifyContent != FlexContainerBlockBox.JUSTIFY_CONTENT_FLEX_START) {
            for (int j = itemsInLine.size() - 1; j >= 0; j--) {
                FlexItemBlockBox item = itemsInLine.get(j);
                applyJustifyContent(item, widthOfPreviousItems, j, totalWidthOfItems);
                widthOfPreviousItems += item.getWidth();

            }
        } else {
            for (int j = 0; j < itemsInLine.size(); j++) {
                FlexItemBlockBox item = itemsInLine.get(j);
                applyJustifyContent(item, widthOfPreviousItems, j, totalWidthOfItems);
                widthOfPreviousItems += item.getWidth();
            }
        }
    }
    @Override
    protected void applyJustifyContent(FlexItemBlockBox item, int widthOfPreviousItems, int j, int totalWidthOfItems){

        if  (owner.justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_FLEX_END) {
            if (owner.isDirectionReversed())
                item.setPosition(widthOfPreviousItems, item.bounds.y);
            else
                item.setPosition(owner.mainSize - widthOfPreviousItems - item.bounds.width , item.bounds.y);

        } else if (owner.justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_CENTER) {
            int halfOfRemainSpace = (owner.mainSize) / 2;
            item.setPosition(halfOfRemainSpace - totalWidthOfItems/2 + widthOfPreviousItems, item.bounds.y);

        } else if (owner.justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_SPACE_BETWEEN) {
            if(itemsInLine.size()-1 == 0 || totalWidthOfItems > owner.mainSize){
                //only one item or items are outside of container - it works like start
                if (owner.isDirectionReversed())
                    item.setPosition(owner.mainSize - widthOfPreviousItems - item.bounds.width , item.bounds.y);
                else
                    item.setPosition(widthOfPreviousItems, item.bounds.y);

            } else {
                int spaceBetween = 0;

                if(owner.isDirectionReversed()) {
                    if(j == 0) {
                        item.setPosition(owner.mainSize -item.bounds.width, item.bounds.y);
                    } else if (j == itemsInLine.size()-1) {
                        item.setPosition(0, item.bounds.y);
                    } else {//it is not first item in row
                        spaceBetween = getRemainingMainSpace() / (itemsInLine.size() - 1);
                        item.setPosition(spaceBetween * (itemsInLine.size()-1 -j) + totalWidthOfItems - widthOfPreviousItems - item.bounds.width, item.bounds.y);
                    }
                } else {
                    if (j == 0) {
                        item.setPosition(0, item.bounds.y);
                    } else {//it is not first item in row
                        spaceBetween = getRemainingMainSpace() / (itemsInLine.size() - 1);
                        item.setPosition(spaceBetween * j + widthOfPreviousItems, item.bounds.y);
                    }
                }
            }

        } else if (owner.justifyContent == FlexContainerBlockBox.JUSTIFY_CONTENT_SPACE_AROUND) {
            if(itemsInLine.size()-1 == 0 || totalWidthOfItems > owner.mainSize){
                //only one item - it works like center
                int halfOfRemainSpace = (owner.bounds.x + owner.mainSize) / 2;
                item.setPosition(halfOfRemainSpace - totalWidthOfItems/2 + widthOfPreviousItems, item.bounds.y);
            } else {
                //more than one item
                int spaceAround = getRemainingMainSpace() / (itemsInLine.size());
                spaceAround /= 2;
                if(owner.isDirectionReversed()){
                    item.setPosition(spaceAround + (itemsInLine.size()-1 -j) * 2 * spaceAround + widthOfPreviousItems, item.bounds.y);
                } else {
                    item.setPosition(j * 2 * spaceAround + widthOfPreviousItems + spaceAround, item.bounds.y);
                }
            }

        } else {
            //flex start
            if (owner.isDirectionReversed())
                item.setPosition(owner.mainSize - widthOfPreviousItems - item.bounds.width , item.bounds.y);
            else
                item.setPosition(widthOfPreviousItems, item.bounds.y);
        }
    }



}
