/*
  * Created on 12 may. 2018
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package logogui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class Traceur {
	private Color couleur = Color.BLACK;
	private double initx = 350, inity = 350; // position initiale
	private double posx = initx, posy = inity; // position courante
	private double angle = 90;
	private double teta;
	private boolean crayon = false;
	ObjectProperty<GraphLineParameter> line;

	public Traceur() {
		setTeta();
		line = new SimpleObjectProperty<GraphLineParameter>();
	}

	public ObjectProperty<GraphLineParameter> lineProperty() {
		return line;
	}

	public double getPosx() {
		return posx;
	}
	
	public void setPosx(double x) {
		posx = x;
	}

	public double getPosy() {
		return posy;
	}
	
	public void setPosy(double y) {
		posy = y;
	}

	private void setTeta() {
		teta = Math.toRadians(angle);
	}

	private void addLine(double x1, double y1, double x2, double y2) {
		line.setValue(new GraphLineParameter(x1, y1, x2, y2, couleur));
	}

	public void avance(double r) {
		double a = posx + r * Math.cos(teta);
		double b = posy - r * Math.sin(teta);
		if (crayon == true) {
			addLine(posx, posy, a, b);
		}
		posx = a;
		posy = b;
	}

	public void td(double r) {
		angle = (angle - r) % 360;
		setTeta();
	}
	
	public void tg(double r) {
		angle = (angle + r) % 360;
		setTeta();
		}

	public void bc() {
		crayon = true;
		System.out.println("On baisse le crayon");
	}

	public void lc() {
		crayon = false;
		System.out.println("On lève le crayon");
	}

	public void fpos(double newposx, double newposy) {
		posx= newposx;
		posy = newposy;
		System.out.println("On bouge la tortue");
	}
	
	public void recule(double r) {
		double a = posx - r * Math.cos(teta);
		double b = posy + r * Math.sin(teta);
		if (crayon==true)
		{
			addLine(posx,posy,a, b);
			System.out.println("On trace");
		}

		posx = a;
		posy = b;
	}
	
    public void fcap(double newcap) {
        angle = newcap;
        setTeta();
        System.out.println("On a change le cap de la tortue");
    }
    
    public void fcc(String newCouleur) {
    	switch(newCouleur) {
    	case "rouge" :
    		System.out.println("couleur : rouge");
    		couleur = Color.RED;
    		break;
    	case "bleu" :
    		System.out.println("couleur : bleu");
    		couleur = Color.BLUE;
    		break;
    	case "vert" :
    		System.out.println("couleur : vert");
    		couleur = Color.GREEN;
    		break;
    	default : //choix non implémenté ou sélection de la couleur par défaut (noir)
    		System.out.println("couleur : noir (couleur par défaut)");
    		couleur = Color.BLACK;
    	}
    	//Note : le nombre de couleurs disponibles et les couleurs disponibles, ont été choisis arbitrairement
    }

}
