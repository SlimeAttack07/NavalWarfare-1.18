package slimeattack07.naval_warfare.util;

public enum BattleLogAction {
	DROP_BLOCK,
	BOARDSTATE,
	SHIPSTATE,
	DELAY,
	DROP_BLOCKS,
	PLAY_SOUND,
	PLAY_SOUNDS,
	SUMMON_DEPLOYABLE,
	SET_BLOCK,
	SET_DIS_BLOCK,
	SET_BLOCKS,
	MESSAGE;

	public boolean updateViewerState() {
		boolean skip = equals(DELAY) || equals(PLAY_SOUND) || equals(PLAY_SOUNDS) || equals(MESSAGE);
		
		return !skip;
	}
}