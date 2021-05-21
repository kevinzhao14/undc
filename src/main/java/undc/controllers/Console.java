package undc.controllers;

public class Console {

    private static void print(String str, String color) {
        System.out.println(color + ": " + str);
    }

    public static void print(String str) {
        print(str, "black");
    }

    public static void warn(String str) {
        print(str, "yellow");
    }

    public static void error(String str) {
        print(str, "red");
    }

    public static void run(String command) {

    }
}
