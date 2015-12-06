import java.util.*;
import java.awt.*;

public class RegularPolygon extends Polygon
{
	public Point3D center;

	public RegularPolygon(Point3D p, double r, int n)
	{
		super();
		center=p;
		ArrayList<Point3D> points1 = new ArrayList<Point3D>();
		ArrayList<Point3D> points2 = new ArrayList<Point3D>();
		Vect direc = new Vect(Math.PI/4,r);
		for(int i=0;i<n;i++)
		{
			Point pt=direc.getPointFrom(new Point(p.x,p.y));
			points1.add(new Point3D(pt.x,pt.y,p.z));
			direc=new Vect(direc.getAngle()+Math.PI*2/n,r);
		}

		for(int i=1;i<points1.size();i++)
			lines.add(new Line3D(points1.get(i-1),points1.get(i)));

		lines.add(new Line3D(points1.get(0),points1.get(points1.size()-1)));
		pan(new Point3D(0,0,0));

	}


}