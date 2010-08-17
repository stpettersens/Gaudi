/*
 * Display all paths in system PATH variable
 * @Author: Sam Saint-Pettersen
*/
import scala.util.matching.Regex

object PathVar extends Application {
	
	val pathVar: String = System.getProperty("java.library.path")
	println(String.format("PATH {%s}", pathVar))
	val pathPattn: Regex = """([\w\d\s\:\.\-\_\\\/]+);*""".r
	var pathArray: Array[String] = null
	var i: Int = 0
	for(path <- pathPattn.findAllIn(pathVar)) {
		println(String.format("P %s -> %s", i.toString(), path))
		i += 1
	}
}