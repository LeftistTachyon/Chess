/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

/**
 * Rectangle
 * Line2D especially the intersect method
 * Ellipse2D
 * Point
 * Math
 * StrictMath
 * BigInteger BigDecimal
 * Collections
 * Iterator
 * ListIterator
 * StringTokenizer uses a delimeter such as " " to split a string without regex
 * String.split(Pattern.quote(yourString)) to split without a regex
 * Collections.frequency
 * Converter
 * Objects
 * 
 * 
 * Random CS Contest stuff that I put in this project
 * since I work on it so much.
 * @author zwill
 */
public class ContestUtils {
    
    public static final int BINARY = 2; //or Character.MIN_RADIX
    public static final int BASE_3 = 3;
    
     
    public static List<Integer> getNumbers(String str) {
        List<Integer> list = new ArrayList<>();
        for (int outer = 0, len = str.length(); outer < len; ++outer) {
            if (Character.isDigit(str.charAt(outer))) {
                String num = str.substring(outer, outer + 1);
                for (int inner = outer + 1; inner < len; ++inner) {
                    if (Character.isDigit(str.charAt(inner))) {
                        ++outer;
                        num += str.substring(inner, inner + 1);
                    }
                    else {
                        break;
                    }
                }
                list.add(Integer.parseInt(num));
            }
        }
        return list;
    }
    
    private static boolean rangeCheck(int index, int size) {
        return index >= 0 && index <= size;
    }
    
    public static <T> List<T> getPartialView(final List<T> list, int start, final int end) {
        if (list == null) {
            throw new NullPointerException();
        }
        
        if (!rangeCheck(start, list.size()) || !rangeCheck(end, list.size())) {
            throw new IllegalArgumentException();
        }
        
        if (start > end) {
            
        }
        
        List<T> partial;
        if (list instanceof RandomAccess) {
            partial = new ArrayList<T>(end - start);
            while (start < end) {
                partial.add(list.get(start++));
            }
        }
        else {
            partial = new LinkedList<>();
            for (ListIterator<T> it = list.listIterator(start); start < end; ++start) {
                partial.add(it.next());
            }
        }
        return partial;
    }
    
    //1001 -> 1 + 8 = 9
    
    
    public static BigInteger convertBinarySequenceToBigInteger(String binary) {
        return new BigInteger(binary, Character.MIN_RADIX);
    }
    
    public static String convertBigIntegerToBinarySequence(BigInteger n) {
        return n.toString(BINARY);
    }
    
    /**
     * Converts a number to its binary representation. This method 
     * first converts the given number to it's binary string, then parses that
     * string to base 10 form.
     * For example 55 converts to the base-10 number 110111. 
     * @param n The number to convert.
     * @return The converted number.
     */
    public static int convertToBinaryForm(int n) {
        //Parse a binary string as a base 10 number.
        return Integer.parseInt(Integer.toBinaryString(n), 10);
    }
    
    public static int convertToAnyForm(int n, int base) {
        return Integer.parseInt(Integer.toString(n, base), 10);
    }
    
    
    
    /**
     * Converts the given integer to its desired base form as 
     * another integer.
     * @param num The given integer.
     * @param base The desired base.
     * @return A new integer that is the given integer in the desired base form.
     */
    public static BigInteger convertToAnyBaseNumber(BigInteger num, int base) {
        return new BigInteger(num.toString(), base);
    }
    
    /**
     * Converts the given integer to its desired base form as 
     * a string.
     * @param num The given integer.
     * @param base The desired base.
     * @return A string that is the given integer in the desired base form.
     */
    public static String convertToAnyBaseSequence(BigInteger num, int base) {
        return num.toString(base);
    }
    
    /**
     * Converts a string of signed binary digits to the proper base-10 
     * integer.
     * @param binary The given binary sequence.
     * @return A base-10 integer. 
     */
    public static int convertBinarySequenceToInteger(String binary) {
        return Integer.parseInt(binary, Character.MIN_RADIX);
    }
    
    /**
     * Converts a signed base-10 integer to its binary sequence. 
     * @param n The given base-10 integer.
     * @return A string of signed binary digits.
     */
    public static String convertIntegerToBinarySequence(int n) {
        return Integer.toString(n, Character.MIN_RADIX);
    }
    
    //you cannot take any complment of a negative number
    //flip bits and return result
    public static int firstComplement(int num) {
        String bits = Integer.toBinaryString(num);
        String flippedBits = "";
        for (int index = 0, length = bits.length(); index != length; ++index) {
            flippedBits += (bits.charAt(index) == '0') ? '1' : '0';
        }
        int result = Integer.parseInt(flippedBits, Character.MIN_RADIX);
        System.out.println(Integer.toBinaryString(result));
        return result;
    }
    
    //flip bits and convert it into regular decmial form and then add 1 to that decimal form
    //you can convert back to binary to see the two's complement in bin
    public static int secondComplement(int num) {
        String bits = Integer.toBinaryString(num);
        String flippedBits = "";
        for (int index = 0, length = bits.length(); index != length; ++index) {
            flippedBits += (bits.charAt(index) == '0') ? '1' : '0';
        }
        int result = Integer.parseInt(flippedBits, Character.MIN_RADIX) + 1;
        System.out.println(Integer.toBinaryString(result));
        //int i = 100000000000000000;
        return result;
    }
    
    

    public static void main(String[] args) throws UnsupportedEncodingException {
       int num = 53;
       int x = 3;
       System.out.println(num << x); //mutiply num by 2 to the x
       System.out.println(num >> x);
       System.out.println(num >>> x); 
       byte b = -4;
       byte c = 12;
       // byte sum = b + c; //this is illegal even tho b & c are legal values, bc there's a 
       //possibilty of overflow, if you were to add 128 to 128, which are both legal values
       
       for (byte i = 10; b < 130; b++) {
           //infinte loop due to overflow.
           break;
       }
       
       System.out.println(b + c);
       System.out.println(~b);
    }
}
