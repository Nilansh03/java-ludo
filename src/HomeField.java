
import java.awt.Point;
import java.util.ArrayList;


public class HomeField extends Field {

	private final ArrayList<Pawn> homePawns = new ArrayList<Pawn>();
	private final ArrayList<Point> thePoints = new ArrayList<Point>();

	public HomeField(final ArrayList<Point> points) {
		super(points.get(0));
		for (Point p : points) {
			thePoints.add(p);
		}
	}

	
	@Override
	public final Pawn getPawn() {
		Pawn p = null;
		if (hasPawn()) {
			p = homePawns.remove(0);
		}
		return p;
	}

	public final Pawn peekAtPawn() {
		return homePawns.get(0);
	}

	@Override
	public final boolean hasPawn() {
		return (homePawns.size() > 0);
	}

	public final int getPawnCount() {
		return homePawns.size();
	}

	public final boolean isFull() {
		return (homePawns.size() == 4);
	}

	public final void setPawns(final ArrayList<Pawn> pawns) {
		for (Pawn p : pawns) {
			setPawn(p);
		}
	}

	@Override
	public final void setPawn(final Pawn pawn) {
		if (pawn != null) {
			homePawns.add(0, pawn);
		}
	}

	@Override
	public Point getPoint() {
		return getPoints().get(homePawns.size());
	}

	public ArrayList<Point> getPoints() {
		return thePoints;
	}
}
