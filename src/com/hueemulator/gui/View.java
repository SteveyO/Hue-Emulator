package com.hueemulator.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.text.html.HTMLDocument;

public class View {

    private JEditorPane console;  // Console where all text is displayed
    private HueMenuBar menuBar;   
    private GraphicsPanel graphicsPanel;
    private PropertiesFrame propertiesFrame;
    private About         about; 
    private Help          help; 
    private JScrollPane consoleScrollPane;


    public View(){
        JFrame frame = new JFrame("Hue Emulator");

        console = new JEditorPane();
        graphicsPanel = new GraphicsPanel("SMALL");
        graphicsPanel.setVisible(true);

        help       = new Help();
        about      = new About();
        propertiesFrame = new PropertiesFrame();

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(850,550));

        console = new JEditorPane();
        console.setEditable(false);
        console.setContentType("text/html");
        console.setBackground(Color.BLACK);   

        JToolBar toolbar = new JToolBar();
        JButton button = new JButton("blah");
        toolbar.add(button);
        toolbar.setBorderPainted(false);

        menuBar = new HueMenuBar();   

        Font font = new Font("Courier New", Font.PLAIN, 14);
        String bodyRule = "body { color: #BBBBBB; font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
        ((HTMLDocument)console.getDocument()).getStyleSheet().addRule(bodyRule);       


        consoleScrollPane = new JScrollPane(console);
        consoleScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consoleScrollPane.setPreferredSize(new Dimension(250, 250));           


        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        frame.getContentPane().add(consoleScrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(graphicsPanel, BorderLayout.SOUTH);

        //4. Size the frame.
        frame.pack();
        frame.setVisible(true);        

    }

    public JEditorPane getConsole() {
        return console;
    }

    public void setConsole(JEditorPane console) {
        this.console = console;
    }

    public HueMenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(HueMenuBar menuBar) {
        this.menuBar = menuBar;
    } 

    public GraphicsPanel getGraphicsPanel() {
        return graphicsPanel;
    }

    public void setGraphicsPanel(GraphicsPanel graphicsPanel) {
        this.graphicsPanel = graphicsPanel;
    }

    public About getAbout() {
        return about;
    }

    public void setAbout(About about) {
        this.about = about;

    } 
    public Help getHelp() {
        return help;
    }
    
    public void setHelp(Help help) {
        this.help = help;
    } 

    public PropertiesFrame getPropertiesFrame() {
        return propertiesFrame;
    }

    public void setPropertiesFrame(PropertiesFrame propertiesFrame) {
        this.propertiesFrame = propertiesFrame;
    }

    public JScrollPane getConsoleScrollPane() {
        return consoleScrollPane;
    }

    public void setConsoleScrollPane(JScrollPane consoleScrollPane) {
        this.consoleScrollPane = consoleScrollPane;
    } 

}
