 import java.awt.*;

public class Link3D
{
	public double FORCECONST=100;
	public double COLORCONST=3;

	public VSphere a;
	public VSphere b;
	public double eD=0;

	public Link3D(VSphere end1, VSphere end2)
	{
		a=end1;
		b=end2;
		eD=a.getDist(b);
		FORCECONST/=eD;
	}

	public LineP getLine()
	{
		LineP out = new LineP(a.center,b.center);
		out.color=getColor(getDisplacement());
		return out;
	}

	public void force()
	{
		Vect3D direc = new Vect3D(a.center,b.center);
		double dist=direc.getMag();
		direc.multiply(1.0/dist);
		direc.multiply((dist-eD)*FORCECONST/a.MASS);
		a.accel=a.accel.addVect(direc);
		direc.multiply(-a.MASS/b.MASS);
		b.accel=b.accel.addVect(direc);
	}
	public void force(Vect3D k1, Vect3D k2, int kn, double timeInterval)
	{
		Vect3D direc = (new Vect3D(a.center)).addVect(k1).subtractVect((new Vect3D(b.center)).addVect(k2));
		double dist=direc.getMag();
		direc.multiply(1.0/dist);
		direc.multiply((eD-dist)*FORCECONST/a.MASS);
		a.k[kn][0]=a.k[kn][0].addVect(direc.times(timeInterval));
		direc.multiply(-a.MASS/b.MASS);
		b.k[kn][0]=b.k[kn][0].addVect(direc.times(timeInterval));
	}
	public void forceRigid()
	{
		Vect3D direc = new Vect3D(a.center,b.center);
		direc.multiply(1/direc.getMag());
		direc.multiply(getDisplacement()*.9);
		a.vect=a.vect.addVect(direc);
		direc.multiply(-1);
		b.vect=b.vect.addVect(direc);
	}

	public double getDisplacement()
	{
		return a.getDist(b)-eD;
	}

	public boolean contains(VSphere in)
	{
		if(a.equals(in)||b.equals(in))
			return true;
		return false;
	}

	public Color getColor(double d)
   {
   		double cConst=COLORCONST;
   		double e=Math.abs(d);
   		int out=(int)(2*255/(1+Math.pow(Math.E,-cConst*e))-254);
   		if(out>=0&&out<=255)
   		{
   		if(d<0)
   			return new Color(0,0,out);
   		return new Color(0,out,0);
   		}
   		return new Color(255,0,0);

   }


}