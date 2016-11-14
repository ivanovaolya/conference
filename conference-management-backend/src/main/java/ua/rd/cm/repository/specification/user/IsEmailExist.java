package ua.rd.cm.repository.specification.user;

import ua.rd.cm.domain.User;
import ua.rd.cm.repository.specification.Specification;

public class IsEmailExist implements Specification<User>{

	private String email;
	
	public IsEmailExist(String email) {
		super();
		this.email = email;
	}

	@Override
	public boolean test(User usr) {
		return usr.getEmail().equals(email);
	}

	@Override
	public String toSqlClauses() {
		return String.format(" u.email = %s ", email);
	} 

	
}
