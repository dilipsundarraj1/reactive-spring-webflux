package com.reactivespring.sinks;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.stream.IntStream;

public class SinksTest {

    @Test
    void sink() {
        //given


        //Sinks.Many<Integer> replaySinks = Sinks.many().replay().latest();
        Sinks.Many<Integer> replaySinks = Sinks.many().replay().all();

        var emitResult = replaySinks.tryEmitNext(1);
        System.out.println("emitResult :  " + emitResult);
        replaySinks.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        /*Sinks.EmitResult emitResult1 = null;
        try {
            emitResult1 = replaySinks.tryEmitNext(errorFunction());
        }catch (Exception ex ){
            System.out.println("Exception is : " + ex);
            System.out.println("emitResult1  :" + emitResult1);
        }
*/

        Flux<Integer> integerFlux = replaySinks.asFlux();
        integerFlux
                .subscribe(s->{
                    System.out.println("Subscriber 1 : " + s);
                });

        Flux<Integer> integerFlux1 = replaySinks.asFlux();

        integerFlux1
                .subscribe(s->{
                    System.out.println("Subscriber 2 : " + s);
                });

    }


    @Test
    void sink_multicast() throws InterruptedException {

        //when

        // It can hold up to 256 elements by default
        Sinks.Many<Integer> multiCast = Sinks.many().multicast().onBackpressureBuffer();

        IntStream.rangeClosed(0,300)
                .forEach(multiCast::tryEmitNext);


        multiCast.tryEmitNext(301);
        multiCast.tryEmitNext(302);

        //then

        Flux<Integer> integerFlux = multiCast.asFlux();
        integerFlux
                .subscribe(s->{
                    System.out.println("Subscriber 1 : " + s);
                });

        multiCast.tryEmitNext(303);

        Flux<Integer> integerFlux1 = multiCast.asFlux();

        integerFlux1
                .subscribe(s->{
                    System.out.println("Subscriber 2 : " + s);
                });

        multiCast.tryEmitNext(4);
    }


}
