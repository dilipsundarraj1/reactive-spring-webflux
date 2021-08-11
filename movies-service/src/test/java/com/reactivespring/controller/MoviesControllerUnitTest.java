package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesController.class)
@AutoConfigureWebTestClient
public class MoviesControllerUnitTest {

    @MockBean
    private MoviesInfoRestClient moviesInfoRestClient;

    @MockBean
    private ReviewsRestClient reviewsRestClient;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void retrieveMovieById() {

        var reviewList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        var movieId = "abc";
        when(moviesInfoRestClient.retrieveMovieInfo(anyString()))
                .thenReturn(Mono.just(new MovieInfo(movieId, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));

        when(reviewsRestClient.retrieveReviews(anyString()))
                .thenReturn(Flux.fromIterable(reviewList));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", "abc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                            var movie = movieEntityExchangeResult.getResponseBody();
                            assert Objects.requireNonNull(movie).getReviewList().size() == 3;
                            assertEquals("Batman Begins", movie.getMovieInfo().getName());
                        }
                );
        //then
    }

    @Test
    void retrieveMovieById_404() {

        var reviewList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        when(moviesInfoRestClient.retrieveMovieInfo(anyString()))
                .thenReturn(Mono.error(new MoviesInfoClientException("MovieNotFound", 404)));

        when(reviewsRestClient.retrieveReviews(anyString()))
                .thenReturn(Flux.fromIterable(reviewList));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", "abc")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .consumeWith(movieEntityExchangeResult -> {
                            var errorMessage = movieEntityExchangeResult.getResponseBody();
                            assertEquals("MovieNotFound", errorMessage);
                        }
                );
    }

    @Test
    void retrieveMovieById_500() {


        var reviewList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        var errorMsg = "Service Unavailable";
        when(moviesInfoRestClient.retrieveMovieInfo(anyString()))
                .thenReturn(Mono.error(new MoviesInfoServerException(errorMsg)));

        when(reviewsRestClient.retrieveReviews(anyString()))
                .thenReturn(Flux.fromIterable(reviewList));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", "abc")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .consumeWith(movieEntityExchangeResult -> {
                            var errorMessage = movieEntityExchangeResult.getResponseBody();
                            assertEquals(errorMsg, errorMessage);
                        }
                );
    }


}
