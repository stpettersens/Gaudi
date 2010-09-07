/*
 Find file in path in system variable
 @author Sam Saint-Pettersen, 2010
 Released into the public domain.
	
 Originally written for use with 
 the NSIS installation script for Gaudi.
	
 But use as you like. No warranty.
	
 Usage: java -cp . FindInPath <system variable> <file>
 Output: (Each path to every file found)
 Exit code: 0 (not found) / 1 (found)
*/
import java.io.File;

class FindInPath {
	public static void main(String[] args) {
		byte returned = 0; // Return exit code 0 for file not found, default assumption
		String systemVar = System.getenv(args[0]);
		String[] paths = systemVar.split(";");
		
		// For each path in system variable, try to find file
		for(int i = 0; i < paths.length; i++) {
			if(new File(paths[i]+"\\"+args[1]).exists()) {
				System.out.println(paths[i]);
				returned = 1; // Return exit code 1 for file found
				break;
			}
		}
		System.exit(returned);
	}
}
