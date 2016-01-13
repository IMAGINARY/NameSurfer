/**
 * This file is part of NameSurfer.
 * Copyright (C) 2011-2016 Xin Wei Gou and Christian Stussak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.xwgou.namesurfer.translator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


/**
 *
 * @author xwgou
 */
public class PinyinTranslator extends  SyllableTranslator {


    public PinyinTranslator() {
        super();
    }

    public PinyinTranslator(String arg, String arg0) throws IOException {
        super(arg, arg0);
    }

    public PinyinTranslator(InputStream rulesStream, InputStream keywordsStream) throws IOException {
        super( rulesStream, keywordsStream );
    }

    @Override
    public String translate(String phrase) {
        if (phrase == null || phrase.isEmpty()) {
            return "";
        }

        HanyuPinyinOutputFormat pinyinFormat = new HanyuPinyinOutputFormat();

        pinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);


        String[] pinyin;

        StringBuilder result = new StringBuilder();
//        System.out.println("phrase: " + phrase + " size: " + phrase.length());
        int i;
        for (i = 0; i < phrase.length(); i++) {
            try {
                pinyin = PinyinHelper.toHanyuPinyinStringArray(phrase.charAt(i), pinyinFormat);
            } catch (BadHanyuPinyinOutputFormatCombination ex) {
                Logger.getLogger(PinyinTranslator.class.getName()).log(Level.SEVERE, null, ex);
                return super.translate(phrase);
            }
            if (pinyin == null) {
                // it's not chinese character
//                System.out.println("it's not chinese character");
                return super.translate(phrase);
            }

            result.append(Arrays.toString(pinyin).replaceAll("\\[", "").replaceAll("]", ""));

//            System.out.println("result: " + result.toString() + " | pinyin: + " +Arrays.toString(pinyin));

        }


//        System.out.println("phrase: " + phrase);

//        System.out.println("result: " + result.toString());

        return super.translate(result.toString());

    }






}
