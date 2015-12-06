import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class NumMenu {

	public ArrayList<NumBar> numBars = new ArrayList<NumBar>();
	Graphics page;
	int XBOUND;
	int YBOUND;
	public int WIDTH=100;
	public int HEIGHT=50;
	int last=-1;
	int x=0;
	int y=0;

    public NumMenu(Graphics p, int n, int xB, int yB) {
    	page=p;
		XBOUND=xB;
		YBOUND=yB;
		x=XBOUND/2;
		y=YBOUND/2;
//		arrowDist=YBOUND/3;
		for(int i=0;i<n;i++)
		{
			numBars.add(new NumBar(page,y-50,x-(50*n/2+5*n/2)+i*(50+5),WIDTH,HEIGHT,0.0,1.0));
		}
    }

    public NumMenu(Graphics p, int n, int xB, int yB, int a, int b)
    {
    	x=a;
    	y=b;
    	page=p;
		XBOUND=xB;
		YBOUND=yB;
//		arrowDist=YBOUND/3;
		for(int i=0;i<n;i++)
		{
			numBars.add(new NumBar(page,y-50,x-(50*n/2+5*n/2)+i*(50+5),WIDTH,HEIGHT,0.0,1.0));
		}
		for(NumBar nb : numBars)
		{
			nb.setRange(0,1000);
			nb.value=0;
		}
    }

	public NumMenu(Graphics p, int n, int xB, int yB, int a, int b, int c, int d)
    {
    	WIDTH=c;
		HEIGHT=d;
    	x=a;
    	y=b;
    	page=p;
		XBOUND=xB;
		YBOUND=yB;
//		arrowDist=YBOUND/3;
		for(int i=0;i<n;i++)
		{
			numBars.add(new NumBar(page,y-HEIGHT,x-(HEIGHT*n/2+5*n/2)+i*(HEIGHT+5),WIDTH,HEIGHT,0.0,1.0));
		}
		for(NumBar nb : numBars)
		{
			nb.setRange(0,1000);
			nb.value=0;
		}
    }

    public void toggle(int a, int b)
	{
		for(int i=0;i<numBars.size();i++)
			if(numBars.get(i).containsPoint(a,b))
			{
				numBars.get(i).click(a,b);
				break;
			}
	}


    public void printMenu()
    {
    	for(NumBar n : numBars)
    		n.printBar();
    }

    public double [] getValues()
    {
    	double [] out = new double[numBars.size()];
    	for(int i=0;i<numBars.size();i++)
    		out[i]=numBars.get(i).value;
    	return out;
    }

	public void setLabels(String s)
	{
		String in[] = s.split(",");
		for(int i=0;i<in.length;i++)
			numBars.get(i).setLabel(in[i]);
	}

	public boolean containsPoint(int a, int b)
	{
		boolean out=false;
		for(NumBar nb : numBars)
		{
			if(nb.containsPoint(a,b))
			{
				out=true;
				break;
			}
		}
		return out;
	}

}