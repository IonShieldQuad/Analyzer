package lexis;

public class PascalSymbolPack extends SymbolPack {
    
    @Override
    protected void initSymbols() {
        this.add("program");
        this.add("var");
        this.add("integer");
        this.add("real");
        this.add("char");
        this.add("string");
        this.add("begin");
        this.add("end");
        this.add("if");
        this.add("else");
        this.add("then");
        this.add("while");
        this.add("for");
        this.add("to");
        this.add("downto");
        this.add("do");
        this.add("read");
        this.add("write");
        this.addSpaced(":=");
        this.addSpaced("+");
        this.addSpaced("-");
        this.addSpaced("*");
        this.add("div");
        this.add("mod");
        this.addSpaced(";");
        this.addSpaced(",");
        this.addSpaced(".");
        this.addSpaced("(");
        this.addSpaced(")");
        this.addSpaced(":");
        this.addSpaced("=");
        this.addSpaced("<>");
        this.addSpaced(">");
        this.addSpaced("<");
        this.addSpaced("<=");
        this.addSpaced(">=");
        this.add("not");
        this.add("or");
        this.add("and");
        this.setIdentifierCode(this.getSymbolCount());
        this.setLiteralCode(this.getSymbolCount() + 1);
    }    
}
