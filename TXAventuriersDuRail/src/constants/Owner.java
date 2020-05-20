package constants;

public class Owner {
	public static final int ME = 1;
	public static final int OTHER = -1;
	public static final int NEUTRAL = 0;

	public static final int OTHER_VALUE = 10000;

	public static final String MY_ROADS = "Mes routes";
	public static final String OTHER_ROADS = "Routes des autres";

	public static int getRelatedOwner(String roadOwner) {
		switch (roadOwner) {
		case MY_ROADS:
			return ME;
		case OTHER_ROADS:
			return OTHER;
		default:
			return NEUTRAL;
		}
	}

}
