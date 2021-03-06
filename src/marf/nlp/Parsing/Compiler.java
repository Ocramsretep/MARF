package marf.nlp.Parsing;

import marf.util.Debug;


/**
 * <p>General Compiler Class.
 *
 * This class instantiates the parser with the given command-line
 * arguments and invokes it then. Afterwards, it collects the results
 * of lexical and syntax analysis and outputs them.</p>
 *
 * TODO: complete review.
 *
 * <p>(C) 2001 - 2012 Serguei Mokhov, mokhov@cs.concordia.ca</p>
 * 
 * @author Serguei Mokhov
 */
public class Compiler
{
	/**
	 * Instance of our parser.
	 */
	protected Parser oParser = null;

	/**
	 * Default compiler.
	 * @since 0.3.0.5
	 */
	public Compiler()
	{
	}
	
	/**
	 * Main compilation method.
	 * TODO: employ OptionProcessor.
	 * TODO: move option processing to main().
	 *
	 * @param argv command-line arguments
	 * @since 0.3.0.5
	 * @throws CompilerError in case of any lexical, syntax, semantic, or otherwise errors
	 */
	public void compile(final String[] argv)
	throws CompilerError
	{
		if(argv.length < 1)
		{
			System.out.println
			(
				"Too few arguments...\nUsage: java "
				+ getClass().getName()
				+ " [-v | --version] | FILENAME\n"
			);
		}
		else
		{
			if(argv[0].equals("--version") || argv[0].equals("-v"))
			{
				System.out.println("MARF Language Compiler $Revision: 1.21 $");
				System.out.println("Copyright (C) 2001 - 2012 Serguei A. Mokhov");
				System.out.println("Report bugs to: <mailto:marf-bugs@lists.sf.net>");
			}
			else
			{
				System.out.println("Inititating compilation....");

				// Parsing
				System.out.println("Instantiating parser... ");
				this.oParser = new Parser(argv);
				System.out.println("Initiating MARF parsing process......\n");

				if(this.oParser.parse() == true)
				{
					System.out.println("The source program from \"" + argv[0] + "\" was successfully parsed. :-)\n");
				}
				else
				{
					System.out.println("Parsing failed for the source program from \"" + argv[0] + "\". :-( \n");
				}

				// Output stats
				LexicalAnalyzer oLexer = this.oParser.getLexer();
				oLexer.serialize(1);

				System.out.println("The lexical analysis output file is \"" + oLexer.getOutputFilename() + "\".");

				if(oLexer.getErrorsPresent() == true)
				{
					System.out.println
					(
						"There were " + oLexer.getLexicalErrors().size() +
						" lexical errors while scanning the source file."
					);

					System.out.println("The error log file is \"" + oLexer.getErrorLogFilename() + "\".");
				}

				System.out.println("Global Scope Symbol Table has " + Parser.getSymbolTable().getSymTabEntries().size() + " entries.");
			}
		}
	}
	
	/**
	 * Main Function.
	 * @param argv command-line arguments
	 */
	public static final void main(String[] argv)
	{
		try
		{
			Debug.enableDebug(true);
			System.out.println("Language Compiler has started....");
			new Compiler().compile(argv);
			System.out.println("Terminating...");
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

// EOF
