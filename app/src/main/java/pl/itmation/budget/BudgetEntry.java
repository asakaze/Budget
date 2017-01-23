package pl.itmation.budget;


import java.util.Calendar;

class BudgetEntry {


    private String name;
    private String category;
    private BudgetCategory.Type type;
    private int value;
    private Calendar date;
    private String comment;
    private String owner;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public BudgetCategory.Type getType() {
        return type;
    }

    public void setType(BudgetCategory.Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public BudgetEntry(long id, String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner, String comment) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.value = value;
        this.date = date;
        this.comment = comment;
        this.owner = owner;
    }

    public BudgetEntry(long id, String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner) {
        this(id, name, category, type, value, date, owner, null);
    }

    public BudgetEntry(String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner) {
        this(0, name, category, type, value, date, owner, null);
    }

    public BudgetEntry(String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner, String comment) {
        this(0, name, category, type, value, date, owner, comment);
    }

}
