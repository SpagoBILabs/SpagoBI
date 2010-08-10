package it.eng.spagobi.tools.dataset.common.query;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractQueryTransformer implements IQueryTransformer {
	private IQueryTransformer previousTransformer;
	
	public AbstractQueryTransformer() {
		this(null);
	}
	
	public AbstractQueryTransformer(IQueryTransformer previousTransformer) {
		setPreviousTransformer( previousTransformer );
	}

	public IQueryTransformer getPreviousTransformer() {
		return previousTransformer;
	}

	public void setPreviousTransformer(IQueryTransformer previousTransformer) {
		this.previousTransformer = previousTransformer;
	}
	
	public boolean hasPreviousTransformer() {
		return  getPreviousTransformer() != null;
	}


	public Object transformQuery(Object query) {
		if( hasPreviousTransformer() ) {
			query = getPreviousTransformer().transformQuery(query);
		}
		return execTransformation(query);
	}
	
	public abstract Object execTransformation(Object query);
}
