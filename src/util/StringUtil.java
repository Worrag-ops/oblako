package util;

public class StringUtil {
	public static String constructDirectoryName(String name) {
		String newName = name.replace(':', ' ');
		newName = newName.replace('*', ' ');
		newName = newName.replace('/', ' ');
		newName = newName.replace('.', ' ');
		newName = newName.replace(',', ' ');
		return newName;
	}
}
