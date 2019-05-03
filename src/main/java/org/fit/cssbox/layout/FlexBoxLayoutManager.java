package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

import java.util.ArrayList;

public class FlexBoxLayoutManager implements ILayoutManager {

    private FlexContainerBlockBox container;

    public FlexBoxLayoutManager(FlexContainerBlockBox container) {
        this.container = container;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("IN FLEX LAYOUT");
//start... finally
        if (!container.isDisplayed())
        {
            container.getContent().setSize(0, 0);
            container.getBounds().setSize(0, 0);
            return true;
        }

        container.setInceptiveMainSize(); //content width nebo height
        container.setInceptiveCrossSize();
        System.out.println("kontejner content (mainSize, height) nastaven na: ("+ container.mainSize + ", " + container.crossSize + ")\n");


        if(container.isRowContainer())
            container.layoutItemsInRow(getItemList());
        else
            container.layoutItemsInColumn(getItemList());
        //set of cont height (without this, would be height of containing block of container zero)

        if(container.isRowContainer()) {
            container.bounds.width = container.mainSize +
                    container.margin.left + container.margin.right +
                    container.padding.left + container.padding.right +
                    container.border.left + container.border.right;
            container.bounds.height += container.crossSize +
                    container.margin.top + container.margin.bottom +
                    container.padding.top + container.padding.bottom +
                    container.border.top + container.border.bottom;
        } else {
            container.bounds.width = container.crossSize +
                    container.margin.left  + container.margin.right+
                    container.padding.left + container.padding.right +
                    container.border.left + container.border.right;
            container.bounds.height += container.mainSize +
                    container.margin.top + container.margin.bottom +
                    container.padding.top + container.padding.bottom +
                    container.border.top + container.border.bottom;
        }

        return true;
    }

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
