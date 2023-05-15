package com.code.Asgmnt1;

import com.code.Asgmnt1.Grammar.TokenType;

public class Token {
	private TokenType tokType;
	private String lexeme;
	private int lineNum;
	private int colNum;

	public Token(TokenType tokType, String lexeme, int lineNum, int colNum) throws LexicalError  {
		this.tokType = tokType;
		this.lexeme = lexeme;
		if (lineNum < 0)
			throw new LexicalError("invalid line number");
		this.lineNum = lineNum;
		if (colNum < 0)
			throw new LexicalError("invalid column number");
		this.colNum = colNum;
	}

	public Token(TokenType type, String lexeme) throws Exception {
		this(type,lexeme,0,0);
	}

	/**
	 * @return the lineNum
	 */
	public int getLineNum() {
		return lineNum;
	}

	/**
	 * @return the colNum
	 */
	public int getColNum() {
		return colNum;
	}

	public TokenType getTokType() {
		return tokType;
	}

	public String getLexeme() {
		return lexeme;
	}

	@Override
	public String toString() {
		return "Token [tokType=" + tokType + ", lexeme=" + lexeme + ", lineNum=" + lineNum + ", colNum=" + colNum + "]";
	}

	
}
