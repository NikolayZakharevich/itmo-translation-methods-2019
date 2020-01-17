import java.io.InputStream;
import java.text.ParseException;

class Parser {

    private LexicalAnalyzer lex;

    Tree parse(InputStream input) throws ParseException {
        lex = new LexicalAnalyzer(input);
        lex.nextToken();
        Tree regex = E();
        if (lex.getCurToken() != Token.END) {
            throw parseException();
        }
        return regex;
    }

    private Tree E() throws ParseException {
        switch (lex.getCurToken()) {
            case SYMBOL:
            case LPAREN:
                Tree concat = C();
                Tree cont = EPrime();
                return new Tree("E", concat, cont);
            default:
                throw parseException();
        }
    }

    private Tree EPrime() throws ParseException {
        switch (lex.getCurToken()) {
            case SYMBOL:
            case LPAREN:
                Tree sub = C();
                Tree cont = EPrime();
                return new Tree("E'", sub, cont);
            case RPAREN:
            case END:
                return new Tree("E'", new Tree("eps"));
            default:
                throw parseException();
        }
    }

    private Tree C() throws ParseException {
        switch (lex.getCurToken()) {
            case LPAREN:
            case SYMBOL:
                Tree option = Cl();
                Tree cont = CPrime();
                return new Tree("C", option, cont);
            default:
                throw parseException();
        }
    }

    private Tree CPrime() throws ParseException {
        switch (lex.getCurToken()) {
            case CHOICE:
                lex.nextToken();
                Tree option = Cl();
                Tree cont = CPrime();
                return new Tree("C'", new Tree("|"), option, cont);
            case LPAREN:
            case RPAREN:
            case SYMBOL:
            case END:
                return new Tree("C'", new Tree("eps"));
            default:
                throw parseException();
        }
    }

    private Tree Cl() throws ParseException {
        switch (lex.getCurToken()) {
            case LPAREN:
            case SYMBOL:
                Tree term = T();
                Tree cont = ClPrime();
                return new Tree("Cl", term, cont);
            case KLEENE_CL:

            default:
                throw parseException();
        }
    }

    private Tree ClPrime() throws ParseException {
        switch (lex.getCurToken()) {
            case KLEENE_CL:
                Tree star = Closure();
                Tree orPrime = ClPrime();
                return new Tree("Cl'", star, orPrime);
            case LPAREN:
            case RPAREN:
            case CHOICE:
            case SYMBOL:
            case END:
                return new Tree("Cl'", new Tree("eps"));
            default:
                throw parseException();
        }
    }

    private Tree Closure() throws ParseException {
        switch (lex.getCurToken()) {
            case KLEENE_CL:
                lex.nextToken();
                return new Tree("Closure'", new Tree("*"));
            default:
                throw parseException();
        }
    }

    private Tree T() throws ParseException {
        switch (lex.getCurToken()) {
            case LPAREN:
                lex.nextToken();
                Tree expr = E();
                if (lex.getCurToken() != Token.RPAREN) {
                    throw new ParseException(") expected at position ", (lex.getCurPos() - 1));
                }
                lex.nextToken();
                return new Tree("T", new Tree("("), expr, new Tree(")"));
            case SYMBOL:
                char c = (char) lex.getCurChar();
                lex.nextToken();
                return new Tree("T", new Tree(String.valueOf(c)));
            default:
                throw parseException();
        }
    }

    private ParseException parseException() {
        return new ParseException("Unexpected character '" + ((char) lex.getCurChar()) + "'", (lex.getCurPos() - 1));
    }
}
