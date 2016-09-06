 package form;

import java.util.Arrays;

import form.Token.flags;

abstract interface addToken
{
    void add();
}

public abstract class Token extends Handler
{
    public enum flags {pending, handled, terminated};
   
    protected String value;
    private flags flag = flags.pending;
         
    private String getText() { return value; }
    public void setText(String value) { this.value = value; }
    public void setFlag(flags flag) { this.flag = flag; }
    public flags getFlag() { return flag; }
    
    public  void add() { /* FIXME error */ };
    public void handle(Token t) { };

    public class placeholder extends Token
    {
        private String key;
        private int lineno;
        private int srcpos;
        private int offset;
        
        placeholder(String key, int lineno, int srcpos)
        {
            this.key = key;
            this.lineno = lineno;
            this.srcpos = srcpos;
        }
        
        public String getKey() { return key; }
        public void setOffset(int offset) { this.offset = offset; }

        public void add() { super.add(); }
        
        @Override
        public void handle(Token token)
        {
            // TODO 
            System.out.println(Form.getName(this) + ".handle(" + token + ")");
        }

        @Override
        public String toString()
        {
            return Form.getName(this) + " [" + key + "]";
        }
    }
    
    public class text extends Token
    {        
        private int len ;

        text(String text)
        {
            this.value =text;
            this.len = text.length();
        }
        
        public void add()
        {
            System.out.println("add() text=" + value);
        }

        @Override
        public void handle(Token token)
        {
            System.out.println(getClass() + ".handle(" + token + ")");
        }

        @Override
        public String toString()
        {
            return "text [" + value + "]";
        }
        
    }
    
    public class field extends Token
    {
        private String name;
        private int pos;
        
        field(String value, int pos) 
        {  
            this.value = value;
            this.pos = pos;
        }

        public void setName(String name)
        {
            this.name = name;            
        }
        
        public String getName()
        {
            return this.name;            
        }

        public boolean firstPos()
        {
            return pos == 0;
        }

        @Override
        public String toString()
        {
            return "field [ name=" + name + ", value=" + value + ", pos=" + pos + "]";
        }

        public int getPos()
        {
            return pos;
        }
    }
    
    public class command extends Token
    {
        private String command;
        private String args;

        public String getCommand() { return command; }
        public String getArgs() { return args;  }
        
        command(String command, String args)
        {
            this.args =args;
            this.command = command;
        } 
        
        public void add()
        {
            // TODO
            System.out.println("command=" + command + "(" + args + ") flag=" + this.getFlag());
        }

        @Override
        public void handle(Token token)
        {
            //System.out.println(getClass() + ".handle(" + token + ")");
        }

        @Override
        public String toString()
        {
            return "command [command=" + command + ", args=" + args + ",flag=" + this.getFlag() + "]@" + this.hashCode();
        }
    }
    
    static public class array
    {
        private Token[] tokens = new Token[0]; 
        
        public array()
        {
            //add();
        }
        public Token[] getTokens() { return tokens; } 
        
        public void push(Token token)
        {
            Token[] tmp = new Token[tokens.length + 1];
            System.arraycopy(tokens, 0, tmp, 0, tokens.length);
            tokens = tmp;
            tokens[tokens.length - 1] = token;
        }
        
        @Override
        public String toString()
        {
            return Arrays.toString(tokens) + ", length=" + tokens.length;
        }
    }
}
