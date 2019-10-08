package com.amedigital.SwPlanetsAPIAme.handler;

import org.springframework.http.HttpStatus;

import com.amedigital.SwPlanetsAPIAme.exception.PathNotFoundException;
import com.amedigital.SwPlanetsAPIAme.exception.PlanetNotFoundException;

import reactor.core.publisher.Mono;

class ThrowableTranslator {

    private final HttpStatus httpStatus;
    private final String message;

    private ThrowableTranslator(final Throwable throwable) {
        this.httpStatus = getStatus(throwable);
        this.message = throwable.getMessage();
    }

    private HttpStatus getStatus(final Throwable error) {
        if (error instanceof PathNotFoundException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof PlanetNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    HttpStatus getHttpStatus() {
        return httpStatus;
    }

    String getMessage() {
        return message;
    }

    static <T extends Throwable> Mono<ThrowableTranslator> translate(final Mono<T> throwable) {
        return throwable.flatMap(error -> Mono.just(new ThrowableTranslator(error)));
    }
}
