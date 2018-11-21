import java.util.ArrayList;


public class Computer implements Strategy {

	private HomeField theHome;
	private ArrayList<Pawn> thePawns;

	
	@Override
	public void chooseMove(final Player player, final int dieRoll) {
		theHome = player.getHomeField();
		thePawns = player.getPawns();

		Pawn frontMostValid = null;
		ArrayList<Pawn> basicFieldPawns = new ArrayList<Pawn>();

		Field field = theHome.getNextField();
		do {
			if (field.hasPawn()) {
				if (thePawns.contains(field.getPawn())) {
					basicFieldPawns.add(0, field.getPawn());
				}
			}
			field = field.getNextField();
		} while (field != theHome.getNextField());

		ArrayList<Move> rejects = new ArrayList<Move>();
		Move chosen = null;
		for (Pawn p : basicFieldPawns) {
			Field f = player.checkMovePawnBasic(p, (BasicField) p.getField(),
					dieRoll);
			if (f != null) {
				if (frontMostValid == null) {
					frontMostValid = p;
					if (player.checkMovePawnBasic(frontMostValid,
							(BasicField) frontMostValid.getField(), dieRoll)
							.getClass() == GoalField.class) {
						sendMoveToPlayer(player, new Move(frontMostValid, f));
						return;
					}
				}

				Field nextField = p.getField();
				for (int i = 0; i < dieRoll; i++) {
					
					if (nextField.getNextField().getPawn() == null
							&& nextField.getNextField().equals(f)) {
						chosen = new Move(p, f);
						break;
					}
					else if (nextField.getNextField().getPawn() == null) {
						nextField = nextField.getNextField();
					}
					
					else {
						nextField = nextField.getNextField();
						rejects.add(new Move(p, f));
						continue;
					}
				}
				if (chosen != null) {
					sendMoveToPlayer(player, chosen);
					return;
				}

			}
		}

		for (Pawn p : thePawns) {
			if (p.isAtGoal()) {
				Field f = player.checkMovePawnGoal(p, (GoalField) p.getField(),
						dieRoll);
				if (f != null) {
					sendMoveToPlayer(player, new Move(p, f));
					return;
				}
			}
		}

		if (rejects.size() != 0) {
			sendMoveToPlayer(player, rejects.get(0));
			return;
		}

		sendMoveToPlayer(player, null);
	}

	
	@Override
	public void sendMoveToPlayer(final Player player, final Move move) {
		player.takeMove(move);
	}

}
