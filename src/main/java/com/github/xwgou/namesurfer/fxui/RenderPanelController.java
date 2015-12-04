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

public class RenderPanelController implements Initializable {

    protected StringProperty formula;
    @FXML protected VBox vbox;
    @FXML protected ImageView imageView;

    final static Logger logger = LoggerFactory.getLogger( RenderPanelController.class );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NumberBinding size = Bindings.min( vbox.widthProperty(), vbox.heightProperty() );
        imageView.fitWidthProperty().bind( size );
        imageView.fitHeightProperty().bind( size );
        imageView.setImage( new Image( "http://www.algebraicsurface.net/images/AlgSurfTitlePic.jpg" ) );
    }

    @FXML protected void handleMousePressed( MouseEvent e ) {
        logger.debug( "{}", e );
        e.consume();
    }
    @FXML protected void handleMouseReleased( MouseEvent e ) { logger.debug( "{}", e ); e.consume(); }
    @FXML protected void handleMouseDragged( MouseEvent e ) { logger.debug( "{}", e ); e.consume(); }
    @FXML protected void handleScroll( ScrollEvent e ) { logger.debug( "{}", e ); e.consume(); }
    @FXML protected void handleZoom( ZoomEvent e ) { logger.debug( "{}", e ); e.consume(); }
}
