package org.fit.cssbox.layout;

import java.util.ArrayList;

abstract public class FlexLine {

    /**
     * The FlexContainerBlockBox containing the lines
     */
    protected FlexContainerBlockBox owner;

    /**
     * Total width in pixels (for horizontal alignment)
     */
    protected int width;

    protected int height;

    protected int y;

    protected boolean isFirstItem;

    protected ArrayList<FlexItemBlockBox> itemsInLine;

    protected int remainingMainSpace;
    protected FlexItemBlockBox refItem;


    public FlexContainerBlockBox getOwner() {
        return owner;
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

    protected abstract void applyAlignContent(ArrayList<FlexLine> lines, int i);

    protected abstract void setYPositionToItem(FlexItemBlockBox item);

    protected abstract void setPositionAndAdaptHeight(FlexItemBlockBox item);

    protected abstract void setLineCrossSize();

    protected abstract void applyAlignItemsAndSelf();


    public int getRemainingMainSpace() {
        return remainingMainSpace;
    }

    public void setRemainingMainSpace(int remainingMainSpace) {
        this.remainingMainSpace = remainingMainSpace;
        if(this.remainingMainSpace < 0)
            this.remainingMainSpace = 0;
    }

    protected boolean registerItem(FlexItemBlockBox item) {
        int totalMainSizeOfItem;
        if(owner.isRowContainer())
            totalMainSizeOfItem = item.hypotheticalMainSize + item.margin.left + item.margin.right + item.padding.left + item.padding.right + item.border.left + item.border.right;
        else
            totalMainSizeOfItem = item.content.height + item.margin.top + item.margin.bottom + item.padding.top + item.padding.bottom + item.border.top + item.border.bottom;
        if (isFirstItem) {
            setRemainingMainSpace(remainingMainSpace - totalMainSizeOfItem);
            itemsInLine.add(item);
            refItem = item;
            isFirstItem = false;
            return true;
        }

        //not first item in row, does it fit into line?
        if((totalMainSizeOfItem > remainingMainSpace) &&
            owner.flexWrap != FlexContainerBlockBox.FLEX_WRAP_NOWRAP &&
            !owner.mainSizeSetByCont)
            return false;

        setRemainingMainSpace(remainingMainSpace - totalMainSizeOfItem);

        itemsInLine.add(item);
        return true;
    }
}
