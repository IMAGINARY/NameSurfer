package com.github.xwgou.namesurfer.fxui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import jfxtras.labs.scene.layout.ScalableContentPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.xwgou.namesurfer.cli.JSurferOptions;
import com.github.xwgou.namesurfer.translator.PinyinTranslator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
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
            scene.getStylesheets().add(getClass().getResource("../css/color.css").toExternalForm());

            System.out.println(translator.translate( "Christian Stussak" ));

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
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse( options, args);

            String rules = PinyinTranslator.RULES_PATH;
            if (cmd.hasOption(JSurferOptions.RULES)) {
                rules = cmd.getOptionValue(JSurferOptions.RULES);
            }

            String keywords = PinyinTranslator.KEYWORDS_PATH;
            if (cmd.hasOption(JSurferOptions.KEYWORDS)) {
                keywords = cmd.getOptionValue(JSurferOptions.KEYWORDS);
            }

            translator = new PinyinTranslator( rules, keywords );

            launch(args);
        } catch ( Throwable t ) {
            logger.error( "Exception during application startup", t );
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "help", options);
            System.exit(-1);
        }
    }
}
