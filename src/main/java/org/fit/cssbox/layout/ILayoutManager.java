package org.fit.cssbox.layout;

/**
 * Interface implemented by Layout managers. They are useful for layout of content in different type of boxes.
 *
 * @author Ondry, Ondra
 */
public interface ILayoutManager {

    boolean doLayout(int availw, boolean force, boolean linestart);

}
