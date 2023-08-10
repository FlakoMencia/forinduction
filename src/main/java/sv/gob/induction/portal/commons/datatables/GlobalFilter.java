package sv.gob.induction.portal.commons.datatables;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

import sv.gob.induction.portal.commons.datatables.mapping.Search;

/**
 * Filter which creates a basic "WHERE ... LIKE ..." clause
 */
class GlobalFilter implements Filter {
    private final String escapedRawValue;
    protected final Search search;

    GlobalFilter(Search search) {
        this.search = search;
        escapedRawValue = escapeValue(search.getValue());
    }

    String nullOrTrimmedValue(String value) {
        return "\\NULL".equals(value) ? "NULL" : value.trim();
    }

    private String escapeValue(String filterValue) {
        return "%" + nullOrTrimmedValue(filterValue).toLowerCase()
                .replaceAll("~", "~~")
                .replaceAll("%", "~%")
                .replaceAll("_", "~_") + "%";
    }

    @Override
    public Predicate createPredicate(From<?, ?> from, CriteriaBuilder criteriaBuilder, String attributeName) throws NoSuchFieldException {
        Expression<?> expression = from.get(attributeName);
        return criteriaBuilder.like(criteriaBuilder.lower(expression.as(String.class)), escapedRawValue, '~');
    }
}
