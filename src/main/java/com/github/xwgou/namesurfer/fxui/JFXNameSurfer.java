package com.github.xwgou.namesurfer.fxui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import jfxtras.labs.scene.layout.ScalableContentPane;

import java.io.InputStream;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.xwgou.namesurfer.cli.JSurferOptions;
import com.github.xwgou.namesurfer.translator.PinyinTranslator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class JFXNameSurfer extends Application {

    final static Logger logger = LoggerFactory.getLogger( JFXNameSurfer.class );

    static PinyinTranslator translator;

    @Override
    public void start (final Stage stage) {
        try
        {
            final ScalableContentPane scp = new ScalableContentPane();

            scp.setStyle("-fx-background-color: green;");
            Pane root = scp.getContentPane();

            final JFXNameSurferPanel rootNode = new JFXNameSurferPanel( translator );
            root.getChildren().add( rootNode );
            scp.widthProperty().addListener(
                new ChangeListener(){
                    @Override public void changed(ObservableValue o,Object oldVal, Object newVal)
                    {
                        scp.layout();
                        scp.getContentPane().layout();
                    }
                }
            );

            final Scene scene = new Scene(scp, 1024, 576);
            scene.getStylesheets().add(getClass().getResource("/com/github/xwgou/namesurfer/css/color.css").toExternalForm());

            scp.layoutBoundsProperty().addListener( (observable, oldValue, newValue) -> {
                double aspect_ratio = scene.getWidth() / scene.getHeight();
                logger.debug( scp.getLayoutBounds().toString() );
                logger.debug( "" + aspect_ratio );
                rootNode.setPrefWidth( 1024 );
                rootNode.setPrefHeight( 576 );
                if( aspect_ratio > 1024 / 576 )
                    rootNode.setPrefWidth( 576 * aspect_ratio );
                else
                    rootNode.setPrefHeight( 1024 / aspect_ratio );
            });

            stage.setTitle("NameSurfer");
            stage.setScene(scene);
            stage.show();
        }
        catch( Exception ex )
        {
            logger.error( "Exception during application startup", ex );
            throw ex;
        }
    }

    @Override
    public void stop() {
        logger.debug( Thread.getAllStackTraces().keySet().toString() );
        Platform.exit();
        System.exit( 0 );
    }

    public static void main(String[] args) {
        JSurferOptions options = new JSurferOptions();

        HelpFormatter formatter = new HelpFormatter();
        String cmd_line_syntax = "namesurfer [options]";
        String help_header = "NameSurfer renderes beautiful algebraic surfaces derived from your name.";
        String help_footer = "";

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse( options, args);

            if( cmd.hasOption( JSurferOptions.HELP ) )
			{
    			formatter.printHelp( cmd_line_syntax, help_header, options, help_footer );
    			return;
    		}

			if( cmd.hasOption( JSurferOptions.VERSION ) )
			{
				System.out.println( JFXNameSurfer.class.getPackage().getImplementationVersion() );
    			return;
    		}

            InputStream rules = PinyinTranslator.RULES_PATH.openStream();
            if (cmd.hasOption(JSurferOptions.RULES)) {
                rules = new FileInputStream( cmd.getOptionValue(JSurferOptions.RULES) );
            }

            InputStream keywords = PinyinTranslator.KEYWORDS_PATH.openStream();
            if (cmd.hasOption(JSurferOptions.KEYWORDS)) {
                keywords = new FileInputStream( cmd.getOptionValue(JSurferOptions.KEYWORDS) );
            }

            translator = new PinyinTranslator( rules, keywords );

            launch(args);
        } catch ( Throwable t ) {
            logger.error( "Exception during application startup", t );
            formatter.printHelp( "help", options);
            System.exit(-1);
        }
    }
}
