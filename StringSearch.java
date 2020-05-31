import java.nio.file.*;
import java.io.IOException;

class FileHelper {
	/*
	 * Takes a path to a file and returns all of the lines in the file as an array
	 * of strings, printing an error if it failed.
	 */
	static String[] getLines(String path) {
		try {
			return Files.readAllLines(Paths.get(path)).toArray(String[]::new);
		} catch (IOException e) {
			System.err.println("Error reading file " + path + ": " + e);
			return new String[] { "Error reading file " + path + ": " + e };
		}
	}
}

interface Query {
	boolean matches(String line);
}

class LengthQuery implements Query {
	int length;

	LengthQuery(int length) {
		this.length = length;
	}

	public boolean matches(String line) {
		return line.length() == this.length;
	}
}

class GreaterQuery implements Query {
	int length;

	GreaterQuery(int length) {
		this.length = length;
	}

	public boolean matches(String line) {
		return line.length() > this.length;
	}
}

class LessQuery implements Query {
	int length;

	LessQuery(int length) {
		this.length = length;
	}

	public boolean matches(String line) {
		return line.length() < this.length;
	}
}

class ContainsQuery implements Query {
	String match;

	ContainsQuery(String match) {
		this.match = match;
	}

	public boolean matches(String line) {
		return line.contains(this.match);
	}
}

class StartsQuery implements Query {
	String match;

	StartsQuery(String match) {
		this.match = match;
	}

	public boolean matches(String line) {
		return line.startsWith(this.match);
	}
}

class EndsQuery implements Query {
	String match;

	EndsQuery(String match) {
		this.match = match;
	}

	public boolean matches(String line) {
		return line.endsWith(this.match);
	}
}

class NotQuery implements Query {
	Query q;

	NotQuery(Query q) {
		this.q = q;
	}

	public boolean matches(String line) {
		return !this.q.matches(line);
	}
}

class StringSearch {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please provide the name of a file");
			return;
		}	

		// Read the lines of the file
		String filePath = args[0];
		String[] lines = FileHelper.getLines(filePath);		

		if (args.length == 1) {
			// No query provided. Print all the lines.
			for (String line: lines) {
				System.out.println(line);
			}
		}
		else if (args.length == 2) {		
			Query query = parseQuery(args[1]);

			if (query != null) {
				// A single valid query was provided. Print lines matching that query.
				for (String line : lines) {
					if (query.matches(line)) {
						System.out.println(line);
					}
				}
			}			
		}
	}

	/*
	 * This method accepts a String parameter representing a single Query,
	 * and returns a matching Query object.
	 */
	static Query parseQuery(String arg) {
		Query rs;

		if (arg.startsWith("length")) {
			rs = new LengthQuery(getInt(arg));
		} 
		else if (arg.startsWith("greater")) {
			rs = new GreaterQuery(getInt(arg));
		} 
		else if (arg.startsWith("less")) {
			rs = new LessQuery(getInt(arg));
		} 
		else if (arg.startsWith("contains")) {
			rs = new ContainsQuery(getString(arg));
		} 
		else if (arg.startsWith("starts")) {
			rs = new StartsQuery(getString(arg));
		} 
		else if (arg.startsWith("ends")) {
			rs = new EndsQuery(getString(arg));
		} 
		else if (arg.startsWith("not(")) {
			int startIndex = "not(".length();
			int endIndex = arg.length() - 1;
			Query q = parseQuery(arg.substring(startIndex, endIndex));
			rs = new NotQuery(q);
		}
		else {
			System.out.println("Query not recognized: " + arg);
			rs = null;
		}

		return rs;
	}

	/*
	 * This method accepts a String parameter with the following format: 
	 *     "query='argument'"
	 * and returns "argument" as a String.
	 */
	static String getString(String str) {
		String rs;
		String[] queryAndArg = str.split("=");

		if (queryAndArg.length > 1) {
			String quotedRs = queryAndArg[1];
			rs = quotedRs.substring(1, quotedRs.length() - 1);
		}
		else {
			System.out.println("Could not get the argument for: " + str);
			rs = null;
		}

		return rs;
	}

	/*
	 * This method accepts a String parameter with the following format:
	 *     "query=argument"
	 * and returns argument as an integer.
	 */
	static int getInt(String str) {		
		int rs;
		String[] queryAndArg = str.split("=");

		if (queryAndArg.length > 1) {
			rs = Integer.valueOf(queryAndArg[1]);
		}
		else {
			System.out.println("Could not get the argument for: " + str);
			rs = -1;
		}

		return rs;		
	}
}
