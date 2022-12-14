package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RestOfIf extends Command {

    public RestOfIf(Command c1AST ,SourcePosition thePosition) {
        super(thePosition);
        C1 = c1AST;
    }
    public RestOfIf(Command c1AST , BarCommand bc1AST, SourcePosition thePosition) {
        super(thePosition);
        C1 = c1AST;
        BC1 = bc1AST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitRestOfIfCommand(this,o);
    }

    public Command C1;
    public BarCommand BC1 = null;

}
