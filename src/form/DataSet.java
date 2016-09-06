package form;

import java.util.ArrayList;
import java.util.Arrays;

import form.Token.field;

public class DataSet extends Handler
{
    private ArrayList<String> head = new ArrayList<String>();
    private ArrayList<DataRow> rows = new ArrayList<DataRow>();
    private int rowidx = 0;
    
    public DataRow fetchDataRow()
    {
        DataRow row = null; 
        // System.out.println("rows=" + rows.size());
        if (rowidx < rows.size()) {
            row = rows.get(rowidx ++);
        } else {
            rowidx = 0;
        }
        return row;
    }
     
    public void addRow(DataRow row)
    {
        rows.add(row);    
    }

    public String getHead(int pos)
    {
        return head.get(pos);
    } 
    
    @Override
    public void handle(Token token)
    {
        //System.out.println(Form.getName(this) + ".handele(" + token + ",lineno=" + Form.lineno + ")" );
        if (token instanceof Token.field)
        {
            Token.field field = (Token.field)token;
            if (Form.lineno == 1) 
            {
                head.add(field.value);
                field.setFlag(Token.flags.handled);
            }
        }
        if (token.getFlag() == Token.flags.pending) super.handle(token);
    }

}

class DataRow extends Handler
{
    private DataSet dataset;
    private Token.array fields = new Token.array();
    private int lineno;
    
    public DataRow(int lineno, DataSet dataset)
    {
        this.lineno = lineno;
        this.dataset = dataset;
    }
    @Override
    public void handle(Token token)
    {
        if (token instanceof Token.field)
        {
            Token.field field = (Token.field)token;
            //System.out.println(field);
            if (field.firstPos()) dataset.addRow(this);
            fields.push(field);
            field.setName(dataset.getHead(field.getPos()));
            field.setFlag(Token.flags.handled);
        }
        if (token.getFlag() == Token.flags.pending) super.handle(token);
    }

    public Token[] getFields()
    {
         return fields.getTokens();
    }
    
    @Override
    public String toString()
    {
        return "DataRow [fields=" + fields + ", lineno=" + lineno + "]";
    }
    
}

