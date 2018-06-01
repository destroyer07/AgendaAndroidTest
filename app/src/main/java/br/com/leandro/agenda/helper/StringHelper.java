package br.com.leandro.agenda.helper;

public class StringHelper {

    public static boolean isNullOrWhitespace(String s) {
        return isNullOrEmpty(s) || isWhitespace(s);
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }


    private static boolean isWhitespace(String s) {
        int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
