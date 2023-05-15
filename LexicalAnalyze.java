package com.code.Asgmnt1;

import java.util.List;

import com.code.Asgmnt1.Grammar.TokenType;

public class LexicalAnalyze {

	private List<String> code;

	private int rowNum;

	private int index;

	private String current;

	private boolean sentEOF;

	private boolean sentLastEOLN;

	/************************************************
	 * if \n is a character in the string then these boolean flags are not needed.
	 */

	/**
	 * @param code - list of lines of code
	 * @throws LexicalError if there is an error in analyzing the code
	 */
	public LexicalAnalyze(List<String> code) throws LexicalError {
		this.code = code;
		this.rowNum = 1;
		this.current = code.get(0);
		this.index = 0;
		this.sentEOF = false;
		this.sentLastEOLN = false;
	}

	/**
	 * skips white space leaving current character at next non-white space character
	 * 
	 * @throws Exception
	 */
	private void skipWhiteSpace() throws LexicalError {
		while (index < current.length() && Character.isWhitespace(current.charAt(index)))
			index++;
	}

	/**
	 * checks the input variable format allows sequence of characters starting with
	 * a letter and followed by letters, digits, or underscore
	 */
	private String getIdentifier() {
		String result = "";
		while (index < current.length()
				&& (Character.isLetterOrDigit(current.charAt(index)) || current.charAt(index) == '_')) {
			result += current.charAt(index);
			index++;
		}
		return result;
	}

	private String getNum() throws LexicalError {
		int start = index;
		int dotCount = 0;
		while (index < current.length() && (Character.isDigit(current.charAt(index)) || current.charAt(index) == '.')) {
			if (current.charAt(index) == '.') {
				dotCount++;
				if (dotCount > 1) {
					throw new LexicalError("Multiple decimal points found");
				}
			}
			index++;
		}
		return current.substring(start, index);
	}

	/**
	 * @return next token
	 * @throws LexicalError
	 */
	public Token getToken() throws LexicalError {
		Token tok = null;
		if (sentEOF)
			throw new LexicalError("attempt to read past end of program");
		skipWhiteSpace();
		if (index == current.length()) {
			if (sentLastEOLN) {
				tok = new Token(TokenType.EOS, "", rowNum, index + 1);
				sentEOF = true;
			} else {
				tok = new Token(TokenType.EOLN, "", rowNum, index + 1);
				if (rowNum == code.size())
					sentLastEOLN = true;
				else {
					current = code.get(rowNum);
					rowNum++;
					index = 0;
				}
			}
		} else {
			switch (current.charAt(index)) {
			case '#':
				tok = new Token(TokenType.EOLN, "", rowNum, index + 1);
				if (rowNum == code.size())
					sentLastEOLN = true;
				else {
					current = code.get(rowNum);
					rowNum++;
					index = 0;
				}
				break;
			case '+':
				tok = new Token(TokenType.PLUS, "+", rowNum, index + 1);
				index++;
				break;
			case '-':
				tok = new Token(TokenType.MINUS, "-", rowNum, index + 1);
				index++;
				break;
			case '*':
				tok = new Token(TokenType.TIMES, "*", rowNum, index + 1);
				index++;
				break;
			case '/':
				if (current.charAt(index + 1) == '=') {
					tok = new Token(TokenType.NTEQ, "/=", rowNum, index + 1);
					index += 2;
				} else {
					tok = new Token(TokenType.DIVIDE, "/", rowNum, index + 1);
					index++;
				}
				break;
			case '(':
				tok = new Token(TokenType.LPAREN, "(", rowNum, index + 1);
				index++;
				break;
			case ')':
				tok = new Token(TokenType.RPAREN, ")", rowNum, index + 1);
				index++;
				break;
			case '^':
				tok = new Token(TokenType.EXPONENT, "^", rowNum, index + 1);
				index++;
				break;
			case '<':
				if (current.charAt(index + 1) == '-') {
					tok = new Token(TokenType.ASSIGN, "<-", rowNum, index + 1);
					index += 2;
				} else if (current.charAt(index + 1) == '=') {
					tok = new Token(TokenType.LE, "<=", rowNum, index + 1);
					index += 2;
				} else {
					tok = new Token(TokenType.LT, "<", rowNum, index + 1);
					index++;
				}
				break;
			case '>':
				if (current.charAt(index + 1) == '=') {
					tok = new Token(TokenType.GE, ">=", rowNum, index + 1);
					index += 2;
				} else {
					tok = new Token(TokenType.GT, ">", rowNum, index + 1);
					index++;
				}
				break;
			case '=':
				tok = new Token(TokenType.EQ, "=", rowNum, index + 1);
				index++;
				break;
			default:
				if (Character.isDigit(current.charAt(index))) {
					int begin = index;
					String num = getNum();
					if (num.contains("."))
						return new Token(TokenType.FLOATLIT, num, rowNum, begin + 1);
					else
						return new Token(TokenType.INTLIT, num, rowNum, begin + 1);
				} else if (Character.isLetter(current.charAt(index))) {
					int begin = index;
					String id = getIdentifier();
					if (id.equals("display"))
						tok = new Token(TokenType.DISPLAY, id, rowNum, begin + 1);
					else if (id.equals("input"))
						tok = new Token(TokenType.INPUT, id, rowNum, begin + 1);
					else if (id.equals("block"))
						tok = new Token(TokenType.BLOCK, id, rowNum, begin + 1);
					else if (id.equals("if"))
						tok = new Token(TokenType.IF, id, rowNum, begin + 1);
					else if (id.equals("then"))
						tok = new Token(TokenType.THEN, id, rowNum, begin + 1);
					else if (id.equals("else"))
						tok = new Token(TokenType.ELSE, id, rowNum, begin + 1);
					else if (id.equals("end"))
						tok = new Token(TokenType.END, id, rowNum, begin + 1);
					else if (id.equals("do"))
						tok = new Token(TokenType.DO, id, rowNum, begin + 1);
					else if (id.equals("while"))
						tok = new Token(TokenType.WHILE, id, rowNum, begin + 1);
					else if (id.equals("repeat"))
						tok = new Token(TokenType.REPEAT, id, rowNum, begin + 1);
					else if (id.equals("until"))
						tok = new Token(TokenType.UNTIL, id, rowNum, begin + 1);
					else
						tok = new Token(TokenType.IDENTIFIER, id, rowNum, begin + 1);
				} else if (current.charAt(index) == '.') {
					int start = index;
					index++;
					String s = getDigitSequence();
					tok = new Token(TokenType.FLOATLIT, s, rowNum, start);
				} else
					throw new LexicalError(rowNum, index+1, current.charAt(index));

			}
		}
		return tok;
	}

	/**
	 * @return longest string containing strictly digits starting at current
	 *         character
	 * @throws LexicalError
	 */
	private String getDigitSequence() throws LexicalError {
		int start = index;
		while (index < current.length() && Character.isDigit(current.charAt(index)))
			index++;
		return current.substring(start, index);
	}

}
