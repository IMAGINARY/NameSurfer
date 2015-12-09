package com.github.xwgou.namesurfer.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Command argument options created in this class.
 *
 * @author xwgou
 */
public class JSurferOptions extends Options {
    public static String RULES = "r";
    public static String KEYWORDS = "k";
    
    public JSurferOptions() {
        Option r = new Option( "r", "rules", true, "specify the word translation "
                + "rules as rules.properties. Example: "
                + "-r $yourpath/rules.properties" );

        Option k = new Option( "k", "key", true, "specify the keyword rules"
                + " translation as keywords.properties. Example: "
                + "-k $yourpath/keywords.properties" );

        this.addOption(r);
        this.addOption(k);
    }
}
