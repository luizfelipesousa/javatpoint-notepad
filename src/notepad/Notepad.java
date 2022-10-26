package notepad;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class Notepad implements ActionListener, MenuConstants {

	JFrame frame;
	JTextArea textArea;
	JLabel statusBar;

	private String fileName = "Untitled";
	String applicationName = "Javapad";
	String searchString;
	String replaceString;
	int lastSearchIndex;

	FileOperation fileHandler;
	FontChooser fontDialog = null;
	FindDialog findReplaceDialog = null;
	JColorChooser bcolorChooser = null;
	JColorChooser fcolorChooser = null;
	JDialog backgroundDialog = null;
	JDialog foregroundDialog = null;
	JMenuItem cutItem;
	JMenuItem copyItem;
	JMenuItem deleteItem;
	JMenuItem findItem;
	JMenuItem findNextItem;
	JMenuItem replaceItem;
	JMenuItem gotoItem;
	JMenuItem selectAllItem;

	Notepad() {
		frame = new JFrame(fileName + " - " + applicationName);
		textArea = new JTextArea(30, 60);
		statusBar = new JLabel("||       Ln 1, Col 1  ", JLabel.RIGHT);

		createMenuBar(frame);

		frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
		frame.add(statusBar, BorderLayout.SOUTH);
		frame.add(new JLabel(" "), BorderLayout.EAST);
		frame.add(new JLabel(" "), BorderLayout.WEST);

		frame.pack();
		frame.setLocation(100, 50);
		frame.setVisible(true);
		frame.setLocation(150, 50);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		fileHandler = new FileOperation(this);

		textArea.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {

				int lineNumber = 0;
				int column = 0;
				int pos = 0;

				try {

					pos = textArea.getCaretPosition();
					lineNumber = textArea.getLineOfOffset(pos);
					column = pos - textArea.getLineStartOffset(lineNumber);

				} catch (Exception e2) {
				}

				if (textArea.getText().length() == 0) {
					lineNumber = 0;
					column = 0;
				}

				statusBar.setText("||       Ln " + (lineNumber + 1) + ", Col " + (column + 1));
			}
		});

		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				fileHandler.saved = false;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				fileHandler.saved = false;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fileHandler.saved = false;
			}

		};

		textArea.getDocument().addDocumentListener(documentListener);

		WindowListener frameClose = new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (fileHandler.confirmSave()) {
					System.exit(0);
				}
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (fileHandler.confirmSave()) {
					System.exit(0);
				}
			}
		};

		frame.addWindowListener(frameClose);

	}

	public void goTo() {
		int lineNumber = 0;
		try {

			lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
			String tempLineNumber = JOptionPane.showInputDialog(frame, "Enter Line Number:", "" + lineNumber);

			if (tempLineNumber == null) {
				return;
			}

			lineNumber = Integer.parseInt(tempLineNumber);
			textArea.setCaretPosition(lineNumber - 1);

		} catch (Exception e) {
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String commandText = e.getActionCommand();

		if (commandText.equals(fileNew)) {
			fileHandler.newFile();
		}

		if (commandText.equals(fileOpen)) {
			fileHandler.openFile();
		}

		if (commandText.equals(fileSave)) {
			fileHandler.saveThisFile();
		}

		if (commandText.equals(fileSaveAs)) {
			fileHandler.saveAsFile();
		}

		if (commandText.equals(fileExit)) {
			boolean confirmSave = fileHandler.confirmSave();
			if (confirmSave) {
				System.exit(0);
			}
		}

		if (commandText.equals(filePrint)) {
			JOptionPane.showMessageDialog(frame, "Get ur printer repaired first! It seems you don't have one!",
					"Bad Printer", JOptionPane.INFORMATION_MESSAGE);
		}

		if (commandText.equals(editCut)) {
			textArea.cut();
		}

		if (commandText.equals(editCopy)) {
			textArea.copy();
		}

		if (commandText.equals(editPaste)) {
			textArea.paste();
		}

		if (commandText.equals(editDelete)) {
			textArea.replaceSelection("");
		}

		if (commandText.equals(editFind)) {
			if (textArea.getText().length() == 0) {
				return;
			}

			if (findReplaceDialog == null) {
				findReplaceDialog = new FindDialog(textArea);
				findReplaceDialog.showDialog(frame, true);
			}
		}

		if (commandText.equals(editFindNext)) {
			if (textArea.getText().length() == 0) {
				return;
			}

			if (findReplaceDialog == null) {
				statusBar.setText("Use Find option of Edit Menu first !!!!");
			} else {
				findReplaceDialog.findNextWithSelection();
			}
		}

		if (commandText.equals(editReplace)) {
			if (textArea.getText().length() == 0) {
				return;
			}

			if (findReplaceDialog == null) {
				findReplaceDialog = new FindDialog(textArea);
				findReplaceDialog.showDialog(frame, false);
			}
		}

		if (commandText.equals(editGoTo)) {
			if (textArea.getText().length() == 0) {
				return;
			}
			goTo();
		}

		if (commandText.equals(editSelectAll)) {
			textArea.selectAll();
		}

		if (commandText.equals(editTimeDate)) {
			textArea.insert(new Date().toString(), textArea.getSelectionStart());
		}

		if (commandText.equals(formatWordWrap)) {
			JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
			textArea.setLineWrap(source.isSelected());
		}

		if (commandText.equals(formatFont)) {
			if (fontDialog == null) {
				fontDialog = new FontChooser(textArea.getFont());
			}

			if (fontDialog.showDialog(frame, "Choose a font")) {
				textArea.setFont(fontDialog.createFont());
			}
		}

		if (commandText.equals(formatForeground)) {
			showForegroundColorDialog();
		}

		if (commandText.equals(formatBackground)) {
			showBackgroundColorDialog();
		}

		if (commandText.equals(viewStatusBar)) {
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) e.getSource();
			statusBar.setVisible(temp.isSelected());
		}

		if (commandText.equals(helpAboutNotepad)) {
			JOptionPane.showMessageDialog(frame, aboutText, "Dedicated to you!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	void showBackgroundColorDialog() {
		if (bcolorChooser == null) {
			bcolorChooser = new JColorChooser();
		}

		if (backgroundDialog == null) {
			backgroundDialog = JColorChooser.createDialog(frame, formatBackground, false, bcolorChooser,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							textArea.setBackground(bcolorChooser.getColor());

						}
					}, null);

			backgroundDialog.setVisible(true);
		}
	}

	void showForegroundColorDialog() {
		if (fcolorChooser == null) {
			fcolorChooser = new JColorChooser();
		}

		if (foregroundDialog == null) {
			foregroundDialog = JColorChooser.createDialog(frame, formatForeground, false, fcolorChooser,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							textArea.setForeground(fcolorChooser.getColor());
						}
					}, null);

			foregroundDialog.setVisible(true);
		}
	}

	public JMenuItem createMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
		JMenuItem jMenuItem = new JMenuItem(s, key);
		jMenuItem.addActionListener(al);
		toMenu.add(jMenuItem);
		toMenu.setVisible(true);

		return jMenuItem;
	}

	public JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, ActionListener al) {
		JMenuItem jMenuItem = new JMenuItem(s, key);
		jMenuItem.addActionListener(al);
		jMenuItem.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK));
		toMenu.add(jMenuItem);

		return jMenuItem;
	}

	public JCheckBoxMenuItem createCheckBoxMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
		JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(s);
		jCheckBoxMenuItem.setMnemonic(key);
		jCheckBoxMenuItem.addActionListener(al);
		jCheckBoxMenuItem.setSelected(false);
		toMenu.add(jCheckBoxMenuItem);

		return jCheckBoxMenuItem;
	}

	public JMenu createMenu(String menuTitle, int key, JMenuBar toMenuBar) {
		JMenu jMenu = new JMenu(menuTitle);
		jMenu.setMnemonic(key);
		toMenuBar.add(jMenu);

		return jMenu;
	}

	public void createMenuBar(JFrame frame) {
		JMenuBar jMenuBar = new JMenuBar();

		JMenuItem temp;

		JMenu fileMenu = createMenu(fileText, KeyEvent.VK_F, jMenuBar);
		JMenu editMenu = createMenu(editText, KeyEvent.VK_E, jMenuBar);
		JMenu formatMenu = createMenu(formatText, KeyEvent.VK_O, jMenuBar);
		JMenu viewMenu = createMenu(viewText, KeyEvent.VK_V, jMenuBar);
		JMenu helpMenu = createMenu(fileText, KeyEvent.VK_H, jMenuBar);

		createMenuItem(fileNew, KeyEvent.VK_N, fileMenu, KeyEvent.VK_N, this);
		createMenuItem(fileOpen, KeyEvent.VK_O, fileMenu, KeyEvent.VK_O, this);
		createMenuItem(fileSave, KeyEvent.VK_S, fileMenu, KeyEvent.VK_S, this);
		createMenuItem(fileSaveAs, KeyEvent.VK_A, fileMenu, KeyEvent.VK_N, this);

		fileMenu.addSeparator();
		temp = createMenuItem(filePageSetup, KeyEvent.VK_U, fileMenu, this);
		temp.setEnabled(false);
		createMenuItem(filePrint, KeyEvent.VK_P, fileMenu, KeyEvent.VK_P, this);
		fileMenu.addSeparator();
		createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);

		temp = createMenuItem(editUndo, KeyEvent.VK_U, editMenu, KeyEvent.VK_Z, this);
		temp.setEnabled(false);
		editMenu.addSeparator();
		cutItem = createMenuItem(editCut, KeyEvent.VK_T, editMenu, KeyEvent.VK_X, this);
		copyItem = createMenuItem(editCopy, KeyEvent.VK_C, editMenu, KeyEvent.VK_C, this);
		createMenuItem(editPaste, KeyEvent.VK_P, editMenu, KeyEvent.VK_V, this);
		deleteItem = createMenuItem(editDelete, KeyEvent.VK_L, editMenu, KeyEvent.VK_C, this);
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		editMenu.addSeparator();
		findItem = createMenuItem(editFindNext, KeyEvent.VK_F, editMenu, KeyEvent.VK_F, this);
		findNextItem = createMenuItem(editFindNext, KeyEvent.VK_N, editMenu, this);
		findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		replaceItem = createMenuItem(editReplace, KeyEvent.VK_R, editMenu, KeyEvent.VK_H, this);
		gotoItem = createMenuItem(editGoTo, KeyEvent.VK_G, editMenu, KeyEvent.VK_G, this);
		editMenu.addSeparator();
		selectAllItem = createMenuItem(editSelectAll, KeyEvent.VK_A, editMenu, KeyEvent.VK_A, this);
		createMenuItem(editTimeDate, KeyEvent.VK_D, editMenu, this)
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		createCheckBoxMenuItem(formatWordWrap, KeyEvent.VK_W, formatMenu, this);

		createMenuItem(formatFont, KeyEvent.VK_F, formatMenu, this);
		formatMenu.addSeparator();
		createMenuItem(formatForeground, KeyEvent.VK_T, formatMenu, this);
		createMenuItem(formatBackground, KeyEvent.VK_P, formatMenu, this);

		createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);

		LookAndFeelMenu.createLookAndFeelMenuItem(viewMenu, frame);

		temp = createMenuItem(helpHelpTopic, KeyEvent.VK_H, helpMenu, this);
		temp.setEnabled(false);
		helpMenu.addSeparator();
		createMenuItem(helpAboutNotepad, KeyEvent.VK_A, helpMenu, this);

		MenuListener editMenuListener = new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				if (textArea.getText().length() == 0) {
					findItem.setEnabled(false);
					findNextItem.setEnabled(false);
					replaceItem.setEnabled(false);
					selectAllItem.setEnabled(false);
					gotoItem.setEnabled(false);

				} else {
					findItem.setEnabled(true);
					findNextItem.setEnabled(true);
					replaceItem.setEnabled(true);
					selectAllItem.setEnabled(true);
					gotoItem.setEnabled(true);
				}

				if (textArea.getSelectionStart() == textArea.getSelectionEnd()) {
					cutItem.setEnabled(false);
					copyItem.setEnabled(false);
					deleteItem.setEnabled(false);

				} else {
					cutItem.setEnabled(true);
					copyItem.setEnabled(true);
					deleteItem.setEnabled(true);

				}
			}

			@Override
			public void menuDeselected(MenuEvent e) {

			}

			@Override
			public void menuCanceled(MenuEvent e) {

			}

		};

		editMenu.addMenuListener(editMenuListener);
		jMenuBar.setVisible(true);
		frame.setJMenuBar(jMenuBar);
	}

}
