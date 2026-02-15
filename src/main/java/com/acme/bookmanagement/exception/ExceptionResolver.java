package com.acme.bookmanagement.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class ExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof BookNotFoundException || ex instanceof AuthorNotFoundException) {
            return buildError(ex, env, graphql.ErrorType.DataFetchingException);
        }
        if (ex instanceof DuplicateAuthorException || ex instanceof DuplicateBookException || ex instanceof IllegalArgumentException) {
            return buildError(ex, env, graphql.ErrorType.ValidationError);
        }
        return null;
    }

    private GraphQLError buildError(Throwable ex, DataFetchingEnvironment env, graphql.ErrorType errorType) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .errorType(errorType)
                .build();
    }
}