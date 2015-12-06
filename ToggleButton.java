import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class ToggleButton {
	public int x;
	public int y;
	public int xW;
	public int yW;
	public boolean hasOption=true;
	public boolean hasOtherOption=true;
	public boolean hasNum=true;
	String label="";
	Color color=Color.WHITE;
	Color stringColor=Color.BLACK;
	Graphics page;
	boolean toggled=false;
	boolean selected=false;

    public ToggleButton(Graphics p, int a, int b, int c,int d, String s) {
    	page=p;
    	x=a;
    	y=b;
    	xW=c;
    	yW=d;
    	label=s;
    }

    public void printButton()
    {
    	page.setColor(Color.BLACK);
    	page.fillRect(x,y,xW,yW);
    	toggleSelect();
    	page.setColor(color);
    	page.fillRect(x+1,y+1,xW-2,yW-2);
    	page.setColor(stringColor);
    	page.drawString(label,x+2,y+yW/2);
    }

    public void printButtonTrans()
    {
    	page.setColor(Color.BLACK);
    	page.drawRect(x,y,xW-1,yW-1);
    	toggleSelect();
    	page.setColor(color);
    	if(!color.equals(Color.WHITE))
    		page.fillRect(x+1,y+1,xW-2,yW-2);
    	page.setColor(stringColor);
    	page.drawString(label,x+2,y+yW/2);
    }

    public void toggle()
    {
    	toggled=!toggled;
    	if(toggled)
    		color=Color.GREEN;
    	else
    		color=Color.WHITE;
    }

    public boolean pointIsWithin(int a,int b)
    {
    	if(a>=x&&a<=x+xW&&b>=y&&b<=y+yW)
    		return true;
    	return false;
    }

    public void setColor(Color a)
    {
    	color=a;
    }

    public void toggleSelect()
    {
    	if(toggled&&selected)
    	{
    		color=Color.BLUE;
    	}
    	else if(toggled&&!selected)
    		color=Color.GREEN;
    	else if(!toggled&&!selected)
    		color=Color.WHITE;
    	else
    		color=Color.RED;
    }
    public void selected(boolean a)
    {
    	selected=a;
    }

    public void setLabel(String s)
    {
    	label=s;
    }

    public void setOption(boolean a)
    {
    	hasOption=a;
    }



}