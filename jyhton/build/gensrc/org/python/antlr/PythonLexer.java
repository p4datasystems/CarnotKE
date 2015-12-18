// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g 2015-12-17 10:41:59

package org.python.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class PythonLexer extends Lexer {
    public static final int PRINT=11;
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

    /** Handles context-sensitive lexing of implicit line joining such as
     *  the case where newline is ignored in cases like this:
     *  a = [3,
     *       4]
     */
    int implicitLineJoiningLevel = 0;
    int startPos=-1;

    //For use in partial parsing.
    public boolean eofWhileNested = false;
    public boolean partial = false;
    public boolean single = false;

    //If you want to use another error recovery mechanism change this
    //and the same one in the parser.
    private ErrorHandler errorHandler;

        public void setErrorHandler(ErrorHandler eh) {
            this.errorHandler = eh;
        }

        /**
         *  Taken directly from antlr's Lexer.java -- needs to be re-integrated every time
         *  we upgrade from Antlr (need to consider a Lexer subclass, though the issue would
         *  remain).
         */
        public Token nextToken() {
            startPos = getCharPositionInLine();
            while (true) {
                state.token = null;
                state.channel = Token.DEFAULT_CHANNEL;
                state.tokenStartCharIndex = input.index();
                state.tokenStartCharPositionInLine = input.getCharPositionInLine();
                state.tokenStartLine = input.getLine();
                state.text = null;
                if ( input.LA(1)==CharStream.EOF ) {
                    if (implicitLineJoiningLevel > 0) {
                        eofWhileNested = true;
                    }
                    return Token.EOF_TOKEN;
                }
                try {
                    mTokens();
                    if ( state.token==null ) {
                        emit();
                    }
                    else if ( state.token==Token.SKIP_TOKEN ) {
                        continue;
                    }
                    return state.token;
                } catch (NoViableAltException nva) {
                    reportError(nva);
                    errorHandler.recover(this, nva); // throw out current char and try again
                } catch (FailedPredicateException fp) {
                    //XXX: added this for failed STRINGPART -- the FailedPredicateException
                    //     hides a NoViableAltException.  This should be the only
                    //     FailedPredicateException that gets thrown by the lexer.
                    reportError(fp);
                    errorHandler.recover(this, fp); // throw out current char and try again
                } catch (RecognitionException re) {
                    reportError(re);
                    // match() routine has already called recover()
                }
            }
        }
        @Override
        public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
            //Do nothing. We will handle error display elsewhere.
        }



    // delegates
    // delegators

    public PythonLexer() {;} 
    public PythonLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public PythonLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g"; }

    // $ANTLR start "AS"
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2421:11: ( 'as' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2421:13: 'as'
            {
            match("as"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AS"

    // $ANTLR start "ASSERT"
    public final void mASSERT() throws RecognitionException {
        try {
            int _type = ASSERT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2422:11: ( 'assert' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2422:13: 'assert'
            {
            match("assert"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASSERT"

    // $ANTLR start "BREAK"
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2423:11: ( 'break' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2423:13: 'break'
            {
            match("break"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BREAK"

    // $ANTLR start "CLASS"
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2424:11: ( 'class' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2424:13: 'class'
            {
            match("class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLASS"

    // $ANTLR start "PERSISTIT"
    public final void mPERSISTIT() throws RecognitionException {
        try {
            int _type = PERSISTIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2425:13: ( 'persistit' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2425:15: 'persistit'
            {
            match("persistit"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PERSISTIT"

    // $ANTLR start "CONTINUE"
    public final void mCONTINUE() throws RecognitionException {
        try {
            int _type = CONTINUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2426:11: ( 'continue' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2426:13: 'continue'
            {
            match("continue"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONTINUE"

    // $ANTLR start "DEF"
    public final void mDEF() throws RecognitionException {
        try {
            int _type = DEF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2427:11: ( 'def' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2427:13: 'def'
            {
            match("def"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEF"

    // $ANTLR start "DELETE"
    public final void mDELETE() throws RecognitionException {
        try {
            int _type = DELETE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2428:11: ( 'del' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2428:13: 'del'
            {
            match("del"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DELETE"

    // $ANTLR start "ELIF"
    public final void mELIF() throws RecognitionException {
        try {
            int _type = ELIF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2429:11: ( 'elif' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2429:13: 'elif'
            {
            match("elif"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELIF"

    // $ANTLR start "EXCEPT"
    public final void mEXCEPT() throws RecognitionException {
        try {
            int _type = EXCEPT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2430:11: ( 'except' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2430:13: 'except'
            {
            match("except"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXCEPT"

    // $ANTLR start "EXEC"
    public final void mEXEC() throws RecognitionException {
        try {
            int _type = EXEC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2431:11: ( 'exec' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2431:13: 'exec'
            {
            match("exec"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXEC"

    // $ANTLR start "FINALLY"
    public final void mFINALLY() throws RecognitionException {
        try {
            int _type = FINALLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2432:11: ( 'finally' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2432:13: 'finally'
            {
            match("finally"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FINALLY"

    // $ANTLR start "FROM"
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2433:11: ( 'from' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2433:13: 'from'
            {
            match("from"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FROM"

    // $ANTLR start "FOR"
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2434:11: ( 'for' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2434:13: 'for'
            {
            match("for"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FOR"

    // $ANTLR start "GLOBAL"
    public final void mGLOBAL() throws RecognitionException {
        try {
            int _type = GLOBAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2435:11: ( 'global' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2435:13: 'global'
            {
            match("global"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GLOBAL"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2436:11: ( 'if' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2436:13: 'if'
            {
            match("if"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "IMPORT"
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2437:11: ( 'import' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2437:13: 'import'
            {
            match("import"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IMPORT"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2438:11: ( 'in' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2438:13: 'in'
            {
            match("in"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "IS"
    public final void mIS() throws RecognitionException {
        try {
            int _type = IS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2439:11: ( 'is' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2439:13: 'is'
            {
            match("is"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IS"

    // $ANTLR start "LAMBDA"
    public final void mLAMBDA() throws RecognitionException {
        try {
            int _type = LAMBDA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2440:11: ( 'lambda' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2440:13: 'lambda'
            {
            match("lambda"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LAMBDA"

    // $ANTLR start "ORELSE"
    public final void mORELSE() throws RecognitionException {
        try {
            int _type = ORELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2441:11: ( 'else' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2441:13: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ORELSE"

    // $ANTLR start "PASS"
    public final void mPASS() throws RecognitionException {
        try {
            int _type = PASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2442:11: ( 'pass' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2442:13: 'pass'
            {
            match("pass"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PASS"

    // $ANTLR start "PRINT"
    public final void mPRINT() throws RecognitionException {
        try {
            int _type = PRINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2443:11: ( 'print' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2443:13: 'print'
            {
            match("print"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PRINT"

    // $ANTLR start "RAISE"
    public final void mRAISE() throws RecognitionException {
        try {
            int _type = RAISE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2444:11: ( 'raise' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2444:13: 'raise'
            {
            match("raise"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RAISE"

    // $ANTLR start "RETURN"
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2445:11: ( 'return' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2445:13: 'return'
            {
            match("return"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RETURN"

    // $ANTLR start "TRY"
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2446:11: ( 'try' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2446:13: 'try'
            {
            match("try"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRY"

    // $ANTLR start "WHILE"
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2447:11: ( 'while' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2447:13: 'while'
            {
            match("while"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHILE"

    // $ANTLR start "WITH"
    public final void mWITH() throws RecognitionException {
        try {
            int _type = WITH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2448:11: ( 'with' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2448:13: 'with'
            {
            match("with"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WITH"

    // $ANTLR start "YIELD"
    public final void mYIELD() throws RecognitionException {
        try {
            int _type = YIELD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2449:11: ( 'yield' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2449:13: 'yield'
            {
            match("yield"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "YIELD"

    // $ANTLR start "BATCH"
    public final void mBATCH() throws RecognitionException {
        try {
            int _type = BATCH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2450:11: ( 'mybatches' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2450:13: 'mybatches'
            {
            match("mybatches"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BATCH"

    // $ANTLR start "SQL"
    public final void mSQL() throws RecognitionException {
        try {
            int _type = SQL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2453:9: ( 'SQL' ( ' ' )+ 'on' ( ' ' )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2453:11: 'SQL' ( ' ' )+ 'on' ( ' ' )+
            {
            match("SQL"); 

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2453:17: ( ' ' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==' ') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2453:18: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            match("on"); 

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2453:29: ( ' ' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2453:30: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SQL"

    // $ANTLR start "SIM"
    public final void mSIM() throws RecognitionException {
        try {
            int _type = SIM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2454:9: ( 'SIM' ( ' ' )+ 'on' ( ' ' )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2454:11: 'SIM' ( ' ' )+ 'on' ( ' ' )+
            {
            match("SIM"); 

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2454:17: ( ' ' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==' ') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2454:18: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            match("on"); 

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2454:29: ( ' ' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==' ') ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2454:30: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SIM"

    // $ANTLR start "Neo4j"
    public final void mNeo4j() throws RecognitionException {
        try {
            int _type = Neo4j;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2455:9: ( 'Neo4j' ( ' ' )+ 'on' ( ' ' )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2455:11: 'Neo4j' ( ' ' )+ 'on' ( ' ' )+
            {
            match("Neo4j"); 

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2455:19: ( ' ' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==' ') ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2455:20: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);

            match("on"); 

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2455:31: ( ' ' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==' ') ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2455:32: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Neo4j"

    // $ANTLR start "OORELINSERT"
    public final void mOORELINSERT() throws RecognitionException {
        try {
            int _type = OORELINSERT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2456:12: ( 'relInsert' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2456:14: 'relInsert'
            {
            match("relInsert"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OORELINSERT"

    // $ANTLR start "OORELCOMMIT"
    public final void mOORELCOMMIT() throws RecognitionException {
        try {
            int _type = OORELCOMMIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2457:12: ( 'relCommit' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2457:14: 'relCommit'
            {
            match("relCommit"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OORELCOMMIT"

    // $ANTLR start "CONNECTTO"
    public final void mCONNECTTO() throws RecognitionException {
        try {
            int _type = CONNECTTO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2458:10: ( 'connectTo' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2458:13: 'connectTo'
            {
            match("connectTo"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONNECTTO"

    // $ANTLR start "DODEBUG"
    public final void mDODEBUG() throws RecognitionException {
        try {
            int _type = DODEBUG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2459:8: ( 'dodebug' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2459:11: 'dodebug'
            {
            match("dodebug"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DODEBUG"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2461:11: ( '(' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2461:13: '('
            {
            match('('); 
            implicitLineJoiningLevel++;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2463:11: ( ')' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2463:13: ')'
            {
            match(')'); 
            implicitLineJoiningLevel--;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "LBRACK"
    public final void mLBRACK() throws RecognitionException {
        try {
            int _type = LBRACK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2465:11: ( '[' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2465:13: '['
            {
            match('['); 
            implicitLineJoiningLevel++;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACK"

    // $ANTLR start "RBRACK"
    public final void mRBRACK() throws RecognitionException {
        try {
            int _type = RBRACK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2467:11: ( ']' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2467:13: ']'
            {
            match(']'); 
            implicitLineJoiningLevel--;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACK"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2469:11: ( ':' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2469:13: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2471:10: ( ',' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2471:12: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2473:9: ( ';' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2473:11: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMI"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2475:9: ( '+' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2475:11: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2477:10: ( '-' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2477:12: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2479:9: ( '*' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2479:11: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "SLASH"
    public final void mSLASH() throws RecognitionException {
        try {
            int _type = SLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2481:10: ( '/' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2481:12: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SLASH"

    // $ANTLR start "VBAR"
    public final void mVBAR() throws RecognitionException {
        try {
            int _type = VBAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2483:9: ( '|' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2483:11: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VBAR"

    // $ANTLR start "AMPER"
    public final void mAMPER() throws RecognitionException {
        try {
            int _type = AMPER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2485:10: ( '&' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2485:12: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMPER"

    // $ANTLR start "LESS"
    public final void mLESS() throws RecognitionException {
        try {
            int _type = LESS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2487:9: ( '<' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2487:11: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS"

    // $ANTLR start "GREATER"
    public final void mGREATER() throws RecognitionException {
        try {
            int _type = GREATER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2489:12: ( '>' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2489:14: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER"

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2491:11: ( '=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2491:13: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASSIGN"

    // $ANTLR start "PERCENT"
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2493:12: ( '%' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2493:14: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PERCENT"

    // $ANTLR start "BACKQUOTE"
    public final void mBACKQUOTE() throws RecognitionException {
        try {
            int _type = BACKQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2495:14: ( '`' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2495:16: '`'
            {
            match('`'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BACKQUOTE"

    // $ANTLR start "LCURLY"
    public final void mLCURLY() throws RecognitionException {
        try {
            int _type = LCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2497:11: ( '{' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2497:13: '{'
            {
            match('{'); 
            implicitLineJoiningLevel++;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LCURLY"

    // $ANTLR start "RCURLY"
    public final void mRCURLY() throws RecognitionException {
        try {
            int _type = RCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2499:11: ( '}' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2499:13: '}'
            {
            match('}'); 
            implicitLineJoiningLevel--;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RCURLY"

    // $ANTLR start "CIRCUMFLEX"
    public final void mCIRCUMFLEX() throws RecognitionException {
        try {
            int _type = CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2501:15: ( '^' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2501:17: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CIRCUMFLEX"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2503:10: ( '~' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2503:12: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "EQUAL"
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2505:10: ( '==' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2505:12: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUAL"

    // $ANTLR start "NOTEQUAL"
    public final void mNOTEQUAL() throws RecognitionException {
        try {
            int _type = NOTEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2507:13: ( '!=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2507:15: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOTEQUAL"

    // $ANTLR start "ALT_NOTEQUAL"
    public final void mALT_NOTEQUAL() throws RecognitionException {
        try {
            int _type = ALT_NOTEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2509:13: ( '<>' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2509:15: '<>'
            {
            match("<>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ALT_NOTEQUAL"

    // $ANTLR start "LESSEQUAL"
    public final void mLESSEQUAL() throws RecognitionException {
        try {
            int _type = LESSEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2511:14: ( '<=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2511:16: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESSEQUAL"

    // $ANTLR start "LEFTSHIFT"
    public final void mLEFTSHIFT() throws RecognitionException {
        try {
            int _type = LEFTSHIFT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2513:14: ( '<<' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2513:16: '<<'
            {
            match("<<"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFTSHIFT"

    // $ANTLR start "GREATEREQUAL"
    public final void mGREATEREQUAL() throws RecognitionException {
        try {
            int _type = GREATEREQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2515:17: ( '>=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2515:19: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATEREQUAL"

    // $ANTLR start "RIGHTSHIFT"
    public final void mRIGHTSHIFT() throws RecognitionException {
        try {
            int _type = RIGHTSHIFT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2517:15: ( '>>' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2517:17: '>>'
            {
            match(">>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHTSHIFT"

    // $ANTLR start "PLUSEQUAL"
    public final void mPLUSEQUAL() throws RecognitionException {
        try {
            int _type = PLUSEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2519:14: ( '+=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2519:16: '+='
            {
            match("+="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUSEQUAL"

    // $ANTLR start "MINUSEQUAL"
    public final void mMINUSEQUAL() throws RecognitionException {
        try {
            int _type = MINUSEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2521:15: ( '-=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2521:17: '-='
            {
            match("-="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUSEQUAL"

    // $ANTLR start "DOUBLESTAR"
    public final void mDOUBLESTAR() throws RecognitionException {
        try {
            int _type = DOUBLESTAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2523:15: ( '**' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2523:17: '**'
            {
            match("**"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLESTAR"

    // $ANTLR start "STAREQUAL"
    public final void mSTAREQUAL() throws RecognitionException {
        try {
            int _type = STAREQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2525:14: ( '*=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2525:16: '*='
            {
            match("*="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STAREQUAL"

    // $ANTLR start "DOUBLESLASH"
    public final void mDOUBLESLASH() throws RecognitionException {
        try {
            int _type = DOUBLESLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2527:16: ( '//' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2527:18: '//'
            {
            match("//"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLESLASH"

    // $ANTLR start "SLASHEQUAL"
    public final void mSLASHEQUAL() throws RecognitionException {
        try {
            int _type = SLASHEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2529:15: ( '/=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2529:17: '/='
            {
            match("/="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SLASHEQUAL"

    // $ANTLR start "VBAREQUAL"
    public final void mVBAREQUAL() throws RecognitionException {
        try {
            int _type = VBAREQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2531:14: ( '|=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2531:16: '|='
            {
            match("|="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VBAREQUAL"

    // $ANTLR start "PERCENTEQUAL"
    public final void mPERCENTEQUAL() throws RecognitionException {
        try {
            int _type = PERCENTEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2533:17: ( '%=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2533:19: '%='
            {
            match("%="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PERCENTEQUAL"

    // $ANTLR start "AMPEREQUAL"
    public final void mAMPEREQUAL() throws RecognitionException {
        try {
            int _type = AMPEREQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2535:15: ( '&=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2535:17: '&='
            {
            match("&="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMPEREQUAL"

    // $ANTLR start "CIRCUMFLEXEQUAL"
    public final void mCIRCUMFLEXEQUAL() throws RecognitionException {
        try {
            int _type = CIRCUMFLEXEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2537:20: ( '^=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2537:22: '^='
            {
            match("^="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CIRCUMFLEXEQUAL"

    // $ANTLR start "LEFTSHIFTEQUAL"
    public final void mLEFTSHIFTEQUAL() throws RecognitionException {
        try {
            int _type = LEFTSHIFTEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2539:19: ( '<<=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2539:21: '<<='
            {
            match("<<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFTSHIFTEQUAL"

    // $ANTLR start "RIGHTSHIFTEQUAL"
    public final void mRIGHTSHIFTEQUAL() throws RecognitionException {
        try {
            int _type = RIGHTSHIFTEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2541:20: ( '>>=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2541:22: '>>='
            {
            match(">>="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHTSHIFTEQUAL"

    // $ANTLR start "DOUBLESTAREQUAL"
    public final void mDOUBLESTAREQUAL() throws RecognitionException {
        try {
            int _type = DOUBLESTAREQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2543:20: ( '**=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2543:22: '**='
            {
            match("**="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLESTAREQUAL"

    // $ANTLR start "DOUBLESLASHEQUAL"
    public final void mDOUBLESLASHEQUAL() throws RecognitionException {
        try {
            int _type = DOUBLESLASHEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2545:21: ( '//=' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2545:23: '//='
            {
            match("//="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLESLASHEQUAL"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2547:5: ( '.' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2547:7: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2549:4: ( '@' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2549:6: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2551:5: ( 'and' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2551:7: 'and'
            {
            match("and"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2553:4: ( 'or' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2553:6: 'or'
            {
            match("or"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2555:5: ( 'not' )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2555:7: 'not'
            {
            match("not"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2558:5: ( '.' DIGITS ( Exponent )? | DIGITS '.' Exponent | DIGITS ( '.' ( DIGITS ( Exponent )? )? | Exponent ) )
            int alt11=3;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2558:9: '.' DIGITS ( Exponent )?
                    {
                    match('.'); 
                    mDIGITS(); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2558:20: ( Exponent )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='E'||LA7_0=='e') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2558:21: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2559:9: DIGITS '.' Exponent
                    {
                    mDIGITS(); 
                    match('.'); 
                    mExponent(); 

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:9: DIGITS ( '.' ( DIGITS ( Exponent )? )? | Exponent )
                    {
                    mDIGITS(); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:16: ( '.' ( DIGITS ( Exponent )? )? | Exponent )
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='.') ) {
                        alt10=1;
                    }
                    else if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 0, input);

                        throw nvae;
                    }
                    switch (alt10) {
                        case 1 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:17: '.' ( DIGITS ( Exponent )? )?
                            {
                            match('.'); 
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:21: ( DIGITS ( Exponent )? )?
                            int alt9=2;
                            int LA9_0 = input.LA(1);

                            if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                                alt9=1;
                            }
                            switch (alt9) {
                                case 1 :
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:22: DIGITS ( Exponent )?
                                    {
                                    mDIGITS(); 
                                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:29: ( Exponent )?
                                    int alt8=2;
                                    int LA8_0 = input.LA(1);

                                    if ( (LA8_0=='E'||LA8_0=='e') ) {
                                        alt8=1;
                                    }
                                    switch (alt8) {
                                        case 1 :
                                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:30: Exponent
                                            {
                                            mExponent(); 

                                            }
                                            break;

                                    }


                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2560:45: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "LONGINT"
    public final void mLONGINT() throws RecognitionException {
        try {
            int _type = LONGINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2564:5: ( INT ( 'l' | 'L' ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2564:9: INT ( 'l' | 'L' )
            {
            mINT(); 
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LONGINT"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2569:5: ( ( 'e' | 'E' ) ( '+' | '-' )? DIGITS )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2569:10: ( 'e' | 'E' ) ( '+' | '-' )? DIGITS
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2569:22: ( '+' | '-' )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='+'||LA12_0=='-') ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            mDIGITS(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "Exponent"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2572:5: ( '0' ( 'x' | 'X' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ | '0' ( 'o' | 'O' ) ( '0' .. '7' )* | '0' ( '0' .. '7' )* | '0' ( 'b' | 'B' ) ( '0' .. '1' )* | '1' .. '9' ( DIGITS )* )
            int alt18=5;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='0') ) {
                switch ( input.LA(2) ) {
                case 'X':
                case 'x':
                    {
                    alt18=1;
                    }
                    break;
                case 'O':
                case 'o':
                    {
                    alt18=2;
                    }
                    break;
                case 'B':
                case 'b':
                    {
                    alt18=4;
                    }
                    break;
                default:
                    alt18=3;}

            }
            else if ( ((LA18_0>='1' && LA18_0<='9')) ) {
                alt18=5;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2573:9: '0' ( 'x' | 'X' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
                    {
                    match('0'); 
                    if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2573:25: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
                    int cnt13=0;
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>='0' && LA13_0<='9')||(LA13_0>='A' && LA13_0<='F')||(LA13_0>='a' && LA13_0<='f')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:
                    	    {
                    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt13 >= 1 ) break loop13;
                                EarlyExitException eee =
                                    new EarlyExitException(13, input);
                                throw eee;
                        }
                        cnt13++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2575:9: '0' ( 'o' | 'O' ) ( '0' .. '7' )*
                    {
                    match('0'); 
                    if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2575:25: ( '0' .. '7' )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>='0' && LA14_0<='7')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2575:27: '0' .. '7'
                    	    {
                    	    matchRange('0','7'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2576:9: '0' ( '0' .. '7' )*
                    {
                    match('0'); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2576:14: ( '0' .. '7' )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>='0' && LA15_0<='7')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2576:16: '0' .. '7'
                    	    {
                    	    matchRange('0','7'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2578:9: '0' ( 'b' | 'B' ) ( '0' .. '1' )*
                    {
                    match('0'); 
                    if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2578:25: ( '0' .. '1' )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( ((LA16_0>='0' && LA16_0<='1')) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2578:27: '0' .. '1'
                    	    {
                    	    matchRange('0','1'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2580:9: '1' .. '9' ( DIGITS )*
                    {
                    matchRange('1','9'); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2580:18: ( DIGITS )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2580:18: DIGITS
                    	    {
                    	    mDIGITS(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "COMPLEX"
    public final void mCOMPLEX() throws RecognitionException {
        try {
            int _type = COMPLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2584:5: ( ( DIGITS )+ ( 'j' | 'J' ) | FLOAT ( 'j' | 'J' ) )
            int alt20=2;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2584:9: ( DIGITS )+ ( 'j' | 'J' )
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2584:9: ( DIGITS )+
                    int cnt19=0;
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( ((LA19_0>='0' && LA19_0<='9')) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2584:9: DIGITS
                    	    {
                    	    mDIGITS(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt19 >= 1 ) break loop19;
                                EarlyExitException eee =
                                    new EarlyExitException(19, input);
                                throw eee;
                        }
                        cnt19++;
                    } while (true);

                    if ( input.LA(1)=='J'||input.LA(1)=='j' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2585:9: FLOAT ( 'j' | 'J' )
                    {
                    mFLOAT(); 
                    if ( input.LA(1)=='J'||input.LA(1)=='j' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMPLEX"

    // $ANTLR start "DIGITS"
    public final void mDIGITS() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2589:8: ( ( '0' .. '9' )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2589:10: ( '0' .. '9' )+
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2589:10: ( '0' .. '9' )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2589:12: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGITS"

    // $ANTLR start "NAME"
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2591:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2591:10: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2592:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( ((LA22_0>='0' && LA22_0<='9')||(LA22_0>='A' && LA22_0<='Z')||LA22_0=='_'||(LA22_0>='a' && LA22_0<='z')) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAME"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:5: ( ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )? ( '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\'' | '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"' | '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' | '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:9: ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )? ( '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\'' | '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"' | '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' | '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' )
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:9: ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )?
            int alt23=15;
            alt23 = dfa23.predict(input);
            switch (alt23) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:10: 'r'
                    {
                    match('r'); 

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:14: 'u'
                    {
                    match('u'); 

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:18: 'b'
                    {
                    match('b'); 

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:22: 'ur'
                    {
                    match("ur"); 


                    }
                    break;
                case 5 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:27: 'br'
                    {
                    match("br"); 


                    }
                    break;
                case 6 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:32: 'R'
                    {
                    match('R'); 

                    }
                    break;
                case 7 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:36: 'U'
                    {
                    match('U'); 

                    }
                    break;
                case 8 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:40: 'B'
                    {
                    match('B'); 

                    }
                    break;
                case 9 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:44: 'UR'
                    {
                    match("UR"); 


                    }
                    break;
                case 10 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:49: 'BR'
                    {
                    match("BR"); 


                    }
                    break;
                case 11 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:54: 'uR'
                    {
                    match("uR"); 


                    }
                    break;
                case 12 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:59: 'Ur'
                    {
                    match("Ur"); 


                    }
                    break;
                case 13 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:64: 'Br'
                    {
                    match("Br"); 


                    }
                    break;
                case 14 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2600:69: 'bR'
                    {
                    match("bR"); 


                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2601:9: ( '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\'' | '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"' | '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' | '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' )
            int alt28=4;
            int LA28_0 = input.LA(1);

            if ( (LA28_0=='\'') ) {
                int LA28_1 = input.LA(2);

                if ( (LA28_1=='\'') ) {
                    int LA28_3 = input.LA(3);

                    if ( (LA28_3=='\'') ) {
                        alt28=1;
                    }
                    else {
                        alt28=4;}
                }
                else if ( ((LA28_1>='\u0000' && LA28_1<='\t')||(LA28_1>='\u000B' && LA28_1<='&')||(LA28_1>='(' && LA28_1<='\uFFFF')) ) {
                    alt28=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA28_0=='\"') ) {
                int LA28_2 = input.LA(2);

                if ( (LA28_2=='\"') ) {
                    int LA28_5 = input.LA(3);

                    if ( (LA28_5=='\"') ) {
                        alt28=2;
                    }
                    else {
                        alt28=3;}
                }
                else if ( ((LA28_2>='\u0000' && LA28_2<='\t')||(LA28_2>='\u000B' && LA28_2<='!')||(LA28_2>='#' && LA28_2<='\uFFFF')) ) {
                    alt28=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2601:13: '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\''
                    {
                    match("'''"); 

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2601:22: ( options {greedy=false; } : TRIAPOS )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0=='\'') ) {
                            int LA24_1 = input.LA(2);

                            if ( (LA24_1=='\'') ) {
                                int LA24_3 = input.LA(3);

                                if ( (LA24_3=='\'') ) {
                                    alt24=2;
                                }
                                else if ( ((LA24_3>='\u0000' && LA24_3<='&')||(LA24_3>='(' && LA24_3<='\uFFFF')) ) {
                                    alt24=1;
                                }


                            }
                            else if ( ((LA24_1>='\u0000' && LA24_1<='&')||(LA24_1>='(' && LA24_1<='\uFFFF')) ) {
                                alt24=1;
                            }


                        }
                        else if ( ((LA24_0>='\u0000' && LA24_0<='&')||(LA24_0>='(' && LA24_0<='\uFFFF')) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2601:47: TRIAPOS
                    	    {
                    	    mTRIAPOS(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);

                    match("'''"); 


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2602:13: '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"'
                    {
                    match("\"\"\""); 

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2602:19: ( options {greedy=false; } : TRIQUOTE )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0=='\"') ) {
                            int LA25_1 = input.LA(2);

                            if ( (LA25_1=='\"') ) {
                                int LA25_3 = input.LA(3);

                                if ( (LA25_3=='\"') ) {
                                    alt25=2;
                                }
                                else if ( ((LA25_3>='\u0000' && LA25_3<='!')||(LA25_3>='#' && LA25_3<='\uFFFF')) ) {
                                    alt25=1;
                                }


                            }
                            else if ( ((LA25_1>='\u0000' && LA25_1<='!')||(LA25_1>='#' && LA25_1<='\uFFFF')) ) {
                                alt25=1;
                            }


                        }
                        else if ( ((LA25_0>='\u0000' && LA25_0<='!')||(LA25_0>='#' && LA25_0<='\uFFFF')) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2602:44: TRIQUOTE
                    	    {
                    	    mTRIQUOTE(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);

                    match("\"\"\""); 


                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2603:13: '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"'
                    {
                    match('\"'); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2603:17: ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )*
                    loop26:
                    do {
                        int alt26=3;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0=='\\') ) {
                            alt26=1;
                        }
                        else if ( ((LA26_0>='\u0000' && LA26_0<='\t')||(LA26_0>='\u000B' && LA26_0<='!')||(LA26_0>='#' && LA26_0<='[')||(LA26_0>=']' && LA26_0<='\uFFFF')) ) {
                            alt26=2;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2603:18: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2603:22: ~ ( '\\\\' | '\\n' | '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 4 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2604:13: '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\''
                    {
                    match('\''); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2604:18: ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )*
                    loop27:
                    do {
                        int alt27=3;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0=='\\') ) {
                            alt27=1;
                        }
                        else if ( ((LA27_0>='\u0000' && LA27_0<='\t')||(LA27_0>='\u000B' && LA27_0<='&')||(LA27_0>='(' && LA27_0<='[')||(LA27_0>=']' && LA27_0<='\uFFFF')) ) {
                            alt27=2;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2604:19: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2604:23: ~ ( '\\\\' | '\\n' | '\\'' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;

            }


                       if (state.tokenStartLine != input.getLine()) {
                           state.tokenStartLine = input.getLine();
                           state.tokenStartCharPositionInLine = -2;
                       }
                    

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "TRIQUOTE"
    public final void mTRIQUOTE() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:5: ( ( '\"' )? ( '\"' )? ( ESC | ~ ( '\\\\' | '\"' ) )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:7: ( '\"' )? ( '\"' )? ( ESC | ~ ( '\\\\' | '\"' ) )+
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:7: ( '\"' )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='\"') ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:7: '\"'
                    {
                    match('\"'); 

                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:12: ( '\"' )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0=='\"') ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:12: '\"'
                    {
                    match('\"'); 

                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:17: ( ESC | ~ ( '\\\\' | '\"' ) )+
            int cnt31=0;
            loop31:
            do {
                int alt31=3;
                int LA31_0 = input.LA(1);

                if ( (LA31_0=='\\') ) {
                    alt31=1;
                }
                else if ( ((LA31_0>='\u0000' && LA31_0<='!')||(LA31_0>='#' && LA31_0<='[')||(LA31_0>=']' && LA31_0<='\uFFFF')) ) {
                    alt31=2;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:18: ESC
            	    {
            	    mESC(); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2616:22: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt31 >= 1 ) break loop31;
                        EarlyExitException eee =
                            new EarlyExitException(31, input);
                        throw eee;
                }
                cnt31++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "TRIQUOTE"

    // $ANTLR start "TRIAPOS"
    public final void mTRIAPOS() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:5: ( ( '\\'' )? ( '\\'' )? ( ESC | ~ ( '\\\\' | '\\'' ) )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:7: ( '\\'' )? ( '\\'' )? ( ESC | ~ ( '\\\\' | '\\'' ) )+
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:7: ( '\\'' )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0=='\'') ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:7: '\\''
                    {
                    match('\''); 

                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:13: ( '\\'' )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0=='\'') ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:13: '\\''
                    {
                    match('\''); 

                    }
                    break;

            }

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:19: ( ESC | ~ ( '\\\\' | '\\'' ) )+
            int cnt34=0;
            loop34:
            do {
                int alt34=3;
                int LA34_0 = input.LA(1);

                if ( (LA34_0=='\\') ) {
                    alt34=1;
                }
                else if ( ((LA34_0>='\u0000' && LA34_0<='&')||(LA34_0>='(' && LA34_0<='[')||(LA34_0>=']' && LA34_0<='\uFFFF')) ) {
                    alt34=2;
                }


                switch (alt34) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:20: ESC
            	    {
            	    mESC(); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2622:24: ~ ( '\\\\' | '\\'' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "TRIAPOS"

    // $ANTLR start "ESC"
    public final void mESC() throws RecognitionException {
        try {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2627:5: ( '\\\\' . )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2627:10: '\\\\' .
            {
            match('\\'); 
            matchAny(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "ESC"

    // $ANTLR start "CONTINUED_LINE"
    public final void mCONTINUED_LINE() throws RecognitionException {
        try {
            int _type = CONTINUED_LINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            Token nl=null;

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2635:5: ( '\\\\' ( '\\r' )? '\\n' ( ' ' | '\\t' )* ( COMMENT | nl= NEWLINE | ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2635:10: '\\\\' ( '\\r' )? '\\n' ( ' ' | '\\t' )* ( COMMENT | nl= NEWLINE | )
            {
            match('\\'); 
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2635:15: ( '\\r' )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0=='\r') ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2635:16: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2635:28: ( ' ' | '\\t' )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0=='\t'||LA36_0==' ') ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

             _channel=HIDDEN; 
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2636:10: ( COMMENT | nl= NEWLINE | )
            int alt37=3;
            int LA37_0 = input.LA(1);

            if ( (LA37_0=='\t'||LA37_0==' ') && ((startPos==0))) {
                alt37=1;
            }
            else if ( (LA37_0=='#') ) {
                alt37=1;
            }
            else if ( (LA37_0=='\n'||(LA37_0>='\f' && LA37_0<='\r')) ) {
                alt37=2;
            }
            else {
                alt37=3;}
            switch (alt37) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2636:12: COMMENT
                    {
                    mCOMMENT(); 

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2637:12: nl= NEWLINE
                    {
                    int nlStart1934 = getCharIndex();
                    mNEWLINE(); 
                    nl = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nlStart1934, getCharIndex()-1);

                                   emit(new CommonToken(NEWLINE,nl.getText()));
                               

                    }
                    break;
                case 3 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2642:10: 
                    {
                    }
                    break;

            }


                           if (input.LA(1) == -1) {
                               throw new ParseException("unexpected character after line continuation character");
                           }
                       

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONTINUED_LINE"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;

                int newlines = 0;

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:5: ( ( ( '\\u000C' )? ( '\\r' )? '\\n' )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:9: ( ( '\\u000C' )? ( '\\r' )? '\\n' )+
            {
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:9: ( ( '\\u000C' )? ( '\\r' )? '\\n' )+
            int cnt40=0;
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0=='\n'||(LA40_0>='\f' && LA40_0<='\r')) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:10: ( '\\u000C' )? ( '\\r' )? '\\n'
            	    {
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:10: ( '\\u000C' )?
            	    int alt38=2;
            	    int LA38_0 = input.LA(1);

            	    if ( (LA38_0=='\f') ) {
            	        alt38=1;
            	    }
            	    switch (alt38) {
            	        case 1 :
            	            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:11: '\\u000C'
            	            {
            	            match('\f'); 

            	            }
            	            break;

            	    }

            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:21: ( '\\r' )?
            	    int alt39=2;
            	    int LA39_0 = input.LA(1);

            	    if ( (LA39_0=='\r') ) {
            	        alt39=1;
            	    }
            	    switch (alt39) {
            	        case 1 :
            	            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2659:22: '\\r'
            	            {
            	            match('\r'); 

            	            }
            	            break;

            	    }

            	    match('\n'); 
            	    newlines++; 

            	    }
            	    break;

            	default :
            	    if ( cnt40 >= 1 ) break loop40;
                        EarlyExitException eee =
                            new EarlyExitException(40, input);
                        throw eee;
                }
                cnt40++;
            } while (true);


                     if ( startPos==0 || implicitLineJoiningLevel>0 )
                        _channel=HIDDEN;
                    

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2665:5: ({...}? => ( ' ' | '\\t' | '\\u000C' )+ )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2665:10: {...}? => ( ' ' | '\\t' | '\\u000C' )+
            {
            if ( !((startPos>0)) ) {
                throw new FailedPredicateException(input, "WS", "startPos>0");
            }
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2665:26: ( ' ' | '\\t' | '\\u000C' )+
            int cnt41=0;
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0=='\t'||LA41_0=='\f'||LA41_0==' ') ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt41 >= 1 ) break loop41;
                        EarlyExitException eee =
                            new EarlyExitException(41, input);
                        throw eee;
                }
                cnt41++;
            } while (true);

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "LEADING_WS"
    public final void mLEADING_WS() throws RecognitionException {
        try {
            int _type = LEADING_WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;

                int spaces = 0;
                int newlines = 0;

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2679:5: ({...}? => ({...}? ( ' ' | '\\t' )+ | ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )* ) )
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2679:9: {...}? => ({...}? ( ' ' | '\\t' )+ | ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )* )
            {
            if ( !((startPos==0)) ) {
                throw new FailedPredicateException(input, "LEADING_WS", "startPos==0");
            }
            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2680:9: ({...}? ( ' ' | '\\t' )+ | ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )* )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==' ') ) {
                int LA46_1 = input.LA(2);

                if ( ((implicitLineJoiningLevel>0)) ) {
                    alt46=1;
                }
                else if ( (true) ) {
                    alt46=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 46, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA46_0=='\t') ) {
                int LA46_2 = input.LA(2);

                if ( ((implicitLineJoiningLevel>0)) ) {
                    alt46=1;
                }
                else if ( (true) ) {
                    alt46=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 46, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2680:13: {...}? ( ' ' | '\\t' )+
                    {
                    if ( !((implicitLineJoiningLevel>0)) ) {
                        throw new FailedPredicateException(input, "LEADING_WS", "implicitLineJoiningLevel>0");
                    }
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2680:43: ( ' ' | '\\t' )+
                    int cnt42=0;
                    loop42:
                    do {
                        int alt42=2;
                        int LA42_0 = input.LA(1);

                        if ( (LA42_0=='\t'||LA42_0==' ') ) {
                            alt42=1;
                        }


                        switch (alt42) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:
                    	    {
                    	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt42 >= 1 ) break loop42;
                                EarlyExitException eee =
                                    new EarlyExitException(42, input);
                                throw eee;
                        }
                        cnt42++;
                    } while (true);

                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2681:14: ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )*
                    {
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2681:14: ( ' ' | '\\t' )+
                    int cnt43=0;
                    loop43:
                    do {
                        int alt43=3;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==' ') ) {
                            alt43=1;
                        }
                        else if ( (LA43_0=='\t') ) {
                            alt43=2;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2681:20: ' '
                    	    {
                    	    match(' '); 
                    	     spaces++; 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2682:19: '\\t'
                    	    {
                    	    match('\t'); 
                    	     spaces += 8; spaces -= (spaces % 8); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt43 >= 1 ) break loop43;
                                EarlyExitException eee =
                                    new EarlyExitException(43, input);
                                throw eee;
                        }
                        cnt43++;
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2684:14: ( ( '\\r' )? '\\n' )*
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0=='\n'||LA45_0=='\r') ) {
                            alt45=1;
                        }


                        switch (alt45) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2684:16: ( '\\r' )? '\\n'
                    	    {
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2684:16: ( '\\r' )?
                    	    int alt44=2;
                    	    int LA44_0 = input.LA(1);

                    	    if ( (LA44_0=='\r') ) {
                    	        alt44=1;
                    	    }
                    	    switch (alt44) {
                    	        case 1 :
                    	            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2684:17: '\\r'
                    	            {
                    	            match('\r'); 

                    	            }
                    	            break;

                    	    }

                    	    match('\n'); 
                    	    newlines++; 

                    	    }
                    	    break;

                    	default :
                    	    break loop45;
                        }
                    } while (true);


                                       if (input.LA(1) != -1 || newlines == 0) {
                                           // make a string of n spaces where n is column number - 1
                                           char[] indentation = new char[spaces];
                                           for (int i=0; i<spaces; i++) {
                                               indentation[i] = ' ';
                                           }
                                           CommonToken c = new CommonToken(LEADING_WS,new String(indentation));
                                           c.setLine(input.getLine());
                                           c.setCharPositionInLine(input.getCharPositionInLine());
                                           c.setStartIndex(input.index() - 1);
                                           c.setStopIndex(input.index() - 1);
                                           emit(c);
                                           // kill trailing newline if present and then ignore
                                           if (newlines != 0) {
                                               if (state.token!=null) {
                                                   state.token.setChannel(HIDDEN);
                                               } else {
                                                   _channel=HIDDEN;
                                               }
                                           }
                                       } else if (this.single && newlines == 1) {
                                           // This is here for this case in interactive mode:
                                           //
                                           // def foo():
                                           //   print 1
                                           //   <spaces but no code>
                                           //
                                           // The above would complete in interactive mode instead
                                           // of giving ... to wait for more input.
                                           //
                                           throw new ParseException("Trailing space in single mode.");
                                       } else {
                                           // make a string of n newlines
                                           char[] nls = new char[newlines];
                                           for (int i=0; i<newlines; i++) {
                                               nls[i] = '\n';
                                           }
                                           CommonToken c = new CommonToken(NEWLINE,new String(nls));
                                           c.setLine(input.getLine());
                                           c.setCharPositionInLine(input.getCharPositionInLine());
                                           c.setStartIndex(input.index() - 1);
                                           c.setStopIndex(input.index() - 1);
                                           emit(c);
                                       }
                                    

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEADING_WS"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;

                _channel=HIDDEN;

            // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2755:5: ({...}? => ( ' ' | '\\t' )* '#' (~ '\\n' )* ( '\\n' )+ | '#' (~ '\\n' )* )
            int alt51=2;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2755:10: {...}? => ( ' ' | '\\t' )* '#' (~ '\\n' )* ( '\\n' )+
                    {
                    if ( !((startPos==0)) ) {
                        throw new FailedPredicateException(input, "COMMENT", "startPos==0");
                    }
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2755:27: ( ' ' | '\\t' )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0=='\t'||LA47_0==' ') ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:
                    	    {
                    	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop47;
                        }
                    } while (true);

                    match('#'); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2755:43: (~ '\\n' )*
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);

                        if ( ((LA48_0>='\u0000' && LA48_0<='\t')||(LA48_0>='\u000B' && LA48_0<='\uFFFF')) ) {
                            alt48=1;
                        }


                        switch (alt48) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2755:44: ~ '\\n'
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop48;
                        }
                    } while (true);

                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2755:52: ( '\\n' )+
                    int cnt49=0;
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0=='\n') ) {
                            alt49=1;
                        }


                        switch (alt49) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2755:52: '\\n'
                    	    {
                    	    match('\n'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt49 >= 1 ) break loop49;
                                EarlyExitException eee =
                                    new EarlyExitException(49, input);
                                throw eee;
                        }
                        cnt49++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2756:10: '#' (~ '\\n' )*
                    {
                    match('#'); 
                    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2756:14: (~ '\\n' )*
                    loop50:
                    do {
                        int alt50=2;
                        int LA50_0 = input.LA(1);

                        if ( ((LA50_0>='\u0000' && LA50_0<='\t')||(LA50_0>='\u000B' && LA50_0<='\uFFFF')) ) {
                            alt50=1;
                        }


                        switch (alt50) {
                    	case 1 :
                    	    // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:2756:15: ~ '\\n'
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop50;
                        }
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    public void mTokens() throws RecognitionException {
        // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:8: ( AS | ASSERT | BREAK | CLASS | PERSISTIT | CONTINUE | DEF | DELETE | ELIF | EXCEPT | EXEC | FINALLY | FROM | FOR | GLOBAL | IF | IMPORT | IN | IS | LAMBDA | ORELSE | PASS | PRINT | RAISE | RETURN | TRY | WHILE | WITH | YIELD | BATCH | SQL | SIM | Neo4j | OORELINSERT | OORELCOMMIT | CONNECTTO | DODEBUG | LPAREN | RPAREN | LBRACK | RBRACK | COLON | COMMA | SEMI | PLUS | MINUS | STAR | SLASH | VBAR | AMPER | LESS | GREATER | ASSIGN | PERCENT | BACKQUOTE | LCURLY | RCURLY | CIRCUMFLEX | TILDE | EQUAL | NOTEQUAL | ALT_NOTEQUAL | LESSEQUAL | LEFTSHIFT | GREATEREQUAL | RIGHTSHIFT | PLUSEQUAL | MINUSEQUAL | DOUBLESTAR | STAREQUAL | DOUBLESLASH | SLASHEQUAL | VBAREQUAL | PERCENTEQUAL | AMPEREQUAL | CIRCUMFLEXEQUAL | LEFTSHIFTEQUAL | RIGHTSHIFTEQUAL | DOUBLESTAREQUAL | DOUBLESLASHEQUAL | DOT | AT | AND | OR | NOT | FLOAT | LONGINT | INT | COMPLEX | NAME | STRING | CONTINUED_LINE | NEWLINE | WS | LEADING_WS | COMMENT )
        int alt52=96;
        alt52 = dfa52.predict(input);
        switch (alt52) {
            case 1 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:10: AS
                {
                mAS(); 

                }
                break;
            case 2 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:13: ASSERT
                {
                mASSERT(); 

                }
                break;
            case 3 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:20: BREAK
                {
                mBREAK(); 

                }
                break;
            case 4 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:26: CLASS
                {
                mCLASS(); 

                }
                break;
            case 5 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:32: PERSISTIT
                {
                mPERSISTIT(); 

                }
                break;
            case 6 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:42: CONTINUE
                {
                mCONTINUE(); 

                }
                break;
            case 7 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:51: DEF
                {
                mDEF(); 

                }
                break;
            case 8 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:55: DELETE
                {
                mDELETE(); 

                }
                break;
            case 9 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:62: ELIF
                {
                mELIF(); 

                }
                break;
            case 10 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:67: EXCEPT
                {
                mEXCEPT(); 

                }
                break;
            case 11 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:74: EXEC
                {
                mEXEC(); 

                }
                break;
            case 12 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:79: FINALLY
                {
                mFINALLY(); 

                }
                break;
            case 13 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:87: FROM
                {
                mFROM(); 

                }
                break;
            case 14 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:92: FOR
                {
                mFOR(); 

                }
                break;
            case 15 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:96: GLOBAL
                {
                mGLOBAL(); 

                }
                break;
            case 16 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:103: IF
                {
                mIF(); 

                }
                break;
            case 17 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:106: IMPORT
                {
                mIMPORT(); 

                }
                break;
            case 18 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:113: IN
                {
                mIN(); 

                }
                break;
            case 19 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:116: IS
                {
                mIS(); 

                }
                break;
            case 20 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:119: LAMBDA
                {
                mLAMBDA(); 

                }
                break;
            case 21 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:126: ORELSE
                {
                mORELSE(); 

                }
                break;
            case 22 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:133: PASS
                {
                mPASS(); 

                }
                break;
            case 23 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:138: PRINT
                {
                mPRINT(); 

                }
                break;
            case 24 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:144: RAISE
                {
                mRAISE(); 

                }
                break;
            case 25 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:150: RETURN
                {
                mRETURN(); 

                }
                break;
            case 26 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:157: TRY
                {
                mTRY(); 

                }
                break;
            case 27 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:161: WHILE
                {
                mWHILE(); 

                }
                break;
            case 28 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:167: WITH
                {
                mWITH(); 

                }
                break;
            case 29 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:172: YIELD
                {
                mYIELD(); 

                }
                break;
            case 30 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:178: BATCH
                {
                mBATCH(); 

                }
                break;
            case 31 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:184: SQL
                {
                mSQL(); 

                }
                break;
            case 32 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:188: SIM
                {
                mSIM(); 

                }
                break;
            case 33 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:192: Neo4j
                {
                mNeo4j(); 

                }
                break;
            case 34 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:198: OORELINSERT
                {
                mOORELINSERT(); 

                }
                break;
            case 35 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:210: OORELCOMMIT
                {
                mOORELCOMMIT(); 

                }
                break;
            case 36 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:222: CONNECTTO
                {
                mCONNECTTO(); 

                }
                break;
            case 37 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:232: DODEBUG
                {
                mDODEBUG(); 

                }
                break;
            case 38 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:240: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 39 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:247: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 40 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:254: LBRACK
                {
                mLBRACK(); 

                }
                break;
            case 41 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:261: RBRACK
                {
                mRBRACK(); 

                }
                break;
            case 42 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:268: COLON
                {
                mCOLON(); 

                }
                break;
            case 43 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:274: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 44 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:280: SEMI
                {
                mSEMI(); 

                }
                break;
            case 45 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:285: PLUS
                {
                mPLUS(); 

                }
                break;
            case 46 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:290: MINUS
                {
                mMINUS(); 

                }
                break;
            case 47 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:296: STAR
                {
                mSTAR(); 

                }
                break;
            case 48 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:301: SLASH
                {
                mSLASH(); 

                }
                break;
            case 49 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:307: VBAR
                {
                mVBAR(); 

                }
                break;
            case 50 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:312: AMPER
                {
                mAMPER(); 

                }
                break;
            case 51 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:318: LESS
                {
                mLESS(); 

                }
                break;
            case 52 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:323: GREATER
                {
                mGREATER(); 

                }
                break;
            case 53 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:331: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 54 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:338: PERCENT
                {
                mPERCENT(); 

                }
                break;
            case 55 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:346: BACKQUOTE
                {
                mBACKQUOTE(); 

                }
                break;
            case 56 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:356: LCURLY
                {
                mLCURLY(); 

                }
                break;
            case 57 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:363: RCURLY
                {
                mRCURLY(); 

                }
                break;
            case 58 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:370: CIRCUMFLEX
                {
                mCIRCUMFLEX(); 

                }
                break;
            case 59 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:381: TILDE
                {
                mTILDE(); 

                }
                break;
            case 60 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:387: EQUAL
                {
                mEQUAL(); 

                }
                break;
            case 61 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:393: NOTEQUAL
                {
                mNOTEQUAL(); 

                }
                break;
            case 62 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:402: ALT_NOTEQUAL
                {
                mALT_NOTEQUAL(); 

                }
                break;
            case 63 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:415: LESSEQUAL
                {
                mLESSEQUAL(); 

                }
                break;
            case 64 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:425: LEFTSHIFT
                {
                mLEFTSHIFT(); 

                }
                break;
            case 65 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:435: GREATEREQUAL
                {
                mGREATEREQUAL(); 

                }
                break;
            case 66 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:448: RIGHTSHIFT
                {
                mRIGHTSHIFT(); 

                }
                break;
            case 67 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:459: PLUSEQUAL
                {
                mPLUSEQUAL(); 

                }
                break;
            case 68 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:469: MINUSEQUAL
                {
                mMINUSEQUAL(); 

                }
                break;
            case 69 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:480: DOUBLESTAR
                {
                mDOUBLESTAR(); 

                }
                break;
            case 70 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:491: STAREQUAL
                {
                mSTAREQUAL(); 

                }
                break;
            case 71 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:501: DOUBLESLASH
                {
                mDOUBLESLASH(); 

                }
                break;
            case 72 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:513: SLASHEQUAL
                {
                mSLASHEQUAL(); 

                }
                break;
            case 73 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:524: VBAREQUAL
                {
                mVBAREQUAL(); 

                }
                break;
            case 74 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:534: PERCENTEQUAL
                {
                mPERCENTEQUAL(); 

                }
                break;
            case 75 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:547: AMPEREQUAL
                {
                mAMPEREQUAL(); 

                }
                break;
            case 76 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:558: CIRCUMFLEXEQUAL
                {
                mCIRCUMFLEXEQUAL(); 

                }
                break;
            case 77 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:574: LEFTSHIFTEQUAL
                {
                mLEFTSHIFTEQUAL(); 

                }
                break;
            case 78 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:589: RIGHTSHIFTEQUAL
                {
                mRIGHTSHIFTEQUAL(); 

                }
                break;
            case 79 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:605: DOUBLESTAREQUAL
                {
                mDOUBLESTAREQUAL(); 

                }
                break;
            case 80 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:621: DOUBLESLASHEQUAL
                {
                mDOUBLESLASHEQUAL(); 

                }
                break;
            case 81 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:638: DOT
                {
                mDOT(); 

                }
                break;
            case 82 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:642: AT
                {
                mAT(); 

                }
                break;
            case 83 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:645: AND
                {
                mAND(); 

                }
                break;
            case 84 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:649: OR
                {
                mOR(); 

                }
                break;
            case 85 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:652: NOT
                {
                mNOT(); 

                }
                break;
            case 86 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:656: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 87 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:662: LONGINT
                {
                mLONGINT(); 

                }
                break;
            case 88 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:670: INT
                {
                mINT(); 

                }
                break;
            case 89 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:674: COMPLEX
                {
                mCOMPLEX(); 

                }
                break;
            case 90 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:682: NAME
                {
                mNAME(); 

                }
                break;
            case 91 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:687: STRING
                {
                mSTRING(); 

                }
                break;
            case 92 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:694: CONTINUED_LINE
                {
                mCONTINUED_LINE(); 

                }
                break;
            case 93 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:709: NEWLINE
                {
                mNEWLINE(); 

                }
                break;
            case 94 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:717: WS
                {
                mWS(); 

                }
                break;
            case 95 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:720: LEADING_WS
                {
                mLEADING_WS(); 

                }
                break;
            case 96 :
                // /Users/pcannata/Mine/Carnot/CarnotKE/grammar/Python.g:1:731: COMMENT
                {
                mCOMMENT(); 

                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA23 dfa23 = new DFA23(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA52 dfa52 = new DFA52(this);
    static final String DFA11_eotS =
        "\3\uffff\1\4\2\uffff";
    static final String DFA11_eofS =
        "\6\uffff";
    static final String DFA11_minS =
        "\1\56\1\uffff\1\56\1\105\2\uffff";
    static final String DFA11_maxS =
        "\1\71\1\uffff\2\145\2\uffff";
    static final String DFA11_acceptS =
        "\1\uffff\1\1\2\uffff\1\3\1\2";
    static final String DFA11_specialS =
        "\6\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\1\1\uffff\12\2",
            "",
            "\1\3\1\uffff\12\2\13\uffff\1\4\37\uffff\1\4",
            "\1\5\37\uffff\1\5",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "2557:1: FLOAT : ( '.' DIGITS ( Exponent )? | DIGITS '.' Exponent | DIGITS ( '.' ( DIGITS ( Exponent )? )? | Exponent ) );";
        }
    }
    static final String DFA20_eotS =
        "\4\uffff";
    static final String DFA20_eofS =
        "\4\uffff";
    static final String DFA20_minS =
        "\2\56\2\uffff";
    static final String DFA20_maxS =
        "\1\71\1\152\2\uffff";
    static final String DFA20_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA20_specialS =
        "\4\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\2\1\uffff\12\1\13\uffff\1\2\4\uffff\1\3\32\uffff\1\2\4\uffff"+
            "\1\3",
            "",
            ""
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "2583:1: COMPLEX : ( ( DIGITS )+ ( 'j' | 'J' ) | FLOAT ( 'j' | 'J' ) );";
        }
    }
    static final String DFA23_eotS =
        "\24\uffff";
    static final String DFA23_eofS =
        "\24\uffff";
    static final String DFA23_minS =
        "\1\42\1\uffff\2\42\1\uffff\2\42\15\uffff";
    static final String DFA23_maxS =
        "\1\165\1\uffff\2\162\1\uffff\2\162\15\uffff";
    static final String DFA23_acceptS =
        "\1\uffff\1\1\2\uffff\1\6\2\uffff\1\17\1\4\1\13\1\2\1\5\1\16\1\3"+
        "\1\11\1\14\1\7\1\12\1\15\1\10";
    static final String DFA23_specialS =
        "\24\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\7\4\uffff\1\7\32\uffff\1\6\17\uffff\1\4\2\uffff\1\5\14\uffff"+
            "\1\3\17\uffff\1\1\2\uffff\1\2",
            "",
            "\1\12\4\uffff\1\12\52\uffff\1\11\37\uffff\1\10",
            "\1\15\4\uffff\1\15\52\uffff\1\14\37\uffff\1\13",
            "",
            "\1\20\4\uffff\1\20\52\uffff\1\16\37\uffff\1\17",
            "\1\23\4\uffff\1\23\52\uffff\1\21\37\uffff\1\22",
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

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "2600:9: ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )?";
        }
    }
    static final String DFA51_eotS =
        "\2\uffff\2\4\1\uffff";
    static final String DFA51_eofS =
        "\5\uffff";
    static final String DFA51_minS =
        "\1\11\1\uffff\2\0\1\uffff";
    static final String DFA51_maxS =
        "\1\43\1\uffff\2\uffff\1\uffff";
    static final String DFA51_acceptS =
        "\1\uffff\1\1\2\uffff\1\2";
    static final String DFA51_specialS =
        "\1\1\1\uffff\1\0\1\2\1\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\1\26\uffff\1\1\2\uffff\1\2",
            "",
            "\12\3\1\1\ufff5\3",
            "\12\3\1\1\ufff5\3",
            ""
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "2734:1: COMMENT : ({...}? => ( ' ' | '\\t' )* '#' (~ '\\n' )* ( '\\n' )+ | '#' (~ '\\n' )* );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_2 = input.LA(1);

                         
                        int index51_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA51_2>='\u0000' && LA51_2<='\t')||(LA51_2>='\u000B' && LA51_2<='\uFFFF')) ) {s = 3;}

                        else if ( (LA51_2=='\n') && ((startPos==0))) {s = 1;}

                        else s = 4;

                         
                        input.seek(index51_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA51_0 = input.LA(1);

                         
                        int index51_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA51_0=='\t'||LA51_0==' ') && ((startPos==0))) {s = 1;}

                        else if ( (LA51_0=='#') ) {s = 2;}

                         
                        input.seek(index51_0);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA51_3 = input.LA(1);

                         
                        int index51_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA51_3>='\u0000' && LA51_3<='\t')||(LA51_3>='\u000B' && LA51_3<='\uFFFF')) ) {s = 3;}

                        else if ( (LA51_3=='\n') && ((startPos==0))) {s = 1;}

                        else s = 4;

                         
                        input.seek(index51_3);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 51, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA52_eotS =
        "\1\uffff\21\63\7\uffff\1\134\1\136\1\141\1\144\1\146\1\150\1\154"+
        "\1\157\1\161\1\163\3\uffff\1\165\2\uffff\1\167\1\uffff\2\63\2\175"+
        "\4\63\3\uffff\1\u008b\1\uffff\1\u008d\1\u008f\1\uffff\1\u0091\20"+
        "\63\1\u00a4\1\63\1\u00a6\1\u00a7\13\63\4\uffff\1\u00b5\2\uffff\1"+
        "\u00b7\10\uffff\1\u00b9\2\uffff\1\u00bb\7\uffff\1\u00bc\1\uffff"+
        "\1\u00be\1\63\1\uffff\2\175\1\uffff\1\u00bc\1\uffff\1\175\3\uffff"+
        "\1\175\6\63\5\uffff\1\63\1\uffff\1\u00c8\6\63\1\u00d0\1\u00d1\7"+
        "\63\1\u00d9\1\63\1\uffff\1\63\2\uffff\4\63\1\u00e1\7\63\13\uffff"+
        "\1\u00eb\3\175\1\uffff\1\u00bc\1\uffff\1\u00bc\1\63\1\uffff\5\63"+
        "\1\u00f5\1\63\2\uffff\1\63\1\u00f8\1\u00f9\1\63\1\u00fb\1\63\1\u00fd"+
        "\1\uffff\7\63\1\uffff\1\63\1\u0106\2\63\2\uffff\1\63\1\uffff\1\u00bc"+
        "\2\uffff\1\u00bc\1\uffff\1\63\1\u010d\1\u010e\3\63\1\uffff\1\u0112"+
        "\1\63\2\uffff\1\63\1\uffff\1\63\1\uffff\3\63\1\u0119\3\63\1\u011d"+
        "\1\uffff\1\u011e\2\63\1\uffff\1\u00bc\1\u0121\2\uffff\3\63\1\uffff"+
        "\1\63\1\u0126\1\63\1\u0128\1\u0129\1\u012a\1\uffff\1\u012b\2\63"+
        "\2\uffff\1\63\2\uffff\3\63\1\u0132\1\uffff\1\u0133\4\uffff\3\63"+
        "\1\u0137\2\63\2\uffff\3\63\1\uffff\1\u013d\1\u013e\1\u013f\1\u0140"+
        "\1\u0141\5\uffff";
    static final String DFA52_eofS =
        "\u0142\uffff";
    static final String DFA52_minS =
        "\1\11\1\156\1\42\1\154\1\141\1\145\1\154\1\151\1\154\1\146\1\141"+
        "\1\42\1\162\1\150\1\151\1\171\1\111\1\145\7\uffff\2\75\1\52\1\57"+
        "\2\75\1\74\3\75\3\uffff\1\75\2\uffff\1\60\1\uffff\1\162\1\157\2"+
        "\56\4\42\3\uffff\1\12\1\uffff\2\11\1\uffff\1\60\1\144\2\42\1\141"+
        "\1\156\1\162\1\163\1\151\1\146\1\144\1\151\1\143\1\156\1\157\1\162"+
        "\1\157\1\60\1\160\2\60\1\155\1\151\1\154\1\171\1\151\1\164\1\145"+
        "\1\142\1\114\1\115\1\157\4\uffff\1\75\2\uffff\1\75\10\uffff\1\75"+
        "\2\uffff\1\75\7\uffff\1\60\1\uffff\1\60\1\164\3\60\1\uffff\1\60"+
        "\1\53\2\56\2\uffff\1\56\6\42\2\uffff\1\0\1\uffff\1\0\1\145\1\uffff"+
        "\1\60\1\141\1\163\1\156\2\163\1\156\2\60\1\145\1\146\2\145\1\143"+
        "\1\141\1\155\1\60\1\142\1\uffff\1\157\2\uffff\1\142\1\163\1\165"+
        "\1\103\1\60\1\154\1\150\1\154\1\141\2\40\1\64\11\uffff\1\53\1\uffff"+
        "\4\60\1\53\3\60\1\162\1\uffff\1\153\1\163\1\151\1\145\1\151\1\60"+
        "\1\164\2\uffff\1\142\2\60\1\160\1\60\1\154\1\60\1\uffff\1\141\1"+
        "\162\1\144\1\145\1\162\1\156\1\157\1\uffff\1\145\1\60\1\144\1\164"+
        "\2\uffff\1\152\2\60\1\uffff\2\60\1\53\1\164\2\60\1\156\1\143\1\163"+
        "\1\uffff\1\60\1\165\2\uffff\1\164\1\uffff\1\154\1\uffff\1\154\1"+
        "\164\1\141\1\60\1\156\1\163\1\155\1\60\1\uffff\1\60\1\143\1\40\3"+
        "\60\2\uffff\1\165\2\164\1\uffff\1\147\1\60\1\171\3\60\1\uffff\1"+
        "\60\1\145\1\155\2\uffff\1\150\2\uffff\1\145\1\124\1\151\1\60\1\uffff"+
        "\1\60\4\uffff\1\162\1\151\1\145\1\60\1\157\1\164\2\uffff\2\164\1"+
        "\163\1\uffff\5\60\5\uffff";
    static final String DFA52_maxS =
        "\1\176\1\163\1\162\1\157\1\162\1\157\1\170\1\162\1\154\1\163\1\141"+
        "\1\145\1\162\2\151\1\171\1\121\1\145\7\uffff\6\75\2\76\2\75\3\uffff"+
        "\1\75\2\uffff\1\71\1\uffff\1\162\1\157\1\170\1\154\1\162\1\47\2"+
        "\162\3\uffff\1\15\1\uffff\2\43\1\uffff\1\172\1\144\1\145\1\47\1"+
        "\141\1\156\1\162\1\163\1\151\1\154\1\144\1\163\1\145\1\156\1\157"+
        "\1\162\1\157\1\172\1\160\2\172\1\155\1\151\1\164\1\171\1\151\1\164"+
        "\1\145\1\142\1\114\1\115\1\157\4\uffff\1\75\2\uffff\1\75\10\uffff"+
        "\1\75\2\uffff\1\75\7\uffff\1\152\1\uffff\1\172\1\164\1\146\2\154"+
        "\1\uffff\1\152\1\71\1\154\1\152\2\uffff\1\154\6\47\2\uffff\1\0\1"+
        "\uffff\1\0\1\145\1\uffff\1\172\1\141\1\163\1\164\2\163\1\156\2\172"+
        "\1\145\1\146\2\145\1\143\1\141\1\155\1\172\1\142\1\uffff\1\157\2"+
        "\uffff\1\142\1\163\1\165\1\111\1\172\1\154\1\150\1\154\1\141\2\40"+
        "\1\64\11\uffff\1\71\1\uffff\1\172\3\154\1\71\1\152\1\71\1\152\1"+
        "\162\1\uffff\1\153\1\163\1\151\1\145\1\151\1\172\1\164\2\uffff\1"+
        "\142\2\172\1\160\1\172\1\154\1\172\1\uffff\1\141\1\162\1\144\1\145"+
        "\1\162\1\156\1\157\1\uffff\1\145\1\172\1\144\1\164\2\uffff\1\152"+
        "\1\71\1\152\1\uffff\1\71\1\152\1\71\1\164\2\172\1\156\1\143\1\163"+
        "\1\uffff\1\172\1\165\2\uffff\1\164\1\uffff\1\154\1\uffff\1\154\1"+
        "\164\1\141\1\172\1\156\1\163\1\155\1\172\1\uffff\1\172\1\143\1\40"+
        "\1\71\1\152\1\172\2\uffff\1\165\2\164\1\uffff\1\147\1\172\1\171"+
        "\3\172\1\uffff\1\172\1\145\1\155\2\uffff\1\150\2\uffff\1\145\1\124"+
        "\1\151\1\172\1\uffff\1\172\4\uffff\1\162\1\151\1\145\1\172\1\157"+
        "\1\164\2\uffff\2\164\1\163\1\uffff\5\172\5\uffff";
    static final String DFA52_acceptS =
        "\22\uffff\1\46\1\47\1\50\1\51\1\52\1\53\1\54\12\uffff\1\67\1\70"+
        "\1\71\1\uffff\1\73\1\75\1\uffff\1\122\10\uffff\1\132\1\133\1\134"+
        "\1\uffff\1\135\2\uffff\1\140\40\uffff\1\103\1\55\1\104\1\56\1\uffff"+
        "\1\106\1\57\1\uffff\1\110\1\60\1\111\1\61\1\113\1\62\1\76\1\77\1"+
        "\uffff\1\63\1\101\1\uffff\1\64\1\74\1\65\1\112\1\66\1\114\1\72\1"+
        "\uffff\1\121\5\uffff\1\130\4\uffff\1\127\1\131\7\uffff\1\136\1\137"+
        "\1\uffff\1\140\2\uffff\1\1\22\uffff\1\20\1\uffff\1\22\1\23\14\uffff"+
        "\1\117\1\105\1\120\1\107\1\115\1\100\1\116\1\102\1\126\1\uffff\1"+
        "\124\11\uffff\1\123\7\uffff\1\7\1\10\7\uffff\1\16\7\uffff\1\32\4"+
        "\uffff\1\37\1\40\3\uffff\1\125\11\uffff\1\26\2\uffff\1\11\1\25\1"+
        "\uffff\1\13\1\uffff\1\15\10\uffff\1\34\6\uffff\1\3\1\4\3\uffff\1"+
        "\27\6\uffff\1\30\3\uffff\1\33\1\35\1\uffff\1\41\1\2\4\uffff\1\12"+
        "\1\uffff\1\17\1\21\1\24\1\31\6\uffff\1\45\1\14\3\uffff\1\6\5\uffff"+
        "\1\44\1\5\1\42\1\43\1\36";
    static final String DFA52_specialS =
        "\1\5\65\uffff\1\0\1\uffff\1\3\1\4\123\uffff\1\1\1\uffff\1\2\u00b2"+
        "\uffff}>";
    static final String[] DFA52_transitionS = {
            "\1\71\1\67\1\uffff\1\66\1\67\22\uffff\1\70\1\50\1\64\1\72\1"+
            "\uffff\1\42\1\36\1\64\1\22\1\23\1\33\1\31\1\27\1\32\1\51\1\34"+
            "\1\55\11\56\1\26\1\30\1\37\1\41\1\40\1\uffff\1\52\1\63\1\62"+
            "\13\63\1\21\3\63\1\60\1\20\1\63\1\61\5\63\1\24\1\65\1\25\1\46"+
            "\1\63\1\43\1\1\1\2\1\3\1\5\1\6\1\7\1\10\1\63\1\11\2\63\1\12"+
            "\1\17\1\54\1\53\1\4\1\63\1\13\1\63\1\14\1\57\1\63\1\15\1\63"+
            "\1\16\1\63\1\44\1\35\1\45\1\47",
            "\1\74\4\uffff\1\73",
            "\1\64\4\uffff\1\64\52\uffff\1\76\37\uffff\1\75",
            "\1\77\2\uffff\1\100",
            "\1\102\3\uffff\1\101\14\uffff\1\103",
            "\1\104\11\uffff\1\105",
            "\1\106\13\uffff\1\107",
            "\1\110\5\uffff\1\112\2\uffff\1\111",
            "\1\113",
            "\1\114\6\uffff\1\115\1\116\4\uffff\1\117",
            "\1\120",
            "\1\64\4\uffff\1\64\71\uffff\1\121\3\uffff\1\122",
            "\1\123",
            "\1\124\1\125",
            "\1\126",
            "\1\127",
            "\1\131\7\uffff\1\130",
            "\1\132",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\133",
            "\1\135",
            "\1\137\22\uffff\1\140",
            "\1\142\15\uffff\1\143",
            "\1\145",
            "\1\147",
            "\1\153\1\152\1\151",
            "\1\155\1\156",
            "\1\160",
            "\1\162",
            "",
            "",
            "",
            "\1\164",
            "",
            "",
            "\12\166",
            "",
            "\1\170",
            "\1\171",
            "\1\176\1\uffff\10\u0080\2\u0081\10\uffff\1\174\2\uffff\1\177"+
            "\4\uffff\1\u0083\1\uffff\1\u0082\2\uffff\1\173\10\uffff\1\172"+
            "\11\uffff\1\174\2\uffff\1\177\4\uffff\1\u0083\1\uffff\1\u0082"+
            "\2\uffff\1\173\10\uffff\1\172",
            "\1\176\1\uffff\12\u0084\13\uffff\1\177\4\uffff\1\u0083\1\uffff"+
            "\1\u0082\30\uffff\1\177\4\uffff\1\u0083\1\uffff\1\u0082",
            "\1\64\4\uffff\1\64\52\uffff\1\u0086\37\uffff\1\u0085",
            "\1\64\4\uffff\1\64",
            "\1\64\4\uffff\1\64\52\uffff\1\u0087\37\uffff\1\u0088",
            "\1\64\4\uffff\1\64\52\uffff\1\u0089\37\uffff\1\u008a",
            "",
            "",
            "",
            "\1\67\2\uffff\1\67",
            "",
            "\1\71\1\u008c\1\uffff\1\u008b\1\u008c\22\uffff\1\70\2\uffff"+
            "\1\u008e",
            "\1\71\1\u008c\1\uffff\1\u008b\1\u008c\22\uffff\1\70\2\uffff"+
            "\1\u008e",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\22\63\1\u0090\7\63",
            "\1\u0092",
            "\1\64\4\uffff\1\64\75\uffff\1\u0093",
            "\1\64\4\uffff\1\64",
            "\1\u0094",
            "\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099\5\uffff\1\u009a",
            "\1\u009b",
            "\1\u009c\11\uffff\1\u009d",
            "\1\u009e\1\uffff\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00a5",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00ab\7\uffff\1\u00aa",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "",
            "",
            "",
            "",
            "\1\u00b4",
            "",
            "",
            "\1\u00b6",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00b8",
            "",
            "",
            "\1\u00ba",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\166\13\uffff\1\u00bd\4\uffff\1\u0083\32\uffff\1\u00bd\4"+
            "\uffff\1\u0083",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00bf",
            "\12\u00c0\7\uffff\6\u00c0\32\uffff\6\u00c0",
            "\10\u00c1\24\uffff\1\u0082\37\uffff\1\u0082",
            "\2\u00c2\32\uffff\1\u0082\37\uffff\1\u0082",
            "",
            "\12\u00c4\13\uffff\1\u00c3\4\uffff\1\u0083\32\uffff\1\u00c3"+
            "\4\uffff\1\u0083",
            "\1\u00c5\1\uffff\1\u00c5\2\uffff\12\u00c6",
            "\1\176\1\uffff\10\u0080\2\u0081\13\uffff\1\177\4\uffff\1\u0083"+
            "\1\uffff\1\u0082\30\uffff\1\177\4\uffff\1\u0083\1\uffff\1\u0082",
            "\1\176\1\uffff\12\u0081\13\uffff\1\177\4\uffff\1\u0083\32\uffff"+
            "\1\177\4\uffff\1\u0083",
            "",
            "",
            "\1\176\1\uffff\12\u0084\13\uffff\1\177\4\uffff\1\u0083\1\uffff"+
            "\1\u0082\30\uffff\1\177\4\uffff\1\u0083\1\uffff\1\u0082",
            "\1\64\4\uffff\1\64",
            "\1\64\4\uffff\1\64",
            "\1\64\4\uffff\1\64",
            "\1\64\4\uffff\1\64",
            "\1\64\4\uffff\1\64",
            "\1\64\4\uffff\1\64",
            "",
            "",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\u00c7",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00c9",
            "\1\u00ca",
            "\1\u00cc\5\uffff\1\u00cb",
            "\1\u00cd",
            "\1\u00ce",
            "\1\u00cf",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00da",
            "",
            "\1\u00db",
            "",
            "",
            "\1\u00dc",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00e0\5\uffff\1\u00df",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00e2",
            "\1\u00e3",
            "\1\u00e4",
            "\1\u00e5",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00e9\1\uffff\1\u00e9\2\uffff\12\u00ea",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\u00c0\7\uffff\6\u00c0\5\uffff\1\u0082\24\uffff\6\u00c0"+
            "\5\uffff\1\u0082",
            "\10\u00c1\24\uffff\1\u0082\37\uffff\1\u0082",
            "\2\u00c2\32\uffff\1\u0082\37\uffff\1\u0082",
            "\1\u00ec\1\uffff\1\u00ec\2\uffff\12\u00ed",
            "\12\u00c4\13\uffff\1\u00ee\4\uffff\1\u0083\32\uffff\1\u00ee"+
            "\4\uffff\1\u0083",
            "\12\u00c6",
            "\12\u00c6\20\uffff\1\u0083\37\uffff\1\u0083",
            "\1\u00ef",
            "",
            "\1\u00f0",
            "\1\u00f1",
            "\1\u00f2",
            "\1\u00f3",
            "\1\u00f4",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00f6",
            "",
            "",
            "\1\u00f7",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00fa",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u00fc",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "",
            "\1\u0105",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u0107",
            "\1\u0108",
            "",
            "",
            "\1\u0109",
            "\12\u00ea",
            "\12\u00ea\20\uffff\1\u0083\37\uffff\1\u0083",
            "",
            "\12\u00ed",
            "\12\u00ed\20\uffff\1\u0083\37\uffff\1\u0083",
            "\1\u010a\1\uffff\1\u010a\2\uffff\12\u010b",
            "\1\u010c",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u0113",
            "",
            "",
            "\1\u0114",
            "",
            "\1\u0115",
            "",
            "\1\u0116",
            "\1\u0117",
            "\1\u0118",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u011a",
            "\1\u011b",
            "\1\u011c",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u011f",
            "\1\u0120",
            "\12\u010b",
            "\12\u010b\20\uffff\1\u0083\37\uffff\1\u0083",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "",
            "",
            "\1\u0122",
            "\1\u0123",
            "\1\u0124",
            "",
            "\1\u0125",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u0127",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u012c",
            "\1\u012d",
            "",
            "",
            "\1\u012e",
            "",
            "",
            "\1\u012f",
            "\1\u0130",
            "\1\u0131",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "",
            "",
            "",
            "",
            "\1\u0134",
            "\1\u0135",
            "\1\u0136",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\1\u0138",
            "\1\u0139",
            "",
            "",
            "\1\u013a",
            "\1\u013b",
            "\1\u013c",
            "",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "\12\63\7\uffff\32\63\4\uffff\1\63\1\uffff\32\63",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA52_eot = DFA.unpackEncodedString(DFA52_eotS);
    static final short[] DFA52_eof = DFA.unpackEncodedString(DFA52_eofS);
    static final char[] DFA52_min = DFA.unpackEncodedStringToUnsignedChars(DFA52_minS);
    static final char[] DFA52_max = DFA.unpackEncodedStringToUnsignedChars(DFA52_maxS);
    static final short[] DFA52_accept = DFA.unpackEncodedString(DFA52_acceptS);
    static final short[] DFA52_special = DFA.unpackEncodedString(DFA52_specialS);
    static final short[][] DFA52_transition;

    static {
        int numStates = DFA52_transitionS.length;
        DFA52_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA52_transition[i] = DFA.unpackEncodedString(DFA52_transitionS[i]);
        }
    }

    class DFA52 extends DFA {

        public DFA52(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 52;
            this.eot = DFA52_eot;
            this.eof = DFA52_eof;
            this.min = DFA52_min;
            this.max = DFA52_max;
            this.accept = DFA52_accept;
            this.special = DFA52_special;
            this.transition = DFA52_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( AS | ASSERT | BREAK | CLASS | PERSISTIT | CONTINUE | DEF | DELETE | ELIF | EXCEPT | EXEC | FINALLY | FROM | FOR | GLOBAL | IF | IMPORT | IN | IS | LAMBDA | ORELSE | PASS | PRINT | RAISE | RETURN | TRY | WHILE | WITH | YIELD | BATCH | SQL | SIM | Neo4j | OORELINSERT | OORELCOMMIT | CONNECTTO | DODEBUG | LPAREN | RPAREN | LBRACK | RBRACK | COLON | COMMA | SEMI | PLUS | MINUS | STAR | SLASH | VBAR | AMPER | LESS | GREATER | ASSIGN | PERCENT | BACKQUOTE | LCURLY | RCURLY | CIRCUMFLEX | TILDE | EQUAL | NOTEQUAL | ALT_NOTEQUAL | LESSEQUAL | LEFTSHIFT | GREATEREQUAL | RIGHTSHIFT | PLUSEQUAL | MINUSEQUAL | DOUBLESTAR | STAREQUAL | DOUBLESLASH | SLASHEQUAL | VBAREQUAL | PERCENTEQUAL | AMPEREQUAL | CIRCUMFLEXEQUAL | LEFTSHIFTEQUAL | RIGHTSHIFTEQUAL | DOUBLESTAREQUAL | DOUBLESLASHEQUAL | DOT | AT | AND | OR | NOT | FLOAT | LONGINT | INT | COMPLEX | NAME | STRING | CONTINUED_LINE | NEWLINE | WS | LEADING_WS | COMMENT );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA52_54 = input.LA(1);

                         
                        int index52_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_54=='\n'||LA52_54=='\r') ) {s = 55;}

                        else s = 139;

                         
                        input.seek(index52_54);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA52_141 = input.LA(1);

                         
                        int index52_141 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((startPos>0)) ) {s = 139;}

                        else if ( (((startPos==0)||((startPos==0)&&(implicitLineJoiningLevel>0)))) ) {s = 140;}

                         
                        input.seek(index52_141);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA52_143 = input.LA(1);

                         
                        int index52_143 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((startPos>0)) ) {s = 139;}

                        else if ( (((startPos==0)||((startPos==0)&&(implicitLineJoiningLevel>0)))) ) {s = 140;}

                         
                        input.seek(index52_143);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA52_56 = input.LA(1);

                         
                        int index52_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_56=='\n'||LA52_56=='\r') && ((startPos==0))) {s = 140;}

                        else if ( (LA52_56==' ') && (((startPos==0)||(startPos>0)))) {s = 56;}

                        else if ( (LA52_56=='\t') && (((startPos==0)||(startPos>0)))) {s = 57;}

                        else if ( (LA52_56=='#') && ((startPos==0))) {s = 142;}

                        else if ( (LA52_56=='\f') && ((startPos>0))) {s = 139;}

                        else s = 141;

                         
                        input.seek(index52_56);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA52_57 = input.LA(1);

                         
                        int index52_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_57=='\n'||LA52_57=='\r') && ((startPos==0))) {s = 140;}

                        else if ( (LA52_57==' ') && (((startPos==0)||(startPos>0)))) {s = 56;}

                        else if ( (LA52_57=='\t') && (((startPos==0)||(startPos>0)))) {s = 57;}

                        else if ( (LA52_57=='#') && ((startPos==0))) {s = 142;}

                        else if ( (LA52_57=='\f') && ((startPos>0))) {s = 139;}

                        else s = 143;

                         
                        input.seek(index52_57);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA52_0 = input.LA(1);

                         
                        int index52_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_0=='a') ) {s = 1;}

                        else if ( (LA52_0=='b') ) {s = 2;}

                        else if ( (LA52_0=='c') ) {s = 3;}

                        else if ( (LA52_0=='p') ) {s = 4;}

                        else if ( (LA52_0=='d') ) {s = 5;}

                        else if ( (LA52_0=='e') ) {s = 6;}

                        else if ( (LA52_0=='f') ) {s = 7;}

                        else if ( (LA52_0=='g') ) {s = 8;}

                        else if ( (LA52_0=='i') ) {s = 9;}

                        else if ( (LA52_0=='l') ) {s = 10;}

                        else if ( (LA52_0=='r') ) {s = 11;}

                        else if ( (LA52_0=='t') ) {s = 12;}

                        else if ( (LA52_0=='w') ) {s = 13;}

                        else if ( (LA52_0=='y') ) {s = 14;}

                        else if ( (LA52_0=='m') ) {s = 15;}

                        else if ( (LA52_0=='S') ) {s = 16;}

                        else if ( (LA52_0=='N') ) {s = 17;}

                        else if ( (LA52_0=='(') ) {s = 18;}

                        else if ( (LA52_0==')') ) {s = 19;}

                        else if ( (LA52_0=='[') ) {s = 20;}

                        else if ( (LA52_0==']') ) {s = 21;}

                        else if ( (LA52_0==':') ) {s = 22;}

                        else if ( (LA52_0==',') ) {s = 23;}

                        else if ( (LA52_0==';') ) {s = 24;}

                        else if ( (LA52_0=='+') ) {s = 25;}

                        else if ( (LA52_0=='-') ) {s = 26;}

                        else if ( (LA52_0=='*') ) {s = 27;}

                        else if ( (LA52_0=='/') ) {s = 28;}

                        else if ( (LA52_0=='|') ) {s = 29;}

                        else if ( (LA52_0=='&') ) {s = 30;}

                        else if ( (LA52_0=='<') ) {s = 31;}

                        else if ( (LA52_0=='>') ) {s = 32;}

                        else if ( (LA52_0=='=') ) {s = 33;}

                        else if ( (LA52_0=='%') ) {s = 34;}

                        else if ( (LA52_0=='`') ) {s = 35;}

                        else if ( (LA52_0=='{') ) {s = 36;}

                        else if ( (LA52_0=='}') ) {s = 37;}

                        else if ( (LA52_0=='^') ) {s = 38;}

                        else if ( (LA52_0=='~') ) {s = 39;}

                        else if ( (LA52_0=='!') ) {s = 40;}

                        else if ( (LA52_0=='.') ) {s = 41;}

                        else if ( (LA52_0=='@') ) {s = 42;}

                        else if ( (LA52_0=='o') ) {s = 43;}

                        else if ( (LA52_0=='n') ) {s = 44;}

                        else if ( (LA52_0=='0') ) {s = 45;}

                        else if ( ((LA52_0>='1' && LA52_0<='9')) ) {s = 46;}

                        else if ( (LA52_0=='u') ) {s = 47;}

                        else if ( (LA52_0=='R') ) {s = 48;}

                        else if ( (LA52_0=='U') ) {s = 49;}

                        else if ( (LA52_0=='B') ) {s = 50;}

                        else if ( (LA52_0=='A'||(LA52_0>='C' && LA52_0<='M')||(LA52_0>='O' && LA52_0<='Q')||LA52_0=='T'||(LA52_0>='V' && LA52_0<='Z')||LA52_0=='_'||LA52_0=='h'||(LA52_0>='j' && LA52_0<='k')||LA52_0=='q'||LA52_0=='s'||LA52_0=='v'||LA52_0=='x'||LA52_0=='z') ) {s = 51;}

                        else if ( (LA52_0=='\"'||LA52_0=='\'') ) {s = 52;}

                        else if ( (LA52_0=='\\') ) {s = 53;}

                        else if ( (LA52_0=='\f') ) {s = 54;}

                        else if ( (LA52_0=='\n'||LA52_0=='\r') ) {s = 55;}

                        else if ( (LA52_0==' ') && (((startPos==0)||(startPos>0)))) {s = 56;}

                        else if ( (LA52_0=='\t') && (((startPos==0)||(startPos>0)))) {s = 57;}

                        else if ( (LA52_0=='#') ) {s = 58;}

                         
                        input.seek(index52_0);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 52, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}