package com.tc.bubblelayout;

import android.text.TextUtils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * date：   2017/12/13  16:04
 * description 数值类型转换util
 * modify by
 *
 * @author tc
 */
public class URegex {

    public static final Pattern RegImage = Pattern.compile("\\.(png|gif|jpg|jpeg|bmp)");
    public static final Pattern RegUrlName = Pattern.compile("/((?!/).)*$");
    public static final Pattern RegUrlDomain = Pattern.compile("^https?://[^/]*");
    private static Pattern NUMERIC_PATTERN = Pattern.compile("^([-\\+]?[0-9]([0-9]*)(\\.[0-9]+)?)|(^0$)$");
    private static Pattern INTEGER_OR_LONG_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");
    private static Pattern DOUBLE_OR_FLOAT_PATTERN = Pattern.compile("^[-\\+]?[.\\d]*$");

    /**
     * 判断是否为数字。包含0123，123，1.11，0.11，0123.11等
     *
     * @param value 字符串
     * @return 是则返回true, 否则false
     */
    public static boolean isNumeric(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }

        Matcher matcher = NUMERIC_PATTERN.matcher(value);
        return matcher.find();
    }

    /**
     * 判断是否为int或long，整数--包括0123写法。
     *
     * @param value 字符串
     * @return 是则返回true, 否则false
     */
    public static boolean isIntegerOrLong(String value) {

        return INTEGER_OR_LONG_PATTERN.matcher(value).matches();
    }


    /**
     * 判断是否为浮点数，包括double和float
     *
     * @param value 传入的字符串
     * @return 是浮点数返回true, 否则返回false
     */
    public static boolean isDoubleOrFloat(String value) {
        return DOUBLE_OR_FLOAT_PATTERN.matcher(value).matches();
    }

    /**
     * 数字字符转换为整形
     *
     * @param value 字符串
     * @return 返回数字，不为数字则返回0
     */
    public static int convertInt(String value) {
        return convertInt(value, 0);
    }

    /**
     * 数字字符转换为整形
     *
     * @param value        字符串
     * @param defaultValue 默认值
     * @return 返回数字，不为数字则返回默认值
     */
    public static int convertInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 数字字符转换为长整型
     *
     * @param value 要转换的string
     * @return 返回long，不为数字则返回0
     */
    public static long convertLong(String value) {
        return convertLong(value, 0);
    }

    /**
     * 数字字符转换为长整型
     *
     * @param value        要转换的string
     * @param defaultValue 默认值
     * @return 返回long，不为数字则返回默认值
     */
    public static long convertLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 数字字符转换为双精度型
     *
     * @param value
     * @return 返回double，不为数字则返回0
     */
    public static double convertDouble(String value) {
        return convertDouble(value, 0);
    }

    /**
     * 数字字符转换为双精度型
     *
     * @param value        要转换的string
     * @param defaultValue 默认值
     * @return 返回double，不为数字则返回默认值
     */
    public static double convertDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * 数字字符转换单双精度型
     *
     * @param value
     * @return float，不为数字则返回0
     */
    public static float convertFloat(String value) {
        return convertFloat(value, 0);
    }

    /**
     * 数字字符转换为单精度型
     *
     * @param value
     * @param defaultValue 默认值
     * @return float，不为数字则返回默认值
     */
    public static float convertFloat(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换为boolean
     *
     * @param value        value
     * @param defaultValue defaultValue
     * @return
     */
    public static boolean toBoolean(String value, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 转换为boolean类型
     *
     * @param value
     * @return
     */
    public static boolean toBoolean(String value) {
        return toBoolean(value, false);
    }

    /**
     * 本方法主要是为了防止，和.出现显示错误。默认格式化为US。获取当前机器的默认语言来格式化对应的货币
     *
     * @param price  要格式化的价格
     * @param symbol 当前的货币符号
     * @return 当前货币符号对应的价格展示string
     */
    public static String getPriceStrByLocale(double price, String symbol) {
        NumberFormat format = NumberFormat.getNumberInstance();
        if (symbol.trim().equals("JP¥")) {
            int result = RoundUtil.roundReturnInt(price, 0);
            return format.format(result);

        }
        return format.format(price);
    }

    /**
     * 本方法主要是为了防止，和.出现显示错误。默认格式化为US。获取当前机器的默认语言来格式化对应的货币
     *
     * @param price 要格式化的价格
     * @return 当前货币符号对应的价格展示string
     */
    public static String getPriceStrByLocale(double price) {
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
        Currency currency = currencyInstance.getCurrency();
        return getPriceStrByLocale(price, currency.getSymbol());
    }

    /**
     * 过滤url
     *
     * @param url url
     * @return 过滤后的url
     */
    public static String getDomain(String url) {
        if (url == null) {
            return "";
        }
        Matcher matcher = RegUrlDomain.matcher(url);
        String value = "";
        if (matcher.find()) {
            value = matcher.group();
        }
        //ULog.db(value);
        return value;
    }

    /**
     * 根据相应的正式表达则，查看是否匹配
     *
     * @param content 内容
     * @param reg     正则表达式
     * @return 过滤后的内容，没有则返回空字符串
     */
    public static String match(String content, String reg) {
        if (content == null) {
            return "";
        }
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        String value = "";
        if (matcher.find()) {
            value = matcher.group();
        }
        return value;
    }

    /**
     * 根据相应的正式表达则，查看是否匹配
     *
     * @param content 内容
     * @param pattern 正则表达式
     * @return 过滤后的内容，没有则返回空字符串
     */
    public static String match(String content, Pattern pattern) {
        if (content == null) {
            return "";
        }
        Matcher matcher = pattern.matcher(content);
        String value = "";
        if (matcher.find()) {
            value = matcher.group();
        }
        return value;
    }

    /**
     * 校验邮箱格式
     *
     * @param email 邮箱字符串
     * @return 正确格式返回true, 否则false
     */
    public static boolean matchEmail(String email) {
        String check = "^[a-zA-Z\\d]+([-._][a-zA-Z\\d]+)*@[a-zA-Z\\d]+((-[a-zA-Z\\d]+)?)+([\\" +
                ".][a-zA-Z]+)+$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    /**
     * 是否匹配字符串
     *
     * @param content 内容
     * @param reg     正则式
     * @return 匹配则返回true，否则false
     */
    public static boolean isMatch(String content, String reg) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }
}
