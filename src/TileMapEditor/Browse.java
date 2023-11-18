package TileMapEditor;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Browse extends JPanel {

	static private final String newline = "\n";
	JTextArea log;
	JFileChooser fc;

	public Browse() {
		super(new BorderLayout());
		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);
		fc = new JFileChooser();
	}

	@Override
	public String getName() {
		File file = fc.getSelectedFile();
		return file.getName();
	}

	public String getFile() {
		File file = fc.getSelectedFile();
		return "" + fc.getSelectedFile();
	}

	public void BrowseFile(String fileLoc, boolean map) {
		fc.setCurrentDirectory(new File("C:/Users/The Source/Desktop/Eclipse Projects/Project Penguin/res/cache/" + fileLoc));
		FileFilter filter;
		if (map) {
			filter = new FileNameExtensionFilter("Map file", "map");
		} else if (!map) {
			filter = new FileNameExtensionFilter("Tileset file", "gif");
		} else {
			filter = new FileNameExtensionFilter("Any file", "*");
		}
		fc.setFileFilter(filter);
		fc.addChoosableFileFilter(filter);
		int returnVal = fc.showOpenDialog(Browse.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
		} else {
			log.append("Open command cancelled by user." + newline);
		}
		log.setCaretPosition(log.getDocument().getLength());
	}

	public void saveFile() {
		fc.setCurrentDirectory(new File("C:/Users/The Source/Desktop/Eclipse Projects/Project Penguin/res/cache/Maps/"));
	    int retrival = fc.showSaveDialog(Browse.this);
	    if (retrival == JFileChooser.APPROVE_OPTION) {
	        try {
				File file = fc.getSelectedFile();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
		} else {
			log.append("Open command cancelled by user." + newline);
		}
		log.setCaretPosition(log.getDocument().getLength());
	}
}