/*
 * IDE-Triangle v1.0
 * Compiler.java 
 */
package Triangle;

import FilesGenerator.HTMLWriter;
import FilesGenerator.XMLWriter;
import Triangle.SyntacticAnalyzer.SourceFile;
import Triangle.SyntacticAnalyzer.Scanner;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.SyntacticAnalyzer.Parser;

/**
 * This is merely a reimplementation of the Triangle.Compiler class. We need to
 * get to the ASTs in order to draw them in the IDE without modifying the
 * original Triangle code.
 *
 * @author Luis Leopoldo Pérez <luiperpe@ns.isi.ulatina.ac.cr>
 */
public class IDECompiler {

    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    /**
     * Creates a new instance of IDECompiler.
     *
     */
    public IDECompiler() {
    }

    /**
     * Particularly the same compileProgram method from the Triangle.Compiler
     * class.
     *
     * @param sourceName Path to the source file.
     * @return True if compilation was succesful.
     */
    public boolean compileProgram(String sourceName) {
        System.out.println("********** "
                + "Triangle Compiler (IDE-Triangle 1.0)"
                + " **********");

        System.out.println("Syntactic Analysis ...");
        SourceFile source = new SourceFile(sourceName);
        Scanner scanner = new Scanner(source);
        report = new IDEReporter();
        Parser parser = new Parser(scanner, report);
        boolean success = false;

        rootAST = parser.parseProgram();
        if (report.numErrors == 0) {
            //System.out.println("Contextual Analysis ...");
            //Checker checker = new Checker(report);
            //checker.check(rootAST);
            if (report.numErrors == 0) {
                //System.out.println("Code Generation ...");
                //Encoder encoder = new Encoder(report);
                //encoder.encodeRun(rootAST, false);

                if (report.numErrors == 0) {
                    //  encoder.saveObjectProgram(sourceName.replace(".tri", ".tam"));
                    success = true;
                }
            }
        }
        String fileSourceName = sourceName.substring(0, sourceName.length() -3);
        if (success) {
            System.out.println("Compilation was successful.");
            XMLWriter xml = new XMLWriter(fileSourceName + "xml");
            xml.write(getAST());
            System.out.println("XML file generated successfully");
        } else {
            System.out.println("Compilation was unsuccessful.");
        }

        HTMLWriter html = new HTMLWriter(fileSourceName + "html");
        html.write(getAST());
        System.out.println("HTML file generated successfully");
        
        return (success);
    }

    /**
     * Returns the line number where the first error is.
     *
     * @return Line number.
     */
    public int getErrorPosition() {
        return (report.getFirstErrorPosition());
    }

    /**
     * Returns the root Abstract Syntax Tree.
     *
     * @return Program AST (root).
     */
    public Program getAST() {
        return (rootAST);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    private Program rootAST;        // The Root Abstract Syntax Tree.    
    private IDEReporter report;     // Our ErrorReporter class.
    // </editor-fold>
}
