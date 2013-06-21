/*-----------------------------------------------------------------------------
 * Modul Name       : SatzTxtReaderMainFrame.java
 * Verwendung       : Drucken
 * Autor            : mg
 * Verantwortlich   : mg
 * Kontrolle        : 
 * Beratungspflicht : nein
 * Copyright        : (c)1990-2013 ABAS Software AG
 *
 *---------------------------------------------------------------------------*/

package de.abas.utils;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author mg
 *
 */
public class SatzTxtReaderMainFrame extends JFrame {
    private final static Log logger = LogFactory.getLog(SatzTxtReaderMainFrame.class);
       
    private static final long serialVersionUID = 2500568980243312871L;

    private SatzTxt satzTxt;
    private final Font textFont = new Font("Courier", Font.PLAIN, 12);
    private List<JTextPane> panes = new ArrayList<JTextPane>();
    
    
    public SatzTxtReaderMainFrame(final SatzTxt satz) {
        super("Test");
        satzTxt = satz;
        logger.debug("Start Editor");
    }
    
    public void initializeMainFrame() {
        SwingUtilities.updateComponentTreeUI(this);
        
        guiInit();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocation(100, 100);
        setSize(1400, 800);
        //pack();
        setVisible(true);
    }

    
    // ========== Private Methods =================================================================


    private void guiInit() {
        int columns = satzTxt.getGroups().size();

        setBackground(Color.WHITE);
        setLayout(new GridLayout(0, columns));

        for (int i=0; i<columns; i++) {
            add(getTextPaneForGroupNumber(i));
        }
        
        JButton ok = new JButton("OK");
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        //aboutPanel.add(ok);
        
        addWindowListener(new ThisWindowListener());
        addKeyListener(new AboutKeyListener());
    }

    private JScrollPane getTextPaneForGroupNumber(final int group) {
        JTextPane textPane = new JTextPane();
        textPane.setCaretPosition(0);
        textPane.setEditable(false);
        textPane.setFont(textFont);
        textPane.setContentType("text/plain");
        textPane.addCaretListener(new CurrentLineHighlighter());
        textPane.addMouseListener(new MyMouseListener());
        textPane.addKeyListener(new MyKeyListener());

        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(textPane);

        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.white));
//        scrollPane.setPreferredSize(new Dimension(800,400));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        String text = satzTxt.getGroup(group).getData();
        textPane.setText(text);
        textPane.setCaretPosition(0);
        
        panes.add(textPane);
        return scrollPane;
    }
    
    private void setAllRowPositionsTo(int rowNumber) {
        for (JTextPane pane : panes) {
            setLineNumberTo(pane, rowNumber);
        }
    }
    
    private void setLineNumberTo(final JTextPane text, final int line) {
        int currentLine = 0;
        int currentSelection = 0;
        String textContent = text.getText();
        String seperator = "\n";
        int seperatorLength = seperator.length();
        while (currentLine < line) {
            int next = textContent.indexOf(seperator,currentSelection);
            if (next > -1) {
                currentSelection = next + seperatorLength;
                currentLine++;
            } 
            else {
                // set to the end of doc
                currentSelection = textContent.length();
                currentLine = line; // exits loop
            }
        }
        text.setCaretPosition(currentSelection);
    }    
    
    private int getRowFor(final int pos, final JTextComponent editor) {
        int rn = (pos==0) ? 1 : 0;
        try {
            int offs=pos;
            while( offs>0) {
                offs=Utilities.getRowStart(editor, offs)-1;
                rn++;
            }
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        return rn;
    }
    
    private class MyMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            evalRowPositionEvent(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private void evalRowPositionEvent(final InputEvent e) {
        JTextPane source = (JTextPane)e.getSource();
        int pos = source.getCaretPosition();
        int row = getRowFor(pos, source)-1;
        setAllRowPositionsTo(row);
    }
    
    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if ((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_UP)) {
                evalRowPositionEvent(e);
            }
        }
    }
    
    public class CurrentLineHighlighter implements CaretListener {
        private final Color DEFAULT_COLOR = new Color(230, 230, 210);

        private Highlighter.HighlightPainter painter;
        private Object highlight;

        public CurrentLineHighlighter() {
            this(null);
        }

        public CurrentLineHighlighter(Color highlightColor) {
            Color c = highlightColor != null ? highlightColor : DEFAULT_COLOR;
            painter = new DefaultHighlighter.DefaultHighlightPainter(c);
        }

        public void caretUpdate(CaretEvent evt) {
            JTextComponent comp = (JTextComponent)evt.getSource();
            if (comp != null && highlight != null) {
                comp.getHighlighter().removeHighlight(highlight);
                highlight = null;
            }

            int pos = comp.getCaretPosition();
            Element elem = Utilities.getParagraphElement(comp, pos);
            int start = elem.getStartOffset();
            int end = elem.getEndOffset();
            try {
                highlight = comp.getHighlighter().addHighlight(start, end, painter);
            }
            catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class ThisWindowListener extends WindowAdapter {
        /* (non-Javadoc)
         * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
         */
        @Override
        public void windowOpened(final WindowEvent e) {
            getContentPane().setVisible(true);
        }

        /* (non-Javadoc)
         * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
         */
        @Override
        public void windowClosing(final WindowEvent we){
            setVisible(false);
            System.exit(0);
        }
    }

    private class AboutKeyListener extends KeyAdapter {
        /* (non-Javadoc)
         * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(KeyEvent arg0) {
            textField_keyPressed(arg0);
        }
    }

    private void textField_keyPressed(final KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                setVisible(false);
                break;
        }
    }
}
