package com.code.Asgmnt1;

public class ParseTree
{
   
	private Node root;
	
	public ParseTree(Node root)
	{
		this.root = root;
	}
	
	public void execute()
	{
		 root.evaluate();				
	}
	
}
