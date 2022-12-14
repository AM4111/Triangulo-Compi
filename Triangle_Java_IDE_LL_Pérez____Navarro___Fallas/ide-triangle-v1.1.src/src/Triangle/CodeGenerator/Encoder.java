/*
 * @(#)Encoder.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */
package Triangle.CodeGenerator;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import TAM.Instruction;
import TAM.Machine;
import Triangle.AbstractSyntaxTrees.*;
import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.AST;
import Triangle.AbstractSyntaxTrees.AnyTypeDenoter;
import Triangle.AbstractSyntaxTrees.ArrayExpression;
import Triangle.AbstractSyntaxTrees.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.AssignCommand;
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
import Triangle.AbstractSyntaxTrees.Declaration;
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
import Triangle.AbstractSyntaxTrees.MultipleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleArrayAggregate;
import Triangle.AbstractSyntaxTrees.MultipleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.MultipleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Operator;
import Triangle.AbstractSyntaxTrees.ProcActualParameter;
import Triangle.AbstractSyntaxTrees.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.ProcFormalParameter;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.AbstractSyntaxTrees.RecordExpression;
import Triangle.AbstractSyntaxTrees.RecordTypeDenoter;
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
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.Vname;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;
import Triangle.AbstractSyntaxTrees.LetInCommand;
import Triangle.AbstractSyntaxTrees.LoopWhileDoCommand;
import Triangle.AbstractSyntaxTrees.LoopDoWhileCommand;
import Triangle.AbstractSyntaxTrees.LoopUntilDoCommand;
import Triangle.AbstractSyntaxTrees.LoopDoUntilCommand;
import Triangle.AbstractSyntaxTrees.LoopForFromToDoCommand;
import Triangle.AbstractSyntaxTrees.LoopForFromToWhileDoCommand;
import Triangle.AbstractSyntaxTrees.LoopForFromToUntilDoCommand;

public final class Encoder implements Visitor {
    // Nuevos agregado - Parte x

    // Commands
    public Object visitAssignCommand(AssignCommand ast, Object o) {
        Frame frame = (Frame) o;
        Integer valSize = (Integer) ast.E.visit(this, frame);
        encodeStore(ast.V, new Frame(frame, valSize.intValue()),
                valSize.intValue());
        return null;
    }

    public Object visitCallCommand(CallCommand ast, Object o) {
        Frame frame = (Frame) o;
        Integer argsSize = (Integer) ast.APS.visit(this, frame);
        ast.I.visit(this, new Frame(frame.level, argsSize));
        return null;
    }

    public Object visitEmptyCommand(EmptyCommand ast, Object o) {
        return null;
    }

    public Object visitIfCommand(IfCommand ast, Object o) {
        Frame frame = (Frame) o;
        int jumpifAddr, jumpAddr;
        Integer valSize = (Integer) ast.E.visit(this, frame);
        jumpifAddr = nextInstrAddr;
        emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
        ast.C1.visit(this, frame);
        jumpAddr = nextInstrAddr;
        emit(Machine.JUMPop, Machine.falseRep, Machine.CBr, 0);
        patch(jumpifAddr, nextInstrAddr);
        ast.ROI1.visit(this, frame); //Adentro est?? el comando del else
        patch(jumpAddr, nextInstrAddr);
        return null;
    }

    public Object visitLetCommand(LetCommand ast, Object o) {
        Frame frame = (Frame) o;
        int extraSize = ((Integer) ast.D.visit(this, frame)).intValue();
        ast.C.visit(this, new Frame(frame, extraSize));
        if (extraSize > 0) {
            emit(Machine.POPop, 0, 0, extraSize);
        }
        return null;
    }

    public Object visitSequentialCommand(SequentialCommand ast, Object o) {
        ast.C1.visit(this, o);
        ast.C2.visit(this, o);
        return null;
    }

    public Object visitWhileCommand(WhileCommand ast, Object o) {
        Frame frame = (Frame) o;
        int jumpAddr, loopAddr;

        jumpAddr = nextInstrAddr;
        emit(Machine.JUMPop, 0, Machine.CBr, 0);
        loopAddr = nextInstrAddr;
        ast.C.visit(this, frame);
        patch(jumpAddr, nextInstrAddr);
        ast.E.visit(this, frame);
        emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, loopAddr);
        return null;
    }


    /* CAMBIOS NUEVOS Parte 1
     * Autores: Joshua Arcia
      -visitRestOfIfCommand
      -Modificaci??n de if para que sea con rest of if
      -visitBarCommand
     */
    @Override
    public Object visitRestOfIfCommand(RestOfIf ast, Object o) {
        Frame frame = (Frame) o;
        int  jumpAddr;

        if (ast.BC1 != null)
        {
            ast.BC1.visit(this, frame);
        }
            ast.C1.visit(this, o); // Comando del primer if
            jumpAddr = nextInstrAddr;
            emit(Machine.JUMPop, 0, Machine.CBr, 0);
            patch(jumpAddr, nextInstrAddr);
        return null;
    }

    @Override
    public Object visitBarCommand(BarCommand ast, Object o) {
        Frame frame = (Frame) o;
        int jumpifAddr, jumpAddr;
        Integer valSize = (Integer) ast.E1.visit(this,frame); //Load and call eq
        jumpifAddr = nextInstrAddr; //Get next instruction address, where to go if the instruction is false
        emit(Machine.JUMPIFop,Machine.falseRep,Machine.CBr,0); //Jumps to the next address, if patched, the command is useless

        ast.C1.visit(this,frame); // Writes the command that needs to be done
        emit(Machine.HALTop,Machine.falseRep,Machine.CBr,0);
        jumpAddr = nextInstrAddr;
        emit(Machine.JUMPop,Machine.falseRep,Machine.CBr,0);
        patch(jumpifAddr,nextInstrAddr);
        patch(jumpAddr,nextInstrAddr);

        if (ast.BC1 != null){
            ast.BC1.visit(this,frame);
        }
        return null;
    }

    @Override
    public Object visitLetInCommand(LetInCommand ast, Object o) {
        Frame frame = (Frame) o;
        int extraSize = ((Integer) ast.D.visit(this, frame)).intValue();
        ast.C.visit(this, new Frame(frame, extraSize));
        if (extraSize > 0) {
            emit(Machine.POPop, 0, 0, extraSize);
        }
        return null;
    }

    /* CAMBIOS NUEVOS Parte 3 
        * Autores: Joshua Arcia, Paula Bustos y Max Lee
        -while do
        -do while
        -until do
        -do until
        -For from to do
     */
    public Object visitLoopWhileDoCommand(LoopWhileDoCommand ast, Object o) {
        Frame frame = (Frame) o;
        // almacena la direccion de la siguiente instruccion 
        // tanto del ciclo y al salto para repetir
        int jumpAddr, loopAddr;
        jumpAddr = nextInstrAddr;
        // guardando el salto
        emit(Machine.JUMPop, 0, Machine.CBr, 0);
        loopAddr = nextInstrAddr;
        ast.C.visit(this, frame);
        // cambia la instruccion actual al salto para repetir el do
        patch(jumpAddr, nextInstrAddr);
        ast.E.visit(this, frame);
        // trueRep => repite hasta que la expresion sea falsa
        emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, loopAddr);
        return null;
    }

    @Override
    public Object visitLoopDoWhileCommand(LoopDoWhileCommand ast, Object o) {
        // Similar al anterior pero sin tener que guardar el inicio del do
        // po lo que no ocupa el jumpAddr y su manejo de direccion
        Frame frame = (Frame) o;
        int loopAddr;
        loopAddr = nextInstrAddr;
        ast.C.visit(this, frame);
        ast.E.visit(this, frame);
        emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, loopAddr);
        return null;
    }

    @Override
    public Object visitLoopUntilDoCommand(LoopUntilDoCommand ast, Object o) {
        // Similar al while do
        Frame frame = (Frame) o;
        int jumpAddr, loopAddr;
        jumpAddr = nextInstrAddr;
        emit(Machine.JUMPop, 0, Machine.CBr, 0);
        loopAddr = nextInstrAddr;
        ast.C.visit(this, frame);
        patch(jumpAddr, nextInstrAddr);
        ast.E.visit(this, frame);
        // falseRep => no repite
        emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, loopAddr);
        return null;
    }

    @Override
    public Object visitLoopDoUntilCommand(LoopDoUntilCommand ast, Object o) {
        // Similar al do while
        Frame frame = (Frame) o;
        int loopAddr;
        loopAddr = nextInstrAddr;
        ast.C.visit(this, frame);
        ast.E.visit(this, frame);
        emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, loopAddr);
        return null;
    }

    @Override
    public Object visitLoopForFromToDoCommand(LoopForFromToDoCommand ast, Object o) {

        int jumpAddr, extraSize1, extraSize2, extraSize3, left_of_to_adress,right_of_to_adress;

        Frame frame1 = (Frame) o;
        right_of_to_adress = nextInstrAddr;
        extraSize1 = (Integer) ast.E2.visit(this, frame1); // Expresi??n derecha del to
        emit(Machine.STOREop,extraSize1,Machine.CTr,right_of_to_adress); //Guarda el valor original de la expresi??n de la derecha
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        Frame frame2 = new Frame(frame1, extraSize1);
        left_of_to_adress = nextInstrAddr;
        extraSize2 = (Integer) ast.E1.visit(this, frame2); // Expresi??n izquierda del to
        emit(Machine.STOREop,extraSize2,Machine.CTr,left_of_to_adress); //Guarda el valor original de la expresi??n de la izquierda
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        extraSize3 = (Integer) ast.I.decl.visit(this,o); // loop for _variable_; pushed to stack base

        Frame frame3 = new Frame(frame1, extraSize2 + extraSize1 + extraSize3 );


        jumpAddr = nextInstrAddr;
        ast.C.visit(this,frame3);

        emit(Machine.LOADop,1,Machine.SBr,0); //Carga la base del stack
        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.succDisplacement); //Suma 1
        emit(Machine.STOREop,1,Machine.SBr,0); //Lo sobre escribe

        emit(Machine.LOADop,extraSize2,Machine.CTr,left_of_to_adress); // Carga la Expresi??n de la izquierda del to
        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.succDisplacement); // Suma 1 a la expresi??n
        emit(Machine.STOREop,extraSize2,Machine.CTr,left_of_to_adress); //Lo sobre escribe

        emit(Machine.LOADop,extraSize2,Machine.CTr,left_of_to_adress); // Carga la expresi??n de la izquierda del to
        emit(Machine.LOADop,extraSize1,Machine.CTr,right_of_to_adress); // Carga la Expresi??n de la derecha del to
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.gtDisplacement); //Verifica que la de la derecha sea mayor
        emit(Machine.JUMPIFop,Machine.falseRep,Machine.SBr,jumpAddr); //Si no es mayor, se termina el ciclo

        return null;
    }

    @Override
    public Object visitLoopForFromToWhileDoCommand(LoopForFromToWhileDoCommand ast, Object o) {
        
        int jumpAddr, extraSize1, extraSize2, extraSize3, left_of_to_adress,right_of_to_adress, exitAddr, commandAddr;

        Frame frame1 = (Frame) o;
        right_of_to_adress = nextInstrAddr;
        extraSize1 = (Integer) ast.E1.visit(this, frame1); // Expresion derecha del to
        emit(Machine.STOREop,extraSize1,Machine.CTr,right_of_to_adress); //Guarda el valor original de la expresion de la derecha
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        Frame frame2 = new Frame(frame1, extraSize1);
        
        left_of_to_adress = nextInstrAddr;
        extraSize2 = (Integer) ast.E2.visit(this, frame2); // Expresion izquierda del to
        emit(Machine.STOREop,extraSize2,Machine.CTr,left_of_to_adress); //Guarda el valor original de la expresion de la izquierda
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        extraSize3 = (Integer) ast.I.decl.visit(this,o); // loop for _variable_; pushed to stack base

        Frame frame3 = new Frame(frame1, extraSize2 + extraSize1 + extraSize3 );
        emit(Machine.LOADop,0,Machine.SBr,0); //Carga la base del stack
        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.succDisplacement); //Suma 1
        emit(Machine.STOREop,0,Machine.SBr,0); //Lo sobre escribe

        jumpAddr = nextInstrAddr;
        ast.E3.visit(this, frame3);
        
        exitAddr = nextInstrAddr;
        emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
        
        ast.C.visit(this,frame3);
        
        emit(Machine.LOADop,extraSize1,Machine.CTr,right_of_to_adress); // Carga la Expresi??n de la izquierda del to
        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.succDisplacement); //Suma 1
        emit(Machine.STOREop,extraSize1,Machine.CTr,right_of_to_adress); // Carga la Expresi??n de la izquierda del to
        emit(Machine.LOADop,extraSize1,Machine.CTr,right_of_to_adress); // Carga la Expresi??n de la izquierda del to
        emit(Machine.LOADop,extraSize2,Machine.CTr,left_of_to_adress); // Carga la Expresi??n de la derecha del to
        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.gtDisplacement); //Verifica que la de la derecha sea mayor
        emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, jumpAddr);
        patch(exitAddr, nextInstrAddr);
        return null;
    }

    @Override
    public Object visitLoopForFromToUntilDoCommand(LoopForFromToUntilDoCommand ast, Object o) {
                int jumpAddr, extraSize1, extraSize2, extraSize3, left_of_to_adress,right_of_to_adress;

        Frame frame1 = (Frame) o;
        right_of_to_adress = nextInstrAddr;
        extraSize1 = (Integer) ast.E1.visit(this, frame1); // Expresion derecha del to
        emit(Machine.STOREop,extraSize1,Machine.CTr,right_of_to_adress); //Guarda el valor original de la expresion de la derecha
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        Frame frame2 = new Frame(frame1, extraSize1);
        
        left_of_to_adress = nextInstrAddr;
        extraSize2 = (Integer) ast.E2.visit(this, frame2); // Expresion izquierda del to
        emit(Machine.STOREop,extraSize2,Machine.CTr,left_of_to_adress); //Guarda el valor original de la expresion de la izquierda
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        extraSize3 = (Integer) ast.I.decl.visit(this,o); // loop for _variable_; pushed to stack base

        Frame frame3 = new Frame(frame1, extraSize2 + extraSize1 + extraSize3 );


        jumpAddr = nextInstrAddr;
        ast.C.visit(this,frame3);

        emit(Machine.LOADop,1,Machine.SBr,0); //Carga la base del stack
        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.succDisplacement); //Suma 1
        emit(Machine.STOREop,1,Machine.SBr,0); //Lo sobre escribe

        emit(Machine.LOADop,extraSize2,Machine.CTr,left_of_to_adress); // Carga la Expresion de la izquierda del to

        //Until
        int jumpAddrU, loopAddrU;
        jumpAddrU = nextInstrAddr;
        emit(Machine.JUMPop, 0, Machine.CBr, 0);
        loopAddrU = nextInstrAddr;
        ast.E3.visit(this, frame3);
        patch(jumpAddrU, nextInstrAddr);
        ast.C.visit(this, frame3);
        // falseRep => no repite
        emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, loopAddrU);

        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.succDisplacement); // Suma 1 a la expresion
        emit(Machine.STOREop,extraSize2,Machine.CTr,left_of_to_adress); //Lo sobre escribe

        emit(Machine.LOADop,extraSize2,Machine.CTr,left_of_to_adress); // Carga la expresion de la izquierda del to
        emit(Machine.LOADop,extraSize1,Machine.CTr,right_of_to_adress); // Carga la Expresion de la derecha del to
        //emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.putintDisplacement); //Para debuggear

        emit(Machine.CALLop,Machine.SBr,Machine.PBr,Machine.gtDisplacement); //Verifica que la de la derecha sea mayor
        emit(Machine.JUMPIFop,Machine.falseRep,Machine.SBr,jumpAddr); //Si no es mayor, se termina el ciclo


        return null;
    }

    // Expressions
    public Object visitArrayExpression(ArrayExpression ast, Object o) {
        ast.type.visit(this, null);
        return ast.AA.visit(this, o);
    }

    public Object visitBinaryExpression(BinaryExpression ast, Object o) {
        Frame frame = (Frame) o;
        Integer valSize = (Integer) ast.type.visit(this, null);
        int valSize1 = ((Integer) ast.E1.visit(this, frame)).intValue();
        Frame frame1 = new Frame(frame, valSize1);
        int valSize2 = ((Integer) ast.E2.visit(this, frame1)).intValue();
        Frame frame2 = new Frame(frame.level, valSize1 + valSize2);
        ast.O.visit(this, frame2);
        return valSize;
    }

    public Object visitCallExpression(CallExpression ast, Object o) {
        Frame frame = (Frame) o;
        Integer valSize = (Integer) ast.type.visit(this, null);
        Integer argsSize = (Integer) ast.APS.visit(this, frame);
        ast.I.visit(this, new Frame(frame.level, argsSize));
        return valSize;
    }

    public Object visitCharacterExpression(CharacterExpression ast,
            Object o) {
        Frame frame = (Frame) o;
        Integer valSize = (Integer) ast.type.visit(this, null);
        emit(Machine.LOADLop, 0, 0, ast.CL.spelling.charAt(1));
        return valSize;
    }

    public Object visitEmptyExpression(EmptyExpression ast, Object o) {
        return 0;
    }

    public Object visitIfExpression(IfExpression ast, Object o) {
        Frame frame = (Frame) o;
        Integer valSize;
        int jumpifAddr, jumpAddr;

        ast.type.visit(this, null);
        ast.E1.visit(this, frame);
        jumpifAddr = nextInstrAddr;
        emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
        valSize = (Integer) ast.E2.visit(this, frame);
        jumpAddr = nextInstrAddr;
        emit(Machine.JUMPop, 0, Machine.CBr, 0);
        patch(jumpifAddr, nextInstrAddr);
        valSize = (Integer) ast.E3.visit(this, frame);
        patch(jumpAddr, nextInstrAddr);
        return valSize;
    }

    public Object visitIntegerExpression(IntegerExpression ast, Object o) {
        Frame frame = (Frame) o;
        Integer valSize = (Integer) ast.type.visit(this, null);
        emit(Machine.LOADLop, 0, 0, Integer.parseInt(ast.IL.spelling));
        return valSize;
    }

    public Object visitLetExpression(LetExpression ast, Object o) {
        Frame frame = (Frame) o;
        ast.type.visit(this, null);
        int extraSize = ((Integer) ast.D.visit(this, frame)).intValue();
        Frame frame1 = new Frame(frame, extraSize);
        Integer valSize = (Integer) ast.E.visit(this, frame1);
        if (extraSize > 0) {
            emit(Machine.POPop, valSize.intValue(), 0, extraSize);
        }
        return valSize;
    }

    public Object visitRecordExpression(RecordExpression ast, Object o) {
        ast.type.visit(this, null);
        return ast.RA.visit(this, o);
    }

    public Object visitUnaryExpression(UnaryExpression ast, Object o) {
        Frame frame = (Frame) o;
        Integer valSize = (Integer) ast.type.visit(this, null);
        ast.E.visit(this, frame);
        ast.O.visit(this, new Frame(frame.level, valSize.intValue()));
        return valSize;
    }

    public Object visitVnameExpression(VnameExpression ast, Object o) {
        Frame frame = (Frame) o;
        Integer valSize = (Integer) ast.type.visit(this, null);
        encodeFetch(ast.V, frame, valSize.intValue());
        return valSize;
    }

    // Declarations
    public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast,
            Object o) {
        return Integer.valueOf(0);
    }

    public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
        Frame frame = (Frame) o;
        int extraSize = 0;

        if (ast.E instanceof CharacterExpression) {
            CharacterLiteral CL = ((CharacterExpression) ast.E).CL;
            ast.entity = new KnownValue(Machine.characterSize,
                    characterValuation(CL.spelling));
        } else if (ast.E instanceof IntegerExpression) {
            IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
            ast.entity = new KnownValue(Machine.integerSize,
                    Integer.parseInt(IL.spelling));
        } else {
            int valSize = ((Integer) ast.E.visit(this, frame)).intValue();
            ast.entity = new UnknownValue(valSize, frame.level, frame.size);
            extraSize = valSize;
        }
        writeTableDetails(ast);
        return Integer.valueOf(extraSize);
    }

    public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
        Frame frame = (Frame) o;
        int jumpAddr = nextInstrAddr;
        int argsSize = 0, valSize = 0;

        emit(Machine.JUMPop, 0, Machine.CBr, 0);
        ast.entity = new KnownRoutine(Machine.closureSize, frame.level, nextInstrAddr);
        writeTableDetails(ast);
        if (frame.level == Machine.maxRoutineLevel) {
            reporter.reportRestriction("can't nest routines more than 7 deep");
        } else {
            Frame frame1 = new Frame(frame.level + 1, 0);
            argsSize = ((Integer) ast.FPS.visit(this, frame1)).intValue();
            Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
            valSize = ((Integer) ast.E.visit(this, frame2)).intValue();
        }
        emit(Machine.RETURNop, valSize, 0, argsSize);
        patch(jumpAddr, nextInstrAddr);
        return Integer.valueOf(0);
    }

    public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
        Frame frame = (Frame) o;
        int jumpAddr = nextInstrAddr;
        int argsSize = 0;

        emit(Machine.JUMPop, 0, Machine.CBr, 0);
        ast.entity = new KnownRoutine(Machine.closureSize, frame.level,
                nextInstrAddr);
        writeTableDetails(ast);
        if (frame.level == Machine.maxRoutineLevel) {
            reporter.reportRestriction("can't nest routines so deeply");
        } else {
            Frame frame1 = new Frame(frame.level + 1, 0);
            argsSize = ((Integer) ast.FPS.visit(this, frame1)).intValue();
            Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
            ast.C.visit(this, frame2);
        }
        emit(Machine.RETURNop, 0, 0, argsSize);
        patch(jumpAddr, nextInstrAddr);
        return Integer.valueOf(0);
    }

    public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
        Frame frame = (Frame) o;
        int extraSize1, extraSize2;

        extraSize1 = ((Integer) ast.D1.visit(this, frame)).intValue();
        Frame frame1 = new Frame(frame, extraSize1);
        extraSize2 = ((Integer) ast.D2.visit(this, frame1)).intValue();
        return Integer.valueOf(extraSize1 + extraSize2);
    }

    public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
        // just to ensure the type's representation is decided
        ast.T.visit(this, null);
        return Integer.valueOf(0);
    }

    public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast,
            Object o) {
        return Integer.valueOf(0);
    }

    public Object visitVarDeclaration(VarDeclaration ast, Object o) {
        Frame frame = (Frame) o;
        int extraSize;

        extraSize = ((Integer) ast.T.visit(this, null)).intValue();
        emit(Machine.PUSHop, 0, 0, extraSize);
        ast.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
        writeTableDetails(ast);
        return Integer.valueOf(extraSize);
    }

    /* CAMBIOS NUEVOS Parte 1
   * Autores: Joshua Arcia y Max Lee 
      -VisitVarInitDeclaration
      -VisitLocalDeclaration
      -VisitProcFuncsDeclaration
      *
     */
    @Override
    public Object visitVarInitDeclaration(VarInitDeclaration ast, Object o) {
        // Igual que var declaration pero usando la expresion a evaluar
        Frame frame = (Frame) o;
        
        int extraSize;
        // Define el espacio extra que debe de consumir la expresion a evaluar
        extraSize = ((Integer) ast.E.visit(this, frame)).intValue();
        // Realiza el espacio de la expresion 
        emit(Machine.PUSHop, 0, 0, extraSize);
        // Settea el valor de direccion de esta expresion
        ast.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
        // Escribe en la tabla el valor de la expresion 
        writeTableDetails(ast);
        return extraSize;
    }

    @Override
    public Object visitLocalDeclaration(LocalDeclaration ast, Object o) {
        // Declarar las variables de los espacios extra de cada declaracion
        int extraSize1, extraSize2;
        
        // Crear el cuadro de la primera declaracion
        Frame frame1 = (Frame) o;
        
        // Guarda el tamannio de la 1era declaracion
        extraSize1 = ((Integer) ast.D1.visit(this, frame1)).intValue();
        
        // Crear el cuadro de la primera declaracion sumando el tamannio del anterior
        Frame frame2 = new Frame(frame1, extraSize1);
        
        // Guarda el tamannio de la 2da declaracion
        extraSize2 = ((Integer) ast.D2.visit(this, frame2)).intValue() + extraSize1;
        // Devuelve el tamannio de la cantidad a guardar.
        return extraSize2;
    }

    @Override
    public Object visitProcFuncsDeclaration(ProcFuncDeclaration ast, Object o) {
        // Crear dos espacios para el proc y func
        Frame frame1 = (Frame) o;
        Frame frame2 = (Frame) o;
        // Declarar las variables de los espacios extra de cada declaracion
        int extraSize1 = 0;
        int extraSize2 = 0;
        if (ast.PD != null) {
            // Guarda el tamannio de la 1era declaracion
            extraSize1 = ((Integer) ast.PD.visit(this, frame1)).intValue();
            ast.PD.visit(this, null);
            if (ast.PF != null) {
                // Guarda el tamannio de la 2da declaracion
                extraSize2 = ((Integer) ast.PF.visit(this, frame2)).intValue() + extraSize1;
            }
        }
        // Mismo proceso
        if (ast.PF != null) {
            extraSize1 = ((Integer) ast.PF.visit(this, frame2)).intValue();
            if (ast.PF != null) {
                extraSize2 = ((Integer) ast.PF.visit(this, frame2)).intValue() + extraSize1;
            }
        }
        return extraSize2;
    }

    // Array Aggregates
    public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast,
            Object o) {
        Frame frame = (Frame) o;
        int elemSize = ((Integer) ast.E.visit(this, frame)).intValue();
        Frame frame1 = new Frame(frame, elemSize);
        int arraySize = ((Integer) ast.AA.visit(this, frame1)).intValue();
        return Integer.valueOf(elemSize + arraySize);
    }

    public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
        return ast.E.visit(this, o);
    }

    // Record Aggregates
    public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast,
            Object o) {
        Frame frame = (Frame) o;
        int fieldSize = ((Integer) ast.E.visit(this, frame)).intValue();
        Frame frame1 = new Frame(frame, fieldSize);
        int recordSize = ((Integer) ast.RA.visit(this, frame1)).intValue();
        return Integer.valueOf(fieldSize + recordSize);
    }

    public Object visitSingleRecordAggregate(SingleRecordAggregate ast,
            Object o) {
        return ast.E.visit(this, o);
    }

    // Formal Parameters
    public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
        Frame frame = (Frame) o;
        int valSize = ((Integer) ast.T.visit(this, null)).intValue();
        ast.entity = new UnknownValue(valSize, frame.level, -frame.size - valSize);
        writeTableDetails(ast);
        return Integer.valueOf(valSize);
    }

    public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
        Frame frame = (Frame) o;
        int argsSize = Machine.closureSize;
        ast.entity = new UnknownRoutine(Machine.closureSize, frame.level,
                -frame.size - argsSize);
        writeTableDetails(ast);
        return Integer.valueOf(argsSize);
    }

    public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
        Frame frame = (Frame) o;
        int argsSize = Machine.closureSize;
        ast.entity = new UnknownRoutine(Machine.closureSize, frame.level,
                -frame.size - argsSize);
        writeTableDetails(ast);
        return Integer.valueOf(argsSize);
    }

    public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
        Frame frame = (Frame) o;
        ast.T.visit(this, null);
        ast.entity = new UnknownAddress(Machine.addressSize, frame.level,
                -frame.size - Machine.addressSize);
        writeTableDetails(ast);
        return Integer.valueOf(Machine.addressSize);
    }

    public Object visitEmptyFormalParameterSequence(
            EmptyFormalParameterSequence ast, Object o) {
        return Integer.valueOf(0);
    }

    public Object visitMultipleFormalParameterSequence(
            MultipleFormalParameterSequence ast, Object o) {
        Frame frame = (Frame) o;
        int argsSize1 = ((Integer) ast.FPS.visit(this, frame)).intValue();
        Frame frame1 = new Frame(frame, argsSize1);
        int argsSize2 = ((Integer) ast.FP.visit(this, frame1)).intValue();
        return Integer.valueOf(argsSize1 + argsSize2);
    }

    public Object visitSingleFormalParameterSequence(
            SingleFormalParameterSequence ast, Object o) {
        return ast.FP.visit(this, o);
    }

    // Actual Parameters
    public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
        return ast.E.visit(this, o);
    }

    public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
        Frame frame = (Frame) o;
        if (ast.I.decl.entity instanceof KnownRoutine) {
            ObjectAddress address = ((KnownRoutine) ast.I.decl.entity).address;
            // static link, code address
            emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0);
            emit(Machine.LOADAop, 0, Machine.CBr, address.displacement);
        } else if (ast.I.decl.entity instanceof UnknownRoutine) {
            ObjectAddress address = ((UnknownRoutine) ast.I.decl.entity).address;
            emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
                    address.level), address.displacement);
        } else if (ast.I.decl.entity instanceof PrimitiveRoutine) {
            int displacement = ((PrimitiveRoutine) ast.I.decl.entity).displacement;
            // static link, code address
            emit(Machine.LOADAop, 0, Machine.SBr, 0);
            emit(Machine.LOADAop, 0, Machine.PBr, displacement);
        }
        return Integer.valueOf(Machine.closureSize);
    }

    public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
        Frame frame = (Frame) o;
        if (ast.I.decl.entity instanceof KnownRoutine) {
            ObjectAddress address = ((KnownRoutine) ast.I.decl.entity).address;
            // static link, code address
            emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0);
            emit(Machine.LOADAop, 0, Machine.CBr, address.displacement);
        } else if (ast.I.decl.entity instanceof UnknownRoutine) {
            ObjectAddress address = ((UnknownRoutine) ast.I.decl.entity).address;
            emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
                    address.level), address.displacement);
        } else if (ast.I.decl.entity instanceof PrimitiveRoutine) {
            int displacement = ((PrimitiveRoutine) ast.I.decl.entity).displacement;
            // static link, code address
            emit(Machine.LOADAop, 0, Machine.SBr, 0);
            emit(Machine.LOADAop, 0, Machine.PBr, displacement);
        }
        return Integer.valueOf(Machine.closureSize);
    }

    public Object visitVarActualParameter(VarActualParameter ast, Object o) {
        encodeFetchAddress(ast.V, (Frame) o);
        return Integer.valueOf(Machine.addressSize);
    }

    public Object visitEmptyActualParameterSequence(
            EmptyActualParameterSequence ast, Object o) {
        return Integer.valueOf(0);
    }

    public Object visitMultipleActualParameterSequence(
            MultipleActualParameterSequence ast, Object o) {
        Frame frame = (Frame) o;
        int argsSize1 = ((Integer) ast.AP.visit(this, frame)).intValue();
        Frame frame1 = new Frame(frame, argsSize1);
        int argsSize2 = ((Integer) ast.APS.visit(this, frame1)).intValue();
        return Integer.valueOf(argsSize1 + argsSize2);
    }

    public Object visitSingleActualParameterSequence(
            SingleActualParameterSequence ast, Object o) {
        return ast.AP.visit(this, o);
    }

    // Type Denoters
    public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
        return Integer.valueOf(0);
    }

    public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
        int typeSize;
        if (ast.entity == null) {
            int elemSize = ((Integer) ast.T.visit(this, null)).intValue();
            typeSize = Integer.parseInt(ast.IL.spelling) * elemSize;
            ast.entity = new TypeRepresentation(typeSize);
            writeTableDetails(ast);
        } else {
            typeSize = ast.entity.size;
        }
        return Integer.valueOf(typeSize);
    }

    public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
        if (ast.entity == null) {
            ast.entity = new TypeRepresentation(Machine.booleanSize);
            writeTableDetails(ast);
        }
        return Integer.valueOf(Machine.booleanSize);
    }

    public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
        if (ast.entity == null) {
            ast.entity = new TypeRepresentation(Machine.characterSize);
            writeTableDetails(ast);
        }
        return Integer.valueOf(Machine.characterSize);
    }

    public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
        return Integer.valueOf(0);
    }

    public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast,
            Object o) {
        return Integer.valueOf(0);
    }

    public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
        if (ast.entity == null) {
            ast.entity = new TypeRepresentation(Machine.integerSize);
            writeTableDetails(ast);
        }
        return Integer.valueOf(Machine.integerSize);
    }

    public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
        int typeSize;
        if (ast.entity == null) {
            typeSize = ((Integer) ast.FT.visit(this, Integer.valueOf(0))).intValue();
            ast.entity = new TypeRepresentation(typeSize);
            writeTableDetails(ast);
        } else {
            typeSize = ast.entity.size;
        }
        return Integer.valueOf(typeSize);
    }

    public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast,
            Object o) {
        int offset = ((Integer) o).intValue();
        int fieldSize;

        if (ast.entity == null) {
            fieldSize = ((Integer) ast.T.visit(this, null)).intValue();
            ast.entity = new Field(fieldSize, offset);
            writeTableDetails(ast);
        } else {
            fieldSize = ast.entity.size;
        }

        Integer offset1 = Integer.valueOf(offset + fieldSize);
        int recSize = ((Integer) ast.FT.visit(this, offset1)).intValue();
        return Integer.valueOf(fieldSize + recSize);
    }

    public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast,
            Object o) {
        int offset = ((Integer) o).intValue();
        int fieldSize;

        if (ast.entity == null) {
            fieldSize = ((Integer) ast.T.visit(this, null)).intValue();
            ast.entity = new Field(fieldSize, offset);
            writeTableDetails(ast);
        } else {
            fieldSize = ast.entity.size;
        }

        return Integer.valueOf(fieldSize);
    }

    // Literals, Identifiers and Operators
    public Object visitCharacterLiteral(CharacterLiteral ast, Object o) {
        return null;
    }

    public Object visitIdentifier(Identifier ast, Object o) {
        Frame frame = (Frame) o;
        if (ast.decl.entity instanceof KnownRoutine) {
            ObjectAddress address = ((KnownRoutine) ast.decl.entity).address;
            emit(Machine.CALLop, displayRegister(frame.level, address.level),
                    Machine.CBr, address.displacement);
        } else if (ast.decl.entity instanceof UnknownRoutine) {
            ObjectAddress address = ((UnknownRoutine) ast.decl.entity).address;
            emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
                    address.level), address.displacement);
            emit(Machine.CALLIop, 0, 0, 0);
        } else if (ast.decl.entity instanceof PrimitiveRoutine) {
            int displacement = ((PrimitiveRoutine) ast.decl.entity).displacement;
            if (displacement != Machine.idDisplacement) {
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
            }
        } else if (ast.decl.entity instanceof EqualityRoutine) { // "=" or "\="
            int displacement = ((EqualityRoutine) ast.decl.entity).displacement;
            emit(Machine.LOADLop, 0, 0, frame.size / 2);
            emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
        }
        return null;
    }

    public Object visitIntegerLiteral(IntegerLiteral ast, Object o) {
        return null;
    }

    public Object visitOperator(Operator ast, Object o) {
        Frame frame = (Frame) o;
        if (ast.decl.entity instanceof KnownRoutine) {
            ObjectAddress address = ((KnownRoutine) ast.decl.entity).address;
            emit(Machine.CALLop, displayRegister(frame.level, address.level),
                    Machine.CBr, address.displacement);
        } else if (ast.decl.entity instanceof UnknownRoutine) {
            ObjectAddress address = ((UnknownRoutine) ast.decl.entity).address;
            emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
                    address.level), address.displacement);
            emit(Machine.CALLIop, 0, 0, 0);
        } else if (ast.decl.entity instanceof PrimitiveRoutine) {
            int displacement = ((PrimitiveRoutine) ast.decl.entity).displacement;
            if (displacement != Machine.idDisplacement) {
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
            }
        } else if (ast.decl.entity instanceof EqualityRoutine) { // "=" or "\="
            int displacement = ((EqualityRoutine) ast.decl.entity).displacement;
            emit(Machine.LOADLop, 0, 0, frame.size / 2);
            emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
        }
        return null;
    }

    // Value-or-variable names
    public Object visitDotVname(DotVname ast, Object o) {
        Frame frame = (Frame) o;
        RuntimeEntity baseObject = (RuntimeEntity) ast.V.visit(this, frame);
        ast.offset = ast.V.offset + ((Field) ast.I.decl.entity).fieldOffset;
        // I.decl points to the appropriate record field
        ast.indexed = ast.V.indexed;
        return baseObject;
    }

    public Object visitSimpleVname(SimpleVname ast, Object o) {
        ast.offset = 0;
        ast.indexed = false;
        return ast.I.decl.entity;
    }

    public Object visitSubscriptVname(SubscriptVname ast, Object o) {
        Frame frame = (Frame) o;
        RuntimeEntity baseObject;
        int elemSize, indexSize;

        baseObject = (RuntimeEntity) ast.V.visit(this, frame);
        ast.offset = ast.V.offset;
        ast.indexed = ast.V.indexed;
        elemSize = ((Integer) ast.type.visit(this, null)).intValue();
        if (ast.E instanceof IntegerExpression) {
            IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
            ast.offset = ast.offset + Integer.parseInt(IL.spelling) * elemSize;
        } else {
            // v-name is indexed by a proper expression, not a literal
            if (ast.indexed) {
                frame.size = frame.size + Machine.integerSize;
            }
            indexSize = ((Integer) ast.E.visit(this, frame)).intValue();
            if (elemSize != 1) {
                emit(Machine.LOADLop, 0, 0, elemSize);
                emit(Machine.CALLop, Machine.SBr, Machine.PBr,
                        Machine.multDisplacement);
            }
            if (ast.indexed) {
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            } else {
                ast.indexed = true;
            }
        }
        return baseObject;
    }

    // Programs
    public Object visitProgram(Program ast, Object o) {
        return ast.C.visit(this, o);
    }

    public Encoder(ErrorReporter reporter) {
        this.reporter = reporter;
        nextInstrAddr = Machine.CB;
        elaborateStdEnvironment();
    }

    private final ErrorReporter reporter;

    // Generates code to run a program.
    // showingTable is true iff entity description details
    // are to be displayed.
    public void encodeRun(Program theAST, boolean showingTable) {
        tableDetailsReqd = showingTable;
        //startCodeGeneration();
        theAST.visit(this, new Frame(0, 0));
        emit(Machine.HALTop, 0, 0, 0);
    }

    // Decides run-time representation of a standard constant.
    private void elaborateStdConst(Declaration constDeclaration,
                                   int value) {

        if (constDeclaration instanceof ConstDeclaration) {
            ConstDeclaration decl = (ConstDeclaration) constDeclaration;
            int typeSize = ((Integer) decl.E.type.visit(this, null)).intValue();
            decl.entity = new KnownValue(typeSize, value);
            writeTableDetails(constDeclaration);
        }
    }

    // Decides run-time representation of a standard routine.
    private void elaborateStdPrimRoutine(Declaration routineDeclaration,
                                         int routineOffset) {
        routineDeclaration.entity = new PrimitiveRoutine(Machine.closureSize, routineOffset);
        writeTableDetails(routineDeclaration);
    }

    private void elaborateStdEqRoutine(Declaration routineDeclaration,
                                       int routineOffset) {
        routineDeclaration.entity = new EqualityRoutine(Machine.closureSize, routineOffset);
        writeTableDetails(routineDeclaration);
    }

    private void elaborateStdRoutine(Declaration routineDeclaration,
                                     int routineOffset) {
        routineDeclaration.entity = new KnownRoutine(Machine.closureSize, 0, routineOffset);
        writeTableDetails(routineDeclaration);
    }

    private void elaborateStdEnvironment() {
        tableDetailsReqd = false;
        elaborateStdConst(StdEnvironment.falseDecl, Machine.falseRep);
        elaborateStdConst(StdEnvironment.trueDecl, Machine.trueRep);
        elaborateStdPrimRoutine(StdEnvironment.notDecl, Machine.notDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.andDecl, Machine.andDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.orDecl, Machine.orDisplacement);
        elaborateStdConst(StdEnvironment.maxintDecl, Machine.maxintRep);
        elaborateStdPrimRoutine(StdEnvironment.addDecl, Machine.addDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.subtractDecl, Machine.subDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.multiplyDecl, Machine.multDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.divideDecl, Machine.divDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.moduloDecl, Machine.modDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.lessDecl, Machine.ltDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.notgreaterDecl, Machine.leDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.greaterDecl, Machine.gtDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.notlessDecl, Machine.geDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.chrDecl, Machine.idDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.ordDecl, Machine.idDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.eolDecl, Machine.eolDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.eofDecl, Machine.eofDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.getDecl, Machine.getDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.putDecl, Machine.putDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.getintDecl, Machine.getintDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.putintDecl, Machine.putintDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.geteolDecl, Machine.geteolDisplacement);
        elaborateStdPrimRoutine(StdEnvironment.puteolDecl, Machine.puteolDisplacement);
        elaborateStdEqRoutine(StdEnvironment.equalDecl, Machine.eqDisplacement);
        elaborateStdEqRoutine(StdEnvironment.unequalDecl, Machine.neDisplacement);
    }

    // Saves the object program in the named file.
    public void saveObjectProgram(String objectName) {
        FileOutputStream objectFile = null;
        DataOutputStream objectStream = null;

        int addr;

        try {
            objectFile = new FileOutputStream(objectName);
            objectStream = new DataOutputStream(objectFile);

            addr = Machine.CB;
            for (addr = Machine.CB; addr < nextInstrAddr; addr++) {
                Machine.code[addr].write(objectStream);
            }
            objectFile.close();
        } catch (FileNotFoundException s) {
            System.err.println("Error opening object file: " + s);
        } catch (IOException s) {
            System.err.println("Error writing object file: " + s);
        }
    }

    boolean tableDetailsReqd;

    public static void writeTableDetails(AST ast) {
    }

    // OBJECT CODE
    // Implementation notes:
    // Object code is generated directly into the TAM Code Store, starting at CB.
    // The address of the next instruction is held in nextInstrAddr.
    private int nextInstrAddr;

    // Appends an instruction, with the given fields, to the object code.
    private void emit(int op, int n, int r, int d) {
        Instruction nextInstr = new Instruction();
        if (n > 255) {
            reporter.reportRestriction("length of operand can't exceed 255 words");
            n = 255; // to allow code generation to continue
        }
        nextInstr.op = op;
        nextInstr.n = n;
        nextInstr.r = r;
        nextInstr.d = d;
        if (nextInstrAddr == Machine.PB) {
            reporter.reportRestriction("too many instructions for code segment");
        } else {
            Machine.code[nextInstrAddr] = nextInstr;
            nextInstrAddr = nextInstrAddr + 1;
        }
    }

    // Patches the d-field of the instruction at address addr.
    private void patch(int addr, int d) {
        Machine.code[addr].d = d;
    }

    // DATA REPRESENTATION
    public int characterValuation(String spelling) {
        // Returns the machine representation of the given character literal.
        return spelling.charAt(1);
        // since the character literal is of the form 'x'}
    }

    // REGISTERS
    // Returns the register number appropriate for object code at currentLevel
    // to address a data object at objectLevel.
    private int displayRegister(int currentLevel, int objectLevel) {
        if (objectLevel == 0) {
            return Machine.SBr;
        } else if (currentLevel - objectLevel <= 6) {
            return Machine.LBr + currentLevel - objectLevel; // LBr|L1r|...|L6r
        } else {
            reporter.reportRestriction("can't access data more than 6 levels out");
            return Machine.L6r;  // to allow code generation to continue
        }
    }

    // Generates code to fetch the value of a named constant or variable
    // and push it on to the stack.
    // currentLevel is the routine level where the vname occurs.
    // frameSize is the anticipated size of the local stack frame when
    // the constant or variable is fetched at run-time.
    // valSize is the size of the constant or variable's value.
    private void encodeStore(Vname V, Frame frame, int valSize) {

        RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
        // If indexed = true, code will have been generated to load an index value.
        if (valSize > 255) {
            reporter.reportRestriction("can't store values larger than 255 words");
            valSize = 255; // to allow code generation to continue
        }
        if (baseObject instanceof KnownAddress) {
            ObjectAddress address = ((KnownAddress) baseObject).address;
            if (V.indexed) {
                emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
                        address.displacement + V.offset);
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
                emit(Machine.STOREIop, valSize, 0, 0);
            } else {
                emit(Machine.STOREop, valSize, displayRegister(frame.level,
                        address.level), address.displacement + V.offset);
            }
        } else if (baseObject instanceof UnknownAddress) {
            ObjectAddress address = ((UnknownAddress) baseObject).address;
            emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level,
                    address.level), address.displacement);
            if (V.indexed) {
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            }
            if (V.offset != 0) {
                emit(Machine.LOADLop, 0, 0, V.offset);
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            }
            emit(Machine.STOREIop, valSize, 0, 0);
        }
    }

    // Generates code to fetch the value of a named constant or variable
    // and push it on to the stack.
    // currentLevel is the routine level where the vname occurs.
    // frameSize is the anticipated size of the local stack frame when
    // the constant or variable is fetched at run-time.
    // valSize is the size of the constant or variable's value.
    private void encodeFetch(Vname V, Frame frame, int valSize) {

        RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
        // If indexed = true, code will have been generated to load an index value.
        if (valSize > 255) {
            reporter.reportRestriction("can't load values larger than 255 words");
            valSize = 255; // to allow code generation to continue
        }
        if (baseObject instanceof KnownValue) {
            // presumably offset = 0 and indexed = false
            int value = ((KnownValue) baseObject).value;
            emit(Machine.LOADLop, 0, 0, value);
        } else if ((baseObject instanceof UnknownValue)
                || (baseObject instanceof KnownAddress)) {
            ObjectAddress address = (baseObject instanceof UnknownValue)
                    ? ((UnknownValue) baseObject).address
                    : ((KnownAddress) baseObject).address;
            if (V.indexed) {
                emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
                        address.displacement + V.offset);
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
                emit(Machine.LOADIop, valSize, 0, 0);
            } else {
                emit(Machine.LOADop, valSize, displayRegister(frame.level,
                        address.level), address.displacement + V.offset);
            }
        } else if (baseObject instanceof UnknownAddress) {
            ObjectAddress address = ((UnknownAddress) baseObject).address;
            emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level,
                    address.level), address.displacement);
            if (V.indexed) {
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            }
            if (V.offset != 0) {
                emit(Machine.LOADLop, 0, 0, V.offset);
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            }
            emit(Machine.LOADIop, valSize, 0, 0);
        }
    }

    // Generates code to compute and push the address of a named variable.
    // vname is the program phrase that names this variable.
    // currentLevel is the routine level where the vname occurs.
    // frameSize is the anticipated size of the local stack frame when
    // the variable is addressed at run-time.
    private void encodeFetchAddress(Vname V, Frame frame) {

        RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
        // If indexed = true, code will have been generated to load an index value.
        if (baseObject instanceof KnownAddress) {
            ObjectAddress address = ((KnownAddress) baseObject).address;
            emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
                    address.displacement + V.offset);
            if (V.indexed) {
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            }
        } else if (baseObject instanceof UnknownAddress) {
            ObjectAddress address = ((UnknownAddress) baseObject).address;
            emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level,
                    address.level), address.displacement);
            if (V.indexed) {
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            }
            if (V.offset != 0) {
                emit(Machine.LOADLop, 0, 0, V.offset);
                emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
            }
        }
    }

}
