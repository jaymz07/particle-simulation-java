import java.util.*;
import java.awt.*;

public class LineP extends Polygon
{
	public Line3D line;
	public LineP(Point3D p1,Point3D p2)
	{
		super();
		id="LineP";
		color=Color.BLUE;
		line=new Line3D(p1,p2);
		lines.add(line);
	}
	public LineP(ArrayList<Line3D> lns)
	{
		lines=lns;
	}
	public LineP(ArrayList<Line3D> lns, Color c)
	{
		this(lns);
		color=c;
	}
	
	public LineP(Line3D ln)
	{
	    lines=new ArrayList<Line3D>();
	    line=ln;
	    lines.add(ln);
	}
	public LineP(Line3D ln, Color c)
	{
	  this(ln);
	  color=c;
	}

}