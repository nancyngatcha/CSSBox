package org.fit.cssbox.layout;

import java.util.ArrayList;

public class FlexLineColumn extends FlexLine{

    protected int savedHeight;

    public FlexLineColumn(FlexContainerBlockBox owner) {
        this.owner = owner;
        y = 0;
        height = owner.getContentHeight();
        itemsInLine = new ArrayList<>();
        isFirstItem = true;
        width = 0;
        remainingMainSpace = height;
        savedHeight = -1;
    }


    @Override
    protected void applyAlignContent(ArrayList<FlexLine> lines, int i) {

    }

    @Override
    protected void setYPositionToItem(FlexItemBlockBox item) {

    }

    @Override
    protected void setPositionAndAdaptHeight(FlexItemBlockBox item) {

    }

    @Override
    protected void setLineCrossSize() {

        if (owner.flexWrap == FlexContainerBlockBox.FLEX_WRAP_NOWRAP) {
            setWidth(owner.crossSize);
            return;
        }
        if (itemsInLine.size() == 1)
            setWidth(itemsInLine.get(0).bounds.width);
        else {
            int width = 0;
            for (FlexItemBlockBox item : itemsInLine) {
                if (width< item.bounds.width)
                    width = item.bounds.width;
            }
            setWidth(width);

        }
    }

    @Override
    protected void applyAlignItemsAndSelf() {

    }

}
