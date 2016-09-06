package form;

import java.util.Arrays;

/**
 * 
 * @author db
 *
 */
public class Form extends Handler
{
    // Attributes
    private String name;
    private Line.array lines = new Line.array();
    private StringBuilder output = new StringBuilder();
    private int pagesize;
    private int linesize;
    private DataRow row;
    
    private Token.array placeholders = new Token.array();
    public static int lineno = 0;

    // Ctor
    public Form()
    {
        this.name = "undefined";
    }

    public Form(String name, int pagesize, int linesize)
    {
        this.name = name;
        this.pagesize = pagesize;
        this.linesize = linesize;
    }

    final public static String getName(java.lang.Object obj)
    {
        return obj.getClass().getSimpleName();
    }

    public String getName()
    {
        return name;
    }

    public Line[] getLines()
    {
        return this.lines.getLines();
    }

    public void push(Line line)
    {
        lines.push(line);
    }

    public Line newLine(int lineno)
    {
        push(new Line(lineno));
        return lines.getLines()[lines.length - 1];
    }

    public Token[] getPlaceholders()
    {
        return placeholders.getTokens();
    }

    public void add(Token token)
    {
        //System.out.println("++add token=" + token);
        if (token instanceof Token.text)
            output.append(token.value);
        else if (token instanceof Token.placeholder)
            output.append(replace((Token.placeholder)token));
        else 
            throw new Error("Illegal token" + token);
    }
    
    private String replace(Token.placeholder placeholder)
    {
        //System.out.println("--placeholder=" + token);
        Token.field field = null;
        for (Token token: row.getFields())
        {
            if (token instanceof Token.field)
            {
                field = (Token.field)token; 
                //System.out.println("++replace field=" + field + "/" + placeholder);
                if (field.getName().equals(placeholder.getKey())) return field.value;
            }
        }
        return "<" + placeholder.getKey() + ">";
    }

    public void add(String text)
    {
        output.append(text);
    }

    public String getOutput()
    {
        return output.toString();        
    }
    
    public void setDatRow(DataRow row)
    {
        this.row = row;        
    }

    @Override
    public void handle(Token token)
    {
        // System.out.println(getClass().getName() + ".handle(" + token + ")@" + this.hashCode());
        if (token instanceof Token.command)
        {
            Token.command command = (Token.command) token;
//            switch (command.getCommand())
//            {
//            case "formname":
//                name = command.getArgs();
//                break;
//            case "pagesize":
//                pagesize = Integer.parseInt(command.getArgs());
//                break;
//            case "linesize":
//                linesize = Integer.parseInt(command.getArgs());
//                break;
//            case "skipline":
//                int linecount = Integer.parseInt(command.getArgs());
//                for (int num = 0; num < linecount; num++)
//                    this.push(new Line(lineno));
//                break;
//            case "break":
//                if (command.getArgs().equals("form"))
//                {
//                    // System.out.println(getClass().getName() + ".handle: hndlr=" + this.root+ ",flag=" + this.root.flag);
//                    token.setFlag(Token.flags.terminated);
//                    this.root.flag = Handler.flags.terminated;
//                    this.root.clear(); // FIXME is it right placed?
//                    return;
//                }
//            default:
//                super.handle(token);
//                return;
//            }
          if (command.getCommand().equals("formname"))
              name = command.getArgs();
          else if (command.getCommand().equals("pagesize"))
              pagesize = Integer.parseInt(command.getArgs());
          else if (command.getCommand().equals("linesize"))
              linesize = Integer.parseInt(command.getArgs());
          else if (command.getCommand().equals("skipline"))
          {
              int linecount = Integer.parseInt(command.getArgs());
              for (int num = 0; num < linecount; num++)
                  this.push(new Line(lineno));
          }
          else if (command.getCommand().equals("break"))
              if (command.getArgs().equals("form"))
              {
                  // System.out.println(getClass().getName() + ".handle: hndlr=" + this.root+ ",flag=" + this.root.flag);
                  token.setFlag(Token.flags.terminated);
                  this.root.flag = Handler.flags.terminated;
                  this.root.clear(); // FIXME is it right placed?
                  return;
              }
          else {
              super.handle(token);
              return;
          }
            token.setFlag(Token.flags.handled);
            // System.out.println(getClass() + ".handle(" + command + ")=handled");
        }
        else
        {
            super.handle(token);
        }
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[name=" + name + ", pages=" + Arrays.toString(lines.getLines()) + ",pagesize="
                + pagesize + ",linesize=" + linesize + "]";
    }

    public void resetOutput()
    {
        output.setLength(0);
    }



}
