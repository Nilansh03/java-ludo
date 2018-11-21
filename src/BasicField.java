
import java.awt.Point;

public class BasicField extends Field {

	private GoalField goalLink;

	public BasicField(final Point point) {
		super(point);
		setGoalField(null);
	}

	
	public Pawn removePawn() {
		Pawn p = getPawn();
		setPawn(null);
		return p;
	}

	
	public final boolean hasGoalField() {
		return (getGoalField() != null ? true : false);
	}

	
	public final GoalField getGoalField() {
		return goalLink;
	}

	
	public final void setGoalField(final GoalField goal) {
		this.goalLink = goal;
	}
}
