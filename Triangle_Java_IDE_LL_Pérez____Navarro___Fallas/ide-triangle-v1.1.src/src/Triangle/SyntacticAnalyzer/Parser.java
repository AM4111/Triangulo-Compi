/*
 * @(#)Parser.java                        2.1 2003/10/07
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
package Triangle.SyntacticAnalyzer;

import Triangle.AbstractSyntaxTrees.*;
import Triangle.ErrorReporter;

//import java.lang.runtime.SwitchBootstraps;

public class Parser {

    private Scanner lexicalAnalyser;
    private ErrorReporter errorReporter;
    private Token currentToken;
    private SourcePosition previousTokenPosition;

    public Parser(Scanner lexer, ErrorReporter reporter) {
        lexicalAnalyser = lexer;
        errorReporter = reporter;
        previousTokenPosition = new SourcePosition();
    }

// accept checks whether the current token matches tokenExpected.
// If so, fetches the next token.
// If not, reports a syntactic error.
    void accept(int tokenExpected) throws SyntaxError {
        if (currentToken.kind == tokenExpected) {
            previousTokenPosition = currentToken.position;
            currentToken = lexicalAnalyser.scan();
        } else {
            syntacticError("\"%\" expected here", Token.spell(tokenExpected));
        }
    }

    void acceptIt() {
        previousTokenPosition = currentToken.position;
        currentToken = lexicalAnalyser.scan();
    }

// start records the position of the start of a phrase.
// This is defined to be the position of the first
// character of the first token of the phrase.
    void start(SourcePosition position) {
        position.start = currentToken.position.start;
    }

// finish records the position of the end of a phrase.
// This is defined to be the position of the last
// character of the last token of the phrase.
    void finish(SourcePosition position) {
        position.finish = previousTokenPosition.finish;
    }

    void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
        SourcePosition pos = currentToken.position;
        errorReporter.reportError(messageTemplate, tokenQuoted, pos);
        throw (new SyntaxError());
    }

///////////////////////////////////////////////////////////////////////////////
//
// PROGRAMS
//
///////////////////////////////////////////////////////////////////////////////
    public Program parseProgram() {

        Program programAST = null;

        previousTokenPosition.start = 0;
        previousTokenPosition.finish = 0;
        currentToken = lexicalAnalyser.scan();

        try {
            Command cAST = parseCommand();
            programAST = new Program(cAST, previousTokenPosition);
            if (currentToken.kind != Token.EOT) {
                syntacticError("\"%\" not expected after end of program",
                        currentToken.spelling);
            }
        } catch (SyntaxError s) {
            return null;
        }
        return programAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// LITERALS
//
///////////////////////////////////////////////////////////////////////////////
// parseIntegerLiteral parses an integer-literal, and constructs
// a leaf AST to represent it.
    IntegerLiteral parseIntegerLiteral() throws SyntaxError {
        IntegerLiteral IL = null;

        if (currentToken.kind == Token.INTLITERAL) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            IL = new IntegerLiteral(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            IL = null;
            syntacticError("integer literal expected here", "");
        }
        return IL;
    }

// parseCharacterLiteral parses a character-literal, and constructs a leaf
// AST to represent it.
    CharacterLiteral parseCharacterLiteral() throws SyntaxError {
        CharacterLiteral CL = null;

        if (currentToken.kind == Token.CHARLITERAL) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            CL = new CharacterLiteral(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            CL = null;
            syntacticError("character literal expected here", "");
        }
        return CL;
    }

// parseIdentifier parses an identifier, and constructs a leaf AST to
// represent it.
    Identifier parseIdentifier() throws SyntaxError {
        Identifier I = null;

        if (currentToken.kind == Token.IDENTIFIER) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            I = new Identifier(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            I = null;
            syntacticError("identifier expected here", "");
        }
        return I;
    }

// parseOperator parses an operator, and constructs a leaf AST to
// represent it.
    Operator parseOperator() throws SyntaxError {
        Operator O = null;

        if (currentToken.kind == Token.OPERATOR) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            O = new Operator(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            O = null;
            syntacticError("operator expected here", "");
        }
        return O;
    }

///////////////////////////////////////////////////////////////////////////////
//
// COMMANDS
//
///////////////////////////////////////////////////////////////////////////////
// parseCommand parses the command, and constructs an AST
// to represent its phrase structure.
    Command parseCommand() throws SyntaxError {
        Command commandAST = null; // in case there's a syntactic error

        SourcePosition commandPos = new SourcePosition();

        start(commandPos);
        commandAST = parseSingleCommand();
        while (currentToken.kind == Token.SEMICOLON) {
            acceptIt();
            Command c2AST = parseSingleCommand();
            finish(commandPos);
            commandAST = new SequentialCommand(commandAST, c2AST, commandPos);
        }
        return commandAST;
    }

    Command parseSingleCommand() throws SyntaxError {
        Command commandAST = null; // in case there's a syntactic error

        SourcePosition commandPos = new SourcePosition();
        start(commandPos);

        switch (currentToken.kind) {
            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                if (currentToken.kind == Token.LPAREN) {
                    acceptIt();
                    ActualParameterSequence apsAST = parseActualParameterSequence();
                    accept(Token.RPAREN);
                    finish(commandPos);
                    commandAST = new CallCommand(iAST, apsAST, commandPos);

                } else {

                    Vname vAST = parseRestOfVname(iAST);
                    accept(Token.BECOMES);
                    Expression eAST = parseExpression();
                    finish(commandPos);
                    commandAST = new AssignCommand(vAST, eAST, commandPos);
                }
            }
            break;
            /*
      CAMBIOS NUEVOS:
      -Eliminaci??n de single command
      -Eliminaci??n de comando vac??o
       Autor: Joshua
             */
            /*   case Token.BEGIN:
      acceptIt();
      commandAST = parseCommand();
      accept(Token.END);
      break;
    //Cambios Nuevos
    /*
      -If sin | y con RestOfIf
      -If con |
      -ParseBarCommand
     */
            case Token.IF: {
                acceptIt();
                Expression eAST = parseExpression();
                accept(Token.THEN);
                Command c1AST = parseCommand(); //Cambio de singlecommand a Command
                RestOfIf roi1AST = parseRestOfIf();
                finish(commandPos);
                commandAST = new IfCommand(eAST, c1AST, roi1AST, commandPos);
            }
            break;

            // Autores: Max Lee y Paula Mariana Bustos
            // Crear el caso del "let decl in comm end"
            case Token.LET: {
                acceptIt();
                Declaration dAST = parseDeclaration();
                accept(Token.IN);
                Command cAST = parseCommand();
                accept(Token.END);
                finish(commandPos);
                commandAST = new LetInCommand(dAST, cAST, commandPos);
            }
            break;
            // Autores: Max Lee y Paula Mariana Bustos 
            // Crear el caso "loop"

            case Token.LOOP:
                acceptIt();
                switch (currentToken.kind) {
                    // Crear el caso del "loop while exp do comm end"
                    case Token.WHILE: {
                        acceptIt();
                        Expression eAST = parseExpression();
                        accept(Token.DO);
                        Command cAST = parseCommand();
                        accept(Token.END);
                        finish(commandPos);
                        commandAST = new LoopWhileDoCommand(eAST, cAST, commandPos);
                    }
                    break;

                    // Crear el caso del "loop do comm"
                    case Token.DO: {
                        acceptIt();
                        Command cAST = parseCommand();
                        switch (currentToken.kind) {
                            // Crear el caso del "loop do comm while exp end"
                            case Token.WHILE: {
                                acceptIt();
                                Expression eAST = parseExpression();
                                accept(Token.END);
                                finish(commandPos);
                                commandAST = new LoopDoWhileCommand(cAST, eAST, commandPos);
                            }
                            break;
                            // Crear el caso del "loop do comm until exp end"
                            case Token.UNTIL: {
                                acceptIt();
                                Expression eAST = parseExpression();
                                accept(Token.END);
                                finish(commandPos);
                                commandAST = new LoopDoUntilCommand(cAST, eAST, commandPos);
                            }
                            break;
                            // Caso predetermidado por si no coincide con algun caso anterior del "loop do"
                            default:
                                syntacticError("\"%\" cannot start a loop do command",
                                        currentToken.spelling);
                                break;
                        }
                    }
                    break;

                    // Crear el caso del "loop until exp do comm end"
                    case Token.UNTIL: {
                        acceptIt();
                        Expression eAST = parseExpression();
                        accept(Token.DO);
                        Command cAST = parseCommand();
                        accept(Token.END);
                        finish(commandPos);
                        commandAST = new LoopUntilDoCommand(eAST, cAST, commandPos);
                    }
                    break;

                    // Crear el caso del "loop for ident from exp1 to exp2"
                    case Token.FOR: {
                        acceptIt();
                        Identifier iAST = parseIdentifier();
                        accept(Token.FROM);
                        Expression eAST1 = parseExpression();
                        accept(Token.TO);
                        Expression eAST2 = parseExpression();
                        switch (currentToken.kind) {
                            // Crear el caso del "loop for ident from exp1 to exp2 do comm end"
                            case Token.DO: {
                                acceptIt();
                                Command cAST = parseCommand();
                                accept(Token.END);
                                finish(commandPos);
                                commandAST = new LoopForFromToDoCommand(iAST, eAST1, eAST2, cAST, commandPos);
                            }
                            break;
                            // Crear el caso del "loop for ident from exp1 to exp2 while exp3 do comm end"
                            case Token.WHILE: {
                                acceptIt();
                                Expression eAST3 = parseExpression();
                                accept(Token.DO);
                                Command cAST = parseCommand();
                                accept(Token.END);
                                finish(commandPos);
                                commandAST = new LoopForFromToWhileDoCommand(iAST, eAST1, eAST2, eAST3, cAST, commandPos);
                            }
                            break;
                            // Crear el caso del "loop for ident from exp1 to exp2 until exp3 do comm end"
                            case Token.UNTIL: {
                                acceptIt();
                                Expression eAST3 = parseExpression();
                                accept(Token.DO);
                                Command cAST = parseCommand();
                                accept(Token.END);
                                finish(commandPos);
                                commandAST = new LoopForFromToUntilDoCommand(iAST, eAST1, eAST2, eAST3, cAST, commandPos);
                            }
                            break;
                            default:
                                // Caso predetermidado por si no coincide con algun caso anterior del "loop for ident from exp1 to exp2"
                                syntacticError("\"%\" cannot start a loop for command",
                                        currentToken.spelling);
                                break;
                        }
                    }
                    break;

                    // Caso predetermidado por si no coincide con algun caso anterior del "loop"
                    default:
                        syntacticError("\"%\" cannot start a loop command",
                                currentToken.spelling);
                        break;
                }
                break;
            //case Token.SEMICOLON:
            //case Token.END:
            //case Token.ELSE:
            //case Token.IN:
                //case Token.EOT: Comando vac??o eliminado
            //    finish(commandPos);
            //    commandAST = new EmptyCommand(commandPos);
            //    break;


            // Autores: Max Lee, Paula Mariana Bustos y Joshua Arcia
            // Crear el caso "nil"
            case Token.NIL: {
                acceptIt();
                finish(commandPos);
                commandAST = new EmptyCommand(commandPos);
            }
            break;
            // Caso predetermidado por si no coincide con algun caso anterior
            default:
                syntacticError("\"%\" cannot start a command",
                        currentToken.spelling);
                break;

        }
        return commandAST;
    }




  private RestOfIf parseRestOfIf() throws SyntaxError {
    RestOfIf restOfIfAST = null; // in case there's a syntactic error
    BarCommand barCommandAST = null;

    SourcePosition commandPos = new SourcePosition();
    start(commandPos);
    if (currentToken.kind == Token.BAR){
      acceptIt();
      barCommandAST = parseBarCommand();
    }
    accept(Token.ELSE);
    Command c1 = parseCommand();
    accept(Token.END);

    finish(commandPos);
    if (barCommandAST == null){
      restOfIfAST = new RestOfIf(c1,commandPos);
    }
    else{
      restOfIfAST = new RestOfIf(c1,barCommandAST,commandPos);
    }

    return restOfIfAST;
  }

  private BarCommand parseBarCommand() throws SyntaxError {
    BarCommand barCommandAST = null; // in case there's a syntactic error
    BarCommand nestedBarCommandAST = null;

    SourcePosition commandPos = new SourcePosition();
    start(commandPos);

    Expression e1AST = parseExpression();
    accept(Token.THEN);
    Command c1AST = parseCommand();
    if (currentToken.kind == Token.BAR){
      acceptIt();
      nestedBarCommandAST = parseBarCommand();
    }

    finish(commandPos);
    if (nestedBarCommandAST == null)
    {
      barCommandAST = new BarCommand(e1AST,c1AST,commandPos);
    }
    else{
      barCommandAST = new BarCommand(e1AST,c1AST,nestedBarCommandAST,commandPos);
    }

    return barCommandAST;
  }

///////////////////////////////////////////////////////////////////////////////
//
// EXPRESSIONS
//
///////////////////////////////////////////////////////////////////////////////
    Expression parseExpression() throws SyntaxError {
        Expression expressionAST = null; // in case there's a syntactic error

        SourcePosition expressionPos = new SourcePosition();

        start(expressionPos);

        switch (currentToken.kind) {

            case Token.LET: {
                acceptIt();
                Declaration dAST = parseDeclaration();
                accept(Token.IN);
                Expression eAST = parseExpression();
                finish(expressionPos);
                expressionAST = new LetExpression(dAST, eAST, expressionPos);
            }
            break;

            case Token.IF: {
                acceptIt();
                Expression e1AST = parseExpression();
                accept(Token.THEN);
                Expression e2AST = parseExpression();
                accept(Token.ELSE);
                Expression e3AST = parseExpression();
                finish(expressionPos);
                expressionAST = new IfExpression(e1AST, e2AST, e3AST, expressionPos);
            }
            break;

            default:
                expressionAST = parseSecondaryExpression();
                break;
        }
        return expressionAST;
    }

    Expression parseSecondaryExpression() throws SyntaxError {
        Expression expressionAST = null; // in case there's a syntactic error

        SourcePosition expressionPos = new SourcePosition();
        start(expressionPos);

        expressionAST = parsePrimaryExpression();
        while (currentToken.kind == Token.OPERATOR) {
            Operator opAST = parseOperator();
            Expression e2AST = parsePrimaryExpression();
            expressionAST = new BinaryExpression(expressionAST, opAST, e2AST,
                    expressionPos);
        }
        return expressionAST;
    }

    Expression parsePrimaryExpression() throws SyntaxError {
        Expression expressionAST = null; // in case there's a syntactic error

        SourcePosition expressionPos = new SourcePosition();
        start(expressionPos);

        switch (currentToken.kind) {

            case Token.INTLITERAL: {
                IntegerLiteral ilAST = parseIntegerLiteral();
                finish(expressionPos);
                expressionAST = new IntegerExpression(ilAST, expressionPos);
            }
            break;

            case Token.CHARLITERAL: {
                CharacterLiteral clAST = parseCharacterLiteral();
                finish(expressionPos);
                expressionAST = new CharacterExpression(clAST, expressionPos);
            }
            break;

            case Token.LBRACKET: {
                acceptIt();
                ArrayAggregate aaAST = parseArrayAggregate();
                accept(Token.RBRACKET);
                finish(expressionPos);
                expressionAST = new ArrayExpression(aaAST, expressionPos);
            }
            break;

            case Token.LCURLY: {
                acceptIt();
                RecordAggregate raAST = parseRecordAggregate();
                accept(Token.RCURLY);
                finish(expressionPos);
                expressionAST = new RecordExpression(raAST, expressionPos);
            }
            break;

            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                if (currentToken.kind == Token.LPAREN) {
                    acceptIt();
                    ActualParameterSequence apsAST = parseActualParameterSequence();
                    accept(Token.RPAREN);
                    finish(expressionPos);
                    expressionAST = new CallExpression(iAST, apsAST, expressionPos);

                } else {
                    Vname vAST = parseRestOfVname(iAST);
                    finish(expressionPos);
                    expressionAST = new VnameExpression(vAST, expressionPos);
                }
            }
            break;

            case Token.OPERATOR: {
                Operator opAST = parseOperator();
                Expression eAST = parsePrimaryExpression();
                finish(expressionPos);
                expressionAST = new UnaryExpression(opAST, eAST, expressionPos);
            }
            break;

            case Token.LPAREN:
                acceptIt();
                expressionAST = parseExpression();
                accept(Token.RPAREN);
                break;

            default:
                syntacticError("\"%\" cannot start an expression",
                        currentToken.spelling);
                break;

        }
        return expressionAST;
    }

    RecordAggregate parseRecordAggregate() throws SyntaxError {
        RecordAggregate aggregateAST = null; // in case there's a syntactic error

        SourcePosition aggregatePos = new SourcePosition();
        start(aggregatePos);

        Identifier iAST = parseIdentifier();
        accept(Token.IS);
        Expression eAST = parseExpression();

        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            RecordAggregate aAST = parseRecordAggregate();
            finish(aggregatePos);
            aggregateAST = new MultipleRecordAggregate(iAST, eAST, aAST, aggregatePos);
        } else {
            finish(aggregatePos);
            aggregateAST = new SingleRecordAggregate(iAST, eAST, aggregatePos);
        }
        return aggregateAST;
    }

    ArrayAggregate parseArrayAggregate() throws SyntaxError {
        ArrayAggregate aggregateAST = null; // in case there's a syntactic error

        SourcePosition aggregatePos = new SourcePosition();
        start(aggregatePos);

        Expression eAST = parseExpression();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            ArrayAggregate aAST = parseArrayAggregate();
            finish(aggregatePos);
            aggregateAST = new MultipleArrayAggregate(eAST, aAST, aggregatePos);
        } else {
            finish(aggregatePos);
            aggregateAST = new SingleArrayAggregate(eAST, aggregatePos);
        }
        return aggregateAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// VALUE-OR-VARIABLE NAMES
//
///////////////////////////////////////////////////////////////////////////////
    Vname parseVname() throws SyntaxError {
        Vname vnameAST = null; // in case there's a syntactic error
        Identifier iAST = parseIdentifier();
        vnameAST = parseRestOfVname(iAST);
        return vnameAST;
    }

    Vname parseRestOfVname(Identifier identifierAST) throws SyntaxError {
        SourcePosition vnamePos = new SourcePosition();
        vnamePos = identifierAST.position;
        Vname vAST = new SimpleVname(identifierAST, vnamePos);

        while (currentToken.kind == Token.DOT
                || currentToken.kind == Token.LBRACKET) {

            if (currentToken.kind == Token.DOT) {
                acceptIt();
                Identifier iAST = parseIdentifier();
                vAST = new DotVname(vAST, iAST, vnamePos);
            } else {
                acceptIt();
                Expression eAST = parseExpression();
                accept(Token.RBRACKET);
                finish(vnamePos);
                vAST = new SubscriptVname(vAST, eAST, vnamePos);
            }
        }
        return vAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// DECLARATIONS
//
///////////////////////////////////////////////////////////////////////////////

    /*
    Declaration parseDeclaration() throws SyntaxError {
        Declaration declarationAST = null; // in case there's a syntactic error

        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);
        declarationAST = parseSingleDeclaration();
        while (currentToken.kind == Token.SEMICOLON) {
            acceptIt();
            Declaration d2AST = parseSingleDeclaration();
            finish(declarationPos);
            declarationAST = new SequentialDeclaration(declarationAST, d2AST,
                    declarationPos);
        }
        return declarationAST;
    }
    */

    /* CAMBIOS NUEVOS Parte 1
     * Autores: Joshua Arcia
     * -Parse compound declaration
     * -Local Declaration
     * -Parse Declaration
     * -Parse Single declaration
     * -ParseProcFuncs
    */
    Declaration parseDeclaration() throws SyntaxError {
        Declaration compoundDeclarationAST = null; // in case there's a syntactic error

        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);

        compoundDeclarationAST = parseCompoundDeclaration();

        while (currentToken.kind == Token.SEMICOLON) {
            acceptIt();
            Declaration d2AST = parseDeclaration();
            finish(declarationPos);
            compoundDeclarationAST = new SequentialDeclaration(compoundDeclarationAST, d2AST,
                    declarationPos);
        }

        return compoundDeclarationAST;
    }

    Declaration parseSingleDeclaration() throws SyntaxError {
        Declaration declarationAST = null; // in case there's a syntactic error

        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);

        switch (currentToken.kind) {

            case Token.CONST: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.IS);
                Expression eAST = parseExpression();
                finish(declarationPos);
                declarationAST = new ConstDeclaration(iAST, eAST, declarationPos);
            }
            break;

            case Token.VAR: {
                acceptIt();
                Identifier iAST = parseIdentifier();

                switch (currentToken.kind){

                    case Token.COLON:{
                        acceptIt();
                        TypeDenoter tAST;
                        tAST = parseTypeDenoter();
                        finish(declarationPos);
                        declarationAST = new VarDeclaration(iAST,tAST,declarationPos);
                    }
                    break;

                    case Token.INIT:{
                        acceptIt();
                        Expression eAST;
                        eAST = parseExpression();
                        finish(declarationPos);
                        declarationAST = new VarInitDeclaration(iAST,eAST,declarationPos);
                    }
                    break;

                    default:
                        syntacticError("\"%\" cannot start a declaration",
                                currentToken.spelling);
                        break;
                }


            }
            break;

            case Token.PROC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.IS);
                //Command cAST = parseSingleCommand(); Eliminaci??n de ParseSinglecommand
                //Cambios nuevos
                Command cAST = parseCommand(); //Se a??ade command
                accept(Token.END); //Se a??ade END
                finish(declarationPos);
                declarationAST = new ProcDeclaration(iAST, fpsAST, cAST, declarationPos);
            }
            break;

            case Token.FUNC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                accept(Token.IS);
                Expression eAST = parseExpression();
                finish(declarationPos);
                declarationAST = new FuncDeclaration(iAST, fpsAST, tAST, eAST,
                        declarationPos);
            }
            break;

            case Token.TYPE: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.IS);
                TypeDenoter tAST = parseTypeDenoter();
                finish(declarationPos);
                declarationAST = new TypeDeclaration(iAST, tAST, declarationPos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start a declaration",
                        currentToken.spelling);
                break;

        }
        return declarationAST;
    }

    Declaration parseCompoundDeclaration() throws SyntaxError{
        Declaration compoundDeclarationAST = null; // in case there's a syntactic error

        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);

        switch(currentToken.kind){

            case Token.REC:
            {
                acceptIt();
                ProcFuncDeclaration pfAST;
                pfAST = parseProcfuncsAUX();
                accept(Token.END);
                finish(declarationPos);
                compoundDeclarationAST = pfAST;
            }
            break;


            case Token.LOCAL:
            {
                acceptIt();
                Declaration dAST;
                dAST = parseDeclaration();
                accept(Token.IN);
                Declaration d2AST;
                d2AST = parseDeclaration();
                accept(Token.END);
                finish(declarationPos);
                compoundDeclarationAST = new LocalDeclaration(dAST,d2AST,declarationPos);
            }
            break;

            default:
                compoundDeclarationAST = parseSingleDeclaration();
                break;
        }

        return compoundDeclarationAST;
    }

    ProcFuncDeclaration parseProcfuncsAUX() throws SyntaxError {
        ProcFuncDeclaration procFuncsDeclarationAST = null; // in case there's a syntactic error
        FuncDeclaration fdAST = null;
        ProcDeclaration pdAST = null;
        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);

        switch (currentToken.kind){
            case Token.PROC:
            {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.IS);
                Command cAST = parseCommand();
                accept(Token.END);
                pdAST = new ProcDeclaration(iAST,fpsAST,cAST,declarationPos);
               // finish(declarationPos);
               // procFuncsDeclarationAST = new ProcFuncDeclaration(pdAST, declarationPos);
                /*if (currentToken.kind == Token.BAR){
                    acceptIt();
                    ProcFuncDeclaration pfdAST = parseProcfuncs();
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(pdAST,pfdAST, declarationPos);
                }
                else
                {
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(pdAST, declarationPos);
                }*/
            }
            break;

            case Token.FUNC:
            {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.COLON);
                TypeDenoter tdAST = parseTypeDenoter();
                accept(Token.IS);
                Expression eAST = parseExpression();

                fdAST = new FuncDeclaration(iAST,fpsAST,tdAST,eAST,declarationPos);
               // finish(declarationPos);
                //procFuncsDeclarationAST = new ProcFuncDeclaration(fdAST, declarationPos);

                /*if (currentToken.kind == Token.BAR){
                    acceptIt();
                    ProcFuncDeclaration pfdAST = parseProcfuncs();
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(fdAST,pfdAST, declarationPos);
                }
                else
                {
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(fdAST, declarationPos);
                }*/

            }
            break;

            default:
                syntacticError("\"%\" cannot start a declaration",
                        currentToken.spelling);
                break;
        }

        accept(Token.BAR);
        ProcFuncDeclaration pfdAST2 =  parseProcfuncs();
        finish(declarationPos);
        if (fdAST != null){
            procFuncsDeclarationAST = new ProcFuncDeclaration(fdAST,pfdAST2,declarationPos);
        }
        else{
            procFuncsDeclarationAST = new ProcFuncDeclaration(pdAST,pfdAST2,declarationPos);
        }

        return  procFuncsDeclarationAST;
    }

    ProcFuncDeclaration parseProcfuncs() throws SyntaxError{
        ProcFuncDeclaration procFuncsDeclarationAST = null; // in case there's a syntactic error

        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);

        switch (currentToken.kind){
            case Token.PROC:
            {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.IS);
                Command cAST = parseCommand();
                accept(Token.END);
                ProcDeclaration pdAST = new ProcDeclaration(iAST,fpsAST,cAST,declarationPos);

                if (currentToken.kind == Token.BAR){
                    acceptIt();
                    ProcFuncDeclaration pfdAST = parseProcfuncs();
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(pdAST,pfdAST, declarationPos);
                }
                else
                {
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(pdAST, declarationPos);
                }
            }
            break;

            case Token.FUNC:
            {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.COLON);
                TypeDenoter tdAST = parseTypeDenoter();
                accept(Token.IS);
                Expression eAST = parseExpression();

                FuncDeclaration fdAST = new FuncDeclaration(iAST,fpsAST,tdAST,eAST,declarationPos);

                if (currentToken.kind == Token.BAR){
                    acceptIt();
                    ProcFuncDeclaration pfdAST = parseProcfuncs();
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(fdAST,pfdAST, declarationPos);
                }
                else
                {
                    finish(declarationPos);
                    procFuncsDeclarationAST = new ProcFuncDeclaration(fdAST, declarationPos);
                }

            }
            break;

            default:
                syntacticError("\"%\" cannot start a declaration",
                        currentToken.spelling);
                break;
        }

        return  procFuncsDeclarationAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// PARAMETERS
//
///////////////////////////////////////////////////////////////////////////////
    FormalParameterSequence parseFormalParameterSequence() throws SyntaxError {
        FormalParameterSequence formalsAST;

        SourcePosition formalsPos = new SourcePosition();

        start(formalsPos);
        if (currentToken.kind == Token.RPAREN) {
            finish(formalsPos);
            formalsAST = new EmptyFormalParameterSequence(formalsPos);

        } else {
            formalsAST = parseProperFormalParameterSequence();
        }
        return formalsAST;
    }

    FormalParameterSequence parseProperFormalParameterSequence() throws SyntaxError {
        FormalParameterSequence formalsAST = null; // in case there's a syntactic error;

        SourcePosition formalsPos = new SourcePosition();
        start(formalsPos);
        FormalParameter fpAST = parseFormalParameter();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            FormalParameterSequence fpsAST = parseProperFormalParameterSequence();
            finish(formalsPos);
            formalsAST = new MultipleFormalParameterSequence(fpAST, fpsAST,
                    formalsPos);

        } else {
            finish(formalsPos);
            formalsAST = new SingleFormalParameterSequence(fpAST, formalsPos);
        }
        return formalsAST;
    }

    FormalParameter parseFormalParameter() throws SyntaxError {
        FormalParameter formalAST = null; // in case there's a syntactic error;

        SourcePosition formalPos = new SourcePosition();
        start(formalPos);

        switch (currentToken.kind) {

            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                finish(formalPos);
                formalAST = new ConstFormalParameter(iAST, tAST, formalPos);
            }
            break;

            case Token.VAR: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                finish(formalPos);
                formalAST = new VarFormalParameter(iAST, tAST, formalPos);
            }
            break;

            case Token.PROC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                finish(formalPos);
                formalAST = new ProcFormalParameter(iAST, fpsAST, formalPos);
            }
            break;

            case Token.FUNC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                finish(formalPos);
                formalAST = new FuncFormalParameter(iAST, fpsAST, tAST, formalPos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start a formal parameter",
                        currentToken.spelling);
                break;

        }
        return formalAST;
    }

    ActualParameterSequence parseActualParameterSequence() throws SyntaxError {
        ActualParameterSequence actualsAST;

        SourcePosition actualsPos = new SourcePosition();

        start(actualsPos);
        if (currentToken.kind == Token.RPAREN) {
            finish(actualsPos);
            actualsAST = new EmptyActualParameterSequence(actualsPos);

        } else {
            actualsAST = parseProperActualParameterSequence();
        }
        return actualsAST;
    }

    ActualParameterSequence parseProperActualParameterSequence() throws SyntaxError {
        ActualParameterSequence actualsAST = null; // in case there's a syntactic error

        SourcePosition actualsPos = new SourcePosition();

        start(actualsPos);
        ActualParameter apAST = parseActualParameter();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            ActualParameterSequence apsAST = parseProperActualParameterSequence();
            finish(actualsPos);
            actualsAST = new MultipleActualParameterSequence(apAST, apsAST,
                    actualsPos);
        } else {
            finish(actualsPos);
            actualsAST = new SingleActualParameterSequence(apAST, actualsPos);
        }
        return actualsAST;
    }

    ActualParameter parseActualParameter() throws SyntaxError {
        ActualParameter actualAST = null; // in case there's a syntactic error

        SourcePosition actualPos = new SourcePosition();

        start(actualPos);

        switch (currentToken.kind) {

            case Token.IDENTIFIER:
            case Token.INTLITERAL:
            case Token.CHARLITERAL:
            case Token.OPERATOR:
            case Token.LET:
            case Token.IF:
            case Token.LPAREN:
            case Token.LBRACKET:
            case Token.LCURLY: {
                Expression eAST = parseExpression();
                finish(actualPos);
                actualAST = new ConstActualParameter(eAST, actualPos);
            }
            break;

            case Token.VAR: {
                acceptIt();
                Vname vAST = parseVname();
                finish(actualPos);
                actualAST = new VarActualParameter(vAST, actualPos);
            }
            break;

            case Token.PROC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                finish(actualPos);
                actualAST = new ProcActualParameter(iAST, actualPos);
            }
            break;

            case Token.FUNC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                finish(actualPos);
                actualAST = new FuncActualParameter(iAST, actualPos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start an actual parameter",
                        currentToken.spelling);
                break;

        }
        return actualAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// TYPE-DENOTERS
//
///////////////////////////////////////////////////////////////////////////////
    TypeDenoter parseTypeDenoter() throws SyntaxError {
        TypeDenoter typeAST = null; // in case there's a syntactic error
        SourcePosition typePos = new SourcePosition();

        start(typePos);

        switch (currentToken.kind) {

            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                finish(typePos);
                typeAST = new SimpleTypeDenoter(iAST, typePos);
            }
            break;

            case Token.ARRAY: {
                acceptIt();
                IntegerLiteral ilAST = parseIntegerLiteral();
                accept(Token.OF);
                TypeDenoter tAST = parseTypeDenoter();
                finish(typePos);
                typeAST = new ArrayTypeDenoter(ilAST, tAST, typePos);
            }
            break;

            case Token.RECORD: {
                acceptIt();
                FieldTypeDenoter fAST = parseFieldTypeDenoter();
                accept(Token.END);
                finish(typePos);
                typeAST = new RecordTypeDenoter(fAST, typePos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start a type denoter",
                        currentToken.spelling);
                break;

        }
        return typeAST;
    }

    FieldTypeDenoter parseFieldTypeDenoter() throws SyntaxError {
        FieldTypeDenoter fieldAST = null; // in case there's a syntactic error

        SourcePosition fieldPos = new SourcePosition();

        start(fieldPos);
        Identifier iAST = parseIdentifier();
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            FieldTypeDenoter fAST = parseFieldTypeDenoter();
            finish(fieldPos);
            fieldAST = new MultipleFieldTypeDenoter(iAST, tAST, fAST, fieldPos);
        } else {
            finish(fieldPos);
            fieldAST = new SingleFieldTypeDenoter(iAST, tAST, fieldPos);
        }
        return fieldAST;
    }
}
