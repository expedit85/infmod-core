package br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

public class MakeNamedNodeBuiltin extends BaseBuiltin
{
	private HashMap <String, Node> map = new HashMap<String, Node>(); 
	
	@Override
	public String getName() {
		return "makeNamedNode";
	}
	
	@Override
	public int getArgLength() {
		return 0;
	}
	
	
	
	/**
	 * Finds a node at any triple (either as subject, predicate or object).
	 * Returns on the first matching, i.e, if the node is found as a subject
	 * it will not be searched at any other triple.
	 * 
	 * @param context the rule current context
	 * @param node the node to be searched
	 * 
	 * @return the node on the graph (asserted or deducted)
	 */
	private Node find(RuleContext context, Node node)
	{
		ClosableIterator <Triple> it = context.find(node, Node.ANY, Node.ANY);
		if (it.hasNext())
		{
			return it.next().getSubject();
		}
		
		it = context.find(Node.ANY, node, Node.ANY);
		if (it.hasNext())
		{
			return it.next().getPredicate();
		}
		
		it = context.find(Node.ANY, Node.ANY, node);
		if (it.hasNext())
		{
			return it.next().getObject();
		}
		
		return null;	// Node not found
	}
	
	
	@Override
	public boolean bodyCall(Node[] args, int length, RuleContext context)
	{
		StringBuilder uri = new StringBuilder();
		
		Node n = getArg(1, args, context);

		if (n.isURI()) uri.append(n.getURI());
	
        for (int i = 2; i < length; i++) {
        	
            n = getArg(i, args, context);
            
            if (n.isLiteral()) {
                uri.append(n.getLiteralLexicalForm()); 
            } else {
                return false;
            }
        }


        String s = uri.toString();
        
        // TODO: nao sei se posso criar outro URI com o mesmo nome
        Node named = null;
        Node node = map.get(s);

        if (node != null)
        {
        	Node existing = find(context, node);
        	
	        if (node == existing)
	        {
	        	//System.out.println("MakeNamedNodeBuiltin: Reaproveitando node: " + s);
	        	named = node;
	        }
	        else if (existing == null)
	        {
	        	//System.out.println("MakeNamedNodeBuiltin: Reaproveitando node (criado por este built-in, mas para outro grafo/reasoning: " + s);
	        	named = node;
	        }
	        else	// existing != node ==> existing was not created by this built-in 
	        {
	        	//System.out.println("MakeNamedNodeBuiltin: Reaproveitando node criado (nao foi criado por este built-in): " + s);
	        	named = existing;
	        	map.put(s, named);	// esquece node antigo
	        }
        }
        else	// TODO checar se foi removido e excluir tambem de 'map'
        {
        	//System.out.println("MakeNamedNodeBuiltin: Criando node: " + s);
        	named = Node.createURI(s);
        	map.put(s, named);	// guarda novo node
        }
        
		return context.getEnv().bind(args[0], named);
	}
}
