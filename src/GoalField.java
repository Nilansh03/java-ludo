
import java.awt.Point;


public class GoalField extends Field {

	
	public GoalField(final Point point) {
		super(point);
	}

	public final void setNextGoalField(final GoalField goalField) {
		setNextField(goalField);
	}
}
