import java.util.*;
import java.awt.*;

public class Cube extends Polygon
{
	public double width;
	public Cube(double x, double y, double z, double w)
	{
		super();
		id="Cube";
		width=w;
		ArrayList<Point3D> pts = new ArrayList<Point3D>();
		pts.add(new Point3D(x,y,z));
		pts.add(new Point3D(x+w,y,z));
		pts.add(new Point3D(x+w,y+w,z));
		pts.add(new Point3D(x,y+w,z));
		for(int i=0;i<pts.size()-1;i++)
		{
			lines.add(new Line3D(pts.get(i), pts.get(i+1)));
		}
		lines.add(new Line3D(pts.get(0), new Point3D(x,y+w,z)));
		int size=lines.size();
		for(int i=0;i<size;i++)
		{
			lines.add(new Line3D(new Point3D(lines.get(i).point1.x,lines.get(i).point1.y,lines.get(i).point1.z+w),new Point3D(lines.get(i).point2.x,lines.get(i).point2.y,lines.get(i).point2.z+w)));
		}
		size=pts.size();
		for(int i=0;i<size;i++)
		{
			pts.add(new Point3D(pts.get(i).x,pts.get(i).y,pts.get(i).z+w));
		}
		for(int i=0;i<4;i++)
		{
			lines.add(new Line3D(pts.get(i),pts.get(i+4)));
		}
		pan(new Point3D(0,0,0));

	}

	public Cube(Point3D p, double w)
	{
		this(p.x,p.y,p.z,w);
	}
	public void drawWireFramed(Graphics page, Point pt, double d)
	{
	    drawFilled(page,pt,d);
	    Color bak=color;
	    color=Color.BLACK;
	    printPolygon(page,pt,d);
	    color=bak;
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
		ArrayList<Plane> out = new ArrayList<Plane>();
		ArrayList<Point3D> add = new ArrayList<Point3D>();
		add.add(lines.get(0).point1);
		add.add(lines.get(1).point1);
		add.add(lines.get(2).point1);
		add.add(lines.get(3).point2);
		plane=new Plane(add);
		plane.color=color;
		out.add(plane);

		add=new ArrayList<Point3D>();
		add.add(lines.get(4).point1);
		add.add(lines.get(5).point1);
		add.add(lines.get(6).point1);
		add.add(lines.get(7).point2);
		plane=new Plane(add);
		plane.color=color;
		out.add(plane);

		add=new ArrayList<Point3D>();
		add.add(lines.get(0).point1);
		add.add(lines.get(0).point2);
		add.add(lines.get(4).point2);
		add.add(lines.get(4).point1);
		plane=new Plane(add);
		plane.color=color;
		out.add(plane);

		add=new ArrayList<Point3D>();
		add.add(lines.get(1).point1);
		add.add(lines.get(1).point2);
		add.add(lines.get(5).point2);
		add.add(lines.get(5).point1);
		plane=new Plane(add);
		plane.color=color;
		out.add(plane);

		add=new ArrayList<Point3D>();
		add.add(lines.get(2).point1);
		add.add(lines.get(2).point2);
		add.add(lines.get(6).point2);
		add.add(lines.get(6).point1);
		plane=new Plane(add);
		plane.color=color;
		out.add(plane);

		add=new ArrayList<Point3D>();
		add.add(lines.get(3).point1);
		add.add(lines.get(3).point2);
		add.add(lines.get(7).point2);
		add.add(lines.get(7).point1);
		plane=new Plane(add);
		plane.color=color;
		out.add(plane);

		return out;
	}


}