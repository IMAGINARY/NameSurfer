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

    public JSurferOptions() {
        Option help = OptionBuilder.withLongOpt( "help" ).withDescription( "display this help text and exit" ).create();
        Option version = OptionBuilder.withLongOpt( "version" ).withDescription( "print program version and exit" ).create();

        Option r = new Option( "r", "rules", true, "specify the word translation "
                + "rules as rules.properties. Example: "
                + "-r $yourpath/rules.properties" );

        Option k = new Option( "k", "key", true, "specify the keyword rules"
                + " translation as keywords.properties. Example: "
                + "-k $yourpath/keywords.properties" );

        Option f = new Option( "f", "fullscreen", false, "run in full screen mode" );

        this.addOption( help );
        this.addOption( version );
        this.addOption( r );
        this.addOption( k );
        this.addOption( f );
    }
}
