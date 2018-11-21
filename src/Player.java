
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public class Player {
	private final Strategy strategy;
	private final ArrayList<GoalField> goalField;
	private final HomeField homeField;
	private final JLabel playLabel;
	private final LudoGame parent;
	// Should be a set of 4.
	private final ArrayList<Pawn> pawns;

	
	public Player(final Strategy strategy,
			final ArrayList<GoalField> goalField, final HomeField homeField,
			final JLabel playLabel, final ArrayList<Pawn> pawns,
			final LudoGame parent) {
		this.strategy = strategy;
		this.goalField = goalField;
		this.homeField = homeField;
		this.playLabel = playLabel;
		this.playLabel.setFont(new Font("Sans-Serif", Font.BOLD, 26));
		this.pawns = pawns;
		this.parent = parent;
	}

	
	public final void doMove(final int dieRoll) {
		if (dieRoll == 6 && homeField.getPawnCount() > 0
				&& checkMovePawnHome() != null) {
			takeMove(new Move(homeField.getPawn(), homeField.getNextField()));
		} else {
			strategy.chooseMove(this, dieRoll);
		}
	}

	public void takeMove(final Move move) {
		if (move != null) {
			Pawn p = move.getPawn();
			Field f = move.getField();

			while (move.getField() != f) {
				if (f.getClass() == BasicField.class) {
					if (((BasicField) f).hasGoalField()) {
						if (((BasicField) f).getGoalField() == goalField.get(3)) {
							f = ((BasicField) f).getGoalField();
						} else {
							f = f.getNextField();
						}
					} else {
						f = f.getNextField();
					}
				} else {
					f = f.getNextField();
				}
			}
			p.moveToField(f);
		}
		SwingUtilities.invokeLater(parent.continueAfterThreadEnd);
	}

        
	public final HomeField getHomeField() {
		return homeField;
	}

	
	public final GoalField getEntryGoalField() {
		return goalField.get(goalField.size() - 1);
	}

	
	public final ArrayList<Pawn> getPawns() {
		return pawns;
	}

	
	public Field checkMovePawnHome() {
		if (checkValidMove(homeField.getNextField())) {
			return homeField.getNextField();
		} else {
			return null;
		}
	}


	public Field checkMovePawnBasic(final Pawn pawn, final BasicField field,
			final int distance) {
		if (distance == 0 && checkValidMove(field)) {
			return field;
		} else if (distance == 0 && !checkValidMove(field)) {
			return null;
		} else {
			if (field.hasGoalField()) {
				if (field.getGoalField() == goalField.get(3)) {
					return checkMovePawnGoal(pawn, goalField.get(3),
							distance - 1);
				} else {
					System.out
							.println("Noticed a goal field ... failed to be interested");
					return checkMovePawnBasic(pawn,
							(BasicField) field.getNextField(), distance - 1);
				}
			} else {
				return checkMovePawnBasic(pawn,
						(BasicField) field.getNextField(), distance - 1);
			}
		}
	}


	public Field checkMovePawnGoal(final Pawn pawn, final GoalField goal,
			final int distance) {
		if (distance == 0) {
			if (checkValidMove(goal)) {
				return goal;
			} else {
				return null;
			}
		} else if (!goal.hasNextField()) {
			return null;
		} else {
			return checkMovePawnGoal(pawn, (GoalField) goal.getNextField(),
					distance - 1);
		}
	}

	
	public boolean checkIfGoalFull() {
		boolean isFull = true;
		for (GoalField g : goalField) {
			isFull &= g.hasPawn();
		}
		return isFull;
	}

	
	public boolean checkIfGoalOccupied() {
		boolean hasPawn = false;
		for (GoalField g : goalField) {
			hasPawn |= g.hasPawn();
		}
		return hasPawn;
	}

	
	public int getGoalOccupiedCount() {
		int numPawns = 0;
		for (GoalField g : goalField) {
			if (g.hasPawn()) {
				numPawns++;
			}
		}
		return numPawns;
	}

	
	public void setLabelNotTurn() {
		playLabel.setForeground(new Color(0x000000));
	}

	
	public void setLabelIsTurn() {
		playLabel.setForeground(new Color(0x128282));
	}

	
	public boolean checkValidMove(final Field field) {
		if (field.hasPawn()) {
			if (isOwnPawn(field.getPawn())) {
				System.out
						.println("Invalid move considered, own pawn at field location");
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	
	private boolean isOwnPawn(final Pawn foundPawn) {
		boolean ownPawn = false;
		for (Pawn p : pawns) {
			ownPawn |= (p == foundPawn);
		}
		return ownPawn;
	}
}
