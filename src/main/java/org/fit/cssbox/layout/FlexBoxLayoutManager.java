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

        System.out.println("je row: " + container.isDirectionRow);
        System.out.println("je reversed: " + container.isDirectionReversed);
        container.setMainSpace(); //content width nebo height
        container.setCrossSpace();
        System.out.println("kontejner content (mainSpace, crossSpace) nastaven na: ("+ container.mainSpace + ", " + container.crossSpace + ")\n");

        //urceni flex-basis (zakladni velikosti flexu) polozkam
        System.out.println("--------------\nCHILDS: \n");


        ArrayList<FlexItemBlockBox> ItemList = new ArrayList<>();

        for (int i = 0; i < container.getSubBoxNumber(); i++) {
            FlexItemBlockBox Item = (FlexItemBlockBox) container.getSubBox(i);
//            System.out.println(Item.toString());
            Item.disableFloats();
            ItemList.add(Item);
        }
        Collections.sort(ItemList);

        container.layoutItems(ItemList, container);

        return true;
    }
}
