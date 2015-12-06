import java.util.*;
import java.awt.*;

public class Pyramid extends Polygon
{
	public int numSides;
	public Pyramid(Point3D p, int n, double r, double h)
	{
		super();
		id="Pyramid";
		numSides=n;
		ArrayList<Point3D> points1 = new ArrayList<Point3D>();
		ArrayList<Point3D> points2 = new ArrayList<Point3D>();
		Vect direc = new Vect(Math.PI/4,r);
		for(int i=0;i<n;i++)
		{
			Point pt=direc.getPointFrom(new Point(p.x,p.y));
			points1.add(new Point3D(pt.x,pt.y,p.z));
			direc=new Vect(direc.getAngle()+Math.PI*2/n,r);
		}
		for(Point3D pt : points1)
			lines.add(new Line3D(new Point3D(p.x,p.y,p.z+h),pt));

		for(int i=1;i<points1.size();i++)
			lines.add(new Line3D(points1.get(i-1),points1.get(i)));

		lines.add(new Line3D(points1.get(0),points1.get(points1.size()-1)));
		pan(new Point3D(0,0,0));

	}

	public void drawFilled(Graphics page, Point pt, double d)
	{
	    ArrayList<Plane> planes = getPlanes();
	    for(Plane plane : planes)
	      plane.printPolygon(page,pt,d);
	}

	public ArrayList<Plane> getPlanes()
	{
	    Plane plane;
	    ArrayList<Plane> out=new ArrayList<Plane>();
	    ArrayList<Point3D> add;
	    for(int i=0;i<numSides-1;i++)
	    {
		add = new ArrayList<Point3D>();
		add.add(lines.get(i).point1);
		add.add(lines.get(i).point2);
		add.add(lines.get(i+1).point2);
		plane= new Plane(add);
		plane.color=color;
		out.add(plane);
	    }
	    add= new ArrayList<Point3D>();
	    add.add(lines.get(0).point1);
	    add.add(lines.get(0).point2);
	    add.add(lines.get(numSides-1).point2);
	    plane= new Plane(add);
	    plane.color=color;
	    out.add(plane);

	    add= new ArrayList<Point3D>();
	    for(int i=0;i<numSides;i++)
	    {
	      add.add(lines.get(i).point2);
	    }
	    plane= new Plane(add);
	    plane.color=color;
	    out.add(plane);
	    return out;
	}


}