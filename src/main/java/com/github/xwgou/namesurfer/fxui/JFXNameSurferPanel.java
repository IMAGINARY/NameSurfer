package com.github.xwgou.namesurfer.fxui;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

import java.io.IOException;

import com.github.xwgou.namesurfer.translator.ITranslator;
import com.github.xwgou.namesurfer.translator.PinyinTranslator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JFXNameSurferPanel extends GridPane {

    final static Logger logger = LoggerFactory.getLogger( JFXNameSurferPanel.class );

    private Node view;
    private JFXNameSurferPanelController controller;

    public JFXNameSurferPanel()
    {
        this( new PinyinTranslator() );
    }

    public JFXNameSurferPanel( final ITranslator translator ) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/JFXNameSurferPanel.fxml"));
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return controller = new JFXNameSurferPanelController( translator );
            }
        });
        fxmlLoader.setRoot( this );
        try {
            view = (Node) fxmlLoader.load();
        } catch (IOException ex) {
            logger.error( null, ex );
        }
    }
}
