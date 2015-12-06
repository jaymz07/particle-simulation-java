import java.util.*;
import java.awt.*;

public class Plane extends Polygon
{
	public ArrayList<Point3D> points;

	public Plane(ArrayList<Point3D> pts)
	{
		super();
		id="Plane";
		color=new Color(0,0,255,127);
		points=pts;
		pan(new Point3D(0,0,0));
	}
	public Point getScreenPoint(Point3D point, Point p, double d)
	{
		Vect direc= new Vect(p,new Point(point.x,point.y));
		direc.multiply(d/(d+point.z));
		if(point.z>=0)
			return direc.getPointFrom(p);
		return new Point(0,0);
	}
	public void printPolygon(Graphics page, Point pt, double d)
	{
	    ArrayList<Point> filtered = new ArrayList<Point>();
	    for(Point3D p : points)
		if(p.z>0.01) {
		    filtered.add(getScreenPoint(p,pt,d));

		}
	    if(filtered.size()==0)
		return;
	    int [] outX = new int[filtered.size()];
	    int [] outY = new int[filtered.size()];
	    for(int i=0;i<filtered.size();i++)
	    {
		outX[i]=(int)(filtered.get(i).x);
		outY[i]=(int)(filtered.get(i).y);
	    }
	    page.setColor(color);
	    page.fillPolygon(outX,outY,outX.length);

	}
	public void printPolygon2(Graphics page, Point pt, double d)
	{
		int count=0;
		for(Point3D pnt : points)
		  if(pnt.z<=0)
		    count++;

		int [] outPointsX = new int[points.size()-count];
		int [] outPointsY = new int[points.size()-count];
		int cnt=0;
		for(int i=0;i<points.size();i++) {
			if(points.get(i).z>0) {
			  Point outP=getScreenPoint(points.get(i),pt,d);
			  outPointsX[i-cnt]=(int)(outP.x);
			  outPointsY[i-cnt]=(int)(outP.y);
			}
			else cnt++;
		}
		page.setColor(color);
		page.fillPolygon(outPointsX,outPointsY,points.size()-count);

	}
	public void rotate(Point3D p, double ang1, double ang2, double ang3)
	{
		for(Point3D point : points)
		{
			Vect xy,xz,yz;
			if(Math.abs(ang1)>THRESHOLD)
			{
			xy = new Vect(new Point(p.x,p.y),new Point(point.x,point.y));
			xy=new Vect(xy.getAngle()+ang1,xy.getMag());
			point.x=p.x+xy.getX();
			point.y=p.y+xy.getY();
			}
			if(Math.abs(ang2)>THRESHOLD)
			{
			xz = new Vect(new Point(p.x,p.z),new Point(point.x,point.z));
			xz=new Vect(xz.getAngle()+ang2,xz.getMag());
			point.x=p.x+xz.getX();
			point.z=p.z+xz.getY();
			}
			if(Math.abs(ang3)>THRESHOLD)
			{
			yz = new Vect(new Point(p.y,p.z),new Point(point.y,point.z));
			yz=new Vect(yz.getAngle()+ang3,yz.getMag());
			point.y=p.y+yz.getX();
			point.z=p.z+yz.getY();
			}
		}
	}
	public void rotateAroundAxis(Point3D p, Vect3D a, double ang)
	{
		for(Point3D pt : points)
			pt.rotateAroundAxis(p,a,ang);
	}
	public void pan(Point3D d)
	{
		for(Point3D pt : points)
		{
			pt.x+=d.x;
			pt.y+=d.y;
			pt.z+=d.z;
		}
	}
	public ArrayList<Point3D> getPoints()
	{
		return points;
	}
	public Point3D getCenter()
	{
		Point3D out=new Point3D(0,0,0);
		for(Point3D pt : points)
		{
			out.x+=pt.x;
			out.y+=pt.y;
			out.z+=pt.z;
		}
		out.x/=points.size();
		out.y/=points.size();
		out.z/=points.size();
		return out;
	}
	public void explode(double d, Point3D pt)
	{
		for(Point3D point : points) {
			Vect3D direc = new Vect3D(pt,point);
			direc.multiply(d);
			point=direc.getPointFrom(pt);
		}
	}
	public Vect3D getNormal()
	{
	    Vect3D v1=new Vect3D(points.get(0),points.get(1));
	    Vect3D v2=new Vect3D(points.get(0),points.get(2));
	    Vect3D out=v1.crossProduct(v2);
	    out.normalize();
	    return out;
	}

}