import java.awt.*;

public class Line3D
{
	public Point3D point1;
	public Point3D point2;
	public Color color=Color.BLUE;

	public Line3D(Point3D p1, Point3D p2)
	{
		point1=p1;
		point2=p2;
	}

	public Point getScreenPoint(Point3D point, Point p, double d)
	{
		Vect direc= new Vect(p,new Point(point.x,point.y));
		direc.multiply(d/(d+point.z));
		if(point.z>=0)
			return direc.getPointFrom(p);
		return new Point(0,0);
	}

	public void printLine(Graphics page, Point p, double d)
	{
		if(!(point1.z<=0||point2.z<=0))
		{
		  Point s1=getScreenPoint(point1,p,d);
		  Point s2=getScreenPoint(point2,p,d);

		  page.setColor(color);
		  page.drawLine((int)s1.x,(int)s1.y,(int)s2.x,(int)s2.y);
		}
	}
	public Point3D getMidpoint()
	{
	    return new Point3D((point1.x+point2.x)/2,(point1.y+point2.y)/2,(point1.z+point2.z)/2);
	}
}