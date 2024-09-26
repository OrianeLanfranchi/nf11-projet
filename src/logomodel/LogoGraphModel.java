package logomodel;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import javafx.beans.value.ChangeListener;
import logogui.GraphLineParameter;
import logoparsing.LogoLexer;
import logoparsing.LogoParser;
import logoparsing.LogoParser.ProgrammeContext;

public class LogoGraphModel {
	String program;
	List<String> rules;
	ProgrammeContext tree;
	LogoTreeVisitor visitor;

	public LogoGraphModel(String program) {
		this.program = program;
		initialize();
	}

	public void initialize() {
		CharStream str = CharStreams.fromString(program);
		LogoLexer lexer = new LogoLexer(str);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LogoParser parser = new LogoParser(tokens);
		tree = parser.programme();
		rules = Arrays.asList(parser.getRuleNames());
		visitor = new LogoTreeVisitor();
	}

	public void visit() {
		visitor.visit(tree);
	}

	public void addListeners(ChangeListener<String> logListener, ChangeListener<GraphLineParameter> lineListener) {
		visitor.logProperty().addListener(logListener);
		visitor.getTraceur().lineProperty().addListener(lineListener);
	}

	public List<String> getRules() {
		return rules;
	}

	public ProgrammeContext getTree() {
		return tree;
	}

	public LogoTreeVisitor getVisitor() {
		return visitor;
	}

}
