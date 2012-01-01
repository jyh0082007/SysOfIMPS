package com.imps.activities;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/**
* ��ͼ�ϵ�����ͼ��:����һ�����,һ���յ�,�Լ�֮�������
* @author superwang
*/
public class TrackOverlay extends ItemizedOverlay<OverlayItem> {
	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | 
	Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
	
	/**
	 * ���ڱ������/�յ�����
	 */
	private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	/**
	 * ���ڱ��湹�����ߵĵ������
	 */
	private final ArrayList<GeoPoint> linePoints = new ArrayList<GeoPoint>();

	/**
	 * @param defaultMarker
	 */
	public TrackOverlay() {
		super(null);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	/**
	 * �������/�յ�
	 * description:
	 * @param overlay
	 */
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	/**
	 * ��������еĵ�
	 * description:
	 * @param point
	 */
	public void addLinePoint(GeoPoint point) {
		linePoints.add(point);
	}

	public ArrayList<GeoPoint> getLinePoints() {
		return linePoints;
	}

	/**
	 * �����/�յ�/�켣
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {
			//System.out.println("!!!!!!!!!!!!!!");
			
			canvas.save(LAYER_FLAGS);
			//canvas.save();
			
			Projection projection = mapView.getProjection();
			int size = mOverlays.size();
			Point point = new Point();
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			OverlayItem overLayItem;

			//�����/�յ�
			for (int i = 0; i < size; i++) {
				overLayItem = mOverlays.get(i);

				Drawable marker = overLayItem.getMarker(0);
				//marker.getBounds()
				/* ���ص�ȡ��ת�� */
				projection.toPixels(overLayItem.getPoint(), point);

				if (marker != null) {
					boundCenterBottom(marker);
				}

				/* ԲȦ */
				//Paint paintCircle = new Paint();
				//paintCircle.setColor(Color.RED);
				paint.setColor(Color.RED);
				canvas.drawCircle(point.x, point.y, 5, paint);

				/* �������� */
				/* ���� */
				String title = overLayItem.getTitle();
				/* ��� */
				//    String snippet = overLayItem.getSnippet();
				//
				//    StringBuffer txt = new StringBuffer();
				//    if (title != null && !"".equals(title))
				//     txt.append(title);
				//
				//    if (snippet != null && !"".equals(snippet)) {
				//     if (txt.length() > 0) {
				//      txt.append(":");
				//     }
				//     txt.append(snippet);
				//    }    
				//Paint paintText = new Paint();

				if (title != null && title.length() > 0) {
					paint.setColor(Color.BLACK);
					paint.setTextSize(15);
					canvas.drawText(title, point.x, point.y, paint);
				}

			}

			//����
			boolean prevInBound = false;//ǰһ�����Ƿ��ڿ�������
			Point prev = null;
			int mapWidth = mapView.getWidth();
			int mapHeight = mapView.getHeight();
			//Paint paintLine = new Paint();
			paint.setColor(Color.RED);
			//paint.setPathEffect(new CornerPathEffect(10));
			paint.setStrokeWidth(2);
			int count = linePoints.size();

			//Path path = new Path();
			//path.setFillType(Path.FillType.INVERSE_WINDING);
			for (int i = 0; i < count; i++) {
				GeoPoint geoPoint = linePoints.get(i);
				//projection.toPixels(geoPoint, point); //��һ���ƺ�������
				point = projection.toPixels(geoPoint, null);
				if (prev != null) {
					if (point.x >= 0 && point.x <= mapWidth && point.y >= 0 && point.y <= mapHeight) {
						if ((Math.abs(prev.x - point.x) > 2 || Math.abs(prev.y - point.y) > 2)) {
							//�����жϵ��Ƿ��غϣ��غϵĲ����ߣ����ܻᵼ�»��߲���·��
							canvas.drawLine(prev.x, prev.y, point.x, point.y, paint);
							//path.lineTo(point.x, point.y);

							prev = point;
							prevInBound = true;

						}
					} 
					else {
						//�ڿ�������֮��
						if (prevInBound) {//ǰһ�����ڿ��������ڣ�Ҳ��Ҫ����
							//path.lineTo(point.x, point.y);
							canvas.drawLine(prev.x, prev.y, point.x, point.y, paint);
						}
						prev = point;
						prevInBound = false;
					}
				} 
				else {
					//path.moveTo(point.x, point.y);
					prev = point;
				}
			}
			//canvas.drawPath(path, paint);
			canvas.restore();
			//DebugUtils.showMemory();
		}
		super.draw(canvas, mapView, shadow);
	}
}