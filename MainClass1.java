package com.code.Asgmnt1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainClass1
{

	public static void main(String[] args)
	{
		try
		{
			ArrayList<String> code = getProgramCode("test.txt");
			Parser p = new Parser(code);
			ParseTree prog = p.parse();
			prog.execute();
		}
		catch (IOException e)
		{
			System.err.println (e.getMessage());
		}
		catch (LexicalError e)
		{
			System.err.println (e.getMessage());
		}
		catch (ParseError e)
		{
			System.err.println (e.getMessage());
		}
		catch (Exception e) {
			System.out.println("~~Exception occured~~");
			e.printStackTrace();
		}
	}

	private static ArrayList<String> getProgramCode(String fileName) throws IOException
	{
		File f = new File (fileName);
		BufferedReader br = new BufferedReader(new FileReader(f));
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null)
			lines.add(line);
		br.close();
		return lines;
	}

}
