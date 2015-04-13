package com.hueemulator.gui;

import javax.swing.JEditorPane;
import javax.swing.JFrame;


public class Help extends JFrame{
    private JEditorPane aboutPane;
    
    public Help() {

        String text = "<h2>Help</h2>For full hue API documentation see: </br> "
                + "<p><a href=\"http://www.developers.meethue.com/philips-hue-api\">http://www.developers.meethue.com/philips-hue-api</a>&nbsp;(Login Required)</p>"
                + "<p><b>Note</b> that Rules and Sensors are not supported in the emulator</p>" +
                  "<p>Any bugs or issues please use the github issues page: <a href=\"https://github.com/SteveyO/Hue-Emulator/issues\">https://github.com/SteveyO/Hue-Emulator/issues</a></p>";

        aboutPane = new JEditorPane();
        aboutPane.setContentType("text/html");
        aboutPane.setText(text);

        setSize(450, 250);
        aboutPane.setVisible(true);
        add(aboutPane);
    }
}
