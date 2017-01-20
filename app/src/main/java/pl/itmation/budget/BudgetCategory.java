package pl.itmation.budget;



class BudgetCategory {

    public enum DefaultType {
        NONE, INCOME, EXPENSE
    }

    private DefaultType defaultType;
    private String name;
    private Integer defaultValue;
    private String comment;

    public static class BudgetCategoryBuilder {
        private DefaultType defaultType = DefaultType.NONE;
        private String name = null;
        private Integer defaultValue = 0;
        private String comment = null;

        public BudgetCategoryBuilder(String n) {
            this.name = n;
        }

        public BudgetCategoryBuilder defaultType(DefaultType t) {
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

}
