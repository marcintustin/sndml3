package servicenow.datamart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * A file with YAML Loader Config instructions. 
 * This class is used primarily for JUnit tests.
 *
 */
@SuppressWarnings("serial")
public class YamlFile extends File {
		
	public YamlFile(File file) {		
		super(file.getPath());
	}
	
	public FileReader getReader() throws FileNotFoundException {
		return new FileReader(this);
	}
	
	public LoaderConfig getConfig() throws ConfigParseException, FileNotFoundException {
		return new LoaderConfig(getReader());
	}
	
	public Loader getLoader(ConnectionProfile profile) 
			throws ConfigParseException, FileNotFoundException{
		return new Loader(profile.getSession(), profile.getDatabase(), getConfig());
	}
	
	/**
	 * Return the name of this file to be displayed when running JUnit tests.
	 */
	@Override	
	public String toString() {
		return this.getName();
	}
	
	/**
	 * Return true if a file has a ".yaml" or ".yml" file extension.
	 */
	public static boolean hasYamlExt(File file) {
		String path = file.getPath();
		return path.endsWith(".yaml") || path.endsWith(".yml");
	}
	
}
