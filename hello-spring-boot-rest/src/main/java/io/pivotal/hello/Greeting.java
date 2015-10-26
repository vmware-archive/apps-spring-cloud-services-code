package io.pivotal.hello;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Greeting {

	@Id
	private Integer id;
	
	private String text;
	
	
	
	public Greeting(Integer id, String text) {
		super();
		this.id = id;
		this.text = text;
	}

	
	
	@Override
	public String toString() {
		return "Greeting [id=" + id + ", text=" + text + "]";
	}



	public Integer getId() {
		return id;
	}



	public String getText() {
		return text;
	}



	public Greeting() {

	}


}
