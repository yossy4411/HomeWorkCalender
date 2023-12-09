package com.okayu.homework;

import java.time.LocalDate;

public class Tester {
    public static void main(String[] args) {
        LocalDate c = LocalDate.of(2012,6,29);
        LocalDate a = LocalDate.of(2012, 6, 30);
        LocalDate b = LocalDate.of(2012, 7, 1);
        System.out.println(a.isAfter(b));
        System.out.println(a.isAfter(c));
        System.out.println(a.isBefore(b));
    }
}
