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
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import javax.vecmath.*;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;

import java.awt.image.*;
import java.awt.Point;

import de.mfo.jsurf.rendering.*;
import de.mfo.jsurf.rendering.cpu.*;
import de.mfo.jsurf.parser.*;
import de.mfo.jsurf.util.*;
import de.mfo.jsurf.algebra.*;
import static de.mfo.jsurf.rendering.cpu.CPUAlgebraicSurfaceRenderer.AntiAliasingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderPanelController implements Initializable {

    protected final StringProperty formula = new SimpleStringProperty( this, "formula", "0" );
    protected final BooleanProperty formulaValid = new SimpleBooleanProperty( this, "formulaValid", true );
    protected final ReadOnlyBooleanProperty formulaValidRO = BooleanProperty.readOnlyBooleanProperty( formulaValid );

    protected final ObjectProperty<javafx.scene.paint.Color> frontColor = new SimpleObjectProperty<javafx.scene.paint.Color>(this, "frontColor", javafx.scene.paint.Color.RED);
    protected final ObjectProperty<javafx.scene.paint.Color> backColor = new SimpleObjectProperty<javafx.scene.paint.Color>(this, "backColor", javafx.scene.paint.Color.GREY);

    @FXML protected Pane pane;
    @FXML protected ImageView imageView;


    final static Logger logger = LoggerFactory.getLogger( RenderPanelController.class );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final ImageView imageView = this.imageView;

        NumberBinding size = Bindings.min( pane.widthProperty(), pane.heightProperty() );
        size.addListener( ( observable, oldValue, newValue ) -> { rw.scheduleRepaint(); } );
        imageView.fitWidthProperty().bind( size );
        imageView.fitHeightProperty().bind( size );
        imageView.xProperty().bind( pane.widthProperty().subtract( size ).divide( 2 ) );
        imageView.yProperty().bind( pane.heightProperty().subtract( size ).divide( 2 ) );
        imageView.setImage( new WritableImage( 1, 1 ) );
        imageView.setScaleY( -1.0 );


        ChangeListener<Object> updater = (observable, oldValue, newValue) -> {
            Bounds b = imageView.localToScene( imageView.getBoundsInLocal());
            Dimension newDesiredRenderSize = new Dimension( ( int ) Math.round( b.getWidth() ), ( int ) Math.round( b.getHeight() ) );
            if( !newDesiredRenderSize.equals( desiredRenderSize ) )
            {
                desiredRenderSize = newDesiredRenderSize;
                rw.scheduleRepaint();
                logger.debug( "{}", desiredRenderSize );
            }
        };
        imageView.boundsInLocalProperty().addListener(updater);
        imageView.localToSceneTransformProperty().addListener(updater);

        asr = new CPUAlgebraicSurfaceRenderer();
        try {
            Properties jsurf = new Properties();
            jsurf.load( getClass().getResourceAsStream( "/com/github/xwgou/namesurfer/gui/CayleyCubic.jsurf" ) );
            FileFormat.load( jsurf, asr );
        } catch( Exception ex )
        {
            logger.error( "failed to load defaults", ex );
            throw new RuntimeException( ex );
        }
        setOptimalCameraDistance( asr.getCamera() );

        minLowResRenderSize = new Dimension( 150, 150 );
        maxLowResRenderSize = new Dimension( 512, 512 );
        desiredRenderSize = ( Dimension ) maxLowResRenderSize.clone();

        rsd = new RotateSphericalDragger();
        scale = new Matrix4d();
        scale.set( Math.pow( 10.0, 0.6 ) );

        currentSurfaceImage = null;
        rw = new RenderWorker();
        rw.start();
        rw.scheduleRepaint();

        formula.addListener( (observable, oldValue, newValue) -> {
            if( newValue != null && !newValue.isEmpty() )
            {
                try {
                    asr.setSurfaceFamily( newValue );
                    formulaValid.setValue( true );
                    rw.scheduleRepaint();
                } catch ( Exception e ) {
                    logger.error( "", e );
                    formulaValid.setValue( false );
                }
            }
        } );

        frontColor.addListener( (observable, oldValue, newValue) -> {
            de.mfo.jsurf.rendering.Material m = asr.getFrontMaterial();
            m.setColor( new Color3f( (float) newValue.getRed(), (float) newValue.getGreen(), (float) newValue.getBlue() ) );
            asr.setFrontMaterial( m );
            rw.scheduleRepaint();
        } );

        backColor.addListener( (observable, oldValue, newValue) -> {
            de.mfo.jsurf.rendering.Material m = asr.getBackMaterial();
            m.setColor( new Color3f( (float) newValue.getRed(), (float) newValue.getGreen(), (float) newValue.getBlue() ) );
            asr.setBackMaterial( m );
            rw.scheduleRepaint();
        } );
    }

    // used for dirty hack that still makes dragging working on some touchscreens which send mouse events in wrong order
    private boolean dragging = false;
    @FXML protected void handleMousePressed( MouseEvent e ) {
        logger.debug( "{}", e );
        dragging = true;
        rsd.startDrag( new Point( (int) e.getX(), (int) e.getY() ) );
        e.consume();
    }

    @FXML protected void handleMouseReleased( MouseEvent e ) {
        logger.debug( "{}", e ); e.consume();
        dragging = false;
        rsd.startDrag( new Point( (int) e.getX(), (int) e.getY() ) );
    }

    @FXML protected void handleMouseDragged( MouseEvent e ) {
        logger.debug( "{}", e );
        if( dragging )
        {
            rsd.dragTo( new Point( (int) e.getX(), (int) e.getY() ) );
            rw.scheduleRepaint();
        }
        e.consume();
    }

    @FXML protected void handleScroll( ScrollEvent e ) {
        scaleSurface( ( int ) ( e.getDeltaX() + e.getDeltaY() ) );
        logger.debug( "{}", e ); e.consume();
    }

    @FXML protected void handleZoom( ZoomEvent e ) {
        zoomSurface( e.getZoomFactor() );
        logger.debug( "{}", e ); e.consume();
    }

    public void repaint() {
        final ImgBuffer ib = currentSurfaceImage;
        if( ib.width > 0 && ib.height > 0)
        {
            final WritableImage wi = new WritableImage( ib.width, ib.height );
            wi.getPixelWriter().setPixels( 0, 0, ib.width, ib.height, PixelFormat.getIntArgbInstance() , ib.rgbBuffer, 0, ib.width );

            final ImageView iv = imageView;
            Platform.runLater( () -> {
                iv.setImage( wi );
                iv.setViewport( new Rectangle2D( 0.0, 0.0, ib.width, ib.height ) );
            });
        }
    }

    public void setScale( double scaleFactor )
    {
        if (scaleFactor<-2.0)scaleFactor=-2.0;
        if (scaleFactor>2.0)scaleFactor=2.0;

        scaleFactor= Math.pow( 10, scaleFactor);
        scale.setScale( scaleFactor );
        rw.scheduleRepaint();
    }

    public double getScale()
    {
        return Math.log10(this.scale.getScale());
    }

    protected void scaleSurface( int units )
    {
        this.setScale(this.getScale()-units/500.0 );
        rw.scheduleRepaint();
    }

    protected void zoomSurface( double amount )
    {
        scale.set( scale.m00 / amount );
        rw.scheduleRepaint();
    }

    static BufferedImage createBufferedImageFromRGB( ImgBuffer ib )
    {
        int w = ib.width;
        int h = ib.height;

        DirectColorModel colormodel = new DirectColorModel( 24, 0xff0000, 0xff00, 0xff );
        SampleModel sampleModel = colormodel.createCompatibleSampleModel( w, h );
        DataBufferInt data = new DataBufferInt( ib.rgbBuffer, w * h );
        WritableRaster raster = WritableRaster.createWritableRaster( sampleModel, data, new Point( 0, 0 ) );
        return new BufferedImage( colormodel, raster, false, null );
    }

    public void saveToPNG( java.io.File f )
            throws java.io.IOException
    {
        saveToPNG( f, currentSurfaceImage );
    }

    public static void saveToPNG( java.io.File f, ImgBuffer imgbuf )
            throws java.io.IOException
    {
        BufferedImage bufferedImage = createBufferedImageFromRGB( imgbuf );
        java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
        javax.imageio.ImageIO.write( bufferedImage, "png", f );
    }

    class ImgBuffer
    {
        public int[] rgbBuffer;
        public int width;
        public int height;

        public ImgBuffer( int w, int h ) { rgbBuffer = new int[ 3 * w * h ]; width = w; height = h; }
    }

    protected static void setOptimalCameraDistance( Camera c )
    {
        float cameraDistance;
        switch( c.getCameraType() )
        {
            case ORTHOGRAPHIC_CAMERA:
                cameraDistance = 1.0f;
                break;
            case PERSPECTIVE_CAMERA:
                cameraDistance = ( float ) ( 1.0 / Math.sin( ( Math.PI / 180.0 ) * ( c.getFoVY() / 2.0 ) ) );
                break;
            default:
                throw new RuntimeException();
        }
        c.lookAt( new Point3d( 0, 0, cameraDistance ), new Point3d( 0, 0, -1 ), new Vector3d( 0, 1, 0 ) );
    }

    CPUAlgebraicSurfaceRenderer asr;
    ImgBuffer currentSurfaceImage;
    boolean resizeImageWithComponent;
    boolean renderCoordinatenSystem;
    Dimension renderSize;
    Dimension desiredRenderSize;
    Dimension minLowResRenderSize;
    Dimension maxLowResRenderSize;
    RotateSphericalDragger rsd;
    Matrix4d scale;
    RenderWorker rw;

    class RenderWorker extends Thread
    {
        Semaphore semaphore = new Semaphore( 0 );
        boolean finish = false;
        boolean is_drawing_hi_res = false;
        double time_per_pixel = 1000.0;
        final double desired_fps = 15.0;
        boolean skip_hi_res = false;

        public void finish()
        {
            finish = true;
        }

        public void scheduleRepaint()
        {
            // schedule redraw
            semaphore.release();

            // try to ensure, that high resolution drawing is canceled
            if( is_drawing_hi_res )
                RenderPanelController.this.asr.stopDrawing();
        }

        public void stopHighResolutionRendering()
        {
            semaphore.drainPermits(); // remove all currently available permits
            skip_hi_res = true;

            // try to ensure, that current high resolution rendering is canceled
            if( is_drawing_hi_res )
                RenderPanelController.this.asr.stopDrawing();
        }

        @Override
        public void run()
        {
            this.setPriority( Thread.MIN_PRIORITY );
            while( !finish )
            {
                try
                {
                    int available_permits = semaphore.availablePermits();
                    semaphore.acquire( Math.max( 1, available_permits ) ); // wait for new task and grab all permits
                    skip_hi_res = false;
                    long minPixels = RenderPanelController.this.minLowResRenderSize.width *RenderPanelController.this.minLowResRenderSize.height;
                    long maxPixels =RenderPanelController.this.maxLowResRenderSize.width *RenderPanelController.this.maxLowResRenderSize.height;
                    maxPixels = Math.max( 1, Math.min( maxPixels, (long) ( RenderPanelController.this.desiredRenderSize.getWidth() *RenderPanelController.this.desiredRenderSize.getHeight() ) ) );
                    minPixels = Math.min( minPixels, maxPixels );
                    long numPixelsAt15FPS = ( long ) ( 1.0 / ( desired_fps * time_per_pixel ) );
                    long pixelsToUse = Math.max( minPixels, Math.min( maxPixels, numPixelsAt15FPS ) );
                   RenderPanelController.this.renderSize = new Dimension( (int) Math.sqrt( pixelsToUse ), (int) Math.sqrt( pixelsToUse ) );

                    // render low res
                    {
                        ImgBuffer ib = draw( renderSize.width, renderSize.height, AntiAliasingMode.ADAPTIVE_SUPERSAMPLING, AntiAliasingPattern.QUINCUNX, true );
                        if( ib != null )
                        {
                            currentSurfaceImage =  ib;
                           RenderPanelController.this.repaint();
                        }
                    }

                    if( semaphore.tryAcquire( 100, TimeUnit.MILLISECONDS ) ) // wait some time, then start with high res drawing
                    {
                        semaphore.release();
                        continue;
                    }
                    else if( skip_hi_res )
                        continue;

                    // render high res, if no new low res rendering is scheduled
                    {
                        is_drawing_hi_res = true;
                        ImgBuffer ib = draw( (int) RenderPanelController.this.desiredRenderSize.getWidth(),(int)RenderPanelController.this.desiredRenderSize.getHeight(), AntiAliasingMode.ADAPTIVE_SUPERSAMPLING, AntiAliasingPattern.OG_4x4, false );
                        if( ib != null )
                        {
                            currentSurfaceImage =  ib;
                           RenderPanelController.this.repaint();
                        }
                        is_drawing_hi_res = false;
                    }

                    if( semaphore.availablePermits() > 0 ) // restart, if user has changes the view
                        continue;
                    else if( skip_hi_res )
                        continue;

                    // render high res with even better quality
                    {
                        //System.out.println( "drawing hi res");
                        is_drawing_hi_res = true;
                        ImgBuffer ib = draw((int)RenderPanelController.this.desiredRenderSize.getWidth(),(int)RenderPanelController.this.desiredRenderSize.getHeight(), AntiAliasingMode.SUPERSAMPLING, AntiAliasingPattern.OG_4x4, false );
                        if( ib != null )
                        {
                            currentSurfaceImage =  ib;
                           RenderPanelController.this.repaint();
                        }
                        is_drawing_hi_res = false;
                        //System.out.println( "finised hi res");
                    }
                }
                catch( InterruptedException ie )
                {
                }
            }
        }

        public ImgBuffer draw( int width, int height, CPUAlgebraicSurfaceRenderer.AntiAliasingMode aam, AntiAliasingPattern aap )
        {
            return draw( width, height, aam, aap, false );
        }

        public ImgBuffer draw( int width, int height, CPUAlgebraicSurfaceRenderer.AntiAliasingMode aam, AntiAliasingPattern aap, boolean save_fps )
        {
            RenderPanelController.logger.debug( "{}x{}", width, height );
            // create color buffer
            ImgBuffer ib = new ImgBuffer( width, height );

            // do rendering
            Matrix4d rotation = new Matrix4d();
            rotation.invert( rsd.getRotation() );
            Matrix4d id = new Matrix4d();
            id.setIdentity();
            Matrix4d tm = new Matrix4d( rsd.getRotation() );
            tm.mul( scale );
            asr.setTransform( rsd.getRotation() );
            asr.setSurfaceTransform( scale );
            asr.setAntiAliasingMode( aam );
            asr.setAntiAliasingPattern( aap );
            setOptimalCameraDistance( asr.getCamera() );

            try
            {
                long t_start = System.nanoTime();
                asr.draw( ib.rgbBuffer, width, height );
                long t_end = System.nanoTime();
                double fps = 1000000000.0 / ( t_end - t_start );
                System.err.println( fps + "fps at " + width +"x" + height );
                if( save_fps )
                    time_per_pixel = ( ( t_end - t_start ) / 1000000000.0 ) / ( width * height );
                return ib;
            }
            catch( RenderingInterruptedException rie )
            {
                return null;
            }
            catch( Throwable t )
            {
                t.printStackTrace();
                return null;
            }
        }
    }

}
