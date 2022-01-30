package slimeattack07.naval_warfare.util.helpers;

import slimeattack07.naval_warfare.util.HitResult;

public class TargetResultHelper {
	public int id;
	public HitResult result;
	
	public TargetResultHelper(int board_id, HitResult hit_result) {
		id = board_id;
		result = hit_result;
	}
}
