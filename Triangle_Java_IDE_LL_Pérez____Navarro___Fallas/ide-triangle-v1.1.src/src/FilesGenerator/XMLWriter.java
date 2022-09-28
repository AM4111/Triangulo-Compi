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
public class XMLWriter {
    private String fileName;

    public XMLWriter(String fileName) {
        this.fileName = fileName;
    }

    // Draw the AST representing a complete program.
    public void write(Program ast) {
        // Prepare the file to write
        try {
            FileWriter fileWriter = new FileWriter(fileName);

            //XML header
            fileWriter.write("<?xml version=\"1.0\" standalone=\"yes\"?>\n");

            XMLWriterVisitor layout = new XMLWriterVisitor(fileWriter);
            ast.visit(layout, null);

            fileWriter.close();

        } catch (IOException e) {
            System.err.println("Error while creating file for print the AST");
            e.printStackTrace();
        }
    }
}
