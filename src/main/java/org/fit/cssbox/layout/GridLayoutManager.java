package org.fit.cssbox.layout;

public class GridLayoutManager implements LayoutManager {

    private GridWrapperBlockBox gridbox;

    public GridLayoutManager(GridWrapperBlockBox gridbox) {
        this.gridbox = gridbox;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("DoLayout -> GridLayoutManager");

        for (int i = 0; i < gridbox.endChild; i++)
        System.out.println("pocet deti: " + gridbox.getSubBox(i).toString());
        System.out.println("vyska " + gridbox.totalHeight());
        System.out.println("sirka" + gridbox.totalWidth());
        return true;
    }
}
