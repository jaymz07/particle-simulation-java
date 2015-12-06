import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class ToggleBar {

	public ArrayList<ToggleButton> buttons = new ArrayList<ToggleButton>();
	Graphics page;
	public int WIDTH=100;
	public int HEIGHT=50;
	int last=-1;
	int x=0;
	int y=0;

    public ToggleBar(Graphics p, int n, int a, int b)
    {
    	x=a;
    	y=b;
    	page=p;
		for(int i=0;i<n;i++)
		{
			buttons.add(new ToggleButton(page,b-(WIDTH*n/2+5*n/2)+i*(WIDTH+5),a-50,WIDTH,HEIGHT,"YAY BUTTON!"));
		}
//		arrowDist=YBOUND/3;
    }

    public ToggleBar(Graphics p, int n, int a, int b, int c, int d)
    {
    	WIDTH=c;
    	HEIGHT=d;
    	x=a;
    	y=b;
    	page=p;
		for(int i=0;i<n;i++)
		{
			buttons.add(new ToggleButton(page,b-(WIDTH*n/2+5*n/2)+i*(WIDTH+5),a-50,WIDTH,HEIGHT,"YAY BUTTON!"));
		}
//		arrowDist=YBOUND/3;
    }

    public void toggle(int a, int b)
	{
		for(int i=0;i<buttons.size();i++)
		{
			if(buttons.get(i).pointIsWithin(a,b))
			{
				buttons.get(i).toggle();
				break;
			}
		}
	}

	public void select(int a, int b)
	{
		int cont=-1;
		for(int i=0;i<buttons.size();i++)
		{
			if(buttons.get(i).pointIsWithin(a,b))
			{
				buttons.get(i).selected=true;
				cont=i;
				break;
			}
		}

		for(int i=0;i<buttons.size();i++)
			if(i!=cont)
				buttons.get(i).selected=false;

	}


    public void print()
    {
		for(ToggleButton tb : buttons)
			tb.printButton();
    }

	public void printTrans()
    {
		for(ToggleButton tb : buttons)
			tb.printButtonTrans();
    }

   public boolean [] getValues()
	{
		boolean [] out = new boolean[buttons.size()];
		for(int i=0;i<buttons.size();i++)
				out[i]=buttons.get(i).toggled;
		return out;
	}

	public void setLabels(String s)
	{
		String in[] = s.split(",");
		for(int i=0;i<in.length;i++)
			buttons.get(i).setLabel(in[i]);
	}

	public boolean containsPoint(int a, int b)
	{
		boolean out=false;
		for(ToggleButton tb : buttons)
		{
			if(tb.pointIsWithin(a,b))
			{
				out=true;
				break;
			}
		}
		return out;
	}

}