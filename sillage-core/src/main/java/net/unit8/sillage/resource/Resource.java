package net.unit8.sillage.resource;

import net.unit8.sillage.DecisionPoint;
import net.unit8.sillage.data.RestContext;

import java.util.function.Function;

/**
 * REST Resource interface.
 *
 * @author kawasima
 */
public interface Resource {
    /**
     * Get a handle function at the given decision point.
     *
     * @param point A decision point
     * @return A handle function at the given decision point
     */
    Function<RestContext, ?> getFunction(DecisionPoint point);
}
