package com.thapasujan5.netanalzyerpro.Tools;

/**
 * Created by Suzan on 12/17/2015.
 */
public class InttoIp {
    public static String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }
}
