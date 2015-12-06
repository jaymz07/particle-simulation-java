import java.util.*;
import java.awt.*;

public class PointP extends Polygon
{
	public int width;
	public Point3D point;
	public PointP(Point3D p)
	{
		super();
		lines.add(new Line3D(p,p));
		point=p;
	}

	public void rotate(Point3D p, double ang1, double ang2, double ang3)
	{
		super.rotate(p,ang1,ang2,ang3);
		point=lines.get(0).point1;
	}

	public Point getScreenPoint(Point p, double d)
	{
		Vect direc= new Vect(p,new Point(point.x,point.y));
		direc.multiply(d/(d+point.z));
		if(point.z>=0)
			return direc.getPointFrom(p);
		return new Point(0,0);
	}

}