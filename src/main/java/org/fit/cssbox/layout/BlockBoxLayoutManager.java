package org.fit.cssbox.layout;

public class BlockBoxLayoutManager extends LayoutManager {
    private BlockBox box;

    public BlockBoxLayoutManager(BlockBox box) {
        this.box = box;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {

        if (!box.isDisplayed())
        {
            box.getContent().setSize(0, 0);
            box.getBounds().setSize(0, 0);
            return true;
        }

        //remove previously splitted children from possible previous layout
        box.clearSplitted();

        //shrink-to-fit when the width is not given by containing box or specified explicitly
        if (!box.hasFixedWidth())
        {
            int min = Math.max(box.getMinimalContentWidthLimit(), box.getMinimalContentWidth());
            int max = box.getMaximalContentWidth();
            LengthSet emargin = box.getEMargin();
            LengthSet padding = box.getPadding();
            LengthSet border = box.getBorder();
            int availcont = availw - emargin.left - border.left - padding.left - emargin.right - border.right - padding.right;

            int pref = Math.min(Math.max(min, availcont), max);
            box.setContentWidth(pref);
            box.updateChildSizes();
        }

        //the width should be fixed from this point
        box.widthComputed = true;

        /* Always try to use the full width. If the box is not in flow, its width
         * is updated after the layout */
        box.setAvailableWidth(box.totalWidth());

        if (!box.contblock)  //block elements containing inline elements only
            box.layoutInline();
        else //block elements containing block elements
            box.layoutBlocks();

        return true;
    }
}
