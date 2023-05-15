package com.code.Asgmnt1;

public class LexicalError extends Exception
{

	private static final long serialVersionUID = 1L;

	private int rowNum;
	
	private int colNum;
	
	private char ch;
	
	public LexicalError(int rowNum, int colNum, char ch)
	{
		super("unknown character "+ch+" found at row: "+rowNum+" col: "+colNum);
		this.rowNum = rowNum;
		this.colNum = colNum;
		this.ch = ch;
	}

	
	public LexicalError(String message)
	{
		super(message);
		this.rowNum = 0;
		this.colNum = 0;
		this.ch = '\0';
	}


	/**
	 * @return the rowNum
	 */
	public int getRowNum()
	{
		return rowNum;
	}

	/**
	 * @return the colNum
	 */
	public int getColNum()
	{
		return colNum;
	}

	/**
	 * @return the ch
	 */
	public char getCh()
	{
		return ch;
	}
	
	
}
