package dev.emi.emi.search;

import java.util.List;

import dev.emi.emi.api.stack.EmiStack;

public class LogicalOrQuery extends Query {
	private final List<Query> queries;

	public LogicalOrQuery(List<Query> queries) {
		this.queries = queries;
	}

	@Override
	public boolean matches(EmiStack stack) {
		for (int i = 0; i < queries.size(); i++) {
			if (queries.get(i).matches(stack)) {
				return true;
			}
		}
		return false;
	}
}
