package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

import java.util.ArrayList;

public class FlexLineRow {
    /**
     * The FlexContainerBlockBox containing the lines
     */
    private FlexContainerBlockBox parent;

    /**
     * Total width in pixels (for horizontal alignment)
     */
    private int width;

    private int height;

    private int y;

    private boolean isFirstItem;

    protected int remainingWidthSpace;

    protected int savedHeight;

    protected ArrayList<FlexItemBlockBox> itemsInLine;

    private FlexItemBlockBox refItem;

    public FlexLineRow(FlexContainerBlockBox parent) {
        this.parent = parent;
        y = 0;
        height = 0;
        itemsInLine = new ArrayList<>();
        isFirstItem = true;
        width = parent.getContentWidth();
        remainingWidthSpace = width;
        refItem = null;
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

    protected boolean registerItem(FlexItemBlockBox item) {

        if(parent.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP && parent.hasFixedHeight())
            setHeight(parent.getContentHeight());


        if (isFirstItem) {
            if (getHeight() < item.getHeight())
                setHeight(item.getHeight());
            setRemainingWidthSpace(remainingWidthSpace - item.getWidth());
            itemsInLine.add(item);

            refItem = item;


            isFirstItem = false;

            return true;
        }

        //not first item in row, does it fit into line?



        if((item.totalWidth() > remainingWidthSpace) && parent.flexWrap != FlexContainerBlockBox.FLEX_WRAP_NOWRAP)
            return false;

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
            x = parent.mainSize - sumOfItemsWidths - item.bounds.width;
        } else {
            x = sumOfItemsWidths;
        }

        if (getHeight() < item.getHeight()) {
            if(!(parent.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP && parent.hasFixedHeight()))
                setHeight(item.getHeight());

            //zmenila se velikost line(pro itemy, ktere jiz byly v itemsInLine)
             if (parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_STRETCH) {
                    for (int y = 0; y < itemsInLine.size(); y++) {
                        FlexItemBlockBox itemInLine = itemsInLine.get(y);
                        if((itemInLine.alignSelf == FlexItemBlockBox.ALIGN_SELF_AUTO && parent.alignItems == FlexContainerBlockBox.ALIGN_ITEMS_STRETCH)
                                || itemInLine.alignSelf == FlexItemBlockBox.ALIGN_SELF_STRETCH) {

                            //if item is set by height value, it does not stretch
                            if (!itemInLine.hasFixedHeight() && !itemInLine.isAlignSelfAuto() && itemInLine.alignSelf != FlexItemBlockBox.ALIGN_SELF_STRETCH) {

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
            if (!(parent.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP && parent.hasFixedHeight()))
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
        if(!item.isAlignSelfAuto())
            return false;

        if(item.alignSelf == CSSProperty.AlignSelf.INHERIT) {
            CSSProperty.AlignSelf parentAlignSelf =  parent.style.getProperty("align-self");
            if(parentAlignSelf != null && parentAlignSelf != CSSProperty.AlignSelf.AUTO && parentAlignSelf != CSSProperty.AlignSelf.INHERIT && parentAlignSelf != CSSProperty.AlignSelf.INITIAL){
                item.alignSelf = parentAlignSelf;
            }
        }

            if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_FLEX_START) {
                item.setPosition(x, y);

            } else if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_FLEX_END) {
                 item.setPosition(x, y + getHeight() - item.getHeight());
            } else if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_CENTER) {
                 item.setPosition(x, y + (getHeight() - item.getHeight()) / 2);
            } else if(item.alignSelf == FlexItemBlockBox.ALIGN_SELF_BASELINE) {
               alignBaseline(item, x);
            } else {
                //STRETCH
                alignStretch(item, x);
            }
            return true;
    }


    private void alignBaseline(FlexItemBlockBox item, int x){
        if (item.getPadding().top + item.getMargin().top + item.ctx.getBaselineOffset() > refItem.getPadding().top + refItem.getMargin().top + refItem.ctx.getBaselineOffset()) {
            refItem = item;
        }
        for (int j = 0; j < itemsInLine.size(); j++) {
            FlexItemBlockBox itemInLine = itemsInLine.get(j);
            if((!itemInLine.isAlignSelfAuto() && parent.alignItems == CSSProperty.AlignItems.BASELINE)  || itemInLine.alignSelf == CSSProperty.AlignSelf.BASELINE ) {
                if (itemInLine != refItem) {
                    itemInLine.setPosition(itemInLine.bounds.x, y + refItem.getPadding().top + refItem.getMargin().top+ refItem.ctx.getBaselineOffset()
                            - itemInLine.getPadding().top - itemInLine.getMargin().top - itemInLine.ctx.getBaselineOffset());
                    if (getHeight() < (refItem.getPadding().top + refItem.getMargin().top + refItem.ctx.getBaselineOffset()
                            - itemInLine.getPadding().top - itemInLine.getMargin().top  - itemInLine.ctx.getBaselineOffset() + itemInLine.bounds.height)) {
                        setHeight((refItem.getPadding().top + refItem.getMargin().top  + refItem.ctx.getBaselineOffset()- itemInLine.getPadding().top - itemInLine.getMargin().top - itemInLine.ctx.getBaselineOffset() + itemInLine.bounds.height));

                    }
                    parent.fixContainerHeight(itemsInLine.get(j));
                }

            item.setPosition(x, y + refItem.getPadding().top + refItem.getMargin().top+ refItem.ctx.getBaselineOffset() - item.getPadding().top - item.getMargin().top - item.ctx.getBaselineOffset());
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

    protected void applyAlignContent(FlexLineRow flexLine, ArrayList<FlexLineRow> lines, int countOfPreviousLines){
        if(parent.crossSize == 0 || parent.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP) //is container height set? is container nowrap?
            return;

        int remainingHeightSpace = parent.crossSize;
        for (int iline = 0; iline < lines.size(); iline++) {
            FlexLineRow line = lines.get(iline);
            if(line.savedHeight == -1)
                remainingHeightSpace -= line.getHeight();
            else
                remainingHeightSpace -= line.savedHeight;
        }
        if(parent.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_FLEX_START){
            return;
        } else if(parent.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_FLEX_END){
            flexLine.setY(flexLine.getY()+remainingHeightSpace);
        } else if(parent.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_CENTER){
            flexLine.setY(flexLine.getY()+remainingHeightSpace/2);
        } else if(parent.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_STRETCH){
            flexLine.savedHeight = flexLine.getHeight();
            flexLine.setHeight(flexLine.getHeight() + remainingHeightSpace/lines.size());

        } else if(parent.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_SPACE_BETWEEN){
            if(lines.size() != 1)
                flexLine.setY(flexLine.getY()+(countOfPreviousLines)*remainingHeightSpace/(lines.size()-1));
        } else if(parent.alignContent == FlexContainerBlockBox.ALIGN_CONTENT_SPACE_AROUND){
            if(countOfPreviousLines == 0)
                flexLine.setY(flexLine.getY()+ remainingHeightSpace/ 2 / lines.size());
            else
                flexLine.setY(flexLine.getY() + remainingHeightSpace/ 2 / lines.size() +(countOfPreviousLines)*remainingHeightSpace/ lines.size()+1);

        }
    }

}
