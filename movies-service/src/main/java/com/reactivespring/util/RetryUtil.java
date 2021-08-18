package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

public class RetryUtil {


    public static Retry retrySpec() {
        return RetrySpec.fixedDelay(3, Duration.ofSeconds(1))
                .filter((ex) -> ex instanceof MoviesInfoServerException || ex instanceof ReviewsServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));

    }
}
