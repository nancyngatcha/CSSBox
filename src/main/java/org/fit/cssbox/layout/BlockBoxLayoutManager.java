package org.fit.cssbox.layout;

public class BlockBoxLayoutManager implements ILayoutManager {
    private BlockBox owner;

    public BlockBoxLayoutManager(BlockBox owner) {
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

        //remove previously splitted children from possible previous layout
        owner.clearSplitted();

        //shrink-to-fit when the width is not given by containing owner or specified explicitly
        if (!owner.hasFixedWidth())
        {
            int min = Math.max(owner.getMinimalContentWidthLimit(), owner.getMinimalContentWidth());
            int max = owner.getMaximalContentWidth();
            LengthSet emargin = owner.getEMargin();
            LengthSet padding = owner.getPadding();
            LengthSet border = owner.getBorder();
            int availcont = availw - emargin.left - border.left - padding.left - emargin.right - border.right - padding.right;
            int pref = Math.min(Math.max(min, availcont), max);
            owner.setContentWidth(pref);
            owner.updateChildSizes();
        }

        //the width should be fixed from this point

        /* Always try to use the full width. If the owner is not in flow, its width
         * is updated after the layout */
        if(owner.parent instanceof FlexItemBlockBox){
            FlexItemBlockBox item = (FlexItemBlockBox) owner.parent;
            FlexContainerBlockBox parent = (FlexContainerBlockBox) item.getContainingBlockBox();
            if(parent.isRowContainer()) {
                owner.setAvailableWidth(item.hypotheticalMainSize);
            } else {
                owner.setAvailableWidth(item.content.width);
            }
        } else {
            owner.widthComputed = true;
            owner.setAvailableWidth(owner.totalWidth());
        }

        if (!owner.contblock)  //block elements containing inline elements only
            owner.layoutInline();
        else //block elements containing block elements
            owner.layoutBlocks();

        return true;
    }


}
