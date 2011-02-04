
public class Utils {
	
	/**
	 * @param q value to take logarithm of
	 * @param base base of logarithm
	 * @return log(base = base) of (q)
	 */
	public static double log(double q, int base){
		return Math.log10(q) / Math.log10(base);
	}
}
