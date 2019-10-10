package com.amedigital.SwPlanetsAPIAme.router;

import org.springframework.web.reactive.function.server.RouterFunction;

import com.amedigital.SwPlanetsAPIAme.handler.ApiHandler;
import com.amedigital.SwPlanetsAPIAme.handler.ErrorHandler;

public class MainRouter {

    public static RouterFunction<?> doRoute(final ApiHandler handler, final ErrorHandler errorHandler) {
        return ApiRouter
                .doRoute(handler, errorHandler)
                .andOther(StaticRouter.doRoute());
    }
}
