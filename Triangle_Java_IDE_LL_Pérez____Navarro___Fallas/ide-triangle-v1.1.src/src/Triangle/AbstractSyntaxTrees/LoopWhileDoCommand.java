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
public class LoopWhileDoCommand extends Command {
    public Expression E;
    public Command C;

    public LoopWhileDoCommand(Expression eAST, Command cAST, SourcePosition thePosition) {
        super(thePosition);
        E = eAST;
        C = cAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitLoopWhileDoCommand(this, o);
    }
}
