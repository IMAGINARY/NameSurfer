package com.github.xwgou.namesurfer.fxui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Bounds;
import javafx.scene.control.TextField;

import jfxtras.labs.scene.layout.ScalableContentPane;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.mfo.jsurf.gui.JSurferRenderPanel;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.xwgou.namesurfer.cli.JSurferOptions;
import com.github.xwgou.namesurfer.translator.ITranslator;
import com.github.xwgou.namesurfer.translator.SyllableTranslator;
import com.github.xwgou.namesurfer.translator.PinyinTranslator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class JFXNameSurferPanel extends Application {

    final static Logger logger = LoggerFactory.getLogger( JFXNameSurferPanel.class );
    static ITranslator translator;

    @Override
    public void start (final Stage stage) {
        try
        {
            final ScalableContentPane scp = new ScalableContentPane();

            scp.setStyle("-fx-background-color: green;");
            Pane root = scp.getContentPane();

            Node rootNode = null;

            try {
                FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource( "../fxml/JFXNameSurferPanel.fxml" ) );
                rootNode = fxmlLoader.load();
            } catch ( IOException ex ) {
                logger.error( "Unable to UI file JFXNameSurferPanel.fxml.", ex );
            }

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

            // connect name input field to formula input field
            final TextField nameTextField = ( TextField ) scene.lookup( "#name" );
            final TextField formulaTextField = ( TextField ) scene.lookup( "#formula" );
            final RenderPanel renderPanel = ( RenderPanel ) scene.lookup( "#renderPanel" );

            System.out.println(translator.translate( "Christian Stussak" ));

            formulaTextField.textProperty().bind(
                Bindings.createStringBinding(
                    () -> translator.translate( nameTextField.getText() ),
                    nameTextField.textProperty()
                )
            );
            renderPanel.formulaProperty().bind( formulaTextField.textProperty() );

            CustomColorPicker ccp_1 = ( CustomColorPicker ) scene.lookup( "#ccp1" );
            renderPanel.frontColorProperty().bind( ccp_1.customColorProperty() );
            CustomColorPicker ccp_2 = ( CustomColorPicker ) scene.lookup( "#ccp2" );
            renderPanel.backColorProperty().bind( ccp_2.customColorProperty() );

/*
            // add the renderer to the scene and ensure quadratic size
            final SwingNode jsurferRenderPanelWrapper = ( SwingNode ) scene.lookup( "#JSurferRenderPanelWrapper" );
            jsurferRenderPanelWrapper.setContent( renderer );
            final Pane swingNodeWrapper = ( Pane ) scene.lookup( "#SwingNodeWrapper" );
            final Pane swingNodeWrapper2 = ( Pane ) scene.lookup( "#SwingNodeWrapper2" );
            final Pane swingNodeWrapper3 = ( Pane ) scene.lookup( "#SwingNodeWrapper3" );
            swingNodeWrapper.prefWidthProperty().bind( Bindings.min( swingNodeWrapper2.widthProperty(), swingNodeWrapper2.heightProperty() ) );
            swingNodeWrapper.prefHeightProperty().bind( Bindings.min( swingNodeWrapper2.widthProperty(), swingNodeWrapper2.heightProperty() ) );
*/
/*
            swingNodeWrapper.layoutBoundsProperty().addListener( (observable, oldValue, newValue) -> {
                renderer.setSize( new java.awt.Dimension( (int) newValue.getHeight(), (int) newValue.getHeight() ) );
            });
*/

            final VBox topNode = ( VBox ) scene.lookup( "#topNode" );
            scp.layoutBoundsProperty().addListener( (observable, oldValue, newValue) -> {
                double aspect_ratio = scene.getWidth() / scene.getHeight();
                logger.debug( scp.getLayoutBounds().toString() );
                logger.debug( "" + aspect_ratio );
                topNode.setPrefWidth( 1024 );
                topNode.setPrefHeight( 576 );
                if( aspect_ratio > 1024 / 576 )
                    topNode.setPrefWidth( 576 * aspect_ratio );
                else
                    topNode.setPrefHeight( 1024 / aspect_ratio );
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

            String rules = new File( SyllableTranslator.class.getResource( "rules.properties" ).toURI() ).getCanonicalPath();
            if (cmd.hasOption(JSurferOptions.RULES)) {
                rules = cmd.getOptionValue(JSurferOptions.RULES);
            }

            String keywords = new File( SyllableTranslator.class.getResource( "keywords.properties" ).toURI() ).getCanonicalPath();
            if (cmd.hasOption(JSurferOptions.KEYWORDS)) {
                keywords = cmd.getOptionValue(JSurferOptions.KEYWORDS);
            }

            //translator = new PinyinTranslator( rules, keywords );
            translator = new SyllableTranslator( rules, keywords );

            launch(args);
        } catch ( Throwable t ) {
            logger.error( "Exception during application startup", t );
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "help", options);
            System.exit(-1);
        }
    }
}
