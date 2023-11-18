package TileMapEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class MapPanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 480;
	public static final int SCALE = 1;
	public static int TILESIZE = 30;
	public static final int numRows = 2;

	private String path = "C:/Users/The Source/Desktop/Eclipse Projects/Project Penguin/res/cache/Tilesets/snowtileset.gif";
	private static BufferedImage tileset;
	private static BufferedImage[][] tiles;
	private static int numTiles;

	private Thread thread;
	private int FPS = 30;
	private long targetTime = 1000 / FPS;

	private BufferedImage image;
	private Graphics2D g;

	private static Block[] blocks;
	private BufferedImage currentBlockImage;
	private int currentBlock;

	static int[][] map;
	static int mapWidth;
	static int mapHeight;

	private int xmap;
	private int ymap;
	private boolean shiftDown;
	private int mmx;
	private int mmy;
	private int xblock;
	private boolean ctrlDown;
	private boolean altDown;

	private int mousex;
	private int mousey;
	private int tilex;
	private int tiley;

	public MapPanel() {
		setPreferredSize(new Dimension(1140, 500));
		setFocusable(true);
		setLayout(null);
		requestFocus();

	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public void run() {

		init();

		long start;
		long elapsed;
		long wait;

		while (true) {
			start = System.nanoTime();
			update();
			render();
			draw();
			elapsed = (System.nanoTime() - start) / 1000000;
			wait = targetTime - elapsed;
			if (wait < 0)
				wait = 10;
			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void init() {

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();

		mapWidth = 20;
		mapHeight = 15;
		map = new int[mapHeight][mapWidth];

		try {
			tileset = ImageIO.read(new File(path));
			int width = tileset.getWidth() / TILESIZE;
			numTiles = width * numRows;
			tiles = new BufferedImage[numRows][width];
			for (int i = 0; i < width; i++) {
				tiles[0][i] = tileset.getSubimage(TILESIZE * i, 0, TILESIZE, TILESIZE);
				tiles[1][i] = tileset.getSubimage(TILESIZE * i, TILESIZE, TILESIZE, TILESIZE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// create clickable blocks
		blocks = new Block[numTiles];
		int width = numTiles / numRows;
		for (int i = 0; i < width; i++) {
			blocks[i] = new Block(tiles[0][i]);
			blocks[i].setPosition(i * TILESIZE, HEIGHT - 2 * TILESIZE);
			blocks[i + width] = new Block(tiles[1][i]);
			blocks[i + width].setPosition(i * TILESIZE, HEIGHT - TILESIZE);
		}

	}

	public static void loadTileSet(String s) {
		try {
			tileset = ImageIO.read(new File(s));
			TILESIZE = tileset.getHeight() / 2;
			int width = tileset.getWidth() / TILESIZE;
			numTiles = width * numRows;
			tiles = new BufferedImage[numRows][width];
			for (int i = 0; i < width; i++) {
				tiles[0][i] = tileset.getSubimage(TILESIZE * i, 0, TILESIZE, TILESIZE);
				tiles[1][i] = tileset.getSubimage(TILESIZE * i, TILESIZE, TILESIZE, TILESIZE);
			}
			blocks = new Block[numTiles];
			for (int i = 0; i < width; i++) {
				blocks[i] = new Block(tiles[0][i]);
				blocks[i].setPosition(i * TILESIZE, HEIGHT - 2 * TILESIZE);
				blocks[i + width] = new Block(tiles[1][i]);
				blocks[i + width].setPosition(i * TILESIZE, HEIGHT - TILESIZE);
			}

		} catch (Exception e) {
			System.out.println("Couldn't load " + s);
			e.printStackTrace();
		}

	}

	private void update() {	

	}

	private void render() {

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH - 140, HEIGHT);

		// draw map
		for (int row = 0; row < mapHeight; row++) {
			for (int col = 0; col < mapWidth; col++) {
				try {
					g.drawImage(blocks[map[row][col]].getImage(), col * TILESIZE + xmap, row * TILESIZE + ymap, null);
				} catch (Exception e) {
				}
			}
		}

		// draw map dimensions
		g.setColor(Color.RED);
		g.drawRect(xmap, ymap, mapWidth * TILESIZE, mapHeight * TILESIZE);

		// draw map border
		g.setColor(Color.GREEN);
		g.drawRect(xmap, ymap, 320, 240);

		// draw clickable blocks
		int bo = HEIGHT - numRows * TILESIZE;
		g.setColor(Color.WHITE);
		g.fillRect(0, bo, WIDTH, numRows * TILESIZE);
		for (int i = 0; i < numTiles; i++) {
			blocks[i].draw(g);
		}
		
		g.setColor(Color.CYAN);
		g.fillRect(660, bo, WIDTH, numRows * TILESIZE);

		
		// draw current block
		g.setColor(Color.RED);
		int width = numTiles / numRows;
		try {
			g.drawRect((currentBlock % width) * TILESIZE + xblock, bo + TILESIZE * (currentBlock / width), TILESIZE, TILESIZE);
		} catch (Exception e) {
		}

		// draw current block number
		g.drawString("Block ID: " + currentBlock, WIDTH - 250, 50);

		// draw position
		g.setColor(Color.RED);
		g.drawString(mousex + ", " + mousey, WIDTH - 200, 20);
		g.drawString(tilex + ", " + tiley, WIDTH - 200, 35);
		
		// draw sq indicater
		g.setColor(Color.BLUE);
		g.drawRect(tilex * 30 + xmap, tiley * 30 + ymap, TILESIZE, TILESIZE);
		if (currentBlock > 0)
		g.drawImage(blocks[currentBlock].getImage(), tilex * 30 + xmap, tiley * 30 + ymap, null);

	}

	private void draw() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}

	@SuppressWarnings("unused")
	private void fillRecursive(int tx, int ty, int cb) {
		if (tx == -1 || tx == mapWidth || ty == -1 || ty == mapHeight)
			return;
		if (map[ty][tx] != cb)
			return;
		map[ty][tx] = currentBlock;
		fillRecursive(tx - 1, ty, cb);
		fillRecursive(tx + 1, ty, cb);
		fillRecursive(tx, ty - 1, cb);
		fillRecursive(tx, ty + 1, cb);
	}

	// ///////////////////////////////////////////////////////////////////

	public void keyTyped(KeyEvent key) {
	}

	public void keyPressed(KeyEvent key) {
		int k = key.getKeyCode();
		if (k == KeyEvent.VK_SHIFT) {
			shiftDown = true;
		}
		if (k == KeyEvent.VK_CONTROL) {
			ctrlDown = true;
		}
		if (k == KeyEvent.VK_ALT) {
			altDown = true;
		}

		if (k == KeyEvent.VK_T) {
			String str = JOptionPane.showInputDialog(null, "Set tile size:", "Tile Size", 1);
			int i = Integer.parseInt(str);
			TILESIZE = i;
		}
		if (k == KeyEvent.VK_EQUALS) {
			String str = JOptionPane.showInputDialog(null, "Amount increased:", "Shifted", 1);
			int amount = Integer.parseInt(str);
			int nt = numTiles - (amount * 3);
			for (int row = 0; row < mapHeight; row++) {
				for (int col = 0; col < mapWidth; col++) {
					if (map[row][col] >= nt / 3) {
						if (map[row][col] < 2 * nt / 3) {
							map[row][col] += amount;
						} else {
							map[row][col] += 2 * amount;
						}
					}
				}
			}
		}
		if (k == KeyEvent.VK_MINUS) {
			String str = JOptionPane.showInputDialog(null, "Amount decreased:", "Shifted", 1);
			int amount = Integer.parseInt(str);
			int nt = numTiles + (amount * 3);
			for (int row = 0; row < mapHeight; row++) {
				for (int col = 0; col < mapWidth; col++) {
					if (map[row][col] >= nt / 3) {
						if (map[row][col] < 2 * nt / 3) {
							map[row][col] -= amount;
						} else {
							map[row][col] -= amount * 2;
						}
					}
				}
			}
		}
		if (k == KeyEvent.VK_L) {
			if (shiftDown) {
				boolean ok = true;
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						if (map[row][col] == blocks.length - 1) {
							ok = false;
							break;
						}
					}
					if (!ok) {
						break;
					}
				}
				if (!ok)
					return;
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						map[row][col]++;
					}
				}
			}
		}
		if (k == KeyEvent.VK_K) {
			if (shiftDown) {
				boolean ok = true;
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						if (map[row][col] == 0) {
							ok = false;
							break;
						}
					}
					if (!ok) {
						break;
					}
				}
				if (!ok)
					return;
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						map[row][col]--;
					}
				}
			}
		}
		// replace
		if (k == KeyEvent.VK_V) {
			try {
				int source = Integer.parseInt(JOptionPane.showInputDialog(null, "Source", "Replace", 1));
				int dest = Integer.parseInt(JOptionPane.showInputDialog(null, "Destination", "Replace", 1));
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						if (map[row][col] == source) {
							map[row][col] = dest;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// shift everything above num
		if (k == KeyEvent.VK_C) {
			try {
				int num = Integer.parseInt(JOptionPane.showInputDialog(null, "Increment 1 from: ", "Shift", 1));
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						if (map[row][col] >= numTiles / 3)
							continue;
						if (map[row][col] >= num) {
							map[row][col]++;
							if (map[row][col] == numTiles) {
								map[row][col]--;
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		if (k == KeyEvent.VK_RIGHT) {
			if (shiftDown) {
				mapWidth++;
				int[][] temp = new int[mapHeight][mapWidth];
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth - 1; col++) {
						temp[row][col] = map[row][col];
					}
				}
				map = temp;
			} else if (ctrlDown) {
				xblock += TILESIZE;
				int width = numTiles / numRows;
				for (int i = 0; i < width; i++) {
					blocks[i].setPosition(i * TILESIZE + xblock, HEIGHT - 2 * TILESIZE);
					blocks[i + width].setPosition(i * TILESIZE + xblock, HEIGHT - TILESIZE);
				}
			} else if (altDown) {
				for (int row = 0; row < mapHeight; row++) {
					for (int col = mapWidth - 1; col > 0; col--) {
						map[row][col] = map[row][col - 1];
					}
					map[row][0] = 0;
				}
			} else {
				xmap -= TILESIZE;
			}
		}
		if (k == KeyEvent.VK_LEFT) {
			if (shiftDown) {
				mapWidth--;
				int[][] temp = new int[mapHeight][mapWidth];
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						temp[row][col] = map[row][col];
					}
				}
				map = temp;
			} else if (ctrlDown) {
				xblock -= TILESIZE;
				int width = numTiles / numRows;
				for (int i = 0; i < width; i++) {
					blocks[i].setPosition(i * TILESIZE + xblock, HEIGHT - 2 * TILESIZE);
					blocks[i + width].setPosition(i * TILESIZE + xblock, HEIGHT - TILESIZE);
				}
			} else if (altDown) {
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth - 1; col++) {
						map[row][col] = map[row][col + 1];
					}
					map[row][mapWidth - 1] = 0;
				}
			} else {
				xmap += TILESIZE;
			}
		}
		if (k == KeyEvent.VK_UP) {
			if (shiftDown) {
				mapHeight--;
				int[][] temp = new int[mapHeight][mapWidth];
				for (int row = 0; row < mapHeight; row++) {
					for (int col = 0; col < mapWidth; col++) {
						temp[row][col] = map[row][col];
					}
				}
				map = temp;
			} else if (altDown) {
				for (int col = 0; col < mapWidth; col++) {
					for (int row = 0; row < mapHeight - 1; row++) {
						map[row][col] = map[row + 1][col];
					}
					map[mapHeight - 1][col] = 0;
				}
			} else {
				ymap += TILESIZE;
			}
		}
		if (k == KeyEvent.VK_DOWN) {
			if (shiftDown) {
				mapHeight++;
				int[][] temp = new int[mapHeight][mapWidth];
				for (int row = 0; row < mapHeight - 1; row++) {
					for (int col = 0; col < mapWidth; col++) {
						temp[row][col] = map[row][col];
					}
				}
				map = temp;
			} else if (altDown) {
				for (int col = 0; col < mapWidth; col++) {
					for (int row = mapHeight - 1; row > 0; row--) {
						map[row][col] = map[row - 1][col];
					}
					map[0][col] = 0;
				}
			} else {
				ymap -= TILESIZE;
			}
		}
	}

	public void keyReleased(KeyEvent key) {
		int k = key.getKeyCode();
		if (k == KeyEvent.VK_SHIFT) {
			shiftDown = false;
		}
		if (k == KeyEvent.VK_CONTROL) {
			ctrlDown = false;
		}
		if (k == KeyEvent.VK_ALT) {
			altDown = false;
		}
	}

	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

	public void mouseClicked(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isLeftMouseButton(me)) {
			int y = me.getY() / SCALE;
			int x = me.getX() / SCALE - xblock;
			// clicked a block
			int b = 0;
			if (y >= HEIGHT - numRows * TILESIZE) {
				b = x / TILESIZE;
				if (y >= HEIGHT - (numRows - 1) * TILESIZE) {
					b = x / TILESIZE + numTiles / numRows;
				}
				setCurrentBlockImage(blocks[b].getImage());
				currentBlock = b;
			} else {
				y = me.getY() / SCALE - ymap;
				x = me.getX() / SCALE - xmap;
				if (x > 0 && x < mapWidth * TILESIZE && y > 0 && y < mapHeight * TILESIZE) {
					map[y / TILESIZE][x / TILESIZE] = currentBlock;
				}
			}
		} else if (SwingUtilities.isRightMouseButton(me)) {
			int y = me.getY() / SCALE - ymap;
			int x = me.getX() / SCALE - xmap;
			if (x > 0 && x < mapWidth * TILESIZE && y > 0 && y < mapHeight * TILESIZE) {
				map[y / TILESIZE][x / TILESIZE] = 0;
			}
		} else if (SwingUtilities.isMiddleMouseButton(me)) {
			mmx = me.getX();
			mmy = me.getY();
		}
	}

	public void mouseReleased(MouseEvent me) {
	}

	public void mouseMoved(MouseEvent me) {
		mousex = me.getX() - xmap;
		mousey = me.getY() - ymap;
		tilex = mousex / TILESIZE;
		tiley = mousey / TILESIZE;
	}

	public void mouseDragged(MouseEvent me) {
		if (SwingUtilities.isLeftMouseButton(me)) {
			int y = me.getY() / SCALE;
			if (y >= HEIGHT - numRows * TILESIZE) {
			} else {
				y = me.getY() / SCALE - ymap;
				int x = me.getX() / SCALE - xmap;
				if (x > 0 && x < mapWidth * TILESIZE && y > 0 && y < mapHeight * TILESIZE) {
					map[y / TILESIZE][x / TILESIZE] = currentBlock;
				}
			}
		} else if (SwingUtilities.isRightMouseButton(me)) {
			int y = me.getY() / SCALE - ymap;
			int x = me.getX() / SCALE - xmap;
			if (x > 0 && x < mapWidth * TILESIZE && y > 0 && y < mapHeight * TILESIZE) {
				map[y / TILESIZE][x / TILESIZE] = 0;
			}
		} else if (SwingUtilities.isMiddleMouseButton(me)) {
			int y = me.getY();
			int x = me.getX();
			int dx = (x - mmx) / TILESIZE;
			int dy = (y - mmy) / TILESIZE;
			if (dx != 0 || dy != 0) {
				mmx = me.getX();
				mmy = me.getY();
				xmap += dx * TILESIZE;
				ymap += dy * TILESIZE;
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent mwe) {
		int notches = mwe.getWheelRotation();
		if (notches < 0) {
			currentBlock--;
		} else {
			currentBlock++;
		}
	}

	public static void newMap() {
		mapWidth = 10;
		mapHeight = 8;
		map = new int[mapHeight][mapWidth];
	}

	public static void saveMap(String str) {
		try {
			if (str == null)
				return;
			BufferedWriter bw = new BufferedWriter(new FileWriter(str));
			bw.write(mapWidth + "\n");
			bw.write(mapHeight + "\n");
			for (int row = 0; row < mapHeight; row++) {
				for (int col = 0; col < mapWidth; col++) {
					bw.write(map[row][col] + " ");
				}
				bw.write("\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadMap(String str) {
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(str));
			mapWidth = Integer.parseInt(br.readLine());
			mapHeight = Integer.parseInt(br.readLine());
			map = new int[mapHeight][mapWidth];
			String delim = "\\s+";
			for (int row = 0; row < mapHeight; row++) {
				String line = br.readLine();
				String[] tokens = line.split(delim);
				for (int col = 0; col < mapWidth; col++) {
					map[row][col] = Integer.parseInt(tokens[col]);
				}
			}
		} catch (Exception e) {
			System.out.println("Couldn't load maps: " + str);
			e.printStackTrace();
		}
	}

	public BufferedImage getCurrentBlockImage() {
		return currentBlockImage;
	}

	public void setCurrentBlockImage(BufferedImage currentBlockImage) {
		this.currentBlockImage = currentBlockImage;
	}
}