// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g 2016-02-09 16:15:05

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
    public static final int COMMENT=107;
    public static final int INDENT=4;
    public static final int IMPORT=28;
    public static final int DELETE=19;
    public static final int JAPI=97;
    public static final int Neo4j=96;
    public static final int ESC=106;
    public static final int CONNECTTO=99;
    public static final int ALT_NOTEQUAL=70;
    public static final int RCURLY=85;
    public static final int COMMA=48;
    public static final int TRIQUOTE=105;
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
    public static final int PERSISTIT=101;
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
    public static final int SPARQL=98;
    public static final int NODEBUG=100;
    public static final int FLOAT=89;
    public static final int TRIAPOS=104;
    public static final int Exponent=103;
    public static final int DIGITS=102;
    public static final int INT=87;
    public static final int DOUBLESLASH=80;
    public static final int RETURN=37;
    public static final int GLOBAL=26;
    public static final int BATCH=42;
    public static final int CONTINUED_LINE=108;
    public static final int WS=109;
    public static final int EOF=-1;
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
    public String getGrammarFileName() { return "/home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g"; }

    // $ANTLR start "AS"
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2468:11: ( 'as' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2468:13: 'as'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2469:11: ( 'assert' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2469:13: 'assert'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2470:11: ( 'break' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2470:13: 'break'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2471:11: ( 'class' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2471:13: 'class'
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

    // $ANTLR start "CONTINUE"
    public final void mCONTINUE() throws RecognitionException {
        try {
            int _type = CONTINUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2472:11: ( 'continue' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2472:13: 'continue'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2473:11: ( 'def' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2473:13: 'def'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2474:11: ( 'del' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2474:13: 'del'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2475:11: ( 'elif' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2475:13: 'elif'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2476:11: ( 'except' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2476:13: 'except'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2477:11: ( 'exec' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2477:13: 'exec'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2478:11: ( 'finally' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2478:13: 'finally'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2479:11: ( 'from' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2479:13: 'from'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2480:11: ( 'for' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2480:13: 'for'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2481:11: ( 'global' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2481:13: 'global'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2482:11: ( 'if' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2482:13: 'if'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2483:11: ( 'import' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2483:13: 'import'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2484:11: ( 'in' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2484:13: 'in'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2485:11: ( 'is' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2485:13: 'is'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2486:11: ( 'lambda' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2486:13: 'lambda'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2487:11: ( 'else' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2487:13: 'else'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2488:11: ( 'pass' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2488:13: 'pass'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2489:11: ( 'print' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2489:13: 'print'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2490:11: ( 'raise' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2490:13: 'raise'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2491:11: ( 'return' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2491:13: 'return'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2492:11: ( 'try' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2492:13: 'try'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2493:11: ( 'while' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2493:13: 'while'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2494:11: ( 'with' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2494:13: 'with'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2495:11: ( 'yield' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2495:13: 'yield'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2496:11: ( 'mybatches' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2496:13: 'mybatches'
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

    // $ANTLR start "PERSISTIT"
    public final void mPERSISTIT() throws RecognitionException {
        try {
            int _type = PERSISTIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2499:13: ( 'persist' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2499:15: 'persist' ( ' ' )+ 'on' ( ' ' )+
            {
            match("persist"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2499:25: ( ' ' )+
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
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2499:26: ' '
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

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2499:37: ( ' ' )+
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
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2499:38: ' '
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
    // $ANTLR end "PERSISTIT"

    // $ANTLR start "SQL"
    public final void mSQL() throws RecognitionException {
        try {
            int _type = SQL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2500:9: ( 'SQL' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2500:11: 'SQL' ( ' ' )+ 'on' ( ' ' )+
            {
            match("SQL"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2500:17: ( ' ' )+
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
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2500:18: ' '
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

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2500:29: ( ' ' )+
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
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2500:30: ' '
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
    // $ANTLR end "SQL"

    // $ANTLR start "SIM"
    public final void mSIM() throws RecognitionException {
        try {
            int _type = SIM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2501:9: ( 'SIM' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2501:11: 'SIM' ( ' ' )+ 'on' ( ' ' )+
            {
            match("SIM"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2501:17: ( ' ' )+
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
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2501:18: ' '
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

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2501:29: ( ' ' )+
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
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2501:30: ' '
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
    // $ANTLR end "SIM"

    // $ANTLR start "Neo4j"
    public final void mNeo4j() throws RecognitionException {
        try {
            int _type = Neo4j;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2502:9: ( 'Neo4j' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2502:11: 'Neo4j' ( ' ' )+ 'on' ( ' ' )+
            {
            match("Neo4j"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2502:19: ( ' ' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==' ') ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2502:20: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            match("on"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2502:31: ( ' ' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==' ') ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2502:32: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Neo4j"

    // $ANTLR start "JAPI"
    public final void mJAPI() throws RecognitionException {
        try {
            int _type = JAPI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2503:8: ( 'JAPI' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2503:10: 'JAPI' ( ' ' )+ 'on' ( ' ' )+
            {
            match("JAPI"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2503:17: ( ' ' )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==' ') ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2503:18: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            match("on"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2503:29: ( ' ' )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==' ') ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2503:30: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "JAPI"

    // $ANTLR start "SPARQL"
    public final void mSPARQL() throws RecognitionException {
        try {
            int _type = SPARQL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2504:10: ( 'SPARQL' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2504:12: 'SPARQL' ( ' ' )+ 'on' ( ' ' )+
            {
            match("SPARQL"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2504:21: ( ' ' )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==' ') ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2504:22: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);

            match("on"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2504:33: ( ' ' )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==' ') ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2504:34: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SPARQL"

    // $ANTLR start "OORELINSERT"
    public final void mOORELINSERT() throws RecognitionException {
        try {
            int _type = OORELINSERT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2505:12: ( 'relInsert' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2505:14: 'relInsert' ( ' ' )+ 'on' ( ' ' )+
            {
            match("relInsert"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2505:26: ( ' ' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==' ') ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2505:27: ' '
            	    {
            	    match(' '); 

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

            match("on"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2505:38: ( ' ' )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==' ') ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2505:39: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
            } while (true);


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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2506:12: ( 'relCommit' ( ' ' )+ 'on' ( ' ' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2506:14: 'relCommit' ( ' ' )+ 'on' ( ' ' )+
            {
            match("relCommit"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2506:26: ( ' ' )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==' ') ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2506:27: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);

            match("on"); 

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2506:38: ( ' ' )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==' ') ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2506:39: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2507:10: ( 'connectTo' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2507:13: 'connectTo'
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

    // $ANTLR start "NODEBUG"
    public final void mNODEBUG() throws RecognitionException {
        try {
            int _type = NODEBUG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2508:8: ( 'nodebug' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2508:11: 'nodebug'
            {
            match("nodebug"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NODEBUG"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2510:11: ( '(' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2510:13: '('
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2512:11: ( ')' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2512:13: ')'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2514:11: ( '[' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2514:13: '['
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2516:11: ( ']' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2516:13: ']'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2518:11: ( ':' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2518:13: ':'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2520:10: ( ',' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2520:12: ','
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2522:9: ( ';' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2522:11: ';'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2524:9: ( '+' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2524:11: '+'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2526:10: ( '-' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2526:12: '-'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2528:9: ( '*' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2528:11: '*'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2530:10: ( '/' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2530:12: '/'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2532:9: ( '|' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2532:11: '|'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2534:10: ( '&' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2534:12: '&'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2536:9: ( '<' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2536:11: '<'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2538:12: ( '>' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2538:14: '>'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2540:11: ( '=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2540:13: '='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2542:12: ( '%' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2542:14: '%'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2544:14: ( '`' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2544:16: '`'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2546:11: ( '{' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2546:13: '{'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2548:11: ( '}' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2548:13: '}'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2550:15: ( '^' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2550:17: '^'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2552:10: ( '~' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2552:12: '~'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2554:10: ( '==' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2554:12: '=='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2556:13: ( '!=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2556:15: '!='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2558:13: ( '<>' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2558:15: '<>'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2560:14: ( '<=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2560:16: '<='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2562:14: ( '<<' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2562:16: '<<'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2564:17: ( '>=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2564:19: '>='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2566:15: ( '>>' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2566:17: '>>'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2568:14: ( '+=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2568:16: '+='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2570:15: ( '-=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2570:17: '-='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2572:15: ( '**' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2572:17: '**'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2574:14: ( '*=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2574:16: '*='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2576:16: ( '//' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2576:18: '//'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2578:15: ( '/=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2578:17: '/='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2580:14: ( '|=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2580:16: '|='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2582:17: ( '%=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2582:19: '%='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2584:15: ( '&=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2584:17: '&='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2586:20: ( '^=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2586:22: '^='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2588:19: ( '<<=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2588:21: '<<='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2590:20: ( '>>=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2590:22: '>>='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2592:20: ( '**=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2592:22: '**='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2594:21: ( '//=' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2594:23: '//='
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2596:5: ( '.' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2596:7: '.'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2598:4: ( '@' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2598:6: '@'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2600:5: ( 'and' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2600:7: 'and'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2602:4: ( 'or' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2602:6: 'or'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2604:5: ( 'not' )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2604:7: 'not'
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2607:5: ( '.' DIGITS ( Exponent )? | DIGITS '.' Exponent | DIGITS ( '.' ( DIGITS ( Exponent )? )? | Exponent ) )
            int alt21=3;
            alt21 = dfa21.predict(input);
            switch (alt21) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2607:9: '.' DIGITS ( Exponent )?
                    {
                    match('.'); 
                    mDIGITS(); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2607:20: ( Exponent )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0=='E'||LA17_0=='e') ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2607:21: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2608:9: DIGITS '.' Exponent
                    {
                    mDIGITS(); 
                    match('.'); 
                    mExponent(); 

                    }
                    break;
                case 3 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:9: DIGITS ( '.' ( DIGITS ( Exponent )? )? | Exponent )
                    {
                    mDIGITS(); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:16: ( '.' ( DIGITS ( Exponent )? )? | Exponent )
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0=='.') ) {
                        alt20=1;
                    }
                    else if ( (LA20_0=='E'||LA20_0=='e') ) {
                        alt20=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 20, 0, input);

                        throw nvae;
                    }
                    switch (alt20) {
                        case 1 :
                            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:17: '.' ( DIGITS ( Exponent )? )?
                            {
                            match('.'); 
                            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:21: ( DIGITS ( Exponent )? )?
                            int alt19=2;
                            int LA19_0 = input.LA(1);

                            if ( ((LA19_0>='0' && LA19_0<='9')) ) {
                                alt19=1;
                            }
                            switch (alt19) {
                                case 1 :
                                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:22: DIGITS ( Exponent )?
                                    {
                                    mDIGITS(); 
                                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:29: ( Exponent )?
                                    int alt18=2;
                                    int LA18_0 = input.LA(1);

                                    if ( (LA18_0=='E'||LA18_0=='e') ) {
                                        alt18=1;
                                    }
                                    switch (alt18) {
                                        case 1 :
                                            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:30: Exponent
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
                            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2609:45: Exponent
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2613:5: ( INT ( 'l' | 'L' ) )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2613:9: INT ( 'l' | 'L' )
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2618:5: ( ( 'e' | 'E' ) ( '+' | '-' )? DIGITS )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2618:10: ( 'e' | 'E' ) ( '+' | '-' )? DIGITS
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2618:22: ( '+' | '-' )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='+'||LA22_0=='-') ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2621:5: ( '0' ( 'x' | 'X' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ | '0' ( 'o' | 'O' ) ( '0' .. '7' )* | '0' ( '0' .. '7' )* | '0' ( 'b' | 'B' ) ( '0' .. '1' )* | '1' .. '9' ( DIGITS )* )
            int alt28=5;
            int LA28_0 = input.LA(1);

            if ( (LA28_0=='0') ) {
                switch ( input.LA(2) ) {
                case 'X':
                case 'x':
                    {
                    alt28=1;
                    }
                    break;
                case 'O':
                case 'o':
                    {
                    alt28=2;
                    }
                    break;
                case 'B':
                case 'b':
                    {
                    alt28=4;
                    }
                    break;
                default:
                    alt28=3;}

            }
            else if ( ((LA28_0>='1' && LA28_0<='9')) ) {
                alt28=5;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2622:9: '0' ( 'x' | 'X' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
                    {
                    match('0'); 
                    if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2622:25: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
                    int cnt23=0;
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( ((LA23_0>='0' && LA23_0<='9')||(LA23_0>='A' && LA23_0<='F')||(LA23_0>='a' && LA23_0<='f')) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:
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
                    	    if ( cnt23 >= 1 ) break loop23;
                                EarlyExitException eee =
                                    new EarlyExitException(23, input);
                                throw eee;
                        }
                        cnt23++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2624:9: '0' ( 'o' | 'O' ) ( '0' .. '7' )*
                    {
                    match('0'); 
                    if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2624:25: ( '0' .. '7' )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( ((LA24_0>='0' && LA24_0<='7')) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2624:27: '0' .. '7'
                    	    {
                    	    matchRange('0','7'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2625:9: '0' ( '0' .. '7' )*
                    {
                    match('0'); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2625:14: ( '0' .. '7' )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( ((LA25_0>='0' && LA25_0<='7')) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2625:16: '0' .. '7'
                    	    {
                    	    matchRange('0','7'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;
                case 4 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2627:9: '0' ( 'b' | 'B' ) ( '0' .. '1' )*
                    {
                    match('0'); 
                    if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2627:25: ( '0' .. '1' )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( ((LA26_0>='0' && LA26_0<='1')) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2627:27: '0' .. '1'
                    	    {
                    	    matchRange('0','1'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);


                    }
                    break;
                case 5 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2629:9: '1' .. '9' ( DIGITS )*
                    {
                    matchRange('1','9'); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2629:18: ( DIGITS )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( ((LA27_0>='0' && LA27_0<='9')) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2629:18: DIGITS
                    	    {
                    	    mDIGITS(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop27;
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2633:5: ( ( DIGITS )+ ( 'j' | 'J' ) | FLOAT ( 'j' | 'J' ) )
            int alt30=2;
            alt30 = dfa30.predict(input);
            switch (alt30) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2633:9: ( DIGITS )+ ( 'j' | 'J' )
                    {
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2633:9: ( DIGITS )+
                    int cnt29=0;
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( ((LA29_0>='0' && LA29_0<='9')) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2633:9: DIGITS
                    	    {
                    	    mDIGITS(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt29 >= 1 ) break loop29;
                                EarlyExitException eee =
                                    new EarlyExitException(29, input);
                                throw eee;
                        }
                        cnt29++;
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
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2634:9: FLOAT ( 'j' | 'J' )
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2638:8: ( ( '0' .. '9' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2638:10: ( '0' .. '9' )+
            {
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2638:10: ( '0' .. '9' )+
            int cnt31=0;
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>='0' && LA31_0<='9')) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2638:12: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

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
    // $ANTLR end "DIGITS"

    // $ANTLR start "NAME"
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2640:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2640:10: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2641:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( ((LA32_0>='0' && LA32_0<='9')||(LA32_0>='A' && LA32_0<='Z')||LA32_0=='_'||(LA32_0>='a' && LA32_0<='z')) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:
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
            	    break loop32;
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:5: ( ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )? ( '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\'' | '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"' | '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' | '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' ) )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:9: ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )? ( '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\'' | '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"' | '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' | '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' )
            {
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:9: ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )?
            int alt33=15;
            alt33 = dfa33.predict(input);
            switch (alt33) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:10: 'r'
                    {
                    match('r'); 

                    }
                    break;
                case 2 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:14: 'u'
                    {
                    match('u'); 

                    }
                    break;
                case 3 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:18: 'b'
                    {
                    match('b'); 

                    }
                    break;
                case 4 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:22: 'ur'
                    {
                    match("ur"); 


                    }
                    break;
                case 5 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:27: 'br'
                    {
                    match("br"); 


                    }
                    break;
                case 6 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:32: 'R'
                    {
                    match('R'); 

                    }
                    break;
                case 7 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:36: 'U'
                    {
                    match('U'); 

                    }
                    break;
                case 8 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:40: 'B'
                    {
                    match('B'); 

                    }
                    break;
                case 9 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:44: 'UR'
                    {
                    match("UR"); 


                    }
                    break;
                case 10 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:49: 'BR'
                    {
                    match("BR"); 


                    }
                    break;
                case 11 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:54: 'uR'
                    {
                    match("uR"); 


                    }
                    break;
                case 12 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:59: 'Ur'
                    {
                    match("Ur"); 


                    }
                    break;
                case 13 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:64: 'Br'
                    {
                    match("Br"); 


                    }
                    break;
                case 14 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2649:69: 'bR'
                    {
                    match("bR"); 


                    }
                    break;

            }

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2650:9: ( '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\'' | '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"' | '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' | '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' )
            int alt38=4;
            int LA38_0 = input.LA(1);

            if ( (LA38_0=='\'') ) {
                int LA38_1 = input.LA(2);

                if ( (LA38_1=='\'') ) {
                    int LA38_3 = input.LA(3);

                    if ( (LA38_3=='\'') ) {
                        alt38=1;
                    }
                    else {
                        alt38=4;}
                }
                else if ( ((LA38_1>='\u0000' && LA38_1<='\t')||(LA38_1>='\u000B' && LA38_1<='&')||(LA38_1>='(' && LA38_1<='\uFFFF')) ) {
                    alt38=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA38_0=='\"') ) {
                int LA38_2 = input.LA(2);

                if ( (LA38_2=='\"') ) {
                    int LA38_5 = input.LA(3);

                    if ( (LA38_5=='\"') ) {
                        alt38=2;
                    }
                    else {
                        alt38=3;}
                }
                else if ( ((LA38_2>='\u0000' && LA38_2<='\t')||(LA38_2>='\u000B' && LA38_2<='!')||(LA38_2>='#' && LA38_2<='\uFFFF')) ) {
                    alt38=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2650:13: '\\'\\'\\'' ( options {greedy=false; } : TRIAPOS )* '\\'\\'\\''
                    {
                    match("'''"); 

                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2650:22: ( options {greedy=false; } : TRIAPOS )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0=='\'') ) {
                            int LA34_1 = input.LA(2);

                            if ( (LA34_1=='\'') ) {
                                int LA34_3 = input.LA(3);

                                if ( (LA34_3=='\'') ) {
                                    alt34=2;
                                }
                                else if ( ((LA34_3>='\u0000' && LA34_3<='&')||(LA34_3>='(' && LA34_3<='\uFFFF')) ) {
                                    alt34=1;
                                }


                            }
                            else if ( ((LA34_1>='\u0000' && LA34_1<='&')||(LA34_1>='(' && LA34_1<='\uFFFF')) ) {
                                alt34=1;
                            }


                        }
                        else if ( ((LA34_0>='\u0000' && LA34_0<='&')||(LA34_0>='(' && LA34_0<='\uFFFF')) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2650:47: TRIAPOS
                    	    {
                    	    mTRIAPOS(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop34;
                        }
                    } while (true);

                    match("'''"); 


                    }
                    break;
                case 2 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2651:13: '\"\"\"' ( options {greedy=false; } : TRIQUOTE )* '\"\"\"'
                    {
                    match("\"\"\""); 

                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2651:19: ( options {greedy=false; } : TRIQUOTE )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0=='\"') ) {
                            int LA35_1 = input.LA(2);

                            if ( (LA35_1=='\"') ) {
                                int LA35_3 = input.LA(3);

                                if ( (LA35_3=='\"') ) {
                                    alt35=2;
                                }
                                else if ( ((LA35_3>='\u0000' && LA35_3<='!')||(LA35_3>='#' && LA35_3<='\uFFFF')) ) {
                                    alt35=1;
                                }


                            }
                            else if ( ((LA35_1>='\u0000' && LA35_1<='!')||(LA35_1>='#' && LA35_1<='\uFFFF')) ) {
                                alt35=1;
                            }


                        }
                        else if ( ((LA35_0>='\u0000' && LA35_0<='!')||(LA35_0>='#' && LA35_0<='\uFFFF')) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2651:44: TRIQUOTE
                    	    {
                    	    mTRIQUOTE(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop35;
                        }
                    } while (true);

                    match("\"\"\""); 


                    }
                    break;
                case 3 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2652:13: '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"'
                    {
                    match('\"'); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2652:17: ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )*
                    loop36:
                    do {
                        int alt36=3;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0=='\\') ) {
                            alt36=1;
                        }
                        else if ( ((LA36_0>='\u0000' && LA36_0<='\t')||(LA36_0>='\u000B' && LA36_0<='!')||(LA36_0>='#' && LA36_0<='[')||(LA36_0>=']' && LA36_0<='\uFFFF')) ) {
                            alt36=2;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2652:18: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2652:22: ~ ( '\\\\' | '\\n' | '\"' )
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
                    	    break loop36;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 4 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2653:13: '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\''
                    {
                    match('\''); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2653:18: ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )*
                    loop37:
                    do {
                        int alt37=3;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0=='\\') ) {
                            alt37=1;
                        }
                        else if ( ((LA37_0>='\u0000' && LA37_0<='\t')||(LA37_0>='\u000B' && LA37_0<='&')||(LA37_0>='(' && LA37_0<='[')||(LA37_0>=']' && LA37_0<='\uFFFF')) ) {
                            alt37=2;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2653:19: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2653:23: ~ ( '\\\\' | '\\n' | '\\'' )
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
                    	    break loop37;
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:5: ( ( '\"' )? ( '\"' )? ( ESC | ~ ( '\\\\' | '\"' ) )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:7: ( '\"' )? ( '\"' )? ( ESC | ~ ( '\\\\' | '\"' ) )+
            {
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:7: ( '\"' )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0=='\"') ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:7: '\"'
                    {
                    match('\"'); 

                    }
                    break;

            }

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:12: ( '\"' )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0=='\"') ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:12: '\"'
                    {
                    match('\"'); 

                    }
                    break;

            }

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:17: ( ESC | ~ ( '\\\\' | '\"' ) )+
            int cnt41=0;
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);

                if ( (LA41_0=='\\') ) {
                    alt41=1;
                }
                else if ( ((LA41_0>='\u0000' && LA41_0<='!')||(LA41_0>='#' && LA41_0<='[')||(LA41_0>=']' && LA41_0<='\uFFFF')) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:18: ESC
            	    {
            	    mESC(); 

            	    }
            	    break;
            	case 2 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2665:22: ~ ( '\\\\' | '\"' )
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
            	    if ( cnt41 >= 1 ) break loop41;
                        EarlyExitException eee =
                            new EarlyExitException(41, input);
                        throw eee;
                }
                cnt41++;
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:5: ( ( '\\'' )? ( '\\'' )? ( ESC | ~ ( '\\\\' | '\\'' ) )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:7: ( '\\'' )? ( '\\'' )? ( ESC | ~ ( '\\\\' | '\\'' ) )+
            {
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:7: ( '\\'' )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0=='\'') ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:7: '\\''
                    {
                    match('\''); 

                    }
                    break;

            }

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:13: ( '\\'' )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0=='\'') ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:13: '\\''
                    {
                    match('\''); 

                    }
                    break;

            }

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:19: ( ESC | ~ ( '\\\\' | '\\'' ) )+
            int cnt44=0;
            loop44:
            do {
                int alt44=3;
                int LA44_0 = input.LA(1);

                if ( (LA44_0=='\\') ) {
                    alt44=1;
                }
                else if ( ((LA44_0>='\u0000' && LA44_0<='&')||(LA44_0>='(' && LA44_0<='[')||(LA44_0>=']' && LA44_0<='\uFFFF')) ) {
                    alt44=2;
                }


                switch (alt44) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:20: ESC
            	    {
            	    mESC(); 

            	    }
            	    break;
            	case 2 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2671:24: ~ ( '\\\\' | '\\'' )
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
            	    if ( cnt44 >= 1 ) break loop44;
                        EarlyExitException eee =
                            new EarlyExitException(44, input);
                        throw eee;
                }
                cnt44++;
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2676:5: ( '\\\\' . )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2676:10: '\\\\' .
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

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2684:5: ( '\\\\' ( '\\r' )? '\\n' ( ' ' | '\\t' )* ( COMMENT | nl= NEWLINE | ) )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2684:10: '\\\\' ( '\\r' )? '\\n' ( ' ' | '\\t' )* ( COMMENT | nl= NEWLINE | )
            {
            match('\\'); 
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2684:15: ( '\\r' )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0=='\r') ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2684:16: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2684:28: ( ' ' | '\\t' )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0=='\t'||LA46_0==' ') ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:
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
            	    break loop46;
                }
            } while (true);

             _channel=HIDDEN; 
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2685:10: ( COMMENT | nl= NEWLINE | )
            int alt47=3;
            int LA47_0 = input.LA(1);

            if ( (LA47_0=='\t'||LA47_0==' ') && ((startPos==0))) {
                alt47=1;
            }
            else if ( (LA47_0=='#') ) {
                alt47=1;
            }
            else if ( (LA47_0=='\n'||(LA47_0>='\f' && LA47_0<='\r')) ) {
                alt47=2;
            }
            else {
                alt47=3;}
            switch (alt47) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2685:12: COMMENT
                    {
                    mCOMMENT(); 

                    }
                    break;
                case 2 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2686:12: nl= NEWLINE
                    {
                    int nlStart2019 = getCharIndex();
                    mNEWLINE(); 
                    nl = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nlStart2019, getCharIndex()-1);

                                   emit(new CommonToken(NEWLINE,nl.getText()));
                               

                    }
                    break;
                case 3 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2691:10: 
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

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:5: ( ( ( '\\u000C' )? ( '\\r' )? '\\n' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:9: ( ( '\\u000C' )? ( '\\r' )? '\\n' )+
            {
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:9: ( ( '\\u000C' )? ( '\\r' )? '\\n' )+
            int cnt50=0;
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0=='\n'||(LA50_0>='\f' && LA50_0<='\r')) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:10: ( '\\u000C' )? ( '\\r' )? '\\n'
            	    {
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:10: ( '\\u000C' )?
            	    int alt48=2;
            	    int LA48_0 = input.LA(1);

            	    if ( (LA48_0=='\f') ) {
            	        alt48=1;
            	    }
            	    switch (alt48) {
            	        case 1 :
            	            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:11: '\\u000C'
            	            {
            	            match('\f'); 

            	            }
            	            break;

            	    }

            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:21: ( '\\r' )?
            	    int alt49=2;
            	    int LA49_0 = input.LA(1);

            	    if ( (LA49_0=='\r') ) {
            	        alt49=1;
            	    }
            	    switch (alt49) {
            	        case 1 :
            	            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2708:22: '\\r'
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
            	    if ( cnt50 >= 1 ) break loop50;
                        EarlyExitException eee =
                            new EarlyExitException(50, input);
                        throw eee;
                }
                cnt50++;
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
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2714:5: ({...}? => ( ' ' | '\\t' | '\\u000C' )+ )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2714:10: {...}? => ( ' ' | '\\t' | '\\u000C' )+
            {
            if ( !((startPos>0)) ) {
                throw new FailedPredicateException(input, "WS", "startPos>0");
            }
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2714:26: ( ' ' | '\\t' | '\\u000C' )+
            int cnt51=0;
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0=='\t'||LA51_0=='\f'||LA51_0==' ') ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:
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
            	    if ( cnt51 >= 1 ) break loop51;
                        EarlyExitException eee =
                            new EarlyExitException(51, input);
                        throw eee;
                }
                cnt51++;
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

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2728:5: ({...}? => ({...}? ( ' ' | '\\t' )+ | ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )* ) )
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2728:9: {...}? => ({...}? ( ' ' | '\\t' )+ | ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )* )
            {
            if ( !((startPos==0)) ) {
                throw new FailedPredicateException(input, "LEADING_WS", "startPos==0");
            }
            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2729:9: ({...}? ( ' ' | '\\t' )+ | ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )* )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==' ') ) {
                int LA56_1 = input.LA(2);

                if ( ((implicitLineJoiningLevel>0)) ) {
                    alt56=1;
                }
                else if ( (true) ) {
                    alt56=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 56, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA56_0=='\t') ) {
                int LA56_2 = input.LA(2);

                if ( ((implicitLineJoiningLevel>0)) ) {
                    alt56=1;
                }
                else if ( (true) ) {
                    alt56=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 56, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2729:13: {...}? ( ' ' | '\\t' )+
                    {
                    if ( !((implicitLineJoiningLevel>0)) ) {
                        throw new FailedPredicateException(input, "LEADING_WS", "implicitLineJoiningLevel>0");
                    }
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2729:43: ( ' ' | '\\t' )+
                    int cnt52=0;
                    loop52:
                    do {
                        int alt52=2;
                        int LA52_0 = input.LA(1);

                        if ( (LA52_0=='\t'||LA52_0==' ') ) {
                            alt52=1;
                        }


                        switch (alt52) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:
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
                    	    if ( cnt52 >= 1 ) break loop52;
                                EarlyExitException eee =
                                    new EarlyExitException(52, input);
                                throw eee;
                        }
                        cnt52++;
                    } while (true);

                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2730:14: ( ' ' | '\\t' )+ ( ( '\\r' )? '\\n' )*
                    {
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2730:14: ( ' ' | '\\t' )+
                    int cnt53=0;
                    loop53:
                    do {
                        int alt53=3;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==' ') ) {
                            alt53=1;
                        }
                        else if ( (LA53_0=='\t') ) {
                            alt53=2;
                        }


                        switch (alt53) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2730:20: ' '
                    	    {
                    	    match(' '); 
                    	     spaces++; 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2731:19: '\\t'
                    	    {
                    	    match('\t'); 
                    	     spaces += 8; spaces -= (spaces % 8); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt53 >= 1 ) break loop53;
                                EarlyExitException eee =
                                    new EarlyExitException(53, input);
                                throw eee;
                        }
                        cnt53++;
                    } while (true);

                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2733:14: ( ( '\\r' )? '\\n' )*
                    loop55:
                    do {
                        int alt55=2;
                        int LA55_0 = input.LA(1);

                        if ( (LA55_0=='\n'||LA55_0=='\r') ) {
                            alt55=1;
                        }


                        switch (alt55) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2733:16: ( '\\r' )? '\\n'
                    	    {
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2733:16: ( '\\r' )?
                    	    int alt54=2;
                    	    int LA54_0 = input.LA(1);

                    	    if ( (LA54_0=='\r') ) {
                    	        alt54=1;
                    	    }
                    	    switch (alt54) {
                    	        case 1 :
                    	            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2733:17: '\\r'
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
                    	    break loop55;
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

            // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2804:5: ({...}? => ( ' ' | '\\t' )* '#' (~ '\\n' )* ( '\\n' )+ | '#' (~ '\\n' )* )
            int alt61=2;
            alt61 = dfa61.predict(input);
            switch (alt61) {
                case 1 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2804:10: {...}? => ( ' ' | '\\t' )* '#' (~ '\\n' )* ( '\\n' )+
                    {
                    if ( !((startPos==0)) ) {
                        throw new FailedPredicateException(input, "COMMENT", "startPos==0");
                    }
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2804:27: ( ' ' | '\\t' )*
                    loop57:
                    do {
                        int alt57=2;
                        int LA57_0 = input.LA(1);

                        if ( (LA57_0=='\t'||LA57_0==' ') ) {
                            alt57=1;
                        }


                        switch (alt57) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:
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
                    	    break loop57;
                        }
                    } while (true);

                    match('#'); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2804:43: (~ '\\n' )*
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( ((LA58_0>='\u0000' && LA58_0<='\t')||(LA58_0>='\u000B' && LA58_0<='\uFFFF')) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2804:44: ~ '\\n'
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
                    	    break loop58;
                        }
                    } while (true);

                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2804:52: ( '\\n' )+
                    int cnt59=0;
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);

                        if ( (LA59_0=='\n') ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2804:52: '\\n'
                    	    {
                    	    match('\n'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt59 >= 1 ) break loop59;
                                EarlyExitException eee =
                                    new EarlyExitException(59, input);
                                throw eee;
                        }
                        cnt59++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2805:10: '#' (~ '\\n' )*
                    {
                    match('#'); 
                    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2805:14: (~ '\\n' )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( ((LA60_0>='\u0000' && LA60_0<='\t')||(LA60_0>='\u000B' && LA60_0<='\uFFFF')) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:2805:15: ~ '\\n'
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
                    	    break loop60;
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
        // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:8: ( AS | ASSERT | BREAK | CLASS | CONTINUE | DEF | DELETE | ELIF | EXCEPT | EXEC | FINALLY | FROM | FOR | GLOBAL | IF | IMPORT | IN | IS | LAMBDA | ORELSE | PASS | PRINT | RAISE | RETURN | TRY | WHILE | WITH | YIELD | BATCH | PERSISTIT | SQL | SIM | Neo4j | JAPI | SPARQL | OORELINSERT | OORELCOMMIT | CONNECTTO | NODEBUG | LPAREN | RPAREN | LBRACK | RBRACK | COLON | COMMA | SEMI | PLUS | MINUS | STAR | SLASH | VBAR | AMPER | LESS | GREATER | ASSIGN | PERCENT | BACKQUOTE | LCURLY | RCURLY | CIRCUMFLEX | TILDE | EQUAL | NOTEQUAL | ALT_NOTEQUAL | LESSEQUAL | LEFTSHIFT | GREATEREQUAL | RIGHTSHIFT | PLUSEQUAL | MINUSEQUAL | DOUBLESTAR | STAREQUAL | DOUBLESLASH | SLASHEQUAL | VBAREQUAL | PERCENTEQUAL | AMPEREQUAL | CIRCUMFLEXEQUAL | LEFTSHIFTEQUAL | RIGHTSHIFTEQUAL | DOUBLESTAREQUAL | DOUBLESLASHEQUAL | DOT | AT | AND | OR | NOT | FLOAT | LONGINT | INT | COMPLEX | NAME | STRING | CONTINUED_LINE | NEWLINE | WS | LEADING_WS | COMMENT )
        int alt62=98;
        alt62 = dfa62.predict(input);
        switch (alt62) {
            case 1 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:10: AS
                {
                mAS(); 

                }
                break;
            case 2 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:13: ASSERT
                {
                mASSERT(); 

                }
                break;
            case 3 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:20: BREAK
                {
                mBREAK(); 

                }
                break;
            case 4 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:26: CLASS
                {
                mCLASS(); 

                }
                break;
            case 5 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:32: CONTINUE
                {
                mCONTINUE(); 

                }
                break;
            case 6 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:41: DEF
                {
                mDEF(); 

                }
                break;
            case 7 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:45: DELETE
                {
                mDELETE(); 

                }
                break;
            case 8 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:52: ELIF
                {
                mELIF(); 

                }
                break;
            case 9 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:57: EXCEPT
                {
                mEXCEPT(); 

                }
                break;
            case 10 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:64: EXEC
                {
                mEXEC(); 

                }
                break;
            case 11 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:69: FINALLY
                {
                mFINALLY(); 

                }
                break;
            case 12 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:77: FROM
                {
                mFROM(); 

                }
                break;
            case 13 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:82: FOR
                {
                mFOR(); 

                }
                break;
            case 14 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:86: GLOBAL
                {
                mGLOBAL(); 

                }
                break;
            case 15 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:93: IF
                {
                mIF(); 

                }
                break;
            case 16 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:96: IMPORT
                {
                mIMPORT(); 

                }
                break;
            case 17 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:103: IN
                {
                mIN(); 

                }
                break;
            case 18 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:106: IS
                {
                mIS(); 

                }
                break;
            case 19 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:109: LAMBDA
                {
                mLAMBDA(); 

                }
                break;
            case 20 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:116: ORELSE
                {
                mORELSE(); 

                }
                break;
            case 21 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:123: PASS
                {
                mPASS(); 

                }
                break;
            case 22 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:128: PRINT
                {
                mPRINT(); 

                }
                break;
            case 23 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:134: RAISE
                {
                mRAISE(); 

                }
                break;
            case 24 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:140: RETURN
                {
                mRETURN(); 

                }
                break;
            case 25 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:147: TRY
                {
                mTRY(); 

                }
                break;
            case 26 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:151: WHILE
                {
                mWHILE(); 

                }
                break;
            case 27 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:157: WITH
                {
                mWITH(); 

                }
                break;
            case 28 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:162: YIELD
                {
                mYIELD(); 

                }
                break;
            case 29 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:168: BATCH
                {
                mBATCH(); 

                }
                break;
            case 30 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:174: PERSISTIT
                {
                mPERSISTIT(); 

                }
                break;
            case 31 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:184: SQL
                {
                mSQL(); 

                }
                break;
            case 32 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:188: SIM
                {
                mSIM(); 

                }
                break;
            case 33 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:192: Neo4j
                {
                mNeo4j(); 

                }
                break;
            case 34 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:198: JAPI
                {
                mJAPI(); 

                }
                break;
            case 35 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:203: SPARQL
                {
                mSPARQL(); 

                }
                break;
            case 36 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:210: OORELINSERT
                {
                mOORELINSERT(); 

                }
                break;
            case 37 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:222: OORELCOMMIT
                {
                mOORELCOMMIT(); 

                }
                break;
            case 38 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:234: CONNECTTO
                {
                mCONNECTTO(); 

                }
                break;
            case 39 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:244: NODEBUG
                {
                mNODEBUG(); 

                }
                break;
            case 40 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:252: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 41 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:259: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 42 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:266: LBRACK
                {
                mLBRACK(); 

                }
                break;
            case 43 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:273: RBRACK
                {
                mRBRACK(); 

                }
                break;
            case 44 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:280: COLON
                {
                mCOLON(); 

                }
                break;
            case 45 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:286: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 46 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:292: SEMI
                {
                mSEMI(); 

                }
                break;
            case 47 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:297: PLUS
                {
                mPLUS(); 

                }
                break;
            case 48 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:302: MINUS
                {
                mMINUS(); 

                }
                break;
            case 49 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:308: STAR
                {
                mSTAR(); 

                }
                break;
            case 50 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:313: SLASH
                {
                mSLASH(); 

                }
                break;
            case 51 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:319: VBAR
                {
                mVBAR(); 

                }
                break;
            case 52 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:324: AMPER
                {
                mAMPER(); 

                }
                break;
            case 53 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:330: LESS
                {
                mLESS(); 

                }
                break;
            case 54 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:335: GREATER
                {
                mGREATER(); 

                }
                break;
            case 55 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:343: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 56 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:350: PERCENT
                {
                mPERCENT(); 

                }
                break;
            case 57 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:358: BACKQUOTE
                {
                mBACKQUOTE(); 

                }
                break;
            case 58 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:368: LCURLY
                {
                mLCURLY(); 

                }
                break;
            case 59 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:375: RCURLY
                {
                mRCURLY(); 

                }
                break;
            case 60 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:382: CIRCUMFLEX
                {
                mCIRCUMFLEX(); 

                }
                break;
            case 61 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:393: TILDE
                {
                mTILDE(); 

                }
                break;
            case 62 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:399: EQUAL
                {
                mEQUAL(); 

                }
                break;
            case 63 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:405: NOTEQUAL
                {
                mNOTEQUAL(); 

                }
                break;
            case 64 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:414: ALT_NOTEQUAL
                {
                mALT_NOTEQUAL(); 

                }
                break;
            case 65 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:427: LESSEQUAL
                {
                mLESSEQUAL(); 

                }
                break;
            case 66 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:437: LEFTSHIFT
                {
                mLEFTSHIFT(); 

                }
                break;
            case 67 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:447: GREATEREQUAL
                {
                mGREATEREQUAL(); 

                }
                break;
            case 68 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:460: RIGHTSHIFT
                {
                mRIGHTSHIFT(); 

                }
                break;
            case 69 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:471: PLUSEQUAL
                {
                mPLUSEQUAL(); 

                }
                break;
            case 70 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:481: MINUSEQUAL
                {
                mMINUSEQUAL(); 

                }
                break;
            case 71 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:492: DOUBLESTAR
                {
                mDOUBLESTAR(); 

                }
                break;
            case 72 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:503: STAREQUAL
                {
                mSTAREQUAL(); 

                }
                break;
            case 73 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:513: DOUBLESLASH
                {
                mDOUBLESLASH(); 

                }
                break;
            case 74 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:525: SLASHEQUAL
                {
                mSLASHEQUAL(); 

                }
                break;
            case 75 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:536: VBAREQUAL
                {
                mVBAREQUAL(); 

                }
                break;
            case 76 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:546: PERCENTEQUAL
                {
                mPERCENTEQUAL(); 

                }
                break;
            case 77 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:559: AMPEREQUAL
                {
                mAMPEREQUAL(); 

                }
                break;
            case 78 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:570: CIRCUMFLEXEQUAL
                {
                mCIRCUMFLEXEQUAL(); 

                }
                break;
            case 79 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:586: LEFTSHIFTEQUAL
                {
                mLEFTSHIFTEQUAL(); 

                }
                break;
            case 80 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:601: RIGHTSHIFTEQUAL
                {
                mRIGHTSHIFTEQUAL(); 

                }
                break;
            case 81 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:617: DOUBLESTAREQUAL
                {
                mDOUBLESTAREQUAL(); 

                }
                break;
            case 82 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:633: DOUBLESLASHEQUAL
                {
                mDOUBLESLASHEQUAL(); 

                }
                break;
            case 83 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:650: DOT
                {
                mDOT(); 

                }
                break;
            case 84 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:654: AT
                {
                mAT(); 

                }
                break;
            case 85 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:657: AND
                {
                mAND(); 

                }
                break;
            case 86 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:661: OR
                {
                mOR(); 

                }
                break;
            case 87 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:664: NOT
                {
                mNOT(); 

                }
                break;
            case 88 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:668: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 89 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:674: LONGINT
                {
                mLONGINT(); 

                }
                break;
            case 90 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:682: INT
                {
                mINT(); 

                }
                break;
            case 91 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:686: COMPLEX
                {
                mCOMPLEX(); 

                }
                break;
            case 92 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:694: NAME
                {
                mNAME(); 

                }
                break;
            case 93 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:699: STRING
                {
                mSTRING(); 

                }
                break;
            case 94 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:706: CONTINUED_LINE
                {
                mCONTINUED_LINE(); 

                }
                break;
            case 95 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:721: NEWLINE
                {
                mNEWLINE(); 

                }
                break;
            case 96 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:729: WS
                {
                mWS(); 

                }
                break;
            case 97 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:732: LEADING_WS
                {
                mLEADING_WS(); 

                }
                break;
            case 98 :
                // /home/noel/GithubRepos/CarnotKE/jyhton/grammar/Python.g:1:743: COMMENT
                {
                mCOMMENT(); 

                }
                break;

        }

    }


    protected DFA21 dfa21 = new DFA21(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA62 dfa62 = new DFA62(this);
    static final String DFA21_eotS =
        "\3\uffff\1\4\2\uffff";
    static final String DFA21_eofS =
        "\6\uffff";
    static final String DFA21_minS =
        "\1\56\1\uffff\1\56\1\105\2\uffff";
    static final String DFA21_maxS =
        "\1\71\1\uffff\2\145\2\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\1\2\uffff\1\3\1\2";
    static final String DFA21_specialS =
        "\6\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\1\1\uffff\12\2",
            "",
            "\1\3\1\uffff\12\2\13\uffff\1\4\37\uffff\1\4",
            "\1\5\37\uffff\1\5",
            "",
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "2606:1: FLOAT : ( '.' DIGITS ( Exponent )? | DIGITS '.' Exponent | DIGITS ( '.' ( DIGITS ( Exponent )? )? | Exponent ) );";
        }
    }
    static final String DFA30_eotS =
        "\4\uffff";
    static final String DFA30_eofS =
        "\4\uffff";
    static final String DFA30_minS =
        "\2\56\2\uffff";
    static final String DFA30_maxS =
        "\1\71\1\152\2\uffff";
    static final String DFA30_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA30_specialS =
        "\4\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\2\1\uffff\12\1\13\uffff\1\2\4\uffff\1\3\32\uffff\1\2\4\uffff"+
            "\1\3",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "2632:1: COMPLEX : ( ( DIGITS )+ ( 'j' | 'J' ) | FLOAT ( 'j' | 'J' ) );";
        }
    }
    static final String DFA33_eotS =
        "\24\uffff";
    static final String DFA33_eofS =
        "\24\uffff";
    static final String DFA33_minS =
        "\1\42\1\uffff\2\42\1\uffff\2\42\15\uffff";
    static final String DFA33_maxS =
        "\1\165\1\uffff\2\162\1\uffff\2\162\15\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\1\2\uffff\1\6\2\uffff\1\17\1\4\1\13\1\2\1\5\1\16\1\3"+
        "\1\11\1\14\1\7\1\12\1\15\1\10";
    static final String DFA33_specialS =
        "\24\uffff}>";
    static final String[] DFA33_transitionS = {
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

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "2649:9: ( 'r' | 'u' | 'b' | 'ur' | 'br' | 'R' | 'U' | 'B' | 'UR' | 'BR' | 'uR' | 'Ur' | 'Br' | 'bR' )?";
        }
    }
    static final String DFA61_eotS =
        "\2\uffff\2\4\1\uffff";
    static final String DFA61_eofS =
        "\5\uffff";
    static final String DFA61_minS =
        "\1\11\1\uffff\2\0\1\uffff";
    static final String DFA61_maxS =
        "\1\43\1\uffff\2\uffff\1\uffff";
    static final String DFA61_acceptS =
        "\1\uffff\1\1\2\uffff\1\2";
    static final String DFA61_specialS =
        "\1\2\1\uffff\1\0\1\1\1\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\1\26\uffff\1\1\2\uffff\1\2",
            "",
            "\12\3\1\1\ufff5\3",
            "\12\3\1\1\ufff5\3",
            ""
    };

    static final short[] DFA61_eot = DFA.unpackEncodedString(DFA61_eotS);
    static final short[] DFA61_eof = DFA.unpackEncodedString(DFA61_eofS);
    static final char[] DFA61_min = DFA.unpackEncodedStringToUnsignedChars(DFA61_minS);
    static final char[] DFA61_max = DFA.unpackEncodedStringToUnsignedChars(DFA61_maxS);
    static final short[] DFA61_accept = DFA.unpackEncodedString(DFA61_acceptS);
    static final short[] DFA61_special = DFA.unpackEncodedString(DFA61_specialS);
    static final short[][] DFA61_transition;

    static {
        int numStates = DFA61_transitionS.length;
        DFA61_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA61_transition[i] = DFA.unpackEncodedString(DFA61_transitionS[i]);
        }
    }

    class DFA61 extends DFA {

        public DFA61(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 61;
            this.eot = DFA61_eot;
            this.eof = DFA61_eof;
            this.min = DFA61_min;
            this.max = DFA61_max;
            this.accept = DFA61_accept;
            this.special = DFA61_special;
            this.transition = DFA61_transition;
        }
        public String getDescription() {
            return "2783:1: COMMENT : ({...}? => ( ' ' | '\\t' )* '#' (~ '\\n' )* ( '\\n' )+ | '#' (~ '\\n' )* );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA61_2 = input.LA(1);

                         
                        int index61_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA61_2>='\u0000' && LA61_2<='\t')||(LA61_2>='\u000B' && LA61_2<='\uFFFF')) ) {s = 3;}

                        else if ( (LA61_2=='\n') && ((startPos==0))) {s = 1;}

                        else s = 4;

                         
                        input.seek(index61_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA61_3 = input.LA(1);

                         
                        int index61_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA61_3>='\u0000' && LA61_3<='\t')||(LA61_3>='\u000B' && LA61_3<='\uFFFF')) ) {s = 3;}

                        else if ( (LA61_3=='\n') && ((startPos==0))) {s = 1;}

                        else s = 4;

                         
                        input.seek(index61_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA61_0 = input.LA(1);

                         
                        int index61_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA61_0=='\t'||LA61_0==' ') && ((startPos==0))) {s = 1;}

                        else if ( (LA61_0=='#') ) {s = 2;}

                         
                        input.seek(index61_0);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 61, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA62_eotS =
        "\1\uffff\23\64\7\uffff\1\137\1\141\1\144\1\147\1\151\1\153\1\157"+
        "\1\162\1\164\1\166\3\uffff\1\170\2\uffff\1\172\1\uffff\1\64\2\177"+
        "\4\64\3\uffff\1\u008d\1\uffff\1\u008e\1\u0091\1\uffff\1\u0093\14"+
        "\64\1\u00a2\1\64\1\u00a4\1\u00a5\21\64\4\uffff\1\u00ba\2\uffff\1"+
        "\u00bc\10\uffff\1\u00be\2\uffff\1\u00c0\7\uffff\1\u00c1\1\uffff"+
        "\1\u00c3\1\uffff\2\177\1\uffff\1\u00c1\1\177\4\uffff\1\177\6\64"+
        "\5\uffff\1\64\1\uffff\1\u00cc\3\64\1\u00d1\1\u00d2\6\64\1\u00d9"+
        "\1\64\1\uffff\1\64\2\uffff\7\64\1\u00e4\12\64\1\u00ef\13\uffff\3"+
        "\177\1\uffff\1\u00c1\1\uffff\1\u00c1\1\64\1\uffff\4\64\2\uffff\1"+
        "\u00fa\1\u00fb\1\64\1\u00fd\1\64\1\u00ff\1\uffff\3\64\1\u0103\6"+
        "\64\1\uffff\1\64\1\u010b\2\64\2\uffff\4\64\2\uffff\1\u00c1\1\uffff"+
        "\1\u00c1\1\uffff\1\64\1\u0115\1\u0116\2\64\2\uffff\1\64\1\uffff"+
        "\1\64\1\uffff\3\64\1\uffff\1\u011e\1\64\1\u0120\3\64\1\u0124\1\uffff"+
        "\1\u0125\3\64\1\uffff\1\64\1\uffff\1\u00c1\1\u012a\2\uffff\2\64"+
        "\1\u012d\1\64\1\u012f\1\u0130\1\u0131\1\uffff\1\64\1\uffff\1\u0133"+
        "\2\64\2\uffff\2\64\1\uffff\1\64\1\uffff\2\64\1\uffff\1\u013b\3\uffff"+
        "\1\64\1\uffff\3\64\1\uffff\1\u0140\1\u0141\1\64\2\uffff\3\64\2\uffff"+
        "\1\u0146\2\64\1\u0149\4\uffff";
    static final String DFA62_eofS =
        "\u014a\uffff";
    static final String DFA62_minS =
        "\1\11\1\156\1\42\1\154\1\145\1\154\1\151\1\154\1\146\2\141\1\42"+
        "\1\162\1\150\1\151\1\171\1\111\1\145\1\101\1\157\7\uffff\2\75\1"+
        "\52\1\57\2\75\1\74\3\75\3\uffff\1\75\2\uffff\1\60\1\uffff\1\162"+
        "\2\56\4\42\3\uffff\1\12\1\uffff\2\11\1\uffff\1\60\1\144\2\42\1\141"+
        "\1\156\1\146\1\151\1\143\1\156\1\157\1\162\1\157\1\60\1\160\2\60"+
        "\1\155\1\163\1\151\1\162\1\151\1\154\1\171\1\151\1\164\1\145\1\142"+
        "\1\114\1\115\1\101\1\157\1\120\1\144\4\uffff\1\75\2\uffff\1\75\10"+
        "\uffff\1\75\2\uffff\1\75\7\uffff\1\60\1\uffff\4\60\1\uffff\1\60"+
        "\1\56\1\53\1\uffff\1\56\1\uffff\1\56\6\42\1\uffff\1\0\2\uffff\1"+
        "\0\1\145\1\uffff\1\60\1\141\1\163\1\156\2\60\1\146\2\145\1\143\1"+
        "\141\1\155\1\60\1\142\1\uffff\1\157\2\uffff\1\142\1\163\1\156\2"+
        "\163\1\165\1\103\1\60\1\154\1\150\1\154\1\141\2\40\1\122\1\64\1"+
        "\111\1\145\1\60\11\uffff\1\53\1\uffff\3\60\1\53\3\60\1\162\1\uffff"+
        "\1\153\1\163\1\151\1\145\2\uffff\2\60\1\160\1\60\1\154\1\60\1\uffff"+
        "\1\141\1\162\1\144\1\60\1\164\1\151\1\145\1\162\1\156\1\157\1\uffff"+
        "\1\145\1\60\1\144\1\164\2\uffff\1\121\1\152\1\40\1\142\1\uffff\4"+
        "\60\1\53\1\164\2\60\1\156\1\143\2\uffff\1\164\1\uffff\1\154\1\uffff"+
        "\1\154\1\164\1\141\1\uffff\1\60\1\163\1\60\1\156\1\163\1\155\1\60"+
        "\1\uffff\1\60\1\143\1\114\1\40\1\uffff\1\165\3\60\2\uffff\1\165"+
        "\1\164\1\60\1\171\3\60\1\uffff\1\164\1\uffff\1\60\1\145\1\155\2"+
        "\uffff\1\150\1\40\1\uffff\1\147\1\uffff\1\145\1\124\1\uffff\1\60"+
        "\3\uffff\1\40\1\uffff\1\162\1\151\1\145\1\uffff\2\60\1\157\2\uffff"+
        "\2\164\1\163\2\uffff\1\60\2\40\1\60\4\uffff";
    static final String DFA62_maxS =
        "\1\176\1\163\1\162\1\157\1\145\1\170\1\162\1\154\1\163\1\141\1\162"+
        "\1\145\1\162\2\151\1\171\1\121\1\145\1\101\1\157\7\uffff\6\75\2"+
        "\76\2\75\3\uffff\1\75\2\uffff\1\71\1\uffff\1\162\1\170\1\154\1\162"+
        "\1\47\2\162\3\uffff\1\15\1\uffff\2\43\1\uffff\1\172\1\144\1\145"+
        "\1\47\1\141\1\156\1\154\1\163\1\145\1\156\1\157\1\162\1\157\1\172"+
        "\1\160\2\172\1\155\1\163\1\151\1\162\1\151\1\164\1\171\1\151\1\164"+
        "\1\145\1\142\1\114\1\115\1\101\1\157\1\120\1\164\4\uffff\1\75\2"+
        "\uffff\1\75\10\uffff\1\75\2\uffff\1\75\7\uffff\1\152\1\uffff\1\172"+
        "\1\146\2\154\1\uffff\1\152\1\154\1\71\1\uffff\1\152\1\uffff\1\154"+
        "\6\47\1\uffff\1\0\2\uffff\1\0\1\145\1\uffff\1\172\1\141\1\163\1"+
        "\164\2\172\1\146\2\145\1\143\1\141\1\155\1\172\1\142\1\uffff\1\157"+
        "\2\uffff\1\142\1\163\1\156\2\163\1\165\1\111\1\172\1\154\1\150\1"+
        "\154\1\141\2\40\1\122\1\64\1\111\1\145\1\172\11\uffff\1\71\1\uffff"+
        "\3\154\1\71\1\152\1\71\1\152\1\162\1\uffff\1\153\1\163\1\151\1\145"+
        "\2\uffff\2\172\1\160\1\172\1\154\1\172\1\uffff\1\141\1\162\1\144"+
        "\1\172\1\164\1\151\1\145\1\162\1\156\1\157\1\uffff\1\145\1\172\1"+
        "\144\1\164\2\uffff\1\121\1\152\1\40\1\142\1\uffff\1\71\1\152\1\71"+
        "\1\152\1\71\1\164\2\172\1\156\1\143\2\uffff\1\164\1\uffff\1\154"+
        "\1\uffff\1\154\1\164\1\141\1\uffff\1\172\1\163\1\172\1\156\1\163"+
        "\1\155\1\172\1\uffff\1\172\1\143\1\114\1\40\1\uffff\1\165\1\71\1"+
        "\152\1\172\2\uffff\1\165\1\164\1\172\1\171\3\172\1\uffff\1\164\1"+
        "\uffff\1\172\1\145\1\155\2\uffff\1\150\1\40\1\uffff\1\147\1\uffff"+
        "\1\145\1\124\1\uffff\1\172\3\uffff\1\40\1\uffff\1\162\1\151\1\145"+
        "\1\uffff\2\172\1\157\2\uffff\2\164\1\163\2\uffff\1\172\2\40\1\172"+
        "\4\uffff";
    static final String DFA62_acceptS =
        "\24\uffff\1\50\1\51\1\52\1\53\1\54\1\55\1\56\12\uffff\1\71\1\72"+
        "\1\73\1\uffff\1\75\1\77\1\uffff\1\124\7\uffff\1\134\1\135\1\136"+
        "\1\uffff\1\137\2\uffff\1\142\42\uffff\1\105\1\57\1\106\1\60\1\uffff"+
        "\1\110\1\61\1\uffff\1\112\1\62\1\113\1\63\1\115\1\64\1\100\1\101"+
        "\1\uffff\1\65\1\103\1\uffff\1\66\1\76\1\67\1\114\1\70\1\116\1\74"+
        "\1\uffff\1\123\4\uffff\1\132\3\uffff\1\133\1\uffff\1\131\7\uffff"+
        "\1\140\1\uffff\1\141\1\142\2\uffff\1\1\16\uffff\1\17\1\uffff\1\21"+
        "\1\22\23\uffff\1\121\1\107\1\122\1\111\1\117\1\102\1\120\1\104\1"+
        "\130\1\uffff\1\126\10\uffff\1\125\4\uffff\1\6\1\7\6\uffff\1\15\12"+
        "\uffff\1\31\4\uffff\1\37\1\40\4\uffff\1\127\12\uffff\1\10\1\24\1"+
        "\uffff\1\12\1\uffff\1\14\3\uffff\1\25\7\uffff\1\33\4\uffff\1\42"+
        "\4\uffff\1\3\1\4\7\uffff\1\26\1\uffff\1\27\3\uffff\1\32\1\34\2\uffff"+
        "\1\41\1\uffff\1\2\2\uffff\1\11\1\uffff\1\16\1\20\1\23\1\uffff\1"+
        "\30\3\uffff\1\43\3\uffff\1\13\1\36\3\uffff\1\47\1\5\4\uffff\1\46"+
        "\1\44\1\45\1\35";
    static final String DFA62_specialS =
        "\1\1\66\uffff\1\0\1\uffff\1\2\1\3\123\uffff\1\4\2\uffff\1\5\u00b8"+
        "\uffff}>";
    static final String[] DFA62_transitionS = {
            "\1\72\1\70\1\uffff\1\67\1\70\22\uffff\1\71\1\52\1\65\1\73\1"+
            "\uffff\1\44\1\40\1\65\1\24\1\25\1\35\1\33\1\31\1\34\1\53\1\36"+
            "\1\56\11\57\1\30\1\32\1\41\1\43\1\42\1\uffff\1\54\1\64\1\63"+
            "\7\64\1\22\3\64\1\21\3\64\1\61\1\20\1\64\1\62\5\64\1\26\1\66"+
            "\1\27\1\50\1\64\1\45\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\64\1\10\2"+
            "\64\1\11\1\17\1\23\1\55\1\12\1\64\1\13\1\64\1\14\1\60\1\64\1"+
            "\15\1\64\1\16\1\64\1\46\1\37\1\47\1\51",
            "\1\75\4\uffff\1\74",
            "\1\65\4\uffff\1\65\52\uffff\1\77\37\uffff\1\76",
            "\1\100\2\uffff\1\101",
            "\1\102",
            "\1\103\13\uffff\1\104",
            "\1\105\5\uffff\1\107\2\uffff\1\106",
            "\1\110",
            "\1\111\6\uffff\1\112\1\113\4\uffff\1\114",
            "\1\115",
            "\1\116\3\uffff\1\120\14\uffff\1\117",
            "\1\65\4\uffff\1\65\71\uffff\1\121\3\uffff\1\122",
            "\1\123",
            "\1\124\1\125",
            "\1\126",
            "\1\127",
            "\1\131\6\uffff\1\132\1\130",
            "\1\133",
            "\1\134",
            "\1\135",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\136",
            "\1\140",
            "\1\142\22\uffff\1\143",
            "\1\145\15\uffff\1\146",
            "\1\150",
            "\1\152",
            "\1\156\1\155\1\154",
            "\1\160\1\161",
            "\1\163",
            "\1\165",
            "",
            "",
            "",
            "\1\167",
            "",
            "",
            "\12\171",
            "",
            "\1\173",
            "\1\u0080\1\uffff\10\u0081\2\u0084\10\uffff\1\176\2\uffff\1"+
            "\u0082\4\uffff\1\u0083\1\uffff\1\u0085\2\uffff\1\175\10\uffff"+
            "\1\174\11\uffff\1\176\2\uffff\1\u0082\4\uffff\1\u0083\1\uffff"+
            "\1\u0085\2\uffff\1\175\10\uffff\1\174",
            "\1\u0080\1\uffff\12\u0086\13\uffff\1\u0082\4\uffff\1\u0083"+
            "\1\uffff\1\u0085\30\uffff\1\u0082\4\uffff\1\u0083\1\uffff\1"+
            "\u0085",
            "\1\65\4\uffff\1\65\52\uffff\1\u0088\37\uffff\1\u0087",
            "\1\65\4\uffff\1\65",
            "\1\65\4\uffff\1\65\52\uffff\1\u0089\37\uffff\1\u008a",
            "\1\65\4\uffff\1\65\52\uffff\1\u008b\37\uffff\1\u008c",
            "",
            "",
            "",
            "\1\70\2\uffff\1\70",
            "",
            "\1\72\1\u008f\1\uffff\1\u008d\1\u008f\22\uffff\1\71\2\uffff"+
            "\1\u0090",
            "\1\72\1\u008f\1\uffff\1\u008d\1\u008f\22\uffff\1\71\2\uffff"+
            "\1\u0090",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\22\64\1\u0092\7\64",
            "\1\u0094",
            "\1\65\4\uffff\1\65\75\uffff\1\u0095",
            "\1\65\4\uffff\1\65",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098\5\uffff\1\u0099",
            "\1\u009a\11\uffff\1\u009b",
            "\1\u009c\1\uffff\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00a3",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ac\7\uffff\1\u00ab",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7\17\uffff\1\u00b8",
            "",
            "",
            "",
            "",
            "\1\u00b9",
            "",
            "",
            "\1\u00bb",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00bd",
            "",
            "",
            "\1\u00bf",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\171\13\uffff\1\u00c2\4\uffff\1\u0083\32\uffff\1\u00c2\4"+
            "\uffff\1\u0083",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\u00c4\7\uffff\6\u00c4\32\uffff\6\u00c4",
            "\10\u00c5\24\uffff\1\u0085\37\uffff\1\u0085",
            "\2\u00c6\32\uffff\1\u0085\37\uffff\1\u0085",
            "",
            "\12\u00c8\13\uffff\1\u00c7\4\uffff\1\u0083\32\uffff\1\u00c7"+
            "\4\uffff\1\u0083",
            "\1\u0080\1\uffff\10\u0081\2\u0084\13\uffff\1\u0082\4\uffff"+
            "\1\u0083\1\uffff\1\u0085\30\uffff\1\u0082\4\uffff\1\u0083\1"+
            "\uffff\1\u0085",
            "\1\u00c9\1\uffff\1\u00c9\2\uffff\12\u00ca",
            "",
            "\1\u0080\1\uffff\12\u0084\13\uffff\1\u0082\4\uffff\1\u0083"+
            "\32\uffff\1\u0082\4\uffff\1\u0083",
            "",
            "\1\u0080\1\uffff\12\u0086\13\uffff\1\u0082\4\uffff\1\u0083"+
            "\1\uffff\1\u0085\30\uffff\1\u0082\4\uffff\1\u0083\1\uffff\1"+
            "\u0085",
            "\1\65\4\uffff\1\65",
            "\1\65\4\uffff\1\65",
            "\1\65\4\uffff\1\65",
            "\1\65\4\uffff\1\65",
            "\1\65\4\uffff\1\65",
            "\1\65\4\uffff\1\65",
            "",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "\1\u00cb",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00cd",
            "\1\u00ce",
            "\1\u00d0\5\uffff\1\u00cf",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00da",
            "",
            "\1\u00db",
            "",
            "",
            "\1\u00dc",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "\1\u00e3\5\uffff\1\u00e2",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00e5",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "\1\u00e9",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "\1\u00ee",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00f0\1\uffff\1\u00f0\2\uffff\12\u00f1",
            "",
            "\12\u00c4\7\uffff\6\u00c4\5\uffff\1\u0085\24\uffff\6\u00c4"+
            "\5\uffff\1\u0085",
            "\10\u00c5\24\uffff\1\u0085\37\uffff\1\u0085",
            "\2\u00c6\32\uffff\1\u0085\37\uffff\1\u0085",
            "\1\u00f2\1\uffff\1\u00f2\2\uffff\12\u00f3",
            "\12\u00c8\13\uffff\1\u00f4\4\uffff\1\u0083\32\uffff\1\u00f4"+
            "\4\uffff\1\u0083",
            "\12\u00ca",
            "\12\u00ca\20\uffff\1\u0083\37\uffff\1\u0083",
            "\1\u00f5",
            "",
            "\1\u00f6",
            "\1\u00f7",
            "\1\u00f8",
            "\1\u00f9",
            "",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00fc",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u00fe",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u0109",
            "",
            "\1\u010a",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u010c",
            "\1\u010d",
            "",
            "",
            "\1\u010e",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "",
            "\12\u00f1",
            "\12\u00f1\20\uffff\1\u0083\37\uffff\1\u0083",
            "\12\u00f3",
            "\12\u00f3\20\uffff\1\u0083\37\uffff\1\u0083",
            "\1\u0112\1\uffff\1\u0112\2\uffff\12\u0113",
            "\1\u0114",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u0117",
            "\1\u0118",
            "",
            "",
            "\1\u0119",
            "",
            "\1\u011a",
            "",
            "\1\u011b",
            "\1\u011c",
            "\1\u011d",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u011f",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u0121",
            "\1\u0122",
            "\1\u0123",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u0126",
            "\1\u0127",
            "\1\u0128",
            "",
            "\1\u0129",
            "\12\u0113",
            "\12\u0113\20\uffff\1\u0083\37\uffff\1\u0083",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "",
            "",
            "\1\u012b",
            "\1\u012c",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u012e",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "",
            "\1\u0132",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u0134",
            "\1\u0135",
            "",
            "",
            "\1\u0136",
            "\1\u0137",
            "",
            "\1\u0138",
            "",
            "\1\u0139",
            "\1\u013a",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "",
            "",
            "",
            "\1\u013c",
            "",
            "\1\u013d",
            "\1\u013e",
            "\1\u013f",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u0142",
            "",
            "",
            "\1\u0143",
            "\1\u0144",
            "\1\u0145",
            "",
            "",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "\1\u0147",
            "\1\u0148",
            "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA62_eot = DFA.unpackEncodedString(DFA62_eotS);
    static final short[] DFA62_eof = DFA.unpackEncodedString(DFA62_eofS);
    static final char[] DFA62_min = DFA.unpackEncodedStringToUnsignedChars(DFA62_minS);
    static final char[] DFA62_max = DFA.unpackEncodedStringToUnsignedChars(DFA62_maxS);
    static final short[] DFA62_accept = DFA.unpackEncodedString(DFA62_acceptS);
    static final short[] DFA62_special = DFA.unpackEncodedString(DFA62_specialS);
    static final short[][] DFA62_transition;

    static {
        int numStates = DFA62_transitionS.length;
        DFA62_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA62_transition[i] = DFA.unpackEncodedString(DFA62_transitionS[i]);
        }
    }

    class DFA62 extends DFA {

        public DFA62(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 62;
            this.eot = DFA62_eot;
            this.eof = DFA62_eof;
            this.min = DFA62_min;
            this.max = DFA62_max;
            this.accept = DFA62_accept;
            this.special = DFA62_special;
            this.transition = DFA62_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( AS | ASSERT | BREAK | CLASS | CONTINUE | DEF | DELETE | ELIF | EXCEPT | EXEC | FINALLY | FROM | FOR | GLOBAL | IF | IMPORT | IN | IS | LAMBDA | ORELSE | PASS | PRINT | RAISE | RETURN | TRY | WHILE | WITH | YIELD | BATCH | PERSISTIT | SQL | SIM | Neo4j | JAPI | SPARQL | OORELINSERT | OORELCOMMIT | CONNECTTO | NODEBUG | LPAREN | RPAREN | LBRACK | RBRACK | COLON | COMMA | SEMI | PLUS | MINUS | STAR | SLASH | VBAR | AMPER | LESS | GREATER | ASSIGN | PERCENT | BACKQUOTE | LCURLY | RCURLY | CIRCUMFLEX | TILDE | EQUAL | NOTEQUAL | ALT_NOTEQUAL | LESSEQUAL | LEFTSHIFT | GREATEREQUAL | RIGHTSHIFT | PLUSEQUAL | MINUSEQUAL | DOUBLESTAR | STAREQUAL | DOUBLESLASH | SLASHEQUAL | VBAREQUAL | PERCENTEQUAL | AMPEREQUAL | CIRCUMFLEXEQUAL | LEFTSHIFTEQUAL | RIGHTSHIFTEQUAL | DOUBLESTAREQUAL | DOUBLESLASHEQUAL | DOT | AT | AND | OR | NOT | FLOAT | LONGINT | INT | COMPLEX | NAME | STRING | CONTINUED_LINE | NEWLINE | WS | LEADING_WS | COMMENT );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA62_55 = input.LA(1);

                         
                        int index62_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA62_55=='\n'||LA62_55=='\r') ) {s = 56;}

                        else s = 141;

                         
                        input.seek(index62_55);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA62_0 = input.LA(1);

                         
                        int index62_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA62_0=='a') ) {s = 1;}

                        else if ( (LA62_0=='b') ) {s = 2;}

                        else if ( (LA62_0=='c') ) {s = 3;}

                        else if ( (LA62_0=='d') ) {s = 4;}

                        else if ( (LA62_0=='e') ) {s = 5;}

                        else if ( (LA62_0=='f') ) {s = 6;}

                        else if ( (LA62_0=='g') ) {s = 7;}

                        else if ( (LA62_0=='i') ) {s = 8;}

                        else if ( (LA62_0=='l') ) {s = 9;}

                        else if ( (LA62_0=='p') ) {s = 10;}

                        else if ( (LA62_0=='r') ) {s = 11;}

                        else if ( (LA62_0=='t') ) {s = 12;}

                        else if ( (LA62_0=='w') ) {s = 13;}

                        else if ( (LA62_0=='y') ) {s = 14;}

                        else if ( (LA62_0=='m') ) {s = 15;}

                        else if ( (LA62_0=='S') ) {s = 16;}

                        else if ( (LA62_0=='N') ) {s = 17;}

                        else if ( (LA62_0=='J') ) {s = 18;}

                        else if ( (LA62_0=='n') ) {s = 19;}

                        else if ( (LA62_0=='(') ) {s = 20;}

                        else if ( (LA62_0==')') ) {s = 21;}

                        else if ( (LA62_0=='[') ) {s = 22;}

                        else if ( (LA62_0==']') ) {s = 23;}

                        else if ( (LA62_0==':') ) {s = 24;}

                        else if ( (LA62_0==',') ) {s = 25;}

                        else if ( (LA62_0==';') ) {s = 26;}

                        else if ( (LA62_0=='+') ) {s = 27;}

                        else if ( (LA62_0=='-') ) {s = 28;}

                        else if ( (LA62_0=='*') ) {s = 29;}

                        else if ( (LA62_0=='/') ) {s = 30;}

                        else if ( (LA62_0=='|') ) {s = 31;}

                        else if ( (LA62_0=='&') ) {s = 32;}

                        else if ( (LA62_0=='<') ) {s = 33;}

                        else if ( (LA62_0=='>') ) {s = 34;}

                        else if ( (LA62_0=='=') ) {s = 35;}

                        else if ( (LA62_0=='%') ) {s = 36;}

                        else if ( (LA62_0=='`') ) {s = 37;}

                        else if ( (LA62_0=='{') ) {s = 38;}

                        else if ( (LA62_0=='}') ) {s = 39;}

                        else if ( (LA62_0=='^') ) {s = 40;}

                        else if ( (LA62_0=='~') ) {s = 41;}

                        else if ( (LA62_0=='!') ) {s = 42;}

                        else if ( (LA62_0=='.') ) {s = 43;}

                        else if ( (LA62_0=='@') ) {s = 44;}

                        else if ( (LA62_0=='o') ) {s = 45;}

                        else if ( (LA62_0=='0') ) {s = 46;}

                        else if ( ((LA62_0>='1' && LA62_0<='9')) ) {s = 47;}

                        else if ( (LA62_0=='u') ) {s = 48;}

                        else if ( (LA62_0=='R') ) {s = 49;}

                        else if ( (LA62_0=='U') ) {s = 50;}

                        else if ( (LA62_0=='B') ) {s = 51;}

                        else if ( (LA62_0=='A'||(LA62_0>='C' && LA62_0<='I')||(LA62_0>='K' && LA62_0<='M')||(LA62_0>='O' && LA62_0<='Q')||LA62_0=='T'||(LA62_0>='V' && LA62_0<='Z')||LA62_0=='_'||LA62_0=='h'||(LA62_0>='j' && LA62_0<='k')||LA62_0=='q'||LA62_0=='s'||LA62_0=='v'||LA62_0=='x'||LA62_0=='z') ) {s = 52;}

                        else if ( (LA62_0=='\"'||LA62_0=='\'') ) {s = 53;}

                        else if ( (LA62_0=='\\') ) {s = 54;}

                        else if ( (LA62_0=='\f') ) {s = 55;}

                        else if ( (LA62_0=='\n'||LA62_0=='\r') ) {s = 56;}

                        else if ( (LA62_0==' ') && (((startPos==0)||(startPos>0)))) {s = 57;}

                        else if ( (LA62_0=='\t') && (((startPos==0)||(startPos>0)))) {s = 58;}

                        else if ( (LA62_0=='#') ) {s = 59;}

                         
                        input.seek(index62_0);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA62_57 = input.LA(1);

                         
                        int index62_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA62_57==' ') && (((startPos==0)||(startPos>0)))) {s = 57;}

                        else if ( (LA62_57=='\f') && ((startPos>0))) {s = 141;}

                        else if ( (LA62_57=='\n'||LA62_57=='\r') && ((startPos==0))) {s = 143;}

                        else if ( (LA62_57=='\t') && (((startPos==0)||(startPos>0)))) {s = 58;}

                        else if ( (LA62_57=='#') && ((startPos==0))) {s = 144;}

                        else s = 142;

                         
                        input.seek(index62_57);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA62_58 = input.LA(1);

                         
                        int index62_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA62_58==' ') && (((startPos==0)||(startPos>0)))) {s = 57;}

                        else if ( (LA62_58=='\f') && ((startPos>0))) {s = 141;}

                        else if ( (LA62_58=='\n'||LA62_58=='\r') && ((startPos==0))) {s = 143;}

                        else if ( (LA62_58=='\t') && (((startPos==0)||(startPos>0)))) {s = 58;}

                        else if ( (LA62_58=='#') && ((startPos==0))) {s = 144;}

                        else s = 145;

                         
                        input.seek(index62_58);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA62_142 = input.LA(1);

                         
                        int index62_142 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((startPos>0)) ) {s = 141;}

                        else if ( (((startPos==0)||((startPos==0)&&(implicitLineJoiningLevel>0)))) ) {s = 143;}

                         
                        input.seek(index62_142);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA62_145 = input.LA(1);

                         
                        int index62_145 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((startPos>0)) ) {s = 141;}

                        else if ( (((startPos==0)||((startPos==0)&&(implicitLineJoiningLevel>0)))) ) {s = 143;}

                         
                        input.seek(index62_145);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 62, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}