package TileMapEditor;

import javax.swing.JFrame;
import java.awt.Dimension;

public class App {

	public static void main(String[] args) {
		
		JFrame window = new JFrame("Level Editor");
		window.setPreferredSize(new Dimension(1280, 700));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MapPanel mapPanel = new MapPanel();
		window.setContentPane(mapPanel);
		window.pack();
		window.setVisible(true);
		
		JFrame optionWindow = new JFrame();
		ButtonPanel btnPanel = new ButtonPanel();
		optionWindow.setUndecorated(true);
		optionWindow.setContentPane(btnPanel);
		optionWindow.pack();
		optionWindow.setVisible(true);
	}
}