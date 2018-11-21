
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class HumanStrategy implements Strategy, MouseListener {

	private Player thePlayer = null;
	private int theRoll = 0;
	private ArrayList<Pawn> validPawns;
	private ArrayList<Move> validMoves;

	
	@Override
	public void chooseMove(final Player player, final int dieRoll) {
		validPawns = new ArrayList<Pawn>();
		validMoves = new ArrayList<Move>();
		this.thePlayer = player;
		this.theRoll = dieRoll;

                for (Pawn p : thePlayer.getPawns()) {
			p.getImgSrc().addMouseListener(this);
			if (p.isAtHome()
					&& thePlayer.checkValidMove(thePlayer.getHomeField()
							.getNextField()) && theRoll == 6) {
				validPawns.add(p);
				validMoves.add(new Move(thePlayer.getHomeField().peekAtPawn(),
						thePlayer.getHomeField().getNextField()));
			} else if (p.isAtGoal()) {
				Field f = thePlayer.checkMovePawnGoal(p,
						(GoalField) p.getField(), theRoll);
				if (f != null) {
					validPawns.add(p);
					validMoves.add(new Move(p, f));
				}
			} else if (p.isAtBasic()) {
				Field f = thePlayer.checkMovePawnBasic(p,
						(BasicField) p.getField(), dieRoll);
				if (f != null) {
					validPawns.add(p);
					validMoves.add(new Move(p, f));
				}
			}
		}
		System.out.println("Valid human pawns? " + validPawns.size());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			System.out.println("Process human move attempt...");
			Pawn thePawn = null;
			Object clickSource = e.getSource();
			for (Pawn p : thePlayer.getPawns()) {
				if (p.getImgSrc() == clickSource) {
					thePawn = p;
					break;
				}
			}

			for (Pawn p : validPawns) {
				if (thePawn == p) {
					if (thePawn.isAtHome()) {
						thePlayer.getHomeField().getPawn();
					}
					sendMoveToPlayer(thePlayer,
							validMoves.get(validPawns.indexOf(p)));
					for (Pawn q : thePlayer.getPawns()) {
						while (q.getImgSrc().getMouseListeners().length > 0) {
							q.getImgSrc().removeMouseListener(
									q.getImgSrc().getMouseListeners()[0]);
						}
					}
					break;
				}
			}

			if (validMoves.size() < 1) {
				System.err.println("No valid moves exist!");
				sendMoveToPlayer(thePlayer, null);
				for (Pawn q : thePlayer.getPawns()) {
					while (q.getImgSrc().getMouseListeners().length > 0) {
						q.getImgSrc().removeMouseListener(
								q.getImgSrc().getMouseListeners()[0]);
					}
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
        
	@Override
	public void sendMoveToPlayer(final Player player, final Move move) {
		player.takeMove(move);
	}
}
