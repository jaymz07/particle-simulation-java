import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class SelectMenu {

	public ArrayList<ToggleButton> buttons = new ArrayList<ToggleButton>();
	Graphics page;
	public int WIDTH=100;
	public int HEIGHT=20;
	int last=-1;
	int x=0;
	int y=0;

    public SelectMenu(Graphics p, int n, int a, int b)
    {
    	x=a;
    	y=b;
    	page=p;
		for(int i=0;i<n;i++)
		{
			buttons.add(new ToggleButton(page,a,b+HEIGHT*i,WIDTH,HEIGHT,"YAY BUTTON!"));
		}
//		arrowDist=YBOUND/3;
		buttons.get(0).toggled=true;
    }

    public SelectMenu(Graphics p, int n, int a, int b, int c, int d)
    {
    	WIDTH=c;
    	HEIGHT=d;
    	x=a;
    	y=b;
    	page=p;
		for(int i=0;i<n;i++)
		{
			buttons.add(new ToggleButton(page,a,b+HEIGHT*i,WIDTH,HEIGHT,"YAY BUTTON!"));
		}
//		arrowDist=YBOUND/3;
		buttons.get(0).toggled=true;
    }

    public void toggle(int a, int b)
	{
		int cont =-1;
		for(int i=0;i<buttons.size();i++)
		{
			if(buttons.get(i).pointIsWithin(a,b))
			{
				buttons.get(i).toggled=true;
				cont=i;
				break;
			}
		}
		if(cont!=-1)
			for(int i=0;i<buttons.size();i++)
				if(i!=cont)
					buttons.get(i).toggled=false;
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

   public int getValue()
	{
		for(int i=0;i<buttons.size();i++)
		{
			if(buttons.get(i).toggled)
				return i;
		}
		return -1;
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