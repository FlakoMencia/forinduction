package sv.gob.induction.portal.commons.datatables;

import static java.util.Collections.unmodifiableList;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

import sv.gob.induction.portal.commons.datatables.mapping.Search;

/**
 * Filter which parses the input value to create one of the following predicates:
 * <ul>
 * <li>WHERE ... LIKE ..., see {@link GlobalFilter}</li>
 * <li>WHERE ... IN ... when the input contains multiple values separated by "+"</li>
 * <li>WHERE ... IS NULL when the input is equals to "NULL"</li>
 * <li>WHERE ... IN ... OR ... IS NULL</li>
 * </ul>
 */
class ColumnFilter extends GlobalFilter {
    private final List<String> values;
    private boolean addNullCase;

    ColumnFilter(Search search) {
        super(search);
        String filterValue = search.getValue();
        List<String> valuesColumnFilter = new ArrayList<>();
        for (String value : filterValue.split("\\+")) {
            if ("NULL".equals(value)) {
                addNullCase = true;
            } else {
                valuesColumnFilter.add(nullOrTrimmedValue(value));
            }
        }
        this.values = unmodifiableList(valuesColumnFilter);
    }

    @Override
    public Predicate createPredicate(From<?, ?> from, CriteriaBuilder criteriaBuilder, String attributeName)
            throws NoSuchFieldException {
        Expression<?> expression = from.get(attributeName);
        if (values.isEmpty()) {
            return addNullCase ? expression.isNull() : criteriaBuilder.conjunction();
        } else if (isBasicFilter()) {

            return getBasicFilterPredicate(from, criteriaBuilder, attributeName);
        }
        return getMultiFilterPredicate(from,criteriaBuilder,attributeName);

    }

    private Predicate getMultiFilterPredicate(From<?, ?> from,
                                              CriteriaBuilder criteriaBuilder,
                                              String attributeName) throws NoSuchFieldException {
        Expression<?> expression = from.get(attributeName);
        Field entityField = from.getJavaType().getDeclaredField(attributeName);
        Class<?> fieldClass = entityField.getType();
        Predicate predicate;
        if(fieldClass.equals(LocalDate.class) || fieldClass.equals(LocalDateTime.class)){
            if (values.size() == 2 && search.getRegex().equals(Constants.RGX_BETWEEN) && !addNullCase) {
                Expression<LocalDate> dateExp = truncExpression(criteriaBuilder, from, attributeName);
                Expression<LocalDate> minDateExp = toDateExpression(values.get(0), criteriaBuilder);
                Expression<LocalDate> maxDateExp = toDateExpression(values.get(1), criteriaBuilder);
                return criteriaBuilder.between(dateExp, minDateExp, maxDateExp);
            }else{
                expression = toCharExpression(expression, criteriaBuilder);
            }
        }
        predicate = expression.in(values);
        if (addNullCase) predicate = criteriaBuilder.or(predicate, expression.isNull());
        return predicate;
    }

    private Predicate getBasicFilterPredicate(From<?, ?> from,
                                              CriteriaBuilder criteriaBuilder,
                                              String attributeName) throws NoSuchFieldException {
        Expression<?> expression = from.get(attributeName);
        Field entityField = from.getJavaType().getDeclaredField(attributeName);
        Class<?> fieldClass = entityField.getType();
        String searchValue = values.get(0);
        if(fieldClass.equals(Boolean.class)){
            return criteriaBuilder.equal(expression, Boolean.valueOf(searchValue));
        }
        if(fieldClass.equals(Integer.class)){
            return criteriaBuilder.equal(expression, Integer.valueOf(searchValue));
        }
        if(fieldClass.equals((LocalDate.class)) || fieldClass.equals((LocalDateTime.class))){
            return getBasicDatePredicate(searchValue,criteriaBuilder,from,attributeName);
        }
        return super.createPredicate(from, criteriaBuilder, attributeName);
    }

    private Predicate getBasicDatePredicate(String searchValue, CriteriaBuilder criteriaBuilder,
                                                From<?, ?> from, String attributeName){
        Expression<LocalDate> expression = truncExpression(criteriaBuilder,from,attributeName);
        Expression<LocalDate> toDate = toDateExpression(searchValue, criteriaBuilder);
        if(search.getRegex().equals(Constants.RGX_GREATER_THAN_OR_EQUAL_TO)) {
            return criteriaBuilder.greaterThanOrEqualTo(expression,toDate);
        }
        if(search.getRegex().equals(Constants.RGX_LESS_THAN_OR_EQUAL_TO)) {
            return criteriaBuilder.lessThanOrEqualTo(expression,toDate);
        }
        return criteriaBuilder.equal(expression, toDate);
    }

    private Expression<?> toCharExpression(Expression<?> expression, CriteriaBuilder criteriaBuilder){
        return criteriaBuilder.function(Constants.FUNCTION_TO_CHAR, String.class,
                expression,
                criteriaBuilder.literal(sv.gob.induction.portal.commons.Constants.DATE_FORMAT));

    }

    private Expression<LocalDate> truncExpression(CriteriaBuilder criteriaBuilder,
                                             From<?, ?> from, String attributeName){
        return criteriaBuilder.function(Constants.FUNCTION_TRUNC, LocalDate.class,
                from.get(attributeName));
    }

    private Expression<LocalDate> toDateExpression(String searchValue, CriteriaBuilder criteriaBuilder){
        return criteriaBuilder.function(Constants.FUNCTION_TO_DATE, LocalDate.class,
                criteriaBuilder.literal(searchValue),
                criteriaBuilder.literal(sv.gob.induction.portal.commons.Constants.DATE_FORMAT));
    }

    private boolean isBasicFilter() {
        return values.size() == 1 && !addNullCase;
    }
}
