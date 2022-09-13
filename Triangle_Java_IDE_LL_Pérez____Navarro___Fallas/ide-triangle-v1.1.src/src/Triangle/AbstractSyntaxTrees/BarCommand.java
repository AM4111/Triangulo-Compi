package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class BarCommand extends Command{
    public BarCommand(Expression e1AST, Command c1AST, BarCommand bc1AST, SourcePosition thePosition) {
        super(thePosition);
        E1 = e1AST;
        C1 = c1AST;
        BC1 = bc1AST;

    }

    public BarCommand(Expression e1AST, Command c1AST, SourcePosition thePosition) {
        super(thePosition);
        E1 = e1AST;
        C1 = c1AST;

    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitBarCommand(this,o);
    }

    public Expression E1;
    public Command C1;
    public BarCommand BC1 = null;
}
