import java.util.*;
import java.awt.*;

public class VSphere extends Sphere
{
	public int NUMSIDES,BOUND;
	public double RADIUS;
	public Point3D center;
	public Vect3D vect;
	public Vect3D accel;
	public Vect3D accelPerp;
	public double PATHSIZE=1000;
	public double DOWNGRAVCONST=0,GRAVCONST=1000,MASS=10,CHARGE=1,CHARGECONST=10000;
	public ArrayList<Point3D> path = new ArrayList<Point3D>();
	public boolean STATIONARY=false;
	public boolean RANDOM=false;
	public double angVel1=0,angVel2=0,angVel3=0;
	public double A1=0,A2=0,A3=0;
	public Color pathColor;
	public String label=null;
	public double PERMEABILITY=100;
	public double RANDMAG=.1;
	public Vect3D [][] k = new Vect3D[4][2];	//Vectors used for Runge-Kutta 4 metod
	Sphere draw;

	public VSphere(Point3D p, double r, int n, Vect3D d, int b)
	{
		super(p,r,n);
		RADIUS=r;
		NUMSIDES=n;
		center=p;
		vect=d;
		BOUND=b;
		id="VSphere";
		accel=new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0));
		accelPerp=new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0));
		draw=new Sphere(new Point3D(0,0,0),1.0,NUMSIDES);
	}

	public Sphere getSphere()
	{
		numSides=NUMSIDES;
		return new Sphere(center,RADIUS,NUMSIDES);
	}
	public Sphere getSphere(Sphere unit)
	{
		Sphere s=new Sphere(unit);
		for(Line3D ln: s.lines)
		{
		  Vect3D pt=new Vect3D(ln.point1);
		  ln.point1=new Point3D(pt.times(RADIUS).addVect(new Vect3D(center)));
		  pt=new Vect3D(ln.point2);
		  ln.point2=new Point3D(pt.times(RADIUS).addVect(new Vect3D(center)));
		}
		numSides=NUMSIDES;
		return s;
	}

	public void move()
	{
		if(RANDOM)
		{
			vect=vect.addVect(new Vect3D(new Point3D(0,0,0),new Point3D((2*Math.random()-1)*RANDMAG,(2*Math.random()-1)*RANDMAG,(2*Math.random()-1)*RANDMAG)));
		}
		if(!STATIONARY)
		{
			path.add(center);
			if(path.size()>PATHSIZE)
				path.remove(0);
			center=new Point3D(center.x+vect.x,center.y+vect.y,center.z+vect.z);
		}
		ArrayList<Line3D> lns= new ArrayList<Line3D>();
		for(int i=0;i<draw.lines.size();i++)
			lns.add(new Line3D(new Point3D(draw.lines.get(i).point1.x*RADIUS+center.x,draw.lines.get(i).point1.y*RADIUS+center.y,draw.lines.get(i).point1.z*RADIUS+center.z),new Point3D(draw.lines.get(i).point2.x*RADIUS+center.x,draw.lines.get(i).point2.y*RADIUS+center.y,draw.lines.get(i).point2.z*RADIUS+center.z)));
		lines=lns;
	}
	public void accelerate(double tI)
	{
		vect=vect.addVect(accel.times(tI));

	}
	public void move(double tI)
	{
		//System.out.println(accel);
		if(!STATIONARY&&!Double.isNaN(accel.getMagSquared()))
		  accelerate(tI);
		if(RANDOM)
		{
			vect=vect.addVect(new Vect3D(new Point3D(0,0,0),new Point3D((2*Math.random()-1)*RANDMAG,(2*Math.random()-1)*RANDMAG,(2*Math.random()-1)*RANDMAG)));
		}
		if(!STATIONARY)
		{
		  path.add(center);
		  if(path.size()>PATHSIZE)
			  path.remove(0);
		  center=new Point3D(center.x+vect.x*tI,center.y+vect.y*tI,center.z+vect.z*tI);
		}
	}
	public Vect3D getVectTo(VSphere s,Vect3D kThis, Vect3D kThat)
	{
	  return (new Vect3D(s.center)).addVect(kThat).subtractVect((new Vect3D(center)).addVect(kThis));
	}
	public Arrow3D getVelArrow() {
		Vect3D direc = new Vect3D(vect);
		direc.setMag(RADIUS);
		Arrow3D arrow = new Arrow3D(direc.getPointFrom(center),RADIUS/5,RADIUS,vect,NUMSIDES);
		arrow.color=Color.BLUE;
		return arrow;
	}

	public void bounce()
	{
		if(center.x+RADIUS>=BOUND&&vect.x>0||center.x-RADIUS<=-BOUND&&vect.x<0)
			vect.x*=-1;
		if(center.y+RADIUS>=BOUND&&vect.y>0||center.y-RADIUS<=-BOUND&&vect.y<0)
		{
			vect.y*=-1;
			vect.y+=DOWNGRAVCONST;
		}
		if(center.z+RADIUS>=BOUND&&vect.z>0||center.z-RADIUS<=-BOUND&&vect.z<0)
			vect.z*=-1;
	}

	public void bounceSphere()
	{
		Vect3D pos = new Vect3D(new Point3D(0,0,0),center);
		if(pos.getMag()>=BOUND-RADIUS)
		{
			pos.multiply(1/pos.getMag());
			if(vect.dotProduct(pos)>=0)
				pos.setMag(vect.dotProduct(pos));
			else
				return;
			pos.multiply(-2);
			vect=vect.addVect(pos);
		}
	}
	public void bounceCylinder()
	{
		if(Math.abs(center.z)>=BOUND-RADIUS)
			vect.z*=-1;
		if(Math.sqrt(Math.pow(center.x,2)+Math.pow(center.y,2))>=BOUND-RADIUS)
		{
			Vect3D pos = new Vect3D(new Point3D(0,0,center.z),center);
			pos.multiply(1/pos.getMag());
			if(pos.dotProduct(vect)>=0)
			{
				pos.setMag(pos.dotProduct(vect));
				pos.multiply(-2);
				vect=vect.addVect(pos);
			}
		}
	}
	public void bounceRotate()
	{
		if(center.x+RADIUS>=BOUND&&vect.x>0||center.x-RADIUS<=-BOUND&&vect.x<0)
		{
			vect.x*=-1;
		}
		if(center.y+RADIUS>=BOUND&&vect.y>0||center.y-RADIUS<=-BOUND&&vect.y<0)
		{
			vect.y*=-1;
			vect.y+=DOWNGRAVCONST;
			if(vect.z>0)
			{
			vect.z=Math.sqrt(3*(angVel3*angVel3*RADIUS*RADIUS+2*vect.z*vect.z))/3;
			angVel3=-vect.z/RADIUS;
			}
			else if(vect.z<0)
			{
				vect.z=-Math.sqrt(3*(angVel3*angVel3*RADIUS*RADIUS+2*vect.z*vect.z))/3;
				angVel3=-vect.z/RADIUS;
			}
			if(vect.x>0)
			{
				vect.x=Math.sqrt(3*(angVel1*angVel1*RADIUS*RADIUS+2*vect.x*vect.x))/3;
				angVel1=vect.x/RADIUS;
			}
			else if(vect.x<0)
			{
				vect.x=-Math.sqrt(3*(angVel1*angVel1*RADIUS*RADIUS+2*vect.x*vect.x))/3;
				angVel1=vect.x/RADIUS;
			}
		}
		if(center.z+RADIUS>=BOUND&&vect.z>0||center.z-RADIUS<=-BOUND&&vect.z<0)
			vect.z*=-1;
	}

	public double getDist(VSphere s)
	{
		return Math.sqrt(Math.pow(s.center.x-center.x,2)+Math.pow(s.center.y-center.y,2)+Math.pow(s.center.z-center.z,2));
	}
	public double getDist(VSphere s,Vect3D kThis,Vect3D kThat)
	{
		return (new Vect3D(s.center)).addVect(kThat).subtractVect((new Vect3D(center)).addVect(kThis)).getMag();	
	}
	public double getDistSquared(VSphere s)
	{
	  return new Vect3D(s.center).subtractVect(new Vect3D(center)).getMagSquared();
	}
	public double getDistSquared(VSphere s,Vect3D kThis,Vect3D kThat)
	{
		return (new Vect3D(s.center)).addVect(kThat).subtractVect((new Vect3D(center)).addVect(kThis)).getMagSquared();	
	}
	public double calcFinal1(double a,double m1,double b,double m2)
	{
		return ((m1-m2)/(m1+m2))*a+((2*m2)/(m1+m2))*b;
	}

	public double calcFinal2(double a,double m1,double b,double m2)
	{
		return ((2*m1)/(m1+m2))*a+((m2-m1)/(m1+m2))*b;
	}

	public void collide(VSphere s)
	{
		double a=calcFinal1(vect.x,MASS,s.vect.x,s.MASS);
		double b=calcFinal2(vect.x,MASS,s.vect.x,s.MASS);

		vect.x=a;
		s.vect.x=b;

		a=calcFinal1(vect.y,MASS,s.vect.y,s.MASS);
		b=calcFinal2(vect.y,MASS,s.vect.y,s.MASS);

		vect.y=a;
		s.vect.y=b;

		a=calcFinal1(vect.z,MASS,s.vect.z,s.MASS);
		b=calcFinal2(vect.z,MASS,s.vect.z,s.MASS);

		vect.z=a;
		s.vect.z=b;
	}

	public void collideSphere(VSphere s)
	{
		Vect3D rHat=new Vect3D(s.center,center);
		double dist=rHat.getMag();
		rHat.multiply(1.0/dist);
		double v1=vect.dotProduct(rHat),v2=s.vect.dotProduct(rHat);
		Vect3D sub1=new Vect3D(rHat),sub2=new Vect3D(rHat);
		sub1.multiply(v1);
		sub2.multiply(v2);
		vect=vect.subtractVect(sub1);
		s.vect=s.vect.subtractVect(sub2);
		sub1=new Vect3D(rHat);
		sub2=new Vect3D(rHat);
		sub1.multiply(calcFinal1(v1,MASS,v2,s.MASS));
		sub2.multiply(calcFinal2(v1,MASS,v2,s.MASS));
		vect=vect.addVect(sub1);
		s.vect=s.vect.addVect(sub2);
	}

	public boolean collidesWith(VSphere s)
	{
		return getDist(s)<=RADIUS+s.RADIUS;
	}

	public void gravitate(VSphere s)
	{
		Vect3D direc = new Vect3D(center,s.center);
		double dSqr=direc.getMagSquared(),dist=Math.sqrt(dSqr);
		direc.multiply(1/dist);
		direc.multiply(GRAVCONST*s.MASS/(dSqr));
		accel=accel.addVect(direc);
		direc.multiply(-MASS/s.MASS);
		s.accel=s.accel.addVect(direc);
	}
	public void gravitate(VSphere s, Vect3D kThis, Vect3D kThat, int kn, double timeInt)
	{
		Vect3D direc = getVectTo(s,kThis,kThat);
		double dSqr=direc.getMagSquared();
		direc.normalize();
		direc.multiply(GRAVCONST*s.MASS/(dSqr));
		k[kn][0]=k[kn][0].addVect(direc.times(timeInt));
		direc.multiply(-MASS/s.MASS);
		s.k[kn][0]=s.k[kn][0].addVect(direc.times(timeInt));
		
	}

	public void staticForce(VSphere s)
	{
		Vect3D direc = new Vect3D(center,s.center);
		direc.multiply(-1/direc.getMag());
		direc.multiply(CHARGECONST*s.CHARGE*CHARGE/(getDistSquared(s))*MASS);
		accel=accel.addVect(direc);
		direc.multiply(-MASS/s.MASS);
		s.accel=s.accel.addVect(direc);
	}
	public void staticForce(VSphere s, Vect3D kThis, Vect3D kThat,int kn, double timeInt)
	{
		Vect3D direc = getVectTo(s,kThis,kThat);
		double dSqr=direc.getMagSquared();
		direc.normalize();
		direc.multiply(-CHARGECONST*s.CHARGE*CHARGE/(dSqr*MASS));
		k[kn][0]=k[kn][0].addVect(direc.times(timeInt));
		direc.multiply(-MASS/s.MASS);
		s.k[kn][0]=s.k[kn][0].addVect(direc.times(timeInt));
	}
	public Vect3D staticForceTo(VSphere s)   //used for field lines
	{
		Vect3D direc = new Vect3D(center,s.center);
		direc.multiply(-1/direc.getMag());
		direc.multiply(CHARGECONST*s.CHARGE*CHARGE/(getDistSquared(s)*MASS));
		return direc;
	}
	public void airRes(double b)
	{
	  accel=accel.addVect(vect.times(-b));
	}
	public void airRes(double b,Vect3D kv,int kn, double timeInt)
	{
	  k[kn][0]=k[kn][0].addVect(vect.addVect(kv).times(-b*timeInt));
	}
	public void spin()
	{
		A1+=angVel1;
		A2+=angVel2;
		A3+=angVel3;
	}

	public void printPolygon(Graphics page, Point pt, double d)
	{
		for(Line3D p : lines)
		{
			p.color=color;
			p.printLine(page,pt,d);
		}
		/*Label lbl = new Label(new Point3D(center.x,-center.y,center.z),label);
		lbl.printPolygon(page,pt,d);*/
	}

	public void magForce(Vect3D mag)
	{
		Vect3D direc = vect.crossProduct(mag);
		direc.multiply(CHARGE*CHARGECONST/10000000/MASS);
		accel=accel.addVect(direc);
	}
	public void magForce(Vect3D mag,Vect3D kv, int kn, double tStep)
	{
		Vect3D direc = vect.addVect(kv).crossProduct(mag);
		direc.multiply(CHARGE*CHARGECONST/10000000/MASS);
		k[kn][0]=k[kn][0].addVect(direc.times(tStep));
	}
	public void magForce(VSphere s)
	{
		Vect3D direc= new Vect3D(center,s.center);
		Vect3D vel=new Vect3D(new Point3D(0,0,0),new Point3D(vect.x,vect.y,vect.z));
		vel.multiply(CHARGE*s.CHARGE*PERMEABILITY/Math.pow(getDist(s),2));
		direc.multiply(1/direc.getMag());
		Vect3D field = vel.crossProduct(s.vect.crossProduct(direc));
		field.multiply(1/MASS);
		accel=accel.addVect(field);

		direc= new Vect3D(s.center,center);
		vel=new Vect3D(new Point3D(0,0,0),new Point3D(s.vect.x,s.vect.y,s.vect.z));
		vel.multiply(CHARGE*s.CHARGE*s.PERMEABILITY/Math.pow(getDist(s),2));
		direc.multiply(1/direc.getMag());
		field = vel.crossProduct(vect.crossProduct(direc));
		field.multiply(1/s.MASS);
		s.accel=s.accel.addVect(field);

	}
	public void magForce(VSphere s, Vect3D kvThis, Vect3D kxThis, Vect3D kvThat, Vect3D kxThat,int kn,double timeInt)
	{
		Vect3D direc=getVectTo(s,kxThis,kxThat);
		double dist=direc.getMag();
		Vect3D vel=(new Vect3D(new Point3D(vect.x,vect.y,vect.z))).addVect(kvThis);
		vel.multiply(CHARGE*s.CHARGE*PERMEABILITY/dist/dist);
		direc.multiply(1/dist);
		Vect3D direc2=new Vect3D(direc);
		Vect3D field = vel.crossProduct(s.vect.addVect(kvThat).crossProduct(direc));
		field.multiply(1/MASS);
		k[kn][0]=k[kn][0].addVect(field.times(timeInt));

		direc= direc2.times(-1);
		vel=(new Vect3D(new Point3D(0,0,0),new Point3D(s.vect.x,s.vect.y,s.vect.z))).addVect(kvThat);
		vel.multiply(CHARGE*s.CHARGE*s.PERMEABILITY/dist/dist);
		field = vel.crossProduct(vect.addVect(kvThis).crossProduct(direc));
		field.multiply(1/s.MASS);
		s.k[kn][0]=s.k[kn][0].addVect(field.times(timeInt));

	}
	public String toString()
	{
	  return "Center: "+center+"\tVelocity: "+vect+"\tAcceleration: "+accel+"\tRadius: "+RADIUS;
	}


}