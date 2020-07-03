package net.unit8.sillage.decision;

import net.unit8.sillage.data.RestContext;

public interface Node<RESPONSE> {
    RESPONSE execute(RestContext context);
}
