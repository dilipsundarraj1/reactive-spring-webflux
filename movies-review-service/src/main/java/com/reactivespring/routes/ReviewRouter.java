package com.reactivespring.routes;

import com.reactivespring.handler.ReviewsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewsHandler reviewsHandler) {
        return route()
                .nest(path("/v1/reviews"), builder ->
                        builder
                                .GET("", reviewsHandler::getReviews)
                                .POST("", reviewsHandler::addReview)
                                .PUT("/{id}", reviewsHandler::updateReview)
                                .DELETE("/{id}", reviewsHandler::deleteReview)
                                .GET("/stream", reviewsHandler::getReviewsStream))
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("HelloWorld")))
                .GET("/v1/greeting/{name}", (request -> ServerResponse.ok().bodyValue("hello " + request.pathVariable("name"))))
                //  .GET("/v1/reviews",reviewsHandler::getReviews)
               // .POST("/v1/reviews", reviewsHandler::addReview)
                .build();
    }
}
