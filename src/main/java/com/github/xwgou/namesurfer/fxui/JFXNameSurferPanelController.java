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
package com.github.xwgou.namesurfer.fxui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.embed.swing.SwingFXUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

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

    @FXML protected Button exportButton;
    @FXML protected Button saveButton;

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
        renderPanel.formulaValidProperty().addListener(
            ( observable, oldVal, newVal ) -> {
                logger.debug( "formulaValid: {}", newVal );
                formulaTextField.setStyle( newVal ? "" : "-fx-background-color: red" );
            }
        );

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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export image");
        fileChooser.setInitialDirectory( new File( System.getProperty( "user.home" ) ) );
        fileChooser.setInitialFileName( nameTextField.getText() );
        fileChooser.getExtensionFilters().add( new ExtensionFilter("PNG image", "*.png") );
        File file = fileChooser.showSaveDialog( renderPanel.getScene().getWindow() );
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage( renderPanel.getImage(), null ), "png", file );
            } catch( IOException ex ) {
                logger.error( "{}", ex );
            }
        }
        e.consume();
    }

    @FXML void handleSave( ActionEvent e )
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save .jsurf");
        fileChooser.setInitialDirectory( new File( System.getProperty( "user.home" ) ) );
        fileChooser.setInitialFileName( nameTextField.getText() );
        fileChooser.getExtensionFilters().add( new ExtensionFilter("jSurf file", "*.jsurf") );
        File file = fileChooser.showSaveDialog( renderPanel.getScene().getWindow() );
        if (file != null) {
            try {
                renderPanel.getJSurf().store( new FileOutputStream( file ), "Created by NameSurfer v"
                    + JFXNameSurfer.class.getPackage().getImplementationVersion() );
            } catch( IOException ex ) {
                logger.error( "{}", ex );
            }
        }
        e.consume();
    }
}
