package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "60000000")
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {

        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll()
                .block();
    }

    @Test
    void name() {
        //given

        //when
        webTestClient
                .get()
                .uri("/v1/helloworld")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("HelloWorld");
    }

    @Test
    void getReviews() {
        //given

        //when
        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });

    }

    @Test
    void getReviewsByMovieInfoId() {
        //given

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path("/v1/reviews")
                            .queryParam("movieInfoId", "1")
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    System.out.println("reviewList : " + reviewList);
                    assertEquals(2, reviewList.size());
                });

    }

    @Test
    void addReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        //when
        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var savedReview = reviewResponse.getResponseBody();
                    assert savedReview != null;
                    assertNotNull(savedReview.getReviewId());
                });

    }

    @Test
    void addReview_null_inputValidations() {
        //given
        var review = new Review(null, null, "Awesome Movie", null);
        //when
        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("movieInfoId.null : Pass a valid movieInfoId, rating.null : Pass a valid rating");

    }

    @Test
    void addReview_negative_rating_inputValidations() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", -9.00);
        //when
        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("rating.negative : rating is negative and please pass a non-negative value");

    }


    @Test
    void updateReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        //when
        assert savedReview != null;

        webTestClient
                .put()
                .uri("/v1/reviews/{id}", savedReview.getReviewId())
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    System.out.println("updatedReview : " + updatedReview);
                    assertNotNull(savedReview.getReviewId());
                    assertEquals(8.0, updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });

    }

    @Test
    void updateReview_NotFound() {
        //given
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        //when
        webTestClient
                .put()
                .uri("/v1/reviews/{id}", "abc")
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        //when
        assert savedReview != null;
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", savedReview.getReviewId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteReview_notFound() {
        //given
        //when
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", "123")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void error() {
        //given

        //when

        webTestClient
                .get()
                .uri("/v1/error")
                .exchange()
                .expectStatus()
                .is5xxServerError();

    }


}
