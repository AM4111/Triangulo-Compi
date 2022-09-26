package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ProcFuncDeclaration extends Declaration {
    public ProcFuncDeclaration(ProcDeclaration pdAST, SourcePosition thePosition) {
        super(thePosition);
        PD = pdAST;
    }

    public ProcFuncDeclaration(FuncDeclaration fdAST, SourcePosition thePosition) {
        super(thePosition);
        FD = fdAST;
    }
    public ProcFuncDeclaration(ProcDeclaration pdAST, ProcFuncDeclaration pfAST, SourcePosition thePosition) {
        super(thePosition);
        PD = pdAST;
        PF = pfAST;
    }

    public ProcFuncDeclaration(FuncDeclaration fdAST, ProcFuncDeclaration pfAST, SourcePosition thePosition) {
        super(thePosition);
        FD = fdAST;
        PF = pfAST;
    }


    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitProcFuncsDeclaration(this,o);
    }

    public ProcDeclaration PD = null;
    public FuncDeclaration FD = null;
    public ProcFuncDeclaration PF = null;
}
