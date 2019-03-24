package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSProperty;

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
        System.out.println("kontejner nastaven na: ("+ container.mainSpace + ", " + container.crossSpace + ")\n");

        //urceni flex-basis (zakladni velikosti flexu) polozkam
        int contw = container.content.width;
        CSSDecoder dec = new CSSDecoder(container.ctx);

        System.out.println("--------------\nCHILDS: \n");

        int max_y = 0;

        for (int i = 0; i < container.getSubBoxNumber(); i++) {
            FlexItemBlockBox Item = (FlexItemBlockBox) container.getSubBox(i);
            System.out.println(Item.toString());

            Item.disableFloats();

            Item.flexBasisValue = Item.setFlexBasisValue(dec, contw, container);

            //TODO: last of 3)
            Item.hypoteticalMainSize = Item.boundFlexBasisByMinAndMaxWidth(Item.flexBasisValue);
            if(container.isDirectionRow()) {
                Item.bounds.width = Item.hypoteticalMainSize + Item.margin.left + Item.margin.right + Item.border.left + Item.border.right;
                Item.content.width = Item.hypoteticalMainSize;
            } else {
                Item.bounds.height = Item.hypoteticalMainSize + Item.margin.top + Item.margin.bottom + Item.border.top + Item.border.bottom;
                Item.content.height = Item.hypoteticalMainSize;
            }

            //jde to za sebou, ale stejně to budu muset narvat do řádků nebo sloupců
        if(i > 0) {
            int x_pozice_predchozi = container.getSubBox(i - 1).getBounds().x;
            int y_pozice_predchozi = container.getSubBox(i -1 ).getBounds().y;
            if (( x_pozice_predchozi + container.getSubBox(i - 1).totalWidth() + Item.totalWidth()) > container.mainSpace) {
                // nevejde se na řádek, dej tento box na začátek dalšího řádku
                System.out.println("nevejde se na radek");
                int prirustek_y = container.getSubBox(i - 1).totalHeight();
                System.out.println("x pozice predchoziho: " + x_pozice_predchozi);
                System.out.println("y_pozice_predchozi: " + y_pozice_predchozi);
                System.out.println("prirustek y: " + prirustek_y);
                Item.setPosition(container.bounds.x, y_pozice_predchozi + prirustek_y);
            } else {
                //ještě se vejde na tento řádek
                System.out.println("vejde se na radek");
                Item.setPosition(x_pozice_predchozi + container.getSubBox(i - 1).totalWidth(), y_pozice_predchozi);
            }


        }
            System.out.println("vyska: " + Item.bounds.height);
            System.out.println("sirka: " + Item.bounds.width);




//            System.out.println("\nflexBasisValue(unbounded): " + subbox.flexBasisValue);
//            System.out.println("hypoteticalMainSize(bounded): " + subbox.hypoteticalMainSize);
//            System.out.println("width: " + subbox.getWidth());
//            System.out.println("content: " + subbox.content.width);
//
//            System.out.println("flexGrowValue: " + subbox.flexGrowValue);
//            System.out.println("flexShrinkValue: " + subbox.flexShrinkValue);
//            System.out.println("ORDER: " + subbox.flexOrderValue);
//
//            System.out.println("Containing block: " + subbox.getContainingBlockBox());
            System.out.println("----------------------------------");

            if(Item.bounds.y + Item.totalHeight() > max_y) {
                max_y = Item.bounds.y + Item.totalHeight();
                container.setContentHeight(max_y);
            }
        }



           //celkova sirka a vyska

//        System.out.println("Hlavní šířka pro content: " + container.getMainSpace());
//        System.out.println("Cross šířka pro content: " + container.getCrossSpace());


        return true;
    }
}
