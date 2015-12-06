import java.util.*;
import java.awt.*;

public class LineArray extends Polygon
{
	public Point3D center;
	public ArrayList<Point3D> points;
	public ArrayList<Color> pointColors;
	public ArrayList<LineP> colorLines;

	public LineArray(ArrayList<Point3D> pts)
	{
		super();
		id="LineArray";
		points=pts;
		pointColors=null;

		for(int i=1;i<pts.size();i++)
			lines.add(new Line3D(pts.get(i-1),pts.get(i)));

		pan(new Point3D(0,0,0));

	}
	public LineArray(ArrayList<Point3D> pts, ArrayList<Color> clrs)
	{
		super();
		points=pts;
		pointColors=null;
		colorLines=new ArrayList<LineP>();
		for(int i=1;i<pts.size();i++) {
				LineP ln = new LineP(pts.get(i-1),pts.get(i));
				ln.color=clrs.get(i-1);
				colorLines.add(ln);
			}

		pan(new Point3D(0,0,0));
	}

	public LineArray(ArrayList<Point3D> pts, int interval)
	{
		super();

		for(int i=interval;i<pts.size();i+=interval)
			if(i>0)
				lines.add(new Line3D(pts.get(i-interval),pts.get(i)));

		pan(new Point3D(0,0,0));

	}


}