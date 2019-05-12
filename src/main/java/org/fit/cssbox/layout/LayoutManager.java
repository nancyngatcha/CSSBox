package org.fit.cssbox.layout;

/**
 * General interface implemented by Layout managers.
 *
 * @author Ondra
 * @author Ondry
 */
public interface LayoutManager {

    boolean doLayout(int availw, boolean force, boolean linestart);
}
