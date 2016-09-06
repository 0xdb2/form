package form;

abstract interface HandlerIf
{
    public void handle(Token token);
}

public abstract class Handler implements HandlerIf 
{
    public enum flags {undefined, ready, terminated};
    private String name;
    private Handler next;
    protected Handler root;
    protected flags flag = flags.undefined;

    protected final Handler push(Handler next)
    {
        if (this.root == null)
        {
            root = this;
            flag = flags.ready;
        } 
        //System.out.println(getClass() + ".push(next=" + next + ")");
        if (this.next == null) this.next = next;
        else this.next.push(next, root);
        return this;
    }
    private final Handler push(Handler next, Handler root)
    {
        //System.out.println(getClass() + ".push(next=" + next + ",root=" + root + ")");
        this.root = root;
        if (this.next == null) this.next = next;
        else this.next.push(next, root);
        return this;
    }
    protected final boolean pop() // true, when next node has popped 
    {
        //System.out.println(getClass() + ".pop this[next=" + next + ",root=" + root + "]");
        if (next == null) // last node in the chain 
        {
            root = null;
            return true;
        }    
        else // not last
        {
            if (next.pop()) next = null; // the next was last node 
            return false;
        }
    }

    /**
     * @return next but one returned, if the next node was cutted out 
     */
    private final Handler prune(Handler hndlr) 
    {
        //System.out.println(getClass() + ".prune(" + hndlr +") this[next=" + next + ",root=" + root + "]");
        Handler ret = next;
        if (this == hndlr)
        {
            this.root = null;
            this.next = null;
            return ret;
        }    
        else 
        {
            ret = this.next.prune(hndlr);
            if (ret != null) 
            {
                this.next = ret;
            }
            return null;
        }
    }
    protected final void clear()
    {
        //System.out.println(getClass().getName() + ".clear this[next=" + next + ",root=" + root + "]");
        this.root = null;
        if (this.next != null) this.next.clear();
        this.next = null;
    }
    
//    protected final void wrap(Handler root)
//    {
//        if (next == null) next = root;
//        else next.wrap(root);
//    }
//    
    protected final void setNext(Handler next)
    {
        this.next = next;
    }
    
    //@Override
    public void handle(Token token)
    {
        if (next != null) next.handle(token);
        else System.out.println(getClass().getName() + ": unhandled token=" + token);
        //next.handle(token);
    }

    public void onPrint()
    {
        System.out.println("Handler [name=" + name + ", next=" + next + ", root=" + root + ", flag=" + flag + "]");
        if (next != null) next.onPrint();
    }
    
}
