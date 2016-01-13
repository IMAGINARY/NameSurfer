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

    protected Node view;
    protected JFXNameSurferPanelController controller;

    public JFXNameSurferPanel()
    {
        this( new PinyinTranslator() );
    }

    public JFXNameSurferPanel( final ITranslator translator ) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/github/xwgou/namesurfer/fxml/JFXNameSurferPanel.fxml"));
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
