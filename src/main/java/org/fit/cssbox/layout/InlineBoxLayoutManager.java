package org.fit.cssbox.layout;

public class InlineBoxLayoutManager implements ILayoutManager {
    private InlineBox box;

    public InlineBoxLayoutManager(InlineBox box) {
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

        box.setAvailableWidth(availw);

        box.setCurline(new LineBox(box, box.getStartChild(), 0));
        int wlimit = box.getAvailableContentWidth();
        int x = 0; //current x
        boolean ret = true;
        box.rest = null;

        int lastbreak = box.getStartChild(); //last possible position of a line break
        box.setCollapsedCompletely(true);

        for (int i = box.getStartChild(); i < box.getEndChild(); i++)
        {
            Box subbox = box.getSubBox(i);
            if (subbox.canSplitBefore())
                lastbreak = i;
            //when forcing, force the first child only and the children before
            //the first possible break
            boolean f = force && (i == box.getStartChild() || lastbreak ==  box.getStartChild());
            boolean fit = subbox.doLayout(wlimit - x, f, linestart && (i ==  box.getStartChild()));
            if (fit) //something has been placed
            {
                if (subbox instanceof Inline)
                {
                    subbox.setPosition(x,  0); //the y position will be updated later
                    x += subbox.getWidth();
                    box.getCurline().considerBox((Inline) subbox);
                    if (((Inline) subbox).finishedByLineBreak())
                        box.setLineBreakStop(true);
                    if (!((Inline) subbox).collapsedCompletely())
                        box.setCollapsedCompletely(false);
                }
                else
                    InlineBox.getLog().debug("Warning: doLayout(): subbox is not inline: " + subbox);
                if (subbox.getRest() != null) //is there anything remaining?
                {
                    InlineBox rbox = box.copyBox();
                    rbox.splitted = true;
                    rbox.splitid = box.getSplitId() + 1;
                    rbox.setStartChild(i); //next starts with me...
                    rbox.nested.setElementAt(subbox.getRest(), i); //..but only with the rest
                    rbox.adoptChildren();
                    box.setEndChild(i+1); //...and this box stops with this element
                    box.rest = rbox;
                    break;
                }
                else if (box.isLineBreakStop()) //nothing remained but there was a line break
                {
                    if (i + 1 < box.getEndChild()) //some children remaining
                    {
                        InlineBox rbox = box.copyBox();
                        rbox.splitted = true;
                        rbox.splitid = box.getSplitId() + 1;
                        rbox.setStartChild(i + 1); //next starts with the next one
                        rbox.adoptChildren();
                        box.setEndChild(i+1); //...and this box stops with this element
                        box.rest = rbox;
                    }
                    break;
                }
            }
            else //nothing from the child has been placed
            {
                if (lastbreak == box.getStartChild()) //no children have been placed, give up
                {
                    ret = false;
                    break;
                }
                else //some children have been placed, contintue the next time
                {
                    InlineBox rbox = box.copyBox();
                    rbox.splitted = true;
                    rbox.splitid = box.getSplitId() + 1;
                    rbox.setStartChild(lastbreak); //next time start from the last break
                    rbox.adoptChildren();
                    box.setEndChild(lastbreak); //this box stops here
                    box.rest = rbox;
                    break;
                }
            }

            if (subbox.canSplitAfter())
                lastbreak = i+1;
        }

        //compute the vertical positions of the boxes
        //updateLineMetrics();
        box.getContent().width = x;
        box.getContent().height = box.ctx.getFontHeight();
        box.setHalflead((box.content.height - box.ctx.getFontHeight()) / 2);
        box.alignBoxes();
        box.setSize(box.totalWidth(), box.totalHeight());

        return ret;
    }
}
