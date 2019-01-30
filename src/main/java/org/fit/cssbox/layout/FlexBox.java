package org.fit.cssbox.layout;

import org.w3c.dom.Element;

import java.awt.*;

public class FlexBox extends ElementBox {
    /**
     * Creates a new element box from a DOM element
     *
     * @param n   the DOM element
     * @param g   current graphics context
     * @param ctx current visual context
     */
    public FlexBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
    }

    @Override
    public ElementBox copyBox() {
        return null;
    }

    @Override
    public boolean mayContainBlocks() {
        return false;
    }

    @Override
    protected void loadSizes() {

    }

    @Override
    public void updateSizes() {

    }

    @Override
    public void computeEfficientMargins() {

    }

    @Override
    public boolean marginsAdjoin() {
        return false;
    }

    @Override
    public boolean canSplitInside() {
        return false;
    }

    @Override
    public boolean canSplitBefore() {
        return false;
    }

    @Override
    public boolean canSplitAfter() {
        return false;
    }

    @Override
    public boolean startsWithWhitespace() {
        return false;
    }

    @Override
    public boolean endsWithWhitespace() {
        return false;
    }

    @Override
    public void setIgnoreInitialWhitespace(boolean b) {

    }

    @Override
    public int totalHeight() {
        return 0;
    }

    @Override
    public int getMinimalWidth() {
        return 0;
    }

    @Override
    public int getMaximalWidth() {
        return 0;
    }

    @Override
    public boolean isInFlow() {
        return false;
    }

    @Override
    public boolean hasFixedWidth() {
        return false;
    }

    @Override
    public boolean hasFixedHeight() {
        return false;
    }

    @Override
    public boolean containsFlow() {
        return false;
    }

    @Override
    public void absolutePositions() {

    }

    @Override
    public void draw(DrawStage turn) {

    }
}
