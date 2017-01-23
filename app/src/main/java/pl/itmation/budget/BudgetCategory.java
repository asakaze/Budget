package pl.itmation.budget;


import android.os.Parcel;
import android.os.Parcelable;

class BudgetCategory implements Parcelable {

    public enum Type {
        INCOME, EXPENSE
    }

    private Type defaultType;
    private String name;
    private int defaultValue;
    private String comment;

    public void setDefaultType(Type defaultType) {
        this.defaultType = defaultType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Type getDefaultType() {
        return defaultType;
    }

    public String getName() {
        return name;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public static class BudgetCategoryBuilder {
        private Type defaultType = null;
        private String name = null;
        private int defaultValue = 0;
        private String comment = null;

        public BudgetCategoryBuilder(String n) {
            this.name = n;
        }

        public BudgetCategoryBuilder defaultType(Type t) {
            this.defaultType = t;
            return this;
        }
        public BudgetCategoryBuilder defaultType(String s) {
            if(s.equals(Type.EXPENSE.name()))
            {
                this.defaultType = Type.EXPENSE;
            }
            else if(s.equals(Type.INCOME.name()))
            {
                this.defaultType = Type.INCOME;
            }
            else
            {
                this.defaultType = null;
            }
            return this;
        }

        public BudgetCategoryBuilder defaultValue(int d) {
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

    public BudgetCategory(Parcel parcel){
        name = parcel.readString();
        String conv = parcel.readString();
        if(conv != null && conv != "")
        {
            defaultType = Type.valueOf(conv);
        }
        else
        {
            defaultType = null;
        }
        defaultValue = parcel.readInt();
        comment = parcel.readString();
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
            defaultValueStr = Integer.toString(defaultValue);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        if(this.defaultType != null)
        {
            parcel.writeString(this.defaultType.name());
        }
        else
        {
            parcel.writeString(null);
        }
        parcel.writeInt(defaultValue);
        parcel.writeString(comment);
    }

    public static final Parcelable.Creator<BudgetCategory> CREATOR =
            new Parcelable.Creator<BudgetCategory>() {
        public BudgetCategory createFromParcel(Parcel parcel) {
            return new BudgetCategory(parcel);
        }
        public BudgetCategory[] newArray(int size) {
            return new BudgetCategory[size];
        }
    };
}

