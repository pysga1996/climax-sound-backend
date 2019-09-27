package com.lambda.climaxsound;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class twoSetComparisonTest {
    @Test
    private void twoSetOfString() {

        // Creating object of Set
        Set<String> arrset1 = new HashSet<String>();

        // Populating arrset1
        arrset1.add("A");
        arrset1.add("B");
        arrset1.add("C");
        arrset1.add("D");
        arrset1.add("E");

        // print arrset1
        System.out.println("First Set: "
                + arrset1);

        // Creating another object of Set
        Set<String> arrset2 = new HashSet<String>();

        // Populating arrset2
        arrset2.add("C");
        arrset2.add("D");
        arrset2.add("A");
        arrset2.add("B");
        arrset2.add("E");

        // print arrset2
        System.out.println("Second Set: "
                + arrset2);

        System.out.println(arrset1.equals(arrset2));

        // comparing first Set to another
        // using equals() method

        assertEquals(arrset1, arrset2);
//        assertTrue(value);

    }
}
