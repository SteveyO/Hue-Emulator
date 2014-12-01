package com.hueemulator.gui;

import javax.swing.JEditorPane;
import javax.swing.JFrame;


public class About extends JFrame{
    private JEditorPane aboutPane;

    public About() {

        String text = "<h2>Hue Emulator v0.2</h2>Programmed By:<br> Stevey O";

        aboutPane = new JEditorPane();
        aboutPane.setContentType("text/html");
        aboutPane.setText(text);

        setSize(300, 200);
        aboutPane.setVisible(true);
        add(aboutPane);
    }
}
