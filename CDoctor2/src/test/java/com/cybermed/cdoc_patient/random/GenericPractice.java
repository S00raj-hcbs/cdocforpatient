package com.cybermed.cdoc_patient.random;

import java.util.Arrays;
import java.util.List;

public class GenericPractice {

    public static void main(String[] args) {
        printData(Arrays.asList("Hello", "World!", "How", "Are", "You"));
    }

    public static void printData(List<?> list) {
        for (Object obj : list) {
            System.out.print(obj + "::");
        }
    }
}
