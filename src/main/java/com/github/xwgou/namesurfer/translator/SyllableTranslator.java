/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.xwgou.namesurfer.translator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author xwgou
 */
public class SyllableTranslator implements ITranslator {

    private Properties rules;
    private Properties keywords;
    private static final String SPACE_SEPARATOR = " ";
    private static final String SUBEPSILON = "0";

    private static final String RULES_PATH = "rules.properties";
    private static final String KEYWORDS_PATH = "keywords.properties";

    public SyllableTranslator() throws IOException {
        this.rules = new Properties();
        this.keywords = new Properties();

        this.rules.load(new FileInputStream(RULES_PATH));
        this.keywords.load(new FileInputStream(KEYWORDS_PATH));
    }

    public SyllableTranslator(String rulesFile, String keywordsFile) throws IOException {
        this.rules = new Properties();
        this.keywords = new Properties();

        this.rules.load(new FileInputStream(rulesFile));
        this.keywords.load(new FileInputStream(keywordsFile));
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

    private boolean isKeyWord(String key) {
        return this.keywords.containsKey(key);
    }

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
