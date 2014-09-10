package it.eng.spagobi.engines.whatif.calculatedmember;

public class Author {
	Integer id;
	String name;

	public Author(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setDescription(String name) {
		this.name = name;
	}
}
