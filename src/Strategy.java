
public interface Strategy {

	
	public void chooseMove(final Player player, final int dieRoll);

	
	public void sendMoveToPlayer(final Player player, final Move move);
}
