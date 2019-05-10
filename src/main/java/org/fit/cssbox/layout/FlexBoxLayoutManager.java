package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

import java.util.ArrayList;

/**
 * Layout manager for Flexbox layout.
 *
 * @author Ondry, Ondra
 */
public class FlexBoxLayoutManager implements ILayoutManager {

    /** flex container using this layout manager */
    private FlexContainerBlockBox container;

    /**
     * Creates an instance of this layout manager.
     * @param container flex container, owner of this manager
     */
    public FlexBoxLayoutManager(FlexContainerBlockBox container) {
        this.container = container;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        if (!container.isDisplayed())
        {
            container.getContent().setSize(0, 0);
            container.getBounds().setSize(0, 0);
            return true;
        }

        //set container main and cross size
        container.setInceptiveMainSize();
        container.setInceptiveCrossSize();

        if(container.isRowContainer())
            container.layoutItemsInRow(getItemList());
        else
            container.layoutItemsInColumn(getItemList());

        //setting content and bounds height and width
        if(container.isRowContainer()) {
            container.content.width = container.mainSize;
            container.bounds.width = container.mainSize +
                    container.margin.left + container.margin.right +
                    container.padding.left + container.padding.right +
                    container.border.left + container.border.right;
            container.content.height = container.crossSize;
            container.bounds.height = container.crossSize +
                    container.margin.top + container.margin.bottom +
                    container.padding.top + container.padding.bottom +
                    container.border.top + container.border.bottom;
        } else {
            container.content.width = container.crossSize;
            container.bounds.width = container.crossSize +
                    container.margin.left  + container.margin.right+
                    container.padding.left + container.padding.right +
                    container.border.left + container.border.right;
            container.content.height = container.mainSize;
            container.bounds.height = container.mainSize +
                    container.margin.top + container.margin.bottom +
                    container.padding.top + container.padding.bottom +
                    container.border.top + container.border.bottom;
        }
        return true;
    }

    /**
     * Creates arraylist of flex items, which are subboxes of container.
     * Also disables floats and clearing of items, they should not have effect.
     * @return ArrayList of items
     */
    private ArrayList<FlexItemBlockBox> getItemList() {
        ArrayList<FlexItemBlockBox> ItemList = new ArrayList<>();

        for (int i = 0; i < container.getSubBoxNumber(); i++) {
            FlexItemBlockBox Item  = (FlexItemBlockBox) container.getSubBox(i);
            Item.disableFloats();
            Item.clearing = CSSProperty.Clear.NONE;
            ItemList.add(Item);
        }
        return ItemList;
    }
}
