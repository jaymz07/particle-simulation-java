import java.util.*;
import java.io.*;
import java.applet.*;
import java.awt.*;

public class PixelShader
{
	public Graphics page;
	public int XBOUND;
	public int YBOUND;
	public double VIEWDIST;
	public boolean LIGHTING=true;
	public int WHITEVALUE=170;
	public double LIGHTINGFACTOR=5;
	public int DEPTHMODE=1;

	public PixelShader(Graphics b, int xb, int yb, double vd)
	{
		page=b;
		XBOUND=xb;
		YBOUND=yb;
		VIEWDIST=vd;
	}

	public void drawDepthSorted(ArrayList<Polygon> shapes)
	{
      ArrayList<LineP> lines= new ArrayList<LineP>();
      ArrayList<Plane> planes = new ArrayList<Plane>();
      ArrayList<Label> labels = new ArrayList<Label>();


      for(int i=0;i<shapes.size();i++)
      {
	      if(shapes.get(i).id.equals("Cube")) {
			  ArrayList<Plane> add;
			  add = ((Cube)(shapes.get(i))).getPlanes();
			  for(Plane P : add) {
			      planes.add(P);
			  }
	      }
	      else if(shapes.get(i).id.equals("Prism")) {
			  ArrayList<Plane> add;
			  add = ((Prism)(shapes.get(i))).getPlanes();
			  for(Plane P : add) {
			      planes.add(P);
			  }
	      }
	      else if(shapes.get(i).id.equals("Pyramid")) {
		  ArrayList<Plane> add;
		  add = ((Pyramid)(shapes.get(i))).getPlanes();
		  for(Plane P : add) {
		      planes.add(P);
		  }
	      }
	      else if(shapes.get(i).id.equals("Arrow3D")) {
		  ArrayList<Plane> add;
		  add = ((Arrow3D)(shapes.get(i))).getPlanes();
		  for(Plane P : add) {
		      planes.add(P);
		  }
	      }
	      else if(shapes.get(i).id.equals("Sphere")||shapes.get(i).id.equals("VSphere")) {
		  ArrayList<Plane> add;
		  add = ((Sphere)(shapes.get(i))).getPlanes();
		  for(Plane P : add) {
		      planes.add(P);
		  }
	      }
	      else if(shapes.get(i).id.equals("Plane")) {
		  planes.add(((Plane)(shapes.get(i))));
	      }
	      else if(shapes.get(i).id.equals("Label")) {
		  labels.add((Label)(shapes.get(i)));
	      }
	      else
		for(Line3D l : shapes.get(i).lines)
		  lines.add(new LineP(l,shapes.get(i).color));

	      //shapes.get(i).color=Color.BLUE;
// 				if(!shapes.get(i).id.equals("Cube"))
// 					shapes.get(i).printPolygon(page,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
// 				else
// 					((Cube)(shapes.get(i))).drawFilled(page,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
      }
      ArrayList<Double> pDists = new ArrayList<Double>();
      ArrayList<Double> lDists = new ArrayList<Double>();
      ArrayList<Double> lblDists = new ArrayList<Double>();
      ArrayList<Integer> pI = new ArrayList<Integer>();
      ArrayList<Integer> lI = new ArrayList<Integer>();
      ArrayList<Integer> lblI = new ArrayList<Integer>();

      if(DEPTHMODE==0) {
	      for(Plane pl : planes)
	      {
			  pDists.add(pl.getCenter().getDistTo(new Point3D(YBOUND/2,XBOUND/2,VIEWDIST)));
	      }
	      for(LineP l : lines)
	      {
			  lDists.add(l.line.getMidpoint().getDistTo(new Point3D(YBOUND/2,XBOUND/2,VIEWDIST)));
	      }
	      for(Label lbl : labels)
	      {
		 	 lblDists.add(lbl.point.getDistTo(new Point3D(YBOUND/2,XBOUND/2,VIEWDIST)));
	      }
      }
      else if(DEPTHMODE==1)
      {
	      for(Plane pl : planes)
	      {
		 	 pDists.add(pl.getCenter().z+VIEWDIST);
	      }
	      for(LineP l : lines)
	      {
			  lDists.add(l.line.getMidpoint().z+VIEWDIST);
	      }
	      for(Label lbl : labels)
	      {
		 	 lblDists.add(lbl.point.z+VIEWDIST);
	      }
      }


      for(int i=0;i<lines.size();i++)
	lI.add(i);
      for(int i=0;i<planes.size();i++)
	pI.add(i);
      for(int i=0;i<labels.size();i++)
	lblI.add(i);

      for(int i=0;i<lI.size();i++)
	for(int j=i+1;j<lI.size();j++)
	{
	  if(lDists.get(lI.get(i))<lDists.get(lI.get(j)))
	  {
	    int temp=lI.get(i);
	    lI.set(i,lI.get(j));
	    lI.set(j,temp);
	  }
	}
      for(int i=0;i<pI.size();i++)
	for(int j=i+1;j<pI.size();j++)
	{
	  if(pDists.get(pI.get(i))<pDists.get(pI.get(j)))
	  {
	    int temp=pI.get(i);
	    pI.set(i,pI.get(j));
	    pI.set(j,temp);
	  }
	}
      for(int i=0;i<lblI.size();i++)
	for(int j=i+1;j<lblI.size();j++)
	{
	  if(lblDists.get(lblI.get(i))<lblDists.get(lblI.get(j)))
	  {
	    int temp=lblI.get(i);
	    lblI.set(i,lblI.get(j));
	    lblI.set(j,temp);
	  }
	}

      int iP=0,iL=0,iLBL=0;

      while(iP<planes.size()||iL<lines.size()||iLBL<labels.size())
      {
	  double pd=Double.MIN_VALUE,ld=Double.MIN_VALUE,lbld=Double.MIN_VALUE;
	  if(iP<planes.size())
	    pd=pDists.get(pI.get(iP));
	  if(iL<lines.size())
	    ld=lDists.get(lI.get(iL));
	  if(iLBL<labels.size())
	    lbld=lblDists.get(lblI.get(iLBL));
	  if(ld>pd&&ld>lbld)
	  {
	      lines.get(lI.get(iL)).printPolygon(page,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
	      iL++;
	  }
	  else if(pd>lbld&&iP<planes.size())
	  {
	      Vect3D v = new Vect3D(new Point3D(YBOUND/2,XBOUND/2,-VIEWDIST),planes.get(pI.get(iP)).getCenter());
	      double cf=Math.pow(Math.abs(planes.get(pI.get(iP)).getNormal().dotProduct(v)/pDists.get(pI.get(iP))),LIGHTINGFACTOR);
	      if(LIGHTING) {
		      Color c= planes.get(pI.get(iP)).color;
		      int r = c.getRed(), g = c.getGreen(), b = c.getBlue(), a = c.getAlpha();
		      if(r<WHITEVALUE)
				r=(int)((WHITEVALUE-r)*cf+r);
		      if(g<WHITEVALUE)
				g=(int)((WHITEVALUE-g)*cf+g);
		      if(b<WHITEVALUE)
				b=(int)((WHITEVALUE-b)*cf+b);
			  if(r>255)
			  	r=255;
			  if(g>255)
			  	g=255;
			  if(b>255)
			  	b=255;
			  if(r<0)
			  	r=0;
			  if(g<0)
			  	g=0;
			  if(b<0)
			  	b=0;
		      planes.get(pI.get(iP)).color=new Color(r,g,b,a);
	      }
	      planes.get(pI.get(iP)).printPolygon(page,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
	      iP++;
	  }
	  else if(iLBL<labels.size())
	  {
	      labels.get(lblI.get(iLBL)).printPolygon(page,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
	      iLBL++;
	  }
      }

      // 		for(Integer i : pI)
      // 		  planes.get(i).printPolygon(page,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
      // 		for(Integer i : lI) {
      // 		  LineP line = new LineP(lines.get(i));
      // 		  line.printPolygon(page,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
      // 		}
   }
}