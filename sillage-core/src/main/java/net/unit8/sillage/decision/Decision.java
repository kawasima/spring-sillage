package net.unit8.sillage.decision;

import net.unit8.sillage.DecisionPoint;
import net.unit8.sillage.data.RestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Decision implements Node<Node<?>> {
    private static final Logger LOG = LoggerFactory.getLogger(Decision.class);

    private final DecisionPoint point;
    private final Function<RestContext, ?> test;
    private final Node<?> thenNode;
    private final Node<?> elseNode;

    public Decision(DecisionPoint name,
                    Function<RestContext, ?> test,
                    Node<?> thenNode,
                    Node<?> elseNode) {
        this.point = name;
        this.test = test;
        this.thenNode = thenNode;
        this.elseNode = elseNode;
    }

    @Override
    public Node<?> execute(RestContext context) {
        LOG.debug("{}", getName());
        Function<RestContext, ?> ftest = context.getResourceFunction(point);
        if (ftest == null) {
            ftest = test;
        }
        if (ftest == null) throw new NullPointerException(point.name());
        Object fres = ftest.apply(context);
        boolean result;
        if (fres == null) {
            result = false;
        } else if (fres instanceof Boolean) {
            result = (Boolean) fres;
        } else {
            context.setMessage(fres);
            result = true;
        }
        return result ? thenNode : elseNode;
    }

    public String getName() {
        return this.point.name();
    }

}
