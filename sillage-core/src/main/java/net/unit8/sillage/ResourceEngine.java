package net.unit8.sillage;

import net.unit8.sillage.data.Resource;
import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.decision.Decision;
import net.unit8.sillage.decision.Handler;
import net.unit8.sillage.decision.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static net.unit8.sillage.DecisionPoint.*;
import static net.unit8.sillage.decision.DecisionFactory.*;

@Component
public class ResourceEngine {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceEngine.class);
    private boolean printStackTrace = false;

    /**
     * Execute a decision graph with the given context.
     *
     * @param context REST Context
     * @return API response
     */
    protected Object runDecisionGraph(RestContext context) {
        Node<?> decisionNode = createDefaultGraph();

        try {
            do {
                if (decisionNode instanceof Decision) {
                    decisionNode = ((Decision) decisionNode).execute(context);
                } else if (decisionNode instanceof Handler) {
                    return ((Handler) decisionNode).execute(context);
                }
            } while (decisionNode != null);
        } catch(Exception e) {
            LOG.error("Error occurs at handling resource", e);
            return Problem.builder()
                    .withStatus(Status.INTERNAL_SERVER_ERROR)
                    .withDetail(printStackTrace ? e.toString() : null);
        }
        return Problem.builder()
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .withDetail("Handler not defined");
    }

    /**
     * Execute a decision class with the given resource and request.
     *
     * @param resource Resource class for executing
     * @param request  A HTTP request
     * @return API response
     */
    public Object run(Resource resource, HttpServletRequest request, HttpServletResponse response) {
        RestContext context = new RestContext(resource, request, response);
        return runDecisionGraph(context);
    }

    private Function<RestContext, ?> createIsMethod(HttpMethod... methods) {
        Set<HttpMethod> methodSet = new HashSet<>(Arrays.asList(methods));
        return context -> methodSet.contains(context.getMethod());
    }

    private final Function<RestContext, ?> IF_MATCH_STAR_FUNC = context -> Objects.equals("*", context.getRequest().getHeader("if-match"));

    /**
     * Create a default decision graph
     *
     * @return A root node of the constructed decision graph.
     */
    protected Node<?> createDefaultGraph() {
        Node<?> handleSeeOther  = handler(HANDLE_SEE_OTHER, 303, null);
        Node<?> handleOK        = handler(HANDLE_OK, 200, "ok");
        Node<?> handleNoContent = handler(HANDLE_NO_CONTENT, 204, null);
        Node<?> handleMultipleRepresentations = handler(HANDLE_MULTIPLE_REPRESENTATIONS, 300, null);
        Node<?> handleAccepted  = handler(HANDLE_ACCEPTED , 202, null);
        Node<?> isMultipleRepresentations = decision(MULTIPLE_REPRESENTATIONS,
            handleMultipleRepresentations, handleOK);
        Node<?> isRespondWithEntity = decision(RESPOND_WITH_ENTITY,
            isMultipleRepresentations, handleNoContent);
        Node<?> handleCreated   = handler(HANDLE_CREATED, 201, null);
        Node<?> isNew           = decision(NEW, handleCreated, isRespondWithEntity);
        Node<?> doesPostRedirect  = decision(POST_REDIRECT, handleSeeOther, isNew);
        Node<?> isPostEnacted   = decision(POST_ENACTED, doesPostRedirect, handleAccepted);
        Node<?> isPutEnacted    = decision(PUT_ENACTED, isNew, handleAccepted);
        Node<?> handleNotFound  = handler(HANDLE_NOT_FOUND, 404, "Resource not found");
        Node<?> handleGone      = handler(HANDLE_GONE, 410, "Resource is gone");
        Node<?> post            = action(POST, isPostEnacted);
        Node<?> canPostToMissing= decision(CAN_POST_TO_MISSING, post, handleNotFound);
        Node<?> postToMissing   = decision(POST_TO_MISSING, createIsMethod(HttpMethod.POST), canPostToMissing, handleNotFound);
        Node<?> handleMovedPermanently = handler(HANDLE_MOVED_PERMANENTLY, 301, null);
        Node<?> handleMovedTemporarily = handler(HANDLE_MOVED_TEMPORARILY, 307, null);
        Node<?> canPostToGone   = decision(CAN_POST_TO_GONE, post, handleGone);
        Node<?> isPostToGone       = decision(POST_TO_GONE, canPostToGone, handleGone);
        Node<?> isMovedTemporarily = decision(MOVED_TEMPORARILY, handleMovedTemporarily, isPostToGone);
        Node<?> isMovedPermanently = decision(MOVED_PERMANENTLY, handleMovedPermanently, isMovedTemporarily);
        Node<?> didExist         = decision(EXISTED, isMovedPermanently, postToMissing);
        Node<?> handleConflict  = handler(HANDLE_CONFLICT, 409, "Conflict.");
        Node<?> isPatchEnacted  = decision(PATCH_ENACTED, isRespondWithEntity, handleAccepted);
        Node<?> patch           = action(PATCH, isPatchEnacted);
        Node<?> put             = action(PUT, isPutEnacted);
        Node<?> isMethodPost      = decision(METHOD_POST, createIsMethod(HttpMethod.POST), post, put);
        Node<?> doesConflict        = decision(CONFLICT, handleConflict, isMethodPost);
        Node<?> handleNotImplemented = handler(HANDLE_NOT_IMPLEMENTED, 501, "Not implemented.");
        Node<?> canPutToMissing = decision(CAN_PUT_TO_MISSING, doesConflict, handleNotImplemented);
        Node<?> doesPutToDifferentUrl = decision(PUT_TO_DIFFERENT_URL, handleMovedPermanently, canPutToMissing);
        Node<?> isMethodPut       = decision(METHOD_PUT, createIsMethod(HttpMethod.PUT), doesPutToDifferentUrl, didExist);
        Node<?> handlePreconditionFailed = handler(HANDLE_PRECONDITION_FAILED, 412, "Precondition failed.");
        Node<?> doesIfMatchStarExistForMissing = decision(DOES_IF_MATCH_STAR_EXIST_FOR_MISSING,
            IF_MATCH_STAR_FUNC,
            handlePreconditionFailed,
            isMethodPut);
        Node<?> handleNotModified = handler(HANDLE_NOT_MODIFIED, 304, null);
        Node<?> ifNoneMatch     = decision(IF_NONE_MATCH,
            createIsMethod(HttpMethod.HEAD, HttpMethod.GET),
            handleNotModified,
            handlePreconditionFailed);
        Node<?> putToExisting   = decision(PUT_TO_EXISTING,
            createIsMethod(HttpMethod.PUT),
            doesConflict,
            isMultipleRepresentations);
        Node<?> postToExisting   = decision(POST_TO_EXISTING,
            createIsMethod(HttpMethod.POST),
            doesConflict,
            putToExisting);
        Node<?> isDeleteEnacted  = decision(DELETE_ENACTED, isRespondWithEntity, handleAccepted);
        Node<?> delete           = action(DELETE, isDeleteEnacted);
        Node<?> methodPatch      = decision(METHOD_PATCH,
            createIsMethod(HttpMethod.PATCH), patch, postToExisting);
        Node<?> methodDelete     = decision(METHOD_DELETE,
            createIsMethod(HttpMethod.DELETE), delete, methodPatch);
        Node<?> modifiedSince    = decision(MODIFIED_SINCE,
            context -> null,
            methodDelete,
            handleNotModified);
        Node<?> ifModifiedSinceValidDate = decision(IF_MODIFIED_SINCE_VALID_DATE,
            context -> null,
            modifiedSince,
            methodDelete);
        Node<?> ifModifiedSinceExists = decision(IF_MODIFIED_SINCE_EXISTS,
            context -> null,
            ifModifiedSinceValidDate,
            methodDelete);

        Node<?> etagMatchesForIfNone = decision(ETAG_MATCHES_FOR_IF_NONE,
            context -> null,
            ifNoneMatch,
            ifModifiedSinceExists);

        Node<?> ifNoneMatchStar = decision(IF_NONE_MATCH_STAR,
            context -> Objects.equals("*", context.getRequest().getHeader("if-none-match")),
            ifNoneMatch,
            etagMatchesForIfNone);

        Node<?> ifNoneMatchExists = decision(IF_NONE_MATCH_EXISTS,
            context -> context.getRequest().getHeader("if-none-match") != null,
            ifNoneMatchStar,
            ifModifiedSinceExists);

        Node<?> unmodifiedSince = decision(UNMODIFIED_SINCE,
            context -> null,
            handlePreconditionFailed,
            ifNoneMatchExists);

        Node<?> ifUnmodifiedSinceValidDate = decision(IF_UNMODIFIED_SINCE_EXISTS,
            context -> null,
            unmodifiedSince,
            ifNoneMatchExists);

        Node<?> ifUnmodifiedSinceExists = decision(IF_UNMODIFIED_SINCE_EXISTS,
            context -> context.getRequest().getHeader("if-unmodified-since") != null,
            ifUnmodifiedSinceValidDate,
            ifNoneMatchExists);

        Node<?> etagMatchesForIfMatch = decision(ETAG_MATCHES_FOR_IF_MATCH,
            context -> null,
            ifUnmodifiedSinceValidDate,
            handlePreconditionFailed);

        Node<?> ifMatchStar = decision(IF_MATCH_STAR,
            ifUnmodifiedSinceExists,
            etagMatchesForIfMatch);

        Node<?> ifMatchExists = decision(IF_MATCH_EXISTS,
            context -> context.getRequest().getHeader("if-match") != null,
            ifMatchStar,
            ifUnmodifiedSinceExists);

        Node<?> exists = decision(EXISTS, ifMatchExists, doesIfMatchStarExistForMissing);
        Node<?> handleUnprocessableEntity = handler(HANDLE_UNPROCESSABLE_ENTITY, 422, "Unprocessable entity.");
        Node<?> processable = decision(PROCESSABLE, exists, handleUnprocessableEntity);
        Node<?> handleNotAcceptable = handler(HANDLE_NOT_ACCEPTABLE, 406, "No acceptable resource available.");
        Node<?> isEncodingAvailable = decision(ENCODING_AVAILABLE,
            context -> true,
            processable, handleNotAcceptable);

        Node<?> acceptEncodingExists = decision(ACCEPT_ENCODING_EXISTS,
            context -> context.getRequest().getHeader("accept-encoding"),
            isEncodingAvailable, processable);

        Node<?> isCharsetAvailable = decision(CHARSET_AVAILABLE,
            context -> true,
            acceptEncodingExists,
            handleNotAcceptable);

        Node<?> acceptCharsetExists = decision(ACCEPT_CHARSET_EXISTS,
            context -> context.getRequest().getHeader("accept-charset"),
            isCharsetAvailable, acceptEncodingExists);
        Node<?> languageAvailable = decision(LANGUAGE_AVAILABLE,
            context -> true,
            acceptCharsetExists,
            handleNotAcceptable);
        Node<?> acceptLanguageExists = decision(ACCEPT_LANGUAGE_EXISTS,
            context -> context.getRequest().getHeader("accept-language"),
            languageAvailable, acceptCharsetExists);
        Node<?> mediaTypeAvailable = decision(MEDIA_TYPE_AVAILABLE,
            context -> true,
            acceptLanguageExists, handleNotAcceptable);
        Node<?> acceptExists = decision(ACCEPT_EXISTS,
            context -> true,
            mediaTypeAvailable, acceptLanguageExists);

        Node<?> handleOptions = handler(HANDLE_OPTIONS, 200, null);

        Node<?> isOptions = decision(IS_OPTIONS,
            createIsMethod(HttpMethod.OPTIONS),
            handleOptions,
            acceptExists);

        Node<?> handleRequestEntityTooLarge = handler(HANDLE_REQUEST_ENTITY_TOO_LARGE, 413, "Request entity too large.");
        Node<?> validEntityLength = decision(VALID_ENTITY_LENGTH,
            isOptions, handleRequestEntityTooLarge);
        Node<?> handleUnsupportedMediaType = handler(HANDLE_UNSUPPORTED_MEDIA_TYPE, 415, "Unsupported media type.");
        Node<?> knownContentType = decision(KNOWN_CONTENT_TYPE, validEntityLength, handleUnsupportedMediaType);
        Node<?> validContentHeader = decision(VALID_CONTENT_HEADER, knownContentType, handleNotImplemented);
        Node<?> handleForbidden = handler(HANDLE_FORBIDDEN, 403, "Forbidden.");
        Node<?> isAllowed = decision(ALLOWED, validContentHeader, handleForbidden);
        Node<?> handleUnauthorized = handler(HANDLE_UNAUTHORIZED, 401, "Not Authorized.");
        Node<?> isAuthorized = decision(AUTHORIZED, isAllowed, handleUnauthorized);
        Node<?> handleMalformed = handler(HANDLE_MALFORMED, 400, "Bad request.");
        Node<?> malformed = decision(MALFORMED, handleMalformed, isAuthorized);

        Node<?> handleMethodNotAllowed = handler(HANDLE_METHOD_NOT_ALLOWED, 405, "Method not Allowed");
        Node<?> methodAllowed = decision(METHOD_ALLOWED, null, malformed, handleMethodNotAllowed);

        Node<?> handleUriTooLong = handler(HANDLE_URI_TOO_LONG, 414, "Request URI too long.");
        Node<?> uriTooLong = decision(URI_TOO_LONG, handleUriTooLong, methodAllowed);

        Node<?> handleUnknownMethod = handler(HANDLE_UNKNOWN_METHOD, 501, "Unknown method.");
        Node<?> knownMethod = decision(KNOWN_METHOD, uriTooLong, handleUnknownMethod);

        Node<?> handleServiceNotAvailable = handler(HANDLE_SERVICE_NOT_AVAILABLE, 503, "Service not available.");
        Node<?> serviceAvailable = decision(SERVICE_AVAILABLE, knownMethod, handleServiceNotAvailable);

        return action(INITIALIZE_CONTEXT, serviceAvailable);
    }

    public void setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
    }
}
