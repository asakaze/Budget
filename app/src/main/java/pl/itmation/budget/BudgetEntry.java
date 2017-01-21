package pl.itmation.budget;


import java.util.Date;

class BudgetEntry {


    private String name;
    private BudgetCategory category;
    private BudgetCategory.Type type;
    private Integer value;
    private Date date;
    private String comment;
    private String owner;

    public BudgetEntry(String name, BudgetCategory category, BudgetCategory.Type type,
                       Integer value, Date date, String owner, String comment) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.value = value;
        this.date = date;
        this.comment = comment;
        this.owner = owner;
    }

    public BudgetEntry(String name, BudgetCategory category, BudgetCategory.Type type,
                       Integer value, Date date, String owner) {
        this(name, category, type, value, date, owner, null);
    }

}
