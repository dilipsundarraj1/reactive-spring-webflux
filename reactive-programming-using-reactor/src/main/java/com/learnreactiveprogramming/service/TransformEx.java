package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class TransformEx {

    public static void main(String[] args) {
        System.out.println("*******Transform examples***********");
        trans();
        System.out.println("********TransformDeferred examples***********");
        trans1();
    }


    static void trans(){

        AtomicInteger atomicInteger = new AtomicInteger();
        Function<Flux<String>, Flux<String>> function = f -> {

            if(atomicInteger.incrementAndGet() == 1) {
                System.out.println("#atomic value in trans : " + atomicInteger);
                return f.filter(color -> !color.contains("orange"))
                        .map(String::toUpperCase);
            } else {
                System.out.println("#atomic value in trans : " + atomicInteger);
                return f.filter(color -> !color.contains("purple"))
                        .map(String::toUpperCase);
            }
        };

        Flux<String> composedFlux =
                Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                        .doOnNext(System.out::println)
                        .transform(function);
        System.out.println("-----subscriber 1 --of transform----");
        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :"+d));
        System.out.println("---subscriber 2-- of transform------");
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: "+d));

    }


    static void trans1(){
        AtomicInteger atomicInteger = new AtomicInteger();

        Function<Flux<String>, Flux<String>> function = f -> {

            if(atomicInteger.incrementAndGet() == 1) {
                System.out.println("!atomic value in trans1: "+ atomicInteger.get());
                return f.filter(color -> !color.contains("orange"))
                        .map(String::toUpperCase);
            } else {
                System.out.println("!atomic value in trans1: "+ atomicInteger.get());
                return f.filter(color -> !color.contains("purple"))
                        .map(String::toUpperCase);
            }
        };

        Flux<String> composedFlux =
                Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                        .doOnNext(System.out::println)
                        .transformDeferred(function);
        System.out.println("---------subscriber 1 of transform deferred-------------");
        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :"+d));
        System.out.println("--------subscriber 2 of transform deferred--------------");
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: "+d));
    }
}
