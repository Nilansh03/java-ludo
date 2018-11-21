import java.awt.Point;


public class Field {

	protected Field nextField;
	protected Pawn occupyingPawn;
	protected Point thePoint;

	
	public Field(final Point point) {
		setNextField(null);
		setPawn(null);
		setPoint(point);
	}

	
	public boolean hasNextField() {
		return (getNextField() != null ? true : false);
	}

		public Field getNextField() {
		return nextField;
	}

	
	protected void setNextField(final Field nextField) {
		this.nextField = nextField;
	}

	
	public boolean hasPawn() {
		return (getPawn() != null ? true : false);
	}

	public Pawn getPawn() {
		return occupyingPawn;
	}


	protected void setPawn(final Pawn pawn) {
		this.occupyingPawn = pawn;
	}

	
	public Point getPoint() {
		return thePoint;
	}

	
	protected void setPoint(final Point point) {
		this.thePoint = point;
	}
}
