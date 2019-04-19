package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

import java.util.ArrayList;
import java.util.Collections;

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

        container.setMainSpace(); //content width nebo height
        container.setCrossSpace();
        System.out.println("kontejner content (mainSpace, crossSpace) nastaven na: ("+ container.mainSpace + ", " + container.crossSpace + ")\n");

        container.content.height = container.crossSpace;

        ArrayList<FlexItemBlockBox> ItemList = new ArrayList<>();

        for (int i = 0; i < container.getSubBoxNumber(); i++) {
            FlexItemBlockBox Item = (FlexItemBlockBox) container.getSubBox(i);
            Item.disableFloats();
            Item.clearing = CSSProperty.Clear.NONE;
            ItemList.add(Item);
        }
        //sort list according to flex property order
        Collections.sort(ItemList);

        container.layoutItems(ItemList);


        //set of cont height (without this, would be height of containing block of container zero)
        if(container.isDirectionRow()) {
            container.bounds.height += container.crossSpace +
                    container.margin.top + container.margin.bottom +
                    container.padding.top + container.padding.bottom +
                    container.border.top + container.border.bottom;
        }
        return true;
    }
}
