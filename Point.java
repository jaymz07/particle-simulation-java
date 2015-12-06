import java.awt.*;

public class Point
{
	public double x;
	public double y;
	public Color color;

	public Point(double a, double b)
	{
		x=a;
		y=b;
		color=Color.RED;
	}

	public Point(double a, double b, Color c)
	{
		this(a,b);
		color=c;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public void setX(double a)
	{
		x=a;
	}

	public void setY(double a)
	{
		y=a;
	}

	public Color getColor()
	{
		return color;
	}
	public double getDistTo(Point pt)
	{
		return Math.sqrt(Math.pow(pt.x-x,2)+Math.pow(pt.y-y,2));
	}
}