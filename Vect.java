import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;

public class Vect
{
	private double slope;
	private double angle;
	private double mag;
	public double x;
	public double y;

	public Vect(double a, double b, double c, double d)
	{
		slope=((double)(d-b))/((double)(c-a));
		angle=Math.PI+Math.atan2(d-b,c-a);
		mag=Math.sqrt(Math.pow(c-a,2)+Math.pow(d-b,2));
		x=Math.cos(angle)*mag;
		y=Math.sin(angle)*mag;
	}

	public Vect(double a, double b)
	{
		angle=a;
		mag=b;
		x=Math.cos(angle)*mag;
		y=Math.sin(angle)*mag;
	}

	public Vect(Point a, Point b)
	{
		x=b.x-a.x;
		y=b.y-a.y;
		mag=Math.sqrt(x*x+y*y);
		angle=Math.atan2(y,x);
		slope=y/x;
	}

	public Vect(Vect in)
	{
		x=in.x;
		y=in.y;
	}
	public String toString()
	{
		return "{"+x+","+y+"}";
	}
	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getAngle()
	{
		angle=Math.atan2(y,x);
		return angle;
	}

	public double getMag()
	{
		mag=Math.sqrt(x*x+y*y);
		return mag;
	}

	public void setX(double a)
	{
		x=a;
		slope=y/x;
		angle=Math.atan(slope);
		if((slope>0&&y>0)||(slope<0&&y<0))
			angle=Math.PI-angle;
		mag=Math.sqrt(x*x+y*y);
	}

	public void setY(double a)
	{
		y=a;
		slope=y/x;
		angle=Math.atan(slope);
		if((slope>0&&y>0)||(slope<0&&y<0))
			angle=Math.PI-angle;
		mag=Math.sqrt(x*x+y*y);
	}

	public void setMag(double m)
	{
		double r=m/mag;
		x=x*r;
		y=y*r;
		mag=m;
	}

	public void add(Vect v)
	{
		x=x+v.getX();
		y=y+v.getY();
	/*	mag=Math.sqrt(x*x+y*y);
		slope=x/y;
		angle=Math.atan(slope);
		if((slope>0&&y<0)||(slope<0&&y>0))
		angle=Math.PI-angle;
		mag=Math.sqrt(x*x+y*y);*/
	}

	public void subtract(Vect v)
	{
		x-=v.x;
		y-=v.y;
	}

	public Vect plus(Vect a)
	{
		Vect out=this.cloneVect();
		out.add(a);
		return out;
	}

	public void multiply(double n)
	{
		y=y*n;
		x=x*n;
		mag=Math.sqrt(x*x+y*y);
		angle=Math.atan(slope);
		if((slope>0&&y<0)||(slope<0&&y>0))
		angle=Math.PI-angle;

	}

	public double getSlope()
	{
		return slope;
	}

	public void reverse()
	{
		x=-x;
		y=-y;
	}

	public double dotProduct(Vect v)
	{
		return x*v.x+y*v.y;
	}

	public Vect cloneVect()
	{
		return new Vect(angle,mag);
	}

	public Vect multipliedBy(double a)
	{
		Vect out = this.cloneVect();
		out.multiply(a);
		return out;
	}

	public Point getPointFrom(Point p)
	{
		return new Point(p.x+x,p.y+y);
	}
	public void normalize()
	{
		mag=Math.sqrt(x*x+y*y);
		x/=mag;
		y/=mag;
	}
	public void rotateVect(double ang)
	{
		double s=Math.sin(ang),c=Math.cos(ang);
		double [][] mat = {{c,-s},{s,c}};
		Matrix m1=new Matrix(mat),m2=new Matrix(this),out=m1.multiply(m2);
		x=out.data[0][0];
		y=out.data[1][0];
	//	System.out.println(m1+"\n\n"+m2+"\n\n"+out+"\n\n------------------");
	}
	public void rotateVect(Matrix rot)
	{
		Matrix out=rot.multiply(new Matrix(this));
		x=out.data[0][0];
		y=out.data[1][0];
	}
}