// how to make a custom component:
// http://stackoverflow.com/a/12681060/1496589
package com.github.xwgou.namesurfer.fxui;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class RenderPanel extends Pane {

    final static Logger logger = LoggerFactory.getLogger( RenderPanel.class );

    private Node view;
    private RenderPanelController controller;

    public RenderPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/RenderPanel.fxml"));
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return controller = new RenderPanelController();
            }
        });
        fxmlLoader.setRoot( this );
        try {
            view = (Node) fxmlLoader.load();
        } catch (IOException ex) {
            logger.error( null, ex );
        }
    }

    public void setFormula(String str) {
        controller.formula.setValue( str );
    }

    public String getFormula() {
        return controller.formula.getValue();
    }

    public StringProperty formulaProperty() {
        return controller.formula;
    }
}