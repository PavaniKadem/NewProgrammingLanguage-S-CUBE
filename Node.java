package com.code.Asgmnt1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.code.Asgmnt1.Grammar.NonTerminal;
import com.code.Asgmnt1.Grammar.TokenType;

public class Node
{
	private List<Node> children;
	
	private NonTerminal nodeType;
	
	private Token tok;
	
	/**
	 * @param nodeType used to create a node representing a non-terminal
	 */
	public Node(NonTerminal nodeType)
	{
		this.nodeType = nodeType;
		this.children = new ArrayList<Node>();
		this.tok = null;
	}

	/**
	 * @param tok used to create a node representing a terminal
	 */
	public Node (Token tok)
	{
		this.tok = tok;
		this.children = null;
		this.nodeType = NonTerminal.NONE;
	}
	
	/**
	 * @param n node to which child will be added
	 * @throws ParseError
	 */
	public void addChild (Node n) throws ParseError
	{
		if (n == null)
			throw new ParseError ("null node argument");
		children.add(n);
	}

	/**
	 * @return value obtained when evaluating the expression
	 */
	public ReturnValue evaluate()
	{
		ReturnValue result = null;
		switch (nodeType)
		{
			case PROGRAM:
				for (int i = 0; i < children.size(); i++)
					children.get(i).evaluate();
				break;
			case STATEMENT:
	            if (!children.isEmpty()) {
	                Node s1 = children.get(0);
	                result = s1.evaluate();
	            }
	            break;
			case ASSIGNMENT:
                Node idNode = children.get(0);
                Node exprNode2 = children.get(2);
                String id = idNode.tok.getLexeme();
                ReturnValue value = exprNode2.evaluate();
                result = value;
                VariableMemory.getInstance().setValue(id, value);
                break;
            case DISPLAY:
                Node idNode2 = children.get(0);
                String id2 = idNode2.tok.getLexeme();
                ReturnValue value2 = VariableMemory.getInstance().getValue(id2);
                System.out.println(value2);
                break;
            case INPUT:
            	Node idNode3 = children.get(0);
            	boolean isInt = true;
            	Number num = null;
            	String id3 = idNode3.tok.getLexeme();
                System.out.println("input "+id3+" value:");
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNextInt()) {
                     num = scanner.nextInt();
                } else if (scanner.hasNextFloat()) {
                     num = scanner.nextFloat();
                    isInt =false;
                } 
                ReturnValue input = new ReturnValue(num, isInt);
                VariableMemory.getInstance().setValue(id3, input);
                break;
            case BLOCK:
            	for (int i = 0; i < children.size(); i++)
					children.get(i).evaluate();
				break;
            case VARIABLE:
            	String id1 = children.get(0).tok.getLexeme();
            	ReturnValue val = VariableMemory.getInstance().getValue(id1);
            	result = val;
            	break;
			case EXPRESSION:
				Node e1 = children.get(0);
				if (e1.nodeType != NonTerminal.NONE)
				{
					result = e1.evaluate();
					if (children.size() > 1)
					{
						Token op = children.get(1).tok;
						Node e2 = children.get(2);
						result = result.performOp(op, e2.evaluate());
					}
				}
				else
				{
					Node n = children.get(1);
					result = n.evaluate();
					if (e1.tok.getTokType() == TokenType.MINUS)
						result = n.evaluate().negate();
				}
				break;
			case TERM:
				Node ef = children.get(0);
				result = ef.evaluate();
				if (children.size() > 1)
				{
					Token op = children.get(1).tok;
					Node e2 = children.get(2);
					result = result.performOp(op, e2.evaluate());
				}
				break;
			case FACTOR:
				Node ee = children.get(0);
				result = ee.evaluate();
				if (children.size() > 1)
				{
					Token op = children.get(1).tok;
					Node e2 = children.get(2);
					result = result.performOp(op, e2.evaluate());
				}
				break;
			case EXPONENT:
				Node n = children.get(0);
				if (n.nodeType == NonTerminal.NONE)
					n = children.get(1);
				result = n.evaluate();
				break;
			case NEG_EXPR:
				Node nn = children.get(0);
				result = nn.evaluate();
				result = result.negate();
				break;
			case NUM:
				result = createReturnNode(children.get(0).tok);
				break;
            case IF:
                Node booleanExpr = children.get(0);
                Node thenStatement = children.get(1);
                Node elseStatement = children.get(2);
                result = booleanExpr.evaluate();
                if (result.isTrue()) {
                    result = thenStatement.evaluate();
                } else {
                    result = elseStatement.evaluate();
                }
                break;
            case WHILE:
                Node boolExpr = children.get(0);
                Node statement = children.get(1);
                while (boolExpr.evaluate().isTrue()) {
                    statement.evaluate();
                }
                break;
            case REPEAT:
            	Node statementNode = children.get(0);
            	Node booleanExprNode = children.get(1);

            	do {
            		statementNode.evaluate();
            		result = booleanExprNode.evaluate();
            	} while (!result.isTrue());

            	break;
            case BOOLEAN_EXPR:
                Node expr1 = children.get(0);
                Token relop = children.get(1).tok;
                Node expr2 = children.get(2);
                
                result = expr1.evaluate();
                result = result.compare(relop, expr2.evaluate());
                break;
			default:
				throw new RuntimeException("Unknown node type: " + nodeType);
		}
		return result;
	}

	/**
	 * @param tok whether token is an integer or a float
	 * @return ReturnValue associated with tok
	 */
	private ReturnValue createReturnNode(Token tok)
	{
		ReturnValue result = null;
		if (tok.getTokType() == TokenType.INTLIT)
		{
			Integer value = Integer.valueOf(tok.getLexeme());
			result = new ReturnValue(value, true);
		}
		else
		{
			Float value = Float.valueOf(tok.getLexeme());
			result = new ReturnValue(value, false);
		}
		return result;
	}

}
