import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class Balls3D extends Applet implements MouseListener, MouseMotionListener, KeyListener
{
	int randTries=2000,PATHINTERVAL=12,CHARGEITERATIONS=50,FOLLOWDIST=500,UNIVERSEMODE=0;
	double DOWNGRAVCONST=1,IVEL=2,MINDIST=0,BONDDIST=100,STATIONARYMASS=10,CHARGEINTERVAL=60,TOPHEIGHT=800,TOPRADIUS=200,TOPANGVEL=0.15,TOPANG=Math.PI/12,ANGVEL=.01,AIRRES=0;

	boolean COLLISIONS=false,GRAVITATION=true,DOWNGRAV=true,DRAWPATH=true,BONDING=true,PRINTMENU=true,STATICFORCE=false,RIGID=false,DRAWFIELD=false,DRAWEPFIELD=false,DRAWMFIELD=false,MAGFORCE=false,DRAWSPHERES=true,DRAWCENTMASS=false,DRAWLINKS=true,DRAWEVECTFIELD=false,ROTATEGRAV=false,FANCYMODE=false;
	boolean VARIABLETIMESTEP=true,DRAWVECTORS=false,RK4FIELD=true,RK4STEP=true,BREAKING=false,GRIDMODE=false;

	Vect3D magField = new Vect3D(new Point3D(0,0,0),new Point3D(0,10,0));

	double NUM=200;

	double VIEWDIST=800;
	double ZOOM=1;

	double timeInterval=1.0;

	double PHASE=0,WAVELENGTH=50,AMPLITUDE=30,PATHLENGTH=1000000;
	double SENSITIVTY=.004;
	double MSENS=8,ASENS=.3;

	int MAXPOINTS=200,INPUTDELAY=0,RADIUS=55,COUNTER=6;

	double MSENSP=MSENS;

	int mode=1,NUMSIDES=5, numP=0,gridNum;
	int prevIndex=-1,index=-1,followIndex=-1;
	PixelShader shader;
	Sphere unit = new Sphere(new Point3D(0,0,0),1,NUMSIDES);

	int BOUND=800;

	public ArrayList<Point3D> points3= new ArrayList<Point3D>();
	public ArrayList<Point> points2=new ArrayList<Point>();
	public ArrayList<Point> gPoints=new ArrayList<Point>();
	ArrayList<Polygon> shapes= new ArrayList<Polygon>();
	ArrayList<VSphere> spheres = new ArrayList<VSphere>();
	ArrayList<Link3D> links = new ArrayList<Link3D>();
	ArrayList<LineArray> cPaths = new ArrayList<LineArray>();
	ArrayList<Point3D> cmPath = new ArrayList<Point3D>();
	ArrayList<Point3D> tCenters = new ArrayList<Point3D>();

	NumMenu menu,selectMenu;
	String mLabels="Grav Const,Down Grav,Init Velocity,Link Tension,Bond Dist,Charge Const,Breaking Strength,Permeability,Universe Size,Air Resistance,Time Step";
	String sLabels="Mass,Radius";

	ToggleBar bar,mBar;
	String bLabels="Collisions,Bonding,Charges,Rigid Links,Mag Field,Cent Mass,Vect Field,Field Lines,Show Axes,Draw Paths,Show FPS,Hide Spheres,Const Step";
	String mbLabels="Show Menu";

	SelectMenu sMenu;
	String smLabels="Cube,Sphere,Cylinder";

	ActionMenu aMenu,saMenu;
	String aLabels="Add Sphere,Remove Last,Delete All,Make Stationary,Clear Paths,Insert Top,Insert Grid,Spinning Block";
	String saLabels="Delete";

	Point3D pos=new Point3D(0,0,-(Math.pow(2,11))*2),direction=new Point3D(0,0,0),gDirection=new Point3D(0,0,0);

	int XBOUND, YBOUND;

	boolean init=true;
	boolean SHOWSTATS=false;
	boolean DRAWNEGATIVE=false;
	boolean drawAxes=false;

	double time=0;
	double timeP=0;

	BufferedImage image;

	Sphere outSphere;

	Graphics iPage;

	Point mP=new Point(0,0),mPP=new Point(0,0);
	
	public void RK4Step()
	{
	  for(int k=0;k<4;k++) {
	    for(VSphere s: spheres)
	      s.k[k][0]=new Vect3D(0,0,0);
	    for(Link3D lk : links)
	    {
	      Vect3D kL1,kL2;
	      if(k==0) {
		kL1=new Vect3D(0,0,0);
		kL2=new Vect3D(0,0,0);
	      }
	      else if(k==1) {
		kL1=lk.a.k[0][1].times(0.5);
		kL2=lk.b.k[0][1].times(0.5);
	      }
	      else if(k==2) {
		kL1=lk.a.k[1][1].times(0.5);
		kL2=lk.b.k[1][1].times(0.5);
	      }
	      else {
		kL1=lk.a.k[2][1];
		kL2=lk.b.k[2][1];
	      }
	      if(lk.a.STATIONARY)
		kL1=new Vect3D(0,0,0);
	      if(lk.b.STATIONARY)
		kL2=new Vect3D(0,0,0);
	      lk.force(kL1,kL2,k,timeInterval);
	    }
	    for(int i=0;i<spheres.size();i++) {
	      //spheres.get(i).k[k][0]=new Vect3D(0,0,0);
	      Vect3D k1,k2,k3,k4;
	      if(k==0)
	      {
		k3=new Vect3D(0,0,0);
		
	      }
	      else if(k==1)
	      {
		k3=spheres.get(i).k[0][0].times(0.5);
	      }
	      else if(k==2)
	      {
		k3=spheres.get(i).k[1][0].times(0.5);
	      }
	      else
	      {
		k3=spheres.get(i).k[2][0];
	      }
	      if(DOWNGRAV)
		spheres.get(i).k[k][0]=spheres.get(i).k[k][0].addVect((new Vect3D(0,DOWNGRAVCONST,0)).times(timeInterval));
	      spheres.get(i).k[k][1]=spheres.get(i).vect.addVect(k3).times(timeInterval);
	      if(MAGFORCE)
		spheres.get(i).magForce(magField,k3,k,timeInterval);
	      spheres.get(i).airRes(AIRRES,k3,k,timeInterval);
	      for(int j=i+1;j<spheres.size();j++)
	      {
		  if(k==0)
		  {
		    k1=new Vect3D(0,0,0);
		    k2=new Vect3D(0,0,0);
		    k4=new Vect3D(0,0,0);
		  }
		  else if(k==1)
		  {
		    k1=spheres.get(i).k[0][1].times(0.5);
		    k2=spheres.get(j).k[0][1].times(0.5);
		    k4=spheres.get(j).k[0][0].times(0.5);
		  }
		  else if(k==2)
		  {
		    k1=spheres.get(i).k[1][1].times(0.5);
		    k2=spheres.get(j).k[1][1].times(0.5);
		    k4=spheres.get(j).k[1][0].times(0.5);
		  }
		  else
		  {
		    k1=spheres.get(i).k[2][1];
		    k2=spheres.get(j).k[2][1];
		    k4=spheres.get(j).k[2][0];
		  }
		  if(spheres.get(i).STATIONARY)
		    k1=new Vect3D(0,0,0);
		  if(spheres.get(j).STATIONARY)
		    k2=new Vect3D(0,0,0);
		  if(GRAVITATION&&spheres.get(i).GRAVCONST>0.0)
		      spheres.get(i).gravitate(spheres.get(j),k1,k2,k,timeInterval);
		  if(STATICFORCE)
		      spheres.get(i).staticForce(spheres.get(j),k1,k2,k,timeInterval);
		  spheres.get(i).magForce(spheres.get(j),k3,k1,k4,k2,k,timeInterval);
	      }
	    }
	  }
	  for(VSphere s: spheres)
	  {
	    Vect3D cStep=(s.k[0][1].addVect(s.k[1][1].times(2)).addVect(s.k[2][1].times(2)).addVect(s.k[3][1])).times(1.0/6);
	    if(!s.STATIONARY&&!Double.isNaN(cStep.getMagSquared()))
	      s.center=new Point3D((new Vect3D(s.center)).addVect(cStep));
	    Vect3D vstep = (s.k[0][0].addVect(s.k[1][0].times(2)).addVect(s.k[2][0].times(2)).addVect(s.k[3][0])).times(1.0/6);
	    if(!Double.isNaN(vstep.getMagSquared()))
	      s.vect=s.vect.addVect(vstep);
	    if(DRAWSPHERES)
	      s.lines=s.getSphere(unit).lines;
	    s.path.add(s.center);
	    s.accel=new Vect3D(0,0,0);
	  }
	}

	public void paint(Graphics p)
	{
		if(init)
		{

			WAVELENGTH=Math.PI*2/WAVELENGTH;
			points3= new ArrayList<Point3D>();
			Dimension appletSize = this.getSize();
  	 		XBOUND = appletSize.height;
   			YBOUND = appletSize.width;
	   		pos=new Point3D(-YBOUND/2,-XBOUND/2,-(Math.pow(2,10))*2);

	   		image = new BufferedImage(YBOUND,XBOUND,BufferedImage.TYPE_3BYTE_BGR);

	   		shader= new PixelShader(image.getGraphics(),XBOUND,YBOUND,VIEWDIST);

	   		menu=new NumMenu(image.getGraphics(),mLabels.split(",").length,XBOUND,YBOUND,XBOUND/2+20,50,100,30);
			bar=new ToggleBar(image.getGraphics(),bLabels.split(",").length,XBOUND+20,YBOUND/2,80,30);
			mBar=new ToggleBar(image.getGraphics(),mbLabels.split(",").length,50,YBOUND-40,75,30);
			sMenu=new SelectMenu(image.getGraphics(),smLabels.split(",").length,XBOUND/2,0,50,20);
			aMenu=new ActionMenu(image.getGraphics(),aLabels.split(",").length,YBOUND-100,XBOUND/2,100,30);
			saMenu=new ActionMenu(image.getGraphics(),saLabels.split(",").length,YBOUND*3/8,XBOUND/2,50,30);

			menu.numBars.get(0).setRange(0,5000);
	   		menu.numBars.get(1).setRange(0,.1);
	   		menu.numBars.get(2).setRange(0,50);
	   		menu.numBars.get(3).setRange(.01,300);
	   		menu.numBars.get(4).setRange(0,500);
	   		menu.numBars.get(5).setRange(0,1000000);
	   		menu.numBars.get(6).setRange(.001,1000);
	   		menu.numBars.get(7).setRange(0,50000);
	   		menu.numBars.get(8).setRange(6,31);
	   		menu.numBars.get(9).setRange(0,.01);
	   		menu.numBars.get(10).setRange(-1.5,3);

	   		menu.numBars.get(3).value=100;
	   		menu.numBars.get(4).value=100;
	   		menu.numBars.get(6).value=1000;
	   		menu.numBars.get(8).value=10;

	   		mBar.buttons.get(0).toggled=true;

			selectMenu=new NumMenu(image.getGraphics(),sLabels.split(",").length,XBOUND,YBOUND,XBOUND/2,YBOUND*5/8,100,30);
			selectMenu.numBars.get(0).setRange(.5,50);
			selectMenu.numBars.get(1).setRange(1,300);

			menu.setLabels(mLabels);
			bar.setLabels(bLabels);
			mBar.setLabels(mbLabels);
			sMenu.setLabels(smLabels);
			aMenu.setLabels(aLabels);
			saMenu.setLabels(saLabels);

			selectMenu.setLabels(sLabels);

	   		/*for(double i=-NUM;i<NUM;i+=1)
	   			for(double j=-NUM;j<NUM;j+=1)
	   			{
	   				if(!function1(i,j).equals(Double.NaN))
	   				{
	   					shapes.add(new PointP(new Point3D(i,j,function1(i,j))));
	   					shapes.get(shapes.size()-1).color=getColor(shapes.get(shapes.size()-1).lines.get(0).point1.z);
	   				}
	   				if(!function2(i,j).equals(Double.NaN)&&DRAWNEGATIVE)
	   				{
	   					shapes.add(new PointP(new Point3D(i,j,-function2(i,j))));
	   					shapes.get(shapes.size()-1).color=getColor(shapes.get(shapes.size()-1).lines.get(0).point1.z);
	   				}

	   			}*/
	   		spheres.add(new VSphere(new Point3D(0,50,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(1,.8,.3)),BOUND/2));
	   		spheres.get(0).CHARGE=getRandCharge();
			int size=shapes.size();
	   	/*	for(int i=0;i<size;i++)
	   		{
	   			Cube c = new Cube(shapes.get(i).getCenter(),RADIUS);
	   			c.makeCenter();
	   			shapes.add(c);
	   		}*/
			init=false;
			this.addKeyListener(this);
			this.addMouseListener( this );
	 		this.addMouseMotionListener( this );

		}
		
		iPage=image.getGraphics();
		shader.page=iPage;

		iPage.setColor(Color.WHITE);
		iPage.fillRect(0,0,YBOUND,XBOUND);
		
		if(gridNum!=spheres.size())
		  GRIDMODE=false;

		int action=aMenu.getValue();
		if(action==0)
		{
			int count=0;
   			VSphere add = new VSphere(new Point3D((Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS)),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL)),BOUND/2);
   			add.pathColor=randColor();
			if(mode==2){
				add.STATIONARY=true;
				add.color=Color.GREEN;
				add.MASS*=STATIONARYMASS;
			}
			if(mode==3)
			{
				add.color=Color.BLUE;
				add.RANDOM=true;
			}

			if(spheres.size()<=2)
			{
				spheres.add(add);
			}
			else
			{
				spheres.add(add);
			}
			for(int i=0;i<spheres.size()-1;i++)
					if(i!=spheres.size()-1&&((spheres.get(i).getDist(spheres.get(spheres.size()-1))<=
						spheres.get(i).RADIUS+spheres.get(spheres.size()-1).RADIUS+MINDIST+BONDDIST||
						((new Vect3D(new Point3D(0,0,0),spheres.get(spheres.size()-1).center)).getMag()>=BOUND/2-spheres.get(spheres.size()-1).RADIUS)&&
						UNIVERSEMODE==1)||(UNIVERSEMODE==2&&Math.sqrt(Math.pow(spheres.get(spheres.size()-1).center.x,2)+Math.pow(spheres.get(spheres.size()-1).center.y,2))>=
						BOUND/2-RADIUS)))
					{
						spheres.remove(spheres.size()-1);
						i=-1;
						count++;
						if(count>randTries)
							break;
						add = new VSphere(new Point3D((Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS)),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL)),BOUND/2);
						spheres.add(add);
					}
			spheres.get(spheres.size()-1).CHARGE=getRandCharge();
		}
		if(action==1)
		{
			if(spheres.size()>=1)
   			{
   			for(int i=0;i<links.size();i++)
   				if(links.get(i).contains(spheres.get(spheres.size()-1)))
   				{
   					links.remove(i);
   					i--;
   				}
   			spheres.remove(spheres.size()-1);
   			}
		}
		if(action==2)
		{
			spheres=new ArrayList<VSphere>();
   			links=new ArrayList<Link3D>();
		}
		if(action==3)
		{
			for(VSphere vs : spheres)
   				vs.vect.multiply(0);
		}
		if(action==4)
		{
			for(VSphere s : spheres)
   				s.path=new ArrayList<Point3D>();
   			cPaths=new ArrayList<LineArray>();
   			cmPath=new ArrayList<Point3D>();
		}
		if(action==5)
		{
			for(int i=1;i<=COUNTER;i++)
   			{
   				Point3D hi = new Point3D(0,-TOPHEIGHT/COUNTER*i,0);
   				double ri = TOPRADIUS/COUNTER*i;
   				RegularPolygon rP = new RegularPolygon(hi,ri,COUNTER);
   				rP.rotate(hi,0,0,Math.PI/2);
   				ArrayList<Point3D> vPoints = rP.getPoints();
   				for(Point3D pt : vPoints)
   				{
   					Vect3D vel=(new Vect3D(hi,pt)).getXZPerp1();
   					vel.setMag(TOPANGVEL*ri);
   					VSphere add = new VSphere(pt,RADIUS,NUMSIDES,vel,BOUND/2);
   					add.pathColor=new Color(125,0,255);
   					spheres.add(add);

   				}

   			}
   			VSphere add = new VSphere(new Point3D(0,0,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   			add.STATIONARY=true;
			add.color=Color.GREEN;
			spheres.add(add);
   			for(int j=spheres.size()-COUNTER*COUNTER-1;j<spheres.size();j++)
					for(int k=j+1;k<spheres.size();k++)
						links.add(new Link3D(spheres.get(j),spheres.get(k)));

			for(int j=spheres.size()-COUNTER*COUNTER-1;j<spheres.size();j++)
			{
				PointP cent = new PointP(spheres.get(j).center);
				cent.rotate(new Point3D(0,0,0),TOPANG,0,0);
				spheres.get(j).center=cent.point;
				spheres.get(j).vect.rotateVect(new Point3D(0,0,0),TOPANG,0,0);
			}
		}
		if(action==6)
		{
			int numspheres=COUNTER;
   			for(int i=0;i<numspheres;i++)
   			{
   				for(int j=0;j<numspheres;j++)
   				{
   					spheres.add(new VSphere(new Point3D((i-numspheres/2)*RADIUS*4,0,(j-numspheres/2)*RADIUS*4),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2));
   					if(j>0)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-2)));
   					if(i>0)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-1-numspheres)));
   					if((i==0||j==0)||(i==numspheres-1||j==numspheres-1))
   						spheres.get(spheres.size()-1).STATIONARY=true;
   					if(i==numspheres/2&&j==numspheres/2)
   					{
   						spheres.get(spheres.size()-1).vect=new Vect3D(new Point3D(0,0,0),new Point3D(0,IVEL,0));
   					}
   					if(i>0&&j>0)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-2-numspheres)));
   					if(i>0&&j<numspheres-1)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-numspheres)));
   				}
   			}
   			GRIDMODE=true;
   			gridNum=spheres.size();
		}
		if(action==7)
		{
			int interv=200,count=0;
   			for(int i=-COUNTER;i<=COUNTER;i++)
   				for(int j=-COUNTER/2;j<=COUNTER/2;j++)
   					for(int k=-COUNTER/4;k<=COUNTER/4;k++)
   					{
   						PointP pt=new PointP(new Point3D(i*interv,j*interv,k*interv));
   						pt.rotate(new Point3D(0,0,0),.01,0,0);
   						VSphere add = new VSphere(pt.point,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   						Vect3D direc = (new Vect3D(new Point3D(0,0,0),add.center)).getXZPerp1();
   						direc.multiply(ANGVEL);
   						add.vect=direc;
   						if(i==0&&j==0&&k==0)
   							add.STATIONARY=true;
   						spheres.add(add);
   						count++;
   					}
   			for(int j=spheres.size()-count;j<spheres.size();j++)
					for(int k=j+1;k<spheres.size();k++)
						links.add(new Link3D(spheres.get(j),spheres.get(k)));
		}

		for(ActionButton ab : aMenu.buttons)
			ab.toggled=false;

		int action2=saMenu.getValue();

		if(action2==0) {
			spheres.remove(followIndex);
			followIndex=-1;
		}

		for(ActionButton ab : saMenu.buttons)
		{
			ab.toggled=false;
		}

		shapes=new ArrayList<Polygon>();

		PHASE-=.1*MSENS/MSENSP;

		if(UNIVERSEMODE==0)
			shapes.add(new Cube(new Point3D(-BOUND/2,-BOUND/2,-BOUND/2),BOUND));
		else if(UNIVERSEMODE==1)
			shapes.add(new Sphere(new Point3D(0,0,0),BOUND/2,NUMSIDES+2));
		else if(UNIVERSEMODE==2)
			shapes.add(new Prism(new Point3D(0,0,-BOUND/2),NUMSIDES+10,BOUND/2,BOUND));

		if(FANCYMODE)
		  shapes.get(shapes.size()-1).color=new Color(0,30,255,40);
		else
		  shapes.get(shapes.size()-1).color=Color.BLACK;
		setParams();

		if(!menu.containsPoint((int)mP.x,(int)mP.y)&&!bar.containsPoint((int)mP.x,(int)mP.y)&&!selectMenu.containsPoint((int)mP.x,(int)mP.y)&&!mBar.containsPoint((int)mP.x,(int)mP.y)&&!sMenu.containsPoint((int)mP.x,(int)mP.y)&&!aMenu.containsPoint((int)mP.x,(int)mP.y))
			index=getSphereAt(new Point(mP.x,mP.y));
		if(followIndex>=spheres.size())
			followIndex=-1;
		if(index>=spheres.size())
			index=-1;
		if(prevIndex>=spheres.size())
			prevIndex=-1;
		if(prevIndex!=-1)
			spheres.get(prevIndex).label="";
		if(followIndex!=-1)
		{
			spheres.get(followIndex).label="Velocity: "+spheres.get(followIndex).vect.getMag();
			if(STATICFORCE)
				spheres.get(followIndex).label=spheres.get(followIndex).label+"\nCharge: "+spheres.get(followIndex).CHARGE;
		}
		if(index!=-1)
		{
			spheres.get(index).label="Velocity: "+spheres.get(index).vect.getMag();
			if(STATICFORCE)
				spheres.get(index).label=spheres.get(index).label+"\nCharge: "+spheres.get(index).CHARGE;
			Point3D cent=spheres.get(index).center;
			double rad=spheres.get(index).RADIUS;
			Cube add = new Cube(new Point3D(cent.x-rad,cent.y-rad,cent.z-rad),rad*2);
			add.color=new Color(255/2,0,255,50);
			shapes.add(add);
		}

		prevIndex=index;




		for(int i=0;i<spheres.size();i++)
			for(int j=i+1;j<spheres.size();j++)
			{
				if(COLLISIONS) {
					Vect3D r = new Vect3D(spheres.get(i).center,spheres.get(j).center);
					Vect3D r2=new Vect3D(r);
					r2.multiply(-1);
					if(spheres.get(i).vect.dotProduct(r)>0||spheres.get(j).vect.dotProduct(r2)>0)
						if(i!=j&&spheres.get(i).collidesWith(spheres.get(j)))
							spheres.get(i).collideSphere(spheres.get(j));
				}
				if(BONDING)
					if(i!=j&&spheres.get(i).getDist(spheres.get(j))<=spheres.get(i).RADIUS+spheres.get(j).RADIUS+BONDDIST)
					{
						boolean add=true;
						for(Link3D l : links)
							if((l.contains(spheres.get(i))&&l.contains(spheres.get(j)))||spheres.get(i).CHARGE==spheres.get(j).CHARGE&&STATICFORCE)
								add=false;
						if(add)
							links.add(new Link3D(spheres.get(i),spheres.get(j)));
					}
 				if(GRAVITATION&&spheres.get(i).GRAVCONST>0.0)
 					spheres.get(i).gravitate(spheres.get(j));
				if(STATICFORCE)
					spheres.get(i).staticForce(spheres.get(j));
				spheres.get(i).magForce(spheres.get(j));
			}
		for(VSphere s : spheres)
		{
			if(DOWNGRAV&&!RK4STEP)
			{
				if(!ROTATEGRAV)
					s.accel.y+=DOWNGRAVCONST;
				else
				{
					Vect3D direc=new Vect3D(new Point3D(0,0,0),new Point3D(0,DOWNGRAVCONST,0));
					direc.rotateVect(new Point3D(0,0,0),0,0,-gDirection.z);
					direc.rotateVect(new Point3D(0,0,0),0,-gDirection.y,0);
					//direc.rotateVect(new Point3D(0,0,0),-gDirection.x,0,0);
					s.accel=s.accel.addVect(direc);
				}

			}
			if(UNIVERSEMODE==0)
				s.bounce();
			if(UNIVERSEMODE==1)
				s.bounceSphere();
			else if(UNIVERSEMODE==2)
				s.bounceCylinder();
			if(MAGFORCE)
				s.magForce(magField);
			s.airRes(AIRRES);
		}
		for(int i=0;i<links.size();i++)
		{
			if(links.get(i).getDisplacement()>=menu.numBars.get(6).value&&BREAKING)
			{
				links.remove(i);
				i--;
			}
		}
		for(Link3D l : links)
		{
			if(!RIGID)
				l.force();
			else
				l.forceRigid();

			if(DRAWLINKS)
				shapes.add(l.getLine());
		}  
		double maxVel=0.0,maxAccel=0.0,maxAccelPerp=0.0;
		for(VSphere s : spheres)
		{
			maxAccel=Math.max(maxAccel,s.accel.getMag());
			maxAccelPerp=Math.max(maxAccelPerp,s.accelPerp.getMag());
			maxVel=Math.max(maxVel,s.vect.getMag());
//			maxVel=Math.max(maxVel,s.k[0][1].getMag());
		}

		double timeStepFactor=Math.pow(10,menu.numBars.get(10).value);
		if(VARIABLETIMESTEP) {
			if(maxVel>0.0)
				timeInterval=Math.min(timeStepFactor,2*timeStepFactor/maxVel);
			if(maxAccel>0.0)
				timeInterval=Math.min(timeInterval,18*timeStepFactor/maxAccel);
			if(maxAccelPerp>0.0)
				timeInterval=Math.min(timeInterval,4.5*timeStepFactor/maxAccelPerp);
		}
		else
			timeInterval=timeStepFactor;               
		if(RK4STEP)
		  RK4Step();
		for(VSphere s : spheres)
		{
			//s.spin();
			if(!RK4STEP)
			  s.move(timeInterval);
			if(DRAWSPHERES)
			  s.lines=s.getSphere(unit).lines;
			s.accel=new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0));
			s.accelPerp=new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0));
			if(DRAWSPHERES)
				shapes.add(s);
			if(DRAWVECTORS&&s.vect.getMag()>0.00000001)
				shapes.add(s.getVelArrow());
		//	shapes.get(shapes.size()-1).rotate(s.center,s.A1,s.A2,s.A3);
			if(DRAWPATH&&!s.STATIONARY&&DRAWSPHERES&&index==-1&&s.path.size()>PATHINTERVAL*2&&!s.STATIONARY)
			{
				LineArray lA = new LineArray(s.path,PATHINTERVAL);
				lA.color=s.pathColor;
				shapes.add(lA);
			}

		}
		if(index!=-1&&!spheres.get(index).STATIONARY)
		{
			LineArray lA = new LineArray(spheres.get(index).path,PATHINTERVAL);
			lA.color=spheres.get(index).pathColor;
			shapes.add(lA);
		}
		if(spheres.size()>=1)
			cmPath.add(getCentMass());
		if(DRAWCENTMASS&&DRAWPATH&&cmPath.size()>=PATHINTERVAL)
			shapes.add(new LineArray(cmPath,PATHINTERVAL));
		if(DRAWCENTMASS&&spheres.size()>0)
		{

			Point3D cm=cmPath.get(cmPath.size()-1);
			Sphere s1=new Sphere(cm,10,NUMSIDES);
			Sphere s2=new Sphere(cm,15,NUMSIDES);
			Sphere s3=new Sphere(cm,20,NUMSIDES);
			s1.color=Color.RED;
			s2.color=Color.GREEN;
			s3.color=Color.BLUE;
			shapes.add(s1);
			shapes.add(s2);
			shapes.add(s3);
			shapes.add(new Label(new Point3D(cm.x,cm.y-40,cm.z),"Center Of Mass"));
		}


   		if(drawAxes)
   		{
   			Arrow3D aR = new Arrow3D(new Point3D(0,0,0),5,300,0,0,0,10);
   			Arrow3D aG = new Arrow3D(new Point3D(0,0,0),5,300,-Math.PI/2,0,0,10);
   			Arrow3D aB = new Arrow3D(new Point3D(0,0,0),5,300,0,Math.PI/2,0,10);
   			aR.color=Color.RED;
   			aG.color=Color.GREEN;
   			aB.color=Color.BLUE;
   			Label xL=new Label(new Point3D(400,0,0),"X");
   			Label yL=new Label(new Point3D(0,-400,0),"Y");
   			Label zL=new Label(new Point3D(0,0,400),"Z");
   			xL.color=Color.RED;
   			yL.color=Color.GREEN;
   			zL.color=Color.BLUE;
   			shapes.add(aR);
			shapes.add(aG);
			shapes.add(aB);
			shapes.add(xL);
			shapes.add(yL);
			shapes.add(zL);
   		}

   		for(VSphere s : spheres)
   		{
			if(s.label!=null)
				shapes.add(new Label(new Point3D(s.center.x,s.center.y-100,s.center.z),s.label));
   		}
		//shapes.add(shapes.remove(0));
   		if(DRAWFIELD&&spheres.size()>0)
   		{
   			cPaths=new ArrayList<LineArray>();
   			int count=0;
   			int NUMFIELDS=3,INTERVAL=200;
   			for(VSphere vs : spheres)
   					{
   						count++;
   						Sphere sp = new Sphere(vs.center,vs.RADIUS*10,2);
   						ArrayList<Point3D> pts=sp.getPoints();
   						for(Point3D pt : pts)
   						{
							if(RK4FIELD)
							  drawFieldThroughRK4(pt);
							else
							  drawFieldThrough(pt);
   						}

   					}
   		}
   		if(DRAWEPFIELD&&spheres.size()>0)
   		{
   			cPaths=new ArrayList<LineArray>();
   			int NUMFIELDS=3,INTERVAL=800;
   			for(int i=-NUMFIELDS;i<NUMFIELDS;i++)
   				for(int j=-NUMFIELDS;j<NUMFIELDS;j++)
   					for(int k=-NUMFIELDS;k<NUMFIELDS;k++)
   						drawEPFieldThrough(new Point3D(i*INTERVAL,j*INTERVAL,k*INTERVAL));
   		}
   		if(DRAWEVECTFIELD&&spheres.size()>0)
   		{
   			ArrayList<LineP> add = new ArrayList<LineP>();
   			ArrayList<Double> addV = new ArrayList<Double>();
   			double min=Double.MAX_VALUE, max=Double.MIN_VALUE;
   			for(double i=-BOUND/2;i<=BOUND/2;i+=BOUND/COUNTER)
   				for(double j=-BOUND/2;j<=BOUND/2;j+=BOUND/COUNTER)
   					for(double k=-BOUND/2;k<=BOUND/2;k+=BOUND/COUNTER)
   					{
   						Vect3D direc = new Vect3D();
   						Point3D curr = new Point3D(i,j,k);
   						for(VSphere s : spheres)
   						{
   							Vect3D d=new Vect3D(curr,s.center);
   							d.setMag(100);
   							d.multiply(1.0/Math.pow(s.center.getDistTo(curr),2));
   							d.multiply(-s.CHARGE);
   							direc=direc.addVect(d);
   						}
   						double mag=direc.getMag();
   						addV.add(mag);
   						min=Math.min(min,mag);
   						max=Math.max(max,mag);
   						direc.setMag(10);
						LineP lp = new LineP(direc.getNegPointFrom(curr),direc.getPointFrom(curr));
   						add.add(lp);
   					}
   			for(int i=0;i<add.size();i++) {
   				add.get(i).color=getColorValue(addV.get(i),min,max);
   				shapes.add(add.get(i));
   			}
   		}
		if(DRAWFIELD)
		  for(LineArray lA : cPaths)
			  shapes.add(new LineArray(lA.points));

		if(DRAWMFIELD)
		{
			for(int i=-2;i<=2;i++)
				for(int j=-2;j<=2;j++)
				{
					Vect3D direc = new Vect3D(new Point3D(0,0,0),new Point3D(magField.x,magField.y,magField.z));
					direc.multiply(BOUND/direc.getMag());
					Point3D pt = new Point3D(i*BOUND/5,-BOUND/2,j*BOUND/5);
					LineP add = new LineP(pt,direc.getPointFrom(pt));
					shapes.add(add);

				}
		}



   		//zoom(ZOOM, getCenter());
		Matrix rotXY = new Matrix(gDirection.x,'z'), rotXZ=new Matrix(-gDirection.y,'y'), rotYZ=new Matrix(gDirection.z,'x'), rot=rotYZ.multiply(rotXZ.multiply(rotXY));
		if(followIndex==-1)
			for(Polygon pg : shapes)
				pg.rotate(new Point3D(0,0,0),rot);
		else
			for(Polygon pg : shapes)
				pg.rotate(spheres.get(followIndex).center,rot);

		if(followIndex==-1)
			pan2(new Point3D(-pos.x,-pos.y,-pos.z));
		else
			pan2(new Point3D(-spheres.get(followIndex).center.x+YBOUND/2,-spheres.get(followIndex).center.y+XBOUND/2,-spheres.get(followIndex).center.z+FOLLOWDIST));

// 		for(Polygon pg : shapes)
// 		{
// 			pg.rotate(new Point3D(pos.x+XBOUND/2,pos.y+YBOUND/2,pos.z-VIEWDIST),direction.x,direction.y,direction.z);
// 		}
		tCenters=new ArrayList<Point3D>();
		for(int i=0;i<shapes.size();i++)
		{
			if(shapes.get(i).id.equals("VSphere"))
			{	
				if(followIndex==-1)
				  tCenters.add((new Matrix(((VSphere)(shapes.get(i))).center.getRotated(new Point3D(0,0,0),rot))).plus((new Matrix(pos)).times(-1)).getPoint3D());
				else {
				  Point3D ct=new Point3D(-spheres.get(followIndex).center.x+YBOUND/2,-spheres.get(followIndex).center.y+XBOUND/2,-spheres.get(followIndex).center.z+FOLLOWDIST);
				  tCenters.add((new Matrix(((VSphere)(shapes.get(i))).center.getRotated(spheres.get(followIndex).center,rot))).plus(new Matrix(ct)).getPoint3D());
				  }
			}
			if(!FANCYMODE)
			  shapes.get(i).printPolygon(iPage,new Point(YBOUND/2,XBOUND/2),VIEWDIST);
		}
		if(FANCYMODE)
		    shader.drawDepthSorted(shapes);
		String place="Error";
		if(mode==1)
			place="Sphere";
		else if(mode==2)
			place="Stationary Sphere";
		else if(mode==3)
			place="Random Sphere";

		iPage.setColor(Color.BLACK);

		iPage.drawString((MSENS/MSENSP)+"X Movement Speed",25,25);
		iPage.drawString("Path Interval: "+PATHINTERVAL,25,40);
		iPage.drawString(NUMSIDES+" Sided "+place,25,55);
		iPage.drawString("Radius: "+RADIUS,25,70);
		iPage.drawString(spheres.size()+" Spheres",25,85);
		iPage.drawString("Counter: "+COUNTER+"",25,100);
		if(RK4STEP)
		  iPage.drawString("Runge-Kutta 4",25,115);
		else
		  iPage.drawString("Forward Euler",25,115);
		if(SHOWSTATS)
		{
			//iPage.drawString((time-timeP)+"ns per frame",YBOUND-200,50);
			iPage.drawString(1.0/((time-timeP)*Math.pow(10,-9))+" fps",YBOUND-200,50);
			gPoints.add(new Point(numP,1.0/((time-timeP)*Math.pow(10,-9))));
			if(gPoints.size()>MAXPOINTS)
			{
				gPoints.remove(1);
				gPoints.get(0).x=numP-MAXPOINTS;
			}

			Graph graph=new Graph(iPage,new DataSet(gPoints));
			graph.printGraph(new Point(YBOUND-200,65),200,200);
			numP++;
		}
		if(PRINTMENU)
		{
			menu.printMenu();
			bar.print();
			sMenu.print();
			aMenu.print();
			if(followIndex!=-1)
			{
				selectMenu.numBars.get(0).value=spheres.get(followIndex).MASS;
				selectMenu.numBars.get(1).value=spheres.get(followIndex).RADIUS;
				selectMenu.printMenu();
				saMenu.print();
			}
		}
		mBar.printTrans();


		p.drawImage(image,0,0,new Color(255,255,255),null);
		timeP=time;
		time=System.nanoTime();

	//	if(SHOWSTATS)
			repaint();

	}

	public void stop()
	{

	}
	
	public void outputInfo()
	{
	  for(int i=0;i<spheres.size();i++)
	    System.out.println(i+":\t"+spheres.get(i)+"\n");
	  if(spheres.size()>0) {
	    System.out.println("Timestep: "+timeInterval);
	    System.out.println("-----------------------------------------------------");
	  }
	}

   public void keyPressed(KeyEvent e) {

   		if(e.getKeyCode()==KeyEvent.VK_PAGE_UP)
   		{
   		//	for(Polygon pg : shapes)
   			pan(new Point3D(0,10*MSENS,0));
   		}
   		if(e.getKeyCode()==KeyEvent.VK_A)
   		{
   		//	for(Polygon pg : shapes)
   			pan(new Point3D(10*MSENS,0,0));
   		}
   		if(e.getKeyCode()==KeyEvent.VK_E)
   		{
		  RK4FIELD=!RK4FIELD;
		}
		if(e.getKeyCode()==KeyEvent.VK_O)
   		{
		  outputInfo();
		}
		if(e.getKeyCode()==KeyEvent.VK_I)
   		{
		  RK4STEP=!RK4STEP;
		  if(RK4STEP)
		    System.out.println("Using Runge-Kutta 4");
		  else
		    System.out.println("Using Forward Euler");
		}
   		if(e.getKeyCode()==KeyEvent.VK_PAGE_DOWN)
   		{
   		//	for(Polygon pg : shapes)
   			pan(new Point3D(0,-10*MSENS,0));
   		}
   		if(e.getKeyCode()==KeyEvent.VK_Z)
   		{
   			for(VSphere vs : spheres)
   				vs.vect.multiply(0);
   		}
   		if(e.getKeyCode()==KeyEvent.VK_0)
   		{
   			COUNTER++;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_9)
   		{
   			if(COUNTER>2)
   				COUNTER--;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_8)
   		{
   			for(VSphere s : spheres)
   				s.CHARGE=1;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_M)
   		{
   			PRINTMENU=!PRINTMENU;
   			mBar.buttons.get(0).toggled=!mBar.buttons.get(0).toggled;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_2)
   		{
   			PATHLENGTH+=250;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_1)
   		{
   			if(PATHLENGTH>250)
   				PATHLENGTH-=250;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_C)
   		{
   			for(VSphere s : spheres)
   				s.path=new ArrayList<Point3D>();
   			cPaths=new ArrayList<LineArray>();
   			cmPath=new ArrayList<Point3D>();
   		}
   		if(e.getKeyCode()==KeyEvent.VK_Q)
   		{
   			spheres=new ArrayList<VSphere>();
   			links=new ArrayList<Link3D>();
   		}
   		if(e.getKeyCode()==KeyEvent.VK_D)
   		{
   		//	for(Polygon pg : shapes)
   			pan(new Point3D(-10*MSENS,0,0));
   		}
   		if(e.getKeyCode()==KeyEvent.VK_W)
   		{
   		//	for(Polygon pg : shapes)
   			if(followIndex==-1)
   				pan(new Point3D(0,0,-10*MSENS));
   			else if(FOLLOWDIST>50)
   				FOLLOWDIST-=50;

   		}
   		if(e.getKeyCode()==KeyEvent.VK_S)
   		{
   		//	for(Polygon pg : shapes)
   			if(followIndex==-1)
   				pan(new Point3D(0,0,10*MSENS));
   			else
   				FOLLOWDIST+=50;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_L)
   		{
		  for(Link3D l : links)
		    l.eD/=4;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_LEFT)
   		{
   			gDirection.y+=.1*ASENS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_RIGHT)
   		{
   			gDirection.y-=.1*ASENS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_UP)
   		{
   			gDirection.z+=.1*ASENS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_DOWN)
   		{
   			gDirection.z-=.1*ASENS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_P)
   		{
   			VSphere add = new VSphere(new Point3D(0,0,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND);
   			add.STATIONARY=true;
			add.color=Color.GREEN;
			add.MASS*=STATIONARYMASS;
   			spheres.add(add);
   			for(int i=1;i<COUNTER;i++)
   			{
   				if(i!=COUNTER-1)
   					spheres.add(new VSphere(new Point3D(0,(RADIUS+BONDDIST)*i,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND));
   				else
   					spheres.add(new VSphere(new Point3D(0,(RADIUS+BONDDIST)*i,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(IVEL,0,0)),BOUND));
   			}
   			for(int i=1;i<=COUNTER;i++)
   				links.add(new Link3D(spheres.get(spheres.size()-i),spheres.get(spheres.size()-i-1)));
   		}
   		if(e.getKeyCode()==KeyEvent.VK_R)
   		{
   			int count=0;
   			VSphere add = new VSphere(new Point3D((Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS)),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL)),BOUND/2);
   			add.pathColor=randColor();
			if(mode==2){
				add.STATIONARY=true;
				add.color=Color.GREEN;
				add.MASS*=STATIONARYMASS;
			}
			if(mode==3)
			{
				add.color=Color.BLUE;
				add.RANDOM=true;
			}

			if(spheres.size()<=2)
			{
				spheres.add(add);
			}
			else
			{
				spheres.add(add);
			}
			for(int i=0;i<spheres.size()-1;i++)
					if(i!=spheres.size()-1&&((spheres.get(i).getDist(spheres.get(spheres.size()-1))<=
						spheres.get(i).RADIUS+spheres.get(spheres.size()-1).RADIUS+MINDIST+BONDDIST||
						((new Vect3D(new Point3D(0,0,0),spheres.get(spheres.size()-1).center)).getMag()>=BOUND/2-spheres.get(spheres.size()-1).RADIUS)&&
						UNIVERSEMODE==1)||(UNIVERSEMODE==2&&Math.sqrt(Math.pow(spheres.get(spheres.size()-1).center.x,2)+Math.pow(spheres.get(spheres.size()-1).center.y,2))>=
						BOUND/2-RADIUS)))
					{
						spheres.remove(spheres.size()-1);
						i=-1;
						count++;
						if(count>randTries)
							break;
						add = new VSphere(new Point3D((Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS),(Math.random()*2-1)*(BOUND/2-RADIUS)),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL,Math.random()*IVEL*2-IVEL)),BOUND/2);
						spheres.add(add);
					}
			spheres.get(spheres.size()-1).CHARGE=getRandCharge();
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD8)
   		{
   			MSENS*=2;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD2)
   		{
   			MSENS/=2;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
   		{
   			if(spheres.size()>=1)
   			{
   			for(int i=0;i<links.size();i++)
   				if(links.get(i).contains(spheres.get(spheres.size()-1)))
   				{
   					links.remove(i);
   					i--;
   				}
   			spheres.remove(spheres.size()-1);
   			}
   		}
   		if(e.getKeyCode()==KeyEvent.VK_ENTER)
   		{
   			DRAWPATH=!DRAWPATH;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_B)
   		{
   			int interv=200,count=0;
   			for(int i=-COUNTER;i<=COUNTER;i++)
   				for(int j=-COUNTER/2;j<=COUNTER/2;j++)
   					for(int k=-COUNTER/4;k<=COUNTER/4;k++)
   					{
   						PointP pt=new PointP(new Point3D(i*interv,j*interv,k*interv));
   						pt.rotate(new Point3D(0,0,0),.01,0,0);
   						VSphere add = new VSphere(pt.point,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   						Vect3D direc = (new Vect3D(new Point3D(0,0,0),add.center)).getXZPerp1();
   						direc.multiply(ANGVEL);
   						add.vect=direc;
   						if(i==0&&j==0&&k==0)
   							add.STATIONARY=true;
   						spheres.add(add);
   						count++;
   					}
   			for(int j=spheres.size()-count;j<spheres.size();j++)
					for(int k=j+1;k<spheres.size();k++)
						links.add(new Link3D(spheres.get(j),spheres.get(k)));
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD6)
   		{
   			NUMSIDES++;
   			unit = new Sphere(new Point3D(0,0,0),1,NUMSIDES);
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD4)
   		{
   			if(NUMSIDES>2)
   			NUMSIDES--;
   			unit = new Sphere(new Point3D(0,0,0),1,NUMSIDES);
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD7)
   		{
   			RADIUS+=25;
   			for(VSphere s : spheres)
   				s.RADIUS=RADIUS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD1)
   		{
   			if(RADIUS>5)
   			RADIUS-=25;
   			for(VSphere s : spheres)
   				s.RADIUS=RADIUS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_7)
   		{
   			for(Link3D l : links)
   				l.COLORCONST/=2;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_8)
   		{
   			for(Link3D l : links)
   				l.COLORCONST*=2;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F)
   		{
   			FANCYMODE=!FANCYMODE;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F1)
   		{
   			SHOWSTATS=!SHOWSTATS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F2)
   		{
   			drawAxes=!drawAxes;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F3)
   		{
   			DRAWFIELD=!DRAWFIELD;

   		}
   		if(e.getKeyCode()==KeyEvent.VK_F4)
   		{
   			DRAWEPFIELD=!DRAWEPFIELD;

   		}
   		if(e.getKeyCode()==KeyEvent.VK_F5)
   		{
   			Point3D cent=new Point3D(0,-1000,0);
   			RegularPolygon rp = new RegularPolygon(cent,1000,COUNTER);
   			rp.rotate(cent,0,0,Math.PI/2);
   			ArrayList<Point3D> pts = rp.getPoints();
   			for(Point3D pt : pts)
   			{
   				VSphere add=new VSphere(pt,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   				spheres.add(add);
   			}
   			rp.pan(new Point3D(0,2000,0));
   			pts = rp.getPoints();
   			for(Point3D pt : pts)
   			{
   				VSphere add=new VSphere(pt,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   				add.CHARGE=-1;
   				spheres.add(add);
   			}
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F6)
   		{
   			int numspheres=COUNTER;
   			for(int i=0;i<numspheres;i++)
   			{
   				for(int j=0;j<numspheres;j++)
   				{
   					spheres.add(new VSphere(new Point3D((i-numspheres/2)*RADIUS*4,0,(j-numspheres/2)*RADIUS*4),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2));
   					if(j>0)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-2)));
   					if(i>0)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-1-numspheres)));
   					if((i==0||j==0)||(i==numspheres-1||j==numspheres-1))
   						spheres.get(spheres.size()-1).STATIONARY=true;
   					if(i==numspheres/2&&j==numspheres/2)
   					{
   						spheres.get(spheres.size()-1).vect=new Vect3D(new Point3D(0,0,0),new Point3D(0,IVEL,0));
   					}
   					if(i>0&&j>0)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-2-numspheres)));
   					if(i>0&&j<numspheres-1)
   						links.add(new Link3D(spheres.get(spheres.size()-1),spheres.get(spheres.size()-numspheres)));
   				}
   			}
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F7)
   		{
   			double size=BOUND/2;
   			spheres.add(new VSphere(new Point3D(-size,size,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2));
   			spheres.add(new VSphere(new Point3D(-size,-size,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2));
   			spheres.get(spheres.size()-1).CHARGE=-1;
   			spheres.add(new VSphere(new Point3D(size,size,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2));
   			spheres.get(spheres.size()-1).CHARGE=-1;
   			spheres.add(new VSphere(new Point3D(size,-size,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2));
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F12)
   		{
   			DRAWSPHERES=!DRAWSPHERES;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_F11)
   		{
   			DRAWLINKS=!DRAWLINKS;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD9)
   		{
   			if(menu.numBars.get(8).value<menu.numBars.get(8).rangeC)
   				menu.numBars.get(8).value+=1;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_NUMPAD3)
   		{
   			if(menu.numBars.get(8).value>menu.numBars.get(8).rangeF)
   				menu.numBars.get(8).value-=1;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_SPACE)
   		{
   			mode++;
   			if(mode>3)
   				mode=1;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_3&&PATHINTERVAL>1)
   		{
   			PATHINTERVAL--;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_4)
   		{
   			PATHINTERVAL++;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_T)
   		{
   			for(int i=1;i<=COUNTER;i++)
   			{
   				Point3D hi = new Point3D(0,-TOPHEIGHT/COUNTER*i,0);
   				double ri = TOPRADIUS/COUNTER*i;
   				RegularPolygon rP = new RegularPolygon(hi,ri,COUNTER);
   				rP.rotate(hi,0,0,Math.PI/2);
   				ArrayList<Point3D> vPoints = rP.getPoints();
   				for(Point3D pt : vPoints)
   				{
   					Vect3D vel=(new Vect3D(hi,pt)).getXZPerp1();
   					vel.setMag(TOPANGVEL*ri);
   					VSphere add = new VSphere(pt,RADIUS,NUMSIDES,vel,BOUND/2);
   					add.pathColor=new Color(125,0,255);
   					spheres.add(add);

   				}

   			}
   			VSphere add = new VSphere(new Point3D(0,0,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   			add.STATIONARY=true;
			add.color=Color.GREEN;
			spheres.add(add);
   			for(int j=spheres.size()-COUNTER*COUNTER-1;j<spheres.size();j++)
					for(int k=j+1;k<spheres.size();k++)
						links.add(new Link3D(spheres.get(j),spheres.get(k)));

			for(int j=spheres.size()-COUNTER*COUNTER-1;j<spheres.size();j++)
			{
				PointP cent = new PointP(spheres.get(j).center);
				cent.rotate(new Point3D(0,0,0),TOPANG,0,0);
				spheres.get(j).center=cent.point;
				spheres.get(j).vect.rotateVect(new Point3D(0,0,0),TOPANG,0,0);
			}

   		}
   		if(e.getKeyCode()==KeyEvent.VK_V)
   		{
   			DRAWEVECTFIELD=!DRAWEVECTFIELD;
   		}

   		if(e.getKeyCode()==KeyEvent.VK_U)
   		{
   			/*Sphere s = new Sphere(new Point3D(0,-300,0),300,COUNTER);
   			ArrayList<Point3D> pts = s.getPoints();
   			for(Point3D pt : pts)
   			{
   				Vect3D v = new Vect3D(new Point3D(0,pt.y,0),pt);
   				v=v.getXZPerp1();
   				v.setMag(TOPANGVEL*Math.sqrt(pt.x*pt.x+pt.z*pt.z));
   				VSphere add = new VSphere(pt,RADIUS,NUMSIDES,v,BOUND/2);
   				spheres.add(add);
   			}
   			for(int j=spheres.size()-pts.size();j<spheres.size();j++)
					for(int k=j+1;k<spheres.size();k++)
						links.add(new Link3D(spheres.get(j),spheres.get(k)));*/

			UNIVERSEMODE++;
			if(UNIVERSEMODE>1)
				UNIVERSEMODE=0;
   		}
   		if(e.getKeyCode()==KeyEvent.VK_Y)
   		{
   			for(int i=1;i<=COUNTER;i++)
   			{
   				Point3D hi = new Point3D(0,-TOPHEIGHT/COUNTER*i,0);
   				double ri = TOPRADIUS/COUNTER*i;
   				RegularPolygon rP = new RegularPolygon(hi,ri,COUNTER);
   				rP.rotate(hi,0,0,Math.PI/2);
   				ArrayList<Point3D> vPoints = rP.getPoints();
   				for(Point3D pt : vPoints)
   				{
   					Vect3D vel=(new Vect3D(hi,pt)).getXZPerp1();
   					vel.setMag(TOPANGVEL*ri);
   					VSphere add = new VSphere(pt,RADIUS,NUMSIDES,vel,BOUND/2);
   					add.pathColor=new Color(125,0,255);
   					spheres.add(add);

   				}

   			}
   			VSphere add = new VSphere(new Point3D(0,0,0),RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   			add.STATIONARY=true;
			add.color=Color.GREEN;
			spheres.add(add);
   			for(int j=spheres.size()-COUNTER*COUNTER-1;j<spheres.size();j++)
					for(int k=j+1;k<spheres.size();k++)
						links.add(new Link3D(spheres.get(j),spheres.get(k)));

			for(int j=spheres.size()-COUNTER*COUNTER-1;j<spheres.size();j++)
			{
				PointP cent = new PointP(spheres.get(j).center);
				cent.rotate(new Point3D(0,0,0),TOPANG,0,0);
				spheres.get(j).center=cent.point;
				spheres.get(j).vect.rotateVect(new Point3D(0,0,0),TOPANG,0,0);
			}
			VSphere rem = spheres.remove(spheres.size()-4);
			for(int i=0;i<links.size();i++)
				if(links.get(i).contains(rem))
				{
					links.remove(i);
					i--;
				}
			rem=spheres.remove(spheres.size()-4);
				for(int i=0;i<links.size();i++)
				if(links.get(i).contains(rem))
				{
					links.remove(i);
					i--;
				}


   		}

//   		try {
//				Thread.sleep(INPUTDELAY);
//				}
//			catch(InterruptedException exc) {
//				}
   		repaint();

	}

	public void keyReleased(KeyEvent e) {

  	}

	public void keyTyped(KeyEvent e) {

	}

	public ArrayList<Point> getScreenPoints(ArrayList<Point3D> points, Point p)
	{
		ArrayList<Point> out= new ArrayList<Point>();
		for(int i=0;i<points.size();i++)
			{
				Vect direc= new Vect(p,new Point(points.get(i).x,points.get(i).y));
				if(points.get(i).z>=0)
				{
					direc.multiply(VIEWDIST/(VIEWDIST+points.get(i).z));
					out.add(direc.getPointFrom(p));
				}
			}
		return out;
	}

	public void pan(Point3D d)
	{
		pos.x-=d.x;
		pos.y-=d.y;
		pos.z-=d.z;
	}

	public void pan2(Point3D d)
	{
		for(Polygon p : shapes)
			p.pan(d);
	}

	public void update( Graphics g ) {
		paint(g);
   }

	public void mouseEntered( MouseEvent e ) {
      // called when the pointer enters the applet's rectangular area
   }
   public void mouseExited( MouseEvent e ) {
      // called when the pointer leaves the applet's rectangular area
   }
   public void mouseClicked( MouseEvent e ) {

		if(followIndex!=-1)
   			spheres.get(followIndex).label="";
		if(index!=-1)
			followIndex=index;
		else if(!menu.containsPoint((int)mP.x,(int)mP.y)&&!bar.containsPoint((int)mP.x,(int)mP.y)&&!selectMenu.containsPoint((int)mP.x,(int)mP.y)&&!mBar.containsPoint((int)mP.x,(int)mP.y)&&!sMenu.containsPoint((int)mP.x,(int)mP.y)&&!aMenu.containsPoint((int)mP.x,(int)mP.y))
			followIndex=-1;

   }
   public void mousePressed( MouseEvent e ) {  // called after a button is pressed down
   		if(PRINTMENU)
   		{
   			menu.toggle(e.getX(),e.getY());
			bar.toggle(e.getX(),e.getY());
			sMenu.toggle(e.getX(),e.getY());
			aMenu.toggle(e.getX(),e.getY());
			saMenu.toggle(e.getX(),e.getY());
			selectMenu.toggle(e.getX(),e.getY());
   		}
   		mBar.toggle(e.getX(),e.getY());
		if(followIndex!=-1&&(selectMenu.containsPoint(e.getX(),e.getY()))&&PRINTMENU)
		{
			spheres.get(followIndex).MASS=selectMenu.numBars.get(0).value;
			spheres.get(followIndex).RADIUS=selectMenu.numBars.get(1).value;
		}

		repaint();
   }
   public void mouseReleased( MouseEvent e ) {  // called after a button is released


	}
   public void mouseMoved( MouseEvent e ) {  // called during motion when no buttons are down

		mPP=mP;
		mP=new Point(e.getX(),e.getY());
		bar.select(e.getX(),e.getY());
		mBar.select(e.getX(),e.getY());
		sMenu.select(e.getX(),e.getY());
		aMenu.select(e.getX(),e.getY());
		saMenu.select(e.getX(),e.getY());
		//System.out.println(index);


   }
   public void mouseDragged( MouseEvent e ) {  // called during motion with buttons down

	pan(new Point3D(0,0,0));
	mPP=mP;
	mP=new Point(e.getX(),e.getY());
	if(!menu.containsPoint((int)mP.x,(int)mP.y)&&!bar.containsPoint((int)mP.x,(int)mP.y)&&!selectMenu.containsPoint((int)mP.x,(int)mP.y)&&!mBar.containsPoint((int)mP.x,(int)mP.y)&&!sMenu.containsPoint((int)mP.x,(int)mP.y)&&!aMenu.containsPoint((int)mP.x,(int)mP.y))
	{
		gDirection.y+=SENSITIVTY*(mP.x-mPP.x);
		gDirection.z+=(mP.y-mPP.y)*SENSITIVTY;					// Look Control
	}
	else if(PRINTMENU)
	{
		menu.toggle(e.getX(),e.getY());
		selectMenu.toggle(e.getX(),e.getY());
		saMenu.toggle(e.getX(),e.getY());
		if(followIndex!=-1&&(selectMenu.containsPoint(e.getX(),e.getY()))&&PRINTMENU)
		{
			spheres.get(followIndex).MASS=selectMenu.numBars.get(0).value;
			spheres.get(followIndex).RADIUS=selectMenu.numBars.get(1).value;
		}
	}


   	repaint();

   }

   public Point3D getCenter()
   {
   		double sumx=0,sumy=0,sumz=0;
   		for(Polygon p : shapes)
   		{
   			Point3D pt=p.getCenter();
   			sumx+=pt.x;
   			sumy+=pt.y;
   			sumz+=pt.z;
   		}
   		return new Point3D(sumx/shapes.size(),sumy/shapes.size(),sumz/shapes.size());
   }

   public Point3D rotate(Point3D pt, Point3D cent, double ang1, double ang2, double ang3)
   {
   		Point3D out=pt;
   		Vect xy=new Vect(new Point(cent.x,cent.y),new Point(pt.x,pt.y));
   		xy=new Vect(xy.getAngle()+ang1,xy.getMag());
   		out.x=pt.x+xy.getX();
		out.y=pt.y+xy.getY();

		Vect xz=new Vect(new Point(cent.x,cent.z),new Point(pt.x,pt.z));
   		xz=new Vect(xz.getAngle()+ang2,xz.getMag());
   		out.x=pt.x+xz.getX();
		out.z=pt.z+xz.getY();

		Vect yz=new Vect(new Point(cent.y,cent.z),new Point(pt.y,pt.z));
   		yz=new Vect(yz.getAngle()+ang3,yz.getMag());
   		out.y=pt.y+yz.getX();
		out.z=pt.z+yz.getY();

		return out;
   }

   public void zoom(double z,Point3D pt)
   {
		for(Polygon pg : shapes)
		{
			for(Line3D line : pg.lines )
			{
				Vect3D direc= new Vect3D(pt,line.point1);
				direc.multiply(z);
				line.point1=direc.getPointFrom(pt);

				direc= new Vect3D(pt,line.point2);
				direc.multiply(z);
				line.point2=direc.getPointFrom(pt);
			}
		}
   }
   public double getRandCharge()
   {
   		int out=(int)(Math.random()*2);
   		if(out==1)
   			return 1;
   		else if(out==0)
   			return -1;
   		return 0;
   }
   public void setParams()
   {
   	BOUND=(int)(Math.pow(2,menu.numBars.get(8).value)-1);
   	boolean [] vals=bar.getValues();
   	for(VSphere s : spheres)
   	{
   		s.BOUND=BOUND/2;
   		if(!STATICFORCE&&!s.STATIONARY)
   			s.color=Color.RED;
   		else if(!STATICFORCE&&s.STATIONARY)
   			s.color=Color.GREEN;
   		else
   		{
   			if(s.CHARGE==1){
   				if(!s.STATIONARY)
   					s.color=Color.RED;
   				else
   					s.color=new Color(255,255,0);
   			}
   			else if(s.CHARGE==-1){
   				if(!s.STATIONARY)
   					s.color=Color.BLUE;
   				else
   					s.color=new Color(0,255,255);
   			}
   		}
   		s.DOWNGRAVCONST=DOWNGRAVCONST;
   		s.PATHSIZE=PATHLENGTH;
// 		s.RADIUS=RADIUS;
   		s.GRAVCONST=menu.numBars.get(0).value;
   		s.DOWNGRAVCONST=menu.numBars.get(1).value;
   		s.NUMSIDES=NUMSIDES;
   		s.CHARGECONST=menu.numBars.get(5).value;
   		s.PERMEABILITY=menu.numBars.get(7).value;
   	}
   	for(Link3D lk : links) {
		double val=menu.numBars.get(3).value;
		if(!GRIDMODE)
		  val/=lk.eD;
		else
		  val/=100;
		lk.FORCECONST=val;
	}
   	DOWNGRAVCONST=menu.numBars.get(1).value;
   	IVEL=menu.numBars.get(2).value;
   	BONDDIST=menu.numBars.get(4).value;
   	AIRRES=menu.numBars.get(9).value;
   	COLLISIONS=vals[0];
   	BONDING=vals[1];
   	STATICFORCE=vals[2];
   	RIGID=vals[3];
	MAGFORCE=vals[4];
	DRAWMFIELD=vals[4];
	DRAWCENTMASS=vals[5];
	DRAWEVECTFIELD=vals[6];
	DRAWFIELD=vals[7];
	drawAxes=vals[8];
	DRAWPATH=vals[9];
	SHOWSTATS=vals[10];
	DRAWSPHERES=!vals[11];
	VARIABLETIMESTEP=!vals[12];

	UNIVERSEMODE=sMenu.getValue();

	PRINTMENU=mBar.buttons.get(0).toggled;
   }

   public Color randColor()
   {
   		return new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
   }
   public Vect3D getFieldAt(Point3D pos)
   {
     Vect3D v=new Vect3D(pos),out=new Vect3D(0,0,0);
     
     for(VSphere s: spheres)
     {
       Vect3D direc = v.subtractVect(new Vect3D(s.center));
       double dSq=direc.getMagSquared();
       direc.normalize();
       direc.multiply(s.CHARGE/dSq);
       out=out.addVect(direc);
     }
     return out;
   }
   public void drawFieldThrough(Point3D point)
   {
     Vect3D pt=new Vect3D(point);
     ArrayList<Point3D> pathP=new ArrayList<Point3D>(),pathN=new ArrayList<Point3D>();
     for(int i=0;i<CHARGEITERATIONS;i++)
     {
       Vect3D field = getFieldAt(new Point3D(pt));
       field.normalize();
       pt=pt.addVect(field.times(CHARGEINTERVAL));
       pathP.add(new Point3D(pt));
     }
     pt=new Vect3D(point);
     for(int i=0;i<CHARGEITERATIONS;i++)
     {
       Vect3D field = getFieldAt(new Point3D(pt));
       field.normalize();
       pt=pt.addVect(field.times(-CHARGEINTERVAL));
       pathN.add(new Point3D(pt));
     }
     cPaths.add(new LineArray(pathP));
     cPaths.add(new LineArray(pathN));
     shapes.add(new LineP(pathP.get(0),pathN.get(0)));
   }
   public void drawFieldThroughRK4(Point3D point)
   {
     Vect3D pt=new Vect3D(point);
     ArrayList<Point3D> pathP=new ArrayList<Point3D>(),pathN=new ArrayList<Point3D>();
     for(int i=0;i<CHARGEITERATIONS;i++)
     {
       Vect3D k1 = getFieldAt(new Point3D(pt)).getNormalized().times(CHARGEINTERVAL),
		k2=getFieldAt(new Point3D(pt.addVect(k1.times(0.5)))).getNormalized().times(CHARGEINTERVAL),
		k3=getFieldAt(new Point3D(pt.addVect(k2.times(0.5)))).getNormalized().times(CHARGEINTERVAL),
		k4=getFieldAt(new Point3D(pt.addVect(k3))).getNormalized().times(CHARGEINTERVAL);
       pt=pt.addVect((k1.addVect(k2.times(2)).addVect(k3.times(2)).addVect(k4)).times(1.0/6));
       pathP.add(new Point3D(pt));
     }
     pt=new Vect3D(point);
     for(int i=0;i<CHARGEITERATIONS;i++)
     {
       Vect3D k1 = getFieldAt(new Point3D(pt)).getNormalized().times(-CHARGEINTERVAL),
		k2=getFieldAt(new Point3D(pt.addVect(k1.times(0.5)))).getNormalized().times(-CHARGEINTERVAL),
		k3=getFieldAt(new Point3D(pt.addVect(k2.times(0.5)))).getNormalized().times(-CHARGEINTERVAL),
		k4=getFieldAt(new Point3D(pt.addVect(k3))).getNormalized().times(-CHARGEINTERVAL);
       pt=pt.addVect((k1.addVect(k2.times(2)).addVect(k3.times(2)).addVect(k4)).times(1.0/6));
       pathN.add(new Point3D(pt));
     }
     cPaths.add(new LineArray(pathP));
     cPaths.add(new LineArray(pathN));
     shapes.add(new LineP(pathP.get(0),pathN.get(0)));
   }
//    public void drawFieldThrough2(Point3D point)
//    {
//    			ArrayList<Point3D> fooPathP = new ArrayList<Point3D>();
//    			ArrayList<Point3D> fooPathN = new ArrayList<Point3D>();
//    			ArrayList<Double> magP = new ArrayList<Double>(),magN = new ArrayList<Double>();
//    			double max=Double.MIN_VALUE, min=Double.MAX_VALUE;
//    			Point3D ptP=point,ptN=point;
//    			boolean bp=true;
//    			for(int i=0;i<CHARGEITERATIONS&&bp;i+=PATHINTERVAL)
//    			{
//    				VSphere fooP = new VSphere(ptP,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
//    				VSphere fooN = new VSphere(ptN,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
//    				for(VSphere s : spheres)
//    				{
//    					fooP.CHARGECONST=1000;
// 					fooP.CHARGE=1;
// 					fooP.staticForceTo(s);
// 					if(fooP.getDist(s)<3*PATHINTERVAL)
// 						bp=false;
//    					fooN.CHARGECONST=1000;
//    					fooN.CHARGE=-1;
// 					fooN.staticForceTo(s);
// 					if(fooN.getDist(s)<3*PATHINTERVAL)
// 						bp=false;
//    				}
//    				magP.add(fooP.vect.getMag());
//    				fooP.vect.setMag(CHARGEINTERVAL*PATHINTERVAL);
//    				fooP.move();
//    				magN.add(fooN.vect.getMag());
//    				fooN.vect.setMag(CHARGEINTERVAL*PATHINTERVAL);
//    				fooN.move();
//    				fooPathP.add(new Point3D(fooP.center.x,fooP.center.y,fooP.center.z));
//    				fooPathN.add(new Point3D(fooN.center.x,fooN.center.y,fooN.center.z));
//    				ptP=fooP.center;
//    				ptN=fooN.center;
//    				max=Math.max(max,Math.max(fooP.vect.getMag(),fooN.vect.getMag()));
//    				min=Math.min(min,Math.min(fooP.vect.getMag(),fooN.vect.getMag()));
// 
//    			}
//    			ArrayList<Color> colorP= new ArrayList<Color>(),colorN= new ArrayList<Color>();
//    			for(int i=0;i<magP.size();i++)
//    			{
//    				colorP.add(getColorValue(magP.get(i),min,max));
//    				colorN.add(getColorValue(magN.get(i),min,max));
//    			}
//    			cPaths.add(new LineArray(fooPathP,colorP));
//    			cPaths.add(new LineArray(fooPathN,colorN));
//    			shapes.add(new LineP(fooPathP.get(0),fooPathN.get(0)));
//    }

   public void drawEPFieldThrough(Point3D point)
   {
   			ArrayList<Point3D> fooPathP = new ArrayList<Point3D>();
   			ArrayList<Point3D> fooPathN = new ArrayList<Point3D>();
   			Point3D ptP=point,ptN=point;
   			boolean bp=true;
   			for(int i=0;i<CHARGEITERATIONS&&bp;i+=PATHINTERVAL)
   			{
   				VSphere fooP = new VSphere(ptP,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   				VSphere fooN = new VSphere(ptN,RADIUS,NUMSIDES,new Vect3D(new Point3D(0,0,0),new Point3D(0,0,0)),BOUND/2);
   				for(VSphere s : spheres)
   				{
   					fooP.CHARGECONST=1000;
					fooP.CHARGE=1;
					fooP.staticForceTo(s);
					if(fooP.getDist(s)<3*PATHINTERVAL)
						bp=false;
   					fooN.CHARGECONST=1000;
   					fooN.CHARGE=-1;
					fooN.staticForceTo(s);
					if(fooN.getDist(s)<3*PATHINTERVAL)
						bp=false;
   				}
   				fooP.vect=new Vect3D(new Point3D(0,0,0),new Point3D(-fooP.vect.y,fooP.vect.x,fooP.vect.z));
   				fooP.vect.setMag(CHARGEINTERVAL*PATHINTERVAL);
   				fooP.move();
   				fooN.vect=new Vect3D(new Point3D(0,0,0),new Point3D(-fooN.vect.y,fooN.vect.x,fooN.vect.z));
   				fooN.vect.setMag(CHARGEINTERVAL*PATHINTERVAL);
   				fooN.move();
   				fooPathP.add(new Point3D(fooP.center.x,fooP.center.y,fooP.center.z));
   				fooPathN.add(new Point3D(fooN.center.x,fooN.center.y,fooN.center.z));
   				ptP=fooP.center;
   				ptN=fooN.center;
   			}
   			cPaths.add(new LineArray(fooPathP));
   			cPaths.add(new LineArray(fooPathN));
   			shapes.add(new LineP(fooPathP.get(0),fooPathN.get(0)));
   }

   public Point3D getCentMass()
   {
   		double sumMX=0,sumMY=0,sumMZ=0,sumMass=0;
   		for(VSphere s : spheres)
   		{
   			sumMX+=s.MASS*s.center.x;
   			sumMY+=s.MASS*s.center.y;
   			sumMZ+=s.MASS*s.center.z;
   			sumMass+=s.MASS;
   		}
   		sumMX/=sumMass;
   		sumMY/=sumMass;
   		sumMZ/=sumMass;
   		return new Point3D(sumMX,sumMY,sumMZ);
   }

   public Color getColorValue(double in, double min, double max)
    {
    	float val=(float)(Math.pow((in-min),1)/Math.pow((max-min),1));
    	Color c = Color.getHSBColor(val,1.0f,1.0f);
    	return c;
    }

   public int getSphereAt(Point pt)
   {
   		for(int i=0;i<tCenters.size();i++)
   		{
   			Point sP = tCenters.get(i).getScreenPoint(new Point(YBOUND/2,XBOUND/2),VIEWDIST);
			if(sP.getDistTo(pt)<=25)
				return i;
   		}
   		return -1;
   }


}