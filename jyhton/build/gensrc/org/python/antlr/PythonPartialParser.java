// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g 2015-12-17 10:42:02

package org.python.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/** Python 2.3.3 Grammar
 *
 *  Terence Parr and Loring Craymer
 *  February 2004
 *
 *  Converted to ANTLR v3 November 2005 by Terence Parr.
 *
 *  This grammar was derived automatically from the Python 2.3.3
 *  parser grammar to get a syntactically correct ANTLR grammar
 *  for Python.  Then Terence hand tweaked it to be semantically
 *  correct; i.e., removed lookahead issues etc...  It is LL(1)
 *  except for the (sometimes optional) trailing commas and semi-colons.
 *  It needs two symbols of lookahead in this case.
 *
 *  Starting with Loring's preliminary lexer for Python, I modified it
 *  to do my version of the whole nasty INDENT/DEDENT issue just so I
 *  could understand the problem better.  This grammar requires
 *  PythonTokenStream.java to work.  Also I used some rules from the
 *  semi-formal grammar on the web for Python (automatically
 *  translated to ANTLR format by an ANTLR grammar, naturally <grin>).
 *  The lexical rules for python are particularly nasty and it took me
 *  a long time to get it 'right'; i.e., think about it in the proper
 *  way.  Resist changing the lexer unless you've used ANTLR a lot. ;)
 *
 *  I (Terence) tested this by running it on the jython-2.1/Lib
 *  directory of 40k lines of Python.
 *
 *  REQUIRES ANTLR v3
 *
 *  Updated to Python 2.5 by Frank Wierzbicki.
 *
 */
public class PythonPartialParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "INDENT", "DEDENT", "TRAILBACKSLASH", "NEWLINE", "LEADING_WS", "NAME", "DOT", "PRINT", "AND", "AS", "ASSERT", "BREAK", "CLASS", "CONTINUE", "DEF", "DELETE", "ELIF", "EXCEPT", "EXEC", "FINALLY", "FROM", "FOR", "GLOBAL", "IF", "IMPORT", "IN", "IS", "LAMBDA", "NOT", "OR", "ORELSE", "PASS", "RAISE", "RETURN", "TRY", "WHILE", "WITH", "YIELD", "BATCH", "AT", "LPAREN", "RPAREN", "COLON", "ASSIGN", "COMMA", "STAR", "DOUBLESTAR", "SEMI", "PLUSEQUAL", "MINUSEQUAL", "STAREQUAL", "SLASHEQUAL", "PERCENTEQUAL", "AMPEREQUAL", "VBAREQUAL", "CIRCUMFLEXEQUAL", "LEFTSHIFTEQUAL", "RIGHTSHIFTEQUAL", "DOUBLESTAREQUAL", "DOUBLESLASHEQUAL", "RIGHTSHIFT", "LESS", "GREATER", "EQUAL", "GREATEREQUAL", "LESSEQUAL", "ALT_NOTEQUAL", "NOTEQUAL", "VBAR", "CIRCUMFLEX", "AMPER", "LEFTSHIFT", "PLUS", "MINUS", "SLASH", "PERCENT", "DOUBLESLASH", "TILDE", "LBRACK", "RBRACK", "LCURLY", "RCURLY", "BACKQUOTE", "INT", "LONGINT", "FLOAT", "COMPLEX", "STRING", "OORELCOMMIT", "OORELINSERT", "SQL", "SIM", "Neo4j", "CONNECTTO", "DODEBUG", "PERSISTIT", "ON", "DIGITS", "Exponent", "TRIAPOS", "TRIQUOTE", "ESC", "COMMENT", "CONTINUED_LINE", "WS", "TRISTRINGPART", "STRINGPART"
    };
    public static final int PRINT=11;
    public static final int TRISTRINGPART=109;
    public static final int VBAREQUAL=58;
    public static final int MINUS=77;
    public static final int TRAILBACKSLASH=6;
    public static final int OORELCOMMIT=92;
    public static final int SLASHEQUAL=55;
    public static final int BREAK=15;
    public static final int IF=27;
    public static final int LESSEQUAL=69;
    public static final int ELIF=20;
    public static final int IN=29;
    public static final int DOT=10;
    public static final int LPAREN=44;
    public static final int IS=30;
    public static final int AS=13;
    public static final int AT=43;
    public static final int PASS=35;
    public static final int LBRACK=82;
    public static final int LEADING_WS=8;
    public static final int LONGINT=88;
    public static final int SEMI=51;
    public static final int ASSIGN=47;
    public static final int CIRCUMFLEXEQUAL=59;
    public static final int DOUBLESTAREQUAL=62;
    public static final int COMMENT=106;
    public static final int INDENT=4;
    public static final int IMPORT=28;
    public static final int DELETE=19;
    public static final int Neo4j=96;
    public static final int ESC=105;
    public static final int CONNECTTO=97;
    public static final int ALT_NOTEQUAL=70;
    public static final int DODEBUG=98;
    public static final int RCURLY=85;
    public static final int COMMA=48;
    public static final int TRIQUOTE=104;
    public static final int YIELD=41;
    public static final int STAREQUAL=54;
    public static final int GREATER=66;
    public static final int LCURLY=84;
    public static final int DOUBLESLASHEQUAL=63;
    public static final int RAISE=36;
    public static final int CONTINUE=17;
    public static final int LEFTSHIFTEQUAL=60;
    public static final int STAR=49;
    public static final int PERCENT=79;
    public static final int STRING=91;
    public static final int BACKQUOTE=86;
    public static final int PERSISTIT=99;
    public static final int CLASS=16;
    public static final int FROM=24;
    public static final int FINALLY=23;
    public static final int RIGHTSHIFTEQUAL=61;
    public static final int TRY=38;
    public static final int OORELINSERT=93;
    public static final int NEWLINE=7;
    public static final int FOR=25;
    public static final int RPAREN=45;
    public static final int EXCEPT=21;
    public static final int RIGHTSHIFT=64;
    public static final int NAME=9;
    public static final int LAMBDA=31;
    public static final int SQL=94;
    public static final int NOTEQUAL=71;
    public static final int EXEC=22;
    public static final int NOT=32;
    public static final int RBRACK=83;
    public static final int SIM=95;
    public static final int AND=12;
    public static final int STRINGPART=110;
    public static final int PERCENTEQUAL=56;
    public static final int LESS=65;
    public static final int LEFTSHIFT=75;
    public static final int PLUS=76;
    public static final int DOUBLESTAR=50;
    public static final int FLOAT=89;
    public static final int TRIAPOS=103;
    public static final int Exponent=102;
    public static final int DIGITS=101;
    public static final int INT=87;
    public static final int DOUBLESLASH=80;
    public static final int RETURN=37;
    public static final int GLOBAL=26;
    public static final int BATCH=42;
    public static final int CONTINUED_LINE=107;
    public static final int WS=108;
    public static final int EOF=-1;
    public static final int ON=100;
    public static final int CIRCUMFLEX=73;
    public static final int COMPLEX=90;
    public static final int OR=33;
    public static final int DEF=18;
    public static final int ASSERT=14;
    public static final int AMPEREQUAL=57;
    public static final int EQUAL=67;
    public static final int SLASH=78;
    public static final int AMPER=74;
    public static final int COLON=46;
    public static final int ORELSE=34;
    public static final int WITH=40;
    public static final int VBAR=72;
    public static final int PLUSEQUAL=52;
    public static final int MINUSEQUAL=53;
    public static final int GREATEREQUAL=68;
    public static final int WHILE=39;
    public static final int TILDE=81;
    public static final int DEDENT=5;

    // delegates
    // delegators


        public PythonPartialParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public PythonPartialParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return PythonPartialParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g"; }


        private ErrorHandler errorHandler = new FailFastHandler();

        protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
            throws RecognitionException {

            Object o = errorHandler.recoverFromMismatchedToken(this, input, ttype, follow);
            if (o != null) {
                return o;
            }
            return super.recoverFromMismatchedToken(input, ttype, follow);
        }




    // $ANTLR start "single_input"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:166:1: single_input : ( NEWLINE | simple_stmt | compound_stmt ( NEWLINE )? EOF );
    public final void single_input() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:168:5: ( NEWLINE | simple_stmt | compound_stmt ( NEWLINE )? EOF )
            int alt2=3;
            switch ( input.LA(1) ) {
            case NEWLINE:
                {
                alt2=1;
                }
                break;
            case TRAILBACKSLASH:
            case NAME:
            case PRINT:
            case ASSERT:
            case BREAK:
            case CONTINUE:
            case DELETE:
            case EXEC:
            case FROM:
            case GLOBAL:
            case IMPORT:
            case LAMBDA:
            case NOT:
            case PASS:
            case RAISE:
            case RETURN:
            case YIELD:
            case LPAREN:
            case PLUS:
            case MINUS:
            case TILDE:
            case LBRACK:
            case LCURLY:
            case BACKQUOTE:
            case INT:
            case LONGINT:
            case FLOAT:
            case COMPLEX:
            case STRING:
            case TRISTRINGPART:
            case STRINGPART:
                {
                alt2=2;
                }
                break;
            case CLASS:
            case DEF:
            case FOR:
            case IF:
            case TRY:
            case WHILE:
            case WITH:
            case AT:
                {
                alt2=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:168:7: NEWLINE
                    {
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_single_input72); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:169:7: simple_stmt
                    {
                    pushFollow(FOLLOW_simple_stmt_in_single_input80);
                    simple_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:170:7: compound_stmt ( NEWLINE )? EOF
                    {
                    pushFollow(FOLLOW_compound_stmt_in_single_input88);
                    compound_stmt();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:170:21: ( NEWLINE )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==NEWLINE) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:170:21: NEWLINE
                            {
                            match(input,NEWLINE,FOLLOW_NEWLINE_in_single_input90); if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,EOF,FOLLOW_EOF_in_single_input93); if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "single_input"


    // $ANTLR start "eval_input"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:174:1: eval_input : ( LEADING_WS )? ( NEWLINE )* ( testlist )? ( NEWLINE )* EOF ;
    public final void eval_input() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:5: ( ( LEADING_WS )? ( NEWLINE )* ( testlist )? ( NEWLINE )* EOF )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:7: ( LEADING_WS )? ( NEWLINE )* ( testlist )? ( NEWLINE )* EOF
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:7: ( LEADING_WS )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==LEADING_WS) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:7: LEADING_WS
                    {
                    match(input,LEADING_WS,FOLLOW_LEADING_WS_in_eval_input111); if (state.failed) return ;

                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:19: ( NEWLINE )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==NEWLINE) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:20: NEWLINE
            	    {
            	    match(input,NEWLINE,FOLLOW_NEWLINE_in_eval_input115); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:30: ( testlist )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==TRAILBACKSLASH||LA5_0==NAME||(LA5_0>=LAMBDA && LA5_0<=NOT)||LA5_0==LPAREN||(LA5_0>=PLUS && LA5_0<=MINUS)||(LA5_0>=TILDE && LA5_0<=LBRACK)||LA5_0==LCURLY||(LA5_0>=BACKQUOTE && LA5_0<=STRING)||(LA5_0>=TRISTRINGPART && LA5_0<=STRINGPART)) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:30: testlist
                    {
                    pushFollow(FOLLOW_testlist_in_eval_input119);
                    testlist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:40: ( NEWLINE )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==NEWLINE) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:175:41: NEWLINE
            	    {
            	    match(input,NEWLINE,FOLLOW_NEWLINE_in_eval_input123); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            match(input,EOF,FOLLOW_EOF_in_eval_input127); if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "eval_input"


    // $ANTLR start "dotted_attr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:179:1: dotted_attr : NAME ( ( DOT NAME )+ | ) ;
    public final void dotted_attr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:180:5: ( NAME ( ( DOT NAME )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:180:7: NAME ( ( DOT NAME )+ | )
            {
            match(input,NAME,FOLLOW_NAME_in_dotted_attr145); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:181:7: ( ( DOT NAME )+ | )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==DOT) ) {
                alt8=1;
            }
            else if ( (LA8_0==NEWLINE||LA8_0==LPAREN) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:181:9: ( DOT NAME )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:181:9: ( DOT NAME )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==DOT) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:181:10: DOT NAME
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_dotted_attr156); if (state.failed) return ;
                    	    match(input,NAME,FOLLOW_NAME_in_dotted_attr158); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:183:7: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dotted_attr"


    // $ANTLR start "attr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:201:1: attr : ( NAME | AND | AS | ASSERT | BREAK | CLASS | CONTINUE | DEF | DELETE | ELIF | EXCEPT | EXEC | FINALLY | FROM | FOR | GLOBAL | IF | IMPORT | IN | IS | LAMBDA | NOT | OR | ORELSE | PASS | PRINT | RAISE | RETURN | TRY | WHILE | WITH | YIELD );
    public final void attr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:202:5: ( NAME | AND | AS | ASSERT | BREAK | CLASS | CONTINUE | DEF | DELETE | ELIF | EXCEPT | EXEC | FINALLY | FROM | FOR | GLOBAL | IF | IMPORT | IN | IS | LAMBDA | NOT | OR | ORELSE | PASS | PRINT | RAISE | RETURN | TRY | WHILE | WITH | YIELD )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:
            {
            if ( input.LA(1)==NAME||(input.LA(1)>=PRINT && input.LA(1)<=YIELD) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "attr"


    // $ANTLR start "decorator"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:237:1: decorator : AT dotted_attr ( LPAREN ( arglist | ) RPAREN | ) NEWLINE ;
    public final void decorator() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:238:5: ( AT dotted_attr ( LPAREN ( arglist | ) RPAREN | ) NEWLINE )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:238:7: AT dotted_attr ( LPAREN ( arglist | ) RPAREN | ) NEWLINE
            {
            match(input,AT,FOLLOW_AT_in_decorator464); if (state.failed) return ;
            pushFollow(FOLLOW_dotted_attr_in_decorator466);
            dotted_attr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:239:5: ( LPAREN ( arglist | ) RPAREN | )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==LPAREN) ) {
                alt10=1;
            }
            else if ( (LA10_0==NEWLINE) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:239:7: LPAREN ( arglist | ) RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_decorator474); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:240:7: ( arglist | )
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==TRAILBACKSLASH||LA9_0==NAME||(LA9_0>=LAMBDA && LA9_0<=NOT)||LA9_0==LPAREN||(LA9_0>=STAR && LA9_0<=DOUBLESTAR)||(LA9_0>=PLUS && LA9_0<=MINUS)||(LA9_0>=TILDE && LA9_0<=LBRACK)||LA9_0==LCURLY||(LA9_0>=BACKQUOTE && LA9_0<=STRING)||(LA9_0>=TRISTRINGPART && LA9_0<=STRINGPART)) ) {
                        alt9=1;
                    }
                    else if ( (LA9_0==RPAREN) ) {
                        alt9=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                        throw nvae;
                    }
                    switch (alt9) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:240:9: arglist
                            {
                            pushFollow(FOLLOW_arglist_in_decorator484);
                            arglist();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:242:7: 
                            {
                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_decorator508); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:245:5: 
                    {
                    }
                    break;

            }

            match(input,NEWLINE,FOLLOW_NEWLINE_in_decorator522); if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "decorator"


    // $ANTLR start "decorators"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:249:1: decorators : ( decorator )+ ;
    public final void decorators() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:250:5: ( ( decorator )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:250:7: ( decorator )+
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:250:7: ( decorator )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==AT) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:250:7: decorator
            	    {
            	    pushFollow(FOLLOW_decorator_in_decorators540);
            	    decorator();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "decorators"


    // $ANTLR start "funcdef"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:254:1: funcdef : ( decorators )? DEF NAME parameters COLON suite ;
    public final void funcdef() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:255:5: ( ( decorators )? DEF NAME parameters COLON suite )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:255:7: ( decorators )? DEF NAME parameters COLON suite
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:255:7: ( decorators )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==AT) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:255:7: decorators
                    {
                    pushFollow(FOLLOW_decorators_in_funcdef559);
                    decorators();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,DEF,FOLLOW_DEF_in_funcdef562); if (state.failed) return ;
            match(input,NAME,FOLLOW_NAME_in_funcdef564); if (state.failed) return ;
            pushFollow(FOLLOW_parameters_in_funcdef566);
            parameters();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_funcdef568); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_funcdef570);
            suite();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "funcdef"


    // $ANTLR start "parameters"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:259:1: parameters : LPAREN ( varargslist | ) RPAREN ;
    public final void parameters() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:260:5: ( LPAREN ( varargslist | ) RPAREN )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:260:7: LPAREN ( varargslist | ) RPAREN
            {
            match(input,LPAREN,FOLLOW_LPAREN_in_parameters588); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:261:7: ( varargslist | )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==NAME||LA13_0==LPAREN||(LA13_0>=STAR && LA13_0<=DOUBLESTAR)) ) {
                alt13=1;
            }
            else if ( (LA13_0==RPAREN) ) {
                alt13=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:261:8: varargslist
                    {
                    pushFollow(FOLLOW_varargslist_in_parameters597);
                    varargslist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:263:7: 
                    {
                    }
                    break;

            }

            match(input,RPAREN,FOLLOW_RPAREN_in_parameters621); if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "parameters"


    // $ANTLR start "defparameter"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:268:1: defparameter : fpdef ( ASSIGN test )? ;
    public final void defparameter() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:269:5: ( fpdef ( ASSIGN test )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:269:7: fpdef ( ASSIGN test )?
            {
            pushFollow(FOLLOW_fpdef_in_defparameter639);
            fpdef();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:269:13: ( ASSIGN test )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==ASSIGN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:269:14: ASSIGN test
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_defparameter642); if (state.failed) return ;
                    pushFollow(FOLLOW_test_in_defparameter644);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "defparameter"


    // $ANTLR start "varargslist"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:275:1: varargslist : ( defparameter ( options {greedy=true; } : COMMA defparameter )* ( COMMA ( STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME )? )? | STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME );
    public final void varargslist() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:276:5: ( defparameter ( options {greedy=true; } : COMMA defparameter )* ( COMMA ( STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME )? )? | STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME )
            int alt20=3;
            switch ( input.LA(1) ) {
            case NAME:
            case LPAREN:
                {
                alt20=1;
                }
                break;
            case STAR:
                {
                alt20=2;
                }
                break;
            case DOUBLESTAR:
                {
                alt20=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:276:7: defparameter ( options {greedy=true; } : COMMA defparameter )* ( COMMA ( STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME )? )?
                    {
                    pushFollow(FOLLOW_defparameter_in_varargslist666);
                    defparameter();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:276:20: ( options {greedy=true; } : COMMA defparameter )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            int LA15_1 = input.LA(2);

                            if ( (LA15_1==NAME||LA15_1==LPAREN) ) {
                                alt15=1;
                            }


                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:276:44: COMMA defparameter
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_varargslist676); if (state.failed) return ;
                    	    pushFollow(FOLLOW_defparameter_in_varargslist678);
                    	    defparameter();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:277:7: ( COMMA ( STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME )? )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==COMMA) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:277:8: COMMA ( STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME )?
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_varargslist689); if (state.failed) return ;
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:278:11: ( STAR NAME ( COMMA DOUBLESTAR NAME )? | DOUBLESTAR NAME )?
                            int alt17=3;
                            int LA17_0 = input.LA(1);

                            if ( (LA17_0==STAR) ) {
                                alt17=1;
                            }
                            else if ( (LA17_0==DOUBLESTAR) ) {
                                alt17=2;
                            }
                            switch (alt17) {
                                case 1 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:278:12: STAR NAME ( COMMA DOUBLESTAR NAME )?
                                    {
                                    match(input,STAR,FOLLOW_STAR_in_varargslist702); if (state.failed) return ;
                                    match(input,NAME,FOLLOW_NAME_in_varargslist704); if (state.failed) return ;
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:278:22: ( COMMA DOUBLESTAR NAME )?
                                    int alt16=2;
                                    int LA16_0 = input.LA(1);

                                    if ( (LA16_0==COMMA) ) {
                                        alt16=1;
                                    }
                                    switch (alt16) {
                                        case 1 :
                                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:278:23: COMMA DOUBLESTAR NAME
                                            {
                                            match(input,COMMA,FOLLOW_COMMA_in_varargslist707); if (state.failed) return ;
                                            match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_varargslist709); if (state.failed) return ;
                                            match(input,NAME,FOLLOW_NAME_in_varargslist711); if (state.failed) return ;

                                            }
                                            break;

                                    }


                                    }
                                    break;
                                case 2 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:279:13: DOUBLESTAR NAME
                                    {
                                    match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_varargslist727); if (state.failed) return ;
                                    match(input,NAME,FOLLOW_NAME_in_varargslist729); if (state.failed) return ;

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:282:7: STAR NAME ( COMMA DOUBLESTAR NAME )?
                    {
                    match(input,STAR,FOLLOW_STAR_in_varargslist759); if (state.failed) return ;
                    match(input,NAME,FOLLOW_NAME_in_varargslist761); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:282:17: ( COMMA DOUBLESTAR NAME )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==COMMA) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:282:18: COMMA DOUBLESTAR NAME
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_varargslist764); if (state.failed) return ;
                            match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_varargslist766); if (state.failed) return ;
                            match(input,NAME,FOLLOW_NAME_in_varargslist768); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:283:7: DOUBLESTAR NAME
                    {
                    match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_varargslist778); if (state.failed) return ;
                    match(input,NAME,FOLLOW_NAME_in_varargslist780); if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "varargslist"


    // $ANTLR start "fpdef"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:287:1: fpdef : ( NAME | ( LPAREN fpdef COMMA )=> LPAREN fplist RPAREN | LPAREN fplist RPAREN );
    public final void fpdef() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:288:5: ( NAME | ( LPAREN fpdef COMMA )=> LPAREN fplist RPAREN | LPAREN fplist RPAREN )
            int alt21=3;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==NAME) ) {
                alt21=1;
            }
            else if ( (LA21_0==LPAREN) ) {
                int LA21_2 = input.LA(2);

                if ( (synpred1_PythonPartial()) ) {
                    alt21=2;
                }
                else if ( (true) ) {
                    alt21=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:288:7: NAME
                    {
                    match(input,NAME,FOLLOW_NAME_in_fpdef798); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:289:7: ( LPAREN fpdef COMMA )=> LPAREN fplist RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_fpdef816); if (state.failed) return ;
                    pushFollow(FOLLOW_fplist_in_fpdef818);
                    fplist();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RPAREN,FOLLOW_RPAREN_in_fpdef820); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:290:7: LPAREN fplist RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_fpdef828); if (state.failed) return ;
                    pushFollow(FOLLOW_fplist_in_fpdef830);
                    fplist();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RPAREN,FOLLOW_RPAREN_in_fpdef832); if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "fpdef"


    // $ANTLR start "fplist"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:294:1: fplist : fpdef ( options {greedy=true; } : COMMA fpdef )* ( COMMA )? ;
    public final void fplist() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:295:5: ( fpdef ( options {greedy=true; } : COMMA fpdef )* ( COMMA )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:295:7: fpdef ( options {greedy=true; } : COMMA fpdef )* ( COMMA )?
            {
            pushFollow(FOLLOW_fpdef_in_fplist850);
            fpdef();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:296:7: ( options {greedy=true; } : COMMA fpdef )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==COMMA) ) {
                    int LA22_1 = input.LA(2);

                    if ( (LA22_1==NAME||LA22_1==LPAREN) ) {
                        alt22=1;
                    }


                }


                switch (alt22) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:296:31: COMMA fpdef
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fplist866); if (state.failed) return ;
            	    pushFollow(FOLLOW_fpdef_in_fplist868);
            	    fpdef();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:296:45: ( COMMA )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==COMMA) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:296:46: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_fplist873); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "fplist"


    // $ANTLR start "stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:300:1: stmt : ( simple_stmt | compound_stmt );
    public final void stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:301:5: ( simple_stmt | compound_stmt )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==TRAILBACKSLASH||LA24_0==NAME||LA24_0==PRINT||(LA24_0>=ASSERT && LA24_0<=BREAK)||LA24_0==CONTINUE||LA24_0==DELETE||LA24_0==EXEC||LA24_0==FROM||LA24_0==GLOBAL||LA24_0==IMPORT||(LA24_0>=LAMBDA && LA24_0<=NOT)||(LA24_0>=PASS && LA24_0<=RETURN)||LA24_0==YIELD||LA24_0==LPAREN||(LA24_0>=PLUS && LA24_0<=MINUS)||(LA24_0>=TILDE && LA24_0<=LBRACK)||LA24_0==LCURLY||(LA24_0>=BACKQUOTE && LA24_0<=STRING)||(LA24_0>=TRISTRINGPART && LA24_0<=STRINGPART)) ) {
                alt24=1;
            }
            else if ( (LA24_0==CLASS||LA24_0==DEF||LA24_0==FOR||LA24_0==IF||(LA24_0>=TRY && LA24_0<=WITH)||LA24_0==AT) ) {
                alt24=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:301:7: simple_stmt
                    {
                    pushFollow(FOLLOW_simple_stmt_in_stmt893);
                    simple_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:302:7: compound_stmt
                    {
                    pushFollow(FOLLOW_compound_stmt_in_stmt901);
                    compound_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "stmt"


    // $ANTLR start "simple_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:306:1: simple_stmt : small_stmt ( options {greedy=true; } : SEMI small_stmt )* ( SEMI )? ( NEWLINE | EOF ) ;
    public final void simple_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:307:5: ( small_stmt ( options {greedy=true; } : SEMI small_stmt )* ( SEMI )? ( NEWLINE | EOF ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:307:7: small_stmt ( options {greedy=true; } : SEMI small_stmt )* ( SEMI )? ( NEWLINE | EOF )
            {
            pushFollow(FOLLOW_small_stmt_in_simple_stmt919);
            small_stmt();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:307:18: ( options {greedy=true; } : SEMI small_stmt )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==SEMI) ) {
                    int LA25_1 = input.LA(2);

                    if ( (LA25_1==TRAILBACKSLASH||LA25_1==NAME||LA25_1==PRINT||(LA25_1>=ASSERT && LA25_1<=BREAK)||LA25_1==CONTINUE||LA25_1==DELETE||LA25_1==EXEC||LA25_1==FROM||LA25_1==GLOBAL||LA25_1==IMPORT||(LA25_1>=LAMBDA && LA25_1<=NOT)||(LA25_1>=PASS && LA25_1<=RETURN)||LA25_1==YIELD||LA25_1==LPAREN||(LA25_1>=PLUS && LA25_1<=MINUS)||(LA25_1>=TILDE && LA25_1<=LBRACK)||LA25_1==LCURLY||(LA25_1>=BACKQUOTE && LA25_1<=STRING)||(LA25_1>=TRISTRINGPART && LA25_1<=STRINGPART)) ) {
                        alt25=1;
                    }


                }


                switch (alt25) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:307:42: SEMI small_stmt
            	    {
            	    match(input,SEMI,FOLLOW_SEMI_in_simple_stmt929); if (state.failed) return ;
            	    pushFollow(FOLLOW_small_stmt_in_simple_stmt931);
            	    small_stmt();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:307:60: ( SEMI )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==SEMI) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:307:61: SEMI
                    {
                    match(input,SEMI,FOLLOW_SEMI_in_simple_stmt936); if (state.failed) return ;

                    }
                    break;

            }

            if ( input.LA(1)==EOF||input.LA(1)==NEWLINE ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "simple_stmt"


    // $ANTLR start "small_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:312:1: small_stmt : ( expr_stmt | print_stmt | del_stmt | pass_stmt | flow_stmt | import_stmt | global_stmt | exec_stmt | assert_stmt );
    public final void small_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:312:12: ( expr_stmt | print_stmt | del_stmt | pass_stmt | flow_stmt | import_stmt | global_stmt | exec_stmt | assert_stmt )
            int alt27=9;
            alt27 = dfa27.predict(input);
            switch (alt27) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:312:14: expr_stmt
                    {
                    pushFollow(FOLLOW_expr_stmt_in_small_stmt959);
                    expr_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:313:14: print_stmt
                    {
                    pushFollow(FOLLOW_print_stmt_in_small_stmt974);
                    print_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:314:14: del_stmt
                    {
                    pushFollow(FOLLOW_del_stmt_in_small_stmt989);
                    del_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:315:14: pass_stmt
                    {
                    pushFollow(FOLLOW_pass_stmt_in_small_stmt1004);
                    pass_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:316:14: flow_stmt
                    {
                    pushFollow(FOLLOW_flow_stmt_in_small_stmt1019);
                    flow_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:317:14: import_stmt
                    {
                    pushFollow(FOLLOW_import_stmt_in_small_stmt1034);
                    import_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:318:14: global_stmt
                    {
                    pushFollow(FOLLOW_global_stmt_in_small_stmt1049);
                    global_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:319:14: exec_stmt
                    {
                    pushFollow(FOLLOW_exec_stmt_in_small_stmt1064);
                    exec_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:320:14: assert_stmt
                    {
                    pushFollow(FOLLOW_assert_stmt_in_small_stmt1079);
                    assert_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "small_stmt"


    // $ANTLR start "expr_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:325:1: expr_stmt : ( ( testlist augassign )=> testlist ( ( augassign yield_expr ) | ( augassign testlist ) ) | ( testlist ASSIGN )=> testlist ( | ( ( ASSIGN testlist )+ ) | ( ( ASSIGN yield_expr )+ ) ) | testlist ) ;
    public final void expr_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:326:5: ( ( ( testlist augassign )=> testlist ( ( augassign yield_expr ) | ( augassign testlist ) ) | ( testlist ASSIGN )=> testlist ( | ( ( ASSIGN testlist )+ ) | ( ( ASSIGN yield_expr )+ ) ) | testlist ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:326:7: ( ( testlist augassign )=> testlist ( ( augassign yield_expr ) | ( augassign testlist ) ) | ( testlist ASSIGN )=> testlist ( | ( ( ASSIGN testlist )+ ) | ( ( ASSIGN yield_expr )+ ) ) | testlist )
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:326:7: ( ( testlist augassign )=> testlist ( ( augassign yield_expr ) | ( augassign testlist ) ) | ( testlist ASSIGN )=> testlist ( | ( ( ASSIGN testlist )+ ) | ( ( ASSIGN yield_expr )+ ) ) | testlist )
            int alt32=3;
            alt32 = dfa32.predict(input);
            switch (alt32) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:326:8: ( testlist augassign )=> testlist ( ( augassign yield_expr ) | ( augassign testlist ) )
                    {
                    pushFollow(FOLLOW_testlist_in_expr_stmt1114);
                    testlist();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:327:9: ( ( augassign yield_expr ) | ( augassign testlist ) )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( ((LA28_0>=PLUSEQUAL && LA28_0<=DOUBLESLASHEQUAL)) ) {
                        int LA28_1 = input.LA(2);

                        if ( (LA28_1==YIELD) ) {
                            alt28=1;
                        }
                        else if ( (LA28_1==TRAILBACKSLASH||LA28_1==NAME||(LA28_1>=LAMBDA && LA28_1<=NOT)||LA28_1==LPAREN||(LA28_1>=PLUS && LA28_1<=MINUS)||(LA28_1>=TILDE && LA28_1<=LBRACK)||LA28_1==LCURLY||(LA28_1>=BACKQUOTE && LA28_1<=STRING)||(LA28_1>=TRISTRINGPART && LA28_1<=STRINGPART)) ) {
                            alt28=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 28, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 0, input);

                        throw nvae;
                    }
                    switch (alt28) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:327:11: ( augassign yield_expr )
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:327:11: ( augassign yield_expr )
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:327:12: augassign yield_expr
                            {
                            pushFollow(FOLLOW_augassign_in_expr_stmt1127);
                            augassign();

                            state._fsp--;
                            if (state.failed) return ;
                            pushFollow(FOLLOW_yield_expr_in_expr_stmt1129);
                            yield_expr();

                            state._fsp--;
                            if (state.failed) return ;

                            }


                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:329:11: ( augassign testlist )
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:329:11: ( augassign testlist )
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:329:12: augassign testlist
                            {
                            pushFollow(FOLLOW_augassign_in_expr_stmt1154);
                            augassign();

                            state._fsp--;
                            if (state.failed) return ;
                            pushFollow(FOLLOW_testlist_in_expr_stmt1156);
                            testlist();

                            state._fsp--;
                            if (state.failed) return ;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:332:7: ( testlist ASSIGN )=> testlist ( | ( ( ASSIGN testlist )+ ) | ( ( ASSIGN yield_expr )+ ) )
                    {
                    pushFollow(FOLLOW_testlist_in_expr_stmt1194);
                    testlist();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:333:9: ( | ( ( ASSIGN testlist )+ ) | ( ( ASSIGN yield_expr )+ ) )
                    int alt31=3;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==EOF||LA31_0==NEWLINE||LA31_0==SEMI) ) {
                        alt31=1;
                    }
                    else if ( (LA31_0==ASSIGN) ) {
                        int LA31_2 = input.LA(2);

                        if ( (LA31_2==YIELD) ) {
                            alt31=3;
                        }
                        else if ( (LA31_2==TRAILBACKSLASH||LA31_2==NAME||(LA31_2>=LAMBDA && LA31_2<=NOT)||LA31_2==LPAREN||(LA31_2>=PLUS && LA31_2<=MINUS)||(LA31_2>=TILDE && LA31_2<=LBRACK)||LA31_2==LCURLY||(LA31_2>=BACKQUOTE && LA31_2<=STRING)||(LA31_2>=TRISTRINGPART && LA31_2<=STRINGPART)) ) {
                            alt31=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 31, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 31, 0, input);

                        throw nvae;
                    }
                    switch (alt31) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:334:9: 
                            {
                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:334:11: ( ( ASSIGN testlist )+ )
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:334:11: ( ( ASSIGN testlist )+ )
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:334:12: ( ASSIGN testlist )+
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:334:12: ( ASSIGN testlist )+
                            int cnt29=0;
                            loop29:
                            do {
                                int alt29=2;
                                int LA29_0 = input.LA(1);

                                if ( (LA29_0==ASSIGN) ) {
                                    alt29=1;
                                }


                                switch (alt29) {
                            	case 1 :
                            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:334:13: ASSIGN testlist
                            	    {
                            	    match(input,ASSIGN,FOLLOW_ASSIGN_in_expr_stmt1218); if (state.failed) return ;
                            	    pushFollow(FOLLOW_testlist_in_expr_stmt1220);
                            	    testlist();

                            	    state._fsp--;
                            	    if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt29 >= 1 ) break loop29;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(29, input);
                                        throw eee;
                                }
                                cnt29++;
                            } while (true);


                            }


                            }
                            break;
                        case 3 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:336:11: ( ( ASSIGN yield_expr )+ )
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:336:11: ( ( ASSIGN yield_expr )+ )
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:336:12: ( ASSIGN yield_expr )+
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:336:12: ( ASSIGN yield_expr )+
                            int cnt30=0;
                            loop30:
                            do {
                                int alt30=2;
                                int LA30_0 = input.LA(1);

                                if ( (LA30_0==ASSIGN) ) {
                                    alt30=1;
                                }


                                switch (alt30) {
                            	case 1 :
                            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:336:13: ASSIGN yield_expr
                            	    {
                            	    match(input,ASSIGN,FOLLOW_ASSIGN_in_expr_stmt1248); if (state.failed) return ;
                            	    pushFollow(FOLLOW_yield_expr_in_expr_stmt1250);
                            	    yield_expr();

                            	    state._fsp--;
                            	    if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt30 >= 1 ) break loop30;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(30, input);
                                        throw eee;
                                }
                                cnt30++;
                            } while (true);


                            }


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:339:7: testlist
                    {
                    pushFollow(FOLLOW_testlist_in_expr_stmt1282);
                    testlist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "expr_stmt"


    // $ANTLR start "augassign"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:345:1: augassign : ( PLUSEQUAL | MINUSEQUAL | STAREQUAL | SLASHEQUAL | PERCENTEQUAL | AMPEREQUAL | VBAREQUAL | CIRCUMFLEXEQUAL | LEFTSHIFTEQUAL | RIGHTSHIFTEQUAL | DOUBLESTAREQUAL | DOUBLESLASHEQUAL );
    public final void augassign() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:346:5: ( PLUSEQUAL | MINUSEQUAL | STAREQUAL | SLASHEQUAL | PERCENTEQUAL | AMPEREQUAL | VBAREQUAL | CIRCUMFLEXEQUAL | LEFTSHIFTEQUAL | RIGHTSHIFTEQUAL | DOUBLESTAREQUAL | DOUBLESLASHEQUAL )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:
            {
            if ( (input.LA(1)>=PLUSEQUAL && input.LA(1)<=DOUBLESLASHEQUAL) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "augassign"


    // $ANTLR start "print_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:362:1: print_stmt : PRINT ( printlist | RIGHTSHIFT printlist | ) ;
    public final void print_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:363:5: ( PRINT ( printlist | RIGHTSHIFT printlist | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:363:7: PRINT ( printlist | RIGHTSHIFT printlist | )
            {
            match(input,PRINT,FOLLOW_PRINT_in_print_stmt1414); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:364:7: ( printlist | RIGHTSHIFT printlist | )
            int alt33=3;
            switch ( input.LA(1) ) {
            case TRAILBACKSLASH:
            case NAME:
            case LAMBDA:
            case NOT:
            case LPAREN:
            case PLUS:
            case MINUS:
            case TILDE:
            case LBRACK:
            case LCURLY:
            case BACKQUOTE:
            case INT:
            case LONGINT:
            case FLOAT:
            case COMPLEX:
            case STRING:
            case TRISTRINGPART:
            case STRINGPART:
                {
                alt33=1;
                }
                break;
            case RIGHTSHIFT:
                {
                alt33=2;
                }
                break;
            case EOF:
            case NEWLINE:
            case SEMI:
                {
                alt33=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:364:8: printlist
                    {
                    pushFollow(FOLLOW_printlist_in_print_stmt1423);
                    printlist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:365:9: RIGHTSHIFT printlist
                    {
                    match(input,RIGHTSHIFT,FOLLOW_RIGHTSHIFT_in_print_stmt1433); if (state.failed) return ;
                    pushFollow(FOLLOW_printlist_in_print_stmt1435);
                    printlist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:367:7: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "print_stmt"


    // $ANTLR start "printlist"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:371:1: printlist : ( ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )? | test );
    public final void printlist() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:372:5: ( ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )? | test )
            int alt36=2;
            alt36 = dfa36.predict(input);
            switch (alt36) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:372:7: ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_test_in_printlist1486);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:373:13: ( options {k=2; } : COMMA test )*
                    loop34:
                    do {
                        int alt34=2;
                        alt34 = dfa34.predict(input);
                        switch (alt34) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:373:30: COMMA test
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_printlist1497); if (state.failed) return ;
                    	    pushFollow(FOLLOW_test_in_printlist1499);
                    	    test();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop34;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:374:10: ( COMMA )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==COMMA) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:374:11: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_printlist1513); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:375:7: test
                    {
                    pushFollow(FOLLOW_test_in_printlist1523);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "printlist"


    // $ANTLR start "del_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:379:1: del_stmt : DELETE exprlist ;
    public final void del_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:380:5: ( DELETE exprlist )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:380:7: DELETE exprlist
            {
            match(input,DELETE,FOLLOW_DELETE_in_del_stmt1541); if (state.failed) return ;
            pushFollow(FOLLOW_exprlist_in_del_stmt1543);
            exprlist();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "del_stmt"


    // $ANTLR start "pass_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:384:1: pass_stmt : PASS ;
    public final void pass_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:385:5: ( PASS )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:385:7: PASS
            {
            match(input,PASS,FOLLOW_PASS_in_pass_stmt1561); if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "pass_stmt"


    // $ANTLR start "flow_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:389:1: flow_stmt : ( break_stmt | continue_stmt | return_stmt | raise_stmt | yield_stmt );
    public final void flow_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:390:5: ( break_stmt | continue_stmt | return_stmt | raise_stmt | yield_stmt )
            int alt37=5;
            switch ( input.LA(1) ) {
            case BREAK:
                {
                alt37=1;
                }
                break;
            case CONTINUE:
                {
                alt37=2;
                }
                break;
            case RETURN:
                {
                alt37=3;
                }
                break;
            case RAISE:
                {
                alt37=4;
                }
                break;
            case YIELD:
                {
                alt37=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:390:7: break_stmt
                    {
                    pushFollow(FOLLOW_break_stmt_in_flow_stmt1579);
                    break_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:391:7: continue_stmt
                    {
                    pushFollow(FOLLOW_continue_stmt_in_flow_stmt1587);
                    continue_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:392:7: return_stmt
                    {
                    pushFollow(FOLLOW_return_stmt_in_flow_stmt1595);
                    return_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:393:7: raise_stmt
                    {
                    pushFollow(FOLLOW_raise_stmt_in_flow_stmt1603);
                    raise_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:394:7: yield_stmt
                    {
                    pushFollow(FOLLOW_yield_stmt_in_flow_stmt1611);
                    yield_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "flow_stmt"


    // $ANTLR start "break_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:398:1: break_stmt : BREAK ;
    public final void break_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:399:5: ( BREAK )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:399:7: BREAK
            {
            match(input,BREAK,FOLLOW_BREAK_in_break_stmt1629); if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "break_stmt"


    // $ANTLR start "continue_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:403:1: continue_stmt : CONTINUE ;
    public final void continue_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:404:5: ( CONTINUE )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:404:7: CONTINUE
            {
            match(input,CONTINUE,FOLLOW_CONTINUE_in_continue_stmt1647); if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "continue_stmt"


    // $ANTLR start "return_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:408:1: return_stmt : RETURN ( testlist | ) ;
    public final void return_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:409:5: ( RETURN ( testlist | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:409:7: RETURN ( testlist | )
            {
            match(input,RETURN,FOLLOW_RETURN_in_return_stmt1665); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:410:7: ( testlist | )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==TRAILBACKSLASH||LA38_0==NAME||(LA38_0>=LAMBDA && LA38_0<=NOT)||LA38_0==LPAREN||(LA38_0>=PLUS && LA38_0<=MINUS)||(LA38_0>=TILDE && LA38_0<=LBRACK)||LA38_0==LCURLY||(LA38_0>=BACKQUOTE && LA38_0<=STRING)||(LA38_0>=TRISTRINGPART && LA38_0<=STRINGPART)) ) {
                alt38=1;
            }
            else if ( (LA38_0==EOF||LA38_0==NEWLINE||LA38_0==SEMI) ) {
                alt38=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:410:8: testlist
                    {
                    pushFollow(FOLLOW_testlist_in_return_stmt1674);
                    testlist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:412:7: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "return_stmt"


    // $ANTLR start "yield_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:416:1: yield_stmt : yield_expr ;
    public final void yield_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:417:5: ( yield_expr )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:417:7: yield_expr
            {
            pushFollow(FOLLOW_yield_expr_in_yield_stmt1708);
            yield_expr();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "yield_stmt"


    // $ANTLR start "raise_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:421:1: raise_stmt : RAISE ( test ( COMMA test ( COMMA test )? )? )? ;
    public final void raise_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:422:5: ( RAISE ( test ( COMMA test ( COMMA test )? )? )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:422:7: RAISE ( test ( COMMA test ( COMMA test )? )? )?
            {
            match(input,RAISE,FOLLOW_RAISE_in_raise_stmt1726); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:422:13: ( test ( COMMA test ( COMMA test )? )? )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==TRAILBACKSLASH||LA41_0==NAME||(LA41_0>=LAMBDA && LA41_0<=NOT)||LA41_0==LPAREN||(LA41_0>=PLUS && LA41_0<=MINUS)||(LA41_0>=TILDE && LA41_0<=LBRACK)||LA41_0==LCURLY||(LA41_0>=BACKQUOTE && LA41_0<=STRING)||(LA41_0>=TRISTRINGPART && LA41_0<=STRINGPART)) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:422:14: test ( COMMA test ( COMMA test )? )?
                    {
                    pushFollow(FOLLOW_test_in_raise_stmt1729);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:422:19: ( COMMA test ( COMMA test )? )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==COMMA) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:422:20: COMMA test ( COMMA test )?
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_raise_stmt1732); if (state.failed) return ;
                            pushFollow(FOLLOW_test_in_raise_stmt1734);
                            test();

                            state._fsp--;
                            if (state.failed) return ;
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:423:9: ( COMMA test )?
                            int alt39=2;
                            int LA39_0 = input.LA(1);

                            if ( (LA39_0==COMMA) ) {
                                alt39=1;
                            }
                            switch (alt39) {
                                case 1 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:423:10: COMMA test
                                    {
                                    match(input,COMMA,FOLLOW_COMMA_in_raise_stmt1745); if (state.failed) return ;
                                    pushFollow(FOLLOW_test_in_raise_stmt1747);
                                    test();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "raise_stmt"


    // $ANTLR start "import_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:427:1: import_stmt : ( import_name | import_from );
    public final void import_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:428:5: ( import_name | import_from )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==IMPORT) ) {
                alt42=1;
            }
            else if ( (LA42_0==FROM) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:428:7: import_name
                    {
                    pushFollow(FOLLOW_import_name_in_import_stmt1771);
                    import_name();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:429:7: import_from
                    {
                    pushFollow(FOLLOW_import_from_in_import_stmt1779);
                    import_from();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "import_stmt"


    // $ANTLR start "import_name"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:433:1: import_name : IMPORT dotted_as_names ;
    public final void import_name() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:434:5: ( IMPORT dotted_as_names )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:434:7: IMPORT dotted_as_names
            {
            match(input,IMPORT,FOLLOW_IMPORT_in_import_name1797); if (state.failed) return ;
            pushFollow(FOLLOW_dotted_as_names_in_import_name1799);
            dotted_as_names();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "import_name"


    // $ANTLR start "import_from"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:439:1: import_from : FROM ( ( DOT )* dotted_name | ( DOT )+ ) IMPORT ( STAR | import_as_names | LPAREN import_as_names ( COMMA )? RPAREN ) ;
    public final void import_from() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:5: ( FROM ( ( DOT )* dotted_name | ( DOT )+ ) IMPORT ( STAR | import_as_names | LPAREN import_as_names ( COMMA )? RPAREN ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:7: FROM ( ( DOT )* dotted_name | ( DOT )+ ) IMPORT ( STAR | import_as_names | LPAREN import_as_names ( COMMA )? RPAREN )
            {
            match(input,FROM,FOLLOW_FROM_in_import_from1818); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:12: ( ( DOT )* dotted_name | ( DOT )+ )
            int alt45=2;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:13: ( DOT )* dotted_name
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:13: ( DOT )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==DOT) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:13: DOT
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_import_from1821); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);

                    pushFollow(FOLLOW_dotted_name_in_import_from1824);
                    dotted_name();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:32: ( DOT )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:32: ( DOT )+
                    int cnt44=0;
                    loop44:
                    do {
                        int alt44=2;
                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==DOT) ) {
                            alt44=1;
                        }


                        switch (alt44) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:440:32: DOT
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_import_from1828); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt44 >= 1 ) break loop44;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(44, input);
                                throw eee;
                        }
                        cnt44++;
                    } while (true);


                    }
                    break;

            }

            match(input,IMPORT,FOLLOW_IMPORT_in_import_from1832); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:441:9: ( STAR | import_as_names | LPAREN import_as_names ( COMMA )? RPAREN )
            int alt47=3;
            switch ( input.LA(1) ) {
            case STAR:
                {
                alt47=1;
                }
                break;
            case NAME:
                {
                alt47=2;
                }
                break;
            case LPAREN:
                {
                alt47=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:441:10: STAR
                    {
                    match(input,STAR,FOLLOW_STAR_in_import_from1843); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:442:11: import_as_names
                    {
                    pushFollow(FOLLOW_import_as_names_in_import_from1855);
                    import_as_names();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:443:11: LPAREN import_as_names ( COMMA )? RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_import_from1867); if (state.failed) return ;
                    pushFollow(FOLLOW_import_as_names_in_import_from1869);
                    import_as_names();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:443:34: ( COMMA )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==COMMA) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:443:34: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_import_from1871); if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_import_from1874); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "import_from"


    // $ANTLR start "import_as_names"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:448:1: import_as_names : import_as_name ( COMMA import_as_name )* ;
    public final void import_as_names() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:449:5: ( import_as_name ( COMMA import_as_name )* )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:449:7: import_as_name ( COMMA import_as_name )*
            {
            pushFollow(FOLLOW_import_as_name_in_import_as_names1902);
            import_as_name();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:449:22: ( COMMA import_as_name )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==COMMA) ) {
                    int LA48_2 = input.LA(2);

                    if ( (LA48_2==NAME) ) {
                        alt48=1;
                    }


                }


                switch (alt48) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:449:23: COMMA import_as_name
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_import_as_names1905); if (state.failed) return ;
            	    pushFollow(FOLLOW_import_as_name_in_import_as_names1907);
            	    import_as_name();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "import_as_names"


    // $ANTLR start "import_as_name"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:453:1: import_as_name : NAME ( AS NAME )? ;
    public final void import_as_name() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:454:5: ( NAME ( AS NAME )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:454:7: NAME ( AS NAME )?
            {
            match(input,NAME,FOLLOW_NAME_in_import_as_name1927); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:454:12: ( AS NAME )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==AS) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:454:13: AS NAME
                    {
                    match(input,AS,FOLLOW_AS_in_import_as_name1930); if (state.failed) return ;
                    match(input,NAME,FOLLOW_NAME_in_import_as_name1932); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "import_as_name"


    // $ANTLR start "dotted_as_name"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:459:1: dotted_as_name : dotted_name ( AS NAME )? ;
    public final void dotted_as_name() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:460:5: ( dotted_name ( AS NAME )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:460:7: dotted_name ( AS NAME )?
            {
            pushFollow(FOLLOW_dotted_name_in_dotted_as_name1953);
            dotted_name();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:460:19: ( AS NAME )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==AS) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:460:20: AS NAME
                    {
                    match(input,AS,FOLLOW_AS_in_dotted_as_name1956); if (state.failed) return ;
                    match(input,NAME,FOLLOW_NAME_in_dotted_as_name1958); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dotted_as_name"


    // $ANTLR start "dotted_as_names"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:464:1: dotted_as_names : dotted_as_name ( COMMA dotted_as_name )* ;
    public final void dotted_as_names() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:465:5: ( dotted_as_name ( COMMA dotted_as_name )* )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:465:7: dotted_as_name ( COMMA dotted_as_name )*
            {
            pushFollow(FOLLOW_dotted_as_name_in_dotted_as_names1978);
            dotted_as_name();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:465:22: ( COMMA dotted_as_name )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==COMMA) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:465:23: COMMA dotted_as_name
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_dotted_as_names1981); if (state.failed) return ;
            	    pushFollow(FOLLOW_dotted_as_name_in_dotted_as_names1983);
            	    dotted_as_name();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dotted_as_names"


    // $ANTLR start "dotted_name"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:469:1: dotted_name : NAME ( DOT attr )* ;
    public final void dotted_name() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:470:5: ( NAME ( DOT attr )* )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:470:7: NAME ( DOT attr )*
            {
            match(input,NAME,FOLLOW_NAME_in_dotted_name2003); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:470:12: ( DOT attr )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==DOT) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:470:13: DOT attr
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_dotted_name2006); if (state.failed) return ;
            	    pushFollow(FOLLOW_attr_in_dotted_name2008);
            	    attr();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dotted_name"


    // $ANTLR start "global_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:474:1: global_stmt : GLOBAL NAME ( COMMA NAME )* ;
    public final void global_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:475:5: ( GLOBAL NAME ( COMMA NAME )* )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:475:7: GLOBAL NAME ( COMMA NAME )*
            {
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global_stmt2028); if (state.failed) return ;
            match(input,NAME,FOLLOW_NAME_in_global_stmt2030); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:475:19: ( COMMA NAME )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==COMMA) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:475:20: COMMA NAME
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_global_stmt2033); if (state.failed) return ;
            	    match(input,NAME,FOLLOW_NAME_in_global_stmt2035); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "global_stmt"


    // $ANTLR start "exec_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:479:1: exec_stmt : EXEC expr ( IN test ( COMMA test )? )? ;
    public final void exec_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:480:5: ( EXEC expr ( IN test ( COMMA test )? )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:480:7: EXEC expr ( IN test ( COMMA test )? )?
            {
            match(input,EXEC,FOLLOW_EXEC_in_exec_stmt2055); if (state.failed) return ;
            pushFollow(FOLLOW_expr_in_exec_stmt2057);
            expr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:480:17: ( IN test ( COMMA test )? )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==IN) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:480:18: IN test ( COMMA test )?
                    {
                    match(input,IN,FOLLOW_IN_in_exec_stmt2060); if (state.failed) return ;
                    pushFollow(FOLLOW_test_in_exec_stmt2062);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:480:26: ( COMMA test )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==COMMA) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:480:27: COMMA test
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_exec_stmt2065); if (state.failed) return ;
                            pushFollow(FOLLOW_test_in_exec_stmt2067);
                            test();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "exec_stmt"


    // $ANTLR start "assert_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:484:1: assert_stmt : ASSERT test ( COMMA test )? ;
    public final void assert_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:485:5: ( ASSERT test ( COMMA test )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:485:7: ASSERT test ( COMMA test )?
            {
            match(input,ASSERT,FOLLOW_ASSERT_in_assert_stmt2089); if (state.failed) return ;
            pushFollow(FOLLOW_test_in_assert_stmt2091);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:485:19: ( COMMA test )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==COMMA) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:485:20: COMMA test
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_assert_stmt2094); if (state.failed) return ;
                    pushFollow(FOLLOW_test_in_assert_stmt2096);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "assert_stmt"


    // $ANTLR start "compound_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:489:1: compound_stmt : ( if_stmt | while_stmt | for_stmt | try_stmt | with_stmt | ( ( decorators )? DEF )=> funcdef | ( ( decorators )? CLASS )=> classdef | decorators );
    public final void compound_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:490:5: ( if_stmt | while_stmt | for_stmt | try_stmt | with_stmt | ( ( decorators )? DEF )=> funcdef | ( ( decorators )? CLASS )=> classdef | decorators )
            int alt57=8;
            alt57 = dfa57.predict(input);
            switch (alt57) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:490:7: if_stmt
                    {
                    pushFollow(FOLLOW_if_stmt_in_compound_stmt2116);
                    if_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:491:7: while_stmt
                    {
                    pushFollow(FOLLOW_while_stmt_in_compound_stmt2124);
                    while_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:492:7: for_stmt
                    {
                    pushFollow(FOLLOW_for_stmt_in_compound_stmt2132);
                    for_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:493:7: try_stmt
                    {
                    pushFollow(FOLLOW_try_stmt_in_compound_stmt2140);
                    try_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:494:7: with_stmt
                    {
                    pushFollow(FOLLOW_with_stmt_in_compound_stmt2148);
                    with_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:495:7: ( ( decorators )? DEF )=> funcdef
                    {
                    pushFollow(FOLLOW_funcdef_in_compound_stmt2165);
                    funcdef();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:496:7: ( ( decorators )? CLASS )=> classdef
                    {
                    pushFollow(FOLLOW_classdef_in_compound_stmt2182);
                    classdef();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:497:7: decorators
                    {
                    pushFollow(FOLLOW_decorators_in_compound_stmt2190);
                    decorators();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "compound_stmt"


    // $ANTLR start "if_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:501:1: if_stmt : IF test COLON suite ( elif_clause )? ;
    public final void if_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:502:5: ( IF test COLON suite ( elif_clause )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:502:7: IF test COLON suite ( elif_clause )?
            {
            match(input,IF,FOLLOW_IF_in_if_stmt2208); if (state.failed) return ;
            pushFollow(FOLLOW_test_in_if_stmt2210);
            test();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_if_stmt2212); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_if_stmt2214);
            suite();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:502:27: ( elif_clause )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==ELIF||LA58_0==ORELSE) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:502:27: elif_clause
                    {
                    pushFollow(FOLLOW_elif_clause_in_if_stmt2216);
                    elif_clause();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "if_stmt"


    // $ANTLR start "elif_clause"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:506:1: elif_clause : ( else_clause | ELIF test COLON suite ( elif_clause | ) );
    public final void elif_clause() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:507:5: ( else_clause | ELIF test COLON suite ( elif_clause | ) )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==ORELSE) ) {
                alt60=1;
            }
            else if ( (LA60_0==ELIF) ) {
                alt60=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:507:7: else_clause
                    {
                    pushFollow(FOLLOW_else_clause_in_elif_clause2235);
                    else_clause();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:508:7: ELIF test COLON suite ( elif_clause | )
                    {
                    match(input,ELIF,FOLLOW_ELIF_in_elif_clause2243); if (state.failed) return ;
                    pushFollow(FOLLOW_test_in_elif_clause2245);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_elif_clause2247); if (state.failed) return ;
                    pushFollow(FOLLOW_suite_in_elif_clause2249);
                    suite();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:509:9: ( elif_clause | )
                    int alt59=2;
                    int LA59_0 = input.LA(1);

                    if ( (LA59_0==ELIF||LA59_0==ORELSE) ) {
                        alt59=1;
                    }
                    else if ( (LA59_0==EOF||(LA59_0>=DEDENT && LA59_0<=NEWLINE)||LA59_0==NAME||LA59_0==PRINT||(LA59_0>=ASSERT && LA59_0<=DELETE)||LA59_0==EXEC||(LA59_0>=FROM && LA59_0<=IMPORT)||(LA59_0>=LAMBDA && LA59_0<=NOT)||(LA59_0>=PASS && LA59_0<=YIELD)||(LA59_0>=AT && LA59_0<=LPAREN)||(LA59_0>=PLUS && LA59_0<=MINUS)||(LA59_0>=TILDE && LA59_0<=LBRACK)||LA59_0==LCURLY||(LA59_0>=BACKQUOTE && LA59_0<=STRING)||(LA59_0>=TRISTRINGPART && LA59_0<=STRINGPART)) ) {
                        alt59=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 59, 0, input);

                        throw nvae;
                    }
                    switch (alt59) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:509:10: elif_clause
                            {
                            pushFollow(FOLLOW_elif_clause_in_elif_clause2260);
                            elif_clause();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:511:9: 
                            {
                            }
                            break;

                    }


                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elif_clause"


    // $ANTLR start "else_clause"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:515:1: else_clause : ORELSE COLON suite ;
    public final void else_clause() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:516:5: ( ORELSE COLON suite )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:516:7: ORELSE COLON suite
            {
            match(input,ORELSE,FOLLOW_ORELSE_in_else_clause2298); if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_else_clause2300); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_else_clause2302);
            suite();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "else_clause"


    // $ANTLR start "while_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:520:1: while_stmt : WHILE test COLON suite ( ORELSE COLON suite )? ;
    public final void while_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:521:5: ( WHILE test COLON suite ( ORELSE COLON suite )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:521:7: WHILE test COLON suite ( ORELSE COLON suite )?
            {
            match(input,WHILE,FOLLOW_WHILE_in_while_stmt2320); if (state.failed) return ;
            pushFollow(FOLLOW_test_in_while_stmt2322);
            test();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_while_stmt2324); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_while_stmt2326);
            suite();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:521:30: ( ORELSE COLON suite )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==ORELSE) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:521:31: ORELSE COLON suite
                    {
                    match(input,ORELSE,FOLLOW_ORELSE_in_while_stmt2329); if (state.failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_while_stmt2331); if (state.failed) return ;
                    pushFollow(FOLLOW_suite_in_while_stmt2333);
                    suite();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "while_stmt"


    // $ANTLR start "for_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:525:1: for_stmt : FOR exprlist IN testlist COLON suite ( ORELSE COLON suite )? ;
    public final void for_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:526:5: ( FOR exprlist IN testlist COLON suite ( ORELSE COLON suite )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:526:7: FOR exprlist IN testlist COLON suite ( ORELSE COLON suite )?
            {
            match(input,FOR,FOLLOW_FOR_in_for_stmt2353); if (state.failed) return ;
            pushFollow(FOLLOW_exprlist_in_for_stmt2355);
            exprlist();

            state._fsp--;
            if (state.failed) return ;
            match(input,IN,FOLLOW_IN_in_for_stmt2357); if (state.failed) return ;
            pushFollow(FOLLOW_testlist_in_for_stmt2359);
            testlist();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_for_stmt2361); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_for_stmt2363);
            suite();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:527:9: ( ORELSE COLON suite )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==ORELSE) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:527:10: ORELSE COLON suite
                    {
                    match(input,ORELSE,FOLLOW_ORELSE_in_for_stmt2374); if (state.failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_for_stmt2376); if (state.failed) return ;
                    pushFollow(FOLLOW_suite_in_for_stmt2378);
                    suite();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "for_stmt"


    // $ANTLR start "try_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:535:1: try_stmt : TRY COLON suite ( ( except_clause )+ ( ORELSE COLON suite )? ( FINALLY COLON suite )? | FINALLY COLON suite )? ;
    public final void try_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:536:5: ( TRY COLON suite ( ( except_clause )+ ( ORELSE COLON suite )? ( FINALLY COLON suite )? | FINALLY COLON suite )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:536:7: TRY COLON suite ( ( except_clause )+ ( ORELSE COLON suite )? ( FINALLY COLON suite )? | FINALLY COLON suite )?
            {
            match(input,TRY,FOLLOW_TRY_in_try_stmt2402); if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_try_stmt2404); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_try_stmt2406);
            suite();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:7: ( ( except_clause )+ ( ORELSE COLON suite )? ( FINALLY COLON suite )? | FINALLY COLON suite )?
            int alt66=3;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==EXCEPT) ) {
                alt66=1;
            }
            else if ( (LA66_0==FINALLY) ) {
                alt66=2;
            }
            switch (alt66) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:9: ( except_clause )+ ( ORELSE COLON suite )? ( FINALLY COLON suite )?
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:9: ( except_clause )+
                    int cnt63=0;
                    loop63:
                    do {
                        int alt63=2;
                        int LA63_0 = input.LA(1);

                        if ( (LA63_0==EXCEPT) ) {
                            alt63=1;
                        }


                        switch (alt63) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:9: except_clause
                    	    {
                    	    pushFollow(FOLLOW_except_clause_in_try_stmt2416);
                    	    except_clause();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt63 >= 1 ) break loop63;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(63, input);
                                throw eee;
                        }
                        cnt63++;
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:24: ( ORELSE COLON suite )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==ORELSE) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:25: ORELSE COLON suite
                            {
                            match(input,ORELSE,FOLLOW_ORELSE_in_try_stmt2420); if (state.failed) return ;
                            match(input,COLON,FOLLOW_COLON_in_try_stmt2422); if (state.failed) return ;
                            pushFollow(FOLLOW_suite_in_try_stmt2424);
                            suite();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:46: ( FINALLY COLON suite )?
                    int alt65=2;
                    int LA65_0 = input.LA(1);

                    if ( (LA65_0==FINALLY) ) {
                        alt65=1;
                    }
                    switch (alt65) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:537:47: FINALLY COLON suite
                            {
                            match(input,FINALLY,FOLLOW_FINALLY_in_try_stmt2429); if (state.failed) return ;
                            match(input,COLON,FOLLOW_COLON_in_try_stmt2431); if (state.failed) return ;
                            pushFollow(FOLLOW_suite_in_try_stmt2433);
                            suite();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:538:9: FINALLY COLON suite
                    {
                    match(input,FINALLY,FOLLOW_FINALLY_in_try_stmt2445); if (state.failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_try_stmt2447); if (state.failed) return ;
                    pushFollow(FOLLOW_suite_in_try_stmt2449);
                    suite();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "try_stmt"


    // $ANTLR start "with_stmt"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:543:1: with_stmt : WITH with_item ( options {greedy=true; } : COMMA with_item )* COLON suite ;
    public final void with_stmt() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:544:5: ( WITH with_item ( options {greedy=true; } : COMMA with_item )* COLON suite )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:544:7: WITH with_item ( options {greedy=true; } : COMMA with_item )* COLON suite
            {
            match(input,WITH,FOLLOW_WITH_in_with_stmt2478); if (state.failed) return ;
            pushFollow(FOLLOW_with_item_in_with_stmt2480);
            with_item();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:544:22: ( options {greedy=true; } : COMMA with_item )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==COMMA) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:544:46: COMMA with_item
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_with_stmt2490); if (state.failed) return ;
            	    pushFollow(FOLLOW_with_item_in_with_stmt2492);
            	    with_item();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);

            match(input,COLON,FOLLOW_COLON_in_with_stmt2496); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_with_stmt2498);
            suite();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "with_stmt"


    // $ANTLR start "with_item"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:548:1: with_item : test ( AS expr )? ;
    public final void with_item() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:549:5: ( test ( AS expr )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:549:7: test ( AS expr )?
            {
            pushFollow(FOLLOW_test_in_with_item2516);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:549:12: ( AS expr )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==AS) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:549:13: AS expr
                    {
                    match(input,AS,FOLLOW_AS_in_with_item2519); if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_with_item2521);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "with_item"


    // $ANTLR start "except_clause"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:553:1: except_clause : EXCEPT ( test ( ( COMMA | AS ) test )? )? COLON suite ;
    public final void except_clause() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:554:5: ( EXCEPT ( test ( ( COMMA | AS ) test )? )? COLON suite )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:554:7: EXCEPT ( test ( ( COMMA | AS ) test )? )? COLON suite
            {
            match(input,EXCEPT,FOLLOW_EXCEPT_in_except_clause2541); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:554:14: ( test ( ( COMMA | AS ) test )? )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==TRAILBACKSLASH||LA70_0==NAME||(LA70_0>=LAMBDA && LA70_0<=NOT)||LA70_0==LPAREN||(LA70_0>=PLUS && LA70_0<=MINUS)||(LA70_0>=TILDE && LA70_0<=LBRACK)||LA70_0==LCURLY||(LA70_0>=BACKQUOTE && LA70_0<=STRING)||(LA70_0>=TRISTRINGPART && LA70_0<=STRINGPART)) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:554:15: test ( ( COMMA | AS ) test )?
                    {
                    pushFollow(FOLLOW_test_in_except_clause2544);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:554:20: ( ( COMMA | AS ) test )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==AS||LA69_0==COMMA) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:554:21: ( COMMA | AS ) test
                            {
                            if ( input.LA(1)==AS||input.LA(1)==COMMA ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_test_in_except_clause2555);
                            test();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,COLON,FOLLOW_COLON_in_except_clause2561); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_except_clause2563);
            suite();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "except_clause"


    // $ANTLR start "suite"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:558:1: suite : ( simple_stmt | NEWLINE ( EOF | ( DEDENT )+ EOF | INDENT ( stmt )+ ( DEDENT | EOF ) ) );
    public final void suite() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:559:5: ( simple_stmt | NEWLINE ( EOF | ( DEDENT )+ EOF | INDENT ( stmt )+ ( DEDENT | EOF ) ) )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==TRAILBACKSLASH||LA74_0==NAME||LA74_0==PRINT||(LA74_0>=ASSERT && LA74_0<=BREAK)||LA74_0==CONTINUE||LA74_0==DELETE||LA74_0==EXEC||LA74_0==FROM||LA74_0==GLOBAL||LA74_0==IMPORT||(LA74_0>=LAMBDA && LA74_0<=NOT)||(LA74_0>=PASS && LA74_0<=RETURN)||LA74_0==YIELD||LA74_0==LPAREN||(LA74_0>=PLUS && LA74_0<=MINUS)||(LA74_0>=TILDE && LA74_0<=LBRACK)||LA74_0==LCURLY||(LA74_0>=BACKQUOTE && LA74_0<=STRING)||(LA74_0>=TRISTRINGPART && LA74_0<=STRINGPART)) ) {
                alt74=1;
            }
            else if ( (LA74_0==NEWLINE) ) {
                alt74=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:559:7: simple_stmt
                    {
                    pushFollow(FOLLOW_simple_stmt_in_suite2581);
                    simple_stmt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:560:7: NEWLINE ( EOF | ( DEDENT )+ EOF | INDENT ( stmt )+ ( DEDENT | EOF ) )
                    {
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_suite2589); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:560:15: ( EOF | ( DEDENT )+ EOF | INDENT ( stmt )+ ( DEDENT | EOF ) )
                    int alt73=3;
                    switch ( input.LA(1) ) {
                    case EOF:
                        {
                        alt73=1;
                        }
                        break;
                    case DEDENT:
                        {
                        alt73=2;
                        }
                        break;
                    case INDENT:
                        {
                        alt73=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 73, 0, input);

                        throw nvae;
                    }

                    switch (alt73) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:560:16: EOF
                            {
                            match(input,EOF,FOLLOW_EOF_in_suite2592); if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:561:17: ( DEDENT )+ EOF
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:561:17: ( DEDENT )+
                            int cnt71=0;
                            loop71:
                            do {
                                int alt71=2;
                                int LA71_0 = input.LA(1);

                                if ( (LA71_0==DEDENT) ) {
                                    alt71=1;
                                }


                                switch (alt71) {
                            	case 1 :
                            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:561:18: DEDENT
                            	    {
                            	    match(input,DEDENT,FOLLOW_DEDENT_in_suite2611); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt71 >= 1 ) break loop71;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(71, input);
                                        throw eee;
                                }
                                cnt71++;
                            } while (true);

                            match(input,EOF,FOLLOW_EOF_in_suite2615); if (state.failed) return ;

                            }
                            break;
                        case 3 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:562:17: INDENT ( stmt )+ ( DEDENT | EOF )
                            {
                            match(input,INDENT,FOLLOW_INDENT_in_suite2633); if (state.failed) return ;
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:562:24: ( stmt )+
                            int cnt72=0;
                            loop72:
                            do {
                                int alt72=2;
                                int LA72_0 = input.LA(1);

                                if ( (LA72_0==TRAILBACKSLASH||LA72_0==NAME||LA72_0==PRINT||(LA72_0>=ASSERT && LA72_0<=DELETE)||LA72_0==EXEC||(LA72_0>=FROM && LA72_0<=IMPORT)||(LA72_0>=LAMBDA && LA72_0<=NOT)||(LA72_0>=PASS && LA72_0<=YIELD)||(LA72_0>=AT && LA72_0<=LPAREN)||(LA72_0>=PLUS && LA72_0<=MINUS)||(LA72_0>=TILDE && LA72_0<=LBRACK)||LA72_0==LCURLY||(LA72_0>=BACKQUOTE && LA72_0<=STRING)||(LA72_0>=TRISTRINGPART && LA72_0<=STRINGPART)) ) {
                                    alt72=1;
                                }


                                switch (alt72) {
                            	case 1 :
                            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:562:25: stmt
                            	    {
                            	    pushFollow(FOLLOW_stmt_in_suite2636);
                            	    stmt();

                            	    state._fsp--;
                            	    if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt72 >= 1 ) break loop72;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(72, input);
                                        throw eee;
                                }
                                cnt72++;
                            } while (true);

                            if ( input.LA(1)==EOF||input.LA(1)==DEDENT ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "suite"


    // $ANTLR start "test"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:569:1: test : ( or_test ( ( IF or_test ORELSE )=> IF or_test ORELSE test | ) | lambdef );
    public final void test() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:570:5: ( or_test ( ( IF or_test ORELSE )=> IF or_test ORELSE test | ) | lambdef )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==TRAILBACKSLASH||LA76_0==NAME||LA76_0==NOT||LA76_0==LPAREN||(LA76_0>=PLUS && LA76_0<=MINUS)||(LA76_0>=TILDE && LA76_0<=LBRACK)||LA76_0==LCURLY||(LA76_0>=BACKQUOTE && LA76_0<=STRING)||(LA76_0>=TRISTRINGPART && LA76_0<=STRINGPART)) ) {
                alt76=1;
            }
            else if ( (LA76_0==LAMBDA) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:570:6: or_test ( ( IF or_test ORELSE )=> IF or_test ORELSE test | )
                    {
                    pushFollow(FOLLOW_or_test_in_test2741);
                    or_test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:571:7: ( ( IF or_test ORELSE )=> IF or_test ORELSE test | )
                    int alt75=2;
                    alt75 = dfa75.predict(input);
                    switch (alt75) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:571:9: ( IF or_test ORELSE )=> IF or_test ORELSE test
                            {
                            match(input,IF,FOLLOW_IF_in_test2761); if (state.failed) return ;
                            pushFollow(FOLLOW_or_test_in_test2763);
                            or_test();

                            state._fsp--;
                            if (state.failed) return ;
                            match(input,ORELSE,FOLLOW_ORELSE_in_test2765); if (state.failed) return ;
                            pushFollow(FOLLOW_test_in_test2767);
                            test();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:573:7: 
                            {
                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:574:7: lambdef
                    {
                    pushFollow(FOLLOW_lambdef_in_test2791);
                    lambdef();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "test"


    // $ANTLR start "or_test"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:578:1: or_test : and_test ( ( OR and_test )+ | ) ;
    public final void or_test() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:579:5: ( and_test ( ( OR and_test )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:579:7: and_test ( ( OR and_test )+ | )
            {
            pushFollow(FOLLOW_and_test_in_or_test2809);
            and_test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:580:9: ( ( OR and_test )+ | )
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==OR) ) {
                alt78=1;
            }
            else if ( (LA78_0==EOF||LA78_0==NEWLINE||LA78_0==AS||LA78_0==FOR||LA78_0==IF||LA78_0==ORELSE||(LA78_0>=RPAREN && LA78_0<=COMMA)||(LA78_0>=SEMI && LA78_0<=DOUBLESLASHEQUAL)||LA78_0==RBRACK||(LA78_0>=RCURLY && LA78_0<=BACKQUOTE)) ) {
                alt78=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:580:11: ( OR and_test )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:580:11: ( OR and_test )+
                    int cnt77=0;
                    loop77:
                    do {
                        int alt77=2;
                        int LA77_0 = input.LA(1);

                        if ( (LA77_0==OR) ) {
                            alt77=1;
                        }


                        switch (alt77) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:580:12: OR and_test
                    	    {
                    	    match(input,OR,FOLLOW_OR_in_or_test2822); if (state.failed) return ;
                    	    pushFollow(FOLLOW_and_test_in_or_test2824);
                    	    and_test();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt77 >= 1 ) break loop77;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(77, input);
                                throw eee;
                        }
                        cnt77++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:583:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "or_test"


    // $ANTLR start "and_test"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:587:1: and_test : not_test ( ( AND not_test )+ | ) ;
    public final void and_test() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:588:5: ( not_test ( ( AND not_test )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:588:7: not_test ( ( AND not_test )+ | )
            {
            pushFollow(FOLLOW_not_test_in_and_test2875);
            not_test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:589:9: ( ( AND not_test )+ | )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==AND) ) {
                alt80=1;
            }
            else if ( (LA80_0==EOF||LA80_0==NEWLINE||LA80_0==AS||LA80_0==FOR||LA80_0==IF||(LA80_0>=OR && LA80_0<=ORELSE)||(LA80_0>=RPAREN && LA80_0<=COMMA)||(LA80_0>=SEMI && LA80_0<=DOUBLESLASHEQUAL)||LA80_0==RBRACK||(LA80_0>=RCURLY && LA80_0<=BACKQUOTE)) ) {
                alt80=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:589:11: ( AND not_test )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:589:11: ( AND not_test )+
                    int cnt79=0;
                    loop79:
                    do {
                        int alt79=2;
                        int LA79_0 = input.LA(1);

                        if ( (LA79_0==AND) ) {
                            alt79=1;
                        }


                        switch (alt79) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:589:12: AND not_test
                    	    {
                    	    match(input,AND,FOLLOW_AND_in_and_test2888); if (state.failed) return ;
                    	    pushFollow(FOLLOW_not_test_in_and_test2890);
                    	    not_test();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt79 >= 1 ) break loop79;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(79, input);
                                throw eee;
                        }
                        cnt79++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:592:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "and_test"


    // $ANTLR start "not_test"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:596:1: not_test : ( NOT not_test | comparison );
    public final void not_test() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:597:5: ( NOT not_test | comparison )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==NOT) ) {
                alt81=1;
            }
            else if ( (LA81_0==TRAILBACKSLASH||LA81_0==NAME||LA81_0==LPAREN||(LA81_0>=PLUS && LA81_0<=MINUS)||(LA81_0>=TILDE && LA81_0<=LBRACK)||LA81_0==LCURLY||(LA81_0>=BACKQUOTE && LA81_0<=STRING)||(LA81_0>=TRISTRINGPART && LA81_0<=STRINGPART)) ) {
                alt81=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:597:7: NOT not_test
                    {
                    match(input,NOT,FOLLOW_NOT_in_not_test2941); if (state.failed) return ;
                    pushFollow(FOLLOW_not_test_in_not_test2943);
                    not_test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:598:7: comparison
                    {
                    pushFollow(FOLLOW_comparison_in_not_test2951);
                    comparison();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "not_test"


    // $ANTLR start "comparison"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:602:1: comparison : expr ( ( comp_op expr )+ | ) ;
    public final void comparison() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:603:5: ( expr ( ( comp_op expr )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:603:7: expr ( ( comp_op expr )+ | )
            {
            pushFollow(FOLLOW_expr_in_comparison2969);
            expr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:604:8: ( ( comp_op expr )+ | )
            int alt83=2;
            int LA83_0 = input.LA(1);

            if ( ((LA83_0>=IN && LA83_0<=IS)||LA83_0==NOT||(LA83_0>=LESS && LA83_0<=NOTEQUAL)) ) {
                alt83=1;
            }
            else if ( (LA83_0==EOF||LA83_0==NEWLINE||(LA83_0>=AND && LA83_0<=AS)||LA83_0==FOR||LA83_0==IF||(LA83_0>=OR && LA83_0<=ORELSE)||(LA83_0>=RPAREN && LA83_0<=COMMA)||(LA83_0>=SEMI && LA83_0<=DOUBLESLASHEQUAL)||LA83_0==RBRACK||(LA83_0>=RCURLY && LA83_0<=BACKQUOTE)) ) {
                alt83=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }
            switch (alt83) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:604:10: ( comp_op expr )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:604:10: ( comp_op expr )+
                    int cnt82=0;
                    loop82:
                    do {
                        int alt82=2;
                        int LA82_0 = input.LA(1);

                        if ( ((LA82_0>=IN && LA82_0<=IS)||LA82_0==NOT||(LA82_0>=LESS && LA82_0<=NOTEQUAL)) ) {
                            alt82=1;
                        }


                        switch (alt82) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:604:12: comp_op expr
                    	    {
                    	    pushFollow(FOLLOW_comp_op_in_comparison2982);
                    	    comp_op();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    pushFollow(FOLLOW_expr_in_comparison2984);
                    	    expr();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt82 >= 1 ) break loop82;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(82, input);
                                throw eee;
                        }
                        cnt82++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:607:8: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "comparison"


    // $ANTLR start "comp_op"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:611:1: comp_op : ( LESS | GREATER | EQUAL | GREATEREQUAL | LESSEQUAL | ALT_NOTEQUAL | NOTEQUAL | IN | NOT IN | IS | IS NOT );
    public final void comp_op() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:612:5: ( LESS | GREATER | EQUAL | GREATEREQUAL | LESSEQUAL | ALT_NOTEQUAL | NOTEQUAL | IN | NOT IN | IS | IS NOT )
            int alt84=11;
            alt84 = dfa84.predict(input);
            switch (alt84) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:612:7: LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_comp_op3032); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:613:7: GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_comp_op3040); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:614:7: EQUAL
                    {
                    match(input,EQUAL,FOLLOW_EQUAL_in_comp_op3048); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:615:7: GREATEREQUAL
                    {
                    match(input,GREATEREQUAL,FOLLOW_GREATEREQUAL_in_comp_op3056); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:616:7: LESSEQUAL
                    {
                    match(input,LESSEQUAL,FOLLOW_LESSEQUAL_in_comp_op3064); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:617:7: ALT_NOTEQUAL
                    {
                    match(input,ALT_NOTEQUAL,FOLLOW_ALT_NOTEQUAL_in_comp_op3072); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:618:7: NOTEQUAL
                    {
                    match(input,NOTEQUAL,FOLLOW_NOTEQUAL_in_comp_op3080); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:619:7: IN
                    {
                    match(input,IN,FOLLOW_IN_in_comp_op3088); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:620:7: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_comp_op3096); if (state.failed) return ;
                    match(input,IN,FOLLOW_IN_in_comp_op3098); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:621:7: IS
                    {
                    match(input,IS,FOLLOW_IS_in_comp_op3106); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:622:7: IS NOT
                    {
                    match(input,IS,FOLLOW_IS_in_comp_op3114); if (state.failed) return ;
                    match(input,NOT,FOLLOW_NOT_in_comp_op3116); if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "comp_op"


    // $ANTLR start "expr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:626:1: expr : xor_expr ( ( VBAR xor_expr )+ | ) ;
    public final void expr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:627:5: ( xor_expr ( ( VBAR xor_expr )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:627:7: xor_expr ( ( VBAR xor_expr )+ | )
            {
            pushFollow(FOLLOW_xor_expr_in_expr3134);
            xor_expr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:628:9: ( ( VBAR xor_expr )+ | )
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==VBAR) ) {
                alt86=1;
            }
            else if ( (LA86_0==EOF||LA86_0==NEWLINE||(LA86_0>=AND && LA86_0<=AS)||LA86_0==FOR||LA86_0==IF||(LA86_0>=IN && LA86_0<=IS)||(LA86_0>=NOT && LA86_0<=ORELSE)||(LA86_0>=RPAREN && LA86_0<=COMMA)||(LA86_0>=SEMI && LA86_0<=DOUBLESLASHEQUAL)||(LA86_0>=LESS && LA86_0<=NOTEQUAL)||LA86_0==RBRACK||(LA86_0>=RCURLY && LA86_0<=BACKQUOTE)) ) {
                alt86=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);

                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:628:11: ( VBAR xor_expr )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:628:11: ( VBAR xor_expr )+
                    int cnt85=0;
                    loop85:
                    do {
                        int alt85=2;
                        int LA85_0 = input.LA(1);

                        if ( (LA85_0==VBAR) ) {
                            alt85=1;
                        }


                        switch (alt85) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:628:12: VBAR xor_expr
                    	    {
                    	    match(input,VBAR,FOLLOW_VBAR_in_expr3147); if (state.failed) return ;
                    	    pushFollow(FOLLOW_xor_expr_in_expr3149);
                    	    xor_expr();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt85 >= 1 ) break loop85;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(85, input);
                                throw eee;
                        }
                        cnt85++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:631:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "expr"


    // $ANTLR start "xor_expr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:635:1: xor_expr : and_expr ( ( CIRCUMFLEX and_expr )+ | ) ;
    public final void xor_expr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:636:5: ( and_expr ( ( CIRCUMFLEX and_expr )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:636:7: and_expr ( ( CIRCUMFLEX and_expr )+ | )
            {
            pushFollow(FOLLOW_and_expr_in_xor_expr3200);
            and_expr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:637:9: ( ( CIRCUMFLEX and_expr )+ | )
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==CIRCUMFLEX) ) {
                alt88=1;
            }
            else if ( (LA88_0==EOF||LA88_0==NEWLINE||(LA88_0>=AND && LA88_0<=AS)||LA88_0==FOR||LA88_0==IF||(LA88_0>=IN && LA88_0<=IS)||(LA88_0>=NOT && LA88_0<=ORELSE)||(LA88_0>=RPAREN && LA88_0<=COMMA)||(LA88_0>=SEMI && LA88_0<=DOUBLESLASHEQUAL)||(LA88_0>=LESS && LA88_0<=VBAR)||LA88_0==RBRACK||(LA88_0>=RCURLY && LA88_0<=BACKQUOTE)) ) {
                alt88=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;
            }
            switch (alt88) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:637:11: ( CIRCUMFLEX and_expr )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:637:11: ( CIRCUMFLEX and_expr )+
                    int cnt87=0;
                    loop87:
                    do {
                        int alt87=2;
                        int LA87_0 = input.LA(1);

                        if ( (LA87_0==CIRCUMFLEX) ) {
                            alt87=1;
                        }


                        switch (alt87) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:637:12: CIRCUMFLEX and_expr
                    	    {
                    	    match(input,CIRCUMFLEX,FOLLOW_CIRCUMFLEX_in_xor_expr3213); if (state.failed) return ;
                    	    pushFollow(FOLLOW_and_expr_in_xor_expr3215);
                    	    and_expr();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt87 >= 1 ) break loop87;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(87, input);
                                throw eee;
                        }
                        cnt87++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:640:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "xor_expr"


    // $ANTLR start "and_expr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:644:1: and_expr : shift_expr ( ( AMPER shift_expr )+ | ) ;
    public final void and_expr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:645:5: ( shift_expr ( ( AMPER shift_expr )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:645:7: shift_expr ( ( AMPER shift_expr )+ | )
            {
            pushFollow(FOLLOW_shift_expr_in_and_expr3266);
            shift_expr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:646:9: ( ( AMPER shift_expr )+ | )
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==AMPER) ) {
                alt90=1;
            }
            else if ( (LA90_0==EOF||LA90_0==NEWLINE||(LA90_0>=AND && LA90_0<=AS)||LA90_0==FOR||LA90_0==IF||(LA90_0>=IN && LA90_0<=IS)||(LA90_0>=NOT && LA90_0<=ORELSE)||(LA90_0>=RPAREN && LA90_0<=COMMA)||(LA90_0>=SEMI && LA90_0<=DOUBLESLASHEQUAL)||(LA90_0>=LESS && LA90_0<=CIRCUMFLEX)||LA90_0==RBRACK||(LA90_0>=RCURLY && LA90_0<=BACKQUOTE)) ) {
                alt90=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }
            switch (alt90) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:646:11: ( AMPER shift_expr )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:646:11: ( AMPER shift_expr )+
                    int cnt89=0;
                    loop89:
                    do {
                        int alt89=2;
                        int LA89_0 = input.LA(1);

                        if ( (LA89_0==AMPER) ) {
                            alt89=1;
                        }


                        switch (alt89) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:646:12: AMPER shift_expr
                    	    {
                    	    match(input,AMPER,FOLLOW_AMPER_in_and_expr3279); if (state.failed) return ;
                    	    pushFollow(FOLLOW_shift_expr_in_and_expr3281);
                    	    shift_expr();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt89 >= 1 ) break loop89;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(89, input);
                                throw eee;
                        }
                        cnt89++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:649:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "and_expr"


    // $ANTLR start "shift_expr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:653:1: shift_expr : arith_expr ( ( shift_op arith_expr )+ | ) ;
    public final void shift_expr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:654:5: ( arith_expr ( ( shift_op arith_expr )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:654:7: arith_expr ( ( shift_op arith_expr )+ | )
            {
            pushFollow(FOLLOW_arith_expr_in_shift_expr3332);
            arith_expr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:655:9: ( ( shift_op arith_expr )+ | )
            int alt92=2;
            int LA92_0 = input.LA(1);

            if ( (LA92_0==RIGHTSHIFT||LA92_0==LEFTSHIFT) ) {
                alt92=1;
            }
            else if ( (LA92_0==EOF||LA92_0==NEWLINE||(LA92_0>=AND && LA92_0<=AS)||LA92_0==FOR||LA92_0==IF||(LA92_0>=IN && LA92_0<=IS)||(LA92_0>=NOT && LA92_0<=ORELSE)||(LA92_0>=RPAREN && LA92_0<=COMMA)||(LA92_0>=SEMI && LA92_0<=DOUBLESLASHEQUAL)||(LA92_0>=LESS && LA92_0<=AMPER)||LA92_0==RBRACK||(LA92_0>=RCURLY && LA92_0<=BACKQUOTE)) ) {
                alt92=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }
            switch (alt92) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:655:11: ( shift_op arith_expr )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:655:11: ( shift_op arith_expr )+
                    int cnt91=0;
                    loop91:
                    do {
                        int alt91=2;
                        int LA91_0 = input.LA(1);

                        if ( (LA91_0==RIGHTSHIFT||LA91_0==LEFTSHIFT) ) {
                            alt91=1;
                        }


                        switch (alt91) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:655:13: shift_op arith_expr
                    	    {
                    	    pushFollow(FOLLOW_shift_op_in_shift_expr3346);
                    	    shift_op();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    pushFollow(FOLLOW_arith_expr_in_shift_expr3348);
                    	    arith_expr();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt91 >= 1 ) break loop91;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(91, input);
                                throw eee;
                        }
                        cnt91++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:658:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "shift_expr"


    // $ANTLR start "shift_op"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:661:1: shift_op : ( LEFTSHIFT | RIGHTSHIFT );
    public final void shift_op() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:662:5: ( LEFTSHIFT | RIGHTSHIFT )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:
            {
            if ( input.LA(1)==RIGHTSHIFT||input.LA(1)==LEFTSHIFT ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "shift_op"


    // $ANTLR start "arith_expr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:667:1: arith_expr : term ( ( arith_op term )+ | ) ;
    public final void arith_expr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:668:5: ( term ( ( arith_op term )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:668:7: term ( ( arith_op term )+ | )
            {
            pushFollow(FOLLOW_term_in_arith_expr3424);
            term();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:669:9: ( ( arith_op term )+ | )
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( ((LA94_0>=PLUS && LA94_0<=MINUS)) ) {
                alt94=1;
            }
            else if ( (LA94_0==EOF||LA94_0==NEWLINE||(LA94_0>=AND && LA94_0<=AS)||LA94_0==FOR||LA94_0==IF||(LA94_0>=IN && LA94_0<=IS)||(LA94_0>=NOT && LA94_0<=ORELSE)||(LA94_0>=RPAREN && LA94_0<=COMMA)||(LA94_0>=SEMI && LA94_0<=LEFTSHIFT)||LA94_0==RBRACK||(LA94_0>=RCURLY && LA94_0<=BACKQUOTE)) ) {
                alt94=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }
            switch (alt94) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:669:11: ( arith_op term )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:669:11: ( arith_op term )+
                    int cnt93=0;
                    loop93:
                    do {
                        int alt93=2;
                        int LA93_0 = input.LA(1);

                        if ( ((LA93_0>=PLUS && LA93_0<=MINUS)) ) {
                            alt93=1;
                        }


                        switch (alt93) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:669:12: arith_op term
                    	    {
                    	    pushFollow(FOLLOW_arith_op_in_arith_expr3437);
                    	    arith_op();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    pushFollow(FOLLOW_term_in_arith_expr3439);
                    	    term();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt93 >= 1 ) break loop93;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(93, input);
                                throw eee;
                        }
                        cnt93++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:672:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "arith_expr"


    // $ANTLR start "arith_op"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:675:1: arith_op : ( PLUS | MINUS );
    public final void arith_op() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:676:5: ( PLUS | MINUS )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:
            {
            if ( (input.LA(1)>=PLUS && input.LA(1)<=MINUS) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "arith_op"


    // $ANTLR start "term"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:681:1: term : factor ( ( term_op factor )+ | ) ;
    public final void term() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:682:5: ( factor ( ( term_op factor )+ | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:682:7: factor ( ( term_op factor )+ | )
            {
            pushFollow(FOLLOW_factor_in_term3515);
            factor();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:683:9: ( ( term_op factor )+ | )
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==STAR||(LA96_0>=SLASH && LA96_0<=DOUBLESLASH)) ) {
                alt96=1;
            }
            else if ( (LA96_0==EOF||LA96_0==NEWLINE||(LA96_0>=AND && LA96_0<=AS)||LA96_0==FOR||LA96_0==IF||(LA96_0>=IN && LA96_0<=IS)||(LA96_0>=NOT && LA96_0<=ORELSE)||(LA96_0>=RPAREN && LA96_0<=COMMA)||(LA96_0>=SEMI && LA96_0<=MINUS)||LA96_0==RBRACK||(LA96_0>=RCURLY && LA96_0<=BACKQUOTE)) ) {
                alt96=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 96, 0, input);

                throw nvae;
            }
            switch (alt96) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:683:11: ( term_op factor )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:683:11: ( term_op factor )+
                    int cnt95=0;
                    loop95:
                    do {
                        int alt95=2;
                        int LA95_0 = input.LA(1);

                        if ( (LA95_0==STAR||(LA95_0>=SLASH && LA95_0<=DOUBLESLASH)) ) {
                            alt95=1;
                        }


                        switch (alt95) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:683:12: term_op factor
                    	    {
                    	    pushFollow(FOLLOW_term_op_in_term3528);
                    	    term_op();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    pushFollow(FOLLOW_factor_in_term3530);
                    	    factor();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt95 >= 1 ) break loop95;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(95, input);
                                throw eee;
                        }
                        cnt95++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:686:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "term"


    // $ANTLR start "term_op"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:689:1: term_op : ( STAR | SLASH | PERCENT | DOUBLESLASH );
    public final void term_op() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:690:5: ( STAR | SLASH | PERCENT | DOUBLESLASH )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:
            {
            if ( input.LA(1)==STAR||(input.LA(1)>=SLASH && input.LA(1)<=DOUBLESLASH) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "term_op"


    // $ANTLR start "factor"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:697:1: factor : ( PLUS factor | MINUS factor | TILDE factor | power | TRAILBACKSLASH );
    public final void factor() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:698:5: ( PLUS factor | MINUS factor | TILDE factor | power | TRAILBACKSLASH )
            int alt97=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt97=1;
                }
                break;
            case MINUS:
                {
                alt97=2;
                }
                break;
            case TILDE:
                {
                alt97=3;
                }
                break;
            case NAME:
            case LPAREN:
            case LBRACK:
            case LCURLY:
            case BACKQUOTE:
            case INT:
            case LONGINT:
            case FLOAT:
            case COMPLEX:
            case STRING:
            case TRISTRINGPART:
            case STRINGPART:
                {
                alt97=4;
                }
                break;
            case TRAILBACKSLASH:
                {
                alt97=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 97, 0, input);

                throw nvae;
            }

            switch (alt97) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:698:7: PLUS factor
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_factor3618); if (state.failed) return ;
                    pushFollow(FOLLOW_factor_in_factor3620);
                    factor();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:699:7: MINUS factor
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_factor3628); if (state.failed) return ;
                    pushFollow(FOLLOW_factor_in_factor3630);
                    factor();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:700:7: TILDE factor
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_factor3638); if (state.failed) return ;
                    pushFollow(FOLLOW_factor_in_factor3640);
                    factor();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:701:7: power
                    {
                    pushFollow(FOLLOW_power_in_factor3648);
                    power();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:702:7: TRAILBACKSLASH
                    {
                    match(input,TRAILBACKSLASH,FOLLOW_TRAILBACKSLASH_in_factor3656); if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "factor"


    // $ANTLR start "power"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:706:1: power : atom ( trailer )* ( options {greedy=true; } : DOUBLESTAR factor )? ;
    public final void power() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:707:5: ( atom ( trailer )* ( options {greedy=true; } : DOUBLESTAR factor )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:707:7: atom ( trailer )* ( options {greedy=true; } : DOUBLESTAR factor )?
            {
            pushFollow(FOLLOW_atom_in_power3674);
            atom();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:707:12: ( trailer )*
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( (LA98_0==DOT||LA98_0==LPAREN||LA98_0==LBRACK) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:707:13: trailer
            	    {
            	    pushFollow(FOLLOW_trailer_in_power3677);
            	    trailer();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:707:23: ( options {greedy=true; } : DOUBLESTAR factor )?
            int alt99=2;
            int LA99_0 = input.LA(1);

            if ( (LA99_0==DOUBLESTAR) ) {
                alt99=1;
            }
            switch (alt99) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:707:47: DOUBLESTAR factor
                    {
                    match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_power3689); if (state.failed) return ;
                    pushFollow(FOLLOW_factor_in_power3691);
                    factor();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "power"


    // $ANTLR start "atom"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:715:1: atom : ( LPAREN ( yield_expr | testlist_gexp | ) RPAREN | LBRACK ( listmaker | ) RBRACK | LCURLY ( dictorsetmaker | ) RCURLY | BACKQUOTE testlist BACKQUOTE | NAME | INT | LONGINT | FLOAT | COMPLEX | ( STRING )+ | TRISTRINGPART | STRINGPART TRAILBACKSLASH );
    public final void atom() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:716:5: ( LPAREN ( yield_expr | testlist_gexp | ) RPAREN | LBRACK ( listmaker | ) RBRACK | LCURLY ( dictorsetmaker | ) RCURLY | BACKQUOTE testlist BACKQUOTE | NAME | INT | LONGINT | FLOAT | COMPLEX | ( STRING )+ | TRISTRINGPART | STRINGPART TRAILBACKSLASH )
            int alt104=12;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt104=1;
                }
                break;
            case LBRACK:
                {
                alt104=2;
                }
                break;
            case LCURLY:
                {
                alt104=3;
                }
                break;
            case BACKQUOTE:
                {
                alt104=4;
                }
                break;
            case NAME:
                {
                alt104=5;
                }
                break;
            case INT:
                {
                alt104=6;
                }
                break;
            case LONGINT:
                {
                alt104=7;
                }
                break;
            case FLOAT:
                {
                alt104=8;
                }
                break;
            case COMPLEX:
                {
                alt104=9;
                }
                break;
            case STRING:
                {
                alt104=10;
                }
                break;
            case TRISTRINGPART:
                {
                alt104=11;
                }
                break;
            case STRINGPART:
                {
                alt104=12;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 104, 0, input);

                throw nvae;
            }

            switch (alt104) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:716:7: LPAREN ( yield_expr | testlist_gexp | ) RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_atom3715); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:717:7: ( yield_expr | testlist_gexp | )
                    int alt100=3;
                    switch ( input.LA(1) ) {
                    case YIELD:
                        {
                        alt100=1;
                        }
                        break;
                    case TRAILBACKSLASH:
                    case NAME:
                    case LAMBDA:
                    case NOT:
                    case LPAREN:
                    case PLUS:
                    case MINUS:
                    case TILDE:
                    case LBRACK:
                    case LCURLY:
                    case BACKQUOTE:
                    case INT:
                    case LONGINT:
                    case FLOAT:
                    case COMPLEX:
                    case STRING:
                    case TRISTRINGPART:
                    case STRINGPART:
                        {
                        alt100=2;
                        }
                        break;
                    case RPAREN:
                        {
                        alt100=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 100, 0, input);

                        throw nvae;
                    }

                    switch (alt100) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:717:9: yield_expr
                            {
                            pushFollow(FOLLOW_yield_expr_in_atom3725);
                            yield_expr();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:718:9: testlist_gexp
                            {
                            pushFollow(FOLLOW_testlist_gexp_in_atom3735);
                            testlist_gexp();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 3 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:720:7: 
                            {
                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_atom3759); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:722:7: LBRACK ( listmaker | ) RBRACK
                    {
                    match(input,LBRACK,FOLLOW_LBRACK_in_atom3767); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:723:7: ( listmaker | )
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==TRAILBACKSLASH||LA101_0==NAME||(LA101_0>=LAMBDA && LA101_0<=NOT)||LA101_0==LPAREN||(LA101_0>=PLUS && LA101_0<=MINUS)||(LA101_0>=TILDE && LA101_0<=LBRACK)||LA101_0==LCURLY||(LA101_0>=BACKQUOTE && LA101_0<=STRING)||(LA101_0>=TRISTRINGPART && LA101_0<=STRINGPART)) ) {
                        alt101=1;
                    }
                    else if ( (LA101_0==RBRACK) ) {
                        alt101=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 101, 0, input);

                        throw nvae;
                    }
                    switch (alt101) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:723:8: listmaker
                            {
                            pushFollow(FOLLOW_listmaker_in_atom3776);
                            listmaker();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:725:7: 
                            {
                            }
                            break;

                    }

                    match(input,RBRACK,FOLLOW_RBRACK_in_atom3800); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:727:7: LCURLY ( dictorsetmaker | ) RCURLY
                    {
                    match(input,LCURLY,FOLLOW_LCURLY_in_atom3808); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:728:8: ( dictorsetmaker | )
                    int alt102=2;
                    int LA102_0 = input.LA(1);

                    if ( (LA102_0==TRAILBACKSLASH||LA102_0==NAME||(LA102_0>=LAMBDA && LA102_0<=NOT)||LA102_0==LPAREN||(LA102_0>=PLUS && LA102_0<=MINUS)||(LA102_0>=TILDE && LA102_0<=LBRACK)||LA102_0==LCURLY||(LA102_0>=BACKQUOTE && LA102_0<=STRING)||(LA102_0>=TRISTRINGPART && LA102_0<=STRINGPART)) ) {
                        alt102=1;
                    }
                    else if ( (LA102_0==RCURLY) ) {
                        alt102=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 102, 0, input);

                        throw nvae;
                    }
                    switch (alt102) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:728:9: dictorsetmaker
                            {
                            pushFollow(FOLLOW_dictorsetmaker_in_atom3818);
                            dictorsetmaker();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:730:8: 
                            {
                            }
                            break;

                    }

                    match(input,RCURLY,FOLLOW_RCURLY_in_atom3845); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:732:8: BACKQUOTE testlist BACKQUOTE
                    {
                    match(input,BACKQUOTE,FOLLOW_BACKQUOTE_in_atom3854); if (state.failed) return ;
                    pushFollow(FOLLOW_testlist_in_atom3856);
                    testlist();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,BACKQUOTE,FOLLOW_BACKQUOTE_in_atom3858); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:733:8: NAME
                    {
                    match(input,NAME,FOLLOW_NAME_in_atom3867); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:734:8: INT
                    {
                    match(input,INT,FOLLOW_INT_in_atom3876); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:735:8: LONGINT
                    {
                    match(input,LONGINT,FOLLOW_LONGINT_in_atom3885); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:736:8: FLOAT
                    {
                    match(input,FLOAT,FOLLOW_FLOAT_in_atom3894); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:737:8: COMPLEX
                    {
                    match(input,COMPLEX,FOLLOW_COMPLEX_in_atom3903); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:738:8: ( STRING )+
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:738:8: ( STRING )+
                    int cnt103=0;
                    loop103:
                    do {
                        int alt103=2;
                        int LA103_0 = input.LA(1);

                        if ( (LA103_0==STRING) ) {
                            alt103=1;
                        }


                        switch (alt103) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:738:9: STRING
                    	    {
                    	    match(input,STRING,FOLLOW_STRING_in_atom3913); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt103 >= 1 ) break loop103;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(103, input);
                                throw eee;
                        }
                        cnt103++;
                    } while (true);


                    }
                    break;
                case 11 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:739:8: TRISTRINGPART
                    {
                    match(input,TRISTRINGPART,FOLLOW_TRISTRINGPART_in_atom3924); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:740:8: STRINGPART TRAILBACKSLASH
                    {
                    match(input,STRINGPART,FOLLOW_STRINGPART_in_atom3933); if (state.failed) return ;
                    match(input,TRAILBACKSLASH,FOLLOW_TRAILBACKSLASH_in_atom3935); if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "atom"


    // $ANTLR start "listmaker"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:744:1: listmaker : test ( list_for | ( options {greedy=true; } : COMMA test )* ) ( COMMA )? ;
    public final void listmaker() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:745:5: ( test ( list_for | ( options {greedy=true; } : COMMA test )* ) ( COMMA )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:745:7: test ( list_for | ( options {greedy=true; } : COMMA test )* ) ( COMMA )?
            {
            pushFollow(FOLLOW_test_in_listmaker3954);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:746:9: ( list_for | ( options {greedy=true; } : COMMA test )* )
            int alt106=2;
            int LA106_0 = input.LA(1);

            if ( (LA106_0==FOR) ) {
                alt106=1;
            }
            else if ( (LA106_0==COMMA||LA106_0==RBRACK) ) {
                alt106=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 106, 0, input);

                throw nvae;
            }
            switch (alt106) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:746:10: list_for
                    {
                    pushFollow(FOLLOW_list_for_in_listmaker3965);
                    list_for();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:747:11: ( options {greedy=true; } : COMMA test )*
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:747:11: ( options {greedy=true; } : COMMA test )*
                    loop105:
                    do {
                        int alt105=2;
                        int LA105_0 = input.LA(1);

                        if ( (LA105_0==COMMA) ) {
                            int LA105_1 = input.LA(2);

                            if ( (LA105_1==TRAILBACKSLASH||LA105_1==NAME||(LA105_1>=LAMBDA && LA105_1<=NOT)||LA105_1==LPAREN||(LA105_1>=PLUS && LA105_1<=MINUS)||(LA105_1>=TILDE && LA105_1<=LBRACK)||LA105_1==LCURLY||(LA105_1>=BACKQUOTE && LA105_1<=STRING)||(LA105_1>=TRISTRINGPART && LA105_1<=STRINGPART)) ) {
                                alt105=1;
                            }


                        }


                        switch (alt105) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:747:35: COMMA test
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_listmaker3985); if (state.failed) return ;
                    	    pushFollow(FOLLOW_test_in_listmaker3987);
                    	    test();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop105;
                        }
                    } while (true);


                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:748:11: ( COMMA )?
            int alt107=2;
            int LA107_0 = input.LA(1);

            if ( (LA107_0==COMMA) ) {
                alt107=1;
            }
            switch (alt107) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:748:12: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_listmaker4002); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "listmaker"


    // $ANTLR start "testlist_gexp"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:752:1: testlist_gexp : test ( ( ( options {k=2; } : COMMA test )* ( COMMA )? ) | ( comp_for ) ) ;
    public final void testlist_gexp() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:753:5: ( test ( ( ( options {k=2; } : COMMA test )* ( COMMA )? ) | ( comp_for ) ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:753:7: test ( ( ( options {k=2; } : COMMA test )* ( COMMA )? ) | ( comp_for ) )
            {
            pushFollow(FOLLOW_test_in_testlist_gexp4022);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:9: ( ( ( options {k=2; } : COMMA test )* ( COMMA )? ) | ( comp_for ) )
            int alt110=2;
            int LA110_0 = input.LA(1);

            if ( (LA110_0==RPAREN||LA110_0==COMMA) ) {
                alt110=1;
            }
            else if ( (LA110_0==FOR) ) {
                alt110=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 110, 0, input);

                throw nvae;
            }
            switch (alt110) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:11: ( ( options {k=2; } : COMMA test )* ( COMMA )? )
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:11: ( ( options {k=2; } : COMMA test )* ( COMMA )? )
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:12: ( options {k=2; } : COMMA test )* ( COMMA )?
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:12: ( options {k=2; } : COMMA test )*
                    loop108:
                    do {
                        int alt108=2;
                        alt108 = dfa108.predict(input);
                        switch (alt108) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:29: COMMA test
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_testlist_gexp4044); if (state.failed) return ;
                    	    pushFollow(FOLLOW_test_in_testlist_gexp4046);
                    	    test();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop108;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:42: ( COMMA )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==COMMA) ) {
                        alt109=1;
                    }
                    switch (alt109) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:754:43: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_testlist_gexp4051); if (state.failed) return ;

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:756:11: ( comp_for )
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:756:11: ( comp_for )
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:756:12: comp_for
                    {
                    pushFollow(FOLLOW_comp_for_in_testlist_gexp4078);
                    comp_for();

                    state._fsp--;
                    if (state.failed) return ;

                    }


                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "testlist_gexp"


    // $ANTLR start "lambdef"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:762:1: lambdef : LAMBDA ( varargslist )? COLON test ;
    public final void lambdef() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:763:5: ( LAMBDA ( varargslist )? COLON test )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:763:7: LAMBDA ( varargslist )? COLON test
            {
            match(input,LAMBDA,FOLLOW_LAMBDA_in_lambdef4118); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:763:14: ( varargslist )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( (LA111_0==NAME||LA111_0==LPAREN||(LA111_0>=STAR && LA111_0<=DOUBLESTAR)) ) {
                alt111=1;
            }
            switch (alt111) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:763:15: varargslist
                    {
                    pushFollow(FOLLOW_varargslist_in_lambdef4121);
                    varargslist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,COLON,FOLLOW_COLON_in_lambdef4125); if (state.failed) return ;
            pushFollow(FOLLOW_test_in_lambdef4127);
            test();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "lambdef"


    // $ANTLR start "trailer"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:767:1: trailer : ( LPAREN ( arglist | ) RPAREN | LBRACK subscriptlist RBRACK | DOT attr );
    public final void trailer() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:768:5: ( LPAREN ( arglist | ) RPAREN | LBRACK subscriptlist RBRACK | DOT attr )
            int alt113=3;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt113=1;
                }
                break;
            case LBRACK:
                {
                alt113=2;
                }
                break;
            case DOT:
                {
                alt113=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 113, 0, input);

                throw nvae;
            }

            switch (alt113) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:768:7: LPAREN ( arglist | ) RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_trailer4145); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:769:9: ( arglist | )
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==TRAILBACKSLASH||LA112_0==NAME||(LA112_0>=LAMBDA && LA112_0<=NOT)||LA112_0==LPAREN||(LA112_0>=STAR && LA112_0<=DOUBLESTAR)||(LA112_0>=PLUS && LA112_0<=MINUS)||(LA112_0>=TILDE && LA112_0<=LBRACK)||LA112_0==LCURLY||(LA112_0>=BACKQUOTE && LA112_0<=STRING)||(LA112_0>=TRISTRINGPART && LA112_0<=STRINGPART)) ) {
                        alt112=1;
                    }
                    else if ( (LA112_0==RPAREN) ) {
                        alt112=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 112, 0, input);

                        throw nvae;
                    }
                    switch (alt112) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:769:10: arglist
                            {
                            pushFollow(FOLLOW_arglist_in_trailer4156);
                            arglist();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:771:9: 
                            {
                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_trailer4184); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:773:7: LBRACK subscriptlist RBRACK
                    {
                    match(input,LBRACK,FOLLOW_LBRACK_in_trailer4192); if (state.failed) return ;
                    pushFollow(FOLLOW_subscriptlist_in_trailer4194);
                    subscriptlist();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RBRACK,FOLLOW_RBRACK_in_trailer4196); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:774:7: DOT attr
                    {
                    match(input,DOT,FOLLOW_DOT_in_trailer4204); if (state.failed) return ;
                    pushFollow(FOLLOW_attr_in_trailer4206);
                    attr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "trailer"


    // $ANTLR start "subscriptlist"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:778:1: subscriptlist : subscript ( options {greedy=true; } : COMMA subscript )* ( COMMA )? ;
    public final void subscriptlist() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:779:5: ( subscript ( options {greedy=true; } : COMMA subscript )* ( COMMA )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:779:7: subscript ( options {greedy=true; } : COMMA subscript )* ( COMMA )?
            {
            pushFollow(FOLLOW_subscript_in_subscriptlist4224);
            subscript();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:779:17: ( options {greedy=true; } : COMMA subscript )*
            loop114:
            do {
                int alt114=2;
                int LA114_0 = input.LA(1);

                if ( (LA114_0==COMMA) ) {
                    int LA114_1 = input.LA(2);

                    if ( (LA114_1==TRAILBACKSLASH||(LA114_1>=NAME && LA114_1<=DOT)||(LA114_1>=LAMBDA && LA114_1<=NOT)||LA114_1==LPAREN||LA114_1==COLON||(LA114_1>=PLUS && LA114_1<=MINUS)||(LA114_1>=TILDE && LA114_1<=LBRACK)||LA114_1==LCURLY||(LA114_1>=BACKQUOTE && LA114_1<=STRING)||(LA114_1>=TRISTRINGPART && LA114_1<=STRINGPART)) ) {
                        alt114=1;
                    }


                }


                switch (alt114) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:779:41: COMMA subscript
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subscriptlist4234); if (state.failed) return ;
            	    pushFollow(FOLLOW_subscript_in_subscriptlist4236);
            	    subscript();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop114;
                }
            } while (true);

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:779:59: ( COMMA )?
            int alt115=2;
            int LA115_0 = input.LA(1);

            if ( (LA115_0==COMMA) ) {
                alt115=1;
            }
            switch (alt115) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:779:60: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_subscriptlist4241); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "subscriptlist"


    // $ANTLR start "subscript"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:783:1: subscript : ( DOT DOT DOT | ( test COLON )=> test ( COLON ( test )? ( sliceop )? )? | ( COLON )=> COLON ( test )? ( sliceop )? | test );
    public final void subscript() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:784:5: ( DOT DOT DOT | ( test COLON )=> test ( COLON ( test )? ( sliceop )? )? | ( COLON )=> COLON ( test )? ( sliceop )? | test )
            int alt121=4;
            alt121 = dfa121.predict(input);
            switch (alt121) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:784:7: DOT DOT DOT
                    {
                    match(input,DOT,FOLLOW_DOT_in_subscript4261); if (state.failed) return ;
                    match(input,DOT,FOLLOW_DOT_in_subscript4263); if (state.failed) return ;
                    match(input,DOT,FOLLOW_DOT_in_subscript4265); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:785:7: ( test COLON )=> test ( COLON ( test )? ( sliceop )? )?
                    {
                    pushFollow(FOLLOW_test_in_subscript4284);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:786:12: ( COLON ( test )? ( sliceop )? )?
                    int alt118=2;
                    int LA118_0 = input.LA(1);

                    if ( (LA118_0==COLON) ) {
                        alt118=1;
                    }
                    switch (alt118) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:786:13: COLON ( test )? ( sliceop )?
                            {
                            match(input,COLON,FOLLOW_COLON_in_subscript4287); if (state.failed) return ;
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:786:19: ( test )?
                            int alt116=2;
                            int LA116_0 = input.LA(1);

                            if ( (LA116_0==TRAILBACKSLASH||LA116_0==NAME||(LA116_0>=LAMBDA && LA116_0<=NOT)||LA116_0==LPAREN||(LA116_0>=PLUS && LA116_0<=MINUS)||(LA116_0>=TILDE && LA116_0<=LBRACK)||LA116_0==LCURLY||(LA116_0>=BACKQUOTE && LA116_0<=STRING)||(LA116_0>=TRISTRINGPART && LA116_0<=STRINGPART)) ) {
                                alt116=1;
                            }
                            switch (alt116) {
                                case 1 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:786:20: test
                                    {
                                    pushFollow(FOLLOW_test_in_subscript4290);
                                    test();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }

                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:786:27: ( sliceop )?
                            int alt117=2;
                            int LA117_0 = input.LA(1);

                            if ( (LA117_0==COLON) ) {
                                alt117=1;
                            }
                            switch (alt117) {
                                case 1 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:786:28: sliceop
                                    {
                                    pushFollow(FOLLOW_sliceop_in_subscript4295);
                                    sliceop();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:787:7: ( COLON )=> COLON ( test )? ( sliceop )?
                    {
                    match(input,COLON,FOLLOW_COLON_in_subscript4316); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:788:13: ( test )?
                    int alt119=2;
                    int LA119_0 = input.LA(1);

                    if ( (LA119_0==TRAILBACKSLASH||LA119_0==NAME||(LA119_0>=LAMBDA && LA119_0<=NOT)||LA119_0==LPAREN||(LA119_0>=PLUS && LA119_0<=MINUS)||(LA119_0>=TILDE && LA119_0<=LBRACK)||LA119_0==LCURLY||(LA119_0>=BACKQUOTE && LA119_0<=STRING)||(LA119_0>=TRISTRINGPART && LA119_0<=STRINGPART)) ) {
                        alt119=1;
                    }
                    switch (alt119) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:788:14: test
                            {
                            pushFollow(FOLLOW_test_in_subscript4319);
                            test();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:788:21: ( sliceop )?
                    int alt120=2;
                    int LA120_0 = input.LA(1);

                    if ( (LA120_0==COLON) ) {
                        alt120=1;
                    }
                    switch (alt120) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:788:22: sliceop
                            {
                            pushFollow(FOLLOW_sliceop_in_subscript4324);
                            sliceop();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:789:7: test
                    {
                    pushFollow(FOLLOW_test_in_subscript4334);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "subscript"


    // $ANTLR start "sliceop"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:793:1: sliceop : COLON ( test | ) ;
    public final void sliceop() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:794:5: ( COLON ( test | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:794:7: COLON ( test | )
            {
            match(input,COLON,FOLLOW_COLON_in_sliceop4352); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:795:6: ( test | )
            int alt122=2;
            int LA122_0 = input.LA(1);

            if ( (LA122_0==TRAILBACKSLASH||LA122_0==NAME||(LA122_0>=LAMBDA && LA122_0<=NOT)||LA122_0==LPAREN||(LA122_0>=PLUS && LA122_0<=MINUS)||(LA122_0>=TILDE && LA122_0<=LBRACK)||LA122_0==LCURLY||(LA122_0>=BACKQUOTE && LA122_0<=STRING)||(LA122_0>=TRISTRINGPART && LA122_0<=STRINGPART)) ) {
                alt122=1;
            }
            else if ( (LA122_0==COMMA||LA122_0==RBRACK) ) {
                alt122=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 122, 0, input);

                throw nvae;
            }
            switch (alt122) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:795:7: test
                    {
                    pushFollow(FOLLOW_test_in_sliceop4360);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:797:6: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "sliceop"


    // $ANTLR start "exprlist"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:801:1: exprlist : ( ( expr COMMA )=> expr ( options {k=2; } : COMMA expr )* ( COMMA )? | expr );
    public final void exprlist() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:5: ( ( expr COMMA )=> expr ( options {k=2; } : COMMA expr )* ( COMMA )? | expr )
            int alt125=2;
            alt125 = dfa125.predict(input);
            switch (alt125) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:7: ( expr COMMA )=> expr ( options {k=2; } : COMMA expr )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_expr_in_exprlist4400);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:28: ( options {k=2; } : COMMA expr )*
                    loop123:
                    do {
                        int alt123=2;
                        alt123 = dfa123.predict(input);
                        switch (alt123) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:45: COMMA expr
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_exprlist4411); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expr_in_exprlist4413);
                    	    expr();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop123;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:58: ( COMMA )?
                    int alt124=2;
                    int LA124_0 = input.LA(1);

                    if ( (LA124_0==COMMA) ) {
                        alt124=1;
                    }
                    switch (alt124) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:59: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_exprlist4418); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:803:7: expr
                    {
                    pushFollow(FOLLOW_expr_in_exprlist4428);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "exprlist"


    // $ANTLR start "del_list"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:808:1: del_list : expr ( options {k=2; } : COMMA expr )* ( COMMA )? ;
    public final void del_list() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:809:5: ( expr ( options {k=2; } : COMMA expr )* ( COMMA )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:809:7: expr ( options {k=2; } : COMMA expr )* ( COMMA )?
            {
            pushFollow(FOLLOW_expr_in_del_list4447);
            expr();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:809:12: ( options {k=2; } : COMMA expr )*
            loop126:
            do {
                int alt126=2;
                alt126 = dfa126.predict(input);
                switch (alt126) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:809:29: COMMA expr
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_del_list4458); if (state.failed) return ;
            	    pushFollow(FOLLOW_expr_in_del_list4460);
            	    expr();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop126;
                }
            } while (true);

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:809:42: ( COMMA )?
            int alt127=2;
            int LA127_0 = input.LA(1);

            if ( (LA127_0==COMMA) ) {
                alt127=1;
            }
            switch (alt127) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:809:43: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_del_list4465); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "del_list"


    // $ANTLR start "testlist"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:813:1: testlist : ( ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )? | test );
    public final void testlist() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:814:5: ( ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )? | test )
            int alt130=2;
            alt130 = dfa130.predict(input);
            switch (alt130) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:814:7: ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_test_in_testlist4496);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:815:12: ( options {k=2; } : COMMA test )*
                    loop128:
                    do {
                        int alt128=2;
                        alt128 = dfa128.predict(input);
                        switch (alt128) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:815:29: COMMA test
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_testlist4507); if (state.failed) return ;
                    	    pushFollow(FOLLOW_test_in_testlist4509);
                    	    test();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop128;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:815:42: ( COMMA )?
                    int alt129=2;
                    int LA129_0 = input.LA(1);

                    if ( (LA129_0==COMMA) ) {
                        alt129=1;
                    }
                    switch (alt129) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:815:43: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_testlist4514); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:816:7: test
                    {
                    pushFollow(FOLLOW_test_in_testlist4524);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "testlist"


    // $ANTLR start "dictorsetmaker"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:821:1: dictorsetmaker : test ( ( COLON test ( comp_for | ( options {k=2; } : COMMA test COLON test )* ) | ( COMMA test )* ) ( COMMA )? | comp_for ) ;
    public final void dictorsetmaker() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:822:5: ( test ( ( COLON test ( comp_for | ( options {k=2; } : COMMA test COLON test )* ) | ( COMMA test )* ) ( COMMA )? | comp_for ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:822:7: test ( ( COLON test ( comp_for | ( options {k=2; } : COMMA test COLON test )* ) | ( COMMA test )* ) ( COMMA )? | comp_for )
            {
            pushFollow(FOLLOW_test_in_dictorsetmaker4543);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:823:10: ( ( COLON test ( comp_for | ( options {k=2; } : COMMA test COLON test )* ) | ( COMMA test )* ) ( COMMA )? | comp_for )
            int alt136=2;
            int LA136_0 = input.LA(1);

            if ( (LA136_0==COLON||LA136_0==COMMA||LA136_0==RCURLY) ) {
                alt136=1;
            }
            else if ( (LA136_0==FOR) ) {
                alt136=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 136, 0, input);

                throw nvae;
            }
            switch (alt136) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:824:14: ( COLON test ( comp_for | ( options {k=2; } : COMMA test COLON test )* ) | ( COMMA test )* ) ( COMMA )?
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:824:14: ( COLON test ( comp_for | ( options {k=2; } : COMMA test COLON test )* ) | ( COMMA test )* )
                    int alt134=2;
                    int LA134_0 = input.LA(1);

                    if ( (LA134_0==COLON) ) {
                        alt134=1;
                    }
                    else if ( (LA134_0==COMMA||LA134_0==RCURLY) ) {
                        alt134=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 134, 0, input);

                        throw nvae;
                    }
                    switch (alt134) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:824:15: COLON test ( comp_for | ( options {k=2; } : COMMA test COLON test )* )
                            {
                            match(input,COLON,FOLLOW_COLON_in_dictorsetmaker4570); if (state.failed) return ;
                            pushFollow(FOLLOW_test_in_dictorsetmaker4572);
                            test();

                            state._fsp--;
                            if (state.failed) return ;
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:825:16: ( comp_for | ( options {k=2; } : COMMA test COLON test )* )
                            int alt132=2;
                            int LA132_0 = input.LA(1);

                            if ( (LA132_0==FOR) ) {
                                alt132=1;
                            }
                            else if ( (LA132_0==COMMA||LA132_0==RCURLY) ) {
                                alt132=2;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 132, 0, input);

                                throw nvae;
                            }
                            switch (alt132) {
                                case 1 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:825:18: comp_for
                                    {
                                    pushFollow(FOLLOW_comp_for_in_dictorsetmaker4591);
                                    comp_for();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;
                                case 2 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:826:18: ( options {k=2; } : COMMA test COLON test )*
                                    {
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:826:18: ( options {k=2; } : COMMA test COLON test )*
                                    loop131:
                                    do {
                                        int alt131=2;
                                        alt131 = dfa131.predict(input);
                                        switch (alt131) {
                                    	case 1 :
                                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:826:34: COMMA test COLON test
                                    	    {
                                    	    match(input,COMMA,FOLLOW_COMMA_in_dictorsetmaker4618); if (state.failed) return ;
                                    	    pushFollow(FOLLOW_test_in_dictorsetmaker4620);
                                    	    test();

                                    	    state._fsp--;
                                    	    if (state.failed) return ;
                                    	    match(input,COLON,FOLLOW_COLON_in_dictorsetmaker4622); if (state.failed) return ;
                                    	    pushFollow(FOLLOW_test_in_dictorsetmaker4624);
                                    	    test();

                                    	    state._fsp--;
                                    	    if (state.failed) return ;

                                    	    }
                                    	    break;

                                    	default :
                                    	    break loop131;
                                        }
                                    } while (true);


                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:828:15: ( COMMA test )*
                            {
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:828:15: ( COMMA test )*
                            loop133:
                            do {
                                int alt133=2;
                                int LA133_0 = input.LA(1);

                                if ( (LA133_0==COMMA) ) {
                                    int LA133_1 = input.LA(2);

                                    if ( (LA133_1==TRAILBACKSLASH||LA133_1==NAME||(LA133_1>=LAMBDA && LA133_1<=NOT)||LA133_1==LPAREN||(LA133_1>=PLUS && LA133_1<=MINUS)||(LA133_1>=TILDE && LA133_1<=LBRACK)||LA133_1==LCURLY||(LA133_1>=BACKQUOTE && LA133_1<=STRING)||(LA133_1>=TRISTRINGPART && LA133_1<=STRINGPART)) ) {
                                        alt133=1;
                                    }


                                }


                                switch (alt133) {
                            	case 1 :
                            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:828:16: COMMA test
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_dictorsetmaker4660); if (state.failed) return ;
                            	    pushFollow(FOLLOW_test_in_dictorsetmaker4662);
                            	    test();

                            	    state._fsp--;
                            	    if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    break loop133;
                                }
                            } while (true);


                            }
                            break;

                    }

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:830:14: ( COMMA )?
                    int alt135=2;
                    int LA135_0 = input.LA(1);

                    if ( (LA135_0==COMMA) ) {
                        alt135=1;
                    }
                    switch (alt135) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:830:15: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_dictorsetmaker4695); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:831:12: comp_for
                    {
                    pushFollow(FOLLOW_comp_for_in_dictorsetmaker4710);
                    comp_for();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dictorsetmaker"


    // $ANTLR start "classdef"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:836:1: classdef : ( decorators )? CLASS NAME ( LPAREN ( testlist )? RPAREN )? COLON suite ;
    public final void classdef() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:5: ( ( decorators )? CLASS NAME ( LPAREN ( testlist )? RPAREN )? COLON suite )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:7: ( decorators )? CLASS NAME ( LPAREN ( testlist )? RPAREN )? COLON suite
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:7: ( decorators )?
            int alt137=2;
            int LA137_0 = input.LA(1);

            if ( (LA137_0==AT) ) {
                alt137=1;
            }
            switch (alt137) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:7: decorators
                    {
                    pushFollow(FOLLOW_decorators_in_classdef4739);
                    decorators();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,CLASS,FOLLOW_CLASS_in_classdef4742); if (state.failed) return ;
            match(input,NAME,FOLLOW_NAME_in_classdef4744); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:30: ( LPAREN ( testlist )? RPAREN )?
            int alt139=2;
            int LA139_0 = input.LA(1);

            if ( (LA139_0==LPAREN) ) {
                alt139=1;
            }
            switch (alt139) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:31: LPAREN ( testlist )? RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_classdef4747); if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:38: ( testlist )?
                    int alt138=2;
                    int LA138_0 = input.LA(1);

                    if ( (LA138_0==TRAILBACKSLASH||LA138_0==NAME||(LA138_0>=LAMBDA && LA138_0<=NOT)||LA138_0==LPAREN||(LA138_0>=PLUS && LA138_0<=MINUS)||(LA138_0>=TILDE && LA138_0<=LBRACK)||LA138_0==LCURLY||(LA138_0>=BACKQUOTE && LA138_0<=STRING)||(LA138_0>=TRISTRINGPART && LA138_0<=STRINGPART)) ) {
                        alt138=1;
                    }
                    switch (alt138) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:837:38: testlist
                            {
                            pushFollow(FOLLOW_testlist_in_classdef4749);
                            testlist();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_classdef4752); if (state.failed) return ;

                    }
                    break;

            }

            match(input,COLON,FOLLOW_COLON_in_classdef4756); if (state.failed) return ;
            pushFollow(FOLLOW_suite_in_classdef4758);
            suite();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "classdef"


    // $ANTLR start "arglist"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:843:1: arglist : ( argument ( COMMA argument )* ( COMMA ( STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test )? )? | STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test );
    public final void arglist() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:844:5: ( argument ( COMMA argument )* ( COMMA ( STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test )? )? | STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test )
            int alt147=3;
            switch ( input.LA(1) ) {
            case TRAILBACKSLASH:
            case NAME:
            case LAMBDA:
            case NOT:
            case LPAREN:
            case PLUS:
            case MINUS:
            case TILDE:
            case LBRACK:
            case LCURLY:
            case BACKQUOTE:
            case INT:
            case LONGINT:
            case FLOAT:
            case COMPLEX:
            case STRING:
            case TRISTRINGPART:
            case STRINGPART:
                {
                alt147=1;
                }
                break;
            case STAR:
                {
                alt147=2;
                }
                break;
            case DOUBLESTAR:
                {
                alt147=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 147, 0, input);

                throw nvae;
            }

            switch (alt147) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:844:7: argument ( COMMA argument )* ( COMMA ( STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test )? )?
                    {
                    pushFollow(FOLLOW_argument_in_arglist4778);
                    argument();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:844:16: ( COMMA argument )*
                    loop140:
                    do {
                        int alt140=2;
                        int LA140_0 = input.LA(1);

                        if ( (LA140_0==COMMA) ) {
                            int LA140_1 = input.LA(2);

                            if ( (LA140_1==TRAILBACKSLASH||LA140_1==NAME||(LA140_1>=LAMBDA && LA140_1<=NOT)||LA140_1==LPAREN||(LA140_1>=PLUS && LA140_1<=MINUS)||(LA140_1>=TILDE && LA140_1<=LBRACK)||LA140_1==LCURLY||(LA140_1>=BACKQUOTE && LA140_1<=STRING)||(LA140_1>=TRISTRINGPART && LA140_1<=STRINGPART)) ) {
                                alt140=1;
                            }


                        }


                        switch (alt140) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:844:17: COMMA argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arglist4781); if (state.failed) return ;
                    	    pushFollow(FOLLOW_argument_in_arglist4783);
                    	    argument();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop140;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:845:11: ( COMMA ( STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test )? )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( (LA144_0==COMMA) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:845:12: COMMA ( STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test )?
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arglist4798); if (state.failed) return ;
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:846:15: ( STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )? | DOUBLESTAR test )?
                            int alt143=3;
                            int LA143_0 = input.LA(1);

                            if ( (LA143_0==STAR) ) {
                                alt143=1;
                            }
                            else if ( (LA143_0==DOUBLESTAR) ) {
                                alt143=2;
                            }
                            switch (alt143) {
                                case 1 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:846:17: STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )?
                                    {
                                    match(input,STAR,FOLLOW_STAR_in_arglist4816); if (state.failed) return ;
                                    pushFollow(FOLLOW_test_in_arglist4818);
                                    test();

                                    state._fsp--;
                                    if (state.failed) return ;
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:846:27: ( COMMA argument )*
                                    loop141:
                                    do {
                                        int alt141=2;
                                        int LA141_0 = input.LA(1);

                                        if ( (LA141_0==COMMA) ) {
                                            int LA141_1 = input.LA(2);

                                            if ( (LA141_1==TRAILBACKSLASH||LA141_1==NAME||(LA141_1>=LAMBDA && LA141_1<=NOT)||LA141_1==LPAREN||(LA141_1>=PLUS && LA141_1<=MINUS)||(LA141_1>=TILDE && LA141_1<=LBRACK)||LA141_1==LCURLY||(LA141_1>=BACKQUOTE && LA141_1<=STRING)||(LA141_1>=TRISTRINGPART && LA141_1<=STRINGPART)) ) {
                                                alt141=1;
                                            }


                                        }


                                        switch (alt141) {
                                    	case 1 :
                                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:846:28: COMMA argument
                                    	    {
                                    	    match(input,COMMA,FOLLOW_COMMA_in_arglist4821); if (state.failed) return ;
                                    	    pushFollow(FOLLOW_argument_in_arglist4823);
                                    	    argument();

                                    	    state._fsp--;
                                    	    if (state.failed) return ;

                                    	    }
                                    	    break;

                                    	default :
                                    	    break loop141;
                                        }
                                    } while (true);

                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:846:45: ( COMMA DOUBLESTAR test )?
                                    int alt142=2;
                                    int LA142_0 = input.LA(1);

                                    if ( (LA142_0==COMMA) ) {
                                        alt142=1;
                                    }
                                    switch (alt142) {
                                        case 1 :
                                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:846:46: COMMA DOUBLESTAR test
                                            {
                                            match(input,COMMA,FOLLOW_COMMA_in_arglist4828); if (state.failed) return ;
                                            match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_arglist4830); if (state.failed) return ;
                                            pushFollow(FOLLOW_test_in_arglist4832);
                                            test();

                                            state._fsp--;
                                            if (state.failed) return ;

                                            }
                                            break;

                                    }


                                    }
                                    break;
                                case 2 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:847:17: DOUBLESTAR test
                                    {
                                    match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_arglist4852); if (state.failed) return ;
                                    pushFollow(FOLLOW_test_in_arglist4854);
                                    test();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:850:7: STAR test ( COMMA argument )* ( COMMA DOUBLESTAR test )?
                    {
                    match(input,STAR,FOLLOW_STAR_in_arglist4892); if (state.failed) return ;
                    pushFollow(FOLLOW_test_in_arglist4894);
                    test();

                    state._fsp--;
                    if (state.failed) return ;
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:850:17: ( COMMA argument )*
                    loop145:
                    do {
                        int alt145=2;
                        int LA145_0 = input.LA(1);

                        if ( (LA145_0==COMMA) ) {
                            int LA145_1 = input.LA(2);

                            if ( (LA145_1==TRAILBACKSLASH||LA145_1==NAME||(LA145_1>=LAMBDA && LA145_1<=NOT)||LA145_1==LPAREN||(LA145_1>=PLUS && LA145_1<=MINUS)||(LA145_1>=TILDE && LA145_1<=LBRACK)||LA145_1==LCURLY||(LA145_1>=BACKQUOTE && LA145_1<=STRING)||(LA145_1>=TRISTRINGPART && LA145_1<=STRINGPART)) ) {
                                alt145=1;
                            }


                        }


                        switch (alt145) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:850:18: COMMA argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arglist4897); if (state.failed) return ;
                    	    pushFollow(FOLLOW_argument_in_arglist4899);
                    	    argument();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop145;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:850:35: ( COMMA DOUBLESTAR test )?
                    int alt146=2;
                    int LA146_0 = input.LA(1);

                    if ( (LA146_0==COMMA) ) {
                        alt146=1;
                    }
                    switch (alt146) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:850:36: COMMA DOUBLESTAR test
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arglist4904); if (state.failed) return ;
                            match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_arglist4906); if (state.failed) return ;
                            pushFollow(FOLLOW_test_in_arglist4908);
                            test();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:851:7: DOUBLESTAR test
                    {
                    match(input,DOUBLESTAR,FOLLOW_DOUBLESTAR_in_arglist4918); if (state.failed) return ;
                    pushFollow(FOLLOW_test_in_arglist4920);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "arglist"


    // $ANTLR start "argument"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:855:1: argument : test ( ( ASSIGN test ) | comp_for | ) ;
    public final void argument() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:856:5: ( test ( ( ASSIGN test ) | comp_for | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:856:7: test ( ( ASSIGN test ) | comp_for | )
            {
            pushFollow(FOLLOW_test_in_argument4938);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:857:9: ( ( ASSIGN test ) | comp_for | )
            int alt148=3;
            switch ( input.LA(1) ) {
            case ASSIGN:
                {
                alt148=1;
                }
                break;
            case FOR:
                {
                alt148=2;
                }
                break;
            case RPAREN:
            case COMMA:
                {
                alt148=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 148, 0, input);

                throw nvae;
            }

            switch (alt148) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:857:10: ( ASSIGN test )
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:857:10: ( ASSIGN test )
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:857:11: ASSIGN test
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_argument4950); if (state.failed) return ;
                    pushFollow(FOLLOW_test_in_argument4952);
                    test();

                    state._fsp--;
                    if (state.failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:858:11: comp_for
                    {
                    pushFollow(FOLLOW_comp_for_in_argument4965);
                    comp_for();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:860:9: 
                    {
                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "argument"


    // $ANTLR start "list_iter"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:864:1: list_iter : ( list_for | list_if );
    public final void list_iter() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:865:5: ( list_for | list_if )
            int alt149=2;
            int LA149_0 = input.LA(1);

            if ( (LA149_0==FOR) ) {
                alt149=1;
            }
            else if ( (LA149_0==IF) ) {
                alt149=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 149, 0, input);

                throw nvae;
            }
            switch (alt149) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:865:7: list_for
                    {
                    pushFollow(FOLLOW_list_for_in_list_iter5003);
                    list_for();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:866:7: list_if
                    {
                    pushFollow(FOLLOW_list_if_in_list_iter5011);
                    list_if();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "list_iter"


    // $ANTLR start "list_for"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:870:1: list_for : FOR exprlist IN testlist ( list_iter )? ;
    public final void list_for() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:871:5: ( FOR exprlist IN testlist ( list_iter )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:871:7: FOR exprlist IN testlist ( list_iter )?
            {
            match(input,FOR,FOLLOW_FOR_in_list_for5029); if (state.failed) return ;
            pushFollow(FOLLOW_exprlist_in_list_for5031);
            exprlist();

            state._fsp--;
            if (state.failed) return ;
            match(input,IN,FOLLOW_IN_in_list_for5033); if (state.failed) return ;
            pushFollow(FOLLOW_testlist_in_list_for5035);
            testlist();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:871:32: ( list_iter )?
            int alt150=2;
            int LA150_0 = input.LA(1);

            if ( (LA150_0==FOR||LA150_0==IF) ) {
                alt150=1;
            }
            switch (alt150) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:871:33: list_iter
                    {
                    pushFollow(FOLLOW_list_iter_in_list_for5038);
                    list_iter();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "list_for"


    // $ANTLR start "list_if"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:875:1: list_if : IF test ( list_iter )? ;
    public final void list_if() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:876:5: ( IF test ( list_iter )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:876:7: IF test ( list_iter )?
            {
            match(input,IF,FOLLOW_IF_in_list_if5058); if (state.failed) return ;
            pushFollow(FOLLOW_test_in_list_if5060);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:876:15: ( list_iter )?
            int alt151=2;
            int LA151_0 = input.LA(1);

            if ( (LA151_0==FOR||LA151_0==IF) ) {
                alt151=1;
            }
            switch (alt151) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:876:16: list_iter
                    {
                    pushFollow(FOLLOW_list_iter_in_list_if5063);
                    list_iter();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "list_if"


    // $ANTLR start "comp_iter"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:880:1: comp_iter : ( comp_for | comp_if );
    public final void comp_iter() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:881:5: ( comp_for | comp_if )
            int alt152=2;
            int LA152_0 = input.LA(1);

            if ( (LA152_0==FOR) ) {
                alt152=1;
            }
            else if ( (LA152_0==IF) ) {
                alt152=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 152, 0, input);

                throw nvae;
            }
            switch (alt152) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:881:7: comp_for
                    {
                    pushFollow(FOLLOW_comp_for_in_comp_iter5083);
                    comp_for();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:882:7: comp_if
                    {
                    pushFollow(FOLLOW_comp_if_in_comp_iter5091);
                    comp_if();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "comp_iter"


    // $ANTLR start "comp_for"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:886:1: comp_for : FOR exprlist IN or_test ( comp_iter )? ;
    public final void comp_for() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:887:5: ( FOR exprlist IN or_test ( comp_iter )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:887:7: FOR exprlist IN or_test ( comp_iter )?
            {
            match(input,FOR,FOLLOW_FOR_in_comp_for5109); if (state.failed) return ;
            pushFollow(FOLLOW_exprlist_in_comp_for5111);
            exprlist();

            state._fsp--;
            if (state.failed) return ;
            match(input,IN,FOLLOW_IN_in_comp_for5113); if (state.failed) return ;
            pushFollow(FOLLOW_or_test_in_comp_for5115);
            or_test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:887:31: ( comp_iter )?
            int alt153=2;
            int LA153_0 = input.LA(1);

            if ( (LA153_0==FOR||LA153_0==IF) ) {
                alt153=1;
            }
            switch (alt153) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:887:31: comp_iter
                    {
                    pushFollow(FOLLOW_comp_iter_in_comp_for5117);
                    comp_iter();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "comp_for"


    // $ANTLR start "comp_if"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:891:1: comp_if : IF test ( comp_iter )? ;
    public final void comp_if() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:892:5: ( IF test ( comp_iter )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:892:7: IF test ( comp_iter )?
            {
            match(input,IF,FOLLOW_IF_in_comp_if5136); if (state.failed) return ;
            pushFollow(FOLLOW_test_in_comp_if5138);
            test();

            state._fsp--;
            if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:892:15: ( comp_iter )?
            int alt154=2;
            int LA154_0 = input.LA(1);

            if ( (LA154_0==FOR||LA154_0==IF) ) {
                alt154=1;
            }
            switch (alt154) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:892:15: comp_iter
                    {
                    pushFollow(FOLLOW_comp_iter_in_comp_if5140);
                    comp_iter();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "comp_if"


    // $ANTLR start "yield_expr"
    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:896:1: yield_expr : YIELD ( testlist )? ;
    public final void yield_expr() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:897:5: ( YIELD ( testlist )? )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:897:7: YIELD ( testlist )?
            {
            match(input,YIELD,FOLLOW_YIELD_in_yield_expr5159); if (state.failed) return ;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:897:13: ( testlist )?
            int alt155=2;
            int LA155_0 = input.LA(1);

            if ( (LA155_0==TRAILBACKSLASH||LA155_0==NAME||(LA155_0>=LAMBDA && LA155_0<=NOT)||LA155_0==LPAREN||(LA155_0>=PLUS && LA155_0<=MINUS)||(LA155_0>=TILDE && LA155_0<=LBRACK)||LA155_0==LCURLY||(LA155_0>=BACKQUOTE && LA155_0<=STRING)||(LA155_0>=TRISTRINGPART && LA155_0<=STRINGPART)) ) {
                alt155=1;
            }
            switch (alt155) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:897:13: testlist
                    {
                    pushFollow(FOLLOW_testlist_in_yield_expr5161);
                    testlist();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException e) {
            throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "yield_expr"

    // $ANTLR start synpred1_PythonPartial
    public final void synpred1_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:289:7: ( LPAREN fpdef COMMA )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:289:8: LPAREN fpdef COMMA
        {
        match(input,LPAREN,FOLLOW_LPAREN_in_synpred1_PythonPartial807); if (state.failed) return ;
        pushFollow(FOLLOW_fpdef_in_synpred1_PythonPartial809);
        fpdef();

        state._fsp--;
        if (state.failed) return ;
        match(input,COMMA,FOLLOW_COMMA_in_synpred1_PythonPartial811); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_PythonPartial

    // $ANTLR start synpred2_PythonPartial
    public final void synpred2_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:326:8: ( testlist augassign )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:326:9: testlist augassign
        {
        pushFollow(FOLLOW_testlist_in_synpred2_PythonPartial1107);
        testlist();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_augassign_in_synpred2_PythonPartial1109);
        augassign();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_PythonPartial

    // $ANTLR start synpred3_PythonPartial
    public final void synpred3_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:332:7: ( testlist ASSIGN )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:332:8: testlist ASSIGN
        {
        pushFollow(FOLLOW_testlist_in_synpred3_PythonPartial1187);
        testlist();

        state._fsp--;
        if (state.failed) return ;
        match(input,ASSIGN,FOLLOW_ASSIGN_in_synpred3_PythonPartial1189); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_PythonPartial

    // $ANTLR start synpred4_PythonPartial
    public final void synpred4_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:372:7: ( test COMMA )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:372:8: test COMMA
        {
        pushFollow(FOLLOW_test_in_synpred4_PythonPartial1472);
        test();

        state._fsp--;
        if (state.failed) return ;
        match(input,COMMA,FOLLOW_COMMA_in_synpred4_PythonPartial1474); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_PythonPartial

    // $ANTLR start synpred5_PythonPartial
    public final void synpred5_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:495:7: ( ( decorators )? DEF )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:495:8: ( decorators )? DEF
        {
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:495:8: ( decorators )?
        int alt156=2;
        int LA156_0 = input.LA(1);

        if ( (LA156_0==AT) ) {
            alt156=1;
        }
        switch (alt156) {
            case 1 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:495:8: decorators
                {
                pushFollow(FOLLOW_decorators_in_synpred5_PythonPartial2157);
                decorators();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        match(input,DEF,FOLLOW_DEF_in_synpred5_PythonPartial2160); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_PythonPartial

    // $ANTLR start synpred6_PythonPartial
    public final void synpred6_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:496:7: ( ( decorators )? CLASS )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:496:8: ( decorators )? CLASS
        {
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:496:8: ( decorators )?
        int alt157=2;
        int LA157_0 = input.LA(1);

        if ( (LA157_0==AT) ) {
            alt157=1;
        }
        switch (alt157) {
            case 1 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:496:8: decorators
                {
                pushFollow(FOLLOW_decorators_in_synpred6_PythonPartial2174);
                decorators();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        match(input,CLASS,FOLLOW_CLASS_in_synpred6_PythonPartial2177); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_PythonPartial

    // $ANTLR start synpred7_PythonPartial
    public final void synpred7_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:571:9: ( IF or_test ORELSE )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:571:10: IF or_test ORELSE
        {
        match(input,IF,FOLLOW_IF_in_synpred7_PythonPartial2752); if (state.failed) return ;
        pushFollow(FOLLOW_or_test_in_synpred7_PythonPartial2754);
        or_test();

        state._fsp--;
        if (state.failed) return ;
        match(input,ORELSE,FOLLOW_ORELSE_in_synpred7_PythonPartial2756); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_PythonPartial

    // $ANTLR start synpred8_PythonPartial
    public final void synpred8_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:785:7: ( test COLON )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:785:8: test COLON
        {
        pushFollow(FOLLOW_test_in_synpred8_PythonPartial4274);
        test();

        state._fsp--;
        if (state.failed) return ;
        match(input,COLON,FOLLOW_COLON_in_synpred8_PythonPartial4276); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_PythonPartial

    // $ANTLR start synpred9_PythonPartial
    public final void synpred9_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:787:7: ( COLON )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:787:8: COLON
        {
        match(input,COLON,FOLLOW_COLON_in_synpred9_PythonPartial4308); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_PythonPartial

    // $ANTLR start synpred10_PythonPartial
    public final void synpred10_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:7: ( expr COMMA )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:802:8: expr COMMA
        {
        pushFollow(FOLLOW_expr_in_synpred10_PythonPartial4393);
        expr();

        state._fsp--;
        if (state.failed) return ;
        match(input,COMMA,FOLLOW_COMMA_in_synpred10_PythonPartial4395); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_PythonPartial

    // $ANTLR start synpred11_PythonPartial
    public final void synpred11_PythonPartial_fragment() throws RecognitionException {   
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:814:7: ( test COMMA )
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/PythonPartial.g:814:8: test COMMA
        {
        pushFollow(FOLLOW_test_in_synpred11_PythonPartial4486);
        test();

        state._fsp--;
        if (state.failed) return ;
        match(input,COMMA,FOLLOW_COMMA_in_synpred11_PythonPartial4488); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_PythonPartial

    // Delegated rules

    public final boolean synpred3_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_PythonPartial() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_PythonPartial_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA27 dfa27 = new DFA27(this);
    protected DFA32 dfa32 = new DFA32(this);
    protected DFA36 dfa36 = new DFA36(this);
    protected DFA34 dfa34 = new DFA34(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA57 dfa57 = new DFA57(this);
    protected DFA75 dfa75 = new DFA75(this);
    protected DFA84 dfa84 = new DFA84(this);
    protected DFA108 dfa108 = new DFA108(this);
    protected DFA121 dfa121 = new DFA121(this);
    protected DFA125 dfa125 = new DFA125(this);
    protected DFA123 dfa123 = new DFA123(this);
    protected DFA126 dfa126 = new DFA126(this);
    protected DFA130 dfa130 = new DFA130(this);
    protected DFA128 dfa128 = new DFA128(this);
    protected DFA131 dfa131 = new DFA131(this);
    static final String DFA27_eotS =
        "\12\uffff";
    static final String DFA27_eofS =
        "\12\uffff";
    static final String DFA27_minS =
        "\1\6\11\uffff";
    static final String DFA27_maxS =
        "\1\156\11\uffff";
    static final String DFA27_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11";
    static final String DFA27_specialS =
        "\12\uffff}>";
    static final String[] DFA27_transitionS = {
            "\1\1\2\uffff\1\1\1\uffff\1\2\2\uffff\1\11\1\5\1\uffff\1\5\1"+
            "\uffff\1\3\2\uffff\1\10\1\uffff\1\6\1\uffff\1\7\1\uffff\1\6"+
            "\2\uffff\2\1\2\uffff\1\4\2\5\3\uffff\1\5\2\uffff\1\1\37\uffff"+
            "\2\1\3\uffff\2\1\1\uffff\1\1\1\uffff\6\1\21\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "312:1: small_stmt : ( expr_stmt | print_stmt | del_stmt | pass_stmt | flow_stmt | import_stmt | global_stmt | exec_stmt | assert_stmt );";
        }
    }
    static final String DFA32_eotS =
        "\26\uffff";
    static final String DFA32_eofS =
        "\26\uffff";
    static final String DFA32_minS =
        "\1\6\22\0\3\uffff";
    static final String DFA32_maxS =
        "\1\156\22\0\3\uffff";
    static final String DFA32_acceptS =
        "\23\uffff\1\1\1\2\1\3";
    static final String DFA32_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\1\15\1\16\1\17\1\20\1\21\3\uffff}>";
    static final String[] DFA32_transitionS = {
            "\1\21\2\uffff\1\11\25\uffff\1\22\1\1\13\uffff\1\5\37\uffff\1"+
            "\2\1\3\3\uffff\1\4\1\6\1\uffff\1\7\1\uffff\1\10\1\12\1\13\1"+
            "\14\1\15\1\16\21\uffff\1\17\1\20",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA32_eot = DFA.unpackEncodedString(DFA32_eotS);
    static final short[] DFA32_eof = DFA.unpackEncodedString(DFA32_eofS);
    static final char[] DFA32_min = DFA.unpackEncodedStringToUnsignedChars(DFA32_minS);
    static final char[] DFA32_max = DFA.unpackEncodedStringToUnsignedChars(DFA32_maxS);
    static final short[] DFA32_accept = DFA.unpackEncodedString(DFA32_acceptS);
    static final short[] DFA32_special = DFA.unpackEncodedString(DFA32_specialS);
    static final short[][] DFA32_transition;

    static {
        int numStates = DFA32_transitionS.length;
        DFA32_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA32_transition[i] = DFA.unpackEncodedString(DFA32_transitionS[i]);
        }
    }

    class DFA32 extends DFA {

        public DFA32(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 32;
            this.eot = DFA32_eot;
            this.eof = DFA32_eof;
            this.min = DFA32_min;
            this.max = DFA32_max;
            this.accept = DFA32_accept;
            this.special = DFA32_special;
            this.transition = DFA32_transition;
        }
        public String getDescription() {
            return "326:7: ( ( testlist augassign )=> testlist ( ( augassign yield_expr ) | ( augassign testlist ) ) | ( testlist ASSIGN )=> testlist ( | ( ( ASSIGN testlist )+ ) | ( ( ASSIGN yield_expr )+ ) ) | testlist )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA32_1 = input.LA(1);

                         
                        int index32_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA32_2 = input.LA(1);

                         
                        int index32_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA32_3 = input.LA(1);

                         
                        int index32_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA32_4 = input.LA(1);

                         
                        int index32_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA32_5 = input.LA(1);

                         
                        int index32_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA32_6 = input.LA(1);

                         
                        int index32_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA32_7 = input.LA(1);

                         
                        int index32_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA32_8 = input.LA(1);

                         
                        int index32_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA32_9 = input.LA(1);

                         
                        int index32_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA32_10 = input.LA(1);

                         
                        int index32_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA32_11 = input.LA(1);

                         
                        int index32_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA32_12 = input.LA(1);

                         
                        int index32_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA32_13 = input.LA(1);

                         
                        int index32_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA32_14 = input.LA(1);

                         
                        int index32_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA32_15 = input.LA(1);

                         
                        int index32_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA32_16 = input.LA(1);

                         
                        int index32_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA32_17 = input.LA(1);

                         
                        int index32_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA32_18 = input.LA(1);

                         
                        int index32_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_PythonPartial()) ) {s = 19;}

                        else if ( (synpred3_PythonPartial()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index32_18);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 32, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA36_eotS =
        "\25\uffff";
    static final String DFA36_eofS =
        "\25\uffff";
    static final String DFA36_minS =
        "\1\6\22\0\2\uffff";
    static final String DFA36_maxS =
        "\1\156\22\0\2\uffff";
    static final String DFA36_acceptS =
        "\23\uffff\1\1\1\2";
    static final String DFA36_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\1\15\1\16\1\17\1\20\1\21\2\uffff}>";
    static final String[] DFA36_transitionS = {
            "\1\21\2\uffff\1\11\25\uffff\1\22\1\1\13\uffff\1\5\37\uffff\1"+
            "\2\1\3\3\uffff\1\4\1\6\1\uffff\1\7\1\uffff\1\10\1\12\1\13\1"+
            "\14\1\15\1\16\21\uffff\1\17\1\20",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "371:1: printlist : ( ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )? | test );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA36_1 = input.LA(1);

                         
                        int index36_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA36_2 = input.LA(1);

                         
                        int index36_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA36_3 = input.LA(1);

                         
                        int index36_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA36_4 = input.LA(1);

                         
                        int index36_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA36_5 = input.LA(1);

                         
                        int index36_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA36_6 = input.LA(1);

                         
                        int index36_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA36_7 = input.LA(1);

                         
                        int index36_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA36_8 = input.LA(1);

                         
                        int index36_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA36_9 = input.LA(1);

                         
                        int index36_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA36_10 = input.LA(1);

                         
                        int index36_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA36_11 = input.LA(1);

                         
                        int index36_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA36_12 = input.LA(1);

                         
                        int index36_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA36_13 = input.LA(1);

                         
                        int index36_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA36_14 = input.LA(1);

                         
                        int index36_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA36_15 = input.LA(1);

                         
                        int index36_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA36_16 = input.LA(1);

                         
                        int index36_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA36_17 = input.LA(1);

                         
                        int index36_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA36_18 = input.LA(1);

                         
                        int index36_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index36_18);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 36, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA34_eotS =
        "\30\uffff";
    static final String DFA34_eofS =
        "\2\2\26\uffff";
    static final String DFA34_minS =
        "\1\7\1\6\26\uffff";
    static final String DFA34_maxS =
        "\1\63\1\156\26\uffff";
    static final String DFA34_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\23\uffff";
    static final String DFA34_specialS =
        "\30\uffff}>";
    static final String[] DFA34_transitionS = {
            "\1\2\50\uffff\1\1\2\uffff\1\2",
            "\1\4\1\2\1\uffff\1\4\25\uffff\2\4\13\uffff\1\4\6\uffff\1\2"+
            "\30\uffff\2\4\3\uffff\2\4\1\uffff\1\4\1\uffff\6\4\21\uffff\2"+
            "\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
    static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
    static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
    static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
    static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
    static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
    static final short[][] DFA34_transition;

    static {
        int numStates = DFA34_transitionS.length;
        DFA34_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
        }
    }

    class DFA34 extends DFA {

        public DFA34(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 34;
            this.eot = DFA34_eot;
            this.eof = DFA34_eof;
            this.min = DFA34_min;
            this.max = DFA34_max;
            this.accept = DFA34_accept;
            this.special = DFA34_special;
            this.transition = DFA34_transition;
        }
        public String getDescription() {
            return "()* loopback of 373:13: ( options {k=2; } : COMMA test )*";
        }
    }
    static final String DFA45_eotS =
        "\4\uffff";
    static final String DFA45_eofS =
        "\4\uffff";
    static final String DFA45_minS =
        "\2\11\2\uffff";
    static final String DFA45_maxS =
        "\1\12\1\34\2\uffff";
    static final String DFA45_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA45_specialS =
        "\4\uffff}>";
    static final String[] DFA45_transitionS = {
            "\1\2\1\1",
            "\1\2\1\1\21\uffff\1\3",
            "",
            ""
    };

    static final short[] DFA45_eot = DFA.unpackEncodedString(DFA45_eotS);
    static final short[] DFA45_eof = DFA.unpackEncodedString(DFA45_eofS);
    static final char[] DFA45_min = DFA.unpackEncodedStringToUnsignedChars(DFA45_minS);
    static final char[] DFA45_max = DFA.unpackEncodedStringToUnsignedChars(DFA45_maxS);
    static final short[] DFA45_accept = DFA.unpackEncodedString(DFA45_acceptS);
    static final short[] DFA45_special = DFA.unpackEncodedString(DFA45_specialS);
    static final short[][] DFA45_transition;

    static {
        int numStates = DFA45_transitionS.length;
        DFA45_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA45_transition[i] = DFA.unpackEncodedString(DFA45_transitionS[i]);
        }
    }

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = DFA45_eot;
            this.eof = DFA45_eof;
            this.min = DFA45_min;
            this.max = DFA45_max;
            this.accept = DFA45_accept;
            this.special = DFA45_special;
            this.transition = DFA45_transition;
        }
        public String getDescription() {
            return "440:12: ( ( DOT )* dotted_name | ( DOT )+ )";
        }
    }
    static final String DFA57_eotS =
        "\12\uffff";
    static final String DFA57_eofS =
        "\12\uffff";
    static final String DFA57_minS =
        "\1\20\5\uffff\1\0\3\uffff";
    static final String DFA57_maxS =
        "\1\53\5\uffff\1\0\3\uffff";
    static final String DFA57_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\uffff\1\6\1\7\1\10";
    static final String DFA57_specialS =
        "\1\0\5\uffff\1\1\3\uffff}>";
    static final String[] DFA57_transitionS = {
            "\1\10\1\uffff\1\7\6\uffff\1\3\1\uffff\1\1\12\uffff\1\4\1\2\1"+
            "\5\2\uffff\1\6",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA57_eot = DFA.unpackEncodedString(DFA57_eotS);
    static final short[] DFA57_eof = DFA.unpackEncodedString(DFA57_eofS);
    static final char[] DFA57_min = DFA.unpackEncodedStringToUnsignedChars(DFA57_minS);
    static final char[] DFA57_max = DFA.unpackEncodedStringToUnsignedChars(DFA57_maxS);
    static final short[] DFA57_accept = DFA.unpackEncodedString(DFA57_acceptS);
    static final short[] DFA57_special = DFA.unpackEncodedString(DFA57_specialS);
    static final short[][] DFA57_transition;

    static {
        int numStates = DFA57_transitionS.length;
        DFA57_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA57_transition[i] = DFA.unpackEncodedString(DFA57_transitionS[i]);
        }
    }

    class DFA57 extends DFA {

        public DFA57(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 57;
            this.eot = DFA57_eot;
            this.eof = DFA57_eof;
            this.min = DFA57_min;
            this.max = DFA57_max;
            this.accept = DFA57_accept;
            this.special = DFA57_special;
            this.transition = DFA57_transition;
        }
        public String getDescription() {
            return "489:1: compound_stmt : ( if_stmt | while_stmt | for_stmt | try_stmt | with_stmt | ( ( decorators )? DEF )=> funcdef | ( ( decorators )? CLASS )=> classdef | decorators );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA57_0 = input.LA(1);

                         
                        int index57_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_0==IF) ) {s = 1;}

                        else if ( (LA57_0==WHILE) ) {s = 2;}

                        else if ( (LA57_0==FOR) ) {s = 3;}

                        else if ( (LA57_0==TRY) ) {s = 4;}

                        else if ( (LA57_0==WITH) ) {s = 5;}

                        else if ( (LA57_0==AT) ) {s = 6;}

                        else if ( (LA57_0==DEF) && (synpred5_PythonPartial())) {s = 7;}

                        else if ( (LA57_0==CLASS) && (synpred6_PythonPartial())) {s = 8;}

                         
                        input.seek(index57_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA57_6 = input.LA(1);

                         
                        int index57_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_PythonPartial()) ) {s = 7;}

                        else if ( (synpred6_PythonPartial()) ) {s = 8;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index57_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 57, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA75_eotS =
        "\20\uffff";
    static final String DFA75_eofS =
        "\1\2\17\uffff";
    static final String DFA75_minS =
        "\1\7\1\0\16\uffff";
    static final String DFA75_maxS =
        "\1\126\1\0\16\uffff";
    static final String DFA75_acceptS =
        "\2\uffff\1\2\14\uffff\1\1";
    static final String DFA75_specialS =
        "\1\uffff\1\0\16\uffff}>";
    static final String[] DFA75_transitionS = {
            "\1\2\5\uffff\1\2\13\uffff\1\2\1\uffff\1\1\21\uffff\4\2\2\uffff"+
            "\15\2\23\uffff\1\2\1\uffff\2\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA75_eot = DFA.unpackEncodedString(DFA75_eotS);
    static final short[] DFA75_eof = DFA.unpackEncodedString(DFA75_eofS);
    static final char[] DFA75_min = DFA.unpackEncodedStringToUnsignedChars(DFA75_minS);
    static final char[] DFA75_max = DFA.unpackEncodedStringToUnsignedChars(DFA75_maxS);
    static final short[] DFA75_accept = DFA.unpackEncodedString(DFA75_acceptS);
    static final short[] DFA75_special = DFA.unpackEncodedString(DFA75_specialS);
    static final short[][] DFA75_transition;

    static {
        int numStates = DFA75_transitionS.length;
        DFA75_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA75_transition[i] = DFA.unpackEncodedString(DFA75_transitionS[i]);
        }
    }

    class DFA75 extends DFA {

        public DFA75(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 75;
            this.eot = DFA75_eot;
            this.eof = DFA75_eof;
            this.min = DFA75_min;
            this.max = DFA75_max;
            this.accept = DFA75_accept;
            this.special = DFA75_special;
            this.transition = DFA75_transition;
        }
        public String getDescription() {
            return "571:7: ( ( IF or_test ORELSE )=> IF or_test ORELSE test | )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA75_1 = input.LA(1);

                         
                        int index75_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_PythonPartial()) ) {s = 15;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index75_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 75, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA84_eotS =
        "\15\uffff";
    static final String DFA84_eofS =
        "\15\uffff";
    static final String DFA84_minS =
        "\1\35\11\uffff\1\6\2\uffff";
    static final String DFA84_maxS =
        "\1\107\11\uffff\1\156\2\uffff";
    static final String DFA84_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\uffff\1\13\1\12";
    static final String DFA84_specialS =
        "\15\uffff}>";
    static final String[] DFA84_transitionS = {
            "\1\10\1\12\1\uffff\1\11\40\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14\2\uffff\1\14\26\uffff\1\13\13\uffff\1\14\37\uffff\2\14"+
            "\3\uffff\2\14\1\uffff\1\14\1\uffff\6\14\21\uffff\2\14",
            "",
            ""
    };

    static final short[] DFA84_eot = DFA.unpackEncodedString(DFA84_eotS);
    static final short[] DFA84_eof = DFA.unpackEncodedString(DFA84_eofS);
    static final char[] DFA84_min = DFA.unpackEncodedStringToUnsignedChars(DFA84_minS);
    static final char[] DFA84_max = DFA.unpackEncodedStringToUnsignedChars(DFA84_maxS);
    static final short[] DFA84_accept = DFA.unpackEncodedString(DFA84_acceptS);
    static final short[] DFA84_special = DFA.unpackEncodedString(DFA84_specialS);
    static final short[][] DFA84_transition;

    static {
        int numStates = DFA84_transitionS.length;
        DFA84_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA84_transition[i] = DFA.unpackEncodedString(DFA84_transitionS[i]);
        }
    }

    class DFA84 extends DFA {

        public DFA84(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 84;
            this.eot = DFA84_eot;
            this.eof = DFA84_eof;
            this.min = DFA84_min;
            this.max = DFA84_max;
            this.accept = DFA84_accept;
            this.special = DFA84_special;
            this.transition = DFA84_transition;
        }
        public String getDescription() {
            return "611:1: comp_op : ( LESS | GREATER | EQUAL | GREATEREQUAL | LESSEQUAL | ALT_NOTEQUAL | NOTEQUAL | IN | NOT IN | IS | IS NOT );";
        }
    }
    static final String DFA108_eotS =
        "\26\uffff";
    static final String DFA108_eofS =
        "\26\uffff";
    static final String DFA108_minS =
        "\1\55\1\6\24\uffff";
    static final String DFA108_maxS =
        "\1\60\1\156\24\uffff";
    static final String DFA108_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\21\uffff";
    static final String DFA108_specialS =
        "\26\uffff}>";
    static final String[] DFA108_transitionS = {
            "\1\2\2\uffff\1\1",
            "\1\4\2\uffff\1\4\25\uffff\2\4\13\uffff\1\4\1\2\36\uffff\2\4"+
            "\3\uffff\2\4\1\uffff\1\4\1\uffff\6\4\21\uffff\2\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA108_eot = DFA.unpackEncodedString(DFA108_eotS);
    static final short[] DFA108_eof = DFA.unpackEncodedString(DFA108_eofS);
    static final char[] DFA108_min = DFA.unpackEncodedStringToUnsignedChars(DFA108_minS);
    static final char[] DFA108_max = DFA.unpackEncodedStringToUnsignedChars(DFA108_maxS);
    static final short[] DFA108_accept = DFA.unpackEncodedString(DFA108_acceptS);
    static final short[] DFA108_special = DFA.unpackEncodedString(DFA108_specialS);
    static final short[][] DFA108_transition;

    static {
        int numStates = DFA108_transitionS.length;
        DFA108_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA108_transition[i] = DFA.unpackEncodedString(DFA108_transitionS[i]);
        }
    }

    class DFA108 extends DFA {

        public DFA108(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 108;
            this.eot = DFA108_eot;
            this.eof = DFA108_eof;
            this.min = DFA108_min;
            this.max = DFA108_max;
            this.accept = DFA108_accept;
            this.special = DFA108_special;
            this.transition = DFA108_transition;
        }
        public String getDescription() {
            return "()* loopback of 754:12: ( options {k=2; } : COMMA test )*";
        }
    }
    static final String DFA121_eotS =
        "\27\uffff";
    static final String DFA121_eofS =
        "\27\uffff";
    static final String DFA121_minS =
        "\1\6\1\uffff\22\0\3\uffff";
    static final String DFA121_maxS =
        "\1\156\1\uffff\22\0\3\uffff";
    static final String DFA121_acceptS =
        "\1\uffff\1\1\22\uffff\1\3\1\2\1\4";
    static final String DFA121_specialS =
        "\1\0\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\1\15\1\16\1\17\1\20\1\21\1\22\3\uffff}>";
    static final String[] DFA121_transitionS = {
            "\1\22\2\uffff\1\12\1\1\24\uffff\1\23\1\2\13\uffff\1\6\1\uffff"+
            "\1\24\35\uffff\1\3\1\4\3\uffff\1\5\1\7\1\uffff\1\10\1\uffff"+
            "\1\11\1\13\1\14\1\15\1\16\1\17\21\uffff\1\20\1\21",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA121_eot = DFA.unpackEncodedString(DFA121_eotS);
    static final short[] DFA121_eof = DFA.unpackEncodedString(DFA121_eofS);
    static final char[] DFA121_min = DFA.unpackEncodedStringToUnsignedChars(DFA121_minS);
    static final char[] DFA121_max = DFA.unpackEncodedStringToUnsignedChars(DFA121_maxS);
    static final short[] DFA121_accept = DFA.unpackEncodedString(DFA121_acceptS);
    static final short[] DFA121_special = DFA.unpackEncodedString(DFA121_specialS);
    static final short[][] DFA121_transition;

    static {
        int numStates = DFA121_transitionS.length;
        DFA121_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA121_transition[i] = DFA.unpackEncodedString(DFA121_transitionS[i]);
        }
    }

    class DFA121 extends DFA {

        public DFA121(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 121;
            this.eot = DFA121_eot;
            this.eof = DFA121_eof;
            this.min = DFA121_min;
            this.max = DFA121_max;
            this.accept = DFA121_accept;
            this.special = DFA121_special;
            this.transition = DFA121_transition;
        }
        public String getDescription() {
            return "783:1: subscript : ( DOT DOT DOT | ( test COLON )=> test ( COLON ( test )? ( sliceop )? )? | ( COLON )=> COLON ( test )? ( sliceop )? | test );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA121_0 = input.LA(1);

                         
                        int index121_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA121_0==DOT) ) {s = 1;}

                        else if ( (LA121_0==NOT) ) {s = 2;}

                        else if ( (LA121_0==PLUS) ) {s = 3;}

                        else if ( (LA121_0==MINUS) ) {s = 4;}

                        else if ( (LA121_0==TILDE) ) {s = 5;}

                        else if ( (LA121_0==LPAREN) ) {s = 6;}

                        else if ( (LA121_0==LBRACK) ) {s = 7;}

                        else if ( (LA121_0==LCURLY) ) {s = 8;}

                        else if ( (LA121_0==BACKQUOTE) ) {s = 9;}

                        else if ( (LA121_0==NAME) ) {s = 10;}

                        else if ( (LA121_0==INT) ) {s = 11;}

                        else if ( (LA121_0==LONGINT) ) {s = 12;}

                        else if ( (LA121_0==FLOAT) ) {s = 13;}

                        else if ( (LA121_0==COMPLEX) ) {s = 14;}

                        else if ( (LA121_0==STRING) ) {s = 15;}

                        else if ( (LA121_0==TRISTRINGPART) ) {s = 16;}

                        else if ( (LA121_0==STRINGPART) ) {s = 17;}

                        else if ( (LA121_0==TRAILBACKSLASH) ) {s = 18;}

                        else if ( (LA121_0==LAMBDA) ) {s = 19;}

                        else if ( (LA121_0==COLON) && (synpred9_PythonPartial())) {s = 20;}

                         
                        input.seek(index121_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA121_2 = input.LA(1);

                         
                        int index121_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA121_3 = input.LA(1);

                         
                        int index121_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA121_4 = input.LA(1);

                         
                        int index121_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA121_5 = input.LA(1);

                         
                        int index121_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA121_6 = input.LA(1);

                         
                        int index121_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA121_7 = input.LA(1);

                         
                        int index121_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA121_8 = input.LA(1);

                         
                        int index121_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA121_9 = input.LA(1);

                         
                        int index121_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA121_10 = input.LA(1);

                         
                        int index121_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA121_11 = input.LA(1);

                         
                        int index121_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA121_12 = input.LA(1);

                         
                        int index121_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA121_13 = input.LA(1);

                         
                        int index121_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA121_14 = input.LA(1);

                         
                        int index121_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA121_15 = input.LA(1);

                         
                        int index121_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA121_16 = input.LA(1);

                         
                        int index121_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA121_17 = input.LA(1);

                         
                        int index121_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA121_18 = input.LA(1);

                         
                        int index121_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_18);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA121_19 = input.LA(1);

                         
                        int index121_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_PythonPartial()) ) {s = 21;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index121_19);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 121, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA125_eotS =
        "\23\uffff";
    static final String DFA125_eofS =
        "\23\uffff";
    static final String DFA125_minS =
        "\1\6\20\0\2\uffff";
    static final String DFA125_maxS =
        "\1\156\20\0\2\uffff";
    static final String DFA125_acceptS =
        "\21\uffff\1\1\1\2";
    static final String DFA125_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\1\15\1\16\1\17\2\uffff}>";
    static final String[] DFA125_transitionS = {
            "\1\20\2\uffff\1\10\42\uffff\1\4\37\uffff\1\1\1\2\3\uffff\1\3"+
            "\1\5\1\uffff\1\6\1\uffff\1\7\1\11\1\12\1\13\1\14\1\15\21\uffff"+
            "\1\16\1\17",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA125_eot = DFA.unpackEncodedString(DFA125_eotS);
    static final short[] DFA125_eof = DFA.unpackEncodedString(DFA125_eofS);
    static final char[] DFA125_min = DFA.unpackEncodedStringToUnsignedChars(DFA125_minS);
    static final char[] DFA125_max = DFA.unpackEncodedStringToUnsignedChars(DFA125_maxS);
    static final short[] DFA125_accept = DFA.unpackEncodedString(DFA125_acceptS);
    static final short[] DFA125_special = DFA.unpackEncodedString(DFA125_specialS);
    static final short[][] DFA125_transition;

    static {
        int numStates = DFA125_transitionS.length;
        DFA125_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA125_transition[i] = DFA.unpackEncodedString(DFA125_transitionS[i]);
        }
    }

    class DFA125 extends DFA {

        public DFA125(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 125;
            this.eot = DFA125_eot;
            this.eof = DFA125_eof;
            this.min = DFA125_min;
            this.max = DFA125_max;
            this.accept = DFA125_accept;
            this.special = DFA125_special;
            this.transition = DFA125_transition;
        }
        public String getDescription() {
            return "801:1: exprlist : ( ( expr COMMA )=> expr ( options {k=2; } : COMMA expr )* ( COMMA )? | expr );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA125_1 = input.LA(1);

                         
                        int index125_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA125_2 = input.LA(1);

                         
                        int index125_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA125_3 = input.LA(1);

                         
                        int index125_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA125_4 = input.LA(1);

                         
                        int index125_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA125_5 = input.LA(1);

                         
                        int index125_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA125_6 = input.LA(1);

                         
                        int index125_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA125_7 = input.LA(1);

                         
                        int index125_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA125_8 = input.LA(1);

                         
                        int index125_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA125_9 = input.LA(1);

                         
                        int index125_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA125_10 = input.LA(1);

                         
                        int index125_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA125_11 = input.LA(1);

                         
                        int index125_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA125_12 = input.LA(1);

                         
                        int index125_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA125_13 = input.LA(1);

                         
                        int index125_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA125_14 = input.LA(1);

                         
                        int index125_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA125_15 = input.LA(1);

                         
                        int index125_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA125_16 = input.LA(1);

                         
                        int index125_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_PythonPartial()) ) {s = 17;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index125_16);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 125, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA123_eotS =
        "\30\uffff";
    static final String DFA123_eofS =
        "\2\2\26\uffff";
    static final String DFA123_minS =
        "\1\7\1\6\26\uffff";
    static final String DFA123_maxS =
        "\1\63\1\156\26\uffff";
    static final String DFA123_acceptS =
        "\2\uffff\1\2\5\uffff\1\1\17\uffff";
    static final String DFA123_specialS =
        "\30\uffff}>";
    static final String[] DFA123_transitionS = {
            "\1\2\25\uffff\1\2\22\uffff\1\1\2\uffff\1\2",
            "\1\10\1\2\1\uffff\1\10\23\uffff\1\2\16\uffff\1\10\6\uffff\1"+
            "\2\30\uffff\2\10\3\uffff\2\10\1\uffff\1\10\1\uffff\6\10\21\uffff"+
            "\2\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA123_eot = DFA.unpackEncodedString(DFA123_eotS);
    static final short[] DFA123_eof = DFA.unpackEncodedString(DFA123_eofS);
    static final char[] DFA123_min = DFA.unpackEncodedStringToUnsignedChars(DFA123_minS);
    static final char[] DFA123_max = DFA.unpackEncodedStringToUnsignedChars(DFA123_maxS);
    static final short[] DFA123_accept = DFA.unpackEncodedString(DFA123_acceptS);
    static final short[] DFA123_special = DFA.unpackEncodedString(DFA123_specialS);
    static final short[][] DFA123_transition;

    static {
        int numStates = DFA123_transitionS.length;
        DFA123_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA123_transition[i] = DFA.unpackEncodedString(DFA123_transitionS[i]);
        }
    }

    class DFA123 extends DFA {

        public DFA123(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 123;
            this.eot = DFA123_eot;
            this.eof = DFA123_eof;
            this.min = DFA123_min;
            this.max = DFA123_max;
            this.accept = DFA123_accept;
            this.special = DFA123_special;
            this.transition = DFA123_transition;
        }
        public String getDescription() {
            return "()* loopback of 802:28: ( options {k=2; } : COMMA expr )*";
        }
    }
    static final String DFA126_eotS =
        "\24\uffff";
    static final String DFA126_eofS =
        "\2\2\22\uffff";
    static final String DFA126_minS =
        "\1\60\1\6\22\uffff";
    static final String DFA126_maxS =
        "\1\60\1\156\22\uffff";
    static final String DFA126_acceptS =
        "\2\uffff\1\2\1\1\20\uffff";
    static final String DFA126_specialS =
        "\24\uffff}>";
    static final String[] DFA126_transitionS = {
            "\1\1",
            "\1\3\2\uffff\1\3\42\uffff\1\3\37\uffff\2\3\3\uffff\2\3\1\uffff"+
            "\1\3\1\uffff\6\3\21\uffff\2\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA126_eot = DFA.unpackEncodedString(DFA126_eotS);
    static final short[] DFA126_eof = DFA.unpackEncodedString(DFA126_eofS);
    static final char[] DFA126_min = DFA.unpackEncodedStringToUnsignedChars(DFA126_minS);
    static final char[] DFA126_max = DFA.unpackEncodedStringToUnsignedChars(DFA126_maxS);
    static final short[] DFA126_accept = DFA.unpackEncodedString(DFA126_acceptS);
    static final short[] DFA126_special = DFA.unpackEncodedString(DFA126_specialS);
    static final short[][] DFA126_transition;

    static {
        int numStates = DFA126_transitionS.length;
        DFA126_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA126_transition[i] = DFA.unpackEncodedString(DFA126_transitionS[i]);
        }
    }

    class DFA126 extends DFA {

        public DFA126(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 126;
            this.eot = DFA126_eot;
            this.eof = DFA126_eof;
            this.min = DFA126_min;
            this.max = DFA126_max;
            this.accept = DFA126_accept;
            this.special = DFA126_special;
            this.transition = DFA126_transition;
        }
        public String getDescription() {
            return "()* loopback of 809:12: ( options {k=2; } : COMMA expr )*";
        }
    }
    static final String DFA130_eotS =
        "\25\uffff";
    static final String DFA130_eofS =
        "\25\uffff";
    static final String DFA130_minS =
        "\1\6\22\0\2\uffff";
    static final String DFA130_maxS =
        "\1\156\22\0\2\uffff";
    static final String DFA130_acceptS =
        "\23\uffff\1\1\1\2";
    static final String DFA130_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\1\15\1\16\1\17\1\20\1\21\2\uffff}>";
    static final String[] DFA130_transitionS = {
            "\1\21\2\uffff\1\11\25\uffff\1\22\1\1\13\uffff\1\5\37\uffff\1"+
            "\2\1\3\3\uffff\1\4\1\6\1\uffff\1\7\1\uffff\1\10\1\12\1\13\1"+
            "\14\1\15\1\16\21\uffff\1\17\1\20",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA130_eot = DFA.unpackEncodedString(DFA130_eotS);
    static final short[] DFA130_eof = DFA.unpackEncodedString(DFA130_eofS);
    static final char[] DFA130_min = DFA.unpackEncodedStringToUnsignedChars(DFA130_minS);
    static final char[] DFA130_max = DFA.unpackEncodedStringToUnsignedChars(DFA130_maxS);
    static final short[] DFA130_accept = DFA.unpackEncodedString(DFA130_acceptS);
    static final short[] DFA130_special = DFA.unpackEncodedString(DFA130_specialS);
    static final short[][] DFA130_transition;

    static {
        int numStates = DFA130_transitionS.length;
        DFA130_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA130_transition[i] = DFA.unpackEncodedString(DFA130_transitionS[i]);
        }
    }

    class DFA130 extends DFA {

        public DFA130(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 130;
            this.eot = DFA130_eot;
            this.eof = DFA130_eof;
            this.min = DFA130_min;
            this.max = DFA130_max;
            this.accept = DFA130_accept;
            this.special = DFA130_special;
            this.transition = DFA130_transition;
        }
        public String getDescription() {
            return "813:1: testlist : ( ( test COMMA )=> test ( options {k=2; } : COMMA test )* ( COMMA )? | test );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA130_1 = input.LA(1);

                         
                        int index130_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA130_2 = input.LA(1);

                         
                        int index130_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA130_3 = input.LA(1);

                         
                        int index130_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA130_4 = input.LA(1);

                         
                        int index130_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA130_5 = input.LA(1);

                         
                        int index130_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA130_6 = input.LA(1);

                         
                        int index130_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA130_7 = input.LA(1);

                         
                        int index130_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA130_8 = input.LA(1);

                         
                        int index130_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA130_9 = input.LA(1);

                         
                        int index130_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA130_10 = input.LA(1);

                         
                        int index130_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA130_11 = input.LA(1);

                         
                        int index130_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA130_12 = input.LA(1);

                         
                        int index130_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA130_13 = input.LA(1);

                         
                        int index130_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA130_14 = input.LA(1);

                         
                        int index130_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA130_15 = input.LA(1);

                         
                        int index130_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA130_16 = input.LA(1);

                         
                        int index130_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA130_17 = input.LA(1);

                         
                        int index130_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA130_18 = input.LA(1);

                         
                        int index130_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_PythonPartial()) ) {s = 19;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index130_18);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 130, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA128_eotS =
        "\52\uffff";
    static final String DFA128_eofS =
        "\2\2\50\uffff";
    static final String DFA128_minS =
        "\1\7\1\6\50\uffff";
    static final String DFA128_maxS =
        "\1\126\1\156\50\uffff";
    static final String DFA128_acceptS =
        "\2\uffff\1\2\12\uffff\1\1\6\uffff\1\1\25\uffff";
    static final String DFA128_specialS =
        "\52\uffff}>";
    static final String[] DFA128_transitionS = {
            "\1\2\21\uffff\1\2\1\uffff\1\2\21\uffff\3\2\1\1\2\uffff\15\2"+
            "\23\uffff\1\2\2\uffff\1\2",
            "\1\15\1\2\1\uffff\1\15\17\uffff\1\2\1\uffff\1\2\3\uffff\2\15"+
            "\13\uffff\1\15\4\2\2\uffff\15\2\14\uffff\2\15\3\uffff\2\15\1"+
            "\2\1\15\1\uffff\1\24\5\15\21\uffff\2\15",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA128_eot = DFA.unpackEncodedString(DFA128_eotS);
    static final short[] DFA128_eof = DFA.unpackEncodedString(DFA128_eofS);
    static final char[] DFA128_min = DFA.unpackEncodedStringToUnsignedChars(DFA128_minS);
    static final char[] DFA128_max = DFA.unpackEncodedStringToUnsignedChars(DFA128_maxS);
    static final short[] DFA128_accept = DFA.unpackEncodedString(DFA128_acceptS);
    static final short[] DFA128_special = DFA.unpackEncodedString(DFA128_specialS);
    static final short[][] DFA128_transition;

    static {
        int numStates = DFA128_transitionS.length;
        DFA128_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA128_transition[i] = DFA.unpackEncodedString(DFA128_transitionS[i]);
        }
    }

    class DFA128 extends DFA {

        public DFA128(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 128;
            this.eot = DFA128_eot;
            this.eof = DFA128_eof;
            this.min = DFA128_min;
            this.max = DFA128_max;
            this.accept = DFA128_accept;
            this.special = DFA128_special;
            this.transition = DFA128_transition;
        }
        public String getDescription() {
            return "()* loopback of 815:12: ( options {k=2; } : COMMA test )*";
        }
    }
    static final String DFA131_eotS =
        "\26\uffff";
    static final String DFA131_eofS =
        "\26\uffff";
    static final String DFA131_minS =
        "\1\60\1\6\24\uffff";
    static final String DFA131_maxS =
        "\1\125\1\156\24\uffff";
    static final String DFA131_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\21\uffff";
    static final String DFA131_specialS =
        "\26\uffff}>";
    static final String[] DFA131_transitionS = {
            "\1\1\44\uffff\1\2",
            "\1\4\2\uffff\1\4\25\uffff\2\4\13\uffff\1\4\37\uffff\2\4\3\uffff"+
            "\2\4\1\uffff\1\4\1\2\6\4\21\uffff\2\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA131_eot = DFA.unpackEncodedString(DFA131_eotS);
    static final short[] DFA131_eof = DFA.unpackEncodedString(DFA131_eofS);
    static final char[] DFA131_min = DFA.unpackEncodedStringToUnsignedChars(DFA131_minS);
    static final char[] DFA131_max = DFA.unpackEncodedStringToUnsignedChars(DFA131_maxS);
    static final short[] DFA131_accept = DFA.unpackEncodedString(DFA131_acceptS);
    static final short[] DFA131_special = DFA.unpackEncodedString(DFA131_specialS);
    static final short[][] DFA131_transition;

    static {
        int numStates = DFA131_transitionS.length;
        DFA131_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA131_transition[i] = DFA.unpackEncodedString(DFA131_transitionS[i]);
        }
    }

    class DFA131 extends DFA {

        public DFA131(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 131;
            this.eot = DFA131_eot;
            this.eof = DFA131_eof;
            this.min = DFA131_min;
            this.max = DFA131_max;
            this.accept = DFA131_accept;
            this.special = DFA131_special;
            this.transition = DFA131_transition;
        }
        public String getDescription() {
            return "()* loopback of 826:18: ( options {k=2; } : COMMA test COLON test )*";
        }
    }
 

    public static final BitSet FOLLOW_NEWLINE_in_single_input72 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_stmt_in_single_input80 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_compound_stmt_in_single_input88 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_NEWLINE_in_single_input90 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_single_input93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_WS_in_eval_input111 = new BitSet(new long[]{0x00001001800002C0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_NEWLINE_in_eval_input115 = new BitSet(new long[]{0x00001001800002C0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_eval_input119 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_NEWLINE_in_eval_input123 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_EOF_in_eval_input127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_dotted_attr145 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_DOT_in_dotted_attr156 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_dotted_attr158 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_set_in_attr0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_decorator464 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_dotted_attr_in_decorator466 = new BitSet(new long[]{0x0000100000000080L});
    public static final BitSet FOLLOW_LPAREN_in_decorator474 = new BitSet(new long[]{0x0006300180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_arglist_in_decorator484 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_decorator508 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_NEWLINE_in_decorator522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decorator_in_decorators540 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_decorators_in_funcdef559 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_DEF_in_funcdef562 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_funcdef564 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_parameters_in_funcdef566 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_funcdef568 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_funcdef570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_parameters588 = new BitSet(new long[]{0x0006300000000200L});
    public static final BitSet FOLLOW_varargslist_in_parameters597 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_parameters621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fpdef_in_defparameter639 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_defparameter642 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_defparameter644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_defparameter_in_varargslist666 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_varargslist676 = new BitSet(new long[]{0x0000100000000200L});
    public static final BitSet FOLLOW_defparameter_in_varargslist678 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_varargslist689 = new BitSet(new long[]{0x0006000000000002L});
    public static final BitSet FOLLOW_STAR_in_varargslist702 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_varargslist704 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_varargslist707 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_varargslist709 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_varargslist711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_varargslist727 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_varargslist729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_varargslist759 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_varargslist761 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_varargslist764 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_varargslist766 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_varargslist768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_varargslist778 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_varargslist780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_fpdef798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_fpdef816 = new BitSet(new long[]{0x0000100000000200L});
    public static final BitSet FOLLOW_fplist_in_fpdef818 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_fpdef820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_fpdef828 = new BitSet(new long[]{0x0000100000000200L});
    public static final BitSet FOLLOW_fplist_in_fpdef830 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_fpdef832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fpdef_in_fplist850 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_fplist866 = new BitSet(new long[]{0x0000100000000200L});
    public static final BitSet FOLLOW_fpdef_in_fplist868 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_fplist873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_stmt_in_stmt893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_compound_stmt_in_stmt901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_small_stmt_in_simple_stmt919 = new BitSet(new long[]{0x0008000000000080L});
    public static final BitSet FOLLOW_SEMI_in_simple_stmt929 = new BitSet(new long[]{0x00001239954ACA40L,0x000060000FD63000L});
    public static final BitSet FOLLOW_small_stmt_in_simple_stmt931 = new BitSet(new long[]{0x0008000000000080L});
    public static final BitSet FOLLOW_SEMI_in_simple_stmt936 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_set_in_simple_stmt940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_stmt_in_small_stmt959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_print_stmt_in_small_stmt974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_del_stmt_in_small_stmt989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pass_stmt_in_small_stmt1004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_flow_stmt_in_small_stmt1019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_stmt_in_small_stmt1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_stmt_in_small_stmt1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exec_stmt_in_small_stmt1064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assert_stmt_in_small_stmt1079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_testlist_in_expr_stmt1114 = new BitSet(new long[]{0xFFF0000000000000L});
    public static final BitSet FOLLOW_augassign_in_expr_stmt1127 = new BitSet(new long[]{0x0000023000028000L});
    public static final BitSet FOLLOW_yield_expr_in_expr_stmt1129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_augassign_in_expr_stmt1154 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_expr_stmt1156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_testlist_in_expr_stmt1194 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_expr_stmt1218 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_expr_stmt1220 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_expr_stmt1248 = new BitSet(new long[]{0x0000023000028000L});
    public static final BitSet FOLLOW_yield_expr_in_expr_stmt1250 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_testlist_in_expr_stmt1282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_augassign0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRINT_in_print_stmt1414 = new BitSet(new long[]{0x0000100180000242L,0x000060000FD63001L});
    public static final BitSet FOLLOW_printlist_in_print_stmt1423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHTSHIFT_in_print_stmt1433 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_printlist_in_print_stmt1435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_printlist1486 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_printlist1497 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_printlist1499 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_printlist1513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_printlist1523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_del_stmt1541 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_exprlist_in_del_stmt1543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PASS_in_pass_stmt1561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_break_stmt_in_flow_stmt1579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_continue_stmt_in_flow_stmt1587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_stmt_in_flow_stmt1595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_raise_stmt_in_flow_stmt1603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_yield_stmt_in_flow_stmt1611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_break_stmt1629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTINUE_in_continue_stmt1647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_return_stmt1665 = new BitSet(new long[]{0x0000100180000242L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_return_stmt1674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_yield_expr_in_yield_stmt1708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RAISE_in_raise_stmt1726 = new BitSet(new long[]{0x0000100180000242L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_raise_stmt1729 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_raise_stmt1732 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_raise_stmt1734 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_raise_stmt1745 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_raise_stmt1747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_name_in_import_stmt1771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_from_in_import_stmt1779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_name1797 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_dotted_as_names_in_import_name1799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_import_from1818 = new BitSet(new long[]{0x0000000000000600L});
    public static final BitSet FOLLOW_DOT_in_import_from1821 = new BitSet(new long[]{0x0000000000000600L});
    public static final BitSet FOLLOW_dotted_name_in_import_from1824 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_DOT_in_import_from1828 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_IMPORT_in_import_from1832 = new BitSet(new long[]{0x0002100000000200L});
    public static final BitSet FOLLOW_STAR_in_import_from1843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_as_names_in_import_from1855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_import_from1867 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_import_as_names_in_import_from1869 = new BitSet(new long[]{0x0001200000000000L});
    public static final BitSet FOLLOW_COMMA_in_import_from1871 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_import_from1874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_as_name_in_import_as_names1902 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_import_as_names1905 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_import_as_name_in_import_as_names1907 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_NAME_in_import_as_name1927 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_AS_in_import_as_name1930 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_import_as_name1932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_dotted_as_name1953 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_AS_in_dotted_as_name1956 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_dotted_as_name1958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_as_name_in_dotted_as_names1978 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_dotted_as_names1981 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_dotted_as_name_in_dotted_as_names1983 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_NAME_in_dotted_name2003 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_DOT_in_dotted_name2006 = new BitSet(new long[]{0x000003FFFFFFFA00L});
    public static final BitSet FOLLOW_attr_in_dotted_name2008 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_GLOBAL_in_global_stmt2028 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_global_stmt2030 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_global_stmt2033 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_global_stmt2035 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_EXEC_in_exec_stmt2055 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_expr_in_exec_stmt2057 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_IN_in_exec_stmt2060 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_exec_stmt2062 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_exec_stmt2065 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_exec_stmt2067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_assert_stmt2089 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_assert_stmt2091 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_assert_stmt2094 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_assert_stmt2096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_stmt_in_compound_stmt2116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_while_stmt_in_compound_stmt2124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_for_stmt_in_compound_stmt2132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_try_stmt_in_compound_stmt2140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_with_stmt_in_compound_stmt2148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_funcdef_in_compound_stmt2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classdef_in_compound_stmt2182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decorators_in_compound_stmt2190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_if_stmt2208 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_if_stmt2210 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_if_stmt2212 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_if_stmt2214 = new BitSet(new long[]{0x0000000400100002L});
    public static final BitSet FOLLOW_elif_clause_in_if_stmt2216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_else_clause_in_elif_clause2235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELIF_in_elif_clause2243 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_elif_clause2245 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_elif_clause2247 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_elif_clause2249 = new BitSet(new long[]{0x0000000400100002L});
    public static final BitSet FOLLOW_elif_clause_in_elif_clause2260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORELSE_in_else_clause2298 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_else_clause2300 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_else_clause2302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_while_stmt2320 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_while_stmt2322 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_while_stmt2324 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_while_stmt2326 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_ORELSE_in_while_stmt2329 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_while_stmt2331 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_while_stmt2333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_stmt2353 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_exprlist_in_for_stmt2355 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_IN_in_for_stmt2357 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_for_stmt2359 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_for_stmt2361 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_for_stmt2363 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_ORELSE_in_for_stmt2374 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_for_stmt2376 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_for_stmt2378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_try_stmt2402 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_try_stmt2404 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_try_stmt2406 = new BitSet(new long[]{0x0000000000A00002L});
    public static final BitSet FOLLOW_except_clause_in_try_stmt2416 = new BitSet(new long[]{0x0000000400A00002L});
    public static final BitSet FOLLOW_ORELSE_in_try_stmt2420 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_try_stmt2422 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_try_stmt2424 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_FINALLY_in_try_stmt2429 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_try_stmt2431 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_try_stmt2433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_try_stmt2445 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_try_stmt2447 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_try_stmt2449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WITH_in_with_stmt2478 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_with_item_in_with_stmt2480 = new BitSet(new long[]{0x0001400000000000L});
    public static final BitSet FOLLOW_COMMA_in_with_stmt2490 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_with_item_in_with_stmt2492 = new BitSet(new long[]{0x0001400000000000L});
    public static final BitSet FOLLOW_COLON_in_with_stmt2496 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_with_stmt2498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_with_item2516 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_AS_in_with_item2519 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_expr_in_with_item2521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCEPT_in_except_clause2541 = new BitSet(new long[]{0x0000500180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_except_clause2544 = new BitSet(new long[]{0x0001400000002000L});
    public static final BitSet FOLLOW_set_in_except_clause2547 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_except_clause2555 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_except_clause2561 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_except_clause2563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_stmt_in_suite2581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_in_suite2589 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_EOF_in_suite2592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEDENT_in_suite2611 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_EOF_in_suite2615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDENT_in_suite2633 = new BitSet(new long[]{0x00001BF99F4FCA40L,0x000060000FD63000L});
    public static final BitSet FOLLOW_stmt_in_suite2636 = new BitSet(new long[]{0x00001BF99F4FCA60L,0x000060000FD63000L});
    public static final BitSet FOLLOW_set_in_suite2640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_test_in_test2741 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IF_in_test2761 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_or_test_in_test2763 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_ORELSE_in_test2765 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_test2767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lambdef_in_test2791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_test_in_or_test2809 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_OR_in_or_test2822 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_and_test_in_or_test2824 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_not_test_in_and_test2875 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_AND_in_and_test2888 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_not_test_in_and_test2890 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_NOT_in_not_test2941 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_not_test_in_not_test2943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparison_in_not_test2951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_comparison2969 = new BitSet(new long[]{0x0000000160000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_comp_op_in_comparison2982 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_expr_in_comparison2984 = new BitSet(new long[]{0x0000000160000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_LESS_in_comp_op3032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_comp_op3040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_comp_op3048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATEREQUAL_in_comp_op3056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSEQUAL_in_comp_op3064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_NOTEQUAL_in_comp_op3072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOTEQUAL_in_comp_op3080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_comp_op3088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_comp_op3096 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_IN_in_comp_op3098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_comp_op3106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_comp_op3114 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_NOT_in_comp_op3116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_xor_expr_in_expr3134 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_VBAR_in_expr3147 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_xor_expr_in_expr3149 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_and_expr_in_xor_expr3200 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_CIRCUMFLEX_in_xor_expr3213 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_and_expr_in_xor_expr3215 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_shift_expr_in_and_expr3266 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_AMPER_in_and_expr3279 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_shift_expr_in_and_expr3281 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_arith_expr_in_shift_expr3332 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000801L});
    public static final BitSet FOLLOW_shift_op_in_shift_expr3346 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_arith_expr_in_shift_expr3348 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000801L});
    public static final BitSet FOLLOW_set_in_shift_op0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_term_in_arith_expr3424 = new BitSet(new long[]{0x0000000000000002L,0x0000000000003000L});
    public static final BitSet FOLLOW_arith_op_in_arith_expr3437 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_term_in_arith_expr3439 = new BitSet(new long[]{0x0000000000000002L,0x0000000000003000L});
    public static final BitSet FOLLOW_set_in_arith_op0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_factor_in_term3515 = new BitSet(new long[]{0x0002000000000002L,0x000000000001C000L});
    public static final BitSet FOLLOW_term_op_in_term3528 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_factor_in_term3530 = new BitSet(new long[]{0x0002000000000002L,0x000000000001C000L});
    public static final BitSet FOLLOW_set_in_term_op0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_factor3618 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_factor_in_factor3620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_factor3628 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_factor_in_factor3630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_factor3638 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_factor_in_factor3640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_in_factor3648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILBACKSLASH_in_factor3656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_power3674 = new BitSet(new long[]{0x0004100000000402L,0x0000000000040000L});
    public static final BitSet FOLLOW_trailer_in_power3677 = new BitSet(new long[]{0x0004100000000402L,0x0000000000040000L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_power3689 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_factor_in_power3691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_atom3715 = new BitSet(new long[]{0x0000323180028240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_yield_expr_in_atom3725 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_testlist_gexp_in_atom3735 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_atom3759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACK_in_atom3767 = new BitSet(new long[]{0x0000100180000240L,0x000060000FDE3000L});
    public static final BitSet FOLLOW_listmaker_in_atom3776 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RBRACK_in_atom3800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_atom3808 = new BitSet(new long[]{0x0000100180000240L,0x000060000FF63000L});
    public static final BitSet FOLLOW_dictorsetmaker_in_atom3818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_RCURLY_in_atom3845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BACKQUOTE_in_atom3854 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_atom3856 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_BACKQUOTE_in_atom3858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_atom3867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_atom3876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONGINT_in_atom3885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_atom3894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMPLEX_in_atom3903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_atom3913 = new BitSet(new long[]{0x0000000000000002L,0x0000000008000000L});
    public static final BitSet FOLLOW_TRISTRINGPART_in_atom3924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRINGPART_in_atom3933 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_TRAILBACKSLASH_in_atom3935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_listmaker3954 = new BitSet(new long[]{0x0001000002000002L});
    public static final BitSet FOLLOW_list_for_in_listmaker3965 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_listmaker3985 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_listmaker3987 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_listmaker4002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_testlist_gexp4022 = new BitSet(new long[]{0x0001000002000002L});
    public static final BitSet FOLLOW_COMMA_in_testlist_gexp4044 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_testlist_gexp4046 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_testlist_gexp4051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_for_in_testlist_gexp4078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LAMBDA_in_lambdef4118 = new BitSet(new long[]{0x0006500000000200L});
    public static final BitSet FOLLOW_varargslist_in_lambdef4121 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_lambdef4125 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_lambdef4127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_trailer4145 = new BitSet(new long[]{0x0006300180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_arglist_in_trailer4156 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_trailer4184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACK_in_trailer4192 = new BitSet(new long[]{0x0000500180000640L,0x000060000FD63000L});
    public static final BitSet FOLLOW_subscriptlist_in_trailer4194 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RBRACK_in_trailer4196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_trailer4204 = new BitSet(new long[]{0x000003FFFFFFFA00L});
    public static final BitSet FOLLOW_attr_in_trailer4206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subscript_in_subscriptlist4224 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_subscriptlist4234 = new BitSet(new long[]{0x0000500180000640L,0x000060000FD63000L});
    public static final BitSet FOLLOW_subscript_in_subscriptlist4236 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_subscriptlist4241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_subscript4261 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_DOT_in_subscript4263 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_DOT_in_subscript4265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_subscript4284 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_COLON_in_subscript4287 = new BitSet(new long[]{0x0000500180000242L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_subscript4290 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_sliceop_in_subscript4295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_subscript4316 = new BitSet(new long[]{0x0000500180000242L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_subscript4319 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_sliceop_in_subscript4324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_subscript4334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_sliceop4352 = new BitSet(new long[]{0x0000100180000242L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_sliceop4360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_exprlist4400 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_exprlist4411 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_expr_in_exprlist4413 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_exprlist4418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_exprlist4428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_del_list4447 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_del_list4458 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_expr_in_del_list4460 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_del_list4465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_testlist4496 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_testlist4507 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_testlist4509 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_testlist4514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_testlist4524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_dictorsetmaker4543 = new BitSet(new long[]{0x0001400002000002L});
    public static final BitSet FOLLOW_COLON_in_dictorsetmaker4570 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_dictorsetmaker4572 = new BitSet(new long[]{0x0001000002000000L});
    public static final BitSet FOLLOW_comp_for_in_dictorsetmaker4591 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_dictorsetmaker4618 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_dictorsetmaker4620 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_dictorsetmaker4622 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_dictorsetmaker4624 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_dictorsetmaker4660 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_dictorsetmaker4662 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_dictorsetmaker4695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_for_in_dictorsetmaker4710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decorators_in_classdef4739 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_CLASS_in_classdef4742 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NAME_in_classdef4744 = new BitSet(new long[]{0x0000500000000000L});
    public static final BitSet FOLLOW_LPAREN_in_classdef4747 = new BitSet(new long[]{0x0000300180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_classdef4749 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAREN_in_classdef4752 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_classdef4756 = new BitSet(new long[]{0x00001239954ACAC0L,0x000060000FD63000L});
    public static final BitSet FOLLOW_suite_in_classdef4758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argument_in_arglist4778 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_arglist4781 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_argument_in_arglist4783 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_arglist4798 = new BitSet(new long[]{0x0006000000000002L});
    public static final BitSet FOLLOW_STAR_in_arglist4816 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_arglist4818 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_arglist4821 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_argument_in_arglist4823 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_arglist4828 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_arglist4830 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_arglist4832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_arglist4852 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_arglist4854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_arglist4892 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_arglist4894 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_arglist4897 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_argument_in_arglist4899 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_arglist4904 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_arglist4906 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_arglist4908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLESTAR_in_arglist4918 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_arglist4920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_argument4938 = new BitSet(new long[]{0x0001800002000000L});
    public static final BitSet FOLLOW_ASSIGN_in_argument4950 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_argument4952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_for_in_argument4965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_for_in_list_iter5003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_if_in_list_iter5011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_list_for5029 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_exprlist_in_list_for5031 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_IN_in_list_for5033 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_list_for5035 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_list_iter_in_list_for5038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_list_if5058 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_list_if5060 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_list_iter_in_list_if5063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_for_in_comp_iter5083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_if_in_comp_iter5091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_comp_for5109 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_exprlist_in_comp_for5111 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_IN_in_comp_for5113 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_or_test_in_comp_for5115 = new BitSet(new long[]{0x000100000A000002L});
    public static final BitSet FOLLOW_comp_iter_in_comp_for5117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_comp_if5136 = new BitSet(new long[]{0x0000100180000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_test_in_comp_if5138 = new BitSet(new long[]{0x000100000A000002L});
    public static final BitSet FOLLOW_comp_iter_in_comp_if5140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_YIELD_in_yield_expr5159 = new BitSet(new long[]{0x0000100180000242L,0x000060000FD63000L});
    public static final BitSet FOLLOW_testlist_in_yield_expr5161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_synpred1_PythonPartial807 = new BitSet(new long[]{0x0000100000000200L});
    public static final BitSet FOLLOW_fpdef_in_synpred1_PythonPartial809 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_COMMA_in_synpred1_PythonPartial811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_testlist_in_synpred2_PythonPartial1107 = new BitSet(new long[]{0xFFF0000000000000L});
    public static final BitSet FOLLOW_augassign_in_synpred2_PythonPartial1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_testlist_in_synpred3_PythonPartial1187 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_synpred3_PythonPartial1189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_synpred4_PythonPartial1472 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_COMMA_in_synpred4_PythonPartial1474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decorators_in_synpred5_PythonPartial2157 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_DEF_in_synpred5_PythonPartial2160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decorators_in_synpred6_PythonPartial2174 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_CLASS_in_synpred6_PythonPartial2177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_synpred7_PythonPartial2752 = new BitSet(new long[]{0x0000100100000240L,0x000060000FD63000L});
    public static final BitSet FOLLOW_or_test_in_synpred7_PythonPartial2754 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_ORELSE_in_synpred7_PythonPartial2756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_synpred8_PythonPartial4274 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_COLON_in_synpred8_PythonPartial4276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred9_PythonPartial4308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_synpred10_PythonPartial4393 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_COMMA_in_synpred10_PythonPartial4395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_test_in_synpred11_PythonPartial4486 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_COMMA_in_synpred11_PythonPartial4488 = new BitSet(new long[]{0x0000000000000002L});

}