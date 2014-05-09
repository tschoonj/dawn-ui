/*-
 * Copyright 2014 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dawnsci.plotting.draw2d.swtxy.selection;

import java.util.EventObject;

import org.dawnsci.plotting.api.region.ROIEvent;
import org.dawnsci.plotting.draw2d.swtxy.translate.FigureTranslator;
import org.dawnsci.plotting.draw2d.swtxy.translate.TranslationEvent;
import org.dawnsci.plotting.draw2d.swtxy.translate.TranslationListener;
import org.dawnsci.plotting.draw2d.swtxy.util.Draw2DUtils;
import org.dawnsci.plotting.draw2d.swtxy.util.PointFunction;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

import uk.ac.diamond.scisoft.analysis.roi.IFitROI;
import uk.ac.diamond.scisoft.analysis.roi.IPolylineROI;
import uk.ac.diamond.scisoft.analysis.roi.IROI;
import uk.ac.diamond.scisoft.analysis.roi.PointROI;
import uk.ac.diamond.scisoft.analysis.roi.PolylineROI;
import uk.ac.diamond.scisoft.analysis.roi.handler.ROIHandler;

/**
 * Class for a parametric shape fitted to a polyline ROI and does not use a ROIHandler
 */
abstract public class FitROIShape<T extends IFitROI> extends ParametricROIShapeBase<T> implements PointFunction {
	protected IPolylineROI proi;

	public FitROIShape() {
		super();
	}

	public FitROIShape(Figure parent, AbstractSelectionRegion<T> region) {
		super(parent, region);
	}

	@Override
	protected ROIHandler<T> createROIHandler(T roi) {
		return null;
	}

	@Override
	public void setCentre(Point nc) {
	}

	abstract protected T createNewROI(IPolylineROI lroi);

	public void setup(PointList points, boolean withHandles) {
		dirty = true;
		proi = new PolylineROI();
		
		final Point p = new Point();
		for (int i = 0, imax = points.size(); i < imax; i++) {
			points.getPoint(p, i);
			proi.insertPoint(new PointROI(cs.getValueFromPosition(p.x(), p.y())));
		}

		croi = createNewROI(proi);

		if (withHandles) {
			configureHandles();
		}
		Rectangle b = getBounds();
		if (b != null)
			setBounds(b);
	}

	public boolean isFitted() {
		if (getROI() == null)
			return false;
		return !Double.isNaN(getROI().getRMS());
	}

	@Override
	protected void configureHandles() {
		boolean mobile = region.isMobile();
		boolean visible = isVisible() && mobile;
		for (IROI p : proi) {
			double[] pt = cs.getPositionFromValue(p.getPointRef());
			addHandle(pt[0], pt[1], mobile, visible, handleListener);
		}

		addCentreHandle(mobile, visible);

		// figure move (commented out for usability when tweaking handles)
//		addFigureListener(moveListener);
//		FigureTranslator mover = new FigureTranslator(region.getXyGraph(), parent, this, handles);
//		mover.setActive(mobile);
//		mover.addTranslationListener(region.createRegionNotifier());
//		fTranslators.add(mover);

		region.setRegionObjects(this, handles);
	}

	@Override
	protected Rectangle updateFromHandles() {
		int imax = handles.size() - 1;
		int i = 0;

		for (IFigure f : handles) { // this is called first so update points
			if (f instanceof SelectionHandle) {
				SelectionHandle h = (SelectionHandle) f;
				Point pt = h.getSelectionPoint();
				double[] p = cs.getValueFromPosition(pt.x(), pt.y());
				if (i < imax) {
					proi.setPoint(i++, new PointROI(p));
				} else {
					croi.setPoint(p);
				}
			}
		}
		croi.setPoints(proi);

		SelectionHandle h = (SelectionHandle) handles.get(imax);
		h.setSelectionPoint(getCentre());

		dirty = true;
		return getBounds();
	}

	private void updateFromCentreHandle(double[] delta, boolean drag) {
		T lroi = getROI();
		IPolylineROI lproi = lroi.getPoints();

		int i = 0;
		for (IROI p: lproi) {
			p.addPoint(delta);
			double[] pnt = cs.getPositionFromValue(p.getPointRef());
			Point pt = new PrecisionPoint(pnt[0], pnt[1]);
			SelectionHandle h = (SelectionHandle) handles.get(i);
			h.setSelectionPoint(pt);
			i++;
		}

		lroi.addPoint(delta); // shift ROI
		if (!drag) {
			lroi.setPoints(lproi); // refit
		}

		dirty = true;
		Rectangle b = getBounds();
		if (b  != null)
			setBounds(b);

		if (drag) {
			region.fireROIDragged(lroi, ROIEvent.DRAG_TYPE.TRANSLATE);
		} else {
			region.fireROIChanged(region.createROI(true));
			region.fireROISelection();
		}
	}

	private void removeHandle(SelectionHandle h) {
		parent.remove(h);
		h.removeMouseListeners();
	}

	private void addHandle(double x, double y, boolean mobile, boolean visible, TranslationListener listener) {
		RectangularHandle h = new RectangularHandle(cs, region.getRegionColor(), this, SIDE, x, y);
		h.setVisible(visible);
		parent.add(h);
		FigureTranslator mover = new FigureTranslator(region.getXyGraph(), h);
		mover.setActive(mobile);
		mover.addTranslationListener(listener);
		fTranslators.add(mover);
		h.addFigureListener(moveListener);
		handles.add(h);
	}

	private void addCentreHandle(boolean mobile, boolean visible) {
		Point c = getCentre();
		addHandle(c.preciseX() - HALF_SIDE, c.preciseX() - HALF_SIDE, mobile, visible, createCentreNotifier());
	}

	@Override
	protected TranslationListener createHandleNotifier() {
		return region.createRegionNotifier();
	}

	protected TranslationListener createCentreNotifier() {
		return new TranslationListener() {
			private double[] spt;

			@SuppressWarnings("unchecked")
			@Override
			public void translateBefore(TranslationEvent evt) {
				Object src = evt.getSource();
				if (!(src instanceof FigureTranslator))
					return;

				final FigureTranslator translator = (FigureTranslator) src;
				Point start = translator.getStartLocation();
				spt = cs.getValueFromPosition(start.x(), start.y());
				troi = (T) croi.copy();
			}

			@Override
			public void translationAfter(TranslationEvent evt) {
				update(evt, true);
			}

			@Override
			public void translationCompleted(TranslationEvent evt) {
				troi = null;

				update(evt, false);
			}

			private void update(EventObject evt, boolean drag) {
				Object src = evt.getSource();
				if (!(src instanceof FigureTranslator))
					return;

				final FigureTranslator translator = (FigureTranslator) src;
				Point end = translator.getEndLocation();
				if (end==null) return;
				double[] del = cs.getValueFromPosition(end.x(), end.y());
				del[0] -= spt[0];
				del[1] -= spt[1];

				updateFromCentreHandle(del, drag);
			}

			@Override
			public void onActivate(TranslationEvent evt) {
			}
		};
	}


	/**
	 * Update according to ROI
	 * @param uroi
	 */
	public void updateFromROI(T uroi) {
		IPolylineROI puroi = uroi.getPoints();
		int imax = handles.size() - 1;
		if (croi == null) {
			croi = uroi;
			proi = puroi;
		}

		if (imax != puroi.getNumberOfPoints()) {
			if (puroi != proi)
				proi.removeAllPoints();
			for (int i = imax; i >= 0; i--) {
				removeHandle((SelectionHandle) handles.remove(i));
			}
			boolean mobile = region.isMobile();
			boolean visible = isVisible() && mobile;
			for (IROI r: puroi) {
				if (puroi != proi)
					proi.insertPoint(r);
				double[] pnt  = cs.getPositionFromValue(r.getPointRef());
				addHandle(pnt[0], pnt[1], mobile, visible, region.createRegionNotifier());
			}
			croi.setPoints(proi);
			addCentreHandle(mobile, visible);
//			addFigureListener(moveListener);
//			FigureTranslator mover = new FigureTranslator(region.getXyGraph(), parent, this, handles);
//			mover.addTranslationListener(region.createRegionNotifier());
//			mover.setActive(region.isMobile());
//			fTranslators.add(mover);
			region.setRegionObjects(this, handles);
		} else {
			for (int i = 0; i < imax; i++) {
				IROI p = puroi.getPoint(i);
				if (puroi != proi)
					proi.setPoint(i, p);
				double[] pnt = cs.getPositionFromValue(p.getPointRef());
				Point pt = new PrecisionPoint(pnt[0], pnt[1]);
				SelectionHandle h = (SelectionHandle) handles.get(i);
				h.setSelectionPoint(pt);
			}
			croi.setPoints(proi);
			SelectionHandle h = (SelectionHandle) handles.get(imax);
			h.setSelectionPoint(getCentre());
		}

		dirty = true;
		Rectangle b = getBounds();
		if (b  != null)
			setBounds(b);
	}

	protected void outlineShape(Graphics graphics, Rectangle parentBounds, boolean isClosed) {
		T lroi = getROI();
		if (lroi == null) {
			return;
		}
		PointList points = Draw2DUtils.generateCurve(this, lroi.getStartParameter(0), lroi.getEndParameter(0));
		Draw2DUtils.drawClippedPolyline(graphics, points, parentBounds, isClosed);
	}

	protected void fillShape(Graphics graphics, Rectangle parentBounds) {
		T lroi = getROI();
		if (lroi == null) {
			return;
		}
		PointList points = Draw2DUtils.generateCurve(this, lroi.getStartParameter(0), lroi.getEndParameter(0));
		graphics.fillPolygon(points);
	}

	@Override
	protected void outlineShape(Graphics graphics) {
		outlineShape(graphics, parent != null ? parent.getBounds() : bnds);
	}

	@Override
	protected void fillShape(Graphics graphics) {
		fillShape(graphics, parent != null ? parent.getBounds() : bnds);
	}
}