package notepad;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MyFileFilter extends FileFilter {

	private String extension;
	private String description;

	public MyFileFilter(String extension, String description) {
		this.extension = extension.toLowerCase();
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		if (f.getName().toLowerCase().endsWith(extension)) {
			return true;
		}

		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
