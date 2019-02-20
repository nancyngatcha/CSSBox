package org.fit.cssbox.layout;

public class BlockBoxLayoutManager implements LayoutManager {

    private BlockBox bbox;

    public BlockBoxLayoutManager(BlockBox bbox) {
        this.bbox = bbox;
    }

    /**
     * Layout the sub-elements.
     *
     * @param availw    Maximal width available to the child elements
     * @param force     Use the area even if the used width is greater than maxwidth
     * @param linestart Indicates whether the element is placed at the line start
     * @return <code>true</code> if the box has been succesfully placed
     */
    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        //if (getElement() != null && getElement().getAttribute("id").equals("gbzc"))
        //	System.out.println("jo!");
        //Skip if not displayed
        if (!bbox.displayed) {
            bbox.getContent().setSize(0, 0);
            bbox.getBounds().setSize(0, 0);
            return true;
        }

        //remove previously splitted children from possible previous layout
        bbox.clearSplitted();

        //shrink-to-fit when the width is not given by containing box or specified explicitly
        if (!bbox.hasFixedWidth()) {
            //int min = getMinimalContentWidthLimit();
            int min = Math.max(bbox.getMinimalContentWidthLimit(), bbox.getMinimalContentWidth());
            int max = bbox.getMaximalContentWidth();
            int availcont = availw - bbox.getMargin().left - bbox.getBorder().left - bbox.getPadding().left - bbox.getMargin().right - bbox.getBorder().right - bbox.getPadding().right;
            //int pref = Math.min(max, availcont);
            //if (pref < min) pref = min;
            int pref = Math.min(Math.max(min, availcont), max);
            bbox.setContentWidth(pref);
            bbox.updateChildSizes();
        }

        //the width should be fixed from this point
        bbox.widthComputed = true;

        /* Always try to use the full width. If the box is not in flow, its width
         * is updated after the layout */
        bbox.setAvailableWidth(bbox.totalWidth());

        if (!bbox.contblock)  //block elements containing inline elements only
            bbox.layoutInline();
        else //block elements containing block elements
            bbox.layoutBlocks();

        //allways fits as well possible
        return true;
    }
}
