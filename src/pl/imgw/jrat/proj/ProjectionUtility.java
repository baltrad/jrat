/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.proj;

import java.awt.Point;
import java.awt.geom.Point2D;

import pl.imgw.jrat.util.LogsHandler;

import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

/**
 *
 * ProjectionUtility - class with methods helping in all kind of needed projections
 * 
 *
 *
 *
 * @author <a href="mailto:tomasz.sznajderski@imgw.pl">Tomasz Sznajderski</a>
 *
 */
public class ProjectionUtility {

	protected static final String DEFAULT_ELLIPSOID = "WGS84";

	/**
	 * Project a point from geographical to Cartesian coordinates using projection set by given parameters.
	 *
	 * 
	 * @param pointGeo - point with geographical coordinates
	 * @param projectionParameters - parameters defining projection
	 * 
	 * @return point with Cartesian coordinates or null (if projection defined by given parameters cannot be found)
	 */
	public static Point2D.Double projectToCartesian(Point2D.Double pointGeo, String[] projectionParameters) {

		return projectToCartesian(pointGeo, getProjection(projectionParameters));
	}

	/**
	 * Project a point from Cartesian to geographical coordinates using projection set by given parameters.
	 *
	 * 
	 * @param pointCartesian - point with Cartesian coordinates
	 * @param projectionParameters - parameters defining projection
	 * 
	 * @return point with geographical coordinates or null (if projection defined by given parameters cannot be found)
	 */
	public static Point2D.Double projectToGeographical(Point2D.Double pointCartesian, String[] projectionParameters) {

		return projectToGeographical(pointCartesian, getProjection(projectionParameters));
	}

	/**
	 * Project a point from geographical to local (image) coordinates using projection set by given parameters and additional information about image.
	 *
	 * 
	 * @param pointGeo - point with geographical coordinates
	 * @param projectionParameters - parameters defining projection
	 * @param imageWidth - width of image
	 * @param imageHeight - height of image
	 * @param leftTopImageCorner - Cartesian coordinates of left-top image corner
	 * @param rightDownImageCorner - Cartesian coordinates of right-down image corner
	 * 
	 * @return point with local (image) coordinates or null (if projection defined by given parameters cannot be found or if this point is out of image bounds)
	 */
	public static Point2D.Double projectToLocal(Point2D.Double pointGeo, String[] projectionParameters,
			Short imageWidth, Short imageHeight, Point2D.Double leftTopImageCorner, Point2D.Double rightDownImageCorner) {

		Double xLoc, yLoc;
		Point2D.Double pointCartesian;
		Projection projection;

		projection = getProjection(projectionParameters);
		if (projection == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "projectToLocal: projection is null!!!");
			return null;
		}

		pointCartesian = projectToCartesian(pointGeo, projection);
		if (pointCartesian == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "projectToLocal: pointCartesian is null!!!");
			return null;
		}

/*		leftTop = projectToCartesian(leftTopImageCorner, projection);
		if (leftTop == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "projectToLocal: leftTop is null!!!");
			return null;
		}

		rightDown = projectToCartesian(rightDownImageCorner, projection);
		if (rightDown == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "projectToLocal: rightDown is null!!!");
			return null;
		}
*/
//		projection = ProjectionFactory.fromPROJ4Specification(projectionParameters);//new String[]{"+proj=" + projectionName, "+ellps=" + DEFAULT_ELLIPSOID, "+lon_0=22.0025", "+lat_0=50.1142"});
//		projection.initialize();

//		pointCartesian = new Point2D.Double();
//		leftTop = new Point2D.Double();
//		rightDown = new Point2D.Double();

		// transforming from geographical to Cartesian coordinates
//		projection.transform(pointGeo, pointCartesian);
//		projection.transform(leftTopImageCorner, leftTop);
//		projection.transform(rightDownImageCorner, rightDown);

		// scaling to local coordinates (from left-top (0, 0) to right-down (imageWidth, imageHeight) corner)
		xLoc = /*(int) Math.rint(*/((pointCartesian.getX() - leftTopImageCorner.getX()) / (rightDownImageCorner.getX() - leftTopImageCorner.getX())) * imageWidth;
		if ((xLoc < 0) || (xLoc > imageWidth)) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(),
				"projectToLocal: horizontal coordinate (x) of given point (pointGeo: " + pointGeo.toString() + ") is out of image bounds!!!");
			return null;
		}

		yLoc = /*(int) Math.rint(*/((pointCartesian.getY() - leftTopImageCorner.getY()) / (rightDownImageCorner.getY() - leftTopImageCorner.getY())) * imageHeight;
		if ((yLoc < 0) || (yLoc > imageHeight)) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(),
				"projectToLocal: vertical coordinate (y) of given point (pointGeo: " + pointGeo.toString() + ") is out of image bounds!!!");
			return null;
		}

		return new Point2D.Double(xLoc, yLoc);
	}

	/**
	 * Project a point from geographical to rounded to integer values local (image) coordinates
	 * using projection set by given parameters and additional information about image.
	 * 
	 * @param pointGeo - point with geographical coordinates
	 * @param projectionParameters - parameters defining projection
	 * @param imageWidth - width of image
	 * @param imageHeight - height of image
	 * @param leftTopImageCorner - Cartesian coordinates of left-top image corner
	 * @param rightDownImageCorner - Cartesian coordinates of right-down image corner
	 * 
	 * @return point with rounded to integer values local (image) coordinates or null
	 * 		  (if projection defined by given parameters cannot be found or if this point is out of image bounds)
	 */
	public static Point projectToLocalRounded(Point2D.Double pointGeo, String[] projectionParameters,
			Short imageWidth, Short imageHeight, Point2D.Double leftTopImageCorner, Point2D.Double rightDownImageCorner) {

		Point2D p;

		p = projectToLocal(pointGeo, projectionParameters, imageWidth, imageHeight, leftTopImageCorner, rightDownImageCorner);
		return new Point((int) Math.rint(p.getX()), (int) Math.rint(p.getY()));
	}

	/**
	 * Project a point from local (image) to geographical coordinates using projection set by given parameters and additional information about image.
	 *
	 * 
	 * @param pointLoc - point with local coordinates
	 * @param projectionParameters - parameters defining projection
	 * @param imageWidth - width of image
	 * @param imageHeight - height of image
	 * @param leftTopImageCorner - Cartesian coordinates of left-top image corner
	 * @param rightDownImageCorner - Cartesian coordinates of right-down image corner
	 * 
	 * @return point with geographical coordinates or null (if projection defined by given parameters cannot be found or if this point is out of image bounds)
	 */
	public static Point2D.Double projectToGeographical(Point pointLoc, String[] projectionParameters, Short imageWidth, Short imageHeight, Point2D.Double leftTopImageCorner, Point2D.Double rightDownImageCorner) {

		Projection projection;
		Double xCartesian, yCartesian;

		projection = getProjection(projectionParameters);
		if (projection == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "projectToGeographical: projection is null!!!");
			return null;
		}

/*		leftTop = projectToCartesian(leftTopImageCorner, projection);
		if (leftTop == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "projectToGeographical: leftTop is null!!!");
			return null;
		}

		rightDown = projectToCartesian(rightDownImageCorner, projection);
		if (rightDown == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "projectToGeographical: rightDown is null!!!");
			return null;
		}
*/
		// scaling to Cartesian coordinates
		xCartesian = leftTopImageCorner.getX() + (new Double(pointLoc.getX()) / new Double(imageWidth) * (rightDownImageCorner.getX() - leftTopImageCorner.getX()));
		if ((xCartesian < leftTopImageCorner.getX()) || (xCartesian > rightDownImageCorner.getX())) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(),
				"projectToGeographical: horizontal coordinate (x) of given point (pointGeo: " + pointLoc.toString() + ") is out of image bounds!!!");
			return null;
		}

		yCartesian = leftTopImageCorner.getY() + (new Double(pointLoc.getY()) / new Double(imageHeight) * (rightDownImageCorner.getY() - leftTopImageCorner.getY()));
		if ((yCartesian < leftTopImageCorner.getY()) || (yCartesian > rightDownImageCorner.getY())) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(),
				"projectToGeographical: vertical coordinate (y) of given point (pointLoc: " + pointLoc.toString() + ") is out of image bounds!!!");
			return null;
		}

		// projecting to geographical coordinates
		return projectToGeographical(new Point2D.Double(xCartesian, yCartesian), projection);
	}

	/*
	 * Project a point from geographical to Cartesian coordinates using given projection.
	 *
	 * 
	 * @param pointGeo - point with geographical coordinates
	 * @param projection - projection used to project pointGeo's coordinates to Cartesian
	 * 
	 * @return point with Cartesian coordinates or null (if projection defined by given parameters cannot be found)
	 */
	private static Point2D.Double projectToCartesian(Point2D.Double pointGeo, Projection projection) {

		Point2D.Double result;

		if (projection != null) {

			result = new Point2D.Double();
			projection.transform(pointGeo, result);

			return result;
		}
		return null;
	}

	/*
	 * Project a point from Cartesian to geographical coordinates using given projection.
	 *
	 * 
	 * @param pointCartesian - point with Cartesian coordinates
	 * @param projection - projection used to project pointCartesian's coordinates to geographical
	 * 
	 * @return point with geographical coordinates or null (if projection defined by given parameters cannot be found)
	 */
	private static Point2D.Double projectToGeographical(Point2D.Double pointCartesian, Projection projection) {

		Point2D.Double result;

		if (projection != null) {
			result = new Point2D.Double();
			projection.inverseTransform(pointCartesian, result);

			return result;
		}
		return null;
	}

	/*
	 * Get projection defined by given parameters.
	 *
	 * 
	 * 
	 * @param projectionParameters - parameters defining projection
	 * 
	 * @return projection or null (if projection defined by given parameters cannot be found)
	 */
	private static Projection getProjection(String[] projectionParameters) {

		Projection projection;

		projection = ProjectionFactory.fromPROJ4Specification(projectionParameters);
		if (projection == null) {
			LogsHandler.saveProgramLogs(ProjectionUtility.class.getName(), "getProjection: could not find projection using projectionParameters!!!");
			return null;
		}
		projection.initialize();

		return projection;
	}

}