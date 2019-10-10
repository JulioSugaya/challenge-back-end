package com.amedigital.SwPlanetsAPIAme.router;

import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RequestPredicate;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.amedigital.SwPlanetsAPIAme.handler.ApiHandler;
import com.amedigital.SwPlanetsAPIAme.handler.ErrorHandler;

public class ApiRouter {

    private static final String PLANETS = "/planets";
    private static final String ID = "/{id}";

    @Bean
    static RouterFunction<?> doRoute(final ApiHandler apiHandler, final ErrorHandler errorHandler) {
        return
                nest(path(PLANETS),
                        nest(accept(APPLICATION_JSON),
                                route(GET(ID), apiHandler::findById)
                                        .andRoute(DELETE(ID), apiHandler::delete)
                                        .andRoute(GET("/swapi/all"), apiHandler::findAllBySwAPI)
                                        .andRoute(GET("/").and(hasQueryParam("search")), apiHandler::findByName)
                                        .andRoute(GET("/"), apiHandler::findAll)
                                        .andRoute(POST("/"), apiHandler::createPlanet)
                        ).andOther(route(RequestPredicates.all(), errorHandler::notFound))
                );
    }
    
    private static RequestPredicate hasQueryParam(String name) {
    	  return RequestPredicates.queryParam(name, p -> StringUtils.hasText(p));
    }
}
