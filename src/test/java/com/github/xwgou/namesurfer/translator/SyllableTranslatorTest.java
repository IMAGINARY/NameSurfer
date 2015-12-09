/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.xwgou.namesurfer.translator;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author xwgou
 */
public class SyllableTranslatorTest {

    private ITranslator translator;

    @Test
    public void FooTest() {
        String[] words = "abc".split(" ");
        Assert.assertEquals("abc", words[0]);

        Character a = 'a';
        Assert.assertEquals("a", a.toString());

        String rule = "$^2-1";
        rule = rule.replaceAll("\\$", "X");
        Assert.assertEquals("X^2-1", rule);

        String rule2 = "$1+$2";
        rule2 = rule2.replaceAll("\\$1", "A");
        rule2 = rule2.replaceAll("\\$2", "B");
        Assert.assertEquals("A+B", rule2);


    }

    @Test
    public void translateTest() {
        init();

        String hola = translator.translate("hola");
        String que = translator.translate("que");
        String tal = translator.translate("tal");

        System.out.println(hola);
        System.out.println(que);
        System.out.println(tal);

        String all = translator.translate("hola que tal");
        System.out.println(all);

        String expected = hola + "+" + que + "+" + tal;

        Assert.assertEquals(expected.trim(), all.trim());
    }

    @Test
    public void translateWithKeywordsTest() {
        init();
        String hola = translator.translate("hola");
        String que = translator.translate("que");

        System.out.println(hola);
        System.out.println(que);

        String all = translator.translate("hola con que");
        System.out.println(all);

    }

    private void init() {
        translator = null;
        try {
            String rules = new File( this.getClass().getResource( "rules.properties" ).toURI() ).getCanonicalPath();
            String keywords = new File( this.getClass().getResource( "keywords.properties" ).toURI() ).getCanonicalPath();

            translator = new SyllableTranslator(rules, keywords);
        } catch (IOException ex) {
            Assert.fail("file not found, so set default rules and keymaps: " + ex.getMessage());
        } catch (java.net.URISyntaxException ex) {
            Assert.fail("could not get URI of rules or keymap" + ex.getMessage());
        }
    }

    @Test
    public void generalTest() {
        init();
        System.out.println(translator.translate("o"));
        System.out.println(translator.translate("u"));
        System.out.println(translator.translate("andreas"));
    }

    @Test
    public void filePropertiesTest() {
        init();
        System.out.println(translator.getRule('a'));
        System.out.println(translator.getKeywordRule("con"));
    }

    public static void main(String[] args) {
        SyllableTranslatorTest test = new SyllableTranslatorTest();
//        test.FooTest();
//        test.translateWithKeywordsTest();
//        test.generalTest();
        test.filePropertiesTest();
    }
}
