import java.util.*;
import java.awt.*;

public class Polygon
{
	public ArrayList<Line3D> lines;
	public double THRESHOLD=Math.pow(10,-3);
	public Color color=Color.BLUE;
	public String id="Polygon";
	public Polygon()
	{
		lines=new ArrayList<Line3D>();
	}
	public Polygon(Polygon ply)
	{
		lines=new ArrayList<Line3D>();
		for(Line3D ln : ply.lines)
		  lines.add(new Line3D(new Point3D(ln.point1.x,ln.point1.y,ln.point1.z),new Point3D(ln.point2.x,ln.point2.y,ln.point2.z)));
	}

	public void printPolygon(Graphics page, Point pt, double d)
	{
		for(Line3D p : lines)
		{
			p.color=color;
			p.printLine(page,pt,d);
		}
	}

	public void pan(Point3D d)
	{
		for(int i=0;i<lines.size();i++)
			lines.set(i,new Line3D(new Point3D(lines.get(i).point1.x+d.x,lines.get(i).point1.y+d.y,lines.get(i).point1.z+d.z),new Point3D(lines.get(i).point2.x+d.x,lines.get(i).point2.y+d.y,lines.get(i).point2.z+d.z)));
	}

	public void rotate2(Point3D p, double ang1, double ang2, double ang3)
	{
		for(Line3D l : lines)
		{
			Vect xy,xz,yz;
			if(Math.abs(ang1)>THRESHOLD)
			{
			xy = new Vect(new Point(p.x,p.y),new Point(l.point1.x,l.point1.y));
			xy.rotateVect(ang1);
			l.point1.x=p.x+xy.getX();
			l.point1.y=p.y+xy.getY();
			}
			if(Math.abs(ang2)>THRESHOLD)
			{
			xz = new Vect(new Point(p.x,p.z),new Point(l.point1.x,l.point1.z));
			xz.rotateVect(ang2);
			l.point1.x=p.x+xz.getX();
			l.point1.z=p.z+xz.getY();
			}
			if(Math.abs(ang3)>THRESHOLD)
			{
			yz = new Vect(new Point(p.y,p.z),new Point(l.point1.y,l.point1.z));
			yz.rotateVect(ang3);
			l.point1.y=p.y+yz.getX();
			l.point1.z=p.z+yz.getY();
			}

			if(Math.abs(ang1)>THRESHOLD)
			{
			xy = new Vect(new Point(p.x,p.y),new Point(l.point2.x,l.point2.y));
			xy.rotateVect(ang1);
			l.point2.x=p.x+xy.getX();
			l.point2.y=p.y+xy.getY();
			}
			if(Math.abs(ang2)>THRESHOLD)
			{
			xz = new Vect(new Point(p.x,p.z),new Point(l.point2.x,l.point2.z));
			xz.rotateVect(ang2);
			l.point2.x=p.x+xz.getX();
			l.point2.z=p.z+xz.getY();
			}
			if(Math.abs(ang3)>THRESHOLD)
			{
			yz = new Vect(new Point(p.y,p.z),new Point(l.point2.y,l.point2.z));
			yz.rotateVect(ang3);
			l.point2.y=p.y+yz.getX();
			l.point2.z=p.z+yz.getY();
			}
		}
	}

	public void rotate(Point3D p, double ang1, double ang2, double ang3)
	{
		Matrix rotXY = new Matrix(ang1,'z'), rotXZ=new Matrix(-ang2,'y'), rotYZ=new Matrix(ang3,'x'), rot=rotYZ.multiply(rotXZ.multiply(rotXY)), cent= new Matrix(p);
		for(Line3D l : lines)
		{
			Matrix mat=new Matrix(new Vect3D(p,l.point1));
			l.point1=rot.multiply(mat).plus(cent).getPoint3D();
			mat=new Matrix(new Vect3D(p,l.point2));
			l.point2=rot.multiply(mat).plus(cent).getPoint3D();
		}
	}
	public void rotate(Point3D p, Matrix rot)
	{
		Matrix cent = new Matrix(p);
		for(Line3D l : lines)
		{
			Matrix mat=new Matrix(new Vect3D(p,l.point1));
			l.point1=rot.multiply(mat).plus(cent).getPoint3D();
			mat=new Matrix(new Vect3D(p,l.point2));
			l.point2=rot.multiply(mat).plus(cent).getPoint3D();
		}
	}

	public void rotateAroundAxis(Point3D pt, Vect3D a, double ang)
	{
		for(Line3D l : lines)
		{
			l.point1.rotateAroundAxis(pt,a,ang);
			l.point2.rotateAroundAxis(pt,a,ang);
		}
	}

	public ArrayList<Point3D> getPoints()
	{
		ArrayList<Point3D> points = new ArrayList<Point3D>();
		for(Line3D line : lines)
		{
			boolean add=true;
			for(Point3D point : points)
			{
				if(point.isSameAs(line.point1))
					add=false;
			}
			if(add)
				points.add(line.point1);
			add=true;
			for(Point3D point : points)
			{
				if(point.isSameAs(line.point2))
					add=false;
			}
			if(add)
				points.add(line.point2);
		}
		return points;
	}

	public Point3D getCenter()
	{
		ArrayList<Point3D> points=getPoints();
		double sumx=0,sumy=0,sumz=0;
		for(Point3D p : points)
		{
			sumx+=p.x;
			sumy+=p.y;
			sumz+=p.z;
		}
		sumx/=points.size();
		sumy/=points.size();
		sumz/=points.size();
		return new Point3D(sumx,sumy,sumz);
	}
	public void explode(double d, Point3D pt)
	{
		for(Line3D ln : lines)
		{
			Vect3D direc = new Vect3D(pt,ln.point1);
			direc.multiply(d);
			ln.point1=direc.getPointFrom(pt);
			direc = new Vect3D(pt,ln.point2);
			direc.multiply(d);
			ln.point2=direc.getPointFrom(pt);
		}
	}
}