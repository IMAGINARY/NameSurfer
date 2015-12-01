package com.github.xwgou.namesurfer.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Command argument options created in this class.
 *
 * @author xwgou
 */
public class JSurferOptions extends Options {
    public static String WIDTH = "w";
    public static String HEIGHT = "h";
    public static String RULES = "r";
    public static String KEYWORDS = "k";
    public static String FS = "fs";


    public JSurferOptions() {
        Option r = new Option( "r", "rules", true, "specify the word translation "
                + "rules as rules.properties. Example: "
                + "-r $yourpath/rules.properties" );
        //r.setRequired(true);

        Option k = new Option( "k", "key", true, "specify the keyword rules"
                + " translation as keywords.properties. Example: "
                + "-k $yourpath/keywords.properties" );
        //k.setRequired(true);
        this.addOption( "w", "width", true, "set width of the windows. "
                + "Example: -w 640" );
        this.addOption( "h", "height", true, "set height of the windows. "
                + "Example: -h 480" );
        this.addOption( "fs", "font-size", true, "set font size of "
                + "input expression. Example: -fs 20f" );
        this.addOption(r);
        this.addOption(k);


    }


}
