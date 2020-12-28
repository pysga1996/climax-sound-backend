package com.lambda.util.formatter;

public class StringAccentRemover {
    public static String removeStringAccent(String str) {
        String newString = String.valueOf(str);
        newString = newString.replaceAll("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ", "a");
        newString = newString.replaceAll("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ", "e");
        newString = newString.replaceAll("ì|í|ị|ỉ|ĩ", "i");
        newString = newString.replaceAll("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ", "o");
        newString = newString.replaceAll("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u");
        newString = newString.replaceAll("ỳ|ý|ỵ|ỷ|ỹ", "y");
        newString = newString.replaceAll("đ", "d");
        newString = newString.replaceAll("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ", "A");
        newString = newString.replaceAll("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ", "E");
        newString = newString.replaceAll("Ì|Í|Ị|Ỉ|Ĩ", "I");
        newString = newString.replaceAll("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ", "O");
        newString = newString.replaceAll("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ", "U");
        newString = newString.replaceAll("Ỳ|Ý|Ỵ|Ỷ|Ỹ", "Y");
        newString = newString.replaceAll("Đ", "D");
        return newString;
    }
}
