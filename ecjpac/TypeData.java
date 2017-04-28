package ecjpac ;
import ec.gp.* ;

public class TypeData extends GPData
{
	public double ret ; // return value, could be 1.0 or 0.0
	public int direction ; // for directional data types
	public void copyTo(final GPData gpd)
	{
		// data transfer
		((TypeData)gpd).ret = ret ;		
		((TypeData)gpd).direction = direction ;
	}
}
