package pl.itmation.budget;



class BudgetCategory {

    public enum Type {
        NONE, INCOME, EXPENSE
    }

    private Type defaultType;
    private String name;
    private Integer defaultValue;
    private String comment;

    public static class BudgetCategoryBuilder {
        private Type defaultType = null;
        private String name = null;
        private Integer defaultValue = 0;
        private String comment = null;

        public BudgetCategoryBuilder(String n) {
            this.name = n;
        }

        public BudgetCategoryBuilder defaultType(Type t) {
            this.defaultType = t;
            return this;
        }

        public BudgetCategoryBuilder defaultValue(Integer d) {
            this.defaultValue = d;
            return this;
        }

        public BudgetCategoryBuilder comment(String c) {
            this.comment = c;
            return this;
        }

        public BudgetCategory build() {
            return new BudgetCategory(this);
        }
    }

    private BudgetCategory(BudgetCategoryBuilder b) {
        this.defaultType = b.defaultType;
        this.name = b.name;
        this.defaultValue = b.defaultValue;
        this.comment = b.comment;
    }

    @Override
    public String toString() {
        String defaultValueStr = null;
        if(defaultValue == 0)
        {
            defaultValueStr = "Brak";
        }
        else
        {
            defaultValueStr = defaultValue.toString();
        }

        String defaultTypeStr = null;
        if(defaultType == null)
        {
            defaultTypeStr = "Brak";
        }
        else if (defaultType == Type.EXPENSE)
        {
            defaultTypeStr = "Wydatek";
        }
        else
        {
            defaultTypeStr = "Przychód";
        }

        return "Źródło: " + name + "\n" + "Domyślna wartość: " + defaultValueStr + "\n" +
                "Domyślny typ: " + defaultTypeStr + "\n" + "Komentarz: " + comment;
    }
}
