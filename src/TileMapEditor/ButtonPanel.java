package TileMapEditor;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ButtonPanel extends JPanel {

	Browse browse = new Browse();
	
	public ButtonPanel() {
		
		setPreferredSize(new Dimension(140, 175));
		setLayout(null);
		
		JButton btnLoadMap = new JButton("Load Map");
		btnLoadMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				browse.BrowseFile("Maps", true);
				MapPanel.loadMap(browse.getFile());
			}
		});
		
		btnLoadMap.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLoadMap.setBounds(10, 44, 120, 23);
		add(btnLoadMap);
		
		JButton btnSaveMap = new JButton("Save Map");
		btnSaveMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				browse.saveFile();
				MapPanel.saveMap(browse.getFile());
			}
		});
		btnSaveMap.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSaveMap.setBounds(10, 112, 120, 23);
		add(btnSaveMap);
		
		JButton btnLoadTileset = new JButton("Load Tiles");
		btnLoadTileset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browse.BrowseFile("tilesets", false);
				MapPanel.loadTileSet(browse.getFile());
			}
		});
		btnLoadTileset.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLoadTileset.setBounds(10, 78, 120, 23);
		add(btnLoadTileset);
		
		JButton btnNewMap = new JButton("New Map");
		btnNewMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MapPanel.newMap();
			}
		});
		btnNewMap.setBounds(10, 11, 120, 23);
		add(btnNewMap);
		
		JButton btnNewButton = new JButton("Add Item");
		btnNewButton.setBounds(10, 146, 120, 23);
		add(btnNewButton);

	}
}
