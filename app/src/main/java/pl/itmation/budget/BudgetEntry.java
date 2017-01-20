package pl.itmation.budget;


import java.util.Date;

class BudgetEntry {

    public enum Type {
        INCOME, EXPENSE
    }

    private String name;
    private BudgetCategory category;
    private Type type;
    private Integer value;
    private Date date;
    private String comment;

    public BudgetEntry(String name, BudgetCategory category, Type type, Integer value, Date date,
                       String comment) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.value = value;
        this.date = date;
        this.comment = comment;
    }

    public BudgetEntry(String name, BudgetCategory category, Type type, Integer value, Date date) {
        this(name, category, type, value, date, null);
    }

}
