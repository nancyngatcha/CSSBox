package org.fit.cssbox.layout;

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

    private int remainingSpace;

    protected ArrayList<FlexItemBlockBox> itemsInLine;

    public FlexLine(FlexContainerBlockBox parent, int y) {
        this.parent = parent;
        this.y = y;
        height = 0;
        itemsInLine = new ArrayList<>();
        isFirstItem = true;
        width = parent.getContentWidth();
        remainingSpace = width;
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

    public int getRemainingSpace() {
        return remainingSpace;
    }

    public void setRemainingSpace(int remainingSpace) {
        this.remainingSpace = remainingSpace;
    }

    protected boolean registerItem(FlexItemBlockBox item) {
        if (isFirstItem) {
            if (getHeight() < item.getHeight())
                setHeight(item.getHeight());
            setRemainingSpace(remainingSpace - item.getWidth());
            itemsInLine.add(item);

            item.setPosition(0, y);

            isFirstItem = false;

            return true;
        }

        //not first item in row, does it fit into line?
        int sumOfItemsWidths = 0;
        for(int i = 0; i < itemsInLine.size(); i++)
            sumOfItemsWidths += itemsInLine.get(i).getWidth();

        if(item.totalWidth() > remainingSpace)
            return false;

        //vejde se na radek

        //je treba zmenit vysku radku?
        if (getHeight() < item.getHeight())
            setHeight(item.getHeight());
        setRemainingSpace(remainingSpace - item.getWidth());

        itemsInLine.add(item);
        item.setPosition(sumOfItemsWidths, y);
        return true;


    }


}
