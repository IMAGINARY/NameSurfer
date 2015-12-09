package com.github.xwgou.namesurfer.gui;


import com.github.xwgou.namesurfer.translator.ITranslator;
import com.github.xwgou.namesurfer.translator.PinyinTranslator;
import com.github.xwgou.namesurfer.translator.SyllableTranslator;
import com.github.xwgou.namesurfer.cli.JSurferOptions;

import de.mfo.jsurf.rendering.*;
import de.mfo.jsurf.parser.*;
import de.mfo.jsurf.algebra.*;
import de.mfo.jsurf.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.bric.swing.ColorPicker;
import javax.vecmath.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Christian Stussak <christian at knorf.de>
 */
public class JSurferPanel extends JPanel {

    private static int DEFAULT_FRAME_WIDTH = 300;
    private static int DEFAULT_FRAME_HEIGHT = 300;
    private static Options options;

    /**
     * Verify received argument must has two non-empty string as file path.
     * @param args
     * @return true if argument is not null and has two non-empty string.
     */
    private static boolean isValidArgs(String[] args) {
        if (args == null || args.length != 2) {
            return false;
        }

        String ruleFile = args[0];
        String keyFile = args[1];

        if (ruleFile == null || ruleFile.isEmpty()
                || keyFile == null || keyFile.isEmpty()) {
            return false;
        }

        return true;
    }

    public enum Layout {

        FIRST {

            void layoutComponents(JSurferPanel jsp) {
                layoutComponentsSecond(jsp);
            }
        },
        SECOND {

            void layoutComponents(JSurferPanel jsp) {
                layoutComponentsSecond(jsp);
            }
        },
        FIRST_WITH_SAVE(true) {

            void layoutComponents(JSurferPanel jsp) {
                layoutComponentsSecond(jsp);
            }
        },
        SECOND_WITH_SAVE(true) {

            void layoutComponents(JSurferPanel jsp) {
                layoutComponentsSecond(jsp);
            }
        },
        SECOND_WITH_SAVE_LANGUAGE(true) {

            void layoutComponents(JSurferPanel jsp) {
//                layoutComponentsSecond(jsp);
                layoutComponentsThird(jsp);
            }
        };

        Layout() {
            this.showSaveButton = false;
        }

        Layout(boolean showSaveButton) {
            this.showSaveButton = showSaveButton;
        }

        abstract void layoutComponents(JSurferPanel jsp);

        void layoutComponentsFirst(JSurferPanel jsp) {
            jsp.setBackground(Color.WHITE);
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(7);
            borderLayout.setVgap(7);
            jsp.setLayout(borderLayout);

            // init text field panel for surface expression
            JPanel surfaceExpressionPanel = new JPanel();
            surfaceExpressionPanel.setOpaque(false);
            surfaceExpressionPanel.setLayout(new BorderLayout());
            JLabel surfaceExpressionLabel = new JLabel("0 = ");
            surfaceExpressionLabel.setFont(jsp.surfaceExpression.getFont());
            surfaceExpressionPanel.add(surfaceExpressionLabel, BorderLayout.WEST);
            surfaceExpressionPanel.add(jsp.surfaceExpression, BorderLayout.CENTER);
            if (showSaveButton) {
                surfaceExpressionPanel.add(jsp.saveButton, BorderLayout.EAST);
            }

            // init front color picker panel
            JPanel frontColorPickerPanel = new JPanel();
            frontColorPickerPanel.setLayout(new BorderLayout());
            frontColorPickerPanel.setOpaque(false);
            frontColorPickerPanel.add(jsp.frontColorPicker, BorderLayout.SOUTH);

            // init back color picker panel
            JPanel backColorPickerPanel = new JPanel();
            backColorPickerPanel.setLayout(new BorderLayout());
            backColorPickerPanel.setOpaque(false);
            backColorPickerPanel.add(jsp.backColorPicker, BorderLayout.SOUTH);

            jsp.add(surfaceExpressionPanel, BorderLayout.SOUTH);
            jsp.add(frontColorPickerPanel, BorderLayout.WEST);
            jsp.add(backColorPickerPanel, BorderLayout.EAST);
            jsp.add(jsp.renderer, BorderLayout.CENTER);
            jsp.validate();
        }

        void layoutComponentsSecond(JSurferPanel jsp) {
            jsp.setBackground(Color.WHITE);
            jsp.setLayout(new BorderLayout(7, 7));

            // init text field panel for surface expression
            JPanel surfaceExpressionPanel = new JPanel(new BorderLayout());
            surfaceExpressionPanel.setOpaque(false);
            JLabel surfaceExpressionLabel = new JLabel("0 = ");
            surfaceExpressionLabel.setFont(jsp.surfaceExpression.getFont());
            surfaceExpressionPanel.add(surfaceExpressionLabel, BorderLayout.WEST);
            surfaceExpressionPanel.add(jsp.surfaceExpression, BorderLayout.CENTER);
            if (showSaveButton) {
                surfaceExpressionPanel.add(jsp.saveButton, BorderLayout.EAST);
            }

            // init front color picker panel
            JPanel colorPickerPanel = new JPanel();
            colorPickerPanel.setLayout(new BoxLayout(colorPickerPanel, BoxLayout.Y_AXIS));
            colorPickerPanel.setOpaque(false);
            colorPickerPanel.add(jsp.frontColorPicker);
            colorPickerPanel.add(jsp.backColorPicker);
            JPanel colorPickerPanel2 = new JPanel(new BorderLayout());
            colorPickerPanel2.setOpaque(false);
            JPanel dummyPanel = new JPanel();
            dummyPanel.setOpaque(false);
            colorPickerPanel2.add(dummyPanel, BorderLayout.CENTER);
            colorPickerPanel2.add(colorPickerPanel, BorderLayout.SOUTH);

            jsp.add(surfaceExpressionPanel, BorderLayout.SOUTH);
            jsp.add(colorPickerPanel2, BorderLayout.EAST);
            jsp.add(jsp.renderer, BorderLayout.CENTER);

            jsp.validate();
        }

        void layoutComponentsThird(JSurferPanel jsp) {
            jsp.setBackground(Color.WHITE);
            jsp.setLayout(new BorderLayout(7, 7));

            // init text field panel for surface expression
            JPanel surfaceExpressionPanel = new JPanel(new BorderLayout());
            surfaceExpressionPanel.setOpaque(false);
            JLabel surfaceExpressionLabel = new JLabel("0 = ");
            surfaceExpressionLabel.setFont(jsp.surfaceExpression.getFont());
            surfaceExpressionPanel.add(surfaceExpressionLabel, BorderLayout.WEST);
            surfaceExpressionPanel.add(jsp.surfaceExpression, BorderLayout.CENTER);
            if (showSaveButton) {
                surfaceExpressionPanel.add(jsp.saveButton, BorderLayout.EAST);
            }

            surfaceExpressionPanel.add(jsp.surfaceLanguageExpression, BorderLayout.NORTH);

            // init front color picker panel
            JPanel colorPickerPanel = new JPanel();
            colorPickerPanel.setLayout(new BoxLayout(colorPickerPanel, BoxLayout.Y_AXIS));
            colorPickerPanel.setOpaque(false);
            colorPickerPanel.add(jsp.frontColorPicker);
            colorPickerPanel.add(jsp.backColorPicker);
            JPanel colorPickerPanel2 = new JPanel(new BorderLayout());
            colorPickerPanel2.setOpaque(false);
            JPanel dummyPanel = new JPanel();
            dummyPanel.setOpaque(false);
            colorPickerPanel2.add(dummyPanel, BorderLayout.CENTER);
            colorPickerPanel2.add(colorPickerPanel, BorderLayout.SOUTH);

            jsp.add(surfaceExpressionPanel, BorderLayout.SOUTH);
            jsp.add(colorPickerPanel2, BorderLayout.EAST);
            jsp.add(jsp.renderer, BorderLayout.CENTER);

            jsp.validate();

        }

        boolean showSaveButton;
    }

    private static ITranslator translator;
    private JTextField surfaceLanguageExpression;
    private JTextField surfaceExpression;
    private ColorPicker frontColorPicker;
    private ColorPicker backColorPicker;
    private JSurferRenderPanel renderer;
    private ResourceBundle strings;
    private JButton saveButton;

    private static CommandLine cmd;

    public JSurferPanel() {
        this(Layout.FIRST);
    }

    public JSurferPanel(Layout l) {
        super();
        strings = ResourceBundle.getBundle("com.github.xwgou.namesurfer.namesurfer");

        initComponents();
        l.layoutComponents(this);
        initMaterials();
        initLights();
        frontColorChanged();
        backColorChanged();
        surfaceExpressionChanged();
    }

    private void initComponents() {

        surfaceLanguageExpression = new JTextField();
        surfaceLanguageExpression.setText("");
        Font font = surfaceLanguageExpression.getFont();
        if (cmd.hasOption(JSurferOptions.FS)) {
            font = surfaceLanguageExpression.getFont().deriveFont(Float.valueOf(cmd.getOptionValue(JSurferOptions.FS)));
        }
        surfaceLanguageExpression.setFont(font);
        surfaceLanguageExpression.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                action();
            }

            public void removeUpdate(DocumentEvent e) {
                action();
            }

            public void changedUpdate(DocumentEvent e) {
                action();
            }

            private void action() {
                surfaceExpression.setText(translator.translate(surfaceLanguageExpression.getText()));
            }
        });

        // init text field for surface expression
        surfaceExpression = new JTextField();
        surfaceExpression.setText("x^2+y^2+z^2+2*x*y*z-1");
        surfaceExpression.setFont(font);
        surfaceExpression.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                surfaceExpressionChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                surfaceExpressionChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                surfaceExpressionChanged();
            }
        });

        // init front color picker panel
        frontColorPicker = new ColorPicker(false, false);
        frontColorPicker.setOpaque(false);
        frontColorPicker.setForeground(Color.WHITE);
        frontColorPicker.setPreferredSize(new Dimension(150, 150));
        for (Component c : frontColorPicker.getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(false);
            }
        }
        frontColorPicker.getColorPanel().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                frontColorChanged();
            }
        });
        TitledBorder ftb = BorderFactory.createTitledBorder(strings.getString("frontColor"));
        ftb.setTitleJustification(TitledBorder.CENTER);
        ftb.setBorder(BorderFactory.createEmptyBorder());
        frontColorPicker.setBorder(ftb);

        // init back color picker panel
        backColorPicker = new ColorPicker(false,false);
        backColorPicker.setOpaque(false);
        backColorPicker.setForeground(Color.WHITE);
        backColorPicker.setPreferredSize(new Dimension(150, 150));
        for (Component c : backColorPicker.getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(false);
            }
        }
        backColorPicker.getColorPanel().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                backColorChanged();
            }
        });
        TitledBorder btb = BorderFactory.createTitledBorder(strings.getString("backColor"));
        btb.setTitleJustification(TitledBorder.CENTER);
        btb.setBorder(BorderFactory.createEmptyBorder());
        backColorPicker.setBorder(btb);

        saveButton = new JButton("Save image");
        saveButton.setToolTipText("Save the currently displayed image to a PNG file.");
        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveToPNG();
            }
        });

        renderer = new JSurferRenderPanel();
        renderer.setScale( 0.6 );
        //renderer.setResizeImageWithComponent( true );
    }

    private void initMaterials() {
        // init front material
        Material frontMaterial = new Material();
        frontMaterial = new Material();
        frontMaterial.setColor(new Color3f(0.70588f, 0.22745f, 0.14117f));
        frontMaterial.setAmbientIntensity(0.4f);
        frontMaterial.setDiffuseIntensity(0.8f);
        frontMaterial.setSpecularIntensity(0.5f);
        frontMaterial.setShininess(30);
        renderer.getAlgebraicSurfaceRenderer().setFrontMaterial(frontMaterial);

        // init front color picker start value
        Color cf = frontMaterial.getColor().get();
        frontColorPicker.setRGB(cf.getRed(), cf.getGreen(), cf.getBlue());

        // init back material
        Material backMaterial = new Material();
        backMaterial = new Material();
        backMaterial.setColor(new Color3f(1.0f, 0.8f, 0.4f));
        backMaterial.setAmbientIntensity(0.4f);
        backMaterial.setDiffuseIntensity(0.8f);
        backMaterial.setSpecularIntensity(0.5f);
        backMaterial.setShininess(30);
        renderer.getAlgebraicSurfaceRenderer().setBackMaterial(backMaterial);

        // init back color picker start value
        Color cb = backMaterial.getColor().get();
        backColorPicker.setRGB(cb.getRed(), cb.getGreen(), cb.getBlue());
    }

    private void initLights() {
        LightSource[] lights = new LightSource[AlgebraicSurfaceRenderer.MAX_LIGHTS];

        lights[ 0] = new LightSource();
        lights[ 0].setPosition(new Point3d(-100.0, 100.0, 100.0));
        lights[ 0].setIntensity(0.5f);
        lights[ 0].setColor(new Color3f(1f, 1f, 1f));

        lights[ 1] = new LightSource();
        lights[ 1].setPosition(new Point3d(100.0, 100.0, 100.0));
        lights[ 1].setIntensity(0.7f);
        lights[ 1].setColor(new Color3f(1f, 1f, 1f));

        lights[ 2] = new LightSource();
        lights[ 2].setPosition(new Point3d(0f, -100f, 100f));
        lights[ 2].setIntensity(0.3f);
        lights[ 2].setColor(new Color3f(1f, 1f, 1f));

        for (int i = 0; i < lights.length; i++) {
            renderer.getAlgebraicSurfaceRenderer().setLightSource(i, lights[ i]);
        }
    }

    private void frontColorChanged() {
        Material m = renderer.getAlgebraicSurfaceRenderer().getFrontMaterial();
        m.setColor(new Color3f(frontColorPicker.getColor()));
        renderer.getAlgebraicSurfaceRenderer().setFrontMaterial(m);
        renderer.repaintImage();
    }

    private void backColorChanged() {
        Material m = renderer.getAlgebraicSurfaceRenderer().getBackMaterial();
        m.setColor(new Color3f(backColorPicker.getColor()));
        renderer.getAlgebraicSurfaceRenderer().setBackMaterial(m);
        renderer.repaintImage();
    }

    private void surfaceExpressionChanged() {
        try {
            PolynomialOperation p = AlgebraicExpressionParser.parse(surfaceExpression.getText());

            // current version does not support surface parameters
            if (p.accept(new DoubleVariableChecker(), (Void) null)) {
                throw new Exception();
            }

            renderer.getAlgebraicSurfaceRenderer().setSurfaceExpression(p);
            renderer.repaintImage();
            surfaceExpression.setBackground(Color.WHITE);
        } catch (Exception e) {
            surfaceExpression.setBackground(new Color(255, 90, 90).brighter());
        }
    }

    public void setSurfaceExpression(String s) {
        surfaceExpression.setText(s);
    }

    public void saveToPNG() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        PNGFilter pngFilter = new PNGFilter();
        fc.addChoosableFileFilter(pngFilter);

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.io.File f = fc.getSelectedFile();
            f = PNGFilter.ensureExtension(f);
            try {
                renderer.saveToPNG(f, 1024, 1024);
            } catch (java.lang.Exception e) {
                String message = "Could not save to file \"" + f.getName() + "\".";
                if (e.getMessage() != null) {
                    message += "\n\nMessage: " + e.getMessage();
                }
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.OK_OPTION);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Layout layout;

            options = new JSurferOptions();
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse( options, args);

            layout = Layout.SECOND_WITH_SAVE_LANGUAGE;

            JFrame f = new JFrame("jSurfer - www.imaginary-exhibition.com");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JSurferPanel jsp = new JSurferPanel(layout);
            jsp.setBorder(new EmptyBorder(10, 10, 10, 10));
            f.getContentPane().add(jsp);
            f.pack();

            Dimension d = new Dimension(DEFAULT_FRAME_WIDTH,
                    DEFAULT_FRAME_HEIGHT);

            d.width = Integer.parseInt(cmd.getOptionValue(JSurferOptions.WIDTH, "640"));
            d.height = Integer.parseInt(cmd.getOptionValue(JSurferOptions.HEIGHT, "480"));

            String rules = new File( SyllableTranslator.class.getResource( "rules.properties" ).toURI() ).getCanonicalPath();
            if (cmd.hasOption(JSurferOptions.RULES)) {
                rules = cmd.getOptionValue(JSurferOptions.RULES);
            }

            String keywords = new File( SyllableTranslator.class.getResource( "keywords.properties" ).toURI() ).getCanonicalPath();
            if (cmd.hasOption(JSurferOptions.KEYWORDS)) {
            	keywords = cmd.getOptionValue(JSurferOptions.KEYWORDS);
            }

            f.setSize(d);
            f.setVisible(true);

            JSurferPanel.translator = new PinyinTranslator(rules, keywords);

        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "help", options);
            System.exit(-1);
        } catch (HeadlessException ex) {
            System.out.println(ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "help", options);
            System.exit(-1);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "help", options);
            System.exit(-1);
        } catch (java.net.URISyntaxException ex) {
            System.out.println(ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "help", options);
            System.exit(-1);
        }
    }
}

class DoubleVariableChecker extends AbstractVisitor<Boolean, Void> {

    public Boolean visit(PolynomialAddition pa, Void param) {
        return pa.firstOperand.accept(this, param) || pa.secondOperand.accept(this, param);
    }

    public Boolean visit(PolynomialSubtraction ps, Void param) {
        return ps.firstOperand.accept(this, param) || ps.secondOperand.accept(this, param);
    }

    public Boolean visit(PolynomialMultiplication pm, Void param) {
        return pm.firstOperand.accept(this, param) || pm.secondOperand.accept(this, param);
    }

    public Boolean visit(PolynomialPower pp, Void param) {
        return pp.base.accept(this, param);
    }

    public Boolean visit(PolynomialNegation pn, Void param) {
        return pn.operand.accept(this, param);
    }

    public Boolean visit(PolynomialDoubleDivision pdd, Void param) {
        return pdd.dividend.accept(this, param) || pdd.divisor.accept(this, param);
    }

    public Boolean visit(PolynomialVariable pv, Void param) {
        return false;
    }

    public Boolean visit(DoubleBinaryOperation dbop, Void param) {
        return dbop.firstOperand.accept(this, param) || dbop.secondOperand.accept(this, param);
    }

    public Boolean visit(DoubleUnaryOperation duop, Void param) {
        return duop.operand.accept(this, param);
    }

    public Boolean visit(DoubleValue dv, Void param) {
        return false;
    }

    public Boolean visit(DoubleVariable dv, Void param) {
        return true;
    }
}

class PNGFilter extends javax.swing.filechooser.FileFilter {

    public static String getExtension(java.io.File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static java.io.File ensureExtension(java.io.File f) {
        if (getExtension(f) != "png") {
            f = new java.io.File(f.getAbsolutePath() + ".png");
        }
        return f;
    }

    //Accept all png files.
    public boolean accept(java.io.File f) {
        return f.isDirectory() || getExtension(f).equals("png");
    }

    //The description of this filter
    public String getDescription() {
        return "*.png (PNG images)";
    }
}
