package fr.quatrevieux.araknemu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Wrap a collection and make a copy on write operation
 * to ensure that the original collection is never modified
 *
 * To keep the same collection type, you should define a custom copy factory (by default use {@link ArrayList})
 *
 * @param <E> The collection element type
 */
final public class CopyOnFirstWriteCollection<E> implements Collection<E> {
    private Collection<E> inner;
    private Function<Collection<E>, Collection<E>> copyFactory;

    public CopyOnFirstWriteCollection(Collection<E> inner, Function<Collection<E>, Collection<E>> copyFactory) {
        this.inner = inner;
        this.copyFactory = copyFactory;
    }

    public CopyOnFirstWriteCollection(Collection<E> inner) {
        this(inner, ArrayList::new);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return inner.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        if (copyFactory == null) {
            return inner.iterator();
        }

        // Overrides iterator to disallow remove()
        return new Iterator<E>() {
            final private Iterator<E> inner = CopyOnFirstWriteCollection.this.inner.iterator();

            @Override
            public boolean hasNext() {
                return inner.hasNext();
            }

            @Override
            public E next() {
                return inner.next();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return inner.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return inner.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return copy().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return copy().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return inner.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return copy().addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return copy().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return copy().retainAll(c);
    }

    @Override
    public void clear() {
        if (inner.isEmpty()) {
            return;
        }

        if (copyFactory != null) {
            inner = copyFactory.apply(Collections.emptyList());
            copyFactory = null;
        } else {
            inner.clear();
        }
    }

    /**
     * Get the copy of the inner collection, if not already done
     */
    private Collection<E> copy() {
        if (copyFactory == null) {
            return inner;
        }

        inner = copyFactory.apply(inner);
        copyFactory = null;

        return inner;
    }
}
