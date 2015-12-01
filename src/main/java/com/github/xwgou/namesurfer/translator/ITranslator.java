/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.xwgou.namesurfer.translator;

/**
 * Interface to translate phrases into math expression.
 *
 * @author xwgou
 */
public interface ITranslator {
    /**
     * Translate given phrase
     * @param phrase
     * @return valid math expression
     */
    public String translate(String phrase);

    /**
     * Get given rule by character
     * @param ch character
     * @return rule as string
     */
    public String getRule(Character ch);

    /**
     * Get given keyword rule
     * @param keyword
     * @return rule as string
     */
    public String getKeywordRule(String keyword);
}
