package com.example.mal.types.coll;

import com.example.mal.types.MalType;

import io.vavr.collection.Seq;

/**
 * Marker class for sequential collections (ie. not maps).
 *
 * @param <T> The concrete collection type
 */
public abstract class MalSequential<T extends Seq<MalType>> extends MalCollection<T> {
}
