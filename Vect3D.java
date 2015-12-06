public class Vect3D
{
	public double x;
	public double y;
	public double z;

	public double mag;

	public Vect3D()
	{
		x=0;
		y=0;
		z=0;
		mag=0;
	}

	public Vect3D(double xi,double yi,double zi)
	{
		x=xi;
		y=yi;
		z=zi;
	}

	public Vect3D(Vect3D o)
	{
		x=o.x;
		y=o.y;
		z=o.z;
	//	mag=Math.sqrt(x*x+y*y+z*z);
	}

	public Vect3D(Point3D p1, Point3D p2)
	{
		x=p2.x-p1.x;
		y=p2.y-p1.y;
		z=p2.z-p1.z;
//		mag=Math.sqrt(x*x+y*y+z*z);
	}
	public Vect3D(Point3D p1)
	{
		x=p1.x;
		y=p1.y;
		z=p1.z;
	}

	public Vect3D(double ang1, double ang2, double ang3, double m)
	{
		mag=m;
		Point3D point = new Point3D(m,0,0);
		point.rotate(new Point3D(0,0,0),ang1,ang2,ang3);
		x=point.x;
		y=point.y;
		z=point.z;
	}

	public double getMag()
	{
		return Math.sqrt(x*x+y*y+z*z);
	}
	public double getMagSquared()
	{
	  return x*x+y*y;
	}
	public void normalize()
	{
		mag=getMag();
		x/=mag;
		y/=mag;
		z/=mag;
	}
	public Vect3D getNormalized()
	{
		mag=getMag();
		return new Vect3D(x/mag,y/mag,z/mag);
	}

   public void rotateVect(Point3D cent, double ang1, double ang2, double ang3)
   {
   		PointP pt = new PointP(new Point3D(x,y,z));
   		pt.rotate(cent,ang1,ang2,ang3);
   		Point3D point = pt.point;
   		x=point.x;
   		y=point.y;
   		z=point.z;
   }

	public Point3D getPointFrom(Point3D p)
	{
		return new Point3D(p.x+x,p.y+y,p.z+z);
	}
	public Point3D getNegPointFrom(Point3D p)
	{
		return new Point3D(p.x-x,p.y-y,p.z-z);
	}

	public void multiply(double d)
	{
		x*=d;
		y*=d;
		z*=d;
	}

	public Vect3D times(double d)
	{
		return new Vect3D(x*d,y*d,z*d);
	}

	public Vect3D addVect(Vect3D v)
	{
		return new Vect3D(new Point3D(0,0,0),new Point3D(x+v.x,y+v.y,z+v.z));
	}
	public Vect3D subtractVect(Vect3D v)
	{
		return new Vect3D(new Point3D(0,0,0),new Point3D(x-v.x,y-v.y,z-v.z));
	}

	public void setMag(double m)
	{
		double mG=getMag();
		x=x*m/mG;
		y=y*m/mG;
		z=z*m/mG;
	}

	public double dotProduct(Vect3D b)
	{
		return x*b.x+y*b.y+z*b.z;
	}

	public Vect3D crossProduct(Vect3D b)
	{
		return new Vect3D(new Point3D(0,0,0),new Point3D(y*b.z-z*b.y,z*b.x-x*b.z,x*b.y-y*b.x));
	}
	public Vect3D getXZPerp1()
	{
		return new Vect3D(new Point3D(0,0,0),new Point3D(-z,y,x));
	}
	public Vect3D getXZPerp2()
	{
		return new Vect3D(new Point3D(0,0,0),new Point3D(z,y,-x));
	}
	public String toString()
	{
		return "["+x+","+y+","+z+"]";
	}
}