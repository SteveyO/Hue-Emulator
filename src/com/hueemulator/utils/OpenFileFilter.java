package com.hueemulator.utils;

/**
 * This class defines which file types are displayed (by default) by the JFileChooser and what file
 * types appear in the drop down menu in the file dialog.
 * You could add more than one file type to the open file dialog by creating multiple instances of this 
 * class and then repeatedly calling addFileFilter.
 * @author LaSpina
 */

import java.io.File;
import javax.swing.filechooser.*;

public class OpenFileFilter extends FileFilter {

    String description = "";
    String fileExt = "";

    public OpenFileFilter(String extension) {
        fileExt = extension;
    }

    public OpenFileFilter(String extension, String typeDescription) {
        fileExt = extension;
        this.description = typeDescription;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        return (f.getName().toLowerCase().endsWith(fileExt));
    }

    @Override
    public String getDescription() {
        return description;
    }
}