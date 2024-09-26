package logogui;

import javafx.scene.paint.Color;

public class GraphLineParameter {
/*
 * 5 Paramètres pour tracer un segment en couleur
 */
public double initx, inity, endx, endy;
public Color color;
	public GraphLineParameter(double initx, double inity, double endx, double endy, Color color) {
		this.initx = initx;
		this.inity = inity;
		this.endx = endx;
		this.endy = endy;
		this.color = color;
	}

}
