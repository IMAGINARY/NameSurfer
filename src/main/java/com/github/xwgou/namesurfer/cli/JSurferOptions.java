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
package com.github.xwgou.namesurfer.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Command argument options created in this class.
 *
 * @author xwgou
 */
public class JSurferOptions extends Options {
    public static String HELP = "help";
    public static String VERSION = "version";
    public static String RULES = "r";
    public static String KEYWORDS = "k";
    public static String FULLSCREEN = "f";
    public static String DISABLE_BUTTONS = "disable-buttons";

    public JSurferOptions() {
        Option help = OptionBuilder.withLongOpt( "help" ).withDescription( "display this help text and exit" ).create();
        Option version = OptionBuilder.withLongOpt( "version" ).withDescription( "print program version and exit" ).create();
        Option disable_buttons = OptionBuilder.withLongOpt( "disable-buttons" ).withDescription( "disable the export and save buttons" ).create();

        Option r = new Option( "r", "rules", true, "specify the word translation "
                + "rules as rules.properties. Example: "
                + "-r $yourpath/rules.properties" );

        Option k = new Option( "k", "key", true, "specify the keyword rules"
                + " translation as keywords.properties. Example: "
                + "-k $yourpath/keywords.properties" );

        Option f = new Option( "f", "fullscreen", false, "run in full screen mode" );

        this.addOption( help );
        this.addOption( version );
        this.addOption( disable_buttons );
        this.addOption( r );
        this.addOption( k );
        this.addOption( f );
    }
}
