package com.google.common.collect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * This class extends Guava's TreeBasedTable to allow subscription for data inserts for given usernames.
 * On each put operation in the tree the value will be pushed into the Flux stream registered for that username
 * @param <R>
 * @param <C>
 * @param <V>
 */
public class FluxEnableTreeBasedTable<R, C, V> extends TreeBasedTable<R, C, V> {
    private final Logger log = LoggerFactory.getLogger(getClass());


    FluxEnableTreeBasedTable(Comparator<? super R> rowComparator, Comparator<? super C> columnComparator) {
        super(rowComparator, columnComparator);
        this.subscribers = new HashMap<>();
    }

    public static <R extends Comparable, C extends Comparable, V> FluxEnableTreeBasedTable<R, C, V> create() {
        return new FluxEnableTreeBasedTable(Ordering.natural(), Ordering.natural());
    }

    // allow several subscribers for one username
    private final Map<String, Set<UpdateListener<V>>> subscribers;

    public Flux<V> subscribe(String username){
        if(subscribers.get(username) == null) {
            subscribers.put(username, new HashSet<>());
        }
        final Flux<V> bridge = Flux.create(sink -> {
            UpdateListener<V> updateListener = new UpdateListener<V>() {
                @Override
                public void onPut(V value) {
                    log.debug("onPut value: " + value);
                    sink.next(value);
                }
            };
            subscribers.get(username).add(updateListener);
        });
        return bridge;
    }

    interface UpdateListener<V> {
        void onPut(V value);
    }

    @Override
    public V put(R rowKey, C columnKey, V value) {
        V put = super.put(rowKey, columnKey, value);
        subscribers.entrySet().stream()
                .filter(entry -> entry.getKey().equals(rowKey))
                .forEach(entry -> entry.getValue().stream()
                        // call onPut for all listeners
                        .forEach(listener -> listener.onPut(value))
                );
        return put;
    }

}
