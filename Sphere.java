import java.util.*;
import java.awt.*;

public class Sphere extends Polygon
{
	public int numSides;
	public Sphere(Point3D p, double r, int n)
	{
		super();
		numSides=n;
		id="Sphere";
		ArrayList<Point3D> points1 = new ArrayList<Point3D>();
		RegularPolygon rp = new RegularPolygon(p,r,n*2);
		Point3D center = rp.getCenter();

		for(int i=0;i<n;i++)
		{
			rp.pan(new Point3D(0,0,0));
			rp.rotate(center,0,0,Math.PI/n);
			ArrayList<Line3D> points2=rp.lines;
			for(Line3D pt : points2)
			  lines.add(pt);

		}
		for(int i=1;i<=n;i++)
		{
		    double xv=i*r*2/(n+1)-r;
		    rp = new RegularPolygon(new Point3D(p.x+xv,p.y,p.z),Math.sqrt(Math.pow(r,2)-Math.pow(xv,2)),n*2);
		    rp.pan(new Point3D(0,0,0));
		    rp.rotate(rp.center,0,Math.PI/2,0);
		    ArrayList<Line3D> points2=rp.lines;
		    for(Line3D pt : points2)
		      lines.add(pt);
		}

		lines.add(new Line3D(new Point3D(p.x-r,p.y,p.z),new Point3D(p.x-r,p.y,p.z)));
		lines.add(new Line3D(new Point3D(p.x+r,p.y,p.z),new Point3D(p.x+r,p.y,p.z)));

	//	lines.add(new Line3D(points1.get(0),points1.get(points1.size()-1)));
		pan(new Point3D(0,0,0));

	}
	public Sphere(Sphere s)
	{
	  super(s);
	  pan(new Point3D(0,0,0));
	}
	public ArrayList<Plane> getPlanes()
	{
	    Plane plane;
	    ArrayList<Point3D> add;
	    ArrayList<Plane> out= new ArrayList<Plane>();
	    int i0=numSides*numSides*2;
	    for(int i=-1;i<numSides;i++)
	    {
	      for(int j=0;j<numSides*2-1;j++)
	      {
		  add=new ArrayList<Point3D>();
		  if(i==-1)
		  {
		      i++;
		      add.add(lines.get(i0+i*numSides*2+j).point1);
		      add.add(lines.get(i0+i*numSides*2+j).point2);
		      add.add(lines.get(lines.size()-2).point1);
		      plane=new Plane(add);
		      plane.color=color;
		      out.add(plane);
		      i--;
		  }
		  else if(i==numSides-1)
		  {
		      add.add(lines.get(i0+i*numSides*2+j).point1);
		      add.add(lines.get(i0+i*numSides*2+j).point2);
		      add.add(lines.get(lines.size()-1).point1);
		      plane=new Plane(add);
		      plane.color=color;
		      out.add(plane);
		  }
		  else
		  {
		      add.add(lines.get(i0+i*numSides*2+j).point1);
		      add.add(lines.get(i0+i*numSides*2+j).point2);
		      add.add(lines.get(i0+(i+1)*numSides*2+j).point2);
		      add.add(lines.get(i0+(i+1)*numSides*2+j).point1);
		      plane=new Plane(add);
		      plane.color=color;
		      out.add(plane);
		  }
	      }
	      add=new ArrayList<Point3D>();
	      if(i==-1)
	      {
		  add.add(lines.get(i0).point1);
		  add.add(lines.get(i0+numSides*2-1).point2);
		  add.add(lines.get(lines.size()-2).point1);
		  plane=new Plane(add);
		  plane.color=color;
		  out.add(plane);
	      }
	      else if(i==numSides-1)
	      {
		  add.add(lines.get(i0+i*numSides*2).point1);
		  add.add(lines.get(i0+(i+1)*numSides*2-1).point2);
		  add.add(lines.get(lines.size()-1).point1);
		  plane=new Plane(add);
		  plane.color=color;
		  out.add(plane);
	      }
	      else {
		  add.add(lines.get(i0+i*numSides*2).point1);
		  add.add(lines.get(i0+(i+1)*numSides*2).point1);
		  add.add(lines.get(i0+(i+1)*numSides*2+numSides*2-1).point2);
		  add.add(lines.get(i0+(i)*numSides*2+numSides*2-1).point2);
		  plane=new Plane(add);
		  plane.color=color;
		  out.add(plane);
	      }

	    }
	    return out;
	}



}