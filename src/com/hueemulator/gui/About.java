package com.hueemulator.gui;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

import com.hueemulator.emulator.Constants;


public class About extends JFrame{
    private JEditorPane aboutPane;
  
    
    public About() {

        String text = "<h2>Hue Emulator " + Constants.EMULATOR_VERSION + "</h2><p>Programmed By:&nbsp;&nbsp;<b>SteveyO</b>. <p>See <a href=\"http://steveyo.github.io/Hue-Emulator/\">http://steveyo.github.io/Hue-Emulator/</a> for more details.</p>";

        aboutPane = new JEditorPane();
        aboutPane.setContentType("text/html");
        aboutPane.setText(text);

        setSize(400, 210);
        aboutPane.setVisible(true);
        add(aboutPane);
    }
}
