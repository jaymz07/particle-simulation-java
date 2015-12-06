import java.util.*;
import java.awt.*;

public class Arrow3D extends Polygon
{
	public int numSides;
	public int numLinesPrism;
	public Arrow3D(Point3D p, int r, int h, double ang1, double ang2, double ang3, int n)
	{
		this(p,r,h,n);
		rotate(p,ang1,ang2,ang3);
	}
	public Arrow3D(Point3D p, double r, double h, int n)
	{
		super();
		id="Arrow3D";
		numSides=n;
		Prism prism = new Prism(p,n,r,h);
		Pyramid pyramid = new Pyramid(new Point3D(p.x,p.y,p.z+h),n,r*3,r*5);
		for(Line3D line : prism.lines)
			lines.add(line);
		numLinesPrism=lines.size();
		for(Line3D line : pyramid.lines)
			lines.add(line);
		pan(new Point3D(0,0,0));
		rotate(p,0,-Math.PI/2,0);
	}

	public Arrow3D(Point3D p, double r, double h, Vect3D v, int n)
	{
		this(p,r,h,n);
		Vect3D direc = new Vect3D(new Point3D(1,0,0));
		Vect3D axis = v.crossProduct(direc);
		double foo=direc.dotProduct(v);
		double ang = Math.acos(foo/(v.getMag()));
		if(foo>0)
			ang=-ang;
		//System.out.println(axis+"\t"+ang);
		rotateAroundAxis(p,axis,ang);
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
	    ArrayList<Plane> out= new ArrayList<Plane>();
	    ArrayList<Point3D> add;
	    for(int i=0;i<numSides-1;i++)
	    {
		add = new ArrayList<Point3D>();
		add.add(lines.get(i).point1);
		add.add(lines.get(i).point2);
		add.add(lines.get(i+1).point2);
		add.add(lines.get(i+1).point1);
		plane= new Plane(add);
		plane.color=color;
		out.add(plane);
	    }
	    add= new ArrayList<Point3D>();
	    add.add(lines.get(0).point1);
	    add.add(lines.get(0).point2);
	    add.add(lines.get(numSides-1).point2);
	    add.add(lines.get(numSides-1).point1);
	    plane= new Plane(add);
	    plane.color=color;
	    out.add(plane);

	    add= new ArrayList<Point3D>();
	    for(int i=0;i<numSides;i++)
	    {
	      add.add(lines.get(i).point1);
	    }
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

	    for(int i=numLinesPrism;i<numSides-1+numLinesPrism;i++)
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
	    add.add(lines.get(numLinesPrism).point1);
	    add.add(lines.get(numLinesPrism).point2);
	    add.add(lines.get(numSides-1+numLinesPrism).point2);
	    plane= new Plane(add);
	    plane.color=color;
	    out.add(plane);

	    add= new ArrayList<Point3D>();
	    for(int i=numLinesPrism;i<numSides+numLinesPrism;i++)
	    {
	      add.add(lines.get(i).point2);
	    }
	    plane= new Plane(add);
	    plane.color=color;
	    out.add(plane);
	    return out;
	}



}