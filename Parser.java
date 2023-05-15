package com.code.Asgmnt1;

import java.util.List;

import com.code.Asgmnt1.Grammar.NonTerminal;
import com.code.Asgmnt1.Grammar.TokenType;

public class Parser
{

	private LexicalAnalyze lex;
	
	private Token tok;
	
	/**
	 *
	 * @throws LexicalError 
	 */
	public Parser(List<String> code) throws LexicalError
	{
			lex = new LexicalAnalyze (code);
			tok = lex.getToken();

	}
		
	/**
	 * @return ParseTree representing the program
	 * @throws ParseError
	 * @throws LexicalError
	 */
	public ParseTree parse() throws ParseError, LexicalError
	{
		Node progNode = new Node(NonTerminal.PROGRAM);
		do
		{
			Node s = getStatement();
			progNode.addChild(s);
		}
		while (tok.getTokType() != TokenType.EOS);
		return new ParseTree(progNode);
	}

	/**
	 * @return Node representing a statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
    private Node getStatement() throws ParseError, LexicalError {
        Node stmtNode = new Node(NonTerminal.STATEMENT);
        if (tok.getTokType() == TokenType.IDENTIFIER) {
            Node assignmentNode = getAssignment();
            stmtNode.addChild(assignmentNode);
        } else if (tok.getTokType() == TokenType.DISPLAY) {
            Node displayNode = getDisplay();
            stmtNode.addChild(displayNode);
		} else if (tok.getTokType() == TokenType.INPUT) {
			Node inputNode = getInput();
			stmtNode.addChild(inputNode);
		} else if (tok.getTokType() == TokenType.BLOCK) {
			Node BlockNode = getBlock();
			stmtNode.addChild(BlockNode);
		} else if (tok.getTokType() == TokenType.IF) {
			Node ifNode = getIf();
			stmtNode.addChild(ifNode);
		} else if (tok.getTokType() == TokenType.WHILE) {
			Node whileNode = getWhile();
			stmtNode.addChild(whileNode);
		} else if (tok.getTokType() == TokenType.REPEAT) {
			Node repeatNode = getRepeat();
			stmtNode.addChild(repeatNode);
		}
        else {
            throw new ParseError("Invalid statement: Expected Identifier or display statements");
        }
        
        return stmtNode;
    }
    /**
	 * @return Node representing an Assignment statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
    private Node getAssignment() throws ParseError, LexicalError {
        Node assnNode = new Node(NonTerminal.ASSIGNMENT);
        Node idNode = new Node(tok);
        match(TokenType.IDENTIFIER);
        Node arrowNode = new Node(tok);
        match(TokenType.ASSIGN);
        Node exprNode = getExpression();
        assnNode.addChild(idNode);
        assnNode.addChild(arrowNode);
        assnNode.addChild(exprNode);
        return assnNode;
    }
    
    /**
	 * @return Node representing an display statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
    private Node getDisplay() throws ParseError, LexicalError {
        Node displayNode = new Node(NonTerminal.DISPLAY);
        match(TokenType.DISPLAY);
        Node idNode = new Node(tok);
        match(TokenType.IDENTIFIER);
        displayNode.addChild(idNode);
        return displayNode;
    }

	/**
	 * @return Node representing an input statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getInput() throws ParseError, LexicalError {
		Node inputNode = new Node(NonTerminal.INPUT);
		match(TokenType.INPUT);
		Node idNode = new Node(tok);
		match(TokenType.IDENTIFIER);
		inputNode.addChild(idNode);
		return inputNode;
	}

	/**
	 * @return Node representing an Block statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getBlock() throws ParseError, LexicalError {
		Node blockNode = new Node(NonTerminal.BLOCK);
		match(TokenType.BLOCK);
		do {
			Node s = getStatement();
			blockNode.addChild(s);
		} while (tok.getTokType() != TokenType.END);
		match(TokenType.END);
		return blockNode;

	}

	/**
	 * @return Node representing an if statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getIf() throws ParseError, LexicalError {
		Node ifNode = new Node(NonTerminal.IF);
		match(TokenType.IF);
		Node booleanExprNode = getExpressionBoolean();
		match(TokenType.THEN);
		Node thenStmtNode = getStatement();
		match(TokenType.ELSE);
		Node elseStmtNode = getStatement();
		match(TokenType.END);
		ifNode.addChild(booleanExprNode);
		ifNode.addChild(thenStmtNode);
		ifNode.addChild(elseStmtNode);
		return ifNode;
	}
	/**
	 * @return Node representing a while statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getWhile() throws ParseError, LexicalError {
	    Node whileNode = new Node(NonTerminal.WHILE);
	    match(TokenType.WHILE);
	    Node boolExprNode = getExpressionBoolean();
	    match(TokenType.DO);
	    Node stmtNode = getStatement();
	    match(TokenType.END);
	    whileNode.addChild(boolExprNode);
	    whileNode.addChild(stmtNode);
	    return whileNode;
	}
	/**
	 * @return Node representing a while statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getRepeat() throws ParseError, LexicalError {
		Node repeatNode = new Node(NonTerminal.REPEAT);
		match(TokenType.REPEAT);
		Node stmtNode = getStatement();
		repeatNode.addChild(stmtNode);
		match(TokenType.UNTIL);
		Node boolExprNode = getExpressionBoolean();
		repeatNode.addChild(boolExprNode);
		return repeatNode;
	}
	/**
	 * @return Node representing an expression
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getExpression() throws ParseError, LexicalError
	{
		Node termNode = getTerm();
		return getExpressionPrime(termNode);
	}

	/**
	 * @param e Node representing the first expression in production
	 * @return Node representing the entire expression
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getExpressionPrime(Node e) throws ParseError, LexicalError
	{
		Node exprNode = null;
		if (tok.getTokType() == TokenType.PLUS)
		{
			Node tokNode = new Node(tok);			
			match (TokenType.PLUS);
			Node termNode = getTerm();
			Node addNode = createBinaryExpression (e, tokNode, termNode);
			exprNode = getExpressionPrime(addNode);
		}
		else if (tok.getTokType() == TokenType.MINUS)
		{
			Node tokNode = new Node(tok);
			match (TokenType.MINUS);
			Node termNode = getTerm();
			Node subNode = createBinaryExpression (e, tokNode, termNode);
			exprNode = getExpressionPrime(subNode);
		}
		else
			exprNode = e;
		return exprNode;
	}
	/**
	 * @return Node representing the boolean expression
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getExpressionBoolean() throws ParseError, LexicalError {
	    Node exprNode = getExpression();
	    Node tokNode = null;
	    switch (tok.getTokType()) {
	        case LT:
	        case LE:
	        case GT:
	        case GE:
	        case EQ:
	        case NTEQ:
	        	tokNode = new Node(tok);
	            match(tok.getTokType());
	            break;
	        default:
	            throw new ParseError("Invalid relational operator");
	    }
	    Node exprNode2 = getExpression();
	    Node boolNode = new Node(NonTerminal.BOOLEAN_EXPR);
	    boolNode.addChild(exprNode);
	    boolNode.addChild(tokNode);
	    boolNode.addChild(exprNode2);
	    return boolNode;
	}

	/**
	 * @param e first expression
	 * @param tokNode node representing the operation
	 * @param termNode second terms
	 * @return Node representing the result of the operation on the 2 operands
	 * @throws ParseError
	 */
	private Node createBinaryExpression(Node e, Node tokNode, Node termNode) throws ParseError
	{
		Node exprNode = new Node(NonTerminal.EXPRESSION);
		exprNode.addChild(e);
		exprNode.addChild(tokNode);
		exprNode.addChild(termNode);
		return exprNode;
	}
    
	/**
	 * @return Node representing a term
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getTerm() throws ParseError, LexicalError
	{
		Node facNode = getFactor();
		return getTermPrime(facNode);
	}

	/**
	 * @param t represents the input term
	 * @return Node representing full term  
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getTermPrime(Node t) throws ParseError, LexicalError
	{
		Node tm = null;
		if (tok.getTokType() == TokenType.TIMES)
		{
			Node tokNode = new Node(tok);
			match (TokenType.TIMES);
			Node fNode = getFactor();
			Node mulTerm = createBinaryTerm(t, tokNode, fNode);
			tm = getTermPrime(mulTerm);
		}
		else if (tok.getTokType() == TokenType.DIVIDE)
		{
			Node tokNode = new Node(tok);
			match (TokenType.DIVIDE);
			Node fNode = getFactor();
			Node divTerm = createBinaryTerm(t, tokNode, fNode);
			tm = getTermPrime(divTerm);
		}
		else
			tm = t;
		return tm;
	}

	/**
	 * @param t node representing first term
	 * @param tokNode node representing operation
	 * @param fNode node representing second operand
	 * @return node representing result of operation
	 * @throws ParseError
	 */
	private Node createBinaryTerm(Node t, Node tokNode, Node fNode) throws ParseError
	{
		Node termNode = new Node (NonTerminal.TERM);
		termNode.addChild(t);
		termNode.addChild(tokNode);
		termNode.addChild(fNode);
		return termNode;
	}

	/**
	 * @return node representing factor
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getFactor() throws ParseError, LexicalError
	{
		Node expNode = getExp();
		return getFactorPrime(expNode);
	}

	/**
	 * @param f node representing first operand
	 * @return node representing entire factor
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getFactorPrime(Node f) throws ParseError, LexicalError
	{
		Node fNode = null;
		if (tok.getTokType() == TokenType.EXPONENT)
		{
			Node tokNode = new Node(tok);
			match(TokenType.EXPONENT);
			Node exp = getExp();
			fNode = createBinaryFactor(f,tokNode, exp);
		}
		else
			fNode = f;
		return fNode;
	}

	/**
	 * @param f node representing first operand
	 * @param tokNode node representing operator
	 * @param exp node representing second operand
	 * @return node representing result of operation
	 * @throws ParseError
	 */
	private Node createBinaryFactor(Node f, Node tokNode, Node exp) throws ParseError
	{
		Node facNode = new Node (NonTerminal.FACTOR);
		facNode.addChild(f);
		facNode.addChild(tokNode);
		facNode.addChild(exp);
		return facNode;
	}

	/**
	 * @return node representing exp 
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getExp() throws ParseError, LexicalError
	{
		Node eNode = null;
		if (tok.getTokType() == TokenType.LPAREN)
		{
			Node tokNode = new Node(tok);
			match (TokenType.LPAREN);
			Node expNode = getExpression();
			Node tokNode1 = new Node(tok);
			match(TokenType.RPAREN);	
			eNode = createExprParen (tokNode, expNode, tokNode1);
		}
		else if (tok.getTokType() == TokenType.MINUS)
		{
			match(TokenType.MINUS);
			Node exp = getExpression();
			Node exp1 = createNegExp(exp);
			Node exp2 = createUnaryExpression(exp1);
			eNode = createExprExponent(exp2);
		}
		else if (tok.getTokType() == TokenType.IDENTIFIER)
		{
			Node idNode = getId();
			eNode = createExprId(idNode);
		}
		else
			eNode = getNum();
		return eNode;
	}
	/**
	 * @return node representing Id 
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getId() throws ParseError, LexicalError
	{
		Node idNode = new Node(tok);
		match(TokenType.IDENTIFIER);
		return idNode;
	}

	/** creates a new node for NonTerminal VARIABLE 
	 * @throws ParseError **/
	private Node createExprId(Node id) throws ParseError {
	    Node n= new Node(NonTerminal.VARIABLE);
	    n.addChild(id);
	    return n;
	}
	/**
	 * @param tokNode node representing left paren
	 * @param expNode node representing expression
	 * @param tokNode1 node representing right parent
	 * @return node representing expression enclosed in parentheses
	 * @throws ParseError
	 */
	private Node createExprParen(Node tokNode, Node expNode, Node tokNode1) throws ParseError
	{
		Node n = new Node (NonTerminal.EXPRESSION);
		n.addChild(tokNode);
		n.addChild(expNode);
		n.addChild(tokNode1);
		return n;
	}

	/**
	 * @param e Node representing expression
	 * @return node representing e as an exponent
	 * @throws ParseError
	 */
	private Node createExprExponent(Node e) throws ParseError
	{
		Node expNode = new Node(NonTerminal.EXPONENT);
		expNode.addChild(e);
		return expNode;
	}

	/**
	 * @param e node representing expression
	 * @return node representing unary expression
	 * @throws ParseError
	 */
	private Node createUnaryExpression(Node e) throws ParseError
	{
		Node expNode = new Node(NonTerminal.EXPRESSION);
		expNode.addChild(e);
		return expNode;
	}

	/**
	 * @param e node representing expression
	 * @return node representing negation of e
	 * @throws ParseError
	 */
	private Node createNegExp(Node e) throws ParseError
	{
		Node n = new Node (NonTerminal.NEG_EXPR);
		n.addChild(e);
		return n;
	}

	/**
	 * @return node representing a number
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getNum() throws ParseError, LexicalError
	{
		Node n = new Node (NonTerminal.NUM);
		Node tokNode = new Node (tok);
		if (tok.getTokType() == TokenType.INTLIT)
			match (TokenType.INTLIT);
		else
			match (TokenType.FLOATLIT);
		n.addChild(tokNode);
		return n;
	}

	/**
	 * @param type expected token type
	 * @throws ParseError if expected token type is different than the current token type
	 * @throws LexicalError
	 */
	private void match(TokenType type) throws ParseError, LexicalError
	{
		if (tok.getTokType() != type)
			throw new ParseError ("expected " + type + " at row " + tok.getLineNum() + " and column " + tok.getColNum());
			tok = lex.getToken();
			if(tok.getTokType() == TokenType.EOLN){ 
	        	match(TokenType.EOLN);
			}
	}
}
