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
