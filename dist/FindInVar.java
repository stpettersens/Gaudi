/*
 Find a value in a system variable.
 Author: Sam Saint-Pettersen, 2010-2011.
 Released into the public domain.
	
 Originally written for use with 
 the NSIS installation script for Gaudi.
	
 But use as you like. No warranty.
	
 Usage: java -cp . FindInVar <system variable> <file>
 Output: (Each path to every file found)
 Exit code: 0 (not found) / 1 (found)
 
 Usage: java -cp . FindInVar <system variable>
 Output: (1st path in system variable)
 Exit code: -1
*/
import java.io.File;

class FindInVar {
	public static void main(String[] args) {
		byte returned = 0; // Return exit code 0 for file not found, default assumption
		String systemVar = System.getenv(args[0]);
		String[] paths = systemVar.split(";");
		
		// With 1 argument; just display the 1st path in the system variable
		if(args.length == 1) {
			System.out.println(paths[0]);
			returned = -1;
		}
		
		// With 2 arguments; for each path in system variable, try to find file
		if(args.length == 2) {
			for(int i = 0; i < paths.length; i++) {
				if(new File(paths[i]+"\\"+args[1]).exists()) {
					System.out.println(paths[i]);
					returned = 1; // Return exit code 1 for file found
					break;
				}
			}
		}
		System.exit(returned);
	}
}
