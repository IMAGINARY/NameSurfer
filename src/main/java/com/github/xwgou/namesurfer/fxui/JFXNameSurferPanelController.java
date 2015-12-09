package com.github.xwgou.namesurfer.fxui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.application.Platform;

import de.mfo.jsurf.gui.JSurferRenderPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.xwgou.namesurfer.translator.ITranslator;
import com.github.xwgou.namesurfer.translator.PinyinTranslator;

public class JFXNameSurferPanelController implements Initializable {

    final static Logger logger = LoggerFactory.getLogger( JFXNameSurferPanelController.class );

    @FXML protected TextField nameTextField;
    @FXML protected TextField formulaTextField;
    @FXML protected RenderPanel renderPanel;

    @FXML protected CustomColorPicker ccp1;
    @FXML protected CustomColorPicker ccp2;

    final private ITranslator translator;

    public JFXNameSurferPanelController() {
        this( new PinyinTranslator() );
    }

    public JFXNameSurferPanelController( ITranslator translator ) {
        this.translator = translator;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // "final" is needed for bindings
        final TextField nameTextField = this.nameTextField;
        final TextField formulaTextField = this.formulaTextField;
        final CustomColorPicker ccp1 = this.ccp1;
        final CustomColorPicker ccp2 = this.ccp2;

        // conect text fields and renderer
        nameTextField.textProperty().addListener(
            ( observable, oldVal, newVal ) -> {
                formulaTextField.textProperty().setValue( translator.translate( newVal ) );
            }
        );
        renderPanel.formulaProperty().bind( formulaTextField.textProperty() );

        // set initial font and back colors and bind renderer to color pickers
        ccp1.setCustomColor( Color.web( "0xffbc00ff" ) );
        ccp2.setCustomColor( Color.web( "0x3e290aff" ) );
        renderPanel.frontColorProperty().bind( ccp1.customColorProperty() );
        renderPanel.backColorProperty().bind( ccp2.customColorProperty() );

        // focus something different than the text fields in order to make
        // their prompt texts visible
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                renderPanel.requestFocus();
            }
        });
    }

    @FXML void handleExportImage( ActionEvent e )
    {
        logger.debug( "Export image" );
        e.consume();
    }

    @FXML void handleSave( ActionEvent e )
    {
        logger.debug( "Save .jsurf" );
        e.consume();
    }
}
