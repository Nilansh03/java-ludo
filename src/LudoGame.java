
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class LudoGame extends JPanel {

	private static final long serialVersionUID = 4096501410402784791L;
	private final static int BOARDLEFTOFFSET = 9;
	private final static int BOARDTOPOFFSET = 7;

	private final static int GRIDSIZE = 48;
	private final static int GRIDNUM = 11;
	private final static Point[][] THEGRID = new Point[GRIDNUM][GRIDNUM];
	public static int SLEEP = 200;
	private boolean theShowMustGoOn = true;
	private int thePlayer = 0;

	private final JLabel redLabel;
	private final JLabel blueLabel;
	private final JLabel yellowLabel;
	private final JLabel greenLabel;
	private final ArrayList<Pawn> redPawns = new ArrayList<Pawn>();
	private final ArrayList<Pawn> bluePawns = new ArrayList<Pawn>();
	private final ArrayList<Pawn> yellowPawns = new ArrayList<Pawn>();
	private final ArrayList<Pawn> greenPawns = new ArrayList<Pawn>();
	private final JLayeredPane boardPane;
	private final Die theDie;

	private HomeField redHome;
	private HomeField blueHome;
	private HomeField greenHome;
	private HomeField yellowHome;

	private final ArrayList<GoalField> redGoal = new ArrayList<GoalField>();
	private final ArrayList<GoalField> blueGoal = new ArrayList<GoalField>();
	private final ArrayList<GoalField> yellowGoal = new ArrayList<GoalField>();
	private final ArrayList<GoalField> greenGoal = new ArrayList<GoalField>();

	private final ArrayList<Player> players = new ArrayList<Player>();

	public Runnable continueAfterThreadEnd = new Runnable() {
		@Override
		public void run() {
			continueGameRound();
		}
	};

	private LudoGame() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final ImageIcon boardBackground = createImageIcon("src/board_bkg.png");
		final ImageIcon redPawnImg = createImageIcon("src/red_disk.png");
		final ImageIcon bluePawnImg = createImageIcon("src/blue_disk.png");
		final ImageIcon yellowPawnImg = createImageIcon("src/yellow_disk.png");
		final ImageIcon greenPawnImg = createImageIcon("src/green_disk.png");
		final ImageIcon dieImg = createImageIcon("src/die_1.png");

		setupTheGrid();

		boardPane = new JLayeredPane();
		boardPane.setPreferredSize(new Dimension(540, 540));
		JLabel board = new JLabel(boardBackground);
		boardPane.add(board, new Integer(0));
		Dimension boardSize = board.getPreferredSize();
		board.setBounds(BOARDLEFTOFFSET, BOARDTOPOFFSET, boardSize.width,
				boardSize.height);

		JPanel rightPane = new JPanel();
		rightPane.setLayout(new GridBagLayout());

		JPanel dieLayer = new JPanel();
		dieLayer.setPreferredSize(new Dimension(200, 200));
		dieLayer.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Die Roll"));
		dieLayer.setLayout(new GridBagLayout());

		JLabel die = new JLabel(dieImg);
		dieLayer.add(die, new GridBagConstraints());
		dieLayer.setBackground(new Color(188, 189, 194));

		JPanel playersLayer = new JPanel();
		playersLayer.setPreferredSize(new Dimension(200, 200));
		playersLayer.setBorder(BorderFactory.createTitledBorder(
		
                BorderFactory.createLineBorder(Color.black), "Players"));
		redLabel = new JLabel("Red Player");
		blueLabel = new JLabel("Blue Player");
		yellowLabel = new JLabel("Yellow Player");
		greenLabel = new JLabel("Green Player");
		playersLayer.setLayout(new GridBagLayout());
		playersLayer.setBackground(new Color(188, 189, 194));

		GridBagConstraints playGrid = new GridBagConstraints();
		playGrid.gridy = 0;
		playersLayer.add(redLabel, playGrid);
		playGrid.gridy = 1;
		playersLayer.add(blueLabel, playGrid);
		playGrid.gridy = 2;
		playersLayer.add(yellowLabel, playGrid);
		playGrid.gridy = 3;
		playersLayer.add(greenLabel, playGrid);

		GridBagConstraints theGrid = new GridBagConstraints();
		theGrid.gridy = 0;
		rightPane.add(dieLayer, theGrid);
		theGrid.gridy = 1;
		rightPane.add(playersLayer, theGrid);
		rightPane.setBackground(new Color(188, 189, 194));

		setupTheFields();

		addPawns(redPawnImg, redPawns, redHome);
		addPawns(bluePawnImg, bluePawns, blueHome);
		addPawns(yellowPawnImg, yellowPawns, yellowHome);
		addPawns(greenPawnImg, greenPawns, greenHome);

		setupThePlayers();

		theDie = Die.getInstance(die);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(boardPane);
		add(rightPane);
	}

	
	protected void startTheGame() {
		startGameRound();
	}

	
	private void startGameRound() {
		if (theShowMustGoOn) {
			Player pl = players.get(thePlayer);
			System.out.println("Player " + thePlayer + " starts turn ...");
			pl.setLabelIsTurn();
			int roll = rollDie();
			sleep(SLEEP);
			pl.doMove(roll);
		}
	}

	
	protected void continueGameRound() {
		sleep(SLEEP);
		if (theDie.lastRoll() == 6) {
			startGameRound();
		} else {
			Player pl = players.get(thePlayer);
			System.out.println("Turn done!\n");
			if (pl.checkIfGoalFull()) {
				System.err.println("We have a winner!!!");
				theShowMustGoOn = false;
			}
			pl.setLabelNotTurn();
			sleep(SLEEP);
			if (theShowMustGoOn) {
				System.out.println("Round done! Next round starting...\n");
				thePlayer++;
				if (thePlayer > 3) {
					thePlayer = 0;
				}
				startGameRound();
			}
		}
	}

	
	private int rollDie() {
		int playerRoll = theDie.roll();
		System.out.println("Roll: " + playerRoll);
		theDie.setImage(createImageIcon("src/die_" + playerRoll + ".png"));
		sleep(SLEEP * 2); 
		return playerRoll;
	}

	
	private void sleep(final long milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException ie) {
			System.err
					.println("Unexpected timing error. Aborting thread sleep");
		}
	}

	
	private void setupTheGrid() {
		for (int i = 0; i < GRIDNUM; i++) {
			for (int j = 0; j < GRIDNUM; j++) {
				THEGRID[i][j] = new Point(BOARDLEFTOFFSET + (i * GRIDSIZE),
						BOARDTOPOFFSET + (j * GRIDSIZE));
			}
		}
	}

	
	private void setupTheFields() {

		final int[] gridJ = { 10, 10, 9, 8, 7, 6, 6, 6, 6, 6, 5, 4, 4, 4, 4, 4,
				3, 2, 1, 0, 0, 0, 1, 2, 3, 4, 4, 4, 4, 4, 5, 6, 6, 6, 6, 6, 7,
				8, 9, 10 };
		final int[] gridI = { 5, 4, 4, 4, 4, 4, 3, 2, 1, 0, 0, 0, 1, 2, 3, 4,
				4, 4, 4, 4, 5, 6, 6, 6, 6, 6, 7, 8, 9, 10, 10, 10, 9, 8, 7, 6,
				6, 6, 6, 6 };
		Field lastField = null;
		BasicField firstField = null;
		for (int i = 0; i < 40; i++) {
			BasicField theTrack = new BasicField(THEGRID[gridI[i]][gridJ[i]]);
			if (i % 10 == 0) {
				if (i == 0) {
					firstField = theTrack;
					int[] goalJ = { 9, 8, 7, 6 };
					int[] goalI = { 5, 5, 5, 5 };
					setupTheGoals(redGoal, goalI, goalJ, theTrack);
				} else if (i == 10) {
					int[] goalJ = { 5, 5, 5, 5 };
					int[] goalI = { 1, 2, 3, 4 };
					setupTheGoals(blueGoal, goalI, goalJ, theTrack);
				} else if (i == 20) {
					int[] goalJ = { 1, 2, 3, 4 };
					int[] goalI = { 5, 5, 5, 5 };
					setupTheGoals(yellowGoal, goalI, goalJ, theTrack);
				} else if (i == 30) {
					int[] goalJ = { 5, 5, 5, 5 };
					int[] goalI = { 9, 8, 7, 6 };
					setupTheGoals(greenGoal, goalI, goalJ, theTrack);
				}
			} else if ((i - 1) % 10 == 0) {
				if (i == 1) {
					int[] homeJ = { 8, 9, 8, 9 };
					int[] homeI = { 1, 1, 2, 2 };
					redHome = setupTheHome(homeI, homeJ, theTrack);
				} else if (i == 11) {
					int[] homeJ = { 1, 1, 2, 2 };
					int[] homeI = { 2, 1, 2, 1 };
					blueHome = setupTheHome(homeI, homeJ, theTrack);
				} else if (i == 21) {
					int[] homeJ = { 2, 1, 2, 1 };
					int[] homeI = { 9, 9, 8, 8 };
					yellowHome = setupTheHome(homeI, homeJ, theTrack);
				} else if (i == 31) {
					int[] homeJ = { 9, 9, 8, 8 };
					int[] homeI = { 8, 9, 8, 9 };
					greenHome = setupTheHome(homeI, homeJ, theTrack);
				}
			}
			if (lastField != null) {
				lastField.setNextField(theTrack);
			}
			lastField = theTrack;
		}
		lastField.setNextField(firstField);
	}

	
	private void setupTheGoals(final ArrayList<GoalField> theGoal,
			final int[] gridI, final int[] gridJ, final BasicField linker) {
		GoalField lastField = null;
		GoalField currentField = null;
		for (int i = 3; i >= 0; i--) {
			currentField = new GoalField(THEGRID[gridI[i]][gridJ[i]]);
			theGoal.add(currentField);
			if (lastField != null) {
				currentField.setNextGoalField(lastField);
			}
			lastField = currentField;
		}
		linker.setGoalField(currentField);
	}

	
	private HomeField setupTheHome(final int[] gridI, final int[] gridJ,
			final BasicField entry) {
		final ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < gridI.length; i++) {
			points.add(THEGRID[gridI[i]][gridJ[i]]);
		}
		HomeField hf = new HomeField(points);
		hf.setNextField(entry);
		return hf;
	}

	
	private void setupThePlayers() {

		ArrayList<ArrayList<GoalField>> goalFields = new ArrayList<ArrayList<GoalField>>(
				Arrays.asList(redGoal, blueGoal, yellowGoal, greenGoal));

		ArrayList<ArrayList<Pawn>> pawns = new ArrayList<ArrayList<Pawn>>(
				Arrays.asList(redPawns, bluePawns, yellowPawns, greenPawns));

		HomeField[] homeFields = { redHome, blueHome, yellowHome, greenHome };

		JLabel[] playerLabels = { redLabel, blueLabel, yellowLabel, greenLabel };

		String[] names = { "Red", "Blue", "Yellow", "Green" };

		String[] strategies = {  "Computer", 
				 "Human Player" };
		@SuppressWarnings("rawtypes")
		JComboBox[] choices = { new JComboBox<String>(strategies),
				new JComboBox<String>(strategies),
				new JComboBox<String>(strategies),
				new JComboBox<String>(strategies) };

		JPanel prompt = new JPanel();
		for (int i = 0; i < 4; i++) {
			prompt.add(new JLabel(names[i]));
			prompt.add(choices[i]);
		}
		int result = JOptionPane.showConfirmDialog(null, prompt,
				"Please designate the players", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			for (int i = 0; i < 4; i++) {
				Strategy someStrategy;
				switch (choices[i].getSelectedItem().toString()) {
				case "Computer":
					someStrategy = new Computer();
					break;
				case "Human Player":
				default:
					someStrategy = new HumanStrategy();
					break;
				}
				players.add(new Player(someStrategy, goalFields.get(i),
						homeFields[i], playerLabels[i], pawns.get(i), this));
			}
		} else {
			System.exit(1);
		}
	}

	
	private void addPawns(final ImageIcon imgSrc,
			final ArrayList<Pawn> pawnList, final HomeField home) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {

                                JButton jl = new JButton(imgSrc);
				jl.setBorderPainted(false);
				jl.setContentAreaFilled(false);
				boardPane.add(jl, new Integer(1));
				Dimension size = new Dimension(jl.getIcon().getIconWidth(), jl
						.getIcon().getIconHeight());
				jl.setBounds(0, 0, size.width, size.height);
				Pawn p = new Pawn(jl, home);
				pawnList.add(p);
			}
		}
	}

	
	private ImageIcon createImageIcon(final String src) {
		try {
			BufferedImage bluePawn = ImageIO.read(new File(src));
			ImageIcon icon = new ImageIcon(bluePawn);
			return icon;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	
	private static void createGUI() {
		JFrame frame = new JFrame("Ludo Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		LudoGame contentPane = new LudoGame();
		contentPane.setOpaque(true);
		contentPane.setBackground(new Color(188, 189, 194));
		frame.setContentPane(contentPane);

		frame.pack();
		frame.setVisible(true);

		contentPane.startTheGame();
	}

	
	public static void main(String[] args) {
		createGUI();
	}
}
