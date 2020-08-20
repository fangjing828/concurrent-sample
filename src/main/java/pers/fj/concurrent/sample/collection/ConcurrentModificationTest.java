package pers.fj.concurrent.sample.collection;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Created by fang_j on 2020/08/20.
 */
public class ConcurrentModificationTest {
    @Test
    public void whilstRemovingDuringIteration_shouldThrowException() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        assertThrows(ConcurrentModificationException.class, () -> {
            for (Integer integer : integers) {
                integers.remove(0);
            }
        });
    }

    @Test
    public void whilstAddingDuringIteration_shouldThrowException() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        assertThrows(ConcurrentModificationException.class, () -> {
            for (Integer integer : integers) {
                integers.add(0, 1);
            }
        });
    }

    @Test
    public void whilstRemovingDuringIterator_shouldThrowException() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        Iterator<Integer> iterator = integers.iterator();
        assertThrows(ConcurrentModificationException.class, () -> {
            while (iterator.hasNext()) {
                integers.remove(0);
            }
        });
    }

    @Test
    public void whilstAddingDuringIterator_shouldThrowException() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        Iterator<Integer> iterator = integers.iterator();
        assertThrows(ConcurrentModificationException.class, () -> {
            while (iterator.hasNext()) {
                integers.add(0);
            }
        });
    }

    @Test
    public void whilstAddingDuringStream_shouldThrowException() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        assertThrows(ConcurrentModificationException.class, () -> {
            integers.stream().forEach(i -> {
                if (integers.size() < 10) {
                    integers.add(0);
                }
            });
        });
    }

    /**
     * Using an Iterator Directly
     */
    @Test
    public void whilstRemovingUseIteratorDirectly() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == 2) {
                iterator.remove();
            }
        }
    }

    @Test
    public void whilstRemovingAfterIteration() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        List<Integer> toRemove = Lists.newArrayList();

        for (Integer integer : integers) {
            if(integer == 2) {
                toRemove.add(integer);
            }
        }
        integers.removeAll(toRemove);

        assertThat(integers).containsExactly(1, 3);
    }

    @Test
    public void whilstRemovingByRemoveIf() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);

        integers.removeIf(i -> i == 2);
        assertThat(integers).containsExactly(1, 3);
    }

    @Test
    public void whilstRemovingByStream() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);

        List<Integer> collected = integers
                .stream()
                .filter(i -> i != 2)
                .collect(toList());
        assertThat(collected).containsExactly(1, 3);
    }


}
