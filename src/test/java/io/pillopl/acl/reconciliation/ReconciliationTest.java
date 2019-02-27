package io.pillopl.acl.reconciliation;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ReconciliationTest {

    @Test
    public void shouldReturnADiff() throws Exception {
        //given
        Reconciliation<Integer> reconciliation = new Reconciliation<>(System.out::println);
        Set<Integer> oldOne = IntStream.of(1,2,3).boxed().collect(Collectors.toSet());
        Set<Integer> newOne = IntStream.of(2,3,4).boxed().collect(Collectors.toSet());

        //when
        Diff<Integer> diff = reconciliation.compare(oldOne, newOne);

        //then
        assertThat(diff.exists()).isTrue();
        assertThat(diff.getPresentJustInNew()).containsExactlyInAnyOrder(4);
        assertThat(diff.getPresentJustInOld()).containsExactlyInAnyOrder(1);

    }

    @Test
    public void shouldWorkForEmptyInput() throws Exception {
        //given
        Reconciliation<Integer> reconciliation = new Reconciliation<>(System.out::println);

        //when
        Diff<Integer> diff = reconciliation.compare(new HashSet<>(), new HashSet<>());

        //then
        assertThat(diff.exists()).isFalse();
        assertThat(diff.getPresentJustInNew()).isEmpty();
        assertThat(diff.getPresentJustInOld()).isEmpty();

    }

    @Test
    public void shouldNotReturnDiffForSameCollections() throws Exception {
        //given
        Reconciliation<Integer> reconciliation = new Reconciliation<>(System.out::println);
        Set<Integer> oldOne = IntStream.of(1,2,3).boxed().collect(Collectors.toSet());
        Set<Integer> newOne = IntStream.of(1,2,3).boxed().collect(Collectors.toSet());

        //when
        Diff<Integer> diff = reconciliation.compare(oldOne, newOne);

        //then
        assertThat(diff.exists()).isFalse();
        assertThat(diff.getPresentJustInNew()).isEmpty();
        assertThat(diff.getPresentJustInOld()).isEmpty();

    }

    @Test
    public void shouldNotWorkForNullInput() throws Exception {
        //given
        Reconciliation<Integer> reconciliation = new Reconciliation<>(System.out::println);

        //expect
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> reconciliation.compare(null, new HashSet<>()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> reconciliation.compare(new HashSet<>(), null));


    }

    @Test
    public void shouldWorkWhenOldHasLess() throws Exception {
        //given
        Reconciliation<Integer> reconciliation = new Reconciliation<>(System.out::println);
        Set<Integer> oldOne = IntStream.of(1,2,3).boxed().collect(Collectors.toSet());
        Set<Integer> newOne = IntStream.of(1,2,3,4).boxed().collect(Collectors.toSet());


        //when
        Diff<Integer> diff = reconciliation.compare(oldOne, newOne);

        //then
        assertThat(diff.exists()).isTrue();
        assertThat(diff.getPresentJustInNew()).containsExactlyInAnyOrder(4);
        assertThat(diff.getPresentJustInOld()).isEmpty();

    }

    @Test
    public void shouldWorkWhenOldHasMore() throws Exception {
        //given
        Reconciliation<Integer> reconciliation = new Reconciliation<>(System.out::println);
        Set<Integer> oldOne = IntStream.of(1,2,3).boxed().collect(Collectors.toSet());
        Set<Integer> newOne = IntStream.of(1).boxed().collect(Collectors.toSet());


        //when
        Diff<Integer> diff = reconciliation.compare(oldOne, newOne);

        //then
        assertThat(diff.exists()).isTrue();
        assertThat(diff.getPresentJustInNew()).isEmpty();
        assertThat(diff.getPresentJustInOld()).contains(2, 3);

    }

}