package logomodel;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import javafx.beans.property.StringProperty;
import logoparsing.LogoParser.FposContext;
import logoparsing.LogoParser.FractionContext;
import logoparsing.LogoParser.HasardContext;
import logoparsing.LogoParser.InfequalContext;
import logoparsing.LogoParser.InfstrictContext;
import logoparsing.LogoParser.ReContext;
import logoparsing.LogoParser.RepeteContext;
import logoparsing.LogoParser.SiContext;
import logoparsing.LogoParser.SinContext;
import logoparsing.LogoParser.SisinonContext;
import logoparsing.LogoParser.StoreContext;
import logoparsing.LogoParser.SupequalContext;
import logoparsing.LogoParser.SupstrictContext;
import logoparsing.LogoParser.TantqueContext;
import logogui.Traceur;
import logoparsing.LogoParser.AdditionContext;
import logoparsing.LogoParser.AvContext;
import logoparsing.LogoParser.BcContext;
import logoparsing.LogoParser.CosContext;
import logoparsing.LogoParser.DifferenceContext;
import logoparsing.LogoParser.DonneContext;
import logoparsing.LogoParser.EqualsContext;
import logoparsing.LogoParser.FacteurContext;
import logoparsing.LogoParser.FcapContext;
import logoparsing.LogoParser.FccContext;
import logoparsing.LogoParser.FloatContext;
import logoparsing.LogoParser.LcContext;
import logoparsing.LogoParser.LoopContext;
import logoparsing.LogoParser.MoveContext;
import logoparsing.LogoParser.MultiplicationContext;
import logoparsing.LogoParser.ParentheseContext;
import logoparsing.LogoParser.TdContext;
import logoparsing.LogoParser.TermeContext;
import logoparsing.LogoParser.TgContext;
import logoparsing.LogoParser.VarContext;

import java.util.ArrayList;
import java.util.List;

public class LogoTreeVisitor extends LogoStoppableTreeVisitor {
	Traceur traceur;
	Log log;
	//List<Integer> listLoop = new ArrayList<>(); 
	Integer loop = 0; //variable loop correspondant au numéro d'itération d'une boucle de type "repete"
	List<Pair<Double, Double>> coorList = new ArrayList<>(); //liste des coordonnées pour les instructions store/move (utilisée comme une pile)
	List<List<Pair<String, Double>>> listTableSymboles = new ArrayList<>(); //liste des tables des symboles (utilisée comme une pile, afin de supporter la notion de portée (exemple : une variable déclarée dans une boucle ne sera plus accessible après avoir quitté la boucle car elle n'existe que dans la portée de la boucle))
	String currentString; 
	
	public Integer getLoop() {
		return loop;
	}
	
	public void setLoop(int newValue) {
		loop = newValue;
	}
		
	public LogoTreeVisitor() {
		traceur = new Traceur();
		log = new Log();
	}
	
    public StringProperty logProperty() {
    	return log;
    }
    
	public Traceur getTraceur() {
		return traceur;
	}

	/*
	 * Map des attributs associés à chaque noeud de l'arbre 
	 * key = node, value = valeur de l'expression du node
	 */
	ParseTreeProperty<Double> atts = new ParseTreeProperty<Double>();

	public void setValue(ParseTree node, double value) {
		atts.put(node, value);
	}

	public double getValue(ParseTree node) {
		Double value = atts.get(node);
		if (value == null) {
			throw new NullPointerException();
		}
		return value;
	}

// Instructions de base
	
	@Override
	public Integer visitTd(TdContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.td(bilan.b);
		}
		return 0; 
	}

	@Override
	public Integer visitAv(AvContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		System.out.println("expr = " + bilan);
		if (bilan.a == 0) {
			traceur.avance(bilan.b);
			log.defaultLog(ctx);
			log.appendLog("Avance de", String.valueOf(bilan.b));
		}
		return bilan.a;
	}
	
	@Override
	public Integer visitTg(TgContext ctx) {
	Pair<Integer, Double> bilan = evaluate(ctx.expr());
	if (bilan.a == 0) {
	traceur.tg(bilan.b);
	}
	return 0; 
	}
	
	@Override
	public Integer visitLc(LcContext ctx) {
	traceur.lc();
	return 0;
	}

	@Override
	public Integer visitBc(BcContext ctx) {
	traceur.bc();
	return 0; 
	}
	
	@Override
	public Integer visitRe(ReContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.recule(bilan.b);
			log.defaultLog(ctx);
			log.appendLog("Recule de", String.valueOf(bilan.b));
		}
		return bilan.a;
	}
	
	@Override
	public Integer visitFpos(FposContext ctx) {
		Pair<Integer, Double> bilan0 = evaluate(ctx.expr(0));
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr(1));
		if (bilan0.a == 0 && bilan1.a==0) {
			traceur.fpos(bilan0.b,bilan1.b);
			log.defaultLog(ctx);
		}
		return bilan0.a;
	}
	
	public Integer visitFcap(FcapContext ctx) { //changement de cap
        Pair<Integer, Double> bilan = evaluate(ctx.expr());
        if (bilan.a==0) {
            traceur.fcap(bilan.b);
            log.defaultLog(ctx);
        }
        return bilan.a;
    }
	
	public Integer visitFcc(FccContext ctx) { //changement de couleur
		String couleur = ctx.VARIABLE().getText(); //récupération de la chaîne de caractères
		traceur.fcc(couleur);
		return 0;
	}
	
	public Integer visitParenthese(ParentheseContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			setValue(ctx, bilan.b);
		}
		return bilan.a;
	}
	
	public Integer visitAddition(AdditionContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr());
		Pair<Integer, Double> bilan2 = evaluate(ctx.term());
		if (bilan1.a == 0 && bilan2.a == 0) {
			setValue(ctx, bilan1.b + bilan2.b);
		}
		return bilan1.a ;
	}
	
	public Integer visitDifference(DifferenceContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr());
		Pair<Integer, Double> bilan2 = evaluate(ctx.term());
		if (bilan1.a == 0 && bilan2.a == 0) {
			setValue(ctx, bilan1.b - bilan2.b);
		}
		return bilan1.a ;
	}
	
	public Integer visitTerme(TermeContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.term());
		if(bilan.a == 0) {
			setValue(ctx, bilan.b);
		}
		return bilan.a;
	}
	
	public Integer visitFacteur(FacteurContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.fact());
		if(bilan.a == 0) {
			setValue(ctx, bilan.b);
		}
		return bilan.a;
	}
	
	public Integer visitMultiplication(MultiplicationContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.fact());
		Pair<Integer, Double> bilan2 = evaluate(ctx.term());
		if (bilan1.a == 0 && bilan2.a == 0) {
			setValue(ctx, bilan1.b * bilan2.b);
		}
		return bilan1.a ;
	}
	
	public Integer visitFraction(FractionContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.term());
		Pair<Integer, Double> bilan2 = evaluate(ctx.fact());
		if (bilan1.a == 0 && bilan2.a == 0 && bilan1.b != 0) {
			setValue(ctx, bilan1.b / bilan2.b);
		}
		return bilan1.a ;
	}
	
	public Integer visitHasard(HasardContext ctx) { //fonction de hasard
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if(bilan.a == 0) {
			setValue(ctx,( Math.random() * (bilan.b - 1)) + 1); //permet de renvoyer un nombre aléatoire compris entre 0 et la valeur renseignée
		}
		return bilan.a;
	}
	
	public Integer visitCos(CosContext ctx) { //cosinus d'un angle en degrés
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if(bilan.a == 0) {
			setValue(ctx, Math.cos(Math.toRadians(bilan.b)));
		}
		return bilan.a;
	}
	
	public Integer visitSin(SinContext ctx) { //sinus d'un angle en degrés
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if(bilan.a == 0) {
			setValue(ctx, Math.sin(Math.toRadians(bilan.b)));
		}
		return bilan.a;
	}
	
	public Integer visitRepete(RepeteContext ctx) { //Boucle de type "for"
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) { 
			
			//Initialisation de la table des symboles dans la portée de la boucle sur le principe d'une pile
			if (listTableSymboles.size() == 0) { 
				//si la table des symboles de la portée courante est vide, on rajoute une table des symboles vide dans la pile
				List<Pair<String, Double>> tableSymboles = new ArrayList<>();
				listTableSymboles.add(tableSymboles);
			} else {
				//si la table des symboles de la portée courante comprend des éléments, on la copie et on rajoute cette copie dans la pile
				List<Pair<String, Double>> tableSymboles = new ArrayList<>(listTableSymboles.get(listTableSymboles.size() - 1));
				listTableSymboles.add(tableSymboles);
			}
			
			//répétition des instructions (boucle)
			for (int i = 0; i < bilan.b; i++) {
				setLoop(i + 1); //affectation de la variable "loop"
				visit(ctx.liste_instructions());
			}
			
			//suppression de la table des symboles de la portée de la boucle car on est sorti de cette boucle.
			listTableSymboles.remove(listTableSymboles.size() - 1);
			setLoop(0); //loop vaut 0 car on est sorti de la boucle
		}
		return bilan.a;
	}
	
	public Integer visitLoop(LoopContext ctx) {
		if (getLoop() == 0) {
			return 1; //la variable loop de devrait pas être accessible ; si sa valeur est 0 cela veut dire qu'on ne se situe pas dans une boucle "repete"
		}
		setValue(ctx, getLoop()); //sinon on renvoie sa valeur
		return 0;
	}
	
	public Integer visitStore(StoreContext ctx) {
		boolean value = coorList.add(new Pair<>(traceur.getPosx(), traceur.getPosy())); //on sauvegarde les coordonnées courantes du curseur et on l'empile (comme on utilise une liste, on le place en fin de liste, pour ne pas devoir décaler tous les éléments)
		if (!value) {
			return 0;	
		}
		return 1;
	}
	
	public Integer visitMove(MoveContext ctx) {
		if (coorList.size() > 0) { //si la liste des coordonnées n'est pas vide
			Pair<Double, Double> coor = coorList.remove(coorList.size() - 1); //on récupère les coordonnées en fin de liste (on dépile) 
			traceur.setPosx(coor.a);
			traceur.setPosy(coor.b);
			return 0;
		}
		return 1; //sinon la liste est vide et on ne devrait pas utiliser "move"
	}
	
	public Integer visitDonne(DonneContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		String identificateur = ctx.VARIABLE().getText();
		
		if (bilan.a == 0) {
			if (listTableSymboles.size() == 0) { //Si la pile de la liste des symboles est vide, on crée une table des symboles et on l'ajoute à la pile
				List<Pair<String, Double>> table = new ArrayList<>();
				listTableSymboles.add(table);
			}
			
			List<Pair<String, Double>> tableSymboles = listTableSymboles.get(listTableSymboles.size() - 1);
			
			for (int i = 0; i < tableSymboles.size(); i++) { // vérifie si la variable existe déjà dans la table des symboles et la supprime si c'est le cas (le type Pair est non-modifiable)
				if (tableSymboles.get(i).a.equals(identificateur)) {
					listTableSymboles.get(listTableSymboles.size() - 1).remove(i);
				}
			}
			listTableSymboles.get(listTableSymboles.size() - 1).add(new Pair<>(identificateur, bilan.b)); //ajout du couple "nom de la variable" / "valeur de la variable" à la table des symboles
		}
		return bilan.a;
	}
	
	public Integer visitVar(VarContext ctx) {
		String nomIdentificateur = ctx.VARIABLE().getText();
		
		List<Pair<String, Double>> tableSymboles = listTableSymboles.get(listTableSymboles.size() - 1);
		
		for (int i = 0; i < tableSymboles.size(); i++) { //on cherche dans la liste des symboles la valeur correspondant à la variable
			if (tableSymboles.get(i).a.equals(nomIdentificateur)) {
				setValue(ctx, tableSymboles.get(i).b);
				return 0;
			}
		}
		return 1;
	}
	
	
	// Booleens
	
	public Integer visitInfstrict(InfstrictContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr(0));
		Pair<Integer, Double> bilan2 = evaluate(ctx.expr(1));
		
		if (bilan1.a == 0 && bilan2.a == 0) {
			boolean result = bilan1.b < bilan2.b;
			if (result) {
				setValue(ctx, 1);
			} else {
				setValue(ctx, 0);
			}
			
		}
		return bilan1.a;
	}
	
	public Integer visitInfequal(InfequalContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr(0));
		Pair<Integer, Double> bilan2 = evaluate(ctx.expr(1));
		
		if (bilan1.a == 0 && bilan2.a == 0) {
			boolean result = bilan1.b <= bilan2.b;
			if (result) {
				setValue(ctx, 1);
			} else {
				setValue(ctx, 0);
			}
			
		}
		return bilan1.a;
	}
	
	public Integer visitSupstrict(SupstrictContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr(0));
		Pair<Integer, Double> bilan2 = evaluate(ctx.expr(1));
		
		if (bilan1.a == 0 && bilan2.a == 0) {
			boolean result = bilan1.b > bilan2.b;
			if (result) {
				setValue(ctx, 1);
			} else {
				setValue(ctx, 0);
			}
			
		}
		return bilan1.a;
	}
	
	public Integer visitSupequal(SupequalContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr(0));
		Pair<Integer, Double> bilan2 = evaluate(ctx.expr(1));
		
		if (bilan1.a == 0 && bilan2.a == 0) {
			boolean result = bilan1.b >= bilan2.b;
			if (result) {
				setValue(ctx, 1);
			} else {
				setValue(ctx, 0);
			}
			
		}
		return bilan1.a;
	}
	
	public Integer visitEquals(EqualsContext ctx) {
		Pair<Integer, Double> bilan1 = evaluate(ctx.expr(0));
		Pair<Integer, Double> bilan2 = evaluate(ctx.expr(1));
		
		if (bilan1.a == 0 && bilan2.a == 0) {
			System.out.println("(visitEquals) - bilan1.b = " + bilan1.b);
			System.out.println("(visitEquals) - bilan2.b = " + bilan2.b);
			double result =  Double.compare(bilan1.b, bilan2.b);
			System.out.println("(visitEquals) - result = " + result);
			if (result == 0) {
				setValue(ctx, 1);
			} else {
				setValue(ctx, 0);
			}
			
		}
		return bilan1.a;
	}
	
	public Integer visitSi(SiContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.booleen());
		System.out.println("(visitSi) - bilan.b = " + bilan.b);
		if (bilan.a == 0 && bilan.b == 1) {
			System.out.println("(visitSi) - CPT");
			
			//gestion tables des symboles de la portée du "si"
			if (listTableSymboles.size() == 0) {
				List<Pair<String, Double>> tableSymboles = new ArrayList<>();
				listTableSymboles.add(tableSymboles);
			} else {
				List<Pair<String, Double>> tableSymboles = new ArrayList<>(listTableSymboles.get(listTableSymboles.size() - 1));
				listTableSymboles.add(tableSymboles);
			}
			
			visit(ctx.liste_instructions());
			
			//fin de la gestion tables des symboles spécifique au bloc ; on quitte la portée du "si", donc on supprime la table des symboles associée à cette portée
			listTableSymboles.remove(listTableSymboles.size() - 1);
		}
		
		return bilan.a;
	}
	
	public Integer visitSisinon(SisinonContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.booleen());
		if (bilan.a == 0) {
			
			//gestion tables des symboles spécifique au bloc
			if (listTableSymboles.size() == 0) {
				List<Pair<String, Double>> tableSymboles = new ArrayList<>();
				listTableSymboles.add(tableSymboles);
			} else {
				List<Pair<String, Double>> tableSymboles = new ArrayList<>(listTableSymboles.get(listTableSymboles.size() - 1));
				listTableSymboles.add(tableSymboles);
			}
			
			
			if (bilan.b == 1) {
				visit(ctx.liste_instructions(0));
			} else {
				visit(ctx.liste_instructions(1));
			}
			
			//fin de la gestion tables des symboles spécifique au bloc
			listTableSymboles.remove(listTableSymboles.size() - 1);
			
		}
		
		return bilan.a;
	}
	
	public Integer visitTantque(TantqueContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.booleen());
		if(bilan.a == 0) {
			
			//gestion tables des symboles spécifique au bloc
			if (listTableSymboles.size() == 0) {
				List<Pair<String, Double>> tableSymboles = new ArrayList<>();
				listTableSymboles.add(tableSymboles);
			} else {
				List<Pair<String, Double>> tableSymboles = new ArrayList<>(listTableSymboles.get(listTableSymboles.size() - 1));
				listTableSymboles.add(tableSymboles);
			}
			
			Double condition = bilan.b;
			int evalResult = Double.compare(condition, Double.valueOf(1));
			System.out.println("(visitTantque) - evalResult = " + evalResult);
			
			while (evalResult == 0) {
				
				visit(ctx.liste_instructions());
				Pair<Integer, Double> bilanBoucle = evaluate(ctx.booleen());
				if (bilanBoucle.a == 0) {
					condition = bilanBoucle.b;
					evalResult = Double.compare(condition, Double.valueOf(1));
					System.out.println("(visitTantque) - evalResult = " + evalResult);
				} else {
					break;
				}
			}
			
			//fin de la gestion tables des symboles spécifique au bloc
			listTableSymboles.remove(listTableSymboles.size() - 1);
		}
		
		return bilan.a;
	}
	

// Expressions
	
	@Override
	public Integer visitFloat(FloatContext ctx) {
		String floatText = ctx.FLOAT().getText();
		setValue(ctx, Double.valueOf(floatText));
		return 0;
	}
/**
 * Visite le noeud expression
 * S'il n'y a pas d'erreur (la valeur de retour de la visite vaut 0)
 *   on récupère la valeur de l'expressions à partir de la map
 * sinon
 *   on affecte une valeur quelconque
 * On retourne une paire, (code de visite, valeur)
 * @param expr
 * @return
 */

	private Pair<Integer, Double> evaluate(ParseTree expr) {	
		int b = visit(expr);
		Double val = b == 0 ? getValue(expr) : Double.POSITIVE_INFINITY;
		return new Pair<Integer, Double>(b, val);
	}

}
