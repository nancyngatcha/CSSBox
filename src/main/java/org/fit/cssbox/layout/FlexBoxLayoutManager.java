package org.fit.cssbox.layout;

public class FlexBoxLayoutManager implements ILayoutManager {

    private BlockBox owner;

    public FlexBoxLayoutManager(BlockBox owner) {
        this.owner = owner;
    }


    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("It's in! ");

        return true;
    }
}
