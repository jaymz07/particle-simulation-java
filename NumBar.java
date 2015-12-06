import java.awt.*;

public class NumBar
{
	public int x;
	public int y;
	public int xB;
	public int yB;
	Graphics page;
	public double rangeF;
	public double rangeC;
	public double value;
	public Color color=Color.BLACK;
	public String label="Bar Thingy!";
	public int last=0;
	public int BORDER=4;
	public int ARROWWIDTH=12;

	public NumBar(Graphics p,int a,int b,int c, int d)
	{
		page=p;
		x=a;
		y=b;
		xB=c;
		yB=d;
	}
	public NumBar(Graphics p,int a,int b,int c, int d, double e, double f)//
	{
		this(p,a,b,c,d);
		rangeF=e;
		rangeC=f;
		value=(e+f)/2;
		last=getSliderX();
		color=calcColor(last);
	}

	public void printBar()
	{
		last=getSliderX();
		color=calcColor(last);
		page.setColor(color);
		page.fillRect(x,y,xB,yB);
		page.setColor(new Color(255,255,255));
		page.fillRect(x+BORDER,y+BORDER,xB-2*BORDER,yB-2*BORDER);
		page.setColor(Color.BLUE);
		int [] xPoints={last,last-ARROWWIDTH/2,last+ARROWWIDTH/2};
		int [] yPoints={y,y+yB,y+yB};
		page.fillPolygon(xPoints,yPoints,3);
		page.setColor(Color.BLACK);
		page.drawString(label,x+5+BORDER,y+5+yB/2);
	}

	public boolean containsPoint(int a,int b)
	{
		if(a>=x&&a<=x+xB)
			if(b>=y&&b<=y+yB)
				return true;

		return false;
	}

	public double getValue(int a)
	{
		return (rangeC-rangeF)/(xB)*(a-x)+rangeF;
	}

	public void click(int a, int b)
	{
		if(containsPoint(a,b))
		{
			value=getValue(a);
			color=calcColor(a);
			last=a;
		}
	}

	public void setLabel(String s)
	{
		label=s;
	}

	public void setRange(double f, double c)
	{
		rangeF=f;
		rangeC=c;
	}

	public int getSliderX()
	{
		return (int)((xB*value+(rangeC-rangeF)*x-rangeF*xB)/(rangeC-rangeF));
	}

	public Color calcColor(int a)
	{
		Color out = new Color(255*(a-x)/xB,255-255*(a-x)/xB,255-255*(a-x)/xB);
		return out;
	}
}