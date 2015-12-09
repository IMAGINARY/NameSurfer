/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.xwgou.namesurfer.translator;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.net.URL;

/**
 *
 * @author xwgou
 */
public class SyllableTranslator implements ITranslator {

//    private Map<Character, String> vowel;
    private Properties rules;
    private Properties keywords;
    private static final String SPACE_SEPARATOR = " ";
    private static final char[] LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z'};
    private static final String[] KEYWORDS = {"con", "sin", "y", "o", "por"};
//    private static final String PLUSEPSILON = "+0.00001";
    private static final String SUBEPSILON = "0";

    public static final URL RULES_PATH = SyllableTranslator.class.getResource( "rules.properties" );
    public static final URL KEYWORDS_PATH = SyllableTranslator.class.getResource( "keywords.properties" );

    public SyllableTranslator() {
        try {
            init( RULES_PATH.openStream(), KEYWORDS_PATH.openStream() );
        } catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public SyllableTranslator(String rulesFile, String keywordsFile) throws IOException {
        this( new FileInputStream(rulesFile), new FileInputStream(keywordsFile) );
    }

    public SyllableTranslator(InputStream rulesStream, InputStream keywordsStream) throws IOException {
        init( rulesStream, keywordsStream );
    }

    private void init(InputStream rulesStream, InputStream keywordsStream) throws IOException {
        this.rules = new Properties();
        this.keywords = new Properties();

        this.rules.load( rulesStream );
        this.keywords.load( keywordsStream );
    }

    public String translate(String phrase) {
        if (phrase.length() == 0) {
            return "";
        }

        String[] words = phrase.split(SPACE_SEPARATOR);
        List<String> result = new ArrayList<String>();
        String recentKeyword = null;
        boolean hasKeyword = false;

        for (String w : words) {
            if (hasKeyword) {
                if (isKeyWord(w)) {
                    // two consecutive keyword, return immediately the result,
                    // even the result is not a valid formula;
                    return toFormula(result);
                } else {
                    recentKeyword = this.keywords.getProperty(recentKeyword);
                    recentKeyword = recentKeyword.replaceAll("\\$1", result.get(result.size() - 1));
                    recentKeyword = recentKeyword.replaceAll("\\$2", translateWord(w));
                    result.set(result.size() - 1, "(" + recentKeyword + ")");
                    hasKeyword = false;
                }
            } else {
                if (isKeyWord(w)) {
                    recentKeyword = w;
                    hasKeyword = true;
                } else {
                    result.add(translateWord(w));
                }
            }
        }

        if (result.isEmpty() && recentKeyword != null) {
            // that means has a single keyword, so, ignore the keyword
            // and treat it as word
            result.add(translateWord(recentKeyword));
        }

        return toFormula(result);
    }

    public String translateWord(String phrase) {
        if (phrase.isEmpty() || phrase.trim().isEmpty()) {
            return "";
        }

        phrase = phrase.toLowerCase();
        return applyRule(phrase.charAt(0), phrase.substring(1));
    }

    private String applyRule(Character ch, String word) {
        String rule = this.rules.getProperty(ch.toString());


        if (word.isEmpty()) {
            if (isVowel(ch)) {
                return "(" + rule + ")";
            }
            return SUBEPSILON;
        }

        if (rule == null) {
            return "";
        }

        if (isVowel(ch)) {
            rule = rule + "+$";
        }
        rule = rule.replaceAll("\\$", translateWord(word));

        return "(" + rule + ")";
    }
//    private static final String a = "x";
//    private static final String e = "y";
//    private static final String i = "z";
//    private static final String o = "x-y";
//    private static final String u = "y-z";
//    private static final String b = "($)*x";
//    private static final String c = "($)*(-x)";
//    private static final String d = "($)*(x-0.5)";
//    private static final String f = "($)*(0.5-x)";
//    private static final String g = "($)*y";
//    private static final String h = "($)*(-y)";
//    private static final String j = "($)*(y-0.5)";
//    private static final String k = "($)*(0.5-y)";
//    private static final String l = "($)*z";
//    private static final String m = "($)*(-z)";
//    private static final String n = "($)*(z-0.5)";
//    private static final String p = "($)*(0.5-z)";
//    private static final String q = "($)-x^2";
//    private static final String r = "($)-y^2";
//    private static final String s = "($)-z^2";
//    private static final String t = "($)-x^2-y^2";
//    private static final String v = "($)-y^2-z^2";
//    private static final String w = "($)-x^2-z^2";
//    private static final String x = "($)-(x+y)^2";
//    private static final String y = "($)-(x+z)^2";
//    private static final String z = "($)-(y+z)^2";
//    private static final String con = "$1*$2" + PLUSEPSILON;
//    private static final String sin = "$1*$2" + SUBEPSILON;
//    private static final String por = "$1^2+$2^2" + PLUSEPSILON;
//    private static final String yKeyword = "$1*$2";
//    private static final String oKeyword = "$1-$2";

//    private String getKeyword(String keyword) {
//        if ("con".equals(keyword)) {
//            return con;
//        } else if ("sin".equals(keyword)) {
//            return sin;
//        } else if ("por".equals(keyword)) {
//            return por;
//        } else if ("y".equals(keyword)) {
//            return yKeyword;
//        } else if ("o".equals(keyword)) {
//            return oKeyword;
//        } else {
//            return null;
//        }
//    }

//    private String getDefaultLetterRule(char ch) {
//        switch (Character.toLowerCase(ch)) {
//            case 'a':
//                return a;
//            case 'e':
//                return e;
//            case 'i':
//                return i;
//            case 'o':
//                return o;
//            case 'u':
//                return u;
//            case 'b':
//                return b;
//            case 'c':
//                return c;
//            case 'd':
//                return d;
//            case 'f':
//                return f;
//            case 'g':
//                return g;
//            case 'h':
//                return h;
//            case 'j':
//                return j;
//            case 'k':
//                return k;
//            case 'l':
//                return l;
//            case 'm':
//                return m;
//            case 'n':
//                return n;
//            case 'p':
//                return p;
//            case 'q':
//                return q;
//            case 'r':
//                return r;
//            case 's':
//                return s;
//            case 't':
//                return t;
//            case 'v':
//                return v;
//            case 'w':
//                return w;
//            case 'x':
//                return x;
//            case 'y':
//                return y;
//            case 'z':
//                return z;
//            default:
//                return "-1";
//        }
//    }

    private boolean isKeyWord(String key) {
        return this.keywords.containsKey(key);
    }

//    private void loadDefaultRules() {
//        for (Character letter : LETTERS) {
//            if (!this.rules.containsKey(letter.toString())) {
//                this.rules.setProperty(letter.toString(), getDefaultLetterRule(letter));
//            }
//        }
//    }

    private String toFormula(List<String> result) {
        if (result == null || result.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String each : result) {
            if (sb.length() != 0) {
                sb.append("+").append(each);
            } else {
                sb.append(each);
            }
        }
        return sb.toString();
    }

//    private void loadDefaultKeyWords() {
//        for (String each : KEYWORDS) {
//            if (!this.keywords.containsKey(each)) {
//                String keywordProperty = getKeyword(each);
//                if (keywordProperty != null) {
//                    this.keywords.setProperty(each, keywordProperty);
//                }
//            }
//        }
//    }

    private boolean isVowel(Character ch) {
        if ('a' == ch || 'e' == ch || 'i' == ch || 'o' == ch || 'u' == ch) {
            return true;
        }

        return false;
    }

    public String getRule(Character ch) {
        return this.rules.getProperty(ch.toString());
    }

    public String getKeywordRule(String ch) {
        return this.keywords.getProperty(ch.toString());
    }
}
