package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        //given

        //when
        var stringFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(stringFlux)
                //.expectNext("alex", "ben", "chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_Immutability() {

        //given

        //when
        var stringFlux = fluxAndMonoGeneratorService.namesFlux_immutablity()
                .log();

        //then
        StepVerifier.create(stringFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNextCount(3)
                .verifyComplete();


    }

    @Test
    void namesMono() {

        //given
        //when
        var stringMono = fluxAndMonoGeneratorService.namesMono();

        //then
        StepVerifier.create(stringMono)
                .expectNext("alex")
                .verifyComplete();

    }

    @Test
    void namesMono_map_filter() {

        //given
        int stringLength = 3;

        //when
        var stringMono = fluxAndMonoGeneratorService.namesMono_map_filter(stringLength);

        //then
        StepVerifier.create(stringMono)
                .expectNext("ALEX")
                .verifyComplete();

    }

    @Test
    void namesMono_map_empty() {

        //given
        int stringLength = 4;

        //when
        var stringMono = fluxAndMonoGeneratorService.namesMono_map_filter(stringLength);

        //then
        StepVerifier.create(stringMono)
                .expectNext("default")
                .verifyComplete();

    }


    @Test
    void namesFlux_map() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap_async() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                /*.expectNext("0-A", "1-L", "2-E", "3-X")
                .expectNextCount(5)*/
                .expectNextCount(9)
                .verifyComplete();

    }

    @Test
    void namesFlux_concatMap() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                //expectNext("0-A", "1-L", "2-E", "3-X")
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_concatmap_withVirtualTime() {
        //given
        VirtualTimeScheduler.getOrSet();
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        //then
        StepVerifier.withVirtualTime(()-> namesFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A","L","E","X","C","H","L","O","E")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_flatmap() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatmap(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();

    }

    @Test
    void namesMono_flatmapMany() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatmapMany(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();

    }


    @Test
    void namesFlux_transform() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_transform_1() {

        //given
        int stringLength = 6;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("default")
                //.expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_transform_switchIfEmpty() {

        //given
        int stringLength = 6;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                //.expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_transform_concatwith() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_concatwith(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE", "4-ANNA")
                .verifyComplete();

    }

    @Test
    void name_defaultIfEmpty() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.name_defaultIfEmpty();

        //then
        StepVerifier.create(value)
                .expectNext("Default")
                .verifyComplete();

    }

    @Test
    void name_switchIfEmpty() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.name_switchIfEmpty();

        //then
        StepVerifier.create(value)
                .expectNext("Default")
                .verifyComplete();

    }

    @Test
    void explore_concat() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_concat();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }


    @Test
    void explore_concatWith() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_concatWith();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void explore_concat_mono() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_concatWith_mono();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B")
                .verifyComplete();

    }

    @Test
    void explore_merge() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_merge();

        //then
        StepVerifier.create(value)
                // .expectNext("A", "B", "C", "D", "E", "F")
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();

    }

    @Test
    void explore_mergeWith() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_mergeWith();

        //then
        StepVerifier.create(value)

                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();

    }

    @Test
    void explore_mergeWith_mono() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_mergeWith_mono();

        //then
        StepVerifier.create(value)

                .expectNext("A", "B")
                .verifyComplete();

    }

    @Test
    void explore_mergeSequential() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_mergeSequential();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void explore_zip() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zip().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

    }

    @Test
    void explore_zip_1() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zip_1().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();

    }


    @Test
    void explore_zip_2() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zip_2().log();

        //then
        StepVerifier.create(value)
                .expectNext("AB")
                .verifyComplete();

    }

    @Test
    void explore_zipWith() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zipWith().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

    }

    @Test
    void explore_zipWith_mono() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zipWith_mono().log();

        //then
        StepVerifier.create(value)
                .expectNext("AB")
                .verifyComplete();

    }

}