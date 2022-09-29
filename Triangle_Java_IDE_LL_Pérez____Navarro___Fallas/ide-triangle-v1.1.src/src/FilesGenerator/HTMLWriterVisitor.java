/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FilesGenerator;

import Triangle.AbstractSyntaxTrees.AnyTypeDenoter;
import Triangle.AbstractSyntaxTrees.ArrayExpression;
import Triangle.AbstractSyntaxTrees.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.AssignCommand;
import Triangle.AbstractSyntaxTrees.BarCommand;
import Triangle.AbstractSyntaxTrees.BinaryExpression;
import Triangle.AbstractSyntaxTrees.BinaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.BoolTypeDenoter;
import Triangle.AbstractSyntaxTrees.CallCommand;
import Triangle.AbstractSyntaxTrees.CallExpression;
import Triangle.AbstractSyntaxTrees.CharTypeDenoter;
import Triangle.AbstractSyntaxTrees.CharacterExpression;
import Triangle.AbstractSyntaxTrees.CharacterLiteral;
import Triangle.AbstractSyntaxTrees.ConstActualParameter;
import Triangle.AbstractSyntaxTrees.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.ConstFormalParameter;
import Triangle.AbstractSyntaxTrees.DotVname;
import Triangle.AbstractSyntaxTrees.EmptyActualParameterSequence;
import Triangle.AbstractSyntaxTrees.EmptyCommand;
import Triangle.AbstractSyntaxTrees.EmptyExpression;
import Triangle.AbstractSyntaxTrees.EmptyFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.ErrorTypeDenoter;
import Triangle.AbstractSyntaxTrees.FuncActualParameter;
import Triangle.AbstractSyntaxTrees.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.FuncFormalParameter;
import Triangle.AbstractSyntaxTrees.Identifier;
import Triangle.AbstractSyntaxTrees.IfCommand;
import Triangle.AbstractSyntaxTrees.IfExpression;
import Triangle.AbstractSyntaxTrees.IntTypeDenoter;
import Triangle.AbstractSyntaxTrees.IntegerExpression;
import Triangle.AbstractSyntaxTrees.IntegerLiteral;
import Triangle.AbstractSyntaxTrees.LetCommand;
import Triangle.AbstractSyntaxTrees.LetExpression;
import Triangle.AbstractSyntaxTrees.LetInCommand;
import Triangle.AbstractSyntaxTrees.LocalDeclaration;
import Triangle.AbstractSyntaxTrees.LoopDoUntilCommand;
import Triangle.AbstractSyntaxTrees.LoopDoWhileCommand;
import Triangle.AbstractSyntaxTrees.LoopForFromToDoCommand;
import Triangle.AbstractSyntaxTrees.LoopForFromToUntilDoCommand;
import Triangle.AbstractSyntaxTrees.LoopForFromToWhileDoCommand;
import Triangle.AbstractSyntaxTrees.LoopUntilDoCommand;
import Triangle.AbstractSyntaxTrees.LoopWhileDoCommand;
import Triangle.AbstractSyntaxTrees.MultipleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleArrayAggregate;
import Triangle.AbstractSyntaxTrees.MultipleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.MultipleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Operator;
import Triangle.AbstractSyntaxTrees.ProcActualParameter;
import Triangle.AbstractSyntaxTrees.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.ProcFormalParameter;
import Triangle.AbstractSyntaxTrees.ProcFuncDeclaration;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.AbstractSyntaxTrees.RecordExpression;
import Triangle.AbstractSyntaxTrees.RecordTypeDenoter;
import Triangle.AbstractSyntaxTrees.RestOfIf;
import Triangle.AbstractSyntaxTrees.SequentialCommand;
import Triangle.AbstractSyntaxTrees.SequentialDeclaration;
import Triangle.AbstractSyntaxTrees.SimpleTypeDenoter;
import Triangle.AbstractSyntaxTrees.SimpleVname;
import Triangle.AbstractSyntaxTrees.SingleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleArrayAggregate;
import Triangle.AbstractSyntaxTrees.SingleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.SingleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleRecordAggregate;
import Triangle.AbstractSyntaxTrees.SubscriptVname;
import Triangle.AbstractSyntaxTrees.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.UnaryExpression;
import Triangle.AbstractSyntaxTrees.UnaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.VarActualParameter;
import Triangle.AbstractSyntaxTrees.VarDeclaration;
import Triangle.AbstractSyntaxTrees.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.VarInitDeclaration;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author maxle
 */
public class HTMLWriterVisitor implements Visitor {

    private FileWriter fileWriter;

    HTMLWriterVisitor(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    // Commands
    public Object visitAssignCommand(AssignCommand ast, Object obj) {
        writeLineHTML("<AssignCommand>");
        ast.V.visit(this, null);
        ast.E.visit(this, null);
        writeLineHTML("</AssignCommand>");
        return null;
    }

    public Object visitCallCommand(CallCommand ast, Object obj) {
        ast.I.visit(this, null);
        ast.APS.visit(this, null);
        writeLineHTML("() <font style='paddingleft:1em'>");
        return null;
    }

    public Object visitEmptyCommand(EmptyCommand ast, Object obj) {
        writeLineHTML("<font color='#00b300'>! Test of nil, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>nil </b><font style='paddingleft:1em'>");
        return null;
    }

    public Object visitIfCommand(IfCommand ast, Object obj) {
        writeLineHTML("<font color='#00b300'>! Test of loop until command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>if </b><font style='paddingleft:1em'>");
        ast.E.visit(this, null);
        writeLineHTML("<b>then </b><font style='paddingleft:1em'>");
        ast.C1.visit(this, null);
        writeLineHTML("<b>| </b><font style='paddingleft:1em'>");
        ast.ROI1.visit(this, null);
        writeLineHTML("<b>end </b>");
        return null;
    }

    public Object visitLetCommand(LetCommand ast, Object obj) {
        writeLineHTML("<LetCommand>");
        ast.D.visit(this, null);
        ast.C.visit(this, null);
        writeLineHTML("</LetCommand>");
        return null;
    }

    public Object visitSequentialCommand(SequentialCommand ast, Object obj) {
        if (ast.C1 != null) {
            ast.C1.visit(this, null);
            writeLineHTML("<b>; </b><font style='paddingleft:1em'>");
        }
        if (ast.C2 != null) {
            ast.C2.visit(this, null);
        }
        return null;
    }

    public Object visitWhileCommand(WhileCommand ast, Object obj) {
        writeLineHTML("<WhileCommand>");
        ast.E.visit(this, null);
        ast.C.visit(this, null);
        writeLineHTML("</WhileCommand>");
        return null;
    }

    // Expressions
    public Object visitArrayExpression(ArrayExpression ast, Object obj) {
        writeLineHTML("<ArrayExpression>");
        ast.AA.visit(this, null);
        writeLineHTML("</ArrayExpression>");
        return null;
    }

    public Object visitBinaryExpression(BinaryExpression ast, Object obj) {
        writeLineHTML("<BinaryExpression>");
        ast.E1.visit(this, null);
        ast.O.visit(this, null);
        ast.E2.visit(this, null);
        writeLineHTML("</BinaryExpression>");
        return null;
    }

    public Object visitCallExpression(CallExpression ast, Object obj) {
        writeLineHTML("<CallExpression>");
        ast.I.visit(this, null);
        ast.APS.visit(this, null);
        writeLineHTML("</CallExpression>");
        return null;
    }

    public Object visitCharacterExpression(CharacterExpression ast, Object obj) {
        writeLineHTML("<CharacterExpression>");
        ast.CL.visit(this, null);
        writeLineHTML("</CharacterExpression>");
        return null;
    }

    public Object visitEmptyExpression(EmptyExpression ast, Object obj) {
        writeLineHTML("<EmptyExpression/>");
        return null;
    }

    public Object visitIfExpression(IfExpression ast, Object obj) {
        writeLineHTML("<IfExpression>");
        ast.E1.visit(this, null);
        ast.E2.visit(this, null);
        ast.E3.visit(this, null);
        writeLineHTML("</IfExpression>");
        return null;
    }

    public Object visitIntegerExpression(IntegerExpression ast, Object obj) {
        writeLineHTML("<IntegerExpression>");
        ast.IL.visit(this, null);
        writeLineHTML("</IntegerExpression>");
        return null;
    }

    public Object visitLetExpression(LetExpression ast, Object obj) {
        writeLineHTML("<LetExpression>");
        ast.D.visit(this, null);
        ast.E.visit(this, null);
        writeLineHTML("</LetExpression>");
        return null;
    }

    public Object visitRecordExpression(RecordExpression ast, Object obj) {
        writeLineHTML("<RecordExpression>");
        ast.RA.visit(this, null);
        writeLineHTML("</RecordExpression>");
        return null;
    }

    public Object visitUnaryExpression(UnaryExpression ast, Object obj) {
        writeLineHTML("<UnaryExpression>");
        ast.O.visit(this, null);
        ast.E.visit(this, null);
        writeLineHTML("</UnaryExpression>");
        return null;
    }

    public Object visitVnameExpression(VnameExpression ast, Object obj) {
        ast.V.visit(this, null);
        return null;
    }

    // Declarations
    public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object obj) {
        writeLineHTML("<BinaryOperatorDeclaration>");
        ast.O.visit(this, null);
        ast.ARG1.visit(this, null);
        ast.ARG2.visit(this, null);
        ast.RES.visit(this, null);
        writeLineHTML("</BinaryOperatorDeclaration>");
        return null;
    }

    public Object visitConstDeclaration(ConstDeclaration ast, Object obj) {
        writeLineHTML("<ConstDeclaration>");
        ast.I.visit(this, null);
        ast.E.visit(this, null);
        writeLineHTML("</ConstDeclaration>");
        return null;
    }

    public Object visitFuncDeclaration(FuncDeclaration ast, Object obj) {
        writeLineHTML("<FuncDeclaration>");
        ast.I.visit(this, null);
        ast.FPS.visit(this, null);
        ast.T.visit(this, null);
        ast.E.visit(this, null);
        writeLineHTML("</FuncDeclaration>");
        return null;
    }

    public Object visitProcDeclaration(ProcDeclaration ast, Object obj) {
        ast.I.visit(this, null);
        writeLineHTML("() <font style='paddingleft:1em'>");
        writeLineHTML("<b>~ </b><font style='paddingleft:1em'>");
        ast.FPS.visit(this, null);
        ast.C.visit(this, null);
        writeLineHTML("<b>end </b><font style='paddingleft:1em'>");
        return null;
    }

    public Object visitSequentialDeclaration(SequentialDeclaration ast, Object obj) {
        if (ast.D1 != null) {
            ast.D1.visit(this, null);
            writeLineHTML("<b>; </b><font style='paddingleft:1em'>");
        }
        if (ast.D2 != null) {
            ast.D2.visit(this, null);
        }
        return null;
    }

    public Object visitTypeDeclaration(TypeDeclaration ast, Object obj) {
        writeLineHTML("<TypeDeclaration>");
        ast.I.visit(this, null);
        ast.T.visit(this, null);
        writeLineHTML("</TypeDeclaration>");
        return null;
    }

    public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object obj) {
        writeLineHTML("<UnaryOperatorDeclaration>");
        ast.O.visit(this, null);
        ast.ARG.visit(this, null);
        ast.RES.visit(this, null);
        writeLineHTML("</UnaryOperatorDeclaration>");
        return null;
    }

    public Object visitVarDeclaration(VarDeclaration ast, Object obj) {
        writeLineHTML("<b>var </b><font style='paddingleft:1em'>");
        ast.I.visit(this, null);
        ast.T.visit(this, null);
        return null;
    }

    // Array Aggregates
    public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object obj) {
        writeLineHTML("<MultipleArrayAggregate>");
        ast.E.visit(this, null);
        ast.AA.visit(this, null);
        writeLineHTML("</MultipleArrayAggregate>");
        return null;
    }

    public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object obj) {
        writeLineHTML("<SingleArrayAggregate>");
        ast.E.visit(this, null);
        writeLineHTML("</SingleArrayAggregate>");
        return null;
    }

    // Record Aggregates
    public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object obj) {
        writeLineHTML("<MultipleRecordAggregate>");
        ast.I.visit(this, null);
        ast.E.visit(this, null);
        ast.RA.visit(this, null);
        writeLineHTML("</MultipleRecordAggregate>");
        return null;
    }

    public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object obj) {
        writeLineHTML("<SingleRecordAggregate>");
        ast.I.visit(this, null);
        ast.E.visit(this, null);
        writeLineHTML("</SingleRecordAggregate>");
        return null;
    }

    // Formal Parameters
    public Object visitConstFormalParameter(ConstFormalParameter ast, Object obj) {
        writeLineHTML("<ConstFormalParameter>");
        ast.I.visit(this, null);
        ast.T.visit(this, null);
        writeLineHTML("</ConstFormalParameter>");
        return null;
    }

    public Object visitFuncFormalParameter(FuncFormalParameter ast, Object obj) {
        writeLineHTML("<FuncFormalParameter>");
        ast.I.visit(this, null);
        ast.FPS.visit(this, null);
        ast.T.visit(this, null);
        writeLineHTML("<FuncFormalParameter>");
        return null;
    }

    public Object visitProcFormalParameter(ProcFormalParameter ast, Object obj) {
        writeLineHTML("<ProcFormalParameter>");
        ast.I.visit(this, null);
        ast.FPS.visit(this, null);
        writeLineHTML("</ProcFormalParameter>");
        return null;
    }

    public Object visitVarFormalParameter(VarFormalParameter ast, Object obj) {
        writeLineHTML("<VarFormalParameter>");
        ast.I.visit(this, null);
        ast.T.visit(this, null);
        writeLineHTML("</VarFormalParameter>");
        return null;
    }

    public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object obj) {
        writeLineHTML("<EmptyFormalParameterSequence/>");
        return null;
    }

    public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object obj) {
        writeLineHTML("<MultipleFormalParameterSequence>");
        ast.FP.visit(this, null);
        ast.FPS.visit(this, null);
        writeLineHTML("</MultipleFormalParameterSequence>");
        return null;
    }

    public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object obj) {
        writeLineHTML("<SingleFormalParameterSequence>");
        ast.FP.visit(this, null);
        writeLineHTML("</SingleFormalParameterSequence>");
        return null;
    }

    // Actual Parameters
    public Object visitConstActualParameter(ConstActualParameter ast, Object obj) {
        writeLineHTML("<ConstActualParameter>");
        ast.E.visit(this, null);
        writeLineHTML("</ConstActualParameter>");
        return null;
    }

    public Object visitFuncActualParameter(FuncActualParameter ast, Object obj) {
        writeLineHTML("<FuncActualParameter>");
        ast.I.visit(this, null);
        writeLineHTML("</FuncActualParameter>");
        return null;
    }

    public Object visitProcActualParameter(ProcActualParameter ast, Object obj) {
        writeLineHTML("<ProcActualParameter>");
        ast.I.visit(this, null);
        writeLineHTML("</ProcActualParameter>");
        return null;
    }

    public Object visitVarActualParameter(VarActualParameter ast, Object obj) {
        writeLineHTML("<VarActualParameter>");
        ast.V.visit(this, null);
        writeLineHTML("</VarActualParameter>");
        return null;
    }

    // Se elimina la etiqueta
    public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object obj) {
        //writeLineHTML("<EmptyActualParameterSequence/>");
        return null;
    }

    public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object obj) {
        writeLineHTML("<MultipleActualParameterSequence>");
        ast.AP.visit(this, null);
        ast.APS.visit(this, null);
        writeLineHTML("</MultipleActualParameterSequence>");
        return null;
    }

    public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object obj) {
        writeLineHTML("<SingleActualParameterSequence>");
        ast.AP.visit(this, null);
        writeLineHTML("</SingleActualParameterSequence>");
        return null;
    }

    // Type Denoters
    public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object obj) {
        writeLineHTML("<AnyTypeDenoter/>");
        return null;
    }

    public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object obj) {
        writeLineHTML("<ArrayTypeDenoter>");
        ast.IL.visit(this, null);
        ast.T.visit(this, null);
        writeLineHTML("</ArrayTypeDenoter>");
        return null;
    }

    public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object obj) {
        writeLineHTML("<BoolTypeDenoter/>");
        return null;
    }

    public Object visitCharTypeDenoter(CharTypeDenoter ast, Object obj) {
        writeLineHTML("<CharTypeDenoter/>");
        return null;
    }

    public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object obj) {
        writeLineHTML("<ErrorTypeDenoter/>");
        return null;
    }

    public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object obj) {
        writeLineHTML("<SimpleTypeDenoter>");
        ast.I.visit(this, null);
        writeLineHTML("</SimpleTypeDenoter>");
        return null;
    }

    public Object visitIntTypeDenoter(IntTypeDenoter ast, Object obj) {
        writeLineHTML("<IntTypeDenoter/>");
        return null;
    }

    public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object obj) {
        writeLineHTML("<RecordTypeDenoter>");
        ast.FT.visit(this, null);
        writeLineHTML("</RecordTypeDenoter>");
        return null;
    }

    public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object obj) {
        writeLineHTML("<MultipleFieldTypeDenoter>");
        ast.I.visit(this, null);
        ast.T.visit(this, null);
        ast.FT.visit(this, null);
        writeLineHTML("</MultipleFieldTypeDenoter>");
        return null;
    }

    public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object obj) {
        writeLineHTML("<SingleFieldTypeDenoter>");
        ast.I.visit(this, null);
        ast.T.visit(this, null);
        writeLineHTML("</SingleFieldTypeDenoter>");
        return null;
    }

    // Literals, Identifiers and Operators
    public Object visitCharacterLiteral(CharacterLiteral ast, Object obj) {
        writeLineHTML("<CharacterLiteral value=\"" + ast.spelling + "\"/>");
        return null;
    }

    // Se obtiene el valor de la expresion
    public Object visitIdentifier(Identifier ast, Object obj) {
        writeLineHTML(ast.spelling + " </font>");
        return null;
    }

    public Object visitIntegerLiteral(IntegerLiteral ast, Object obj) {
        writeLineHTML("<IntegerLiteral value=\"" + ast.spelling + "\"/>");
        return null;
    }

    public Object visitOperator(Operator ast, Object obj) {
        writeLineHTML("<Operator value=\"" + transformOperator(ast.spelling) + "\"/>");
        return null;
    }

    // Value-or-variable names
    public Object visitDotVname(DotVname ast, Object obj) {
        writeLineHTML("<DotVname>");
        ast.V.visit(this, null);
        ast.I.visit(this, null);
        writeLineHTML("</DotVname>");
        return null;
    }

    public Object visitSimpleVname(SimpleVname ast, Object obj) {
        writeLineHTML("<font color='#0000cd'>");
        ast.I.visit(this, null);
        return null;
    }

    public Object visitSubscriptVname(SubscriptVname ast, Object obj) {
        writeLineHTML("<SubscriptVname>");
        ast.V.visit(this, null);
        ast.E.visit(this, null);
        writeLineHTML("</SubscriptVname>");
        return null;
    }

    // Programs
    public Object visitProgram(Program ast, Object obj) {
        ast.C.visit(this, null);
        return null;
    }

    private void writeLineHTML(String line) {
        try {
            fileWriter.write(line);
        } catch (IOException e) {
            System.err.println("Error while writing file for print the AST");
            e.printStackTrace();
        }
    }

    /*
     * Convert the characters "<" & "<=" to their equivalents in html
     */
    private String transformOperator(String operator) {
        if (operator.compareTo("<") == 0) {
            return "&lt;";
        } else if (operator.compareTo("<=") == 0) {
            return "&lt;=";
        } else {
            return operator;
        }
    }

    // Nuevo agregado - Parte 1
    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LetInCommand
    @Override
    public Object visitLetInCommand(LetInCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of let in command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>let </b><font style='paddingleft:1em'>");
        ast.D.visit(this, null);
        writeLineHTML("<b>in </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }

    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LoopWhileDoCommand
    public Object visitLoopWhileDoCommand(LoopWhileDoCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of loop while do command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>loop while </b><font style='paddingleft:1em'>");
        ast.E.visit(this, null);
        writeLineHTML("<b>do </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }

    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LoopDoWhileCommand
    public Object visitLoopDoWhileCommand(LoopDoWhileCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of loop do while command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>loop do </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>while </b><font style='paddingleft:1em'>");
        ast.E.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }

    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LoopUntilDoCommand
    public Object visitLoopUntilDoCommand(LoopUntilDoCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of loop until do command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>loop until </b><font style='paddingleft:1em'>");
        ast.E.visit(this, null);
        writeLineHTML("<b>do </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }
    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LoopDoUntilCommand

    public Object visitLoopDoUntilCommand(LoopDoUntilCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of loop do until command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>loop do </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>until </b><font style='paddingleft:1em'>");
        ast.E.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }

    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LoopForFromToDoCommand
    public Object visitLoopForFromToDoCommand(LoopForFromToDoCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of loop for from to do command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>loop for </b><font style='paddingleft:1em'>");
        ast.I.visit(this, null);
        writeLineHTML("<b>from </b><font style='paddingleft:1em'>");
        ast.E1.visit(this, null);
        writeLineHTML("<b>to </b><font style='paddingleft:1em'>");
        ast.E2.visit(this, null);
        writeLineHTML("<b>do </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }

    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LoopForFromToWhileDoCommand
    public Object visitLoopForFromToWhileDoCommand(LoopForFromToWhileDoCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of loop for from to while do command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>loop for </b><font style='paddingleft:1em'>");
        ast.I.visit(this, null);
        writeLineHTML("<b>from </b><font style='paddingleft:1em'>");
        ast.E1.visit(this, null);
        writeLineHTML("<b>to </b><font style='paddingleft:1em'>");
        ast.E2.visit(this, null);
        writeLineHTML("<b>while </b><font style='paddingleft:1em'>");
        ast.E3.visit(this, null);
        writeLineHTML("<b>do </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }

    // Autores: Max Lee y Paula Mariana Bustos
    // Se agrega el metodo visitor de LoopForFromToUntilDoCommand
    public Object visitLoopForFromToUntilDoCommand(LoopForFromToUntilDoCommand ast, Object o) {
        writeLineHTML("<font color='#00b300'>! Test of loop for from to until do command, it runs correctly. </font>");
        writeLineHTML("<br><br>");
        writeLineHTML("<b>loop for </b><font style='paddingleft:1em'>");
        ast.I.visit(this, null);
        writeLineHTML("<b>from </b><font style='paddingleft:1em'>");
        ast.E1.visit(this, null);
        writeLineHTML("<b>to </b><font style='paddingleft:1em'>");
        ast.E2.visit(this, null);
        writeLineHTML("<b>until </b><font style='paddingleft:1em'>");
        ast.E3.visit(this, null);
        writeLineHTML("<b>do </b><font style='paddingleft:1em'>");
        ast.C.visit(this, null);
        writeLineHTML("<b>end <b>");
        return (null);
    }

    @Override
    public Object visitRestOfIfCommand(RestOfIf ast, Object o) {
        if (ast.BC1 != null) {
            ast.BC1.visit(this, null);
            writeLineHTML("<b>else </b><font style='paddingleft:1em'>");
        }
        ast.C1.visit(this, null);
        return (null);
    }

    @Override
    public Object visitBarCommand(BarCommand ast, Object o) {
        ast.E1.visit(this, null);
        writeLineHTML("<b>then </b><font style='paddingleft:1em'>");
        ast.C1.visit(this, null);
        if (ast.BC1 != null) {
            writeLineHTML("<b>| </b><font style='paddingleft:1em'>");
            ast.BC1.visit(this, null);
        }
        return (null);
    }

    @Override
    public Object visitVarInitDeclaration(VarInitDeclaration ast, Object o) {
        writeLineHTML("<b>var </b><font style='paddingleft:1em'>");
        ast.I.visit(this, null);
        writeLineHTML("<b>init </b><font style='paddingleft:1em'>");
        ast.E.visit(this, null);
        return (null);
    }

    @Override
    public Object visitLocalDeclaration(LocalDeclaration ast, Object o) {
        writeLineHTML("<b>local </b><font style='paddingleft:1em'>");
        ast.D1.visit(this, null);
        writeLineHTML("<b>in </b><font style='paddingleft:1em'>");
        ast.D2.visit(this, null);
        writeLineHTML("<b>end </b><font style='paddingleft:1em'>");
        return (null);
    }

    @Override
    public Object visitProcFuncsDeclaration(ProcFuncDeclaration ast, Object o) {
        if (ast.PD != null) {
            ast.PD.visit(this, null);
            if (ast.PF != null) {
                ast.PF.visit(this, null);
            }
        }
        if (ast.PD != null) {
            ast.PF.visit(this, null);
            if (ast.PF != null) {
                ast.PF.visit(this, null);
            }
        }
        return (null);
    }

}
