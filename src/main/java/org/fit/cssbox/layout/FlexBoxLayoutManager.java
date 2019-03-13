package org.fit.cssbox.layout;

public class FlexBoxLayoutManager implements ILayoutManager {

    private FlexContainerBlockBox owner;

    public FlexBoxLayoutManager(FlexContainerBlockBox owner) {
        this.owner = owner;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("In doLayout of FlexManager. In container are these items:");

        for (int i = 0; i < owner.getSubBoxNumber(); i++)
            System.out.println(owner.getSubBox(i));

        System.out.println("Ok, now layout and draw them.\n*********");

        System.out.println("Šířka kontejneru: " + owner.totalWidth());
        System.out.println("Výška kontejneru: " + owner.totalHeight());

//start... finally
        if (!owner.isDisplayed())
        {
            owner.getContent().setSize(0, 0);
            owner.getBounds().setSize(0, 0);
            return true;
        }



        return true;
    }
}
