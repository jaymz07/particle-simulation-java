import java.util.*;
import java.awt.*;

public class Label extends Polygon
{
	public String label="";
	public Point3D point;

	public Label(Point3D p, String l)
	{
		super();
		id="Label";
		label=l;
		point=p;
		lines.add(new Line3D(point,point));
		pan(new Point3D(0,0,0));
	}

	public void printPolygon(Graphics page, Point pt, double d)
	{
		if(!label.equals(""))
		{
			Point scrPt=getScreenPoint(lines.get(0).point1,pt,d);
			page.setColor(color);
			String [] lines=label.split("\n");
			for(int i=0;i<lines.length;i++)
				page.drawString(lines[i],(int)scrPt.x,(int)scrPt.y-(lines.length-1)*15+i*15);
		}
	}

	public Point getScreenPoint(Point3D point, Point p, double d)
	{
		Vect direc= new Vect(p,new Point(point.x,point.y));
		direc.multiply(d/(d+point.z));
		if(point.z>=0)
			return direc.getPointFrom(p);
		return new Point(0,0);
	}


}