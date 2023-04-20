package heap;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Scanner;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccessibilityHeapTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10); // 10sec timeout

    /** Use assertEquals to check that mh correctly represents a heap
     *  with values b and priorities p.
     *  This means that:
     *    - for each i in 0..size-1, (b[i], p[i]) is in mh.c
     *    - mh.size() = b.length = p.length
     *    - mh satisfies invariants 1 and 2.
     *  Precondition: b.length == p.length.  */
    public <V,P extends Comparable<P>> void check(V[] b, P[] p, Heap<V,P> mh) {
        assert b.length == p.length;

        // Invariant 1
        for (int i = 0; i < mh.size(); i++) {
        	assertTrue(mh.c.get(i) != null);
        }

        // invariant 2
        for (int i = 1; i < mh.size(); i++) {
        	assertTrue(mh.c.get(i).priority.compareTo(mh.c.get((i-1)/2).priority) >= 0);
        }

        // check equality with (b,p)
        assertEquals(b.length, mh.size());
        //assertEquals(b.length, mh.map.size());

        // check the entries of c match b and p
        boolean seen[] = new boolean[b.length];
        for (int i = 0; i < mh.size(); i++) {
            V val = mh.c.get(i).value;
            P pri = mh.c.get(i).priority;
            for (int j = 0; j < b.length; j++) {
                if (val.equals(b[j]) && pri.equals(p[j])) {
                    // make sure we don't 't see an element twice
                    assertFalse(seen[j]);
                    seen[j] = true;
                }
            }
        }

        // make sure we saw every element
        for (int i= 0; i < seen.length; i++) {
            assertTrue(seen[i]);
        }
    }


    /** Use assertEquals to check that mh correctly represents a heap
     *  with values b and priorities p, including the 
     *  This means that:
     *    - for each i in 0..size-1, (b[i], p[i]) is in mh.c
     *    - mh.size() = b.length = p.length
     *    - mh satisfies all 5 invariants (including phase 3 invariants).
     *  Precondition: b.length == p.length.  */
    public <V,P extends Comparable<P>> void checkPhase3(V[] b, P[] p, Heap<V,P> mh) {
        // check phase 1 invariants and equality with p and b:
        check(b, p, mh);

     //  In Phase 3, the following class invariant also must be maintained:
     //    3. The tree cannot contain duplicate *values*; note that dupliate
     //       *priorities* are still allowed.
     //    4. map contains one entry for each element of the heap, so
     //       map.size() == c.size()
     //    5. For each value v in the heap, its map entry contains in the
     //       the index of v in c. Thus: map.get(b[i]) = i.
     //

      
        // invariant 4:
        assertEquals(b.length, mh.map.getSize());

        // invariants 3 and 5:
        for (int i= 0; i < mh.c.size(); i++) {
        	// check that (b,p) is in the map
        	assertTrue(mh.map.containsKey(b[i]));

        	int n = mh.map.get(b[i]);
        	assertEquals(mh.c.get(n).value, b[i]);

        	assertTrue(mh.c.get(n).priority.compareTo(p[i]) == 0);
        }
    }

    /**Return a heap with the values of b added to it, in that order. The
     * priorities are the values. */
    public Heap<Integer,Integer> makeHeap(Integer[] b) {
        Heap<Integer,Integer> m= new Heap<>();
        for (Integer e : b) m.add(e, e);
        return m;
    }

    /**Return a heap with the values of b and corresponding priorities p
     * added to it, in that order.  */
    public Heap<Integer,Double> makeHeap(Integer[] b, double[] p) {
        Heap<Integer,Double> m= new Heap<>();
        for (int h= 0; h < b.length; h= h+1) {
            m.add(b[h], p[h]);
        }
        return m;
    }

    /**Return a heap with the values of b and corresponding priorities p
     * added to it, in that order.  */
    public Heap<String,Double> makeHeap(String[] b, double[] p) {
        Heap<String,Double> m= new Heap<String,Double>();
        for (int h= 0; h < b.length; h= h+1) {
            m.add(b[h], p[h]);
        }
        return m;
    }

    @Test
    /** Test whether add works when the priority of the value being added is
     * not smaller than priorities of other values in the heap. */
    public void test100Add() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[] {5});
        check(new Integer[]{5}, new Integer[]{5}, mh);

        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {5, 7});
        check(new Integer[]{5, 7}, new Integer[]{5,7}, mh1);
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {5, 7, 8});
        check(new Integer[]{5, 7, 8}, new Integer[]{5, 7, 8}, mh2);
    }


    @Test
    /**  Test whether swap works in isolation */
    public void test110Swap() {
        Heap<Integer,Integer> mh = new Heap<Integer,Integer>();
        mh.add(10, 5);
        mh.add(11, 5);
        mh.swap(0, 1);
        assertEquals(11, (int)mh.c.get(0).value);
        assertEquals(10, (int)mh.c.get(1).value);
        check(new Integer[]{10, 11}, new Integer[]{5, 5}, mh);
    }


    @Test
    /** Test add and bubble up. */
    public void test115Add_BubbleUp() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[]{3});

        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{3, 6});
        check(new Integer[]{3, 6}, new Integer[]{3, 6}, mh1);
        mh1.add(8, 8);
        check(new Integer[]{3, 6, 8}, new Integer[]{3, 6, 8}, mh1);
        mh1.add(5, 5);
        check(new Integer[]{3, 5, 8, 6}, new Integer[]{3, 5, 8, 6}, mh1);
        mh1.add(4, 4);
        check(new Integer[]{3, 4, 8, 6, 5}, new Integer[]{3, 4, 8, 6, 5}, mh1);
        mh1.add(1, 1);
        check(new Integer[]{1, 4, 3, 6, 5, 8}, new Integer[]{1, 4, 3, 6, 5, 8}, mh1);
    }

    @Test
    /** Test add and bubble up with duplicate priorities */
    public void test117Add_BubbleUpDuplicatePriorities() {
        Heap<Integer,Double> mh= new Heap<Integer,Double>();
        mh.add(4, 4.0);
        check(new Integer[]{4}, new Double[]{4.0}, mh);
        mh.add(2, 4.0);
        check(new Integer[]{4, 2}, new Double[]{4.0, 4.0}, mh);
        mh.add(1, 4.0);
        check(new Integer[]{4, 2, 1}, new Double[]{4.0, 4.0, 4.0}, mh);
        mh.add(0, 4.0);
        check(new Integer[]{4, 2, 1, 0}, new Double[]{4.0, 4.0, 4.0, 4.0}, mh);
    }

    @Test
    /** Test peek. */
    public void test120Peek() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[]{1, 3});
        assertEquals(1, (int)mh.peek());
        check(new Integer[]{1, 3}, new Integer[]{1, 3}, mh);

        Heap<Integer,Double> mh1= new Heap<Integer,Double>();
        try {
            mh1.peek();  
            fail("Peek didn't throw an exception when the heap is empty.");
        } catch (NoSuchElementException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Peek threw something other than NoSuchElementException when the heap is empty");
        }
    }

    @Test
    /** Test poll and bubbledown with no duplicate priorities. */
    public void test130Poll_BubbleDown_NoDups() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[]{5});
        Integer res= mh.poll();
        assertEquals(5, (int)res);
        check(new Integer[]{}, new Integer[]{}, mh);

        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{5, 6});
        Integer res1= mh1.poll();
        assertEquals(5, (int)res1);
        check(new Integer[]{6}, new Integer[]{6}, mh1);

        // this requires comparing lchild and rchild and using lchild
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {4, 5, 6, 7, 8, 9});
        Integer res2= mh2.poll();
        assertEquals(4, (int)res2);
        check(new Integer[]{5, 7, 6, 9, 8}, new Integer[]{5, 7, 6, 9, 8}, mh2);

        // this requires comparing lchild and rchild and using rchild
        Heap<Integer,Integer> mh3= makeHeap(new Integer[] {4, 6, 5, 7, 8, 9});
        Integer res3= mh3.poll();
        assertEquals(4, (int)res3);
        check(new Integer[]{5, 6, 9, 7, 8}, new Integer[]{5, 6, 9, 7, 8}, mh3);

        // this requires bubbling down when only one child
        Heap<Integer,Integer> mh4= makeHeap(new Integer[] {4, 5, 6, 7, 8});
        Integer res4= mh4.poll();
        assertEquals(4, (int)res4);
        check(new Integer[]{5, 7, 6, 8}, new Integer[]{5, 7, 6, 8}, mh4);

        Heap<Integer,Integer> mh5= makeHeap(new Integer[] {2, 4, 3, 6, 7, 8, 9});
        Integer res5= mh5.poll();
        assertEquals(2, (int)res5);
        check(new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh5);

        Heap<Integer,Integer> mh6= makeHeap(new Integer[] {2, 4, 3, 6, 7, 9, 8});
        Integer res6= mh6.poll();
        assertEquals(2, (int)res6);
        check(new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh6);

        Heap<Integer,Integer> mh7= new Heap<Integer,Integer>();
        try {
            mh7.poll();  
            fail("Polling an empty heap didn't throw an exception");
        } catch (NoSuchElementException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Polling an empty heap threw something other than NoSuchElementExceptionException");
        }
    }

    @Test
    /** Test bubble-up and bubble-down with duplicate priorities. */
    public void test140testDuplicatePriorities() {
        // values should not bubble up or down past ones with duplicate priorities.
        // First two check bubble up
        Heap<Integer,Double> mh1= makeHeap(new Integer[] {6}, new double[] {4});
        mh1.add(5, 4.0);
        check(new Integer[]{6, 5}, new Double[]{4.0, 4.0}, mh1);

        Heap<Integer,Double> mh2= makeHeap(new Integer[] {7, 6}, new double[] {4, 4});
        mh2.add(3, 4.0);
        check(new Integer[]{7, 6, 3}, new Double[]{4.0, 4.0, 4.0}, mh2);

        // Check bubble down
        Heap<Integer,Double> mh3= makeHeap(new Integer[] {5, 6, 7}, new double[] {4, 4, 4});
        mh3.poll();
        check(new Integer[]{7, 6}, new Double[]{4.0, 4.0}, mh3);

        // Check bubble down
        Heap<Integer,Double> mh4= makeHeap(new Integer[] {5, 7, 6, 8}, new double[] {4, 4, 4, 4});
        mh4.poll();
        check(new Integer[]{8, 7, 6}, new Double[]{4.0, 4.0, 4.0}, mh4);
    }


    @Test
    /** Test a few calls with Strings */
    public void test170Strings() {
        Heap<String,Integer> mh= new Heap<String,Integer>();
        check(new String[]{}, new Integer[]{}, mh);
        mh.add("abc", 5);
        check(new String[]{"abc"}, new Integer[]{5}, mh);
        mh.add("beep", 3);
        check(new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);
        mh.add("", 2);
        check(new String[]{"", "abc", "beep"}, new Integer[]{2, 5, 3}, mh);
        String p= mh.poll();
        assertEquals("", p);
        check(new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);
    }

    @Test
    /** Test using values in 0..999 and random values for the priorities.
     *  There will be duplicate priorities. */
    public void test190BigTests() {
        // The values to put in Heap
        int[] b= new int[1000];
        for (int k= 0; k < b.length; k= k+1) {
            b[k]= k;
        }

        Random rand= new Random(52);

        // bp: priorities of the values
        double[] bp= new double[b.length];
        for (int k= 0; k < bp.length; k= k+1) {
            bp[k]= (int)(rand.nextDouble()*bp.length);
        }

        // Build the Heap and map to be able to get priorities easily
        Heap<Integer,Double> mh= new Heap<Integer,Double>();
        HashMap<Integer, Double> hashMap= new HashMap<Integer, Double>();
        for (int k= 0; k < b.length; k= k+1) {
            mh.add(b[k], bp[k]);
            hashMap.put(b[k], bp[k]);
        }

        // poll values one by one, check that priorities are in order, store
        // in dups the number of duplicate priorities, and save polled value
        // in array bpoll.
        double prevPriority= -1;
        int dups= 0; //Number of duplicate keys,
        int[] bpoll= new int[b.length];
        for (int k= 0; k < b.length; k= k+1) {
            bpoll[k]= mh.poll();
            Double p= hashMap.get(bpoll[k]);
            if (p == prevPriority) {
                dups= dups + 1;
            }
            assertEquals("In a larger-scale test, the heap order property was violated after calls to both add and poll.", true, prevPriority <= p);
            prevPriority= p;
        }

        // Sort bpoll and check that it contains 0..bpoll.length-1
        Arrays.sort(bpoll);
        for (int k= 0; k < b.length; k= k+1) {
            assertEquals("In a larger-scale test, the heap did not have the correct elements.", k, bpoll[k]);
        }
        // System.out.println("duplicate priorities: " + dups);
    }

    ///////////////////
    // Phase 2 Tests //
    ///////////////////

    @Test
    /** Test whether add works when the priority of the value being added is
     * not smaller than priorities of other values in the heap. */
    public void test300Add() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[] {5});
        checkPhase3(new Integer[]{5}, new Integer[]{5}, mh);

        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {5, 7});
        checkPhase3(new Integer[]{5, 7}, new Integer[]{5,7}, mh1);
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {5, 7, 8});
        checkPhase3(new Integer[]{5, 7, 8}, new Integer[]{5, 7, 8}, mh2);
    }

    @Test
    /**  Test whether swap works in isolation */
    public void test310Swap() {
        Heap<Integer,Integer> mh = new Heap<Integer,Integer>();
        mh.add(10, 5);
        mh.add(11, 5);
        mh.swap(0, 1);
        assertEquals(11, (int)mh.c.get(0).value);
        assertEquals(10, (int)mh.c.get(1).value);
        checkPhase3(new Integer[]{10, 11}, new Integer[]{5, 5}, mh);
    }


    @Test
    /** Test add and bubble up. */
    public void test315Add_BubbleUp() {

        Heap<Integer,Integer> mh= makeHeap(new Integer[]{3});

        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{3, 6});
        checkPhase3(new Integer[]{3, 6}, new Integer[]{3, 6}, mh1);
        mh1.add(8, 8);
        checkPhase3(new Integer[]{3, 6, 8}, new Integer[]{3, 6, 8}, mh1);
        mh1.add(5, 5);
        checkPhase3(new Integer[]{3, 5, 8, 6}, new Integer[]{3, 5, 8, 6}, mh1);
        mh1.add(4, 4);
        checkPhase3(new Integer[]{3, 4, 8, 6, 5}, new Integer[]{3, 4, 8, 6, 5}, mh1);
        mh1.add(1, 1);
        checkPhase3(new Integer[]{1, 4, 3, 6, 5, 8}, new Integer[]{1, 4, 3, 6, 5, 8}, mh1);

        // Contributed by Guanghai Wang, Winter 2019:
        try {
            mh1.add(6, 6);
            fail("Adding an existing element already in the heap didn't throw an exception");
        } catch (IllegalArgumentException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Adding an existing element already in the heap threw something other than IllegalArgumentException");
        }
    }

    @Test
    /** Test add and bubble up with duplicate priorities */
    public void test317Add_BubbleUpDuplicatePriorities() {
        Heap<Integer,Double> mh= new Heap<Integer,Double>();
        mh.add(4, 4.0);
        checkPhase3(new Integer[]{4}, new Double[]{4.0}, mh);
        mh.add(2, 4.0);
        checkPhase3(new Integer[]{4, 2}, new Double[]{4.0, 4.0}, mh);
        mh.add(1, 4.0);
        checkPhase3(new Integer[]{4, 2, 1}, new Double[]{4.0, 4.0, 4.0}, mh);
        mh.add(0, 4.0);
        checkPhase3(new Integer[]{4, 2, 1, 0}, new Double[]{4.0, 4.0, 4.0, 4.0}, mh);
    }

    @Test
    /** Test poll and bubbledown with no duplicate priorities. */
    public void test330Poll_BubbleDown_NoDups() {

        Heap<Integer,Integer> mh= makeHeap(new Integer[]{5});
        Integer res= mh.poll();
        assertEquals(5, (int)res);
        checkPhase3(new Integer[]{}, new Integer[]{}, mh);

        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{5, 6});
        Integer res1= mh1.poll();
        assertEquals(5, (int)res1);
        checkPhase3(new Integer[]{6}, new Integer[]{6}, mh1);

        // this requires comparing lchild and rchild and using lchild
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {4, 5, 6, 7, 8, 9});
        Integer res2= mh2.poll();
        assertEquals(4, (int)res2);
        checkPhase3(new Integer[]{5, 7, 6, 9, 8}, new Integer[]{5, 7, 6, 9, 8}, mh2);

        // this requires comparing lchild and rchild and using rchild
        Heap<Integer,Integer> mh3= makeHeap(new Integer[] {4, 6, 5, 7, 8, 9});
        Integer res3= mh3.poll();
        assertEquals(4, (int)res3);
        checkPhase3(new Integer[]{5, 6, 9, 7, 8}, new Integer[]{5, 6, 9, 7, 8}, mh3);

        // this requires bubbling down when only one child
        Heap<Integer,Integer> mh4= makeHeap(new Integer[] {4, 5, 6, 7, 8});
        Integer res4= mh4.poll();
        assertEquals(4, (int)res4);
        checkPhase3(new Integer[]{5, 7, 6, 8}, new Integer[]{5, 7, 6, 8}, mh4);

        Heap<Integer,Integer> mh5= makeHeap(new Integer[] {2, 4, 3, 6, 7, 8, 9});
        Integer res5= mh5.poll();
        assertEquals(2, (int)res5);
        checkPhase3(new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh5);

        Heap<Integer,Integer> mh6= makeHeap(new Integer[] {2, 4, 3, 6, 7, 9, 8});
        Integer res6= mh6.poll();
        assertEquals(2, (int)res6);
        checkPhase3(new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh6);

        Heap<Integer,Integer> mh7= new Heap<Integer,Integer>();
        try {
            mh7.poll();  fail("Polling an empty heap didn't throw an exception");
        } catch (NoSuchElementException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Polling an empty heap threw something other than NoSuchElementExceptionException");
        }
    }

    @Test
    /** Test bubble-up and bubble-down with duplicate priorities. */
    public void test340testDuplicatePriorities() {
        // values should not bubble up or down past ones with duplicate priorities.
        // First two check bubble up
        Heap<Integer,Double> mh1= makeHeap(new Integer[] {6}, new double[] {4});
        mh1.add(5, 4.0);
        checkPhase3(new Integer[]{6, 5}, new Double[]{4.0, 4.0}, mh1);

        Heap<Integer,Double> mh2= makeHeap(new Integer[] {7, 6}, new double[] {4, 4});
        mh2.add(3, 4.0);
        checkPhase3(new Integer[]{7, 6, 3}, new Double[]{4.0, 4.0, 4.0}, mh2);

        // Check bubble down
        Heap<Integer,Double> mh3= makeHeap(new Integer[] {5, 6, 7}, new double[] {4, 4, 4});
        mh3.poll();
        checkPhase3(new Integer[]{7, 6}, new Double[]{4.0, 4.0}, mh3);

        // Check bubble down
        Heap<Integer,Double> mh4= makeHeap(new Integer[] {5, 7, 6, 8}, new double[] {4, 4, 4, 4});
        mh4.poll();
        checkPhase3(new Integer[]{8, 7, 6}, new Double[]{4.0, 4.0, 4.0}, mh4);
    }

    @Test
    /** Test the contains method */
    public void test350contains() {
        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
        for (int i = 1; i < 10; i++) {
            assertTrue(mh1.contains(i));
        }
        assertFalse(mh1.contains(0));
        assertFalse(mh1.contains(11));
        assertFalse(mh1.contains(974));
    }


    @Test
    /** Test updatePriority. */
    public void test360ChangeAccessibilityPriority() {
        // First three: bubble up tests
        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh1.changeAccessibilityPriority(5, 4);
        checkPhase3(new Integer[]{1, 2, 3, 5, 6, 7, 9}, new Integer[]{1, 2, 3, 4, 6, 7, 9}, mh1);

        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh2.changeAccessibilityPriority(2, 1);
        checkPhase3(new Integer[]{1, 2, 3, 5, 6, 7, 9}, new Integer[]{1, 1, 3, 5, 6, 7, 9}, mh2);

        Heap<Integer,Integer> mh3= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh3.changeAccessibilityPriority(5, 1);
        checkPhase3(new Integer[]{1, 5, 3, 2, 6, 7, 9}, new Integer[]{1, 1, 3, 2, 6, 7, 9}, mh3);

       // second three: bubble down tests
        Heap<Integer,Integer> mh4= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh4.changeAccessibilityPriority(2, 5);
        checkPhase3(new Integer[]{1, 2, 3, 5, 6, 7, 9}, new Integer[]{1, 5, 3, 5, 6, 7, 9}, mh4);

        Heap<Integer,Integer> mh5= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh5.changeAccessibilityPriority(2, 6);
        checkPhase3(new Integer[]{1, 5, 3, 2, 6, 7, 9}, new Integer[]{1, 5, 3, 6, 6, 7, 9}, mh5);

        Heap<Integer,Integer> mh6= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh6.changeAccessibilityPriority(1, 7);
        checkPhase3(new Integer[]{2, 5, 3, 1, 6, 7, 9}, new Integer[]{2, 5, 3, 7, 6, 7, 9}, mh6);

        Heap<Integer,Integer> mh7= new Heap<Integer,Integer>();
        mh7.add(5, 5);
        try {
            mh7.changeAccessibilityPriority(6, 5);  fail("Changing priority of a nonexistent element didn't throw an exception");
        } catch (IllegalArgumentException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Changing priority of a nonexistent element threw something other than IllegalArgumentException");
        }
    }


    @Test
    /** Test a few calls with Strings */
    public void test370Strings() {
        Heap<String,Integer> mh= new Heap<String,Integer>();
        checkPhase3(new String[]{}, new Integer[]{}, mh);
        mh.add("abc", 5);
        checkPhase3(new String[]{"abc"}, new Integer[]{5}, mh);
        mh.add("beep", 3);
        checkPhase3(new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);
        mh.add("", 2);
        checkPhase3(new String[]{"", "abc", "beep"}, new Integer[]{2, 5, 3}, mh);
        String p= mh.poll();
        assertEquals("", p);
        checkPhase3(new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);

        assertTrue(mh.contains("beep"));
        assertFalse(mh.contains("boop"));
    }

    @Test
    /** Test using values in 0..999 and random values for the priorities.
     *  There will be duplicate priorities. */
    public void test390BigTests() {
        // The values to put in Heap
        int[] b= new int[1000];
        for (int k= 0; k < b.length; k++) {
            b[k]= k;
        }

        Random rand= new Random(52);

        // bp: priorities of the values
        double[] bp= new double[b.length];
        for (int k= 0; k < bp.length; k= k+1) {
            bp[k]= (int)(rand.nextDouble()*bp.length);
        }

        // Build the Heap and map to be able to get priorities easily
        Heap<Integer,Double> mh = new Heap<Integer,Double>();
        HashMap<Integer, Double> hashMap= new HashMap<Integer, Double>();
        for (int k= 0; k < b.length; k= k+1) {
            mh.add(b[k], bp[k]);
            // hashMap.put(b[k], bp[k]);
        }

        // poll values one by one, check that priorities are in order, store
        // in dups the number of duplicate priorities, and save polled value
        // in array bpoll.
        double prevPriority= -1;
        int dups= 0; //Number of duplicate keys,
        int[] bpoll= new int[b.length];
        for (int k = 0; k < b.length; k++) {
            bpoll[k] = mh.poll();
            double p = bp[Arrays.binarySearch(b, bpoll[k])];
            if (p == prevPriority) {
                dups++;
            }
            assertTrue(prevPriority <= p);
            prevPriority = p;
        }

        // Sort bpoll and check that it contains 0..bpoll.length-1
        Arrays.sort(bpoll);
        for (int k= 0; k < b.length; k= k+1) {
            assertEquals(k, bpoll[k]);
        }
    }
}
