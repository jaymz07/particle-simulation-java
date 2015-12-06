import java.util.*;
import java.io.*;
import java.applet.*;
import java.awt.*;

public class Graph
{
	public int WIDTH=1000;
	public int HEIGHT=600;
	public DataSet points;
	public DataSet sPoints;
	public Color color=Color.RED;
	public int pSize=1;

	Graphics page;

	public Graph(Graphics b, DataSet a)
	{
		points=a;
		page=b;
		double ppuY=(WIDTH)/(points.rangeX());
		double ppuX=(HEIGHT)/(points.rangeY());

		ArrayList<Point> c = new ArrayList<Point>();

		for(Point p : points.getData())
		{
			c.add(new Point(((p.getX()-points.minX()))*ppuY,HEIGHT-(p.getY()-points.minY())*ppuX));
		}

		sPoints= new DataSet(c);

		color=points.getData().get(0).getColor();
	}

	public Graph(Graphics b, DataSet a, int c)
	{
		this(b,a);
		pSize=c;
	}
	public Graph(Graphics b, DataSet a, int w, int h)
	{
		WIDTH=w;
		HEIGHT=h;
		points=a;
		page=b;
		double ppuY=(WIDTH)/(points.rangeX());
		double ppuX=(HEIGHT)/(points.rangeY());

		ArrayList<Point> c = new ArrayList<Point>();

		for(Point p : points.getData())
		{
			c.add(new Point(((p.getX()-points.minX()))*ppuY,HEIGHT-(p.getY()-points.minY())*ppuX));
		}

		sPoints= new DataSet(c);

		color=points.getData().get(0).getColor();
	}

	public void printGraph()
	{
		page.setColor(Color.WHITE);
	//	page.fillRect(0,0,WIDTH,HEIGHT);
		page.setColor(color);
		int x=(int)sPoints.getData().get(0).getX(), y=(int)sPoints.getData().get(0).getY();

		for(Point p : sPoints.getData())
		{

			page.fillRect((int)p.getX(),(int)p.getY(),pSize,pSize);
			page.drawLine(x,y,(int)p.getX(),(int)p.getY());
			x=(int)p.getX();
			y=(int)p.getY();
		}

		page.setColor(Color.BLACK);

		if(points.minX()<0&&points.maxX()>0)
		{
			Point a = scrPoint(new Point(0,points.maxY()));
			Point b = scrPoint(new Point(0,points.minY()));
			page.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
		}

		if(points.minY()<0&&points.maxY()>0)
		{
			Point a = scrPoint(new Point(points.maxX(),0));
			Point b = scrPoint(new Point(points.minX(),0));
			page.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
		}

	}

	public void printGraph(Point p, int w, int h)
	{
		WIDTH=w;
		HEIGHT=h;
		Graph g = new Graph(page,points,w,h);
		sPoints=g.sPoints;
		for(Point pt : sPoints.getData())
		{
			pt.y+=p.y;
			pt.x+=p.x;
		}

		page.setColor(Color.WHITE);
	//	page.fillRect(0,0,WIDTH,HEIGHT);
		page.setColor(color);
		int x=(int)sPoints.getData().get(0).getX(), y=(int)sPoints.getData().get(0).getY();

		for(Point pt : sPoints.getData())
		{

			page.fillRect((int)pt.getX(),(int)pt.getY(),pSize,pSize);
			page.drawLine(x,y,(int)pt.getX(),(int)pt.getY());
			x=(int)pt.getX();
			y=(int)pt.getY();
		}

		page.setColor(Color.BLACK);

		if(points.minX()<0&&points.maxX()>0)
		{
			Point a = scrPoint(new Point(0,points.maxY()));
			Point b = scrPoint(new Point(0,points.minY()));
			page.drawLine((int)(a.getX()+p.x),(int)(a.getY()+p.y),(int)(b.getX()+p.x),(int)(b.getY()+p.y));
		}

		if(points.minY()<0&&points.maxY()>0)
		{
			Point a = scrPoint(new Point(points.maxX(),0));
			Point b = scrPoint(new Point(points.minX(),0));
			page.drawLine((int)(a.getX()+p.x),(int)(a.getY()+p.y),(int)(b.getX()+p.x),(int)(b.getY()+p.y));
		}

	}

	public Point scrPoint(Point p)
	{
		double ppuY=(WIDTH)/(points.rangeX());
		double ppuX=(HEIGHT)/(points.rangeY());
		return new Point(((p.getX()-points.minX()))*ppuY,HEIGHT-(p.getY()-points.minY())*ppuX);
	}
}