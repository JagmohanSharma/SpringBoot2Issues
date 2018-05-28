package com.example.springboot2issue;

public class TestClass {

    public static void main(String[] args) {
//        printPrimes(100);

        System.out.println(testFinally());
    }

    static void printPrimes(int limit) {
        int counter = 0;
        int i =2;
        while(counter != limit) {
            if (checkPrime(i)) {
                System.out.println(i);
            }
            i++;
            counter++;
        }
    }

    static boolean checkPrime(int num) {
            for (int i = 2; i <= num/2; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int testFinally() {
        int a = 0;
        try {
           a = 10/0;
           return a;
        } catch (Exception e) {
            a = 3;
            System.out.println("in catch");
            return a;
        } finally {
            a = 5;
            System.out.println("in finally");
            return a;
        }
    }
}
