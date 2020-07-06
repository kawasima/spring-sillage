package net.unit8.sillage.example.user;

import net.unit8.sillage.Decision;
import net.unit8.sillage.resource.AllowedMethods;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import static net.unit8.sillage.DecisionPoint.PUT;

@Component
@AllowedMethods(HttpMethod.PUT)
public class PromotionResource {
    @Decision(PUT)
    public void promote() {

    }
}
