/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 *
 * @author maxle
 */
public class LoopForFromToDoCommand extends Command {

    public Identifier I;
    public Expression E1;
    public Expression E2;
    public Command C;

    public  LoopForFromToDoCommand(Identifier iAST, Expression eAST1, Expression eAST2, Command cAST, SourcePosition thePosition) {
        super(thePosition);
        I = iAST;
        E1 = eAST1;
        E2 = eAST2;
        C = cAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitLoopForFromToDoCommand(this, o);
    }
}
