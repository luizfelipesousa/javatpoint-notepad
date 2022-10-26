package notepad;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileOperation {

	Notepad notepad;

	boolean saved;
	boolean newFileFlag;

	String fileName;
	String applicationTitle = "Notepad - JavaTPoint";

	File fileReference;

	JFileChooser fileChooser;

	public FileOperation(Notepad notepad) {
		saved = true;
		newFileFlag = true;
		fileName = new String("Untitled");
		fileReference = new File(fileName);
		notepad.frame.setTitle(fileName + " - " + applicationTitle);
		this.notepad = notepad;

		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new MyFileFilter(".java", "Java Source Files(*.java)"));
		fileChooser.addChoosableFileFilter(new MyFileFilter(".txt", "Text Files(*.txt)"));
	}

	public boolean saveFile(File temp) {
		try (FileWriter fileWriter = new FileWriter(temp)) {
			fileWriter.write(notepad.textArea.getText());
			updateStatus(temp, true);
			return true;
		} catch (IOException e) {
			updateStatus(temp, false);
			return false;
		}
	}

	public boolean saveThisFile() {
		if (!newFileFlag) {
			return saveFile(fileReference);
		}
		return saveAsFile();
	}

	boolean saveAsFile() {
		File temp = null;
		fileChooser.setDialogTitle("Save AS...");
		fileChooser.setApproveButtonText("Save Now");
		fileChooser.setApproveButtonToolTipText("Click me to save!");
		fileChooser.setApproveButtonMnemonic(KeyEvent.VK_S);

		do {

			if (fileChooser.showSaveDialog(notepad.frame) != JFileChooser.APPROVE_OPTION) {
				return false;
			}

			temp = fileChooser.getSelectedFile();

			if (!temp.exists()) {
				break;
			}

			String fileAlreadyExistsText = String.format("<html>%s<br>Do you wnat to replace it?</html>",
					temp.getPath());

			if (JOptionPane.showConfirmDialog(this.notepad.frame, fileAlreadyExistsText, applicationTitle,
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				break;
			}

		} while (true);

		return saveFile(temp);
	}

	public boolean openFile(File temp) {
		try (FileInputStream fileInputStream = new FileInputStream(temp);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))) {

			String text = "";

			while (text != null) {
				text = bufferedReader.readLine();

				if (text == null) {
					break;
				}

				notepad.textArea.append(text + "\n");
			}

			updateStatus(temp, true);
			return true;

		} catch (IOException e) {
			updateStatus(temp, false);
			return false;
		}
	}

	public void openFile() {
		if (!confirmSave()) {
			return;
		}

		fileChooser.setDialogTitle("Open File...");
		fileChooser.setApproveButtonText("Open this");
		fileChooser.setApproveButtonMnemonic(KeyEvent.VK_O);
		fileChooser.setApproveButtonToolTipText("Click me to open the selected file.!");

		File temp = null;

		do {

			if (fileChooser.showOpenDialog(notepad.frame) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			temp = fileChooser.getSelectedFile();

			if (temp.exists()) {
				break;
			}

			String fileNotFoundText = String.format(
					"<html>%s<br>file not found.<br>Please, verify te correct file name was given.</html>",
					temp.getName());

			JOptionPane.showMessageDialog(notepad.frame, fileNotFoundText, applicationTitle,
					JOptionPane.INFORMATION_MESSAGE);

		} while (true);

		notepad.textArea.setText("");

		if (!openFile(temp)) {
			fileName = "Untitled";
			saved = true;
			notepad.frame.setTitle(fileName + " - " + applicationTitle);
		}

		if (!temp.canWrite()) {
			newFileFlag = true;
		}
	}

	public boolean isSave() {
		return saved;
	}

	public void setSave(boolean save) {
		this.saved = save;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private void updateStatus(File temp, boolean saved) {
		if (saved) {
			this.saved = true;
			fileName = new String(temp.getName());

			if (!temp.canWrite()) {
				fileName += " (Read only)";
				newFileFlag = true;
			}

			fileReference = temp;

			notepad.frame.setTitle(fileName + " - " + applicationTitle);
			notepad.statusBar.setText("File: " + temp.getPath() + " saved/opened successfully.");

			newFileFlag = false;
		} else {

			notepad.statusBar.setText("Failed to save/open: " + temp.getPath());
		}
	}

	public boolean confirmSave() {
		String textChangeMessage = String.format(
				"<html>The text in the %s file fas been changed.<br>Do you want to save the changes?</html>", fileName);

		if (!saved) {

			int option = JOptionPane.showConfirmDialog(notepad.frame, textChangeMessage, applicationTitle,
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}

			if (option == JOptionPane.YES_OPTION && !saveAsFile()) {
				return false;
			}

		}
		return true;
	}

	public void newFile() {
		if (!confirmSave()) {
			return;
		}

		notepad.textArea.setText("");
		saved = true;
		newFileFlag = true;
		fileName = new String("Untitled");
		fileReference = new File(fileName);

		notepad.frame.setTitle(fileName + " - " + applicationTitle);

	}

}
