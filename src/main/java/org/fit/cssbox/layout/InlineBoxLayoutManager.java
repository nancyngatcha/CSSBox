package org.fit.cssbox.layout;

/**
 * Layout manager for layout of InlineBoxes.
 *
 * @author Ondry, Ondra
 */
public class InlineBoxLayoutManager implements ILayoutManager {


    /** inline owner using this layout manager*/
    private InlineBox owner;

    /**
     * Creates an instance of this layout manager.
     * @param owner owner of this manager
     */
    public InlineBoxLayoutManager(InlineBox owner) {
        this.owner = owner;
    }

    @Override
    public boolean doLayout(int availw, boolean force, boolean linestart) {
        if (!owner.isDisplayed())
        {
            owner.getContent().setSize(0, 0);
            owner.getBounds().setSize(0, 0);
            return true;
        }

        owner.setAvailableWidth(availw);

        owner.setCurline(new LineBox(owner, owner.getStartChild(), 0));
        int wlimit = owner.getAvailableContentWidth();
        int x = 0; //current x
        boolean ret = true;
        owner.rest = null;

        int lastbreak = owner.getStartChild(); //last possible position of a line break
        owner.setCollapsedCompletely(true);

        for (int i = owner.getStartChild(); i < owner.getEndChild(); i++)
        {
            Box subbox = owner.getSubBox(i);
            if (subbox.canSplitBefore())
                lastbreak = i;
            //when forcing, force the first child only and the children before
            //the first possible break
            boolean f = force && (i == owner.getStartChild() || lastbreak ==  owner.getStartChild());
            boolean fit = subbox.doLayout(wlimit - x, f, linestart && (i ==  owner.getStartChild()));
            if (fit) //something has been placed
            {
                if (subbox instanceof Inline)
                {
                    subbox.setPosition(x,  0); //the y position will be updated later
                    x += subbox.getWidth();
                    owner.getCurline().considerBox((Inline) subbox);
                    if (((Inline) subbox).finishedByLineBreak())
                        owner.setLineBreakStop(true);
                    if (!((Inline) subbox).collapsedCompletely())
                        owner.setCollapsedCompletely(false);
                }
                else
                    InlineBox.getLog().debug("Warning: doLayout(): subbox is not inline: " + subbox);
                if (subbox.getRest() != null) //is there anything remaining?
                {
                    InlineBox rbox = owner.copyBox();
                    rbox.splitted = true;
                    rbox.splitid = owner.getSplitId() + 1;
                    rbox.setStartChild(i); //next starts with me...
                    rbox.nested.setElementAt(subbox.getRest(), i); //..but only with the rest
                    rbox.adoptChildren();
                    owner.setEndChild(i+1); //...and this owner stops with this element
                    owner.rest = rbox;
                    break;
                }
                else if (owner.isLineBreakStop()) //nothing remained but there was a line break
                {
                    if (i + 1 < owner.getEndChild()) //some children remaining
                    {
                        InlineBox rbox = owner.copyBox();
                        rbox.splitted = true;
                        rbox.splitid = owner.getSplitId() + 1;
                        rbox.setStartChild(i + 1); //next starts with the next one
                        rbox.adoptChildren();
                        owner.setEndChild(i+1); //...and this owner stops with this element
                        owner.rest = rbox;
                    }
                    break;
                }
            }
            else //nothing from the child has been placed
            {
                if (lastbreak == owner.getStartChild()) //no children have been placed, give up
                {
                    ret = false;
                    break;
                }
                else //some children have been placed, contintue the next time
                {
                    InlineBox rbox = owner.copyBox();
                    rbox.splitted = true;
                    rbox.splitid = owner.getSplitId() + 1;
                    rbox.setStartChild(lastbreak); //next time start from the last break
                    rbox.adoptChildren();
                    owner.setEndChild(lastbreak); //this owner stops here
                    owner.rest = rbox;
                    break;
                }
            }

            if (subbox.canSplitAfter())
                lastbreak = i+1;
        }

        //compute the vertical positions of the boxes
        //updateLineMetrics();
        owner.getContent().width = x;
        owner.getContent().height = owner.ctx.getFontHeight();
        owner.setHalflead((owner.content.height - owner.ctx.getFontHeight()) / 2);
        owner.alignBoxes();
        owner.setSize(owner.totalWidth(), owner.totalHeight());

        return ret;
    }
}
