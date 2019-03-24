package org.fit.cssbox.layout;

public class GridLayoutManager implements LayoutManager {

    private GridWrapperBlockBox gridbox;

    public GridLayoutManager(GridWrapperBlockBox gridbox) {
        this.gridbox = gridbox;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        System.out.println("DoLayout -> GridLayoutManager");
        int oneFrUnitColumn;
        int oneFrUnitRow;
        CSSDecoder dec = new CSSDecoder(gridbox.ctx);


        for (int i = 0; i < gridbox.endChild; i++) {
            System.out.println("pocet deti: " + gridbox.getSubBox(i).toString());
        }
        System.out.println("vyska content " + gridbox.content.getHeight());
        System.out.println("sirka content " + gridbox.content.getWidth());

        int contw = gridbox.getContainingBlock().width;

        gridbox.GridGapProcessing(dec);


        if (gridbox.findUnitsForFr(gridbox.gridTemplateColumnsValues, dec, gridbox.gapColumn)) {
            oneFrUnitColumn = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.content.width);
            gridbox.flexFactorSum = 0;
            gridbox.sumofpixels = 0;
            System.out.println("1fr pro column je: " + oneFrUnitColumn + "px");
        }


        if (gridbox.findUnitsForFr(gridbox.gridTemplateRowsValues, dec, gridbox.gapRow)) {
            oneFrUnitRow = gridbox.computingFrUnits(gridbox.flexFactorSum, gridbox.sumofpixels, gridbox.content.height);
            gridbox.flexFactorSum = 0;
            gridbox.sumofpixels = 0;
            System.out.println("1fr pro row je: " + oneFrUnitRow + "px");
        }


//        for (int i = 0; i < gridbox.getSubBoxNumber(); i++) {
//            GridItem subbox = (GridItem) gridbox.getSubBox(i);
//            System.out.println("czzz: " + subbox.gridStartEnd);
//            if (subbox.gridStartEnd  == CSSProperty.GridStartEnd.valueOf("number")) {
//                subbox.gridStartEndValue = dec.getLength(subbox.getLengthValue("grid-column-start"), false, 0, 0, contw);
//                System.out.println("Sloupec: " +subbox.gridStartEndValue);
//               // System.out.println("JSEM V IF");
//            } else
//                System.out.println("fdsfdsfds");
////            System.out.println(subbox.getSubBox(i).toString());
//        }
        return true;
    }
}
