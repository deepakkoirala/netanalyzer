package com.thapasujan5.netanalzyerpro.Tools;

/**
 * Created by Suzan on 12/14/2015.
 */
public class CheckDigit {

    public static final boolean containsDigit(String s) {
        boolean containsDigit = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }
        return containsDigit;
    }
}
