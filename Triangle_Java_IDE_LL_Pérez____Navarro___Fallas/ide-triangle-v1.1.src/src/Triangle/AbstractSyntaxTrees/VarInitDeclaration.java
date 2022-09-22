package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class VarInitDeclaration extends Declaration{

    public VarInitDeclaration(Identifier iAST, Expression eAST, SourcePosition thePosition) {
        super(thePosition);
        I = iAST;
        E = eAST;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitVarInitDeclaration(this,o);
    }

    public Identifier I;
    public Expression E;

}
