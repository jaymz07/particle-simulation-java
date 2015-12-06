public class Point3D
{
	public double x;
	public double y;
	public double z;

	public Point3D(double a, double b, double c)
	{
		x=a;
		y=b;
		z=c;
	}
	public Point3D(Vect3D v)
	{
		x=v.x;
		y=v.y;
		z=v.z;
	}
	public boolean isSameAs(Point3D p)
	{
		return p.x==x&&p.y==y&&p.z==z;
	}

	public String toString()
	{
		return "("+x+","+y+","+z+")";
	}
	public double getDistTo(Point3D other)
	{
		return Math.sqrt(Math.pow(x-other.x,2)+Math.pow(y-other.y,2)+Math.pow(z-other.z,2));
	}
	public double getDistToCenter()
	{
	    return Math.sqrt(x*x+y*y+z*z);
	}
	/*public void rotateAroundAxis(Point3D pt, Vect3D a, double angle)
	{
		x-=pt.x;
		y-=pt.y;
		z-=pt.z;
		double a1=Math.atan2(a.x,a.y), a2=Math.atan2(a.x,a.z);
		rotate(new Point3D(0,0,0),-a1,0,0);
		rotate(new Point3D(0,0,0),0,-a2,0);
		rotate(new Point3D(0,0,0),angle,0,0);
		rotate(new Point3D(0,0,0),0,a2,0);
		rotate(new Point3D(0,0,0),a1,0,0);
		x+=pt.x;
		y+=pt.y;
		z+=pt.z;
	}*/
	public void rotateAroundAxis(Point3D pt, Vect3D a, double angle)
	{
		Matrix rot=new Matrix(a,angle),m=new Matrix(pt),out=rot.multiply(m);
		x=out.data[0][0];
		y=out.data[1][0];
		z=out.data[2][0];
	}
	public void rotate(Point3D p, double ang1, double ang2, double ang3)
	{
		Matrix rotXY = new Matrix(ang1,'z'), rotXZ=new Matrix(-ang2,'y'), rotYZ=new Matrix(ang3,'x'), rot=rotYZ.multiply(rotXZ.multiply(rotXY)), cent= new Matrix(p);
		Point3D out=rot.multiply(new Matrix(new Vect3D(p,this))).plus(cent).getPoint3D();
		x=out.x;
		y=out.y;
	}
	public void rotate(Point3D p, Matrix rot)
	{
		Point3D out=getRotated(p,rot);
		x=out.x;
		y=out.y;
	}
	public Point3D getRotated(Point3D p, Matrix rot)
	{
		Matrix cent= new Matrix(p);
		Point3D out=rot.multiply(new Matrix(new Vect3D(p,this))).plus(cent).getPoint3D();
		return out;
	}
	public Point getScreenPoint(Point vPoint, double vDist)
	{
		Vect direc= new Vect(vPoint,new Point(x,y));
		direc.multiply(1/(1+z/vDist));
		if(z>=0)
			return direc.getPointFrom(vPoint);
		return new Point(0,0);
	}
	
}