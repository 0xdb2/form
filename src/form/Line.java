package form;

public class Line extends Handler
{
    private Token.array tokens = new Token.array();
    private int lineno;
    private Token.text blanks = null;   
     
    public Line() {    }

    public Line(int lineno)
    {
        this.lineno = lineno;
    }

    public int length() { return tokens.getTokens().length; }
    
    @Override
    public void handle(Token token)
    {
        
        if (token instanceof Token.command)
        {
            Token.command mycommand = (Token.command)token;
            
//            switch (mycommand.getCommand())
//            {
//                case "skipline": 
//                    // = mycommand.getArgs();
//                    System.out.println(getClass().getName() + ".handle(" + mycommand + ") TODO");
//                    break;
//                case "dummy": 
//                    break;
//                default:            
//                    super.handle(token); 
//                    return;
//            }
          if (mycommand.getCommand() == "skipline")
          {
              // = mycommand.getArgs();
              System.out.println(getClass().getName() + ".handle(" + mycommand + ") TODO");
          }
          else if (mycommand.getCommand() == "dummy") { }
          else {            
              super.handle(token); 
              return;
          }
        }
        else if ((token instanceof Token.placeholder)) // in Form now 
        {
            Token.placeholder placeholder = (Token.placeholder)token;
            //System.out.println(getClass().getName() + ".handle(" + placeholder + ") placeholder");
            if (blanks != null)
            {
                tokens.push(blanks);
                blanks = null;
            }
            tokens.push(placeholder);
        }
        else if ((token instanceof Token.text)) 
        {
            Token.text text = (Token.text)token;
            if (text.value.matches("^\\s+$")) 
            {
                blanks = text;
            }
            else
            {
                tokens.push(text);
            }
            //output += text.value;
        }
        else       
        {
            super.handle(token); 
            return;
        }
        token.setFlag(Token.flags.handled);
        //System.out.println(getClass() + ".handle(" + token + ")=handeled"); 
    }

    @Override
    public String toString() {
         return Form.getName(this) + " lineno=" + lineno + ", tokens " + tokens + " @" + this.hashCode();
    }
    
    public Token[] getTokens()
    {
        return tokens.getTokens();
    }
    
    static public class array
    {
        private Line[] lines = new Line[0]; 
        public int length = lines.length;
        
        public array()
        {
            //add();
        }
        
        public Line[] getLines()
        {
            return lines;
        }
        
        public void push(Line line) 
        {
            // FIXME is the clone() better??? 
            Line[] tmp = new Line[lines.length + 1];
            System.arraycopy(lines, 0, tmp, 0, this.length);
            lines = tmp;
            lines[lines.length-1] = line;
            length = lines.length;
        }
    }

}
