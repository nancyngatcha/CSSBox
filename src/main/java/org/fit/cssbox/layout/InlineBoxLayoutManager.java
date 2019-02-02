package org.fit.cssbox.layout;

public class InlineBoxLayoutManager extends LayoutManager {

    private InlineBox ibox;

    public InlineBoxLayoutManager(InlineBox ibox) {
        this.ibox = ibox;
    }

    /**
     * Compute the width and height of this element. Layout the sub-elements.
     *
     * @param availw    Maximal width available to the child elements
     * @param force     Use the area even if the used width is greater than maxwidth
     * @param linestart Indicates whether the element is placed at the line start
     * @return True if the box has been succesfully placed
     */
    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        //if (getElement() != null && getElement().getAttribute("id").equals("mojo"))
        //  System.out.println("jo!");
        //Skip if not displayed
        if (!ibox.displayed) {
            ibox.getContent().setSize(0, 0);
            ibox.getBounds().setSize(0, 0);
            return true;
        }

        ibox.setAvailableWidth(availw);

        ibox.setCurline(new LineBox(ibox, ibox.getStartChild(), 0));
        int wlimit = ibox.getAvailableContentWidth();
        int x = 0; //current x
        boolean ret = true;
        ibox.rest = null;

        int lastbreak = ibox.getStartChild(); //last possible position of a line break
        ibox.setCollapsedCompletely(true);

        for (int i = ibox.getStartChild(); i < ibox.getEndChild(); i++) {

            Box subbox = ibox.getSubBox(i);

            if (subbox.canSplitBefore())
                lastbreak = i;
            //when forcing, force the first child only and the children before
            //the first possible break
            boolean f = force && (i == ibox.getStartChild() || lastbreak == ibox.getStartChild());
            boolean fit = subbox.doLayout(wlimit - x, f, linestart && (i == ibox.getStartChild()));

            if (fit) { //something has been placed
                if (subbox instanceof Inline) {
                    subbox.setPosition(x, 0); //the y position will be updated later
                    x += subbox.getWidth();
                    ibox.getCurline().considerBox((Inline) subbox);

                    if (((Inline) subbox).finishedByLineBreak())
                        ibox.setLineBreakStop(true);
                    if (!((Inline) subbox).collapsedCompletely())
                        ibox.setCollapsedCompletely(false);
                } else
                    InlineBox.getLog().debug("Warning: doLayout(): subbox is not inline: " + subbox);
                if (subbox.getRest() != null) { //is there anything remaining?

                    InlineBox rbox = ibox.copyBox();
                    rbox.splitted = true;
                    rbox.splitid = ibox.getSplitId() + 1;
                    rbox.setStartChild(i); //next starts with me...
                    rbox.nested.setElementAt(subbox.getRest(), i); //..but only with the rest
                    rbox.adoptChildren();
                    ibox.setEndChild(i + 1); //...and this box stops with this element
                    ibox.rest = rbox;
                    break;
                } else if (ibox.isLineBreakStop()) { //nothing remained but there was a line break

                    if (i + 1 < ibox.getEndChild()) { //some children remaining
                        InlineBox rbox = ibox.copyBox();
                        rbox.splitted = true;
                        rbox.splitid = ibox.getSplitId() + 1;
                        rbox.setStartChild(i + 1); //next starts with the next one
                        rbox.adoptChildren();
                        ibox.setEndChild(i + 1); //...and this box stops with this element
                        ibox.rest = rbox;
                    }
                    break;
                }
            } else {//nothing from the child has been placed
                if (lastbreak == ibox.getStartChild()) {//no children have been placed, give up
                    ret = false;
                    break;
                } else {//some children have been placed, contintue the next time
                    InlineBox rbox = ibox.copyBox();
                    rbox.splitted = true;
                    rbox.splitid = ibox.getSplitId() + 1;
                    rbox.setStartChild(lastbreak); //next time start from the last break
                    rbox.adoptChildren();
                    ibox.setEndChild(lastbreak); //this box stops here
                    ibox.rest = rbox;
                    break;
                }
            }
            if (subbox.canSplitAfter())
                lastbreak = i + 1;
        }

        //compute the vertical positions of the boxes
        //updateLineMetrics();
        ibox.getContent().width = x;
        ibox.getContent().height = ibox.ctx.getFontHeight();
        ibox.setHalflead((ibox.getContent().height - ibox.ctx.getFontHeight()) / 2);
        ibox.alignBoxes();
        ibox.setSize(ibox.totalWidth(), ibox.totalHeight());

        return ret;
    }
}
