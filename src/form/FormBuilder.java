package form;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormBuilder
{
    public static void main(String[] args)
    {
        FormBuilder builder = new FormBuilder();

        builder.loadTemplate("myform.tpl");
        
        builder.loadDataSet("mydata.csv");
        
        System.out.println("-- loaded form=" + builder.form);

        builder.generate();

        System.exit(0);
    } 

    public Form getForm()
    {
        return form;
    }

    private Form form;
    private Handler tokenhHndlr;
    //private static int lineno = 0;
    private static ArrayList<String> lines = null;
    private DataSet dataset = new DataSet();
    private String filename;

    public FormBuilder()
    {
        super();
        this.form = new Form();
        this.tokenhHndlr = this.form;
    }
    
    public void getOutput()
    {
        generate();
    }

    private void generate()
    {
        DataRow row = null;
        while((row = dataset.fetchDataRow()) != null)
        {
            form.resetOutput();
            form.setDatRow(row);
            for (Line line: form.getLines())
            {
                for (Token token: line.getTokens())
                {
                    //System.out.println("++token=" + token);
                    form.add(token);
                }
                form.add("\n");
            }
            System.out.println("\ngenerate output for row=" + row);
            System.out.println("---------------------------------------------------------------");
            System.out.print(form.getOutput());
            System.out.println("---------------------------------------------------------------");
        }
    }

    private static Matcher getMatcher(String re, String in)
    {
        final Pattern pattern = Pattern.compile(re);
        final Matcher matcher = pattern.matcher(in);
        return matcher;
    }

    private static ArrayList<String> readFile(String filename)
    {
        String file = filename;
        if (! filename.contains("/")) file = "lib/" + file;
        BufferedReader br = null;
        String line;
        lines = new ArrayList<String>();
        boolean commented = false;

        try
        {
            br = new BufferedReader(new FileReader(file));

            readline: while ((line = br.readLine()) != null)
            {
                final Matcher matcher = getMatcher("(/\\*)|((\\*/))", line);
                if (matcher.find())
                {
                    if (!commented && matcher.group(1) != null)
                    {
                        commented = true;
                    } else if (matcher.group(2) != null)
                    {
                        commented = false;
                        continue readline;
                    }
                }
                if (commented)
                    continue readline;
                lines.add(line);
            }
            System.out.println(Form.getName(br) + " read lines=" + lines.size() + " from " + file);

        } catch (Exception exc)
        {
            System.err.println("Error while trying to read file " + file + "(" + exc + ")");
        } finally
        {
            try
            {
                br.close();
            } catch (IOException exc)
            {
            }
        }
        return lines;
    }


    private void parse(String line)
    {
        int slideIndex = 0;
        String token;
        
        final Matcher matcher = getMatcher("<(.*?)/>", line);
        tokenloop: while (matcher.find())
        {
            token = matcher.group(1);
            int startAt = matcher.start(), endAt = matcher.end();

            if (startAt > slideIndex)
            {
                String text = line.substring(slideIndex, startAt);
                //System.out.println("++text=" + text + "[" + slideIndex + "," + startAt + "]");
                // new Token() { }.new text(text).add();
                tokenhHndlr.handle(new Token() { }.new text(text));
            }

            String[] pair = token.split("=");
            if (pair.length == 2)
            {
                //System.out.println("++command=" + pair[0] + "(" + pair[1]+ ")["
                //    + matcher.start() + "," + matcher.end() + "]");
                Token command = new Token() { }.new command(pair[0], pair[1]);
                tokenhHndlr.handle(command);
                // System.out.println("++" + command + " flag=" +
                // command.getFlag() + "@" +command.hashCode());
                if (command.getFlag() == Token.flags.terminated)
                {
                    slideIndex = line.length();
                    break tokenloop;
                }
            } 
            else
            {
                //System.out.println("++placeholder=" + token + "[" +
                //    matcher.start() + "," + matcher.end() + "]");
                tokenhHndlr.handle(new Token(){}.new placeholder(token, Form.lineno, matcher.start()));
            }
            slideIndex = endAt;

        } // while matcher find

        if (slideIndex < line.length())
        {
            String text = line.substring(slideIndex);
            //System.out.println("text=" + text + "[" + slideIndex + "," + line.length() + "]");
            tokenhHndlr.handle(new Token(){}.new text(text));
        }
    }

    public void loadTemplate(String filename)
    {
        this.filename = filename;
        lines = readFile(filename);
        
        readline:
        for (String line: lines)
        {
            Form.lineno++;
            // FIXME: deferred 
            //tokenhHndlr.push(form.getPage().newLine(Form.lineno));
            Line newline = new Line(Form.lineno);
            tokenhHndlr.push(newline);
            //System.out.println("++tokenHndlr=" + tokenhHndlr);
            System.out.println("line[" + Form.lineno + "]='" + line + "'");
            parse(line);
            if (tokenhHndlr.flag != Handler.flags.terminated)
            {
                tokenhHndlr.pop();
                if (newline.length() > 0) { form.push(newline); }
            } 
            else
            {
                tokenhHndlr.clear();
                break readline;
            }
        }
        lines.clear();
        Form.lineno = 0;
    }
  
    public void loadDataSet(ResultSet rset) throws SQLException
    {
        tokenhHndlr.push(dataset);
        
        ResultSetMetaData meta = rset.getMetaData();
        int cols = meta.getColumnCount(), rows = 0;
        Form.lineno++;
        
        tokenhHndlr.push(new DataRow(Form.lineno, dataset));
        for (int i = 1; i <= cols; i++)
        {
            String label = meta.getColumnLabel(i);
            tokenhHndlr.handle(new Token(){}.new field(label, i-1)); 
        }
        tokenhHndlr.pop();
        
        while (rset.next())
        {
            rows++;
            Form.lineno ++;
            
            tokenhHndlr.push(new DataRow(Form.lineno, dataset));
            for (int i = 1; i <= cols; i++)
            {
                String value = rset.getString(i);
                tokenhHndlr.handle(new Token(){}.new field(value, i-1)); 
            }
            tokenhHndlr.pop();
        }
        tokenhHndlr.clear();
        Form.lineno = 0;
    }
  
    private void loadDataSet(String filename)
    {
        lines = readFile(filename);
        tokenhHndlr.push(dataset);
        
        readline: 
        for (String line: lines)
        {
            Form.lineno ++;
            tokenhHndlr.push(new DataRow(Form.lineno, dataset));
            System.out.println("line[" + Form.lineno + "]='" + line + "'");
            
            String[] splits = line.split(";");
            //for (String  token: splits) { tokenhHndlr.handle(new Token(){}.new field(token)); }
            for (int pos = 0; pos < splits.length; pos++) 
            { 
                tokenhHndlr.handle(new Token(){}.new field(splits[pos], pos)); 
            }
            tokenhHndlr.pop();
        }
        
        tokenhHndlr.clear();
        lines.clear();
        Form.lineno = 0;
    }
}