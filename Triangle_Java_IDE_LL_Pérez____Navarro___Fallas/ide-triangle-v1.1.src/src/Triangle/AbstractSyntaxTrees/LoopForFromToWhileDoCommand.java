/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 *
 * @author pauma
 */
//| "loop" [ Identifier ] "for" Identifier "from" Expression "to" Expression"while" Expression "do" Command "end"


public class LoopForFromToWhileDoCommand  extends Command {

    public Identifier I;
    public Expression E1;
    public Expression E2;
    public Expression E3;
    public Command C;

    public  LoopForFromToWhileDoCommand(Identifier iAST, Expression eAST1, Expression eAST2, Expression eAST3, Command cAST, SourcePosition thePosition) {
        super(thePosition);
        I = iAST;
        E1 = eAST1;
        E2 = eAST2;
        E3 = eAST3;
        C = cAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitLoopForFromToWhileDoCommand(this, o);
    }
    
}


