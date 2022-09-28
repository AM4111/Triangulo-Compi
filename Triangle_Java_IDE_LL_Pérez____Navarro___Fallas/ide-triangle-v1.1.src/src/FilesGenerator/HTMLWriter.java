/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FilesGenerator;

import Triangle.AbstractSyntaxTrees.Program;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author maxle
 */
public class HTMLWriter {
    private String fileName;

    public HTMLWriter(String fileName) {
        this.fileName = fileName;
    }

    // Draw the AST representing a complete program.
    public void write(Program ast) {
        // Prepare the file to write
        try {
            FileWriter fileWriter = new FileWriter(fileName);

            //HTML header
            fileWriter.write("<p style=\"font-family: 'DejaVu Sans', monospace;\">");
            
            HTMLWriterVisitor layout = new HTMLWriterVisitor(fileWriter);
            ast.visit(layout, null);
            
            //HTML end
            fileWriter.write("</p>");

            fileWriter.close();

        } catch (IOException e) {
            System.err.println("Error while creating file for print the AST");
            e.printStackTrace();
        }
    }
}