package me.BlazingBroGamer.StandShowcase;

public enum StandType {
	
	COMMAND,SLIDES;
	
	public static StandType matchType(String s){
		if(s == null)
			return null;
		return StandType.valueOf(s.toUpperCase().replaceAll(" ", "_"));
	}
	
}
